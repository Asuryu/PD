package Client.TUI;

import Client.Cliente;

import java.util.Scanner;

public class TextUserInterface {

    private Scanner sc;
    public Cliente c;
    public TextUserInterface(){
        sc = new Scanner(System.in);
    }

    public int mainMenu() {//menu de login
        int option;
        System.out.println("\n\n1 - Login");
        System.out.println("2 - Registo");
        System.out.println("3 - Sair");
        do {
            System.out.print("Escolha uma opcao: ");
            option = sc.nextInt();
        } while(option < 1 || option > 3);
        return option;
    }
}
