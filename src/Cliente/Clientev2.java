package Client;

import Client.Threads.ThreadEnvia;
import Server.Heartbeat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;

public class Clientev2 {

    private final String UDP_IP;
    private final int UDP_PORT;

    private final DatagramSocket ds; // Socket to receive UDP packets

    private ArrayList<Heartbeat> servers = new ArrayList<>();

    private Boolean isLoggedIn = false;
    private Boolean isAdmin = false;

    public Clientev2(String ip, int port) throws Exception {
        this.UDP_IP = ip;
        this.UDP_PORT = port;

        ds = new DatagramSocket();
        try{
            ds.connect(InetAddress.getByName(UDP_IP), UDP_PORT);
            ds.send(new DatagramPacket("I'M ALIVE".getBytes(), "I'M ALIVE".length()));
            getListOfServers();
        } catch (Exception e) {
            System.out.println("[ ! ] The server " + UDP_IP + ":" + UDP_PORT + " is not online");
            System.exit(1);
        }

        if(servers.size() == 0) {
            System.out.println("[ ! ] There are no servers available");
            return;
        }

        // try to connect to the first server in the list
        // if it fails, try the next one
        for (Heartbeat server : servers) {
            try {
                Socket socket = new Socket(server.getIp(), server.getPort());
                System.out.println("[ * ] Connected to " + server.getIp() + ":" + server.getPort());
                ThreadEnvia te = new ThreadEnvia(this, socket);
                te.start();
                te.join();
            }
            catch (IOException e) {
                System.out.println("[ ! ] Failed to connect to " + server.getIp() + ":" + server.getPort());
            }
        }

    }

    public static void main(String[] args) {
        try{
            new Clientev2(args[0], Integer.parseInt(args[1]));
        } catch (Exception e) {
            System.out.println("[ ! ] An error has occurred while setting up the client");
            System.out.println("      " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void getListOfServers() throws IOException, ClassNotFoundException {
        System.out.println("[ * ] Requesting server list to " + UDP_IP + ":" + UDP_PORT);
        // Recebe a lista de servidores disponíveis
        DatagramPacket dp = new DatagramPacket(new byte[4096], 4096);
        ds.receive(dp);
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(dp.getData(), 0, dp.getLength()));
        servers = (ArrayList<Heartbeat>) in.readObject();
        in.close();
        ds.close();
    }

    public synchronized void setLoggedIn(Boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public synchronized void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    private void mostraASCII(){
        System.out.println("██████╗  ██████╗ ██╗          ██████╗ ██████╗");
        System.out.println("██╔══██╗██╔═══██╗██║          ██╔══██╗██╔══██╗");
        System.out.println("██████╔╝██║   ██║██║          ██████╔╝██║  ██║");
        System.out.println("██╔══██╗██║   ██║██║          ██╔═══╝ ██║  ██║");
        System.out.println("██████╔╝╚██████╔╝███████╗     ██║     ██████╔╝");
        System.out.println("╚═════╝  ╚═════╝ ╚══════╝     ╚═╝     ╚═════╝ \n");
    }
}
