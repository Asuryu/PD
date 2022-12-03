package Cliente.Thread;

import Cliente.Client;
import Cliente.TextUserInterface.TUI;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SendsDataToServerThread extends Thread {
    private final Socket socket;
    private final Client c;
    private final TUI tui = new TUI();
    private final Scanner scanner = new Scanner(System.in);
    private Boolean mustContinue;

    public SendsDataToServerThread(Client c,Socket socs){
        this.c = c;
        this.socket = socs;
    }
    @Override
    public void run() {
        mustContinue = false;
        try{
            do{
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            int opt = tui.mainMenu();
            switch (opt) {
                case 1 -> {
                    System.out.println(opt);
                    String[] sendingSTRLOGIN = new String[3];
                    System.out.println("Vai efetuar o login");
                    sendingSTRLOGIN[0] = "LOGIN";
                    System.out.println("Username: ");
                    sendingSTRLOGIN[1] = scanner.nextLine();
                    System.out.println("Password: ");
                    sendingSTRLOGIN[2] = scanner.nextLine();
                    for (String i : sendingSTRLOGIN)
                        System.out.println(i);
                    objectOutputStream.writeObject(sendingSTRLOGIN);
                    objectOutputStream.flush();
                   /* synchronized (c.isLogged) {
                        if (!c.isLogged) {
                            System.out.println("Tente novamente");
                            mustContinue = false;
                            break;
                        }
                    }*/
                    mustContinue=true;
                    if(mustContinue==true){

                            int opt2 = tui.logedMenu();
                            System.out.println(opt2);
                            switch (opt2) {
                                case 1:
                                    System.out.println("Vai editar dados do seu prefil");
                                    String[] sendingSTREDIT = new String[4];
                                    sendingSTREDIT[0] = "EDIT_PROFILE";
                                    System.out.println("Nome: ");
                                    sendingSTREDIT[1] = scanner.nextLine();
                                    System.out.println("Username: ");
                                    sendingSTREDIT[2] = scanner.nextLine();
                                    System.out.println("Password: ");
                                    sendingSTREDIT[3] = scanner.nextLine();
                                    for (String i : sendingSTREDIT)
                                        System.out.println(i);
                                    objectOutputStream.writeObject(sendingSTREDIT);
                                    objectOutputStream.flush();

                                case 2:
                                    objectOutputStream.writeObject("AWAITING_PAYMENT_CONFIRMATION");
                                    objectOutputStream.flush();
                                case 3:
                                    objectOutputStream.writeObject("PAYMENT_CONFIRMED");
                                    objectOutputStream.flush();
                                case 4:
                                    String[] sendingSTRSEARCH = new String[4];
                                    sendingSTRSEARCH[0] = "SHOWS_LIST_SEARCH";
                                    int opts[] = new int[4];
                                    int i = 0;
                                    System.out.println("Pesquisar espetaculos por: ");
                                    System.out.println("1-Nome");
                                    System.out.println("2-Localidade");
                                    System.out.println("3-Genero");
                                    System.out.println("4-Data");
                                    do {
                                        System.out.print("Escolha as opçoes com o seguinte formato: numero espaço numero: ");
                                        opts[i] = scanner.nextInt();
                                        i++;
                                    } while (i <= 4 || i == 3 || i == 2 || i == 1);
                                    int l = opts.length;
                                    int k = 0;
                                    do {
                                        switch (opts[k]) {
                                            case 1:
                                                sendingSTRSEARCH[k + 1] = "nome";
                                                break;
                                            case 2:
                                                sendingSTRSEARCH[k + 1] = "localidade";
                                                break;
                                            case 3:
                                                sendingSTRSEARCH[k + 1] = "genero";
                                                break;
                                            case 4:
                                                sendingSTRSEARCH[k + 1] = "data";
                                                break;
                                        }
                                        k++;
                                    } while (k != l);
                                    for (String sss : sendingSTRSEARCH)
                                        System.out.println(sss);
                                    objectOutputStream.writeObject(sendingSTRSEARCH);
                                    objectOutputStream.flush();

                                case 5:
                                    objectOutputStream.writeObject("SELECT_SHOW");
                                    objectOutputStream.flush();

                                case 6:
                                    String[] sendingSTRSHOWS = new String[4];
                                    sendingSTRSHOWS[0] = "AVAILABLE_SEATS_AND_PRICE";
                                    objectOutputStream.writeObject(sendingSTRSHOWS);
                                    objectOutputStream.flush();

                                case 7:
                                    String selectSeats[] = {"SELECT_SEATS"};
                                    objectOutputStream.writeObject(selectSeats);
                                    objectOutputStream.flush();

                                case 8:
                                    objectOutputStream.writeObject("VALIDATE_RESERVATION");
                                    objectOutputStream.flush();

                                case 9:
                                    objectOutputStream.writeObject("REMOVE_RESERVATION");
                                    objectOutputStream.flush();

                            }

                    }
                }
                case 2 -> {
                    System.out.println("Vai efetuar o registo");
                    String[] sendingSTRREG = new String[4];
                    sendingSTRREG[0] = "REGISTER";
                    System.out.println("Nome: ");
                    sendingSTRREG[1] = scanner.nextLine();
                    System.out.println("Username: ");
                    sendingSTRREG[2] = scanner.nextLine();
                    System.out.println("Password: ");
                    sendingSTRREG[3] = scanner.nextLine();
                    for (String i : sendingSTRREG)
                        System.out.println(i);
                    objectOutputStream.writeObject(sendingSTRREG);
                    objectOutputStream.flush();
                    System.out.println(opt);
                }
                case 3 -> {
                    System.out.println("[ * ] Exiting the application...");
                    System.out.println(opt);
                }
                default -> throw new IllegalStateException("Unexpected value: " + opt);
            }}while(!mustContinue);
        }catch(Exception e){
            System.out.println("[ ! ] An error has occurred while receiving a TCP connection");
            System.out.println("     " + e.getMessage());
            e.printStackTrace();
        }
    }

}
