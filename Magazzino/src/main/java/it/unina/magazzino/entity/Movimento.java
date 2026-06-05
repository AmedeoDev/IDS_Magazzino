package it.unina.magazzino.entity;

import java.util.Date;

public class Movimento {


    private int idMovimento;
    private int qtaProdotto;
    private Date data;
    private String tipoMovimento;
    private String idOperatore;
    private String idProdotto;


    private String nomeProdotto;


    // costrutture usato dal DAO per prelevare dal DB
    public Movimento(int idMovimento, int qtaProdotto, Date data, String tipo, String operatore, String prodotto, String nomeP){
        this.idMovimento = idMovimento;
        this.qtaProdotto = qtaProdotto;
        this.data = data;
        this.tipoMovimento = tipo;
        this.idOperatore = operatore;
        this.idProdotto = prodotto;
        this.nomeProdotto = nomeP;
    }

    public Movimento(int qtaProdotto, Date data, String tipo, String prodotto, String operatore){
        this.qtaProdotto = qtaProdotto;
        this.data = data;
        this.tipoMovimento = tipo;
        this.idProdotto = prodotto;
        this.idOperatore = operatore;
    }



    // get-set

    public int getIdMovimento() { return this.idMovimento; }
    public int getQtaProdotto() { return this.qtaProdotto; }
    public Date getData() { return this.data; }
    public String getTipoMovimento() { return this.tipoMovimento; }
    public String getIdOperatore() { return this.idOperatore; }
    public String getIdProdotto() { return this.idProdotto; }
    public String getNomeProdotto() { return this.nomeProdotto; }

    // i setter sono stati generati da INTELLIJ

    public void setIdMovimento(int idMovimento) {
        this.idMovimento = idMovimento;
    }

    public void setQtaProdotto(int qtaProdotto) {
        this.qtaProdotto = qtaProdotto;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public void setTipoMovimento(String tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public void setIdOperatore(String idOperatore) {
        this.idOperatore = idOperatore;
    }

    public void setIdProdotto(String idProdotto) {
        this.idProdotto = idProdotto;
    }

    public void setNomeProdotto(String nomeProdotto) {
        this.nomeProdotto = nomeProdotto;
    }
}
