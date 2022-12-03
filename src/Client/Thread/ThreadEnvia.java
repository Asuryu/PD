package Client.Thread;

import Client.Clientev2;
import Client.TextUserInterface.TUI;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

public class ThreadEnvia extends Thread {

    private final Clientev2 cliente;
    private final Socket s;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    private final TUI tui = new TUI();
    private Boolean exit = false;

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
                        switch (response) {
                            case "ADMIN_LOGIN_SUCCESSFUL":
                                System.out.println("Login successful ADMIN");
                                cliente.setLoggedIn(true);
                                cliente.setAdmin(true);
                                int opt2;
                                do {
                                    do {
                                        opt2 = tui.logedMenuAdmin();
                                    }while(opt2 < 1 || opt2 > 10 );
                                    switch (opt2) {
                                        case 1:
                                            String[] sendingSTREDITADMIN = new String[4];
                                            System.out.print("******ADMIN******\n---- ALTERAR DADOS ----\n");
                                            sendingSTREDITADMIN[0] = "EDIT_PROFILE";
                                            System.out.print("Name:");
                                            sendingSTREDITADMIN[1] = sc.nextLine();
                                            System.out.print("Username: ");
                                            sendingSTREDITADMIN[2] = sc.nextLine();
                                            System.out.print("Password: ");
                                            sendingSTREDITADMIN[3] = sc.nextLine();
                                            oos.writeObject(sendingSTREDITADMIN);
                                            oos.flush();
                                            String response3 = (String) ois.readObject();
                                            switch (response3) {
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
                                            break;
                                        case 2:
                                            System.out.print("******ADMIN******\n---- Lista de pagamento ----\n");
                                            String[] payments = new String[1];
                                            payments[0] = "AWAITING_PAYMENT_CONFIRMATION";
                                            oos.writeObject(payments);
                                            oos.flush();
                                            String r4 = (String) ois.readObject();
                                            switch (r4) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("Unknown response");
                                                    break;
                                                default:
                                                    ArrayList response4 = (ArrayList) ois.readObject();
                                                    if (response4.size() == 0)
                                                        System.out.print("Tudo pago!!! Pessoa eficaz");
                                                    else {
                                                        System.out.print("Ainda tem por pagar:");
                                                        System.out.println(response4);
                                                    }
                                                    break;
                                            }
                                            break;
                                        case 3:
                                            System.out.print("******ADMIN******\n---- Reservas Pagas ----\n");
                                            String[] paymentsP = new String[1];
                                            paymentsP[0] = "PAYMENT_CONFIRMED";
                                            oos.writeObject(paymentsP);
                                            oos.flush();
                                            String r5 = (String) ois.readObject();
                                            switch (r5) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("Unknown response");
                                                    break;
                                                default:
                                                    ArrayList response5 = (ArrayList) ois.readObject();
                                                    if (response5.size() == 0)
                                                        System.out.print("Historico de reservas:\nVazio");
                                                    else {
                                                        System.out.print("Historico de reservas pagas:");
                                                        System.out.println(response5);
                                                    }
                                                    break;
                                            }

                                            break;
                                        case 4:
                                            System.out.print("******ADMIN******\n---- Consultar e pesquisa de espetaculos ----\n");
                                           /* String[] f = new String[2];
                                            String[] fil = tui.menuFiltros();
                                            System.out.println(fil);*/
                                            String[] f = new String[2];
                                            f[0] = "SHOWS_LIST_SEARCH";
                                            f[1] = "nome Divertido, localidade Porto";
                                            oos.writeObject(f);
                                            oos.flush();
                                            String r6 = (String) ois.readObject();
                                            switch (r6) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("Unknown response");
                                                    break;
                                                default:
                                                    ArrayList response6 = (ArrayList) ois.readObject();
                                                    if (response6.size() == 0)
                                                        System.out.print("Sem pesquisas:\nVazio");
                                                    else {
                                                        System.out.print("Lista:");
                                                        System.out.println(response6);
                                                    }
                                                    break;
                                            }
                                            break;
                                        case 5:
                                            System.out.print("******ADMIN******\n---- Selecionar Espetaculo ----\n");
                                            String[] s = new String[1];
                                            s[0] = "PAYMENT_CONFIRMED";
                                            oos.writeObject(s);
                                            oos.flush();
                                            String r7 = (String) ois.readObject();
                                            switch (r7) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("Unknown response");
                                                    break;
                                                default:
                                                    ArrayList response7 = (ArrayList) ois.readObject();
                                                    if (response7.size() == 0)
                                                        System.out.print("Nao foram encontrados espetaculos:\n");
                                                    else {
                                                        System.out.println(response7);
                                                    }
                                                    break;
                                            }
                                            break;
                                        case 6:
                                            System.out.print("******ADMIN******\n---- Lugares e Precos ----\n");
                                            String[] placesAndPricesSTR = new String[2];
                                            placesAndPricesSTR[0] = "AVAILABLE_SEATS_AND_PRICE";
                                            placesAndPricesSTR[1] = " ";
                                            do {
                                                System.out.print("Dos lugares disponiveis digite os que pretende:");
                                                placesAndPricesSTR[1] += sc.nextLine();
                                            }while(placesAndPricesSTR[1] == "\n");
                                            System.out.println(placesAndPricesSTR[1]);
                                            String r8 = (String) ois.readObject();
                                            switch (r8) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("Unknown response");
                                                    break;
                                                default:
                                                    ArrayList response8 = (ArrayList) ois.readObject();
                                                    if (response8.size() == 0)
                                                        System.out.print("Nao foram encontrados sitios:\n");
                                                    else {
                                                        System.out.println(response8);
                                                    }
                                                    break;
                                            }
                                            break;
                                        case 7:
                                            System.out.print("******ADMIN******\n---- Selecionar sitios ----\n");
                                            break;
                                        case 8:
                                            System.out.print("******ADMIN******\n---- Validar reserva ----\n");
                                            break;
                                        case 9:
                                            System.out.print("******ADMIN******\n---- Remover reserva ----\n");
                                            break;
                                        case 10:
                                            System.out.print("******ADMIN******\n---- Pagar ----\n");
                                            break;
                                        default:
                                            exit = true;
                                            break;
                                    }
                                    }while(!exit);
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
        } catch (SocketException | EOFException e) {
            System.out.println("[ ! ] The server has closed the connection");
        } catch (Exception e) {
            System.out.println("[ ! ] An error has occurred while sending a message");
            e.printStackTrace();
            System.out.println("      " + e.getMessage());
        }
    }
}
