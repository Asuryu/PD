package Client.Thread;

import Client.Clientev2;
import Client.TextUserInterface.TUI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class ThreadEnvia extends Thread {

    private final Clientev2 cliente;
    private final Socket s;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    private final TUI tui = new TUI();

    public ThreadEnvia(Clientev2 cliente, Socket s) throws IOException {
        this.s = s;
        this.cliente = cliente;
        oos = new ObjectOutputStream(s.getOutputStream());
        ois = new ObjectInputStream(s.getInputStream());
    }

    @Override
    public void run() {
        try{
            while(!isInterrupted()){
                int opt = tui.mainMenu();
                Scanner sc = new Scanner(System.in);
                switch (opt){
                    case 1:
                        String[] sendingSTRLOGIN = new String[3];
                        sendingSTRLOGIN[0] = "LOGIN";
                        System.out.println("----- LOGIN -----");
                        System.out.print("Username: ");
                        sendingSTRLOGIN[1] = sc.nextLine();
                        System.out.print("Password: ");
                        sendingSTRLOGIN[2] = sc.nextLine();
                        oos.writeObject(sendingSTRLOGIN);
                        oos.flush();
                        String response = (String) ois.readObject();
                        switch (response){
                            case "ADMIN_LOGIN_SUCCESSFUL":
                                System.out.println("Login successful ADMIN");
                                cliente.setLoggedIn(true);
                                cliente.setAdmin(true);
                                int opt2 = tui.logedMenuAdmin();
                                switch (opt2){
                                    case 1:
                                        String[] sendingSTREDITADMIN = new String[4];
                                        System.out.print("******ADMIN******\n---- ALTERAR DADOS ----\n");
                                        sendingSTREDITADMIN[0] = "EDIT_PROFILE";
                                        System.out.print("Name:");
                                        sendingSTREDITADMIN[1] = sc.nextLine();;
                                        System.out.print("Username: ");
                                        sendingSTREDITADMIN[2] = sc.nextLine();
                                        System.out.print("Password: ");
                                        sendingSTREDITADMIN[3] = sc.nextLine();
                                        oos.writeObject(sendingSTREDITADMIN);
                                        oos.flush();
                                        String response3 = (String) ois.readObject();
                                        switch (response3){
                                            case "UPDATE_SUCCESSFUL":
                                                System.out.println("Edit profile successful");
                                                break;
                                            case "USER_NOT_FOUND":
                                                System.out.println("User not found");
                                                break;
                                            default:
                                                System.out.println("Unknown response");
                                                break;
                                        }
                                }
                                break;
                            case "LOGIN_FAILED":
                                System.out.println("Login failed");
                                break;
                            case "LOGIN_SUCCESSFUL":
                                System.out.println("Login successful");

                                break;
                            default:
                                System.out.println("Unknown response");
                                break;
                        }
                        break;
                    case 2:
                        String[] sendingSTRREGISTER = new String[4];
                        sendingSTRREGISTER[0] = "REGISTER";
                        System.out.println("----- REGISTER -----");
                        System.out.print("Name: ");
                        sendingSTRREGISTER[1] = sc.nextLine();
                        System.out.print("Username: ");
                        sendingSTRREGISTER[2] = sc.nextLine();
                        System.out.print("Password: ");
                        sendingSTRREGISTER[3] = sc.nextLine();
                        oos.writeObject(sendingSTRREGISTER);
                        oos.flush();
                        String response2 = (String) ois.readObject();
                        switch (response2){
                            case "REGISTER_SUCCESSFUL":
                                System.out.println("Register successful");
                                break;
                            case "REGISTER_FAILED":
                                System.out.println("Register failed");
                                break;
                            default:
                                System.out.println("Unknown response");
                                break;
                        }
                        break;
                    case 3:
                        s.close();
                        oos.close();
                        ois.close();
                        cliente.setLoggedIn(false);
                        System.exit(0);
                }
            }
        } catch (SocketException e) {
            System.out.println("[ ! ] The server has closed the connection");
        } catch (Exception e) {
            System.out.println("[ ! ] An error has occurred while sending a message");
            e.printStackTrace();
            System.out.println("      " + e.getMessage());
        }
    }
}
