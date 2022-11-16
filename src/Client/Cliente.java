package Client;

import Server.Heartbeat;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Representa um cliente do sistema
 * O cliente é responsável por enviar um pedido de listagem de servidores disponíveis
 */
public class Cliente {
    public static void main(String[] args) throws Exception {
        if(args.length!=2){
            System.out.println("ERRO");
        }
        String port = args[0];
        int ip = Integer.parseInt(args[1]);

        DatagramSocket datagramSocket = new DatagramSocket();
        datagramSocket.connect(InetAddress.getByName(port), ip);

        String live = "I m live";
        DatagramPacket datagramPacket = new DatagramPacket(live.getBytes(), live.length());
        datagramSocket.send(datagramPacket);

    }
}