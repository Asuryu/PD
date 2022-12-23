package pd.grupo5.restapi.database;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tomcat.jni.Local;
import pd.grupo5.restapi.models.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DatabaseManager {
    private final String JDBC_STRING = "jdbc:sqlite:C:\\Users\\tomas\\Documents\\GitHub\\PD\\REST API\\src\\main\\java\\database.db";
    private static Connection db;
    static int user_id = -1;

    public DatabaseManager() {
        try {
            db = DriverManager.getConnection(JDBC_STRING);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int checkToken(String token) {
        if (token == null)
            return 0;

        try (Statement statement = db.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT * FROM utilizador WHERE token = '" + token + "'");
            // check if token has expired (check token_expiration against current epoch time)
            if (rs.next()) {
                String token_expiration = rs.getString("token_expiration");
                long current_time = System.currentTimeMillis();
                if (Long.parseLong(token_expiration) < current_time) {
                    return -1;
                }
                if(rs.getInt("administrador") == 1){
                    return 2;
                }
                return 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public int loginUser(String username, String password, String token) {
        user_id = -1;
        try (Statement statement = db.createStatement()) {
            String query = "SELECT id FROM utilizador WHERE username = '" + username + "' AND password = '" + password + "'";
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                user_id = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

        if(user_id != -1) {
            try (Statement statement = db.createStatement()) {
                // Get epoch time plus 2 minutes
                long epoch = System.currentTimeMillis() + 120000;
                String query = "UPDATE utilizador SET token = '" + token + "', token_expiration = '"+ epoch + "' WHERE id = " + user_id;
                statement.executeUpdate(query);
                return user_id;
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;

    }

    private int getUserByToken(String token){
        try (Statement statement = db.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT id FROM utilizador WHERE token = '" + token + "'");
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public ArrayList<pd.grupo5.restapi.models.Reservas> getUnpaidReservations() {
        try (Statement statement = db.createStatement()) {
            String query = "SELECT * FROM reserva WHERE id_utilizador = " + user_id + " AND pago = 0";
            ResultSet rs = statement.executeQuery(query);
            ArrayList<pd.grupo5.restapi.models.Reservas> reservations = new ArrayList<>();
            while (rs.next()) {
                Espetaculos espetaculo = getEspetaculoById(rs.getInt("id_espetaculo"));
                ArrayList<Lugar> lugares = getLugaresByIdReserva(rs.getInt("id"));
                reservations.add(new Reservas(rs.getInt("id"), rs.getString("data_hora"), rs.getInt("pago") == 1, rs.getInt("id_utilizador"), rs.getInt("id_espetaculo"), espetaculo, lugares));
            }
            return reservations;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<pd.grupo5.restapi.models.Reservas> getPaidReservations() {
        try (Statement statement = db.createStatement()) {
            String query = "SELECT * FROM reserva WHERE id_utilizador = " + user_id + " AND pago = 1";
            ResultSet rs = statement.executeQuery(query);
            ArrayList<pd.grupo5.restapi.models.Reservas> reservations = new ArrayList<>();
            while (rs.next()) {
                Espetaculos espetaculo = getEspetaculoById(rs.getInt("id_espetaculo"));
                ArrayList<Lugar> lugares = getLugaresByIdReserva(rs.getInt("id"));
                reservations.add(new Reservas(rs.getInt("id"), rs.getString("data_hora"), rs.getInt("pago") == 1, rs.getInt("id_utilizador"), rs.getInt("id_espetaculo"), espetaculo, lugares));
            }
            return reservations;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ArrayList<Lugar> getLugaresByIdReserva(int id){
        try (Statement statement = db.createStatement()) {
            String query = "SELECT * FROM reserva_lugar WHERE id_reserva = " + id;
            ResultSet rs = statement.executeQuery(query);
            ArrayList<Lugar> lugares = new ArrayList<>();
            while (rs.next()) {
                lugares.add(getLugarById(rs.getInt("id_lugar")));
            }
            return lugares;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Lugar getLugarById(int id){
        try (Statement statement = db.createStatement()) {
            String query = "SELECT * FROM lugar WHERE id = " + id;
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                return new Lugar(rs.getInt("id"), rs.getString("fila"), rs.getString("assento"), rs.getFloat("preco"), rs.getInt("espetaculo_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private Espetaculos getEspetaculoById(int id){
        try (Statement statement = db.createStatement()) {
            String query = "SELECT * FROM espetaculo WHERE id = " + id;
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                return new Espetaculos(rs.getInt("id"), rs.getString("descricao"), rs.getString("tipo"), rs.getString("data_hora"), rs.getInt("duracao"), rs.getString("local"), rs.getString("localidade"), rs.getString("pais"), rs.getString("classificacao_etaria"), rs.getInt("visivel") == 1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public ArrayList<Espetaculos> getEspetaculos(String data_inicio, String data_fim){
        try (Statement statement = db.createStatement()) {
            String query = "SELECT * FROM espetaculo WHERE visivel = 1";
            ResultSet rs = statement.executeQuery(query);
            ArrayList<Espetaculos> espetaculos = new ArrayList<>();
            if(data_inicio != null && data_fim != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate start_date = LocalDate.parse(data_inicio, formatter);
                LocalDate end_date = LocalDate.parse(data_fim, formatter);
                while (rs.next()) {
                    String data_hora = rs.getString("data_hora");
                    formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate espetaculo_date = LocalDate.parse(data_hora.substring(0, data_hora.length() - 6), formatter);
                    if (espetaculo_date.isAfter(start_date) && espetaculo_date.isBefore(end_date)) {
                        espetaculos.add(new Espetaculos(rs.getInt("id"), rs.getString("descricao"), rs.getString("tipo"), rs.getString("data_hora"), rs.getInt("duracao"), rs.getString("local"), rs.getString("localidade"), rs.getString("pais"), rs.getString("classificacao_etaria"), rs.getInt("visivel") == 1));
                    }
                }
            } else if(data_inicio != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate start_date = LocalDate.parse(data_inicio, formatter);
                while (rs.next()) {
                    String data_hora = rs.getString("data_hora");
                    formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate espetaculo_date = LocalDate.parse(data_hora.substring(0, data_hora.length() - 6), formatter);
                    if (espetaculo_date.isAfter(start_date)) {
                        espetaculos.add(new Espetaculos(rs.getInt("id"), rs.getString("descricao"), rs.getString("tipo"), rs.getString("data_hora"), rs.getInt("duracao"), rs.getString("local"), rs.getString("localidade"), rs.getString("pais"), rs.getString("classificacao_etaria"), rs.getInt("visivel") == 1));
                    }
                }
            } else if(data_fim != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate end_date = LocalDate.parse(data_fim, formatter);
                while (rs.next()) {
                    String data_hora = rs.getString("data_hora");
                    formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate espetaculo_date = LocalDate.parse(data_hora.substring(0, data_hora.length() - 6), formatter);
                    if (espetaculo_date.isBefore(end_date)) {
                        espetaculos.add(new Espetaculos(rs.getInt("id"), rs.getString("descricao"), rs.getString("tipo"), rs.getString("data_hora"), rs.getInt("duracao"), rs.getString("local"), rs.getString("localidade"), rs.getString("pais"), rs.getString("classificacao_etaria"), rs.getInt("visivel") == 1));
                    }
                }
            } else {
                while (rs.next()) {
                    espetaculos.add(new Espetaculos(rs.getInt("id"), rs.getString("descricao"), rs.getString("tipo"), rs.getString("data_hora"), rs.getInt("duracao"), rs.getString("local"), rs.getString("localidade"), rs.getString("pais"), rs.getString("classificacao_etaria"), rs.getInt("visivel") == 1));
                }
            }
            return espetaculos;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Utilizador> getRegisteredUsers(){
        try (Statement statement = db.createStatement()) {
            String query = "SELECT * FROM utilizador";
            ResultSet rs = statement.executeQuery(query);
            ArrayList<Utilizador> users = new ArrayList<>();
            while (rs.next()) {
                users.add(new Utilizador(rs.getInt("id"), rs.getString("username"), rs.getString("nome"),rs.getInt("administrador") == 1, rs.getInt("autenticado") == 1));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean getUserByUsername(String username){
        try (Statement statement = db.createStatement()) {
            String query = "SELECT * FROM utilizador WHERE username = '" + username + "'";
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private boolean getUserByName(String name){
        try (Statement statement = db.createStatement()) {
            String query = "SELECT * FROM utilizador WHERE nome = '" + name + "'";
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public boolean addUser(String username, String nome, String password, boolean admin){
        if(!getUserByUsername(username) && !getUserByName(nome)) {
            try (Statement statement = db.createStatement()) {
                String query = "INSERT INTO utilizador (username, nome, password, administrador, autenticado) VALUES ('" + username + "', '" + nome + "', '" + password + "', " + (admin ? 1 : 0) + ", 0)";
                statement.executeUpdate(query);
                return true;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else return false;
    }

    public boolean deleteUser(int id){
        try (Statement statement = db.createStatement()) {
            String query = "SELECT * FROM utilizador WHERE id = " + id;
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) { // check if user exists
                if (rs.getInt("autenticado") == 1) { // check if user is connected
                    return false;
                } else {
                    query = "DELETE FROM utilizador WHERE id = " + id;
                    statement.executeUpdate(query);
                    return true;
                }
            } else return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void close() {
        if(db != null) {
            try {
                db.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
