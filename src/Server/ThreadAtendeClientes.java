package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ThreadAtendeClientes extends Thread {

    private final Server server;

    public ThreadAtendeClientes(Server server){
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
            return;
        }

        try{
            while(!isInterrupted()){
                DatagramPacket dp = new DatagramPacket(new byte[256], 256);
                server.ds.receive(dp);
                System.out.println("[ * ] Received packet from " + dp.getAddress().getHostAddress() + ":" + dp.getPort());
            }
        } catch (IOException e) {
            System.out.println("[ ! ] An error has occurred while receiving a packet");
            System.out.println("      " + e.getMessage());
        }

        System.out.println("[ - ] Exiting thread ThreadAtendeClientes");
    }

}
