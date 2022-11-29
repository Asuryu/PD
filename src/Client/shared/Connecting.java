package Client.shared;

import java.io.Serializable;

import Client.shared.Answers;

public class Connecting implements Serializable,Answers {
    private String username = "11111111111111111111";
    private String password = "11111111111111111111";
    private String name = "111111111111111111111111";
    private boolean register = false;
    @Override
    public String toString() {
        return "PedidoDeLigar{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", registado=" + register +
                '}';
    }
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // @return true se é para fazer login, false para registar
    public boolean isRegister() {
        return register;
    }


    public void setAll(String username, String nome, String password, boolean registado) {
        if (username.getBytes().length > 64 || password.getBytes().length > 64 || nome.getBytes().length > 64)
            throw new IllegalArgumentException("Username and password sizes must be smaller than 62.");
        this.username = username;
        this.password = password;
        this.register = registado;
        this.name = nome;
    }

    public Connecting() {
    }
    public Connecting(String username, String nome, String password, boolean registado) {
        setAll(username, nome, password, registado);
    }

    public Connecting(String username, String password) {
        this.username = username;
        this.password = password;
        register = true;
    }
}
