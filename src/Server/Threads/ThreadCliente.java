package Server.Threads;

import Server.Heartbeat;
import Server.Servidor;
import java.io.*;
import java.net.DatagramPacket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ThreadCliente extends Thread{
    protected final Servidor server;
    private final Socket client;
    private int clientID;
    private int showID;
    private int reservationID;
    private boolean admin = false;
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
            while (!isInterrupted()){
                String[] arrayRequest = (String[])in.readObject();
                System.out.println("Received: " + Arrays.toString(arrayRequest));
                switch (arrayRequest[0].toUpperCase()) {
                    case "GET_DATABASE" -> getDatabase();
                    case "REGISTER" -> register(arrayRequest[1], arrayRequest[2], arrayRequest[3]);
                    case "LOGIN" -> login(arrayRequest[1], arrayRequest[2]);
                    case "EDIT_PROFILE" -> edit_profile(arrayRequest[1], arrayRequest[2], arrayRequest[3]);
                    case "AWAITING_PAYMENT_CONFIRMATION" -> listPayments("AWAITING_PAYMENT_CONFIRMATION");
                    case "PAYMENT_CONFIRMED" -> listPayments("PAYMENT_CONFIRMED");
                    case "SHOWS_LIST_SEARCH" -> {
                        HashMap<String, String> filters = new HashMap<>();
                        for (int i = 1; i < arrayRequest.length; i++) {
                            String[] filter = arrayRequest[i].split(",");
                            filters.put(filter[0], filter[1]);
                        }
                        shows_list_search(filters);
                    }
                    case "SELECT_SHOW" -> select_show();
                    case "AVAILABLE_SEATS_AND_PRICE" -> available_seats_and_price(Integer.parseInt(arrayRequest[1]));
                    case "SELECT_SEATS" -> select_seats(arrayRequest[1]);
                    case "REMOVE_RESERVATION" -> remove_reservation(Integer.parseInt(arrayRequest[1]));
                    case "PAY" -> pay(Integer.parseInt(arrayRequest[1]));
                    default -> client.close();
                }
                synchronized (server.activeConnections) {
                    server.activeConnections.remove(client);
                }
                
                // send heartbeat to multicast
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bOut);
                server.dbVersion = server.getDbVersion();
                Heartbeat hb = new Heartbeat(server.TCP_IP, server.TCP_PORT, server.dbVersion, server.activeConnections.size(), server.isAvailable);
                out.writeObject(hb);
                out.flush();
                DatagramPacket dp = new DatagramPacket(bOut.toByteArray(), bOut.size(), server.ipGroup, server.MULTICAST_PORT);
                server.ms.send(dp);
            }
        }
        catch (SocketException | EOFException e){
            try {
                server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
                Statement stmt = server.dbConn.createStatement();
                stmt.executeQuery("UPDATE utilizador SET autenticado = 0 WHERE id = '" + clientID + "'");
                System.out.println("[ ! ] Client " + client.getInetAddress().getHostAddress() + ":" + client.getPort() + " has disconnected");
                admin = false;
            } catch (SQLException ignored) {}
        }
        catch (IOException | ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                client.close();
                synchronized (server.activeConnections) {
                    server.activeConnections.remove(client);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void removeShow(int showID) throws IOException {
        try {
            if(admin){
                server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
                Statement stmt = server.dbConn.createStatement();
                String format = "DELETE FROM espetaculo WHERE id = '" + showID + "'";
                stmt.executeUpdate(format);
                server.incDbVersion(format);
                out.writeObject("SHOW_REMOVED_SUCCESSFULLY");
            }
        } catch (SQLException | IOException e) {
            out.writeObject("ERROR_OCCURED");
            System.out.println("[ ! ] An error has occurred while editing profile");
            System.out.println("      " + e.getMessage());
        }
    }
    private void insertShow(String data) throws IOException {
        try{
            if(admin){
                String[] arrayData = data.split(",");
                server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
                Statement stmt = server.dbConn.createStatement();
                String format = "INSERT INTO espetaculo (nome, data, preco, lotacao, lotacao_maxima) VALUES ('" + arrayData[0] + "', '" + arrayData[1] + "', '" + arrayData[2] + "', '" + arrayData[3] + "', '" + arrayData[4] + "')";
                stmt.executeUpdate(format);
                server.incDbVersion(format);
                out.writeObject("SHOW_INSERTED_SUCCESSFULLY");
            }
        }catch (SQLException | IOException e) {
            out.writeObject("ERROR_OCCURED");
            System.out.println("[ ! ] An error has occurred while editing profile");
            System.out.println("      " + e.getMessage());
        }
    }

    private void pay(int reservationID) throws IOException, SQLException {
        try{
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            String format = ("UPDATE reserva SET pago = 1 WHERE id = '" + reservationID + "' AND id_utilizador = '"+clientID+"'");
            stmt.executeUpdate(format);
            server.incDbVersion(format);
            out.writeObject("PAYMENT_CONFIRMED");
        }catch (SQLException e){
            out.writeObject("ERROR_OCCURED");
            System.out.println("[ ! ] An error has occurred while editing profile");
            System.out.println("      " + e.getMessage());
        }

    }

    private void remove_reservation(int reservationID) throws IOException, SQLException {
        try{
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            String format = "DELETE FROM reserva WHERE id=" + reservationID;
            String format2 = "DELETE FROM reserva_lugar WHERE id_reserva=" + reservationID;
            if(stmt.executeUpdate(format) == 1){
                out.writeObject("RESERVA_SUCCESSFULLY_REMOVED");
                out.flush();
                server.incDbVersion(format);
            }
            if(stmt.executeUpdate(format2) == 1){
                out.writeObject("RESERVA_LUGAR_SUCCESSFULLY_REMOVED");
                out.flush();
                server.incDbVersion(format2);
            }

        }catch (SQLException e){
            out.writeObject("ERROR_OCCURED");
            out.flush();
            System.out.println("[ ! ] An error has occurred while editing profile");
            System.out.println("      " + e.getMessage());
        }
    }

    private void select_seats(String seatsWanted) throws IOException {
        // Select seats
        // Send confirmation
        try {
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            String[] seats = seatsWanted.split(",");
            String format = "INSERT INTO reserva (data_hora, pago, id_utilizador, id_espetaculo) VALUES (NOW(), 0, clientID, showID)";
            stmt.executeUpdate(format);
            server.incDbVersion(format);
            ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
            rs.next();
            reservationID = rs.getInt(1);
            for (String seat : seats) {
                String format2 = "INSERT INTO reserva_lugar (id_reserva, id_lugar) VALUES (resevationID, seat)";
                if(stmt.executeUpdate(format2) == 0){
                    out.writeObject("SEAT_"+ seat +"_ALREADY_RESERVED");
                    out.flush();
                    return;
                }else {
                    out.writeObject("SEAT_"+ seat +"_RESERVATION_SUCCESSFUL");
                    out.flush();
                    server.incDbVersion(format2);
                }
            }
        } catch (SQLException e) {
            out.writeObject("ERROR_OCCURED");
            out.flush();
            System.out.println("[ ! ] An error has occurred while editing profile");
            System.out.println("      " + e.getMessage());
        }
    }

    private void available_seats_and_price(int argShowID) throws IOException {
        // Available seats and price for the selected show
        try {
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM lugar WHERE espetaculo_id = " + argShowID);
            ArrayList<String> seats = new ArrayList<>();
            while (rs.next()) {
                seats.add(rs.getString("id"));
            }
            out.writeObject(seats.toString());
            out.flush();
            showID = argShowID;
        } catch (SQLException | IOException e) {
            out.writeObject("ERROR_OCCURED");
            out.flush();
            System.out.println("[ ! ] An error has occurred while editing profile");
            System.out.println("      " + e.getMessage());
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
            out.writeObject(shows.toString());
            out.flush();
        } catch (SQLException | IOException e) {
            out.writeObject("ERROR_OCCURED");
            out.flush();
            System.out.println("[ ! ] An error has occurred while editing profile");
            System.out.println("      " + e.getMessage());
        }
    }

    private void shows_list_search(HashMap<String, String> filters) throws IOException {
        // Consult and search in the database for shows
        // Send the list of shows to the client
        try{
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            ResultSet rs;
            ArrayList<String> shows = new ArrayList<>();
            String format = "SELECT * FROM espetaculos WHERE visivel = 1";
            if(filters.get("descricao") != null){
                format += " name LIKE '%" + filters.get("descricao") + "%'";
            }
            if(filters.get("tipo") != null){
                format += " tipo LIKE '%" + filters.get("tipo") + "%'";
            }
            if(filters.get("data_hora") != null){
                format += " data_hora LIKE '%" + filters.get("data_hora") + "%'";
            }
            if(filters.get("duracao") != null){
                format += " duracao LIKE '%" + filters.get("duracao") + "%'";
            }
            if(filters.get("local") != null){
                format += " local LIKE '%" + filters.get("local") + "%'";
            }
            if(filters.get("localidade") != null){
                format += " localidade LIKE '%" + filters.get("localidade") + "%'";
            }
            if(filters.get("pais") != null){
                format += " pais LIKE '%" + filters.get("pais") + "%'";
            }
            rs = stmt.executeQuery(format);
            while (rs.next()) {
                shows.add(rs.getString("id") + " " + rs.getString("name") + " " + rs.getString("date") + " " + rs.getString("price"));
            }
            out.writeObject(shows.toString());
            out.flush();
        } catch (SQLException | IOException e) {
            out.writeObject("ERROR_OCCURED");
            out.flush();
            System.out.println("[ ! ] An error has occurred while editing profile");
            System.out.println("      " + e.getMessage());
        }
    }

    private void listPayments(String whatToList) throws IOException {
        try{
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            ArrayList<String> payments = new ArrayList<>();
            ResultSet rs;
            if(whatToList.equals("AWAITING_PAYMENT_CONFIRMATION")){
                out.writeObject("AWAITING_PAYMENT_CONFIRMATION");
                rs = stmt.executeQuery("SELECT * FROM reserva WHERE pago = 0 AND id_utilizador = " + clientID);
                while(rs.next()){
                    String payment = rs.getString("payment_id") + " " + rs.getString("payment_amount") + " " + rs.getString("payment_date") + " " + rs.getString("payment_confirmed");
                    payments.add(payment);
                }
                out.writeObject(payments);
                out.flush();
            }else if(whatToList.equals("PAYMENT_CONFIRMED")){
                out.writeObject("PAYMENT_CONFIRMED");
                rs = stmt.executeQuery("SELECT * FROM reserva WHERE pago = 1 AND id_utilizador = " + clientID);
                while(rs.next()){
                    String payment = rs.getString("payment_id") + " " + rs.getString("payment_amount") + " " + rs.getString("payment_date") + " " + rs.getString("payment_confirmed");
                    payments.add(payment);
                }
                out.writeObject(payments);
                out.flush();
            }
        }catch (IOException | SQLException e) {
            out.writeObject("ERROR_OCCURED");
            out.flush();
            System.out.println("[ ! ] An error has occurred while editing profile");
            System.out.println("      " + e.getMessage());
        }
    }

    private void edit_profile(String name, String username, String password) throws IOException {
        try{
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM utilizador WHERE username='"+username+"'");
            try{
                if(rs.next()){
                    String format = "UPDATE utilizador SET nome='%s', username = '%s', password='%s' WHERE username='%s'";
                    format = String.format(format, name, username, password, username);
                    stmt.executeUpdate(format);
                    out.writeObject("UPDATE_SUCCESSFUL");
                    out.flush();
                    server.incDbVersion(format);
                }else{
                    out.writeObject("USER_NOT_FOUND");
                    out.flush();
                }
            }catch (IOException | SQLException e){
                out.writeObject("ERROR_OCCURED");
                out.flush();
                System.out.println("[ ! ] An error has occurred while editing profile");
                System.out.println("      " + e.getMessage());
            }
        } catch (SQLException | IOException e) {
            out.writeObject("ERROR_OCCURED");
            out.flush();
            System.out.println("[ ! ] An error has occurred while editing profile");
            System.out.println("      " + e.getMessage());
        }
    }

    private void login(String username, String password) throws IOException {
        try {
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            String format = "SELECT id, username FROM utilizador WHERE username='%s' AND password='%s'";
            ResultSet rs = stmt.executeQuery(String.format(format, username, password));
            clientID = rs.getInt("id");
            if(rs.next()){
                ResultSet rs2 = stmt.executeQuery("SELECT username FROM utilizador WHERE administrador = 1 AND username = '" + username + "'");
                if(rs2.next()){
                    out.writeObject("ADMIN_LOGIN_SUCCESSFUL");
                    out.flush();
                }else{
                    out.writeObject("LOGIN_SUCCESSFUL");
                    out.flush();
                }
                // Check if user is an admin and puts admin boolean to true
                String updateQuery = "UPDATE utilizador SET autenticado = 1 WHERE username = '" + username + "'";
                stmt.executeUpdate(updateQuery);
                server.incDbVersion(updateQuery);
            }else{
                out.writeObject("LOGIN_FAILED");
                out.flush();
            }
        } catch (SQLException | IOException e) {
            out.writeObject("LOGIN_FAILED");
            out.flush();
            System.out.println("[ ! ] An error has occurred while logging in user");
            System.out.println("      " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void getDatabase() throws IOException {
        System.out.println("[ * ] A server is requesting the database");
        synchronized (server.dbVersions){
            out.reset();
            out.writeObject(server.dbVersions);
            out.flush();
        }
        System.out.println("[ * ] Sent database to server " + client.getInetAddress().getHostAddress() + ":" + client.getPort());
        client.close();
    }

    private void register(String nome, String username, String password) throws SQLException, IOException {
        try {
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, username FROM utilizador WHERE username='" + username + "'");
            clientID = rs.getInt("id");
            if (rs.next()) {
                System.out.println("[ ! ] User already exists");
                out.writeObject("USER_ALREADY_EXISTS");
                out.flush();
            } else {
                String format = "INSERT INTO utilizador (nome, username, password) VALUES ('%s', '%s', '%s')";
                stmt.executeUpdate(String.format(format, nome, username, password));
                System.out.println("[ * ] User registered");
                out.writeObject("REGISTER_SUCCESSFUL");
                out.flush();
                String updateQuery = "UPDATE utilizador SET autenticado = 1 WHERE username = '" + username + "'";
                stmt.executeUpdate(updateQuery);
                server.incDbVersion(updateQuery);
            }
            rs.close();
            out.writeObject("REGISTER_SUCCESSFUL");
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            out.writeObject("REGISTER_FAILED");
            out.flush();
            System.out.println("[ ! ] An error has occurred while editing profile");
            System.out.println("      " + e.getMessage());
        }
    }
}
