package Client;

import Client.Threads.ThreadAtendeServidor;
import Client.Threads.ThreadEnviaServidor;
import Server.Heartbeat;


import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Cliente {
    public DatagramSocket datagramSocket; // Socket to receive UDP packets
    public Boolean isLogged; // Flag to indicate if the client is logged or not
    public Boolean isReg;// Flag to indicate if the client is register or not
    public Boolean wasEdit;
    public Boolean progress;
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
        while (true) {
            try {
                datagramSocket = new DatagramSocket();
                datagramSocket.connect(InetAddress.getByName(ip), port);
                String live = "I'M ALIVE";
                DatagramPacket datagramPacket = new DatagramPacket(live.getBytes(), live.length());
                //send packet
                datagramSocket.send(datagramPacket);

                // Recebe a lista de servidores disponíveis
                DatagramPacket dp = new DatagramPacket(new byte[4096], 4096);
                datagramSocket.receive(dp);
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(dp.getData(), 0, dp.getLength()));
                servers = (ArrayList<Heartbeat>) in.readObject();
                System.out.println(Arrays.toString(servers.toArray()));


            } catch (Error e) {
                //Caso ocorra erro a ligar ao servidor ele cancela
                System.out.println("Erro ao conecetar a um servidor");
                break;
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
        System.out.println("A comecar o cliente....");
        if (args.length != 2) {
            System.err.println("[ERROR]Sintaxe: <lb address> <lb port>");
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
}

