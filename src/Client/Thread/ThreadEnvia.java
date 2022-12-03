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
                                    }while(opt2 < 1 || opt2 > 11);
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
                                            String[] sendingSTRSEARCH = new String[5];
                                            sendingSTRSEARCH[0] = "SHOWS_LIST_SEARCH";
                                            // Filters to search
                                            System.out.print("Nome do espetaculo: ");
                                            sendingSTRSEARCH[1] = "nome " + sc.nextLine();
                                            System.out.print("Tipo de espetaculo: ");
                                            sendingSTRSEARCH[2] = "tipo " + sc.nextLine();
                                            System.out.print("Data do espetaculo: ");
                                            sendingSTRSEARCH[3] = "data_hora " + sc.nextLine();
                                            System.out.print("Localidade do espetaculo: ");
                                            sendingSTRSEARCH[4] = "localidade " + sc.nextLine();
                                            oos.writeObject(sendingSTRSEARCH);
                                            oos.flush();
                                            String response6 = (String) ois.readObject();
                                            switch (response6) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("Unknown response");
                                                    break;
                                                default:
                                                    ArrayList response7 = (ArrayList) ois.readObject();
                                                    if (response7.size() == 0)
                                                        System.out.print("Nao foram encontrados espetaculos com os filtros indicados");
                                                    else {
                                                        System.out.print("Espetaculos encontrados:");
                                                        System.out.println(response7);
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
                                            placesAndPricesSTR[1] = "";
                                            do {
                                                System.out.print("Dos lugares disponiveis digite os que pretende:");
                                                placesAndPricesSTR[1] += sc.next();
                                            }while(placesAndPricesSTR[1] == "\n");
                                            System.out.println(placesAndPricesSTR[1]);
                                            oos.writeObject(placesAndPricesSTR);
                                            oos.flush();
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
                                            String[] pla = new String[2];
                                            pla[0] = "AVAILABLE_SEATS_AND_PRICE";
                                            pla[1] = " ";
                                            do {
                                                System.out.print("Dos lugares disponiveis digite os que pretende:");
                                                pla[1] += sc.nextLine();
                                            }while(pla[1] == "\n");
                                            System.out.println(pla[1]);
                                            break;
                                        case 8:
                                            System.out.print("******ADMIN******\n---- Remover reserva ----\n");
                                            String[] rem = new String[2];
                                            rem[0] = "REMOVE_RESERVATION";
                                            System.out.print("ID da reserva: ");
                                            rem[1] = sc.nextLine();
                                            oos.writeObject(rem);
                                            oos.flush();
                                            String r9 = (String) ois.readObject();
                                            switch (r9){
                                                case "ERROR_OCCURED":
                                                    System.out.println("Unknown response");
                                                    break;
                                                case "SHOW_REMOVED_SUCCESSFULLY":
                                                    System.out.println("Reserva removida com sucesso");
                                                    break;
                                                default:
                                                    System.out.println("Desconhecido");
                                                    break;
                                            }
                                            break;
                                        case 9:
                                            System.out.print("******ADMIN******\n---- Pagar ----\n");
                                            String[] paying = new String[2];
                                            paying[0] = "PAY";
                                            System.out.print("Pagar: ");
                                            paying[1] = sc.nextLine();
                                            oos.writeObject(paying);
                                            oos.flush();
                                            String r10 = (String) ois.readObject();
                                            switch (r10){
                                                case "ERROR_OCCURED":
                                                    System.out.println("Unknown response");
                                                    break;
                                                case "PAYMENT_SUCCESSFUL":
                                                    System.out.println("Pagamento feito com sucesso");
                                                    break;
                                                default:
                                                    System.out.println("Desconhecido");
                                                    break;
                                            }
                                            break;
                                        case 10:
                                            System.out.print("******ADMIN******\n---- Inserir espetaculos ----\n");
                                            String[] ins = new String[2];
                                            ins[0] = "INSERT";
                                            System.out.print("Nome do espetaculo: ");
                                            ins[1] = sc.nextLine();
                                            oos.writeObject(ins);
                                            oos.flush();
                                            String r11 = (String) ois.readObject();
                                            switch (r11){
                                                case "ERROR_OCCURED":
                                                    System.out.println("Unknown response");
                                                    break;
                                                case "SHOW_INSERTED_SUCCESSFULLY":
                                                    System.out.println("Inserido espetaculo");
                                                    break;
                                                default:
                                                    System.out.println("Desconhecido");
                                                    break;
                                            }
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
                                        int opt3;
                                        do {
                                            do {
                                                opt3 = tui.logedMenu();
                                            }while(opt3 < 1 || opt3 > 9);
                                            switch (opt3) {
                                                case 1:
                                                    String[] sendingSTREDITADMIN = new String[4];
                                                    System.out.print("******USER******\n---- ALTERAR DADOS ----\n");
                                                    sendingSTREDITADMIN[0] = "EDIT_PROFILE";
                                                    System.out.print("Name:");
                                                    sendingSTREDITADMIN[1] = sc.nextLine();
                                                    System.out.print("Username: ");
                                                    sendingSTREDITADMIN[2] = sc.nextLine();
                                                    System.out.print("Password: ");
                                                    sendingSTREDITADMIN[3] = sc.nextLine();
                                                    oos.writeObject(sendingSTREDITADMIN);
                                                    oos.flush();
                                                    String s1 = (String) ois.readObject();
                                                    switch (s1) {
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
                                                    System.out.print("******USER******\n---- Lista de pagamento ----\n");
                                                    String[] payments = new String[1];
                                                    payments[0] = "AWAITING_PAYMENT_CONFIRMATION";
                                                    oos.writeObject(payments);
                                                    oos.flush();
                                                    String s2 = (String) ois.readObject();
                                                    switch (s2){
                                                        case "ERROR_OCCURED":
                                                            System.out.println("Unknown response");
                                                            break;
                                                        default:
                                                            ArrayList d2 = (ArrayList) ois.readObject();
                                                            if (d2.size() == 0)
                                                                System.out.print("Tudo pago!!! Pessoa eficaz");
                                                            else {
                                                                System.out.print("Ainda tem por pagar:");
                                                                System.out.println(d2);
                                                            }
                                                            break;
                                                    }
                                                    break;
                                                case 3:
                                                    System.out.print("******USER******\n---- Reservas Pagas ----\n");
                                                    String[] paymentsP = new String[1];
                                                    paymentsP[0] = "PAYMENT_CONFIRMED";
                                                    oos.writeObject(paymentsP);
                                                    oos.flush();
                                                    String s3 = (String) ois.readObject();
                                                    switch (s3) {
                                                        case "ERROR_OCCURED":
                                                            System.out.println("Unknown response");
                                                            break;
                                                        default:
                                                            ArrayList d3 = (ArrayList) ois.readObject();
                                                            if (d3.size() == 0)
                                                                System.out.print("Historico de reservas:\nVazio");
                                                            else {
                                                                System.out.print("Historico de reservas pagas:");
                                                                System.out.println(d3);
                                                            }
                                                            break;
                                                    }

                                                    break;
                                                case 4:
                                                    //TODO
                                                    System.out.print("******ADMIN******\n---- Consultar e pesquisa de espetaculos ----\n");
                                                    String[] f = new String[2];
                                                    f[0] = "SHOWS_LIST_SEARCH";
                                                    f[1] = "nome Divertido, localidade Porto";
                                                    oos.writeObject(f);
                                                    oos.flush();
                                                    String s4 = (String) ois.readObject();
                                                    switch (s4) {
                                                        case "ERROR_OCCURED":
                                                            System.out.println("Unknown response");
                                                            break;
                                                        default:
                                                            ArrayList d4 = (ArrayList) ois.readObject();
                                                            if (d4.size() == 0)
                                                                System.out.print("Sem pesquisas:\nVazio");
                                                            else {
                                                                System.out.print("Lista:");
                                                                System.out.println(d4);
                                                            }
                                                            break;
                                                    }
                                                    break;
                                                case 5:
                                                    System.out.print("******USER******\n---- Selecionar Espetaculo ----\n");
                                                    String[] s = new String[1];
                                                    s[0] = "PAYMENT_CONFIRMED";
                                                    oos.writeObject(s);
                                                    oos.flush();
                                                    String s5 = (String) ois.readObject();
                                                    switch (s5) {
                                                        case "ERROR_OCCURED":
                                                            System.out.println("Unknown response");
                                                            break;
                                                        default:
                                                            ArrayList d5 = (ArrayList) ois.readObject();
                                                            if (d5.size() == 0)
                                                                System.out.print("Nao foram encontrados espetaculos:\n");
                                                            else {
                                                                System.out.println(d5);
                                                            }
                                                            break;
                                                    }
                                                    break;
                                                case 6:
                                                    //TODO
                                                    System.out.print("******USER******\n---- Lugares e Precos ----\n");
                                                    String[] placesAndPricesSTR = new String[2];
                                                    placesAndPricesSTR[0] = "AVAILABLE_SEATS_AND_PRICE";
                                                    placesAndPricesSTR[1] = "";
                                                    do {
                                                        System.out.print("Dos lugares disponiveis digite os que pretende:");
                                                        placesAndPricesSTR[1] += sc.next();
                                                    }while(placesAndPricesSTR[1] == "\n");
                                                    System.out.println(placesAndPricesSTR[1]);
                                                    oos.writeObject(placesAndPricesSTR);
                                                    oos.flush();
                                                    String s6 = (String) ois.readObject();
                                                    switch (s6) {
                                                        case "ERROR_OCCURED":
                                                            System.out.println("Unknown response");
                                                            break;
                                                        default:
                                                            ArrayList d6 = (ArrayList) ois.readObject();
                                                            if (d6.size() == 0)
                                                                System.out.print("Nao foram encontrados sitios:\n");
                                                            else {
                                                                System.out.println(d6);
                                                            }
                                                            break;
                                                    }
                                                    break;
                                                case 7:
                                                    //TODO
                                                    System.out.print("******USER******\n---- Selecionar sitios ----\n");
                                                    String[] pla = new String[2];
                                                    pla[0] = "AVAILABLE_SEATS_AND_PRICE";
                                                    pla[1] = " ";
                                                    do {
                                                        System.out.print("Dos lugares disponiveis digite os que pretende:");
                                                        pla[1] += sc.nextLine();
                                                    }while(pla[1] == "\n");
                                                    System.out.println(pla[1]);
                                                    break;
                                                case 8:
                                                    System.out.print("******USER******\n---- Pagar ----\n");
                                                    String[] paying = new String[2];
                                                    paying[0] = "PAY";
                                                    System.out.print("Pagar: ");
                                                    paying[1] = sc.nextLine();
                                                    oos.writeObject(paying);
                                                    oos.flush();
                                                    String s8 = (String) ois.readObject();
                                                    switch (s8){
                                                        case "ERROR_OCCURED":
                                                            System.out.println("Unknown response");
                                                            break;
                                                        case "PAYMENT_SUCCESSFUL":
                                                            System.out.println("Pagamento feito com sucesso");
                                                            break;
                                                        default:
                                                            System.out.println("Desconhecido");
                                                            break;
                                                    }
                                                    break;
                                                default:
                                                    exit = true;
                                                    break;
                                            }
                                        }while(!exit);

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
