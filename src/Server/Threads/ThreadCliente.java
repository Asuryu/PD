package Server.Threads;

import Server.Servidor;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class ThreadCliente extends Thread{
    protected final Servidor server;
    private final Socket client;
    public ThreadCliente(Servidor server, Socket client) {
        this.server = server;
        this.client = client;
    }

    @Override
    public void run(){
        try{
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            String request = (String)in.readObject();
            String[] arrayRequest = request.split(" ");
            switch (arrayRequest[0].toUpperCase()) {
                case "GET_DATABASE" -> getDatabase();
                case "REGISTER" -> {}
                case "LOGIN" -> {}
                case "EDIT_PROFILE" -> {}
                case "AWAITING_PAYMENT_CONFIRMATION" -> {}
                case "PAYMENT_CONFIRMED" -> {}
                case "SHOWS_LIST_SEARCH" -> {}
                case "SELECT_SHOW" -> {}
                case "AVAILABLE_SEATS_AND_PRICE" -> {}
                case "SELECT_SEATS" -> {}
                case "VALIDATE_RESERVATION" -> {}
                case "REMOVE_RESERVATION" -> {}
            }
            synchronized (server.activeConnections) {
                server.activeConnections.remove(client);
            }
            client.close();
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private void getDatabase() throws IOException {
        System.out.println("[ * ] A server is requesting the database");
        File file = new File(server.DATABASES_PATH + server.DATABASE_NAME);
        FileInputStream fis = new FileInputStream(file);
        // Enviar ficheiro aos poucos
        int n;
        byte[] fileRead = new byte[4000];
        do {
            n = fis.read(fileRead);
            if (n == -1) break;
            client.getOutputStream().write(fileRead);
        } while (true);
        System.out.println("[ * ] Sent database to server " + client.getInetAddress().getHostAddress() + ":" + client.getPort());
    }
}
