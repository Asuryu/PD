package Server.Threads;

import Server.Servidor;

import java.util.Scanner;

/**
 * Classe que representa a thread responsável por receber comandos do administrador
 */
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
                case "EXIT":
                    for(Thread t : server.threads) t.interrupt();
                    break;
                default:
                    System.out.println("[ ! ] Command '" + command + "' not recognized");
                    break;
            }
        }
        System.out.println("[ - ] Exiting thread ThreadConsolaAdmin");
    }
}
