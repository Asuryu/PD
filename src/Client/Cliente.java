package Client;

import Server.Heartbeat;
import Server.TCPMessages;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Representa um cliente do sistema
 * O cliente é responsável por enviar um pedido de listagem de servidores disponíveis
 */
public class Cliente {
    public static void main(String[] args) throws Exception {
        DatagramSocket ds = new DatagramSocket();
        String ipServer = "localhost";
        int port = 3003;
        ds.connect(InetAddress.getByName(ipServer), port);
        DatagramPacket dp = new DatagramPacket(new byte[4096], 4096);
        ds.send(dp);

        DatagramPacket dpReceive = new DatagramPacket(new byte[4096], 4096);
        ds.receive(dpReceive);
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(dpReceive.getData(), 0, dpReceive.getLength()));
        ArrayList<Heartbeat> availableServers = (ArrayList<Heartbeat>) in.readObject();
        for (Heartbeat heartbeat : availableServers) {
            System.out.println(heartbeat);
        }

        Socket s = new Socket("localhost", availableServers.get(0).getPort());
        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream ind = new ObjectInputStream(s.getInputStream());
        out.writeObject(TCPMessages.LOGIN); // Envia a mensagem ao servidor a pedir a base de dados
        out.flush();

        String mensagem = (String)ind.readObject();
        System.out.println(mensagem);
        HashMap<String, String> credentials = new HashMap<>();
        credentials.put("tomas@isec.pt", "123");
        out.writeObject(credentials);

        // TODO: tentar ligar por TCP ao primeiro servidor da lista (se falhar liga ao segundo, etc...)
    }
}