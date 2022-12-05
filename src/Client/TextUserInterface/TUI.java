package Client.TextUserInterface;

import java.util.Scanner;

public class TUI {
    private final Scanner sc;

    public TUI() {
        sc = new Scanner(System.in);
    }

    public int logedMenuAdmin() {
        int option;
        System.out.println("\t[ 1 ] - Edit profile");
        System.out.println("\t[ 2 ] - List reservations to be paid");
        System.out.println("\t[ 3 ] - List paid reservations");
        System.out.println("\t[ 4 ] - Search and list shows");
        System.out.println("\t[ 5 ] - Select show");
        System.out.println("\t[ 6 ] - List seats and prices");
        System.out.println("\t[ 7 ] - Select seats");
        System.out.println("\t[ 8 ] - Cancel reservation");
        System.out.println("\t[ 9 ] - Pay reservation");
        System.out.println("\t[ 10 ] - Insert show");
        System.out.println("\t[ 11 ] - Remove show");
        System.out.println("\t[ 12 ] - Exit");
        System.out.println("\t[ 13 ] - Change show visibility");
        System.out.print("[ * ] OPTION: ");
        option = sc.nextInt();
        return option;

    }

    public int logedMenu() {
        int option;
        System.out.println("\t[ 1 ] - Edit profile");
        System.out.println("\t[ 2 ] - List reservations to be paid");
        System.out.println("\t[ 3 ] - List paid reservations");
        System.out.println("\t[ 4 ] - Search and list shows");
        System.out.println("\t[ 5 ] - Select show");
        System.out.println("\t[ 6 ] - List seats and prices");
        System.out.println("\t[ 7 ] - Select seats");
        System.out.println("\t[ 8 ] - Pay reservation");
        System.out.println("\t[ 9 ] - Cancel reservation");
        System.out.println("\t[ 10 ] - Exit");
        System.out.print("[ * ] OPTION: ");
            option = sc.nextInt();
            return option;
    }
    public int mainMenu() {//menu de login
        int option;
        System.out.println("\n\t[ * ] BOL-PD");
        System.out.println("[ 1 ] - Login");
        System.out.println("[ 2 ] - Register");
        System.out.println("[ 3 ] - Exit");
        do {
            System.out.print("[ * ] OPTION: ");
            option = sc.nextInt();
        } while (option < 1 || option > 4);
        return option;
    }
}
