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
    private Scanner scanner =  new Scanner(System.in);
    public ThreadEnviaServidor(Cliente c) {
        this.c = c;
    }
    public ThreadEnviaServidor(Cliente c, ArrayList<Heartbeat> servers) {
        this.c = c;
        servers = servers;
    }

    @Override
    public void run() {
        for(Heartbeat heartbeat : servers){
            try{
                Socket socket = new Socket("10.65.132.193",heartbeat.getPort());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                //  String log [] = {"REGISTER","tania","tania","123"};
                int opt = textUserInterface.mainMenu();
                switch (opt){
                    case 1:
                        System.out.println("Vai efetuar o login");
                        String log[] = {"LOGIN"};
                        System.out.println("Username: ");
                        log[1] = scanner.nextLine();
                        System.out.println("Password: ");
                        log[2] = scanner.nextLine();
                        objectOutputStream.writeObject(log);
                        objectOutputStream.flush();
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
            }catch (Exception e){
                System.out.println("[ ! ] An error has occurred while receiving a TCP connection");
                e.printStackTrace();
            }
        }

    }
}