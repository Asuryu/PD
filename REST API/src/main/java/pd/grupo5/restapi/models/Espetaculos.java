package pd.grupo5.restapi.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Espetaculos {
    private int id;
    private String descricao;
    private String tipo;
    private String data_hora;
    private int duracao;
    private String local;
    private String localidade;
    private String pais;
    private String classificacao_etaria;
    private boolean visivel;

    public Espetaculos(int id, String descricao, String tipo, String data_hora, int duracao, String local, String localidade, String pais, String classificacao_etaria, boolean visivel) {
        this.id = id;
        this.descricao = descricao;
        this.tipo = tipo;
        this.data_hora = data_hora;
        this.duracao = duracao;
        this.local = local;
        this.localidade = localidade;
        this.pais = pais;
        this.classificacao_etaria = classificacao_etaria;
        this.visivel = visivel;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public String getData_hora() {
        return data_hora;
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

    public String getClassificacao_etaria() {
        return classificacao_etaria;
    }

    public boolean isVisivel() {
        return visivel;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setData_hora(String data_hora) {
        this.data_hora = data_hora;
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

    public void setClassificacao_etaria(String classificacao_etaria) {
        this.classificacao_etaria = classificacao_etaria;
    }

    public void setVisivel(boolean visivel) {
        this.visivel = visivel;
    }

    public String getDataFim() {
        // add data_hora + duracao
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(data_hora, formatter);
        LocalDateTime dateTimeFim = dateTime.plusMinutes(duracao);
        System.out.println(dateTimeFim.format(formatter));
        return dateTimeFim.format(formatter);
    }

}
