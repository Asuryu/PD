package Server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collection;

public class Server {

    // TODO: Copiar ficheiro PD-2022-23-TP.db sempre que um servidor é aberto

    public static void main(String[] args) throws InterruptedException {
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<Heartbeat> onlineServers = new ArrayList<>();

        ThreadInicialHeartbeat tihb = new ThreadInicialHeartbeat("239.39.39.39", 4004, onlineServers);
        tihb.start();
        tihb.join(30000); // TODO: 30 segundos

        int SERVER_PORT;
        try{
            ServerSocket s = new ServerSocket(0);
            System.out.println("[ * ] Listening for TCP connections at " + s.getInetAddress().getHostAddress() + ":" + s.getLocalPort());
            SERVER_PORT = s.getLocalPort();
        } catch(IOException e) {
            System.out.println("[ ! ] An error has occurred while setting up TCP socket");
            System.out.println("      " + e.getMessage());
            return;
        }

        ThreadAtendeClientes tac = new ThreadAtendeClientes(Integer.parseInt(args[0]), args[1]);
        ThreadHeartbeat thb = new ThreadHeartbeat("239.39.39.39", 4004, SERVER_PORT, onlineServers);
        threads.add(tac); threads.add(thb);

        for(Thread thrd : threads) {
            thrd.start();
        }

        Thread.sleep(500);
        ThreadConsolaAdmin console = new ThreadConsolaAdmin(onlineServers, threads);
        threads.add(console);
        console.start();
    }
}