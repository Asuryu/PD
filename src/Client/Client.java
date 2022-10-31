package Client;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        MulticastSocket ms = new MulticastSocket(4004);
        InetAddress ipGroup = InetAddress.getByName("239.39.39.39");
        SocketAddress sa = new InetSocketAddress(ipGroup, 4004);
        NetworkInterface ni = NetworkInterface.getByName("en0");
        ms.joinGroup(sa, ni);

        System.out.println("[ * ] Welcome to the chat!");
        Scanner sc = new Scanner(System.in);
        System.out.println("[ > ] Input your username: ");
        String username = sc.nextLine();

        boolean keepGoing = true;
        while(keepGoing){
            String msg = sc.nextLine();
            if(msg.equalsIgnoreCase("EXIT")) keepGoing = false;
            msg = "[ " + username + " ]" + msg;
            DatagramPacket dp = new DatagramPacket(msg.getBytes(), msg.getBytes().length, ipGroup, 4004);
            ms.send(dp);
        }

        ms.leaveGroup(sa, ni);
        ms.close();
    }
}
