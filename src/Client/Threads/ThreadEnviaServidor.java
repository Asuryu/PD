package Client.Threads;

import Client.Cliente;
import Client.TUI.TextUserInterface;
import Server.Heartbeat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ThreadEnviaServidor extends Thread {
    private final Cliente c;
    private Boolean exit;
    private Socket soc;
    private TextUserInterface textUserInterface = new TextUserInterface();
    private Scanner scanner = new Scanner(System.in);
    private  ObjectOutputStream objectOutputStream;

    public ThreadEnviaServidor(Cliente c, Socket soc) {
        this.c = c;

        this.soc = soc;

    }

    @Override
    public void run() {
           try {
               objectOutputStream = new ObjectOutputStream(soc.getOutputStream());
               int opt = textUserInterface.mainMenu();
               //  ThreadAtendeServidor threadAtendeServidor = new ThreadAtendeServidor(c,soc);
               //threadAtendeServidor.start();
               do {
                   switch (opt) {
                       case 1:
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
                           synchronized (c.isLogged) {
                               if (!c.isLogged)
                                   break;
                           }
                           do {
                               int opt2 = textUserInterface.logedMenu();
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
                                       sendingSTREDIT[4] = scanner.nextLine();
                                       for (String i : sendingSTREDIT)
                                           System.out.println(i);
                                       objectOutputStream.writeObject(sendingSTREDIT);
                                       objectOutputStream.flush();
                                       synchronized (c.wasEdit) {
                                           if (!c.wasEdit)
                                               break;
                                       }
                                       break;
                                   case 2:
                                       objectOutputStream.writeObject("AWAITING_PAYMENT_CONFIRMATION");
                                       objectOutputStream.flush();
                                       synchronized (c.progress) {
                                           if (!c.progress)
                                               break;
                                       }
                                       break;
                                   case 3:
                                       objectOutputStream.writeObject("PAYMENT_CONFIRMED");
                                       objectOutputStream.flush();
                                       synchronized (c.progress) {
                                           if (!c.progress)
                                               break;
                                       }
                                       break;
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
                                       synchronized (c.progress) {
                                           if (!c.progress)
                                               break;
                                       }
                                       break;
                                   case 5:
                                       objectOutputStream.writeObject("SELECT_SHOW");
                                       objectOutputStream.flush();
                                       synchronized (c.progress) {
                                           if (!c.progress)
                                               break;
                                       }
                                       break;
                                   case 6:
                                       String[] sendingSTRSHOWS = new String[4];
                                       sendingSTRSHOWS[0] = "AVAILABLE_SEATS_AND_PRICE";
                                       objectOutputStream.writeObject(sendingSTRSHOWS);
                                       objectOutputStream.flush();
                                       synchronized (c.progress) {
                                           if (!c.progress)
                                               break;
                                       }
                                       break;
                                   case 7:
                                       String selectSeats[] = {"SELECT_SEATS"};
                                       objectOutputStream.writeObject(selectSeats);
                                       objectOutputStream.flush();
                                       synchronized (c.progress) {
                                           if (!c.progress)
                                               break;
                                       }
                                       break;
                                   case 8:
                                       objectOutputStream.writeObject("VALIDATE_RESERVATION");
                                       objectOutputStream.flush();
                                       synchronized (c.progress) {
                                           if (!c.progress)
                                               break;
                                       }
                                       break;
                                   case 9:
                                       objectOutputStream.writeObject("REMOVE_RESERVATION");
                                       objectOutputStream.flush();
                                       synchronized (c.progress) {
                                           if (!c.progress)
                                               break;
                                       }
                                       break;

                               }
                           } while (opt != 9);
                           break;
                       case 2:
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
                           synchronized (c.isReg) {
                               if (!c.isReg)
                                   break;
                           }
                           exit = false;
                           break;
                       case 3:
                           System.out.println("[ * ] Exiting the application...");
                           exit = true;
                           break;
                   }

               } while (!exit);
           }
            catch (Exception e) {
                System.out.println("[ ! ] An error has occurred while receiving a TCP connection");
                System.out.println("     " + e.getMessage());
            }
        }

    }
