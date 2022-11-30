package Client;

import Client.Threads.ThreadAtendeServidor;
import Client.Threads.ThreadEnviaServidor;
import Server.Heartbeat;


import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Cliente {
    public int port;
    public String ip;
    public DatagramSocket datagramSocket; // Socket to receive UDP packets
    public final ArrayList<Thread> threads = new ArrayList<>(); // List of threads
    public final Socket socket;

    public Cliente(String ip, int port) throws Exception {
        this.port = port;
        this.ip = ip;
        socket = new Socket();
        ArrayList<Heartbeat> servers = null;
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
              /* for (Heartbeat heartbeat : servers) {
                    try {
                        // Socket socket = new Socket("localhost",heartbeat.getPort());
                        Socket socket = new Socket("10.65.132.193", heartbeat.getPort());
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                       /* //  String log [] = {"REGISTER","tania","tania","123"};

                        objectOutputStream.writeObject("REGISTER tania tania 123");
                        objectOutputStream.flush();

                        Integer read = (Integer) objectInputStream.readObject();
                        System.out.println(read);
                    } catch (Exception e) {
                        System.out.println("[ ! ] An error has occurred while receiving a TCP connection");
                        // System.out.println("      " + e.printStackTrace();
                        e.printStackTrace();
                    }
                }*/

            } catch (IOException e) {
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


}

