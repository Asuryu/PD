package Client.TextUserInterface;

import java.util.Scanner;

public class TUI {
    private Scanner sc;
    private boolean exit;

    public TUI() {
        sc = new Scanner(System.in);
        exit = false;
    }

    /*public String[] menuFiltros(){
        String option;
        String [] ft = new String[1];
        do {
            System.out.println("Filtros para pesquisa:");
            System.out.println("Nome");
            System.out.println("Localidade");
            System.out.println("Genero");
            System.out.println("Data");
            System.out.println("Nada");
            System.out.println("Nada");
            option = sc.nextLine();
            option.toLowerCase();
            ft[0] = option + ",";
            System.out.println(ft[0]);
            switch (option) {
                case "nome":
                    System.out.print("Indique ");
                    break;
                case "genero":
                    break;
                case "localidade":
                    break;
                case "data":
                    break;
                case "nada":
                    ft[0] = " ";
                    break;
            }
        }while( option.toLowerCase() == "sair");
        return ft;

    }*/
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
