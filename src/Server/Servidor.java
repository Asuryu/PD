package Server;

import Server.Comparators.HeartbeatComparatorLoad;
import Server.Threads.*;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Servidor {

    public final String DATABASE_ORIGINAL = "PD-2022-23-TP.db";
    public final String DATABASES_PATH;
    public final String DATABASE_NAME;
    public final String JDBC_STRING;
    public Connection dbConn;

    public ServerSocket s = null;
    public int TCP_PORT;

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

    public Servidor(int UDP_PORT, String DATABASES_PATH) throws Exception {
        this.UDP_PORT = UDP_PORT;
        this.DATABASES_PATH = DATABASES_PATH;
        this.DATABASE_NAME = "PD-2022-23-TP-" + UDP_PORT + ".db";
        this.JDBC_STRING = "jdbc:sqlite:" + DATABASES_PATH + DATABASE_NAME;

        s = new ServerSocket(0);
        TCP_PORT = s.getLocalPort();

        ThreadInicialHeartbeat tihb = new ThreadInicialHeartbeat(this);
        tihb.start();
        tihb.join(30000);
        ms.leaveGroup(sa, ni);

        File copy = new File(DATABASES_PATH + DATABASE_NAME);
        if(!copy.exists() && !onlineServers.isEmpty()){
            // get the server with the highest database version and the lowest load
            Heartbeat hb = onlineServers.stream()
                    .collect(Collectors.groupingBy(Heartbeat::getDbVersion, TreeMap::new, Collectors.toList()))
                    .lastEntry().getValue().stream()
                    .min(new HeartbeatComparatorLoad()).get();

            Socket s = new Socket("localhost", hb.getPort());
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            out.writeObject(TCPMessages.GET_DATABASE);
            out.flush();
            File database = (File)in.readObject();
            System.out.println("[ * ] Received database from server " + s.getInetAddress().getHostAddress() + ":" + hb.getPort());
            FileOutputStream fos = new FileOutputStream(copy);
            fos.write(database.getName().getBytes());
            fos.close();
        }


        ThreadTCP tcp = new ThreadTCP(this);
        ThreadAtendeClientes tac = new ThreadAtendeClientes(this);
        ThreadHeartbeat thb = new ThreadHeartbeat(this);
        threads.add(tcp); threads.add(tac); threads.add(thb);

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
            new Servidor(Integer.parseInt(args[0]), args[1]);
        } catch (Exception e) {
            System.out.println("[ ! ] An error has occurred while setting up the server");
            System.out.println("      " + e.getMessage());
            e.printStackTrace();
        }
    }
}