package Client;


import Client.Thread.SendsDataToServerThread;
import Server.Heartbeat;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    public DatagramSocket datagramSocket; // Socket to receive UDP packets

    public final ArrayList<Thread> threads = new ArrayList<>(); // List of threads
    public ArrayList<Heartbeat> servers = new ArrayList<>();
    public int port;
    public String ip;
    Socket socket;

    public final Boolean isLogged;


    public Client(String ip, int port) throws Exception {
        this.port = port;
        this.ip = ip;
        isLogged = false;

        while (true) {
            try {
                datagramSocket = new DatagramSocket();
                datagramSocket.connect(InetAddress.getByName(ip), port);

                String live = "I'M ALIVE";

                DatagramPacket datagramPacket = new DatagramPacket(live.getBytes(), live.length());
                datagramSocket.send(datagramPacket);
                System.out.println("Requesting server list to " + ip + ":" + port);

                // Recebe a lista de servidores disponíveis
                DatagramPacket dp = new DatagramPacket(new byte[4096], 4096);
                datagramSocket.receive(dp);
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(dp.getData(), 0, dp.getLength()));
                servers = (ArrayList<Heartbeat>) in.readObject();
                System.out.println(servers);
                if (servers.size() == 0) {
                    System.out.println("There are no servers available");
                   // break;
                }
                for (Heartbeat heartbeat : servers) {
                    socket = new Socket(InetAddress.getLocalHost(), heartbeat.getPort());
                }
            } catch (Error e) {
                //Caso ocorra erro a ligar ao servidor ele cancela
                System.out.println("An error has while receiving the server list");
                System.out.println("      " + e.getMessage());
            }
        SendsDataToServerThread sendsDataToServerThread = new SendsDataToServerThread(this,socket);
            sendsDataToServerThread.start();

        }

    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("[ ! ] Syntax: <lb address> <lb port>");
            return;
        }
        mostraASCII();
        try {
            new Client(args[0], Integer.parseInt(args[1]));
        } catch (Exception e) {
            System.out.println("[ ! ] An error has occurred while setting up the client");
            System.out.println("      " + e.getMessage());
            e.printStackTrace();
        }

    }

    private static void mostraASCII(){
        System.out.println("██████╗  ██████╗ ██╗          ██████╗ ██████╗");
        System.out.println("██╔══██╗██╔═══██╗██║          ██╔══██╗██╔══██╗");
        System.out.println("██████╔╝██║   ██║██║          ██████╔╝██║  ██║");
        System.out.println("██╔══██╗██║   ██║██║          ██╔═══╝ ██║  ██║");
        System.out.println("██████╔╝╚██████╔╝███████╗     ██║     ██████╔╝");
        System.out.println("╚═════╝  ╚═════╝ ╚══════╝     ╚═╝     ╚═════╝ \n");
    }
}
