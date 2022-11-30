package Client.Threads;

import Client.Cliente;
import Server.Comparators.HeartbeatComparatorLoad;
import Server.Heartbeat;

import java.io.*;


public class ThreadAtendeServidor extends Thread{
    private Cliente c;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    public ThreadAtendeServidor(Cliente c){
        this.c = c;
    }

    @Override
    public void run() {
            try{
                objectOutputStream = new ObjectOutputStream(c.socket.getOutputStream());
                objectInputStream = new ObjectInputStream(c.socket.getInputStream());
                String request = (String)objectInputStream.readObject();
                String[] arrayRequest = request.split(" ");
                switch (arrayRequest[0].toUpperCase()) {
                    case "REGISTER_SUCCESSFUL"-> register();
                    case "USER_ALREADY_EXISTS" ->registerFailed();
                }
    }catch(Exception e){
                e.printStackTrace();
            }
    }
    private void register(){
        System.out.println("Foi registado com sucesso");
    }
    private void registerFailed(){
        System.out.println("O USER já existe");
    }
}
