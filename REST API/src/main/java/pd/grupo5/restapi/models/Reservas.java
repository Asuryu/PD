package pd.grupo5.restapi.models;

public class Reservas {
    private int id;
    private String data;
    private Boolean pago;
    private int id_utilizador;
    private int id_espetaculo;

    public Reservas(int id, String data, Boolean pago, int id_utilizador, int id_espetaculo) {
        this.id = id;
        this.data = data;
        this.pago = pago;
        this.id_utilizador = id_utilizador;
        this.id_espetaculo = id_espetaculo;
    }

    public int getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public Boolean getPago() {
        return pago;
    }

    public int getId_utilizador() {
        return id_utilizador;
    }

    public int getId_espetaculo() {
        return id_espetaculo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setPago(Boolean pago) {
        this.pago = pago;
    }

    public void setId_utilizador(int id_utilizador) {
        this.id_utilizador = id_utilizador;
    }

    public void setId_espetaculo(int id_espetaculo) {
        this.id_espetaculo = id_espetaculo;
    }
}
