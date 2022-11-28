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
                case "LOGIN" -> login(arrayRequest[1], arrayRequest[2]);
                case "EDIT_PROFILE" -> edit_profile();
                case "AWAITING_PAYMENT_CONFIRMATION" -> listPayments("AWAITING_PAYMENT_CONFIRMATION");
                case "PAYMENT_CONFIRMED" -> listPayments("PAYMENT_CONFIRMED");
                case "SHOWS_LIST_SEARCH" -> shows_list_search();
                case "SELECT_SHOW" -> select_show();
                case "AVAILABLE_SEATS_AND_PRICE" -> available_seats_and_price();
                case "SELECT_SEATS" -> select_seats();
                case "VALIDATE_RESERVATION" -> validate_reservation();
                case "REMOVE_RESERVATION" -> remove_reservation();
            }
            synchronized (server.activeConnections) {
                server.activeConnections.remove(client);
            }
            client.close();
        }catch (IOException | ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }

    private void remove_reservation() {

    }

    private void validate_reservation() {
    }

    private void select_seats() {
    }

    private void available_seats_and_price() {
    }

    private void select_show() {
    }

    private void shows_list_search() {
    }

    private void listPayments(String whatToList) {
    }

    private void edit_profile() {

    }

    private void login(String username, String password) {
        try {
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT username FROM utilizador");
            rs.next();
            if(username.equals("admin") && password.equals("admin")){
                client.getOutputStream().write("ADMIN_LOGIN_SUCCESSFUL".getBytes());
            }else{
                if(rs.getString("username").equals(username)) {
                    rs = stmt.executeQuery("SELECT password FROM utilizador");
                    if(rs.getString("password").equals(password)) {
                        client.getOutputStream().write("LOGIN_SUCCESSFUL".getBytes());
                    }
                }else{
                    client.getOutputStream().write("LOGIN_FAILED".getBytes());
                }
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getDatabase() throws IOException {
        System.out.println("[ * ] A server is requesting the database");
        File file = new File(server.DATABASES_PATH + server.DATABASE_NAME);
        FileInputStream fis = new FileInputStream(file);
        // Enviar ficheiro aos poucos
        int n;
        byte[] fileRead = new byte[4000];
        while ((n = fis.read(fileRead)) != -1) {
            client.getOutputStream().write(fileRead, 0, n);
            //System.out.println("Sent " + n + " bytes");
        }
        client.getOutputStream().flush();
        System.out.println("[ * ] Sent database to server " + client.getInetAddress().getHostAddress() + ":" + client.getPort());
    }

    private void register(String nome, String username, String password) throws SQLException {
        try {
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT username FROM utilizador");
            rs.next();
            if(rs.getString("username").equals(username)) {
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
