package Server.Threads;

import Server.Heartbeat;
import Server.Servidor;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
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
                  synchronized (server.activeConnections) {
                      server.activeConnections.add(client);
                  }
                  ThreadCliente threadCliente = new ThreadCliente(server, client);
                  threadCliente.start();

                  // send heartbeat to multicast
                  ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                  ObjectOutputStream out = new ObjectOutputStream(bOut);
                  server.dbVersion = server.getDbVersion();
                  Heartbeat hb = new Heartbeat(server.TCP_IP, server.TCP_PORT, server.dbVersion, server.activeConnections.size(), server.isAvailable);
                  out.writeObject(hb);
                  out.flush();
                  DatagramPacket dp = new DatagramPacket(bOut.toByteArray(), bOut.size(), server.ipGroup, server.MULTICAST_PORT);
                  server.ms.send(dp);

                  // server.sendOnlineServers(); // Send list of online servers to the all clients

              }
         } catch (Exception e) {
              System.out.println("[ ! ] An error has occurred while receiving a TCP connection");
              System.out.println("      " + e.getMessage());
         }
   }
}
