package Server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ThreadAtendeClientes extends Thread {

    private final int SERVER_PORT;
    private final String DATABASE_PATH;

    public ThreadAtendeClientes(int SERVER_PORT, String DATABASE_PATH){
        this.SERVER_PORT = SERVER_PORT;
        this.DATABASE_PATH = DATABASE_PATH;
    }

    @Override
    public void run() {
        try{
            DatagramSocket ds = new DatagramSocket(SERVER_PORT);
            System.out.println("[ * ] Starting server at " + InetAddress.getLocalHost().getHostAddress() + ":" + SERVER_PORT);
        } catch(IOException e) {
            System.out.println("[ ! ] An error has occurred while starting the server");
            System.out.println("      " + e.getMessage());
            return;
        }

        while(!isInterrupted()){
            // Socket s = new Socket();
        }
        System.out.println("[ - ] Exiting thread ThreadAtendeClientes");
    }

}
