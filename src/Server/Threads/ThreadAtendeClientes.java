package Server.Threads;

import Server.Comparators.HeartbeatComparatorDBVersion;
import Server.Comparators.HeartbeatComparatorLoad;
import Server.Heartbeat;
import Server.Servidor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Classe que representa a thread que recebe as ligações dos clientes através de UDP
 */
public class ThreadAtendeClientes extends Thread {

    private final Servidor server;

    public ThreadAtendeClientes(Servidor server){
        this.server = server;
    }

    @Override
    public void run() {
        try{
            server.ds = new DatagramSocket(server.UDP_PORT);
            System.out.println("[ * ] Starting server at " + InetAddress.getLocalHost().getHostAddress() + ":" + server.UDP_PORT);
        } catch(IOException e) {
            System.out.println("[ ! ] An error has occurred while starting the server");
            System.out.println("      " + e.getMessage());
            System.exit(1);
        }

        try{
            while(!isInterrupted()){
                DatagramPacket dp = new DatagramPacket(new byte[4096], 4096);
                server.ds.receive(dp);
                //System.out.println("[ * ] Received packet from " + dp.getAddress().getHostAddress() + ":" + dp.getPort());

                // Cria array availableServers apenas com os servidores disponíveis e ordenados por carga
                synchronized (server.onlineServers){
                    ArrayList<Heartbeat> availableServers = new ArrayList<>(server.onlineServers);
                    availableServers.removeIf(hb -> !hb.isAvailable());
                    availableServers.sort(new HeartbeatComparatorLoad()); // Ordena os servidores por carga

                    ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(bOut);
                    out.writeObject(availableServers);
                    out.flush();

                    DatagramPacket dpSend = new DatagramPacket(bOut.toByteArray(), bOut.size(), dp.getAddress(), dp.getPort());
                    server.ds.send(dpSend);
                    System.out.println("[ * ] Sent list of servers to client " + dp.getAddress().getHostAddress() + ":" + dp.getPort());
                }
            }
        } catch (IOException e) {
            System.out.println("[ ! ] An error has occurred while receiving a packet");
            System.out.println("      " + e.getMessage());
        }

        System.out.println("[ - ] Exiting thread ThreadAtendeClientes");
    }

}
