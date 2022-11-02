package Server;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;

public class ThreadHeartbeat extends Thread {

    private final String MULTICAST_IP;
    private final int SERVER_PORT;
    private final int LOCAL_PORT;

    public final ArrayList<Heartbeat> onlineServers;

    public ThreadHeartbeat(String MULTICAST_IP, int SERVER_PORT, int LOCAL_PORT, ArrayList<Heartbeat> onlineServers){
        this.MULTICAST_IP = MULTICAST_IP;
        this.SERVER_PORT = SERVER_PORT;
        this.LOCAL_PORT = LOCAL_PORT;
        this.onlineServers = onlineServers;
    }

    @Override
    public void run(){
        InetAddress ipGroup;
        MulticastSocket ms;
        try {
            ms = new MulticastSocket(SERVER_PORT);
            ipGroup = InetAddress.getByName(MULTICAST_IP);
            SocketAddress sa = new InetSocketAddress(ipGroup, SERVER_PORT);
            NetworkInterface ni = NetworkInterface.getByName("en0");
            ms.joinGroup(sa, ni);
            System.out.println("[ * ] Joined multicast group " + MULTICAST_IP + ":" + SERVER_PORT);

            ReceiveHeartbeats rhb = new ReceiveHeartbeats(ms, onlineServers);
            RemoveDeadServers rds = new RemoveDeadServers(onlineServers);
            rhb.start();
        } catch (IOException e) {
            System.out.println("[ ! ] An error has occurred while setting up multicast");
            e.printStackTrace();
            return;
        }

        try{
            while(true) {
                Thread.sleep(10000);
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bOut);
                Heartbeat hb = new Heartbeat(LOCAL_PORT, 0.0f, 0, true);
                out.writeObject(hb);
                out.flush();
                DatagramPacket dp = new DatagramPacket(bOut.toByteArray(), bOut.size(), ipGroup, SERVER_PORT);
                ms.send(dp);
                System.out.println("[ · ] Sending heartbeat to " + MULTICAST_IP + ":" + SERVER_PORT);
            }
        }  catch (Exception e) {
            System.out.println("[ ! ] An error has occurred while sending heartbeat");
            e.printStackTrace();
        }
    }

    static class ReceiveHeartbeats extends Thread {

        private final MulticastSocket ms;
        private final ArrayList<Heartbeat> onlineServers;

        public ReceiveHeartbeats(MulticastSocket ms, ArrayList<Heartbeat> onlineServers){
            this.ms = ms;
            this.onlineServers = onlineServers;
        }

        @Override
        public void run(){
            while(true){
                try{
                    DatagramPacket dp = new DatagramPacket(new byte[256], 256);
                    ms.receive(dp);
                    ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(dp.getData(), 0, dp.getLength()));
                    Heartbeat hb = (Heartbeat) in.readObject();
                    System.out.println(hb); // TODO: provisório (pode ser necessário lidar com feedback)
                    synchronized (onlineServers){
                        onlineServers.remove(hb);
                        if(hb.isAvailable()) onlineServers.add(hb);
                        Collections.sort(onlineServers);
                    }
                    System.out.println(onlineServers);
                } catch (Exception e) {
                    System.out.println("[ ! ] An error has occurred while receiving the heartbeat");
                    e.printStackTrace();
                }
            }
        }
    }

    static class RemoveDeadServers extends Thread {

        private final ArrayList<Heartbeat> onlineServers;

        public RemoveDeadServers(ArrayList<Heartbeat> onlineServers){
            this.onlineServers = onlineServers;
        }

        @Override
        public void run() {
            try{
                while(true){
                    Thread.sleep(35000);
                    synchronized (onlineServers){
                        onlineServers.remove();
                    }
                }
            } catch (Exception e){
                System.out.println("[ ! ] An error occurred while removing dead server from list");
                e.printStackTrace();
            }
        }
    }
}
