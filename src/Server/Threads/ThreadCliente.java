package Server.Threads;

import Server.Servidor;
import java.io.*;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ThreadCliente extends Thread{
    protected final Servidor server;
    private final Socket client;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ThreadCliente(Servidor server, Socket client) {
        this.server = server;
        this.client = client;
    }

    @Override
    public void run(){
        try{
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
            String request = (String)in.readObject();
            String[] arrayRequest = request.split(" ");
            switch (arrayRequest[0].toUpperCase()) {
                case "GET_DATABASE" -> getDatabase();
                case "REGISTER" -> register(arrayRequest[1], arrayRequest[2], arrayRequest[3]);
                case "LOGIN" -> login(arrayRequest[1], arrayRequest[2]);
                case "EDIT_PROFILE" -> edit_profile(arrayRequest[1], arrayRequest[2], arrayRequest[3]);
                case "AWAITING_PAYMENT_CONFIRMATION" -> listPayments("AWAITING_PAYMENT_CONFIRMATION");
                case "PAYMENT_CONFIRMED" -> listPayments("PAYMENT_CONFIRMED");
                case "SHOWS_LIST_SEARCH" -> shows_list_search(arrayRequest[1]);
                case "SELECT_SHOW" -> select_show();
                case "AVAILABLE_SEATS_AND_PRICE" -> available_seats_and_price(Integer.parseInt(arrayRequest[1]));
                case "SELECT_SEATS" -> select_seats(arrayRequest[1]);
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

    private void select_seats(String seatsWanted) {
        // Select seats
        // Send confirmation
        try {
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            String[] seats = seatsWanted.split(",");
            // TODO: Finish later, need to think how to indeed "select" the seats
            // Is it already associated with a reservation? No?
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void available_seats_and_price(int showID) throws IOException {
        // Available seats and price for the selected show
        try {
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM lugar WHERE espetaculo_id = " + showID);
            ArrayList<String> seats = new ArrayList<>();
            while (rs.next()) {
                seats.add(rs.getString("fila") + " " + rs.getString("assento") + " " + rs.getString("preco"));
            }
            client.getOutputStream().write(seats.toString().getBytes());
        } catch (SQLException | IOException e) {
            client.getOutputStream().write("ERROR_OCCURED".getBytes());
        }
    }

    private void select_show() throws IOException {
        // Select a show with at least 24 hours before the show starts
        try{
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM espetaculos WHERE data > NOW() + INTERVAL 24 HOUR");
            ArrayList<String> shows = new ArrayList<>();
            while (rs.next()) {
                shows.add(rs.getString("id") + " " + rs.getString("name") + " " + rs.getString("date") + " " + rs.getString("price"));
            }
            client.getOutputStream().write(shows.toString().getBytes());
        } catch (SQLException | IOException throwables) {
            client.getOutputStream().write("ERROR_OCCURED".getBytes());
        }
    }

    private void shows_list_search(String filter) {
        // Consult and search in the database for shows
        // Send the list of shows to the client
        try{
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM espetaculo");
            ArrayList<String> shows = new ArrayList<>();
            while(rs.next()){
                shows.add(rs.getString("name"));
            }
            client.getOutputStream().write(shows.toString().getBytes());
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void listPayments(String whatToList) throws IOException {
        try{
            // TODO: Check later how the user is being confirmed
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            ArrayList<String> payments = new ArrayList<>();
            if(whatToList.equals("AWAITING_PAYMENT_CONFIRMATION")){
                ResultSet rs = stmt.executeQuery("SELECT * FROM payments WHERE pago = 0");
                while(rs.next()){
                    String payment = rs.getString("payment_id") + " " + rs.getString("payment_amount") + " " + rs.getString("payment_date") + " " + rs.getString("payment_confirmed");
                    payments.add(payment);
                    client.getOutputStream().write(payment.getBytes());
                }
            }else if(whatToList.equals("PAYMENT_CONFIRMED")){
                ResultSet rs = stmt.executeQuery("SELECT * FROM payments WHERE pago = 1");
                while(rs.next()){
                    String payment = rs.getString("payment_id") + " " + rs.getString("payment_amount") + " " + rs.getString("payment_date") + " " + rs.getString("payment_confirmed");
                    payments.add(payment);
                    client.getOutputStream().write(payment.getBytes());
                }
            }
        }catch (IOException | SQLException e) {
            client.getOutputStream().write("ERROR_OCCURED".getBytes());
        }
    }

    private void edit_profile(String name, String username, String password) {
        try{
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE username='"+username+"'");
            try{
                if(rs.next()){
                    stmt.executeUpdate(String.format("UPDATE users SET name='%s', username = '%s', password='%s' WHERE username='%s'", name, username, password, username));
                    client.getOutputStream().write("UPDATE_SUCCESSFUL".getBytes());
                }else{
                    client.getOutputStream().write("USER_NOT_FOUND".getBytes());
                }
            }catch (IOException | SQLException e){
                client.getOutputStream().write("ERROR_OCCURED".getBytes());
                System.out.println("[ ! ] An error has occurred while editing profile");
                System.out.println("      " + e.getMessage());
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void login(String username, String password) {
        try {
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT username FROM utilizador");
            rs.next();
            if(username.equals("admin") && password.equals("admin")){
                out.writeObject("ADMIN_LOGIN_SUCCESSFUL");
            }else{
                if(rs.getString("username").equals(username)) {
                    rs = stmt.executeQuery("SELECT password FROM utilizador");
                    if(rs.getString("password").equals(password)) {
                        out.writeObject("LOGIN_SUCCESSFUL");
                    }
                }else{
                    out.writeObject("LOGIN_FAILED");
                }
            }
            rs.close();
            stmt.close();

        } catch (SQLException | IOException e) {
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

    private void register(String nome, String username, String password) throws SQLException, IOException {
        try {
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT username FROM utilizador");
            rs.next();
            stmt.executeUpdate("INSERT INTO utilizador (nome, username, password) VALUES ('" + nome + "', '" + username + "', '" + password + "')");
            System.out.println("[ * ] User registered");
            out.writeObject("REGISTER_SUCCESSFUL");
            out.flush();
        } catch (SQLException e) {
            System.out.println("[ * ] Username already exists");
            out.writeObject("REGISTER_FAILED");
            out.flush();
            //throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
