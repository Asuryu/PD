package Client;

import Server.Heartbeat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        System.out.println("A comecar o cliente....");
        if (args.length != 2) {
            System.err.println("[ERROR]Sintaxe: <lb address> <lb port>");
            return;
        }
     /*   try{
            new Client(args[0],Integer.parseInt(args[1]));
        }catch (Exception e) {
            System.out.println("[ ! ] An error has occurred while setting up the client");
            System.out.println("      " + e.getMessage());
            e.printStackTrace();
        }*/

        int port;
        String ip;

        ip = args[0];
        port = Integer.parseInt(args[1]);

        DatagramSocket datagramSocket = new DatagramSocket();
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bOut);

        datagramSocket.connect( InetAddress.getByName(ip),port);

        String live = "REGISTER";
        DatagramPacket datagramPacket = new DatagramPacket(live.getBytes(), live.length());
        datagramSocket.send(datagramPacket);

        // Recebe a lista de servidores disponíveis
        DatagramPacket dp = new DatagramPacket(new byte[4096], 4096);
        datagramSocket.receive(dp);
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(dp.getData(), 0, dp.getLength()));
        ArrayList<Heartbeat> servers = (ArrayList<Heartbeat>) in.readObject();
        System.out.println(Arrays.toString(servers.toArray()));

        for(Heartbeat heartbeat : servers){
            try{
                // Socket socket = new Socket("localhost",heartbeat.getPort());
                Socket socket = new Socket("10.65.132.193",heartbeat.getPort());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                String log [] = {"REGISTER","tania","tania","123"};

                objectOutputStream.writeObject("REGISTER tania tania 123");
                objectOutputStream.flush();

                String read = (String)objectInputStream.readObject();
                System.out.println("read = "+ read);
            }catch (Exception e){
                System.out.println("[ ! ] An error has occurred while receiving a TCP connection");
                // System.out.println("      " + e.printStackTrace();
                e.printStackTrace();
            }
        }
    }
}