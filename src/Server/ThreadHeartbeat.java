package Server;

import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

public class ThreadHeartbeat extends Thread {

    protected final Server server;

    ReceiveHeartbeats rhb;
    RemoveDeadServers rds;

    public ThreadHeartbeat(Server server){
        this.server = server;
    }

    @Override
    public void run(){
        try {
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
            return;
        }

        try{
            while(!isInterrupted()) {
                Thread.sleep(10000);
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bOut);
                Heartbeat hb = new Heartbeat(server.TCP_PORT, 1, 0, true);
                out.writeObject(hb);
                out.flush();
                DatagramPacket dp = new DatagramPacket(bOut.toByteArray(), bOut.size(), server.ipGroup, server.MULTICAST_PORT);
                server.ms.send(dp);
                System.out.println("[ · ] Sending heartbeat to " + server.MULTICAST_IP + ":" + server.MULTICAST_PORT);
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

        @Override
        public void run(){
            while(!isInterrupted()){
                try {
                    DatagramPacket dp = new DatagramPacket(new byte[256], 256);
                    server.ms.receive(dp);
                    ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(dp.getData(), 0, dp.getLength()));
                    Heartbeat hb = (Heartbeat) in.readObject();
                    synchronized (server.onlineServers) {
                        if(!server.onlineServers.contains(hb)) server.onlineServers.add(hb);
                        Collections.sort(server.onlineServers);
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

        @Override
        public void run() {
            try{
                while(!isInterrupted()){
                    Thread.sleep(500);
                    Instant now = Instant.now();
                    synchronized (server.onlineServers){
                        server.onlineServers.removeIf(hb -> Duration.between(hb.getSentTimestamp(), now).toSeconds() > 35 || !hb.isAvailable());
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
