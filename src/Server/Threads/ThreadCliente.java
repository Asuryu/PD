package Server.Threads;

import Server.Servidor;
import java.io.*;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class ThreadCliente extends Thread{
    protected final Servidor server;
    private final Socket client;
    private int clientID;
    private int showID;
    private int reservationID;
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
                case "SHOWS_LIST_SEARCH" -> {}//shows_list_search(arrayRequest[1]);
                case "SELECT_SHOW" -> select_show();
                case "AVAILABLE_SEATS_AND_PRICE" -> available_seats_and_price(Integer.parseInt(arrayRequest[1]));
                case "SELECT_SEATS" -> select_seats(arrayRequest[1]);
                case "REMOVE_RESERVATION" -> remove_reservation(Integer.parseInt(arrayRequest[1]));
            }
            synchronized (server.activeConnections) {
                server.activeConnections.remove(client);
            }
            client.close();
        }catch (IOException | ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }

    private void remove_reservation(int reservationID) throws IOException, SQLException {
        try{
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            String format = "DELETE FROM reserva WHERE id=" + reservationID;
            String format2 = "DELETE FROM reserva_lugar WHERE id_reserva=" + reservationID;
            if(stmt.executeUpdate(format) == 1){
                client.getOutputStream().write("RESERVA_SUCCESSFULLY_REMOVED".getBytes());
                client.getOutputStream().flush();
                server.incDbVersion(format);
            }
            if(stmt.executeUpdate(format2) == 1){
                client.getOutputStream().write("RESERVA_LUGAR_SUCCESSFULLY_REMOVED".getBytes());
                client.getOutputStream().flush();
                server.incDbVersion(format2);
            }

        }catch (SQLException e){
            client.getOutputStream().write("ERROR_OCCURED".getBytes());
            client.getOutputStream().flush();
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
                    client.getOutputStream().write("SEAT_ALREADY_RESERVED".getBytes());
                    client.getOutputStream().flush();
                    return;
                }else {
                    client.getOutputStream().write("SEAT_RESERVATION_SUCCESSFUL".getBytes());
                    client.getOutputStream().flush();
                    server.incDbVersion(format2);
                }
            }
        } catch (SQLException e) {
            client.getOutputStream().write("ERROR_OCCURED".getBytes());
            client.getOutputStream().flush();
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
            client.getOutputStream().write(seats.toString().getBytes());
            client.getOutputStream().flush();
            showID = argShowID;
        } catch (SQLException | IOException e) {
            client.getOutputStream().write("ERROR_OCCURED".getBytes());
            client.getOutputStream().flush();
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
            client.getOutputStream().write(shows.toString().getBytes());
            client.getOutputStream().flush();
        } catch (SQLException | IOException e) {
            client.getOutputStream().write("ERROR_OCCURED".getBytes());
            client.getOutputStream().flush();
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
            if(filters.get("descricao") != null && filters.get("tipo") != null && filters.get("data_hora") != null && filters.get("local") != null && filters.get("localidade") != null && filters.get("pais") != null){
                rs = stmt.executeQuery("SELECT * FROM espetaculos WHERE descricao LIKE '%" + filters.get("descricao") + "%' AND tipo LIKE '%" + filters.get("tipo") + "%' AND data_hora LIKE '%" + filters.get("data_hora") + "%' AND local LIKE '%" + filters.get("local") + "%' AND localidade LIKE '%" + filters.get("localidade") + "%' AND pais LIKE '%" + filters.get("pais") + "%'");
                while (rs.next()) {
                    shows.add(rs.getString("id") + " " + rs.getString("name") + " " + rs.getString("date") + " " + rs.getString("price"));
                }
                client.getOutputStream().write(shows.toString().getBytes());
                client.getOutputStream().flush();
            }else if(filters.get("descricao") != null && filters.get("tipo") != null && filters.get("data_hora") != null && filters.get("local") != null && filters.get("localidade") != null){
                rs = stmt.executeQuery("SELECT * FROM espetaculos WHERE descricao LIKE '%" + filters.get("descricao") + "%' AND tipo LIKE '%" + filters.get("tipo") + "%' AND data_hora LIKE '%" + filters.get("data_hora") + "%' AND local LIKE '%" + filters.get("local") + "%' AND localidade LIKE '%" + filters.get("localidade") + "%'");
                while (rs.next()) {
                    shows.add(rs.getString("id") + " " + rs.getString("name") + " " + rs.getString("date") + " " + rs.getString("price"));
                }
                client.getOutputStream().write(shows.toString().getBytes());
                client.getOutputStream().flush();
            }else if(filters.get("descricao") != null && filters.get("tipo") != null && filters.get("data_hora") != null && filters.get("local") != null){
                rs = stmt.executeQuery("SELECT * FROM espetaculos WHERE descricao LIKE '%" + filters.get("descricao") + "%' AND tipo LIKE '%" + filters.get("tipo") + "%' AND data_hora LIKE '%" + filters.get("data_hora") + "%' AND local LIKE '%" + filters.get("local") + "%'");
                while (rs.next()) {
                    shows.add(rs.getString("id") + " " + rs.getString("name") + " " + rs.getString("date") + " " + rs.getString("price"));
                }
                client.getOutputStream().write(shows.toString().getBytes());
                client.getOutputStream().flush();
            }else if(filters.get("descricao") != null && filters.get("tipo") != null && filters.get("data_hora") != null){
                rs = stmt.executeQuery("SELECT * FROM espetaculos WHERE descricao LIKE '%" + filters.get("descricao") + "%' AND tipo LIKE '%" + filters.get("tipo") + "%' AND data_hora LIKE '%" + filters.get("data_hora") + "%'");
                while (rs.next()) {
                    shows.add(rs.getString("id") + " " + rs.getString("name") + " " + rs.getString("date") + " " + rs.getString("price"));
                }
                client.getOutputStream().write(shows.toString().getBytes());
                client.getOutputStream().flush();
            }else if(filters.get("descricao") != null && filters.get("tipo") != null){
                rs = stmt.executeQuery("SELECT * FROM espetaculos WHERE descricao LIKE '%" + filters.get("descricao") + "%' AND tipo LIKE '%" + filters.get("tipo") + "%'");
                while (rs.next()) {
                    shows.add(rs.getString("id") + " " + rs.getString("name") + " " + rs.getString("date") + " " + rs.getString("price"));
                }
                client.getOutputStream().write(shows.toString().getBytes());
                client.getOutputStream().flush();
            }else if(filters.get("descricao") != null){
                rs = stmt.executeQuery("SELECT * FROM espetaculos WHERE descricao LIKE '%" + filters.get("descricao") + "%'");
                while (rs.next()) {
                    shows.add(rs.getString("id") + " " + rs.getString("name") + " " + rs.getString("date") + " " + rs.getString("price"));
                }
                client.getOutputStream().write(shows.toString().getBytes());
                client.getOutputStream().flush();
            }else{
                rs = stmt.executeQuery("SELECT * FROM espetaculos");
                while (rs.next()) {
                    shows.add(rs.getString("id") + " " + rs.getString("name") + " " + rs.getString("date") + " " + rs.getString("price"));
                }
                client.getOutputStream().write(shows.toString().getBytes());
                client.getOutputStream().flush();
            }
        } catch (SQLException | IOException e) {
            client.getOutputStream().write("ERROR_OCCURED".getBytes());
            client.getOutputStream().flush();
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
                rs = stmt.executeQuery("SELECT * FROM reserva WHERE pago = 0 AND id_utilizador = " + clientID);
                while(rs.next()){
                    String payment = rs.getString("payment_id") + " " + rs.getString("payment_amount") + " " + rs.getString("payment_date") + " " + rs.getString("payment_confirmed");
                    payments.add(payment);
                    client.getOutputStream().write(payment.getBytes());
                    client.getOutputStream().flush();
                }
            }else if(whatToList.equals("PAYMENT_CONFIRMED")){
                rs = stmt.executeQuery("SELECT * FROM reserva WHERE pago = 1 AND id_utilizador = " + clientID);
                while(rs.next()){
                    String payment = rs.getString("payment_id") + " " + rs.getString("payment_amount") + " " + rs.getString("payment_date") + " " + rs.getString("payment_confirmed");
                    payments.add(payment);
                    client.getOutputStream().write(payment.getBytes());
                    client.getOutputStream().flush();
                }
            }
        }catch (IOException | SQLException e) {
            client.getOutputStream().write("ERROR_OCCURED".getBytes());
            client.getOutputStream().flush();
            System.out.println("[ ! ] An error has occurred while editing profile");
            System.out.println("      " + e.getMessage());
        }
    }

    private void edit_profile(String name, String username, String password) throws IOException {
        try{
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE username='"+username+"'");
            try{
                if(rs.next()){
                    String format = "UPDATE users SET name='%s', username = '%s', password='%s' WHERE username='%s', name, username, password, username";
                    stmt.executeUpdate(format);
                    client.getOutputStream().write("UPDATE_SUCCESSFUL".getBytes());
                    client.getOutputStream().flush();
                    server.incDbVersion(format);
                }else{
                    client.getOutputStream().write("USER_NOT_FOUND".getBytes());
                    client.getOutputStream().flush();
                }
            }catch (IOException | SQLException e){
                client.getOutputStream().write("ERROR_OCCURED".getBytes());
                client.getOutputStream().flush();
                System.out.println("[ ! ] An error has occurred while editing profile");
                System.out.println("      " + e.getMessage());
            }
        } catch (SQLException | IOException e) {
            client.getOutputStream().write("ERROR_OCCURED".getBytes());
            client.getOutputStream().flush();
            System.out.println("[ ! ] An error has occurred while editing profile");
            System.out.println("      " + e.getMessage());
        }
    }

    private void login(String username, String password) throws IOException {
        try {
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            String format = "SELECT username FROM utilizador";
            ResultSet rs = stmt.executeQuery(format);
            rs.next();
            if(username.equals("admin") && password.equals("admin")){
                client.getOutputStream().write("ADMIN_LOGIN_SUCCESSFUL".getBytes());
                client.getOutputStream().flush();
                server.incDbVersion(format);
            }else{
                if(rs.getString("username").equals(username)) {
                    format = "SELECT password FROM utilizador";
                    rs = stmt.executeQuery(format);
                    if(rs.getString("password").equals(password)) {
                        // TODO: Update database for the user "autenticado" field
                        client.getOutputStream().write("LOGIN_SUCCESSFUL".getBytes());
                        client.getOutputStream().flush();
                        stmt.executeQuery("UPDATE utilizador SET autenticado = 1 WHERE username = '" + username + "'");
                        server.incDbVersion(format);
                        ResultSet rs2 = stmt.executeQuery("SELECT id FROM utilizador WHERE username = '" + username + "'");
                        rs2.next();
                        clientID = rs2.getInt("id");
                    }
                }else{
                    client.getOutputStream().write("LOGIN_FAILED".getBytes());
                    client.getOutputStream().flush();
                }
            }
            rs.close();
            stmt.close();

        } catch (SQLException | IOException e) {
            client.getOutputStream().write("LOGIN_FAILED".getBytes());
            client.getOutputStream().flush();
            System.out.println("[ ! ] An error has occurred while editing profile");
            System.out.println("      " + e.getMessage());
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
            String format = "INSERT INTO utilizador (nome, username, password) VALUES ('" + nome + "', '" + username + "', '" + password + "')";
            stmt.executeUpdate(format);
            System.out.println("[ * ] User registered");
            client.getOutputStream().write("REGISTER_SUCCESSFUL".getBytes());
            client.getOutputStream().flush();
            stmt.executeQuery("UPDATE utilizador SET autenticado = 1 WHERE username = '" + username + "'");
            server.incDbVersion(format);
            ResultSet rs3 = stmt.executeQuery("SELECT id FROM utilizador WHERE username = '" + username + "'");
            rs3.next();
            clientID = rs3.getInt("id");
        } catch (IOException | SQLException e) {
            System.out.println("[ * ] Username already exists");
            client.getOutputStream().write("REGISTER_FAILED".getBytes());
            client.getOutputStream().flush();
            System.out.println("[ ! ] An error has occurred while editing profile");
            System.out.println("      " + e.getMessage());
        }
    }
}
