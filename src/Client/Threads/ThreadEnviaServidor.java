package Client.Threads;

import Client.Cliente;
import Client.TUI.TextUserInterface;
import Server.Heartbeat;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ThreadEnviaServidor extends Thread {
    private final Cliente c;
    private ArrayList<Heartbeat> servers;
    private TextUserInterface textUserInterface;
    private Scanner scanner = new Scanner(System.in);

    public ThreadEnviaServidor(Cliente c) {
        this.c = c;
    }

    public ThreadEnviaServidor(Cliente c, ArrayList<Heartbeat> servers) {
        this.c = c;
        servers = servers;
    }

    @Override
    public void run() {
        for (Heartbeat heartbeat : servers) {
            try {
                Socket socket = new Socket("10.65.132.193", heartbeat.getPort());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                int opt = textUserInterface.mainMenu();
                switch (opt) {
                    case 1:
                        System.out.println("Vai efetuar o login");
                        String log[] = {"LOGIN"};
                        System.out.println("Username: ");
                        log[1] = scanner.nextLine();
                        System.out.println("Password: ");
                        log[2] = scanner.nextLine();
                        objectOutputStream.writeObject(log);
                        objectOutputStream.flush();
                        synchronized (c.isLogged) {
                            if (!c.isLogged) break;
                        }
                        do {
                            int opt2 = textUserInterface.logedMenu();
                            switch (opt2) {
                                case 1:
                                    System.out.println("Vai editar dados do seu prefil");
                                    String editProfileName[] = {"EDIT_PROFILE"};
                                    System.out.println("Mome: ");
                                    editProfileName[1] = scanner.nextLine();
                                    System.out.println("Username: ");
                                    editProfileName[1] = scanner.nextLine();
                                    System.out.println("Password: ");
                                    editProfileName[2] = scanner.nextLine();
                                    objectOutputStream.writeObject(editProfileName);
                                    objectOutputStream.flush();
                                    break;
                                case 2:
                                    objectOutputStream.writeObject("AWAITING_PAYMENT_CONFIRMATION");
                                    objectOutputStream.flush();
                                    break;
                                case 3:
                                    objectOutputStream.writeObject("PAYMENT_CONFIRMED");
                                    objectOutputStream.flush();
                                    break;
                                case 4:
                                    String searchAndConsult[] = {"SHOWS_LIST_SEARCH"};
                                    int opts[] = textUserInterface.searchAndConsultMenu();
                                    int l = opts.length;
                                    int i = 0;
                                    int ii = 1;
                                    do {
                                        switch (opts[i]) {
                                            case 1:
                                                searchAndConsult[ii] = "nome";
                                                break;
                                            case 2:
                                                searchAndConsult[ii] = "localidade";
                                                break;
                                            case 3:
                                                searchAndConsult[ii] = "genero";
                                                break;
                                            case 4:
                                                searchAndConsult[ii] = "data";
                                                break;
                                        }
                                        i++;
                                        ii++;
                                    } while (i != l);
                                    objectOutputStream.writeObject(searchAndConsult);
                                    objectOutputStream.flush();
                                    break;
                                case 5:
                                    objectOutputStream.writeObject("SELECT_SHOW");
                                    objectOutputStream.flush();
                                    break;
                                case 6:
                                    String seatsAndPrices[] = {"AVAILABLE_SEATS_AND_PRICE"};
                                    objectOutputStream.writeObject(seatsAndPrices);
                                    objectOutputStream.flush();
                                    break;
                                case 7:
                                    String selectSeats[] = {"SELECT_SEATS"};
                                    objectOutputStream.writeObject(selectSeats);
                                    objectOutputStream.flush();
                                    break;
                                case 8:
                                    objectOutputStream.writeObject("VALIDATE_RESERVATION");
                                    objectOutputStream.flush();
                                    break;
                                case 9:
                                    objectOutputStream.writeObject("REMOVE_RESERVATION");
                                    objectOutputStream.flush();
                                    break;

                            }
                        } while (opt != 9);
                        break;
                    case 2:
                        System.out.println("Vai efetuar o registo");
                        String reg[] = {"REGISTER"};
                        System.out.println("Nome: ");
                        reg[1] = scanner.nextLine();
                        System.out.println("Username: ");
                        reg[2] = scanner.nextLine();
                        System.out.println("Password: ");
                        reg[3] = scanner.nextLine();
                        objectOutputStream.writeObject(reg);
                        objectOutputStream.flush();
                        break;
                    case 3:
                        System.out.println("Saindo do sistema: ");
                        return;
                }
            } catch (Exception e) {
                System.out.println("[ ! ] An error has occurred while receiving a TCP connection");
                e.printStackTrace();
            }
        }

    }
}