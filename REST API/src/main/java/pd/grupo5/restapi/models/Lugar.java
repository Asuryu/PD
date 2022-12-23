package pd.grupo5.restapi.models;

public class Lugar {
    private int id;
    private String fila;
    private String assento;
    private float preco;
    private int id_espetaculo;

    public Lugar(int id, String fila, String assento, float preco, int id_espetaculo) {
        this.id = id;
        this.fila = fila;
        this.assento = assento;
        this.preco = preco;
        this.id_espetaculo = id_espetaculo;
    }

    public int getId() {
        return id;
    }

    public String getFila() {
        return fila;
    }

    public String getAssento() {
        return assento;
    }

    public float getPreco() {
        return preco;
    }

    public int getId_espetaculo() {
        return id_espetaculo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFila(String fila) {
        this.fila = fila;
    }

    public void setAssento(String assento) {
        this.assento = assento;
    }

    public void setPreco(float preco) {
        this.preco = preco;
    }

    public void setId_espetaculo(int id_espetaculo) {
        this.id_espetaculo = id_espetaculo;
    }
}
