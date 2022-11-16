package Server.Threads;

import Server.Heartbeat;
import Server.Servidor;

import java.io.*;
import java.net.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

/**
 * Classe que representa a thread que trata dos heartbeats
 * Esta thread em particular envia os heartbeats para o endereço multicast
 */
public class ThreadHeartbeat extends Thread {

    protected final Servidor server;
    ReceiveHeartbeats rhb;
    RemoveDeadServers rds;

    public ThreadHeartbeat(Servidor server){
        this.server = server;
    }

    @Override
    public void run(){

        try {
            server.ms.setSoTimeout(0);
            server.ms.joinGroup(server.sa, server.ni);
            System.out.println("[ * ] Joined multicast group " + server.MULTICAST_IP + ":" + server.MULTICAST_PORT);
            rhb = new ReceiveHeartbeats();
            rds = new RemoveDeadServers();
            rhb.setDaemon(true);
            rhb.setDaemon(true);
            rhb.start();
            rds.start();
        } catch (IOException e) {
            System.out.println("[ ! ] An error has occurred while setting up multicast");
            System.out.println("      " + e.getMessage());
            System.exit(1);
        }

        try{
            while(!isInterrupted()) {
                Thread.sleep(10000); // Enviar heartbeat de 10 em 10 segundos
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bOut);
                server.dbVersion = getDbVersion();
                // System.out.println("Database version: " + server.dbVersion);
                Heartbeat hb = new Heartbeat(server.TCP_IP, server.TCP_PORT, server.dbVersion, server.activeConnections.size(), true); //TODO: preencher com informação correta
                out.writeObject(hb);
                out.flush();
                DatagramPacket dp = new DatagramPacket(bOut.toByteArray(), bOut.size(), server.ipGroup, server.MULTICAST_PORT);
                server.ms.send(dp);
                //System.out.println("[ · ] Sending heartbeat to " + server.MULTICAST_IP + ":" + server.MULTICAST_PORT);
            }
        } catch (InterruptedException ie){
            System.out.println("[ - ] Exiting thread ThreadHeartbeat");
            rds.interrupt();
            rhb.interrupt();
        } catch (Exception e) {
            System.out.println("[ ! ] An error has occurred while sending heartbeat");
            System.out.println("      " + e.getMessage());
        }
    }

    private int getDbVersion(){
        try {
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT version FROM database WHERE id=1");
            rs.next();
            server.dbVersion = rs.getInt("version");
            rs.close();
            stmt.close();
            return server.dbVersion;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Classe que representa a thread que recebe os heartbeats
     * Esta thread em particular recebe os heartbeats
     * e adiciona os servidores à lista de servidores online
     * ordenada por carga
     */
    class ReceiveHeartbeats extends Thread {

        @Override
        public void run(){
            while(!isInterrupted()){
                try {
                    DatagramPacket dp = new DatagramPacket(new byte[256], 256);
                    server.ms.receive(dp);
                    ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(dp.getData(), 0, dp.getLength()));
                    Heartbeat hb = (Heartbeat) in.readObject(); // Obtém o heartbeat do servidor
                    synchronized (server.onlineServers) {
                        if(!server.onlineServers.contains(hb)) server.onlineServers.add(hb); // Adiciona o servidor à lista de servidores online
                        else {
                            int index = server.onlineServers.indexOf(hb);
                            server.onlineServers.set(index, hb);
                        }
                        Collections.sort(server.onlineServers); // Ordena a lista de servidores online por carga
                    }
                    // TODO: detetar se a versão da base de dados local é inferior à do Heartbeat
                } catch(SocketTimeoutException e) {
                    // System.out.println("[ ! ] Timeout reached");
                } catch (Exception e) {
                    System.out.println("[ ! ] An error has occurred while receiving the heartbeat");
                    System.out.println("      " + e.getMessage());
                }
            }
        }
    }

    /**
     * Classe que representa a thread que remove os heartbeats
     * Esta thread em particular remove os heartbeats
     * da lista de servidores online que já não estão online
     * ou que não enviam heartbeats há mais de 35 segundos
     */
    class RemoveDeadServers extends Thread {

        @Override
        public void run() {
            try{
                while(!isInterrupted()){
                    Thread.sleep(500); // Verificar se algum servidor já não está online a cada 500ms
                    Instant now = Instant.now();
                    synchronized (server.onlineServers){
                        // Remove os servidores que já não estão online ou que não enviam heartbeats há mais de 35 segundos
                        server.onlineServers.removeIf(hb -> Duration.between(hb.getSentTimestamp(), now).toSeconds() > 35 || !hb.isAvailable());
                    }
                }
            } catch (InterruptedException ignored){
            } catch (Exception e){
                System.out.println("[ ! ] An error occurred while removing a dead server from list");
                System.out.println("      " + e.getMessage());
            }
        }
    }
}
