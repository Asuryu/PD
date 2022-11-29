package Client.shared;

public class User {
    private final int id;
    private final String username;
    private final String nome;


    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNome() {
        return nome;
    }

    public User(int id, String username, String nome) {
        this.id = id;
        this.username = username;
        this.nome = nome;
    }
}
