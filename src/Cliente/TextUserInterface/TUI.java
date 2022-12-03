package Cliente.TextUserInterface;

import Client.Cliente;

import java.util.Scanner;

public class TUI {
    private Scanner sc;
    public Cliente c;
    private boolean exit;

    public TUI() {
        sc = new Scanner(System.in);
        exit = false;
    }




    public int logedMenu() {
        int option;
        /*if (!c.isAdmin){
            System.out.println("1 - Editar dados dos prefil");
            System.out.println("2 - Consultar reservas ainda não pagas");
            System.out.println("3 - Consultar reservas pagas");
            System.out.println("4 - Consultar e pesquisa de espetaculos");
            System.out.println("5 - Selecionar espetaculo");
            System.out.println("6 - Ver lugar disponiveis e respectivos precos");
            System.out.println("7 - Selecionar lugares pretendidos");
            System.out.println("8 - Validar reserva");
            System.out.println("9 - Remover reserva");
            System.out.println("10 - Pagar");
            do {
                System.out.print("Escolha uma opcao: ");
                option = sc.nextInt();
            } while (option < 1 || option > 11);
        }else{*/
            System.out.println("1 - Editar dados dos prefil");
            System.out.println("2 - Consultar reservas ainda não pagas");
            System.out.println("3 - Consultar reservas pagas");
            System.out.println("4 - Consultar e pesquisa de espetaculos");
            System.out.println("5 - Selecionar espetaculo");
            System.out.println("6 - Ver lugar disponiveis e respectivos precos");
            System.out.println("7 - Selecionar lugares pretendidos");
            System.out.println("8 - Validar reserva");
            System.out.println("9 - Inserir espetaculo");
            System.out.println("10 - Pagar");
            System.out.println("11 - Eliminar espetaculo");

            do {
                System.out.print("Escolha uma opcao: ");
                option = sc.nextInt();
            } while (option < 1 || option > 12);


        return option;
    }

    public int mainMenu() {//menu de login
        int option;
        System.out.println("1 - Login");
        System.out.println("2 - Registo");
        System.out.println("3 - Sair");
        do {
            System.out.print("Escolha uma opcao: ");
            option = sc.nextInt();
        } while (option < 1 || option > 4);
        return option;
    }
}
