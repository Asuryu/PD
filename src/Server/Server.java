package Server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Server {

    // TODO: Copiar ficheiro PD-2022-23-TP.db sempre que um servidor é aberto

    public final String DATABASE_PATH;

    public int TCP_PORT;
    public ServerSocket s = null;

    public final int UDP_PORT;
    public DatagramSocket ds = null;

    public final String MULTICAST_IP = "239.39.39.39";
    public final int MULTICAST_PORT = 4004;
    public InetAddress ipGroup;
    public MulticastSocket ms;
    public SocketAddress sa;
    public NetworkInterface ni;

    public final ArrayList<Thread> threads = new ArrayList<>();
    public final ArrayList<Heartbeat> onlineServers = new ArrayList<>();

    public Server(int UDP_PORT, String DATABASE_PATH) throws Exception {
        this.UDP_PORT = UDP_PORT;
        this.DATABASE_PATH = DATABASE_PATH;

        ThreadInicialHeartbeat tihb = new ThreadInicialHeartbeat(this);
        tihb.start();
        tihb.join(30000); // TODO: 30 segundos
        System.out.println(onlineServers);
        ms.leaveGroup(sa, ni);

        try{
            s = new ServerSocket(0);
            System.out.println("[ * ] Listening for TCP connections at " + s.getInetAddress().getHostAddress() + ":" + s.getLocalPort());
            TCP_PORT = s.getLocalPort();
        } catch(IOException e) {
            System.out.println("[ ! ] An error has occurred while setting up TCP socket");
            System.out.println("      " + e.getMessage());
            return;
        }

        ThreadAtendeClientes tac = new ThreadAtendeClientes(this);
        ThreadHeartbeat thb = new ThreadHeartbeat(this);
        threads.add(tac); threads.add(thb);

        for(Thread thrd : threads) {
            thrd.start();
        }

        for(Thread thrd : threads) {
            thrd.join();
        }

//        Thread.sleep(500);
//        ThreadConsolaAdmin console = new ThreadConsolaAdmin(this);
//        threads.add(console);
//        console.start();

        s.close();
        ds.close();
        ms.close();
    }

    public static void main(String[] args) {
        try {
            new Server(Integer.parseInt(args[0]), args[1]);
        } catch (Exception e) {
            System.out.println("[ ! ] An error has occurred while setting up the server");
            System.out.println("      " + e.getMessage());
        }
    }
}