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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
                //System.out.println("Received: " + Arrays.toString(arrayRequest)); // DEBUG
                switch (arrayRequest[0].toUpperCase()) {
                    case "GET_DATABASE" -> getDatabase();
                    case "REGISTER" -> register(arrayRequest[1], arrayRequest[2], arrayRequest[3]);
                    case "LOGIN" -> login(arrayRequest[1], arrayRequest[2]);
                    case "EDIT_PROFILE" -> edit_profile(arrayRequest[1], arrayRequest[2], arrayRequest[3]);
                    case "AWAITING_PAYMENT_CONFIRMATION" -> listPayments("AWAITING_PAYMENT_CONFIRMATION");
                    case "PAYMENT_CONFIRMED" -> listPayments("PAYMENT_CONFIRMED");
                    case "SHOWS_LIST_SEARCH" -> {
                        System.out.println("Received: " + Arrays.toString(arrayRequest)); // DEBUG
                        HashMap<String, String> filters = new HashMap<>();
                        for (int i = 1; i < arrayRequest.length; i++) {
                            String[] filter = arrayRequest[i].split(" ");
                            if(filter.length == 2) filters.put(filter[0], filter[1]);
                        }
                        shows_list_search(filters);
                    }
                    case "SELECT_SHOW" -> select_show(Integer.parseInt(arrayRequest[1]));
                    case "AVAILABLE_SEATS_AND_PRICE" -> available_seats_and_price(Integer.parseInt(arrayRequest[1]));
                    case "SELECT_SEATS" -> select_seats(arrayRequest[1]);
                    case "REMOVE_RESERVATION" -> remove_reservation(Integer.parseInt(arrayRequest[1]));
                    case "PAY" -> pay(Integer.parseInt(arrayRequest[1]));
                    case "REMOVE_SHOW" -> removeShow(Integer.parseInt(arrayRequest[1]));
                    case "INSERT_SHOW" -> insertShow(arrayRequest[1]);
                    case "LOGOUT" -> logout();
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
                out.flush();
            }
        } catch (SQLException | IOException e) {
            out.writeObject("ERROR_OCCURED");
            out.flush();
            System.out.println("[ ! ] An error has occurred while removing show");
            System.out.println("      " + e.getMessage());
        }
    }
    private void insertShow(String data) throws IOException {
        try{
            if(admin){
                String[] arrayData = data.split(",");
                server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
                Statement stmt = server.dbConn.createStatement();
                String format = "INSERT INTO espetaculo (descricao, tipo, data_hora, duracao, local, localidade, pais, classificacao_etaria, visivel) VALUES " +
                        "('" + arrayData[0] + "', '" + arrayData[1] + "', '" + arrayData[2] + "', '" + arrayData[3] + "', '" + arrayData[4] + "', '" + arrayData[5] + "', '" + arrayData[6] + "', '" + arrayData[7] + "', '" + arrayData[8] + "')";
                stmt.executeUpdate(format);
                server.incDbVersion(format);
                out.writeObject("SHOW_INSERTED_SUCCESSFULLY");
                out.flush();
            }
        }catch (SQLException | IOException e) {
            out.writeObject("ERROR_OCCURED");
            out.flush();
            System.out.println("[ ! ] An error has occurred while inserting show");
            System.out.println("      " + e.getMessage());
        }
    }

    private void pay(int reservationID) throws IOException, SQLException {
        try{
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            String format = ("UPDATE reserva SET pago = 1 WHERE id = " + reservationID + " AND id_utilizador = " + clientID);
            stmt.executeUpdate(format);
            server.incDbVersion(format);
            out.writeObject("PAYMENT_CONFIRMED");
            out.flush();
        }catch (SQLException e){
            out.writeObject("ERROR_OCCURED");
            out.flush();
            System.out.println("[ ! ] An error has occurred while paying reservation");
            System.out.println("      " + e.getMessage());
        }

    }

    private void remove_reservation(int reservationID) throws IOException, SQLException {
        try{
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            String format = "DELETE FROM reserva WHERE id= "+ reservationID +" AND id_utilizador = "+ clientID +" AND pago = 0";
            String format2 = "DELETE FROM reserva_lugar WHERE id_reserva = " + reservationID;
            stmt.executeUpdate(format);
            stmt.executeUpdate(format2);
            out.writeObject("RESERVA_SUCCESSFULLY_REMOVED");
            out.flush();
            server.incDbVersion(format);
        }catch (SQLException e){
            out.writeObject("ERROR_OCCURED");
            out.flush();
            System.out.println("[ ! ] An error has occurred while removing reservation");
            System.out.println("      " + e.getMessage());
        }
    }

    private void select_seats(String seatsWanted) throws IOException {
        try {
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            String[] seats = seatsWanted.split(",");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime now = LocalDateTime.now();
            String format = "INSERT INTO reserva (data_hora, pago, id_utilizador, id_espetaculo) VALUES ('%s', 0, %d, %d)";
            format = String.format(format, dtf.format(now), clientID, showID);
            stmt.executeUpdate(format);
            server.incDbVersion(format);
            ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ROWID()");
            rs.next();
            reservationID = rs.getInt(1);
            HashMap<String, ArrayList<String>> seatsMap = new HashMap<>();
            seatsMap.put("RESERVED", new ArrayList<>());
            seatsMap.put("ALREADY_RESERVED", new ArrayList<>());
            for (String seat : seats) {
                ResultSet rs2 = stmt.executeQuery("SELECT id_lugar FROM reserva_lugar WHERE id_lugar =" + seat);
                if(rs2.next()){
                     seatsMap.get("ALREADY_RESERVED").add(seat);
                }else {
                    String format2 = "INSERT INTO reserva_lugar (id_reserva, id_lugar) VALUES (%d, %s)";
                    format2 = String.format(format2, reservationID, seat);
                    seatsMap.get("ALREADY_RESERVED").add(seat);
                    server.incDbVersion(format2);
                }
            }
            out.writeObject("RESERVATION_SUCCESSFULLY_MADE");
            out.flush();
            out.writeObject(seatsMap);
            out.flush();
        } catch (SQLException e) {
            out.writeObject("ERROR_OCCURED");
            out.flush();
            System.out.println("[ ! ] An error has occurred while showing seats");
            System.out.println("      " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void available_seats_and_price(int argShowID) throws IOException {
        // Available seats and price for the selected show
        try {
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM lugar WHERE espetaculo_id = " + argShowID);
            ArrayList<String> seats = new ArrayList<>();
            out.writeObject("AVAILABLE_SEATS");
            out.flush();
            while (rs.next()) {
                String result = "ID: " + rs.getString("id") + "\t" + "Fila: " + rs.getString("fila") + "\t" + "Assento: " + rs.getString("assento") + "\t" + "Preço: " + rs.getString("preco");
                seats.add(result);
            }
            out.writeObject(seats);
            out.flush();
            showID = argShowID;
        } catch (SQLException | IOException e) {
            out.writeObject("ERROR_OCCURED");
            out.flush();
            System.out.println("[ ! ] An error has occurred while showing available seats and price");
            System.out.println("      " + e.getMessage());
        }
    }

    private void select_show(int argShowID) throws IOException {
        // Select a show with at least 24 hours before the show starts
        try{
            showID = argShowID;
            out.writeObject("SHOW_SELECTED");
            out.flush();
        } catch (IOException e) {
            out.writeObject("ERROR_OCCURED");
            out.flush();
            System.out.println("[ ! ] An error has occurred while selecting show");
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
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime now = LocalDateTime.now();
            String format = "SELECT * FROM espetaculo WHERE visivel = 1 AND data_hora > '" + dtf.format(now.plusDays(1)) + "'";
            if(filters.get("descricao") != null){
                format += " AND descricao LIKE '%" + filters.get("descricao") + "%'";
            }
            if(filters.get("tipo") != null){
                format += " AND tipo LIKE '%" + filters.get("tipo") + "%'";
            }
            if(filters.get("data_hora") != null){
                format += " AND data_hora LIKE '%" + filters.get("data_hora") + "%'";
            }
            if(filters.get("duracao") != null){
                format += " AND duracao LIKE '%" + filters.get("duracao") + "%'";
            }
            if(filters.get("local") != null){
                format += " AND local LIKE '%" + filters.get("local") + "%'";
            }
            if(filters.get("localidade") != null){
                format += " AND localidade LIKE '%" + filters.get("localidade") + "%'";
            }
            if(filters.get("pais") != null){
                format += " AND pais LIKE '%" + filters.get("pais") + "%'";
            }
            System.out.println(format);
            out.writeObject("SHOW_FOUND");
            out.flush();
            rs = stmt.executeQuery(format);
            while (rs.next()) {
                String result = ("Show ID: " + rs.getString("id") + "\t" + "Show description: " +  rs.getString("descricao") + "\t" + "Show type: " + rs.getString("tipo") + "\t" + "Show date and time: " + rs.getString("data_hora") + "\t" + "Show locale: " + rs.getString("localidade") + "\t" + "Show age rating: " + rs.getString("classificacao_etaria")); // TODO: To be improved
                shows.add(result);
            }
            out.writeObject(shows);
            out.flush();
        } catch (SQLException | IOException e) {
            out.writeObject("ERROR_OCCURED");
            out.flush();
            System.out.println("[ ! ] An error has occurred while showing shows list");
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
                out.flush();
                rs = stmt.executeQuery("SELECT * FROM reserva WHERE pago = 0 AND id_utilizador = " + clientID);
                while(rs.next()){
                    String payment = "Reservation ID: " + rs.getString("id");
                    ResultSet rs2 = stmt.executeQuery("SELECT * FROM espetaculo WHERE id = " + rs.getString("id_espetaculo"));
                    rs2.next();
                    payment += " | Show: " + rs2.getString("descricao") + " | Date: " + rs2.getString("data_hora");
                    ResultSet rs3 = stmt.executeQuery("SELECT * FROM lugar WHERE espetaculo_id = " + rs2.getString("id"));
                    rs3.next();
                    payment += " | Price: " + rs3.getString("preco");
                    payments.add(payment);
                }
                out.writeObject(payments);
                out.flush();
            }else if(whatToList.equals("PAYMENT_CONFIRMED")){
                out.writeObject("PAYMENT_CONFIRMED");
                out.flush();
                rs = stmt.executeQuery("SELECT * FROM reserva WHERE pago = 1 AND id_utilizador = " + clientID);
                while(rs.next()){
                    String payment = "Reservation ID: " + rs.getString("id") + " | Show: " + rs.getString("id_espetaculo") + " | Date: " + rs.getString("data_hora");
                    payments.add(payment);
                }
                out.writeObject(payments);
                out.flush();
            }
        }catch (IOException | SQLException e) {
            out.writeObject("ERROR_OCCURED");
            out.flush();
            System.out.println("[ ! ] An error has occurred while listing payments");
            System.out.println("      " + e.getMessage());
            e.printStackTrace();
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
                    admin = true;
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
            ResultSet rs = stmt.executeQuery("SELECT id, username, nome FROM utilizador WHERE username='" + username + "' OR nome='" + nome + "'");
            clientID = rs.getInt("id");
            if(nome.equals("") || username.equals("") || password.equals("")){
                out.writeObject("REGISTER_FAILED");
                out.flush();
            }else if(rs.next()){
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
            out.flush();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            out.writeObject("REGISTER_FAILED");
            out.flush();
            System.out.println("[ ! ] An error has occurred while registering user");
            System.out.println("      " + e.getMessage());
        }
    }

    private void logout(){
        try {
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            String updateQuery = "UPDATE utilizador SET autenticado = 0 WHERE id = " + clientID;
            stmt.executeUpdate(updateQuery);
            server.incDbVersion(updateQuery);
            System.out.println("[ * ] User logged out");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[ ! ] An error has occurred while logging out user");
            System.out.println("      " + e.getMessage());
        }
    }

}
