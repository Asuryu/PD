package Server.Threads;

import Server.Servidor;
import Server.TCPMessages;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ThreadTCP extends Thread {

    private final Servidor server;

    public ThreadTCP(Servidor server){
        this.server = server;
    }

   @Override
   public void run(){

        System.out.println("[ * ] Listening for TCP connections at " + server.s.getInetAddress().getHostAddress() + ":" + server.s.getLocalPort());

         try{
              while(!isInterrupted()){
                  Socket client = server.s.accept();
                  System.out.println("[ * ] Received TCP connection from " + client.getInetAddress().getHostAddress() + ":" + client.getPort());
                  ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                  ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                  TCPMessages request = (TCPMessages)in.readObject();
                  switch (request){
                      case GET_DATABASE:
                          out.writeObject(new File(server.DATABASES_PATH + server.DATABASE_NAME));
                          System.out.println("[ * ] Sent database to server " + client.getInetAddress().getHostAddress() + ":" + client.getPort());
                          break;
                  }
                  client.close();
              }
         } catch (Exception e) {
              System.out.println("[ ! ] An error has occurred while receiving a TCP connection");
              System.out.println("      " + e.getMessage());
         }
   }

   private File getDatabaseFile(String path){
         return new File(path);
   }

}
