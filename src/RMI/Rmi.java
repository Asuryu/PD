package RMI;


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

      //  Scanner sysReader = new Scanner(System.in);
        System.out.println("I m here");
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