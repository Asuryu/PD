package Server.Threads;

import Server.Servidor;
import java.io.*;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
                case "REGISTER" -> register(arrayRequest[1], arrayRequest[2], arrayRequest[3]);
                case "LOGIN" -> login();
                case "EDIT_PROFILE" -> edit_profile();
                case "AWAITING_PAYMENT_CONFIRMATION" -> listPayments("AWAITING_PAYMENT_CONFIRMATION");
                case "PAYMENT_CONFIRMED" -> listPayments("PAYMENT_CONFIRMED");
                case "SHOWS_LIST_SEARCH" -> shows_list_search();
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
        }catch (IOException | ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }

    private void shows_list_search() {
    }

    private void listPayments(String whatToList) {
    }

    private void edit_profile() {

    }

    private void login() {

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

    private void register(String nome, String username, String password) throws SQLException {
        try {
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT username FROM utilizador");
            rs.next();
            if(rs.getString("username").equals(nome)) {
                System.out.println("[ * ] Username already exists");
            }else{
                stmt.executeUpdate("INSERT INTO utilizador (nome, username, password) VALUES ('" + nome + "', '" + username + "', '" + password + "')");
                System.out.println("[ * ] User registered");
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
