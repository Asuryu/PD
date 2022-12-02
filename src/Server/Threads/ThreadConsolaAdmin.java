package Server;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class ThreadConsolaAdmin extends Thread {

    private final Servidor server;

    public ThreadConsolaAdmin(Servidor server){
        this.server = server;
    }

    @Override
    public void run(){
        Scanner sc = new Scanner(System.in);
        while(!isInterrupted()){
            System.out.print("admin@PD ~ % ");
            String command = sc.nextLine();
            switch(command.toUpperCase()){
                case "EXIT" -> exit();
                case "INSERT" -> {
                    // register new user
                    try {
                        server.dbConn = DriverManager.getConnection(server.JDBC_STRING);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Statement stmt = null;
                    try {
                        stmt = server.dbConn.createStatement();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Inserting new user...");
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    String format = "INSERT INTO utilizador (nome, username, password) VALUES ('" + nome + "', '" + username + "', '" + password + "')";
                    try {
                        stmt.executeUpdate(format);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.out.println("User inserted successfully!");
                    server.incDbVersion(format);
                }
                default -> {
                    System.out.println("[ ! ] Command '" + command + "' not recognized");
                    break;
                }
            }
        }
        System.out.println("[ - ] Exiting thread ThreadConsolaAdmin");
    }

    private void exit(){
        this.interrupt();
        synchronized (server.threads) {
            for(Thread t : server.threads) t.interrupt();
        }
    }
}