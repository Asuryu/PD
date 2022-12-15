package RMI;


import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Rmi {

    public static final Logger LOGGER = Logger.getLogger(Rmi.class.getName());
    private static FileHandler fileHandlerLogger;

    public static void main(String[] args) {
        prepareLogger();
        LOGGER.info("RMI Observer started.");

        if (args.length != 1) {
            LOGGER.log(Level.SEVERE, "Invalid number of arguments! Usage: <rmi service address>");
            return;
        }
        System.out.println("I m here");
        Scanner scanner = new Scanner(System.in);

        LOGGER.log(Level.INFO, "RMI service created and running.");

        int option;

            System.out.println("SERVICE RUNNING! ASYNCHRONOUS NOTIFICATIONS WILL BE PRINTED AUTOMATICALLY");
            System.out.println("OPTIONS:");
            System.out.println("\t1 -> Listar os servidores ativos");
            System.out.println("\t2 -> Terminar");
            System.out.println("\nResposta:");

            option = scanner.nextInt();

          switch (option){
              case 1:
                  System.out.println("Vamos la listar os servidores");
                  break;
              case 2:
                  System.out.println("Saindo");
                  break;
          }


    }

    private static void prepareLogger() {
        SimpleDateFormat format = new SimpleDateFormat("M_d_HHmmss");
        try {
            fileHandlerLogger = new FileHandler("rmi-observer-" + format.format(Calendar.getInstance().getTime()) + ".log");
        } catch (Exception e) {
            e.printStackTrace();
        }

        fileHandlerLogger.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(fileHandlerLogger);
    }
}