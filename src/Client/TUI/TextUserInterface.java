package Client.TUI;

import Client.Cliente;

import java.util.Scanner;

public class TextUserInterface {

    private Scanner sc;
    public Cliente c;
    private boolean exit;

    public TextUserInterface() {
        sc = new Scanner(System.in);
        exit = false;
    }


    public int[] searchAndConsultMenu() {
        int options[] = new int[4];
        int i = 0;
        System.out.println("Pesquisar espetaculos por: ");
        System.out.println("1-Nome");
        System.out.println("2-Localidade");
        System.out.println("3-Genero");
        System.out.println("4-Data");
        System.out.print("Escolha as opçoes com o seguinte formato numero espaço numero: ");
        options[i] = sc.nextInt();
        i++;
        return options;
    }

    public int logedMenu() {
        int option;
        System.out.println("1 - Editar dados dos prefil");
        System.out.println("2 - Consultar reservas ainda não pagas");
        System.out.println("3 - Consultar reservas pagas");
        System.out.println("4 - Consultar e pesquisa de espetaculos");
        System.out.println("5 - Selecionar espetaculo");
        System.out.println("6 - Ver lugar disponiveis e respectivos precos");
        System.out.println("7 - Selecionar lugares pretendidos");
        System.out.println("8 - Validar reserva");
        System.out.println("9 - Remover reserva");
        do {
            System.out.print("Escolha uma opcao: ");
            option = sc.nextInt();
        } while (option < 1 || option > 10);
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
