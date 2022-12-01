package Client.Threads;

import Client.Cliente;
import Server.Comparators.HeartbeatComparatorLoad;
import Server.Heartbeat;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ThreadAtendeServidor extends Thread{
    private Cliente c;
    public ThreadAtendeServidor(Cliente c){
        this.c = c;
    }

    @Override
    public void run() {

    }
}
