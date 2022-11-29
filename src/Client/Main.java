package Client;

import Server.Heartbeat;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        System.out.println("A comecar o cliente....");
        if (args.length != 2) {
            System.err.println("[ERROR]Sintaxe: <lb address> <lb port>");
            return;
        }
        try{
            new Cliente(args[0],Integer.parseInt(args[1]));
        }catch (Exception e) {
            System.out.println("[ ! ] An error has occurred while setting up the client");
            System.out.println("      " + e.getMessage());
            e.printStackTrace();
        }

    }
}