package Server.Threads;

import Server.Comparators.HeartbeatComparatorLoad;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
                server.dbVersion = server.getDbVersion();
                // System.out.println("Database version: " + server.dbVersion);
                Heartbeat hb = new Heartbeat(server.TCP_IP, server.TCP_PORT, server.dbVersion, server.activeConnections.size(), server.isAvailable);
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

                    ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(bOut);
                    ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(dp.getData(), 0, dp.getLength()));

                    Heartbeat hb = (Heartbeat) in.readObject(); // Obtém o heartbeat do servidor
                    synchronized (server.onlineServers) {
                        if(!server.onlineServers.contains(hb)){
                            server.onlineServers.add(hb); // Adiciona o servidor à lista de servidores online
                            server.sendOnlineServers(); // Send list of online servers to the all clients
                        }
                        else {
                            int index = server.onlineServers.indexOf(hb);
                            server.onlineServers.set(index, hb);
                        }
                        Collections.sort(server.onlineServers); // Ordena a lista de servidores online por carga
                    }

                    boolean isUpToDate = true;
//                    for(Heartbeat h : server.onlineServers){
//                        if(h.getDbVersion() > server.dbVersion) isUpToDate = false;
//                    }
                    if (!isUpToDate) {
                        System.out.println("[ ! ] Database is not up to date, updating...");
                        // Server becomes unavailable
                        server.isAvailable = false;

                        // Server sends list of available servers to clients
                        for(Socket s : server.activeConnections){
                            try {
                                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                                oos.writeObject(server.onlineServers);
                                oos.flush();
                                System.out.println("sent list of available servers to client");
                            } catch (IOException e) {
                                System.out.println("[ ! ] An error has occurred while sending list of available servers to client");
                                System.out.println("      " + e.getMessage());
                            }
                        }

                        // Server closes all connections
                        synchronized (server.activeConnections) {
                            for(Socket s : server.activeConnections){
                                s.close();
                            }
                            server.activeConnections.clear();
                        }

                        // Server sends heartbeat to all servers
                        Heartbeat heartbeat = new Heartbeat(server.TCP_IP, server.TCP_PORT, server.dbVersion, 0, server.isAvailable);
                        out.writeObject(heartbeat);
                        out.flush();
                        DatagramPacket datagramPacket = new DatagramPacket(bOut.toByteArray(), bOut.size(), server.ipGroup, server.MULTICAST_PORT);
                        server.ms.send(datagramPacket);

                        // FIXME: pedir ao servidor a base de dados atualizada
//                        synchronized (server.onlineServers){
//                            // Get the server with the highest database version and the loweast load
//                            Heartbeat hb1 = server.onlineServers.stream()
//                                    .collect(Collectors.groupingBy(Heartbeat::getDbVersion, TreeMap::new, Collectors.toList()))
//                                    .lastEntry().getValue().stream()
//                                    .min(new HeartbeatComparatorLoad()).get();
//
//                            Socket s = new Socket("localhost", hb1.getPort());
//                            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
//                            File file = new File("database.db");
//                            FileOutputStream fos = new FileOutputStream(file);
//
//                            // Server sends request to server with the highest database version and lowest load
//                            oos.writeObject("GET_DATABASE");
//                            oos.flush();
//
//                            byte[] msgByte = new byte[4000];
//
//                            int bytesRead;
//                            while ((bytesRead = s.getInputStream().read(msgByte)) != -1) {
//                                fos.write(msgByte, 0, bytesRead);
//                                fos.flush();
//                                System.out.println(bytesRead);
//                            }
//                            fos.close();
//                            s.close();
//
//                            server.dbVersion = hb1.getDbVersion();
//                            server.isAvailable = true;
//                        }
                    }
                } catch(SocketTimeoutException e) {
                    // System.out.println("[ ! ] Timeout reached");
                } catch (Exception e) {
                    System.out.println("[ ! ] An error has occurred while receiving the heartbeat");
                    System.out.println("      " + e.getMessage());
                    e.printStackTrace();
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
