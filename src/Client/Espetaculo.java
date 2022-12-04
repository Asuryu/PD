package Client;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

public class Espetaculo implements Serializable {

    @Serial
    private static final long serialVersionUID = 3L;

    private String designacao;
    private String tipo;
    private String data;
    private String hora;
    private int duracao;
    private String local;
    private String localidade;
    private String pais;
    private int classificacao_etaria;
    private HashMap<String, HashMap<Integer, Integer>> mapa_lugares;

    public Espetaculo(String designacao, String tipo, String data, String hora, int duracao, String local, String localidade, String pais, int classificacao_etaria, HashMap<String, HashMap<Integer, Integer>> mapa_lugares) {
        this.designacao = designacao;
        this.tipo = tipo;
        this.data = data;
        this.hora = hora;
        this.duracao = duracao;
        this.local = local;
        this.localidade = localidade;
        this.pais = pais;
        this.classificacao_etaria = classificacao_etaria;
        this.mapa_lugares = mapa_lugares;
    }

    public Espetaculo() {
        mapa_lugares = new HashMap<>();
    }

    public String getDesignacao() {
        return designacao;
    }

    public String getTipo() {
        return tipo;
    }

    public String getData() {
        return data;
    }

    public String getHora() {
        return hora;
    }

    public String getDateTime() {
        return data + " " + hora;
    }

    public int getDuracao() {
        return duracao;
    }

    public String getLocal() {
        return local;
    }

    public String getLocalidade() {
        return localidade;
    }

    public String getPais() {
        return pais;
    }

    public int getClassificacao_etaria() {
        return classificacao_etaria;
    }

    public HashMap<String, HashMap<Integer, Integer>> getMapa_lugares() {
        return mapa_lugares;
    }


    public void setDesignacao(String designacao) {
        this.designacao = designacao;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public void setClassificacao_etaria(int classificacao_etaria) {
        this.classificacao_etaria = classificacao_etaria;
    }

    public void setMapa_lugares(HashMap<String, HashMap<Integer, Integer>> mapa_lugares) {
        this.mapa_lugares = mapa_lugares;
    }

    public void addFila(String fila){
        mapa_lugares.put(fila, new HashMap<>());
    }

    public void addLugar(String fila, int lugar, int preco){
        mapa_lugares.get(fila).put(lugar, preco);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Espetaculo{");
        sb.append("designacao='").append(designacao).append('\'');
        sb.append(", tipo='").append(tipo).append('\'');
        sb.append(", data='").append(data).append('\'');
        sb.append(", hora='").append(hora).append('\'');
        sb.append(", duracao=").append(duracao);
        sb.append(", local='").append(local).append('\'');
        sb.append(", localidade='").append(localidade).append('\'');
        sb.append(", pais='").append(pais).append('\'');
        sb.append(", classificacao_etaria=").append(classificacao_etaria);
        sb.append(", mapa_lugares=").append(mapa_lugares);
        sb.append('}');
        return sb.toString();
    }
}
