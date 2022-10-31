package Server;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    public static void main(String[] args) {
        ArrayList<Thread> threads = new ArrayList<>();
        threads.add(new ThreadAtendeClientes(Integer.parseInt(args[0]), args[1]));
        threads.add(new ThreadDatagramasUDP("239.39.39.39", 4004));
        threads.add(new ThreadLigacoesTCP());

        for(Thread thrd : threads) {
            thrd.start();
        }
    }

}