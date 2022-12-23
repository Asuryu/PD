package pd.grupo5.restapi.models;

public class Utilizador {
    private int id;
    private String username;
    private String nome;
    private boolean admin;
    private boolean autenticado;

    public Utilizador(int id, String username, String nome, boolean admin, boolean autenticado) {
        this.id = id;
        this.username = username;
        this.nome = nome;
        this.admin = admin;
        this.autenticado = autenticado;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNome() {
        return nome;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isAutenticado() {
        return autenticado;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public void setAutenticado(boolean autenticado) {
        this.autenticado = autenticado;
    }
}
