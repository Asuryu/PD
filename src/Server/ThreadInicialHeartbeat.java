package Server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;

public class ThreadInicialHeartbeat extends Thread {

    private final String MULTICAST_IP;
    private final int MULTICAST_PORT;

    private final ArrayList<Heartbeat> onlineServers;

    public ThreadInicialHeartbeat(String MULTICAST_IP, int MULTICAST_PORT, ArrayList<Heartbeat> onlineServers){
        this.MULTICAST_IP = MULTICAST_IP;
        this.MULTICAST_PORT = MULTICAST_PORT;
        this.onlineServers = onlineServers;
    }

    @Override
    public void run(){
        InetAddress ipGroup;
        MulticastSocket ms;
        try {
            ms = new MulticastSocket(MULTICAST_PORT);
            ms.setSoTimeout(30000);
            ipGroup = InetAddress.getByName(MULTICAST_IP);
            SocketAddress sa = new InetSocketAddress(ipGroup, MULTICAST_PORT);
            NetworkInterface ni = NetworkInterface.getByName("en0");
            ms.joinGroup(sa, ni);
        } catch (IOException e) {
            System.out.println("[ ! ] An error has occurred while setting up multicast");
            e.printStackTrace();
            return;
        }

        try{
            System.out.println("[ * ] Receiving heartbeats for the next 30 seconds");
            while(!isInterrupted()){
                DatagramPacket dp = new DatagramPacket(new byte[256], 256);
                ms.receive(dp);
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(dp.getData(), 0, dp.getLength()));
                Heartbeat hb = (Heartbeat) in.readObject();
                synchronized (onlineServers){
                    if(!onlineServers.contains(hb)) onlineServers.add(hb);
                    Collections.sort(onlineServers);
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
