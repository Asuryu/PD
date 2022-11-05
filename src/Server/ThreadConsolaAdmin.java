package Server;

import java.util.Scanner;

public class ThreadConsolaAdmin extends Thread {

    private final Server server;

    public ThreadConsolaAdmin(Server server){
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
