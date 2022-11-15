package Server.Threads;

import Server.Servidor;
import Server.TCPMessages;

import java.io.*;
import java.net.ServerSocket;
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
            TCPMessages request = (TCPMessages)in.readObject(); // Obtém a mensagem do cliente
            System.out.println(request);
            switch (request) {
                case GET_DATABASE -> getDatabase();
                case LOGIN -> {
                    out.writeObject("Dá-me as tuas credenciais");
                    HashMap<String, String> recv = (HashMap<String, String>) in.readObject();
                    System.out.println(recv);
                }
                //case ... -> func();
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
