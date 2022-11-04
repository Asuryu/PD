package Server;

import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;

public class ThreadHeartbeat extends Thread {

    private final String MULTICAST_IP;
    private final int SERVER_PORT;
    private final int LOCAL_PORT;

    ReceiveHeartbeats rhb;
    RemoveDeadServers rds;

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
            rhb = new ReceiveHeartbeats(ms, onlineServers);
            rds = new RemoveDeadServers(onlineServers);
            rhb.setDaemon(true);
            rhb.setDaemon(true);
            rhb.start();
            rds.start();
        } catch (IOException e) {
            System.out.println("[ ! ] An error has occurred while setting up multicast");
            System.out.println("      " + e.getMessage());
            return;
        }

        try{
            while(!isInterrupted()) {
                Thread.sleep(10000);
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bOut);
                Heartbeat hb = new Heartbeat(LOCAL_PORT, 0.0f, 0, true);
                out.writeObject(hb);
                out.flush();
                DatagramPacket dp = new DatagramPacket(bOut.toByteArray(), bOut.size(), ipGroup, SERVER_PORT);
                ms.send(dp);
                //System.out.println("[ · ] Sending heartbeat to " + MULTICAST_IP + ":" + SERVER_PORT);
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

    class ReceiveHeartbeats extends Thread {

        private final MulticastSocket ms;
        private final ArrayList<Heartbeat> onlineServers;

        public ReceiveHeartbeats(MulticastSocket ms, ArrayList<Heartbeat> onlineServers){
            this.ms = ms;
            this.onlineServers = onlineServers;
        }

        @Override
        public void run(){
            while(!isInterrupted()){
                try {
                    DatagramPacket dp = new DatagramPacket(new byte[256], 256);
                    ms.receive(dp);
                    ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(dp.getData(), 0, dp.getLength()));
                    Heartbeat hb = (Heartbeat) in.readObject();
                    synchronized (onlineServers) {
                        if(!onlineServers.contains(hb)) onlineServers.add(hb);
                        Collections.sort(onlineServers);
                    }
                } catch(SocketTimeoutException e) {
                    // System.out.println("[ ! ] Timeout reached");
                } catch (Exception e) {
                    System.out.println("[ ! ] An error has occurred while receiving the heartbeat");
                    System.out.println("      " + e.getMessage());
                }
            }
        }
    }

    class RemoveDeadServers extends Thread {

        private final ArrayList<Heartbeat> onlineServers;

        public RemoveDeadServers(ArrayList<Heartbeat> onlineServers){
            this.onlineServers = onlineServers;
        }

        @Override
        public void run() {
            try{
                while(!isInterrupted()){
                    Thread.sleep(500);
                    Instant now = Instant.now();
                    synchronized (onlineServers){
                        boolean removed = onlineServers.removeIf(hb -> Duration.between(hb.getSentTimestamp(), now).toSeconds() > 35 || !hb.isAvailable());
                        //if(removed) System.out.println("[ - ] Removed server from online servers' list");
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
