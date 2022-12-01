package Client;

import Client.Threads.ThreadAtendeServidor;
import Client.Threads.ThreadEnviaServidor;
import Server.Heartbeat;


import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;

public class Cliente {
    public DatagramSocket datagramSocket; // Socket to receive UDP packets
    public Boolean isLogged; // Flag to indicate if the client is logged or not
    public Boolean isReg;// Flag to indicate if the client is register or not
    public Boolean wasEdit;
    public Boolean progress;
    public Boolean isAdmin;
    public final ArrayList<Thread> threads = new ArrayList<>(); // List of threads
    public ArrayList<Heartbeat> servers = new ArrayList<>();
    public int port;
    public String ip;
    public final Socket socket;

    public Cliente(String ip, int port) throws Exception {
        this.port = port;
        this.ip = ip;
        socket = new Socket();
        isLogged = false;
        isReg = false;
        wasEdit = false;
        progress = false;
        isAdmin = false;
        while (true) {
            try {
                mostraASCII();
                datagramSocket = new DatagramSocket();
                datagramSocket.connect(InetAddress.getByName(ip), port);
                String live = "I'M ALIVE";
                DatagramPacket datagramPacket = new DatagramPacket(live.getBytes(), live.length());
                datagramSocket.send(datagramPacket);
                System.out.println("[ * ] Requesting server list to " + ip + ":" + port);

                // Recebe a lista de servidores disponíveis
                DatagramPacket dp = new DatagramPacket(new byte[4096], 4096);
                datagramSocket.receive(dp);
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(dp.getData(), 0, dp.getLength()));
                servers = (ArrayList<Heartbeat>) in.readObject();
                System.out.println(servers);
                if (servers.size() == 0) {
                    System.out.println("[ * ] There are no servers available");
                    break;
                }
            } catch (Error e) {
                //Caso ocorra erro a ligar ao servidor ele cancela
                System.out.println("[ ! ] An error has while receiving the server list");
                System.out.println("      " + e.getMessage());
            }

            ThreadEnviaServidor threadEnviaServidor = new ThreadEnviaServidor(this, servers);
            ThreadAtendeServidor threadAtendeServidor = new ThreadAtendeServidor(this);

            threads.add(threadAtendeServidor);
            threads.add(threadEnviaServidor);
            for (Thread t : threads) {
                t.start();
            }

            for (Thread t : threads) {
                t.join();
            }

        }

    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("[ ! ] Syntax: <lb address> <lb port>");
            return;
        }
        try {
            new Cliente(args[0], Integer.parseInt(args[1]));
        } catch (Exception e) {
            System.out.println("[ ! ] An error has occurred while setting up the client");
            System.out.println("      " + e.getMessage());
            e.printStackTrace();
        }

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

