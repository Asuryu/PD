package Client;

import java.net.*;

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