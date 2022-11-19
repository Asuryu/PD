package Client;



import Server.Heartbeat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

import java.util.ArrayList;
import java.util.Arrays;
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
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bOut);

        datagramSocket.connect(InetAddress.getByName(port), ip);

        String live = "I m live";
        DatagramPacket datagramPacket = new DatagramPacket(live.getBytes(), live.length());
        datagramSocket.send(datagramPacket);

        // Recebe a lista de servidores disponíveis
        DatagramPacket dp = new DatagramPacket(new byte[4096], 4096);
        datagramSocket.receive(dp);
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(dp.getData(), 0, dp.getLength()));
        ArrayList<Heartbeat> servers = (ArrayList<Heartbeat>)in.readObject();
        System.out.println(Arrays.toString(servers.toArray()));


        /*int flagCloseCmd=0;
       do{
           System.out.println("\n-->Command:");
            Scanner sc  = new Scanner(System.in);
            String cmd = sc.nextLine();
            switch(cmd.toUpperCase())
            {
                case "EXIT":{
                    flagCloseCmd=1;
                    break;
                }
                case "REGISTER":{
                    break;
                }
                case "LOGIN":{
                    break;
                }
                case "EDIT DATA":{
                    break;
                }
                case "CHECK RESERVATIONS TO PAY":{
                    break;
                }

                case "CHECK PAID RESERVATIONS":{
                    break;
                }

                case "SEARCH SPECTACLE":{
                    break;
                }

                case "SELECT SPECTACLE":{
                    break;
                }

                case "SEE AVAILABLE PLACES":{
                    break;
                }

                case "SUBMIT RESERVATIONS":{
                    break;
                }

                case "DELETE RESERVATIONS":{
                    break;
                }
                case "PAY":{
                    break;
                }
                case "LOGOUT":{
                    break;
                }
            }
        }while(flagCloseCmd==0);*/


    }
}