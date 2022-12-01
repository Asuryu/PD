package Client.Threads;

import Client.Cliente;
import Server.Comparators.HeartbeatComparatorLoad;
import Server.Heartbeat;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class ThreadAtendeServidor extends Thread {
    private Cliente c;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;


    public ThreadAtendeServidor(Cliente c) {
        this.c = c;
    }

    @Override
    public void run() {
        try {
            objectOutputStream = new ObjectOutputStream(c.socket.getOutputStream());
            objectInputStream = new ObjectInputStream(c.socket.getInputStream());
            ArrayList<String> p = (ArrayList<String>)objectInputStream.readObject();
            ArrayList<String> pP = (ArrayList<String>)objectInputStream.readObject();
            ArrayList<String> cS = (ArrayList<String>)objectInputStream.readObject();
            ArrayList<String> sS = (ArrayList<String>)objectInputStream.readObject();
            ArrayList<String> sp = (ArrayList<String>)objectInputStream.readObject();
            Integer seatsNumber[] = (Integer[]) objectInputStream.readObject();
            HashMap<String, String> filters = new HashMap<>();
            String request = (String) objectInputStream.readObject();
            String[] arrayRequest = request.split(" ");
            switch (arrayRequest[0].toUpperCase()) {
                case "REGISTER_SUCCESSFUL" -> register();
                case "USER_ALREADY_EXISTS" -> registerFailed();
                case "ADMIN_LOGIN_SUCCESSFUL" -> sucessAdminLogin();
                case "LOGIN_SUCCESSFUL" -> sucessLogin();
                case "LOGIN_FAILED" -> loginFailed();

                case "UPDATE_SUCCESSFUL" -> wasEditSucess();
                case "USER_NOT_FOUND" -> userNotFound();
                case "ERROR_OCCURED" -> erro();

                case "AWAITING_PAYEMENT" -> awaitingPayementList(p);
                case "PAYEMENT_CONFIRMED" -> payementConfirmedList(pP);

                case "SHOWS_LIST_SEARCH" -> showListSearch(cS);

                case "SELECT_SHOW" -> seeInfoAboutShow(sS);

                case "AVAILABLE_SEATS_AND_PRICE" -> availableSeatsAndPrice(sp);
//ultimo
                case "SEAT_RESERVATION_SUCCESSFUL" -> selectSeatSucess();
                case "SEAT_ALREADY_RESERVED" -> selectSeatAlreadyReserved();

                case "RESERVA_SUCCESSFULLY_REMOVED" -> reserveSuccessfullyRemoved();
                case "RESERVA_LUGAR_SUCCESSFULLY_REMOVED" ->  reservePlaceSuccessfullyRemoved();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void register() {
        System.out.println("Foi registado com sucesso");
        synchronized (c.isReg) {
            c.isReg = true;
        }
    }
    private void registerFailed() {
        System.out.println("O utilizador insirido já existe");
        synchronized (c.isReg) {
            c.isReg = false;
        }
    }
    private void sucessAdminLogin() {
        System.out.println("Login do administrador com sucesso");
        synchronized (c.isLogged) {
            c.isLogged = true;
        }
    }
    private void sucessLogin() {
        System.out.println("Login do utilizador com sucesso");
        synchronized (c.isLogged) {
            c.isLogged = true;
        }
    }
    private void loginFailed() {
        System.out.println("Login falhou");
        synchronized (c.isLogged) {
            c.isLogged = false;
        }
    }

    private void wasEditSucess() {
        System.out.println("Os seus dados foram editados com sucesso");
        synchronized (c.wasEdit) {
            if (!c.wasEdit)
                c.wasEdit = true;
        }
    }
    private void userNotFound() {
        System.out.println("O utilizador nao foi encontrado na base de dados");
        synchronized (c.wasEdit) {
            if (!c.wasEdit)
                c.wasEdit = false;
        }
    }
    private void erro() {
        System.out.println("Ocorreu um erro");
        synchronized (c.wasEdit) {
            if (!c.wasEdit)
                c.wasEdit = false;
        }
        synchronized (c.progress) {
            if (!c.progress)
                c.progress = false;
        }

    }

    private void awaitingPayementList(ArrayList<String> payements) {
        if (payements.size() == 0) {
            System.out.println("Nao tem pagamentos para efetuar");
        } else if (payements.size() >= 1) {
            for (int i = 0; i < payements.size(); i++)
                System.out.println(payements.get(i));
        }
        synchronized (c.progress) {
            if (!c.progress)
                c.progress = true;
        }
    }
    private void payementConfirmedList(ArrayList<String> pp) {
        if (pp.size() == 0) {
            System.out.println("Nao tem historico de pagamentos");

        } else if (pp.size() >= 1) {
            for (int i = 0; i < pp.size(); i++)
                System.out.println(pp.get(i));
        }
        synchronized (c.progress) {
            if (!c.progress)
                c.progress = true;
        }
    }

    private void showListSearch(ArrayList<String>shows) {
        if (shows.size() == 0) {
            System.out.println("Nao existem espetaculos");

        } else if (shows.size() >= 1) {
            for (int i = 0; i < shows.size(); i++)
                System.out.println(shows.get(i));
        }
        synchronized (c.progress) {
            if (!c.progress)
                c.progress = true;
        }
    }

    private void seeInfoAboutShow(ArrayList<String> sShow) {
        if (sShow.size() == 0) {
            System.out.println("Nao existe informação sobre este espetaculos");

        } else if (sShow.size() >= 1) {
            for (int i = 0; i < sShow.size(); i++)
                System.out.println(sShow.get(i));
        }
        synchronized (c.progress) {
            if (!c.progress)
                c.progress = true;
        }
    }

    private void availableSeatsAndPrice(ArrayList<String> seatsAprice) {
        if (seatsAprice.size() == 0) {
            System.out.println("Listar sitios");

        } else if (seatsAprice.size() >= 1) {
            for (int i = 0; i < seatsAprice.size(); i++)
                System.out.println(seatsAprice.get(i));
        }
        synchronized (c.progress) {
            if (!c.progress)
                c.progress = true;
        }
    }

    private void selectSeatSucess() {
    }

    private void selectSeatAlreadyReserved() {
        System.out.println("O lugar ja esta reservado");
        synchronized (c.progress) {
            c.progress = false;
        }
    }

    private void reserveSuccessfullyRemoved() {
        System.out.println("Reserva removida com sucesso");
        synchronized (c.progress) {
            c.progress = true;
        }
    }

    private void reservePlaceSuccessfullyRemoved(){
        System.out.println("Lugar reservado removido com sucesso");
        synchronized (c.progress) {
            c.progress = false;
        }
    }
}
