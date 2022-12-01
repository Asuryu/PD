package Server.Threads;

import Server.Heartbeat;
import Server.Servidor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.nio.file.Files;
import java.util.Collections;

/**
 * Classe que representa a thread que recebe os heartbeats no começo do servidor
 * Após 30 segundos esta thread termina
 * É responsável por reconhecer os servidores na rede
 */
public class ThreadInicialHeartbeat extends Thread {

    private final Servidor server;

    public ThreadInicialHeartbeat(Servidor server){
        this.server = server;
    }

    @Override
    public void run(){
        try {
            server.ms = new MulticastSocket(server.MULTICAST_PORT);
            server.ms.setSoTimeout(2000);
            server.ipGroup = InetAddress.getByName(server.MULTICAST_IP);
            server.sa = new InetSocketAddress(server.ipGroup, server.MULTICAST_PORT);
            server.ni = NetworkInterface.getByName("en0");
            server.ms.joinGroup(server.sa, server.ni);
        } catch (IOException e) {
            System.out.println("[ ! ] An error has occurred while setting up multicast");
            System.out.println("      " + e.getMessage());
            System.exit(1);
        }

        try{
            System.out.println("[ * ] Receiving heartbeats for the next 30 seconds");
            while(!isInterrupted()){
                DatagramPacket dp = new DatagramPacket(new byte[256], 256);
                server.ms.receive(dp);
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(dp.getData(), 0, dp.getLength()));
                Heartbeat hb = (Heartbeat) in.readObject(); // Recebe o heartbeat
                synchronized (server.onlineServers){
                    if(!server.onlineServers.contains(hb)) server.onlineServers.add(hb); // Adiciona o servidor à lista de servidores online
                    Collections.sort(server.onlineServers); // Ordena os servidores por carga
                }
            }
        } catch(SocketTimeoutException e) {
            if(server.onlineServers.isEmpty()){ // Se não houver servidores online
                try { // Tenta criar uma nova base de dados
                    File original = new File(server.DATABASE_ORIGINAL);
                    File copy = new File(server.DATABASES_PATH + server.DATABASE_NAME);
                    if(!copy.exists()){
                        System.out.println("[ ! ] No servers are online, creating a new database");
                        Files.copy(original.toPath(), copy.toPath());
                    }
                } catch (IOException ex) {
                    System.out.println("[ ! ] An error has occurred while creating the database");
                    System.out.println("      " + ex.getMessage());
                }
            }
        } catch(Exception ie){
            System.out.println("[ ! ] An error has occurred while receiving initial heartbeats");
            System.out.println("      " + ie.getMessage());
        }
    }
}
