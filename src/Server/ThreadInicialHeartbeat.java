package Server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.Collections;

public class ThreadInicialHeartbeat extends Thread {

    private final Server server;

    public ThreadInicialHeartbeat(Server server){
        this.server = server;
    }

    @Override
    public void run(){
        try {
            server.ms = new MulticastSocket(server.MULTICAST_PORT);
            server.ms.setSoTimeout(30000);
            server.ipGroup = InetAddress.getByName(server.MULTICAST_IP);
            server.sa = new InetSocketAddress(server.ipGroup, server.MULTICAST_PORT);
            server.ni = NetworkInterface.getByName("en0");
            server.ms.joinGroup(server.sa, server.ni);
        } catch (IOException e) {
            System.out.println("[ ! ] An error has occurred while setting up multicast");
            System.out.println("      " + e.getMessage());
            return;
        }

        try{
            System.out.println("[ * ] Receiving heartbeats for the next 30 seconds");
            while(!isInterrupted()){
                DatagramPacket dp = new DatagramPacket(new byte[256], 256);
                server.ms.receive(dp);
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(dp.getData(), 0, dp.getLength()));
                Heartbeat hb = (Heartbeat) in.readObject();
                synchronized (server.onlineServers){
                    if(!server.onlineServers.contains(hb)) server.onlineServers.add(hb);
                    Collections.sort(server.onlineServers);
                }
            }
        } catch(SocketTimeoutException e) {
            // System.out.println("[ ! ] Timeout reached");
        } catch(Exception ie){
            System.out.println("[ ! ] An error has occurred while receiving initial heartbeats");
            System.out.println("      " + ie.getMessage());
        }
    }
}
