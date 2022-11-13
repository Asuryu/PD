package Server.Threads;

import Server.Servidor;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ThreadLeitorDB extends Thread {
    protected final Servidor server;

    private int dbVersion;
    private int activeConnections;

    public ThreadLeitorDB(Servidor server, String SQL) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
            Statement stmt = server.dbConn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT version FROM database");
            rs.next();
            server.dbVersion = rs.getInt("version");
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
