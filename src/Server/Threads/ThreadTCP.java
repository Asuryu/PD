package Server.Threads;

import Server.Servidor;
import Server.TCPMessages;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Classe que representa a thread que recebe as ligações através de TCP
 */
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
                  TCPMessages request = (TCPMessages)in.readObject(); // Obtém a mensagem do cliente
                  switch (request) {
                      case GET_DATABASE -> {
                          System.out.println("[ * ] A server is requesting the database");
                          File file = new File(server.DATABASES_PATH + server.DATABASE_NAME);
                          FileInputStream fis = new FileInputStream(file);
                          // Enviar ficheiro aos poucos
                          int n;
                          byte[] fileRead = new byte[4000];
                          do {
                              n = fis.read(fileRead);
                              if (n == -1) break;
                              client.getOutputStream().write(fileRead);
                          } while (true);
                          System.out.println("[ * ] Sent database to server " + client.getInetAddress().getHostAddress() + ":" + client.getPort());
                      }
                  }
                  client.close();
              }
         } catch (Exception e) {
              System.out.println("[ ! ] An error has occurred while receiving a TCP connection");
              System.out.println("      " + e.getMessage());
         }
   }

}
