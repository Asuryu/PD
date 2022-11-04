package Server;

import java.util.ArrayList;
import java.util.Scanner;

public class ThreadConsolaAdmin extends Thread {

    private final ArrayList<Heartbeat> onlineServers;
    private final ArrayList<Thread> threads;

    public ThreadConsolaAdmin(ArrayList<Heartbeat> onlineServers, ArrayList<Thread> threads){
        this.onlineServers = onlineServers;
        this.threads = threads;
    }

    @Override
    public void run(){
        Scanner sc = new Scanner(System.in);
        while(!isInterrupted()){
            System.out.print("admin@PD ~ % ");
            String command = sc.nextLine();
            switch(command.toUpperCase()){
                case "EXIT":
                    for(Thread t : threads) t.interrupt();
                    break;
                default:
                    System.out.println("[ ! ] Command '" + command + "' not recognized");
                    break;
            }
        }
        System.out.println("[ - ] Exiting thread ThreadConsolaAdmin");
    }
}
