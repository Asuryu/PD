package Server;

import java.io.IOException;
import java.net.*;

public class ThreadDatagramasUDP extends Thread {

    private final String MULTICAST_IP;
    private final int SERVER_PORT;

    private MulticastSocket ms;
    private InetAddress ipGroup;
    private SocketAddress sa;
    private NetworkInterface ni;

    public ThreadDatagramasUDP(String MULTICAST_IP, int SERVER_PORT){
        this.MULTICAST_IP = MULTICAST_IP;
        this.SERVER_PORT = SERVER_PORT;
    }

    @Override
    public void run() {
        try {
            ms = new MulticastSocket(SERVER_PORT);
            ipGroup = InetAddress.getByName(MULTICAST_IP);
            sa = new InetSocketAddress(ipGroup, SERVER_PORT);
            ni = NetworkInterface.getByName("en0");
            ms.joinGroup(sa, ni);
            System.out.println("[ * ] Joined multicast group " + MULTICAST_IP + ":" + SERVER_PORT);
        } catch (IOException e) {
            System.out.println("[ ! ] An error has occurred while setting up multicast");
            e.printStackTrace();
        }

        try{
            while(true){
                DatagramPacket dp = new DatagramPacket(new byte[256], 256);
                ms.receive(dp);
                String msg = new String(dp.getData(), 0, dp.getLength());
                System.out.println(msg);
            }
        } catch (IOException ignored) {}
    }
}
