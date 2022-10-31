package Server;

import java.io.IOException;
import java.net.ServerSocket;

public class ThreadLigacoesTCP extends Thread {

    public ThreadLigacoesTCP(){

    }

    @Override
    public void run(){
        try{
            ServerSocket s = new ServerSocket(0);
            System.out.println("[ * ] Listening for TCP connections at " + s.getInetAddress().getHostAddress() + ":" + s.getLocalPort());
        } catch(IOException e){
            System.out.println("[ ! ] An error has occurred while setting up TCP socket");
            e.printStackTrace();
        }
    }
}
