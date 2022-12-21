package pd.grupo5.restapi.database;

import org.apache.commons.codec.digest.DigestUtils;

import java.sql.*;

public class DatabaseManager {
    private final String JDBC_STRING = "jdbc:sqlite:C:\\Users\\tomas\\Documents\\GitHub\\PD\\REST API\\src\\main\\java\\database.db";
    private static Connection db;

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
                return 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public boolean loginUser(String username, String password, String token) {
        int user_id = -1;
        try (Statement statement = db.createStatement()) {
            String query = "SELECT id FROM utilizador WHERE username = '" + username + "' AND password = '" + password + "'";
            ResultSet rs = statement.executeQuery(query);
            if (rs.next())
                user_id = rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if(user_id != -1) {
            try (Statement statement = db.createStatement()) {
                // Get epoch time plus 2 minutes
                long epoch = System.currentTimeMillis() + 120000;
                String query = "UPDATE utilizador SET token = '" + token + "', token_expiration = '"+ epoch + "' WHERE id = " + user_id;
                statement.executeUpdate(query);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;

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
