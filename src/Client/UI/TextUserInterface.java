package Client.UI;

import Client.ClientObservavel;
import Client.shared.Connecting;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TextUserInterface {

    private Scanner s;
    private final boolean exit;
    private final ClientObservavel clientObservavel;
    private Connecting request;

    public TextUserInterface(ClientObservavel clientObservavel) {
        s = new Scanner(System.in);
        exit = false;
        this.clientObservavel = clientObservavel;
    }


    private Connecting registering() {
        System.out.print("Nome próprio: ");
        var nome = s.nextLine();
        var username = getInput("Username: ");
        var pass = getInput("Pass: ");
        return new Connecting(nome, username, pass, false);
    }
    private Connecting login() {
        var username = getInput("Username: ");
        var pass = getInput("Pass: ");
        return new Connecting(username, pass);
    }
    private int menuPrincipal() {//menu de login
        int op;
        System.out.println("\n\n1 - Login");
        System.out.println("2 - Registo");
        System.out.println("3 - Sair");
        do {
            System.out.print("Escolha uma opcao: ");
            op = s.nextInt();
        } while(op < 1 || op > 3);
        return op;
    }


    private int menuLogado() {//chama isto depois do login
        int op;
        System.out.println("1- Editar dados do perfil");
        System.out.println("2- Consultar reservas que aguardam pagamento");
        System.out.println("3- Consultar reservas pagas");
        System.out.println("4- Consultar espetaculos");
        System.out.println("5- Selecionar espetaculos");
        System.out.println("6- Selecionar lugar pretendidos");
        System.out.println("7- Ver lugar disponiveis (precos)");
        System.out.println("8- Subemeter um pedido de reserva");
        System.out.println("9- Eliminar reserva ainda nao paga");
        System.out.println("10- Efetuar pagamento");
        System.out.println("11- Sair");
        do {
            System.out.print("Escolha uma opcao: ");
            op = s.nextInt();
        } while(op < 1 || op > 9);
        return op;

    }


    public void UI() {
        do {
            int i = menuPrincipal();
            switch (i) {
                case 1:
                    Connecting log = login();
                    clientObservavel.getClientModel().registOrLogin(log);
                    do {
                        i = menuLogado();
                        switch (i){
                            case 1:

                                break;
                            case 2:
                                break;
                            case 3:
                                break;
                            case 4:
                                break;
                            case 5:
                                break;
                            case 6:
                                break;
                            case 7:
                                break;
                            case 8:
                                break;
                            case 9:
                                break;
                            case 10:
                                break;
                            case 11:
                                break;
                        }
                    }while(i!=11);
                    break;
                case 2:
                    Connecting regis = registering();
                    clientObservavel.getClientModel().registOrLogin(regis);
            }
        }while (!exit);
    }
    private String getInput(String info) {
        System.out.println(info + ": ");
        return s.nextLine();
    }
}


