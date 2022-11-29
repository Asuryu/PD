package Client.shared;

import java.io.*;
import java.time.Instant;
import java.util.Objects;

public class Utils {
    public static byte[] objectToBytes(Object object) {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutput oo;
        try {
            oo = new ObjectOutputStream(bStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            oo.writeObject(object);
            oo.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            oo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] resposta = bStream.toByteArray();
        try {
            bStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resposta;
    }

    public static Object bytesToObject(byte[] bytes) {
        ObjectInputStream iStream = null;
        try {
            iStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Object object = null;
        try {
            object = iStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        try {
            iStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return object;
    }
    public static class Consts {
        // tamanho maximo pedido pelo enunciado para os envios de tcp(em bytes)

        public static final int MAX_SIZE_PER_PACKET = 5 * 1024;

        //nome do servidor principal
        public static final String SERVER_ADDRESS = "localhost";

        // Porta e endereço do servidor par coneção de default para o client se ligar ao servidor
        public static int UDP_CLIENT_REQUEST_PORT = 5555;

        //Variavel para um modo de debug
        public static boolean DEBUG = true;

    }

}
