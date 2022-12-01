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
            ArrayList<String> payements = new ArrayList<>();
            ArrayList<String> payementsP = new ArrayList<>();
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
                case "AWAITING_PAYEMENT" -> awaitingPayementList(payements);
                case "PAYEMENT_CONFIRMED" -> payementConfirmedList(payementsP);
                case "SHOWS_LIST_SEARCH" -> showListSearch();
                case "SELECT_SHOW" -> seeInfoAboutShow();
                case "AVAILABLE_SEATS_AND_PRICE" -> availableSeatsAndPrice();
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
        System.out.println("Login do admin com sucesso");
        synchronized (c.isLogged) {
            c.isLogged = true;
        }
    }

    private void sucessLogin() {
        System.out.println("Login do user com sucesso");
        synchronized (c.isLogged) {
            c.isLogged = true;
        }
    }

    private void loginFailed() {
        System.out.println("Login failed");
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
        System.out.println("O user nao foi encontrado na base de dados");
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

    private void awaitingPayementList(ArrayList p) {
        if (p.size() == 0) {
            System.out.println("Nao tem pagamentos para efetuar");
            synchronized (c.progress) {
                if (c.progress == false)
                    c.progress = true;
            }
        } else if (p.size() >= 1) {
            for (int i = 0; i < p.size(); i++)
                System.out.println(p.get(i));
        }
    }

    private void payementConfirmedList(ArrayList p) {
        if (p.size() == 0) {
            System.out.println("Nao tem historico de pagamentos");
            synchronized (c.progress) {
                if (c.progress == false)
                    c.progress = true;
            }
        } else if (p.size() >= 1) {
            for (int i = 0; i < p.size(); i++)
                System.out.println(p.get(i));
        }
    }

    private void showListSearch() {
    }

    private void seeInfoAboutShow() {
    }

    private void availableSeatsAndPrice() {
    }

    private void selectSeatSucess() {
    }

    private void selectSeatAlreadyReserved() {
    }

    private void reserveSuccessfullyRemoved() {
    }

    private void reservePlaceSuccessfullyRemoved(){}
}
