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
        System.out.println("1 - Editar dados dos prefil");
        System.out.println("2 - Consultar reservas ainda nao pagas");
        System.out.println("3 - Consultar reservas pagas");
        System.out.println("4 - Consultar e pesquisa de espetaculos");
        System.out.println("5 - Selecionar espetaculo");
        System.out.println("6 - Ver lugar disponiveis e respectivos precos");
        System.out.println("7 - Selecionar lugares pretendidos");
        System.out.println("8 - Remover reserva");
        System.out.println("9 - Pagar");
        System.out.println("10 - Inserir espetaculos");
        System.out.print("Escolha uma opcao: ");
        option = sc.nextInt();

        return option;

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
            System.out.println("8 - Pagar");
            System.out.print("Escolha uma opcao: ");
            option = sc.nextInt();
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
