package Server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Server {

    public static void main(String[] args) {
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<Heartbeat> onlineServers = new ArrayList<>(); // TODO: mudar para um tipo de objeto diferente

        int SERVER_PORT;
        try{
            ServerSocket s = new ServerSocket(0);
            System.out.println("[ * ] Listening for TCP connections at " + s.getInetAddress().getHostAddress() + ":" + s.getLocalPort());
            SERVER_PORT = s.getLocalPort();
        } catch(IOException e){
            System.out.println("[ ! ] An error has occurred while setting up TCP socket");
            e.printStackTrace();
            return;
        }

        threads.add(new ThreadAtendeClientes(Integer.parseInt(args[0]), args[1]));
        threads.add(new ThreadHeartbeat("239.39.39.39", 4004, SERVER_PORT, onlineServers));
        for(Thread thrd : threads) {
            thrd.start();
        }
    }

}