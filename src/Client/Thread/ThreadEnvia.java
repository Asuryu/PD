
        package Client.Thread;

        import Client.Clientev2;
        import Client.Espetaculo;
        import Client.TextUserInterface.TUI;

        import java.io.*;
        import java.net.Socket;
        import java.net.SocketException;
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.HashMap;
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
                        System.out.println("\n\t[ * ] LOGIN ");
                        System.out.print("[ · ] Username: ");
                        sendingSTRLOGIN[1] = sc.nextLine();
                        System.out.print("[ · ] Password: ");
                        sendingSTRLOGIN[2] = sc.nextLine();
                        oos.writeObject(sendingSTRLOGIN);
                        oos.flush();
                        String response = (String) ois.readObject();
                        switch (response) {
                            case "ADMIN_LOGIN_SUCCESSFUL":
                                cliente.setLoggedIn(true);
                                cliente.setAdmin(true);
                                int opt2;
                                do {
                                    do {
                                        opt2 = tui.logedMenuAdmin();
                                    }while(opt2 < 1 || opt2 > 13);
                                    switch (opt2) {
                                        case 1:
                                            String[] sendingSTREDITADMIN = new String[4];
                                            System.out.print("\n[ * ] ADMIN \n\t[ * ] EDIT PROFILE\n");
                                            sendingSTREDITADMIN[0] = "EDIT_PROFILE";
                                            System.out.print("[ · ] Name:");
                                            sendingSTREDITADMIN[1] = sc.nextLine();
                                            System.out.print("[ · ] Username: ");
                                            sendingSTREDITADMIN[2] = sc.nextLine();
                                            System.out.print("[ · ] Password: ");
                                            sendingSTREDITADMIN[3] = sc.nextLine();
                                            oos.writeObject(sendingSTREDITADMIN);
                                            oos.flush();
                                            String response3 = (String) ois.readObject();
                                            switch (response3) {
                                                case "UPDATE_SUCCESSFUL":
                                                    System.out.println("[ * ] Successfully updated profile\n");
                                                    break;
                                                case "USER_NOT_FOUND":
                                                    System.out.println("[ ! ] User not found\n");
                                                    break;
                                                default:
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                            }
                                            break;
                                        case 2:
                                            System.out.print("\n[ * ] ADMIN\n\t[ * ] PAYMENT LIST\n");
                                            String[] payments = new String[1];
                                            payments[0] = "AWAITING_PAYMENT_CONFIRMATION";
                                            oos.writeObject(payments);
                                            oos.flush();
                                            String r4 = (String) ois.readObject();
                                            System.out.println(r4);
                                            switch (r4) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                                default:
                                                    ArrayList<String> response4 = (ArrayList<String>) ois.readObject();
                                                    if (response4.size() == 0)
                                                        System.out.print("[ ! ] No payments to confirm\n");
                                                    else {
                                                        System.out.print("[ * ] Payments to confirm: ");
                                                        System.out.println(response4);
                                                        System.out.println("\n");
                                                    }
                                                    break;
                                            }
                                            break;
                                        case 3:
                                            System.out.print("\n[ * ]ADMIN\n\t[ * ] PAID RESERVATIONS\n");
                                            String[] paymentsP = new String[1];
                                            paymentsP[0] = "PAYMENT_CONFIRMED";
                                            oos.writeObject(paymentsP);
                                            oos.flush();
                                            String r5 = (String) ois.readObject();
                                            switch (r5) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                                default:
                                                    ArrayList response5 = (ArrayList) ois.readObject();
                                                    if (response5.size() == 0)
                                                        System.out.print("[ ! ] Empty paid reservations list\n");
                                                    else {
                                                        System.out.print("[ * ] Paid reservations: ");
                                                        System.out.println(response5);
                                                        System.out.println("\n");
                                                    }
                                                    break;
                                            }
                                            break;
                                        case 4:
                                            System.out.print("\n[ * ] ADMIN\n\t[ * ] SEARCH AND LIST SHOWS\n");
                                            String[] sendingSTRSEARCH = new String[5];
                                            sendingSTRSEARCH[0] = "SHOWS_LIST_SEARCH";
                                            // Filters to search
                                            System.out.print("[ · ] Show name: ");
                                            sendingSTRSEARCH[1] = "nome " + sc.nextLine();
                                            System.out.print("[ · ] Show type: ");
                                            sendingSTRSEARCH[2] = "tipo " + sc.nextLine();
                                            System.out.print("[ · ] Show date: ");
                                            sendingSTRSEARCH[3] = "data_hora " + sc.nextLine();
                                            System.out.print("[ · ] Show locale: ");
                                            sendingSTRSEARCH[4] = "localidade " + sc.nextLine();
                                            oos.writeObject(sendingSTRSEARCH);
                                            oos.flush();
                                            String response6 = (String) ois.readObject();
                                            System.out.println(response6);
                                            switch (response6) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                                default:
                                                    ArrayList<String> response7 = (ArrayList<String>) ois.readObject();
                                                    if (response7.size() == 0)
                                                        System.out.print("[ ! ] No shows found with the given filters\n");
                                                    else {
                                                        System.out.print("[ * ] Shows found: ");
                                                        for(String s : response7){
                                                            System.out.println(s);
                                                        }
                                                    }
                                                    break;
                                            }
                                            break;
                                        case 5:
                                            System.out.print("\n[ * ] ADMIN\n\t[ * ] SELECT SHOW \n");
                                            String[] s = new String[2];
                                            s[0] = "SELECT_SHOW";
                                            System.out.print("[ · ] Show ID: ");
                                            s[1] = sc.nextLine();
                                            oos.writeObject(s);
                                            oos.flush();
                                            String r7 = (String) ois.readObject();
                                            switch (r7) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                                case "SHOW_SELECTED":
                                                    System.out.println("[ * ] Show " + s[1] + " selected\n");
                                                    break;
                                                default:
                                                    break;
                                            }
                                            break;
                                        case 6:
                                            System.out.print("\n[ * ] ADMIN\n\t[ * ] SEATS AND PRICE \n");
                                            String[] placesAndPricesSTR = new String[10];
                                            placesAndPricesSTR[0] = "AVAILABLE_SEATS_AND_PRICE";
                                            System.out.print("[ · ] Show ID: ");
                                            placesAndPricesSTR[1] = sc.nextLine();
                                            oos.writeObject(placesAndPricesSTR);
                                            oos.flush();
                                            String r8 = (String) ois.readObject();
                                            switch (r8) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                                default:
                                                    ArrayList<String> response8 = (ArrayList<String>) ois.readObject();
                                                    if (response8.size() == 0)
                                                        System.out.print("[ * ] No seats found\n");
                                                    else {
                                                        System.out.print("[ * ] Seats found: ");
                                                        System.out.println(response8);
                                                        System.out.println("\n");
                                                    }
                                                    break;
                                            }
                                            break;
                                        case 7:
                                            System.out.print("\n[ * ] ADMIN\n\t[ * ] SELECT SEATS \n");
                                            String[] pla = new String[2];
                                            pla[0] = "select_seats";
                                            System.out.println("[ · ] Type the desired seats [Separate the seats with commas]: ");
                                            pla[1] = sc.nextLine();
                                            oos.writeObject(pla);
                                            oos.flush();
                                            String r9 = (String) ois.readObject();
                                            switch (r9) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                                default:
                                                    HashMap<String, ArrayList<String>> hm = (HashMap<String, ArrayList<String>>) ois.readObject();
                                                    System.out.println("[ * ] Reserved seats: " + hm.get("RESERVED"));
                                                    System.out.println("[ * ] Already reserved seats: " + hm.get("ALREADY_RESERVED"));
                                                    break;
                                            }
                                            break;
                                        case 8:
                                            System.out.print("\n[ * ] ADMIN\n\t[ * ] CANCEL RESERVATION \n");
                                            String[] rem = new String[2];
                                            rem[0] = "REMOVE_RESERVATION";
                                            System.out.print("[ · ] Reservation ID: ");
                                            rem[1] = sc.nextLine();
                                            oos.writeObject(rem);
                                            oos.flush();
                                            String r10 = (String) ois.readObject();
                                            switch (r10) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                                case "RESERVA_SUCCESSFULLY_REMOVED":
                                                    System.out.println("[ * ] Reservation removed successfully\n");
                                                    break;
                                                default:
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                            }
                                            break;
                                        case 9:
                                            System.out.print("\n[ * ] ADMIN\n\t[ * ] PAY \n");
                                            String[] paying = new String[2];
                                            paying[0] = "PAY";
                                            System.out.print("[ · ] Reservation ID: ");
                                            paying[1] = sc.nextLine();
                                            oos.writeObject(paying);
                                            oos.flush();
                                            String r11 = (String) ois.readObject();
                                            switch (r11) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                                case "PAYMENT_CONFIRMED":
                                                    System.out.println("[ * ] Payment successful\n");
                                                    break;
                                            }
                                            break;
                                        case 10:
                                            System.out.print("\n[ * ]ADMIN\n\t[ * ] INSERT SHOW \n");
                                            System.out.print("[ · ] Input the file name with the show info: ");
                                            String fileName = sc.nextLine();
                                            Espetaculo espetaculo = lerFicheiroEspetaculos(fileName);
                                            if(espetaculo == null) break;
                                            String[] insertShow = new String[1];
                                            insertShow[0] = "INSERT_SHOW";
                                            oos.writeObject(insertShow);
                                            oos.flush();
                                            String r12 = (String) ois.readObject();
                                            switch (r12) {
                                                case "SEND_SHOW_DATA":
                                                    oos.writeObject(espetaculo);
                                                    oos.flush();
                                                case "NO_SHOW_SELECTED":
                                                    System.out.println("[ ! ] No show selected\n");
                                                    break;
                                                case "SHOW_INSERTED_SUCCESSFULLY":
                                                    System.out.println("[ * ] Show inserted successfully\n");
                                                    break;
                                                default:
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                            }
                                            break;
                                        case 11:
                                            System.out.print("\n[ * ] ADMIN\n\t[ * ] REMOVE SHOW \n");
                                            String[] remov = new String[2];
                                            remov[0] = "REMOVE_SHOW";
                                            System.out.print("[ · ] Show ID: ");
                                            remov[1] = sc.nextLine();
                                            System.out.println(Arrays.toString(remov));
                                            oos.writeObject(remov);
                                            oos.flush();
                                            System.out.println("SENT");
                                            String r13 = (String) ois.readObject();
                                            System.out.println(r13);
                                            switch (r13) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("[ ! ] An error has occurred while inserting show\n");
                                                    break;
                                                case "SHOW_REMOVED_SUCCESSFULLY":
                                                    System.out.println("[ * ] Show removed successfully\n");
                                                    break;
                                                default:
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                            }
                                            break;
                                        case 12:
                                            String[] logout = new String[1];
                                            logout[0] = "LOGOUT";
                                            oos.writeObject(logout);
                                            oos.flush();
                                            exit = true;
                                            break;
                                        case 13:
                                            System.out.print("\n[ * ] ADMIN\n\t[ * ] CHANGE SHOW VISIBILITY\n");
                                            String[] payload = new String[2];
                                            payload[0] = "CHANGE_SHOW_VISIBILITY";
                                            System.out.print("[ · ] Show ID: ");
                                            payload[1] = sc.nextLine();
                                            oos.writeObject(payload);
                                            oos.flush();
                                            String r14 = (String) ois.readObject();
                                            switch (r14) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                                case "SHOW_NOT_FOUND":
                                                    System.out.println("[ ! ] The show with that ID was not found\n");
                                                    break;
                                                case "SHOW_HIDDEN":
                                                    System.out.println("[ * ] Show hidden\n");
                                                    break;
                                                case "SHOW_UNHIDDEN":
                                                    System.out.println("[ * ] Show unhidden\n");
                                                    break;
                                                default:
                                                    System.out.println("[ ! ] Unknown error\n");
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
                                System.out.println("[ ! ] Login failed\n");
                                break;
                            case "LOGIN_SUCCESSFUL":
                                int opt3;
                                do {
                                    do {
                                        opt3 = tui.logedMenu();
                                    }while(opt3 < 1 || opt3 > 11);
                                    switch (opt3) {
                                        case 1:
                                            String[] sendingSTREDITADMIN = new String[4];
                                            System.out.print("[ * ] USER\n\t[ * ] EDIT PROFILE\n");
                                            sendingSTREDITADMIN[0] = "EDIT_PROFILE";
                                            System.out.print("[ · ] Name:");
                                            sendingSTREDITADMIN[1] = sc.nextLine();
                                            System.out.print("[ · ] Username: ");
                                            sendingSTREDITADMIN[2] = sc.nextLine();
                                            System.out.print("[ · ] Password: ");
                                            sendingSTREDITADMIN[3] = sc.nextLine();
                                            oos.writeObject(sendingSTREDITADMIN);
                                            oos.flush();
                                            String response3 = (String) ois.readObject();
                                            switch (response3) {
                                                case "UPDATE_SUCCESSFUL":
                                                    System.out.println("[ * ] Successfully updated profile\n");
                                                    break;
                                                case "USER_NOT_FOUND":
                                                    System.out.println("[ ! ] User not found\n");
                                                    break;
                                                default:
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                            }
                                            break;
                                        case 2:
                                            System.out.print("\n[ * ] USER\n\t[ * ] PAYMENT LIST\n");
                                            String[] payments = new String[1];
                                            payments[0] = "AWAITING_PAYMENT_CONFIRMATION";
                                            oos.writeObject(payments);
                                            oos.flush();
                                            String r4 = (String) ois.readObject();
                                            System.out.println(r4);
                                            switch (r4) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                                default:
                                                    ArrayList<String> response4 = (ArrayList<String>) ois.readObject();
                                                    if (response4.size() == 0)
                                                        System.out.print("[ ! ] No payments to confirm\n");
                                                    else {
                                                        System.out.print("[ * ] Payments to confirm: ");
                                                        System.out.println(response4);
                                                        System.out.println("\n");
                                                    }
                                                    break;
                                            }
                                            break;
                                        case 3:
                                            System.out.print("\n[ * ] USER\n\t[ * ] PAID RESERVATIONS\n");
                                            String[] paymentsP = new String[1];
                                            paymentsP[0] = "PAYMENT_CONFIRMED";
                                            oos.writeObject(paymentsP);
                                            oos.flush();
                                            String r5 = (String) ois.readObject();
                                            switch (r5) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                                default:
                                                    ArrayList response5 = (ArrayList) ois.readObject();
                                                    if (response5.size() == 0)
                                                        System.out.print("[ ! ] Empty paid reservations list\n");
                                                    else {
                                                        System.out.print("[ * ] Paid reservations: ");
                                                        System.out.println(response5);
                                                        System.out.println("\n");
                                                    }
                                                    break;
                                            }
                                            break;
                                        case 4:
                                            System.out.print("\n[ * ] USER\n\t[ * ] SEARCH AND LIST SHOWS\n");
                                            String[] sendingSTRSEARCH = new String[5];
                                            sendingSTRSEARCH[0] = "SHOWS_LIST_SEARCH";
                                            // Filters to search
                                            System.out.print("[ · ] Show name: ");
                                            sendingSTRSEARCH[1] = "nome " + sc.nextLine();
                                            System.out.print("[ · ] Show type: ");
                                            sendingSTRSEARCH[2] = "tipo " + sc.nextLine();
                                            System.out.print("[ · ] Show date: ");
                                            sendingSTRSEARCH[3] = "data_hora " + sc.nextLine();
                                            System.out.print("[ · ] Show locale: ");
                                            sendingSTRSEARCH[4] = "localidade " + sc.nextLine();
                                            oos.writeObject(sendingSTRSEARCH);
                                            oos.flush();
                                            String response6 = (String) ois.readObject();
                                            System.out.println(response6);
                                            switch (response6) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                                default:
                                                    ArrayList<String> response7 = (ArrayList<String>) ois.readObject();
                                                    if (response7.size() == 0)
                                                        System.out.print("[ ! ] No shows found with the given filters\n");
                                                    else {
                                                        System.out.print("[ * ] Shows found: ");
                                                        for(String s : response7){
                                                            System.out.println(s);
                                                        }
                                                    }
                                                    break;
                                            }
                                            break;
                                        case 5:
                                            System.out.print("\n[ * ] USER\n\t[ * ] SELECT SHOW \n");
                                            String[] s = new String[2];
                                            s[0] = "SELECT_SHOW";
                                            System.out.print("[ · ] Show ID: ");
                                            s[1] = sc.nextLine();
                                            oos.writeObject(s);
                                            oos.flush();
                                            String r7 = (String) ois.readObject();
                                            switch (r7) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                                case "SHOW_SELECTED":
                                                    System.out.println("[ * ] Show " + s[1] + " selected\n");
                                                    break;
                                                default:
                                                    break;
                                            }
                                            break;
                                        case 6:
                                            System.out.print("\n[ * ] USER\n\t[ * ] SEATS AND PRICE \n");
                                            String[] placesAndPricesSTR = new String[10];
                                            placesAndPricesSTR[0] = "AVAILABLE_SEATS_AND_PRICE";
                                            System.out.print("[ · ] Show ID: ");
                                            placesAndPricesSTR[1] = sc.nextLine();
                                            oos.writeObject(placesAndPricesSTR);
                                            oos.flush();
                                            String r8 = (String) ois.readObject();
                                            switch (r8) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                                default:
                                                    ArrayList<String> response8 = (ArrayList<String>) ois.readObject();
                                                    if (response8.size() == 0)
                                                        System.out.print("[ * ] No seats found\n");
                                                    else {
                                                        System.out.print("[ * ] Seats found: ");
                                                        System.out.println(response8);
                                                        System.out.println("\n");
                                                    }
                                                    break;
                                            }
                                            break;
                                        case 7:
                                            System.out.print("\n[ * ] USER\n\t[ * ] SELECT SEATS \n");
                                            String[] pla = new String[2];
                                            pla[0] = "select_seats";
                                            System.out.println("[ · ] Type the desired seats [Separate the seats with commas]: ");
                                            pla[1] = sc.nextLine();
                                            oos.writeObject(pla);
                                            oos.flush();
                                            String r9 = (String) ois.readObject();
                                            switch (r9) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                                default:
                                                    HashMap<String, ArrayList<String>> hm = (HashMap<String, ArrayList<String>>) ois.readObject();
                                                    System.out.println("[ * ] Reserved seats: " + hm.get("RESERVED"));
                                                    System.out.println("[ * ] Already reserved seats: " + hm.get("ALREADY_RESERVED"));
                                                    break;
                                            }
                                            break;
                                        case 8:
                                            System.out.print("\n[ * ] USER\n\t[ * ] PAY\n");
                                            String[] paying = new String[2];
                                            paying[0] = "PAY";
                                            System.out.print("[ · ] Reservation ID: ");
                                            paying[1] = sc.nextLine();
                                            oos.writeObject(paying);
                                            oos.flush();
                                            String r11 = (String) ois.readObject();
                                            switch (r11) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                                case "PAYMENT_CONFIRMED":
                                                    System.out.println("[ * ] Payment successful\n");
                                                    break;
                                            }
                                            break;
                                        case 9:
                                            System.out.print("\n[ * ] USER\n\t[ * ] CANCEL RESERVATION\n");
                                            String[] rem = new String[2];
                                            rem[0] = "REMOVE_RESERVATION";
                                            System.out.print("[ · ] Reservation ID: ");
                                            rem[1] = sc.nextLine();
                                            oos.writeObject(rem);
                                            oos.flush();
                                            String r10 = (String) ois.readObject();
                                            switch (r10) {
                                                case "ERROR_OCCURED":
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                                case "RESERVA_SUCCESSFULLY_REMOVED":
                                                    System.out.println("[ * ] Reservation removed successfully\n");
                                                    break;
                                                default:
                                                    System.out.println("[ ! ] Unknown error\n");
                                                    break;
                                            }
                                            break;
                                        case 10:
                                            String[] logout = new String[1];
                                            logout[0] = "LOGOUT";
                                            oos.writeObject(logout);
                                            oos.flush();
                                            exit = true;
                                            break;
                                        default:
                                            exit = true;
                                            break;
                                    }
                                }while(!exit);

                                break;
                            default:
                                System.out.println("[ ! ] Unknown error\n");
                                break;
                        }
                        break;
                    case 2:
                        String[] sendingSTRREGISTER = new String[4];
                        sendingSTRREGISTER[0] = "REGISTER";
                        System.out.println("\n\t[ * ] REGISTER");
                        System.out.print("[ · ] Name: ");
                        sendingSTRREGISTER[1] = sc.nextLine();
                        System.out.print("[ · ] Username: ");
                        sendingSTRREGISTER[2] = sc.nextLine();
                        System.out.print("[ · ] Password: ");
                        sendingSTRREGISTER[3] = sc.nextLine();
                        oos.writeObject(sendingSTRREGISTER);
                        oos.flush();
                        String response2 = (String) ois.readObject();
                        switch (response2){
                            case "REGISTER_SUCCESSFUL":
                                System.out.println("[ * ] Register successful\n");
                                break;
                            case "REGISTER_FAILED":
                                System.out.println("[ ! ] Register failed\n");
                                break;
                            case "USER_ALREADY_EXISTS":
                                System.out.println("[ ! ] User already exists\n");
                                break;
                            default:
                                System.out.println("[ ! ] Unknown error\n");
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
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("[ ! ] An error has occurred while sending a message");
            e.printStackTrace();
            System.out.println("      " + e.getMessage());
        }
    }

    public static Espetaculo lerFicheiroEspetaculos(String filename) {
        File ficheiro = new File(filename);
        Espetaculo espetaculo = new Espetaculo();
        // read line by line
        try {
            BufferedReader br = new BufferedReader(new FileReader(ficheiro));
            String linha = br.readLine();
            int flag = 0;
            while (linha != null) {
                try {
                    String[] dados = linha.split(";");
                    for(int i = 0; i < dados.length; i++){
                        // remove every “ and ” from the string
                        dados[i] = dados[i].replaceAll("”", "");
                        dados[i] = dados[i].replaceAll("“", "");
                        dados[i] = dados[i].replaceAll("\"", "");
                    }
                    if(flag == 1){
                        espetaculo.addFila(dados[0]);
                        for(int i = 1; i < dados.length; i++){
                            String[] lugar_preco = dados[i].split(":");
                            espetaculo.addLugar(dados[0], Integer.parseInt(lugar_preco[0]), Integer.parseInt(lugar_preco[1]));
                        }
                    }
                    switch (dados[0]) {
                        case "Designação":
                            espetaculo.setDesignacao(dados[1]);
                            break;
                        case "Tipo":
                            espetaculo.setTipo(dados[1]);
                            break;
                        case "Data": {
                            StringBuilder data = new StringBuilder();
                            for (int i = 1; i < dados.length; i++) {
                                data.append(dados[i] + "/");
                                if (i == dados.length - 1) {
                                    data.deleteCharAt(data.length() - 1);
                                }
                            }
                            espetaculo.setData(data.toString());
                            break;
                        }
                        case "Hora": {
                            StringBuilder hora = new StringBuilder();
                            for (int i = 1; i < dados.length; i++) {
                                hora.append(dados[i] + ":");
                                if (i == dados.length - 1) {
                                    hora.deleteCharAt(hora.length() - 1);
                                }
                            }
                            espetaculo.setHora(hora.toString());
                            break;
                        }
                        case "Duração":
                            espetaculo.setDuracao(Integer.parseInt(dados[1]));
                            break;
                        case "Local":
                            espetaculo.setLocal(dados[1]);
                            break;
                        case "Localidade":
                            espetaculo.setLocalidade(dados[1]);
                            break;
                        case "País":
                            espetaculo.setPais(dados[1]);
                            break;
                        case "Classificação etária":
                            espetaculo.setClassificacao_etaria(Integer.parseInt(dados[1]));
                            break;
                        case "Fila":
                            flag = 1;
                            break;
                    }
                } catch (Exception e) {
                    continue;
                }
                linha = br.readLine();
            }
            return espetaculo;
        } catch (FileNotFoundException e) {
            System.out.println("[ ! ] File not found");
            return null;
        } catch (IOException e) {
            System.out.println("[ ! ] Error reading file");
            return null;
        }
    }

}
