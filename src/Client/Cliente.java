package Client;

import Server.Heartbeat;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;

/**
 * Representa um cliente do sistema
 * O cliente é responsável por enviar um pedido de listagem de servidores disponíveis
 */
public class Cliente {
    public static void main(String[] args) throws Exception {
        DatagramSocket ds = new DatagramSocket();
        String ipServer = "localhost";
        int port = 5004;
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
        // TODO: tentar ligar por TCP ao primeiro servidor da lista (se falhar liga ao segundo, etc...)
    }
}