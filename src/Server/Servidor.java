package Server;

import Server.Comparators.HeartbeatComparatorLoad;
import Server.Threads.*;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Servidor {

    public final String DATABASE_ORIGINAL = "PD-2022-23-TP.db"; // Name of the original database file
    public final String DATABASES_PATH; // Path to the folder where the databases are stored
    public final String DATABASE_NAME; // Name of the database file
    public final String JDBC_STRING; // JDBC string to connect to the database
    public Connection dbConn; // Connection to the database
    public int dbVersion; // Version of the database
    public ServerSocket s; // Socket to receive TCP connections
    public String TCP_IP; // Port to receive TCP connections
    public int TCP_PORT; // Port to receive TCP connections

    public DatagramSocket ds; // Socket to receive UDP packets
    public final int UDP_PORT; // Port to receive UDP packets

    public final String MULTICAST_IP = "239.20.19.109"; // Multicast IP address (string)
    public final int MULTICAST_PORT = 4004; // Multicast port
    public InetAddress ipGroup; // Multicast IP address
    public MulticastSocket ms; // Multicast socket
    public SocketAddress sa; // Socket address
    public NetworkInterface ni; // Network interface

    public Boolean isAvailable = true; // Flag to indicate if the server is available to receive connections
    public final ArrayList<Thread> threads = new ArrayList<>(); // List of threads
    public final ArrayList<Heartbeat> onlineServers = new ArrayList<>(); // List of online servers
    public final ArrayList<Socket> activeConnections = new ArrayList<>(); // List of active connections
    public final HashMap<Integer, ArrayList<String>> dbVersions = new HashMap<>(); // Map of database versions

    public Servidor(int UDP_PORT, String DATABASES_PATH) throws Exception {
        this.UDP_PORT = UDP_PORT;
        this.DATABASES_PATH = DATABASES_PATH;
        this.DATABASE_NAME = "PD-2022-23-TP-" + UDP_PORT + ".db";
        this.JDBC_STRING = "jdbc:sqlite:" + DATABASES_PATH + DATABASE_NAME;

        mostraASCII();
        s = new ServerSocket(0);
        TCP_IP = s.getInetAddress().getHostAddress();
        TCP_PORT = s.getLocalPort();

        // Começar à escuta por heartbeats (30 segundos)
        ThreadInicialHeartbeat tihb = new ThreadInicialHeartbeat(this);
        tihb.start();
        tihb.join(30000);
        ms.leaveGroup(sa, ni);

        /* Se o servidor não tiver uma cópia local da base de dados
        e existirem servidores com uma cópia, pede uma cópia ao que tiver
        a base de dados com a versão mais atualizada e com menor carga */
        File copy = new File(DATABASES_PATH + DATABASE_NAME);
        if(!onlineServers.isEmpty()){
            // Get the server with the highest database version and the loweast load
            Heartbeat hb = onlineServers.stream()
                    .collect(Collectors.groupingBy(Heartbeat::getDbVersion, TreeMap::new, Collectors.toList()))
                    .lastEntry().getValue().stream()
                    .min(new HeartbeatComparatorLoad()).get();

            // Removes servers with a lower database version
            onlineServers.removeIf(h -> h.getDbVersion() <= 1);

            // Establishes a connection to the server with the highest database version and the loweast load
            // and requests a copy of the database
            if(!copy.exists() && !onlineServers.isEmpty()){
                Socket s = new Socket("localhost", hb.getPort());
                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                FileOutputStream fos = new FileOutputStream(DATABASES_PATH + DATABASE_NAME);
                out.writeObject("GET_DATABASE"); // Envia a mensagem ao servidor a pedir a base de dados
                out.flush();
                // Receber ficheiro aos poucos
                byte[] msgByte = new byte[4000];
                while(s.getInputStream().read(msgByte) != -1){
                    fos.write(msgByte);
                }
                System.out.println("[ · ] Received database from server " + s.getInetAddress().getHostAddress() + ":" + hb.getPort());
                fos.close();
                out.close();
                in.close();
                s.close();
            }
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

        s.close();
        ds.close();
        ms.close();
    }

    public synchronized int getDbVersion(){
        try {
            dbConn = DriverManager.getConnection(JDBC_STRING);
            Statement stmt = dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT version FROM database WHERE id=1");
            rs.next();
            dbVersion = rs.getInt("version");
            rs.close();
            stmt.close();
            return dbVersion;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void sendOnlineServers() throws IOException {

        for(Socket s : activeConnections){
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject("ONLINE_SERVERS");
            synchronized (onlineServers) {
                onlineServers.sort(new HeartbeatComparatorLoad());
                out.writeObject(onlineServers);
            }
            out.flush();
        }
    }

    private void mostraASCII(){
        System.out.println("██████╗  ██████╗ ██╗      ██████╗ ██████╗");
        System.out.println("██╔══██╗██╔═══██╗██║      ██╔══██╗██╔══██╗");
        System.out.println("██████╔╝██║   ██║██║█████╗██████╔╝██║  ██║");
        System.out.println("██╔══██╗██║   ██║██║╚════╝██╔═══╝ ██║  ██║");
        System.out.println("██████╔╝╚██████╔╝███████╗ ██║     ██████╔╝");
        System.out.println("╚═════╝  ╚═════╝ ╚══════╝ ╚═╝     ╚═════╝ \n");
    }

    public synchronized void incDbVersion(){
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