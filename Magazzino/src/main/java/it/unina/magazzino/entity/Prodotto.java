package it.unina.magazzino.entity;

import java.math.BigDecimal;

public class Prodotto {

    private final String id;
    private String categoria;
    private String nome;
    private String descrizione;
    private String IdPos;
    private String IdUtenteResponsabile;
    private int qtaDisponibile;
    private Integer sogliaMinima;


    public Prodotto(String id, String categoria, String nome, String descrizione, int qtaDisponibile, Integer sogliaMinima, String IdPos, String IdUtenteResponsabile){

        if (qtaDisponibile < 0){
            throw new IllegalArgumentException("La quantità non può essere minore di 0!");
        }

        if(sogliaMinima != null && sogliaMinima < 0){
            throw new IllegalArgumentException("La soglia minima non può essere minore di 0!");
        }

        this.id = id;
        this.categoria = categoria;
        this.nome = nome;
        this.descrizione = descrizione;
        this.qtaDisponibile = qtaDisponibile;
        this.sogliaMinima = sogliaMinima;
        this.IdPos = IdPos;
        this.IdUtenteResponsabile = IdUtenteResponsabile;
    }

    public String getID(){ return this.id; }

    public String getCategoria() { return this.categoria; }
    public String getNome() { return this.nome; }
    public String getDescrizione() { return this.descrizione; }
    public int getQtaDisponibile() { return this.qtaDisponibile; }
    public String getIdPos() { return this.IdPos; }
    public String getIdUtenteResponsabile() { return this.IdUtenteResponsabile; }

    public Integer getSogliaMinima(){
        return this.sogliaMinima;
    }

    public void setSogliaMinima(Integer sogliaMinima){
        if(sogliaMinima != null && sogliaMinima < 0){
            throw new IllegalArgumentException("La soglia non può essere negativa");
        }
        this.sogliaMinima = sogliaMinima;
    }

    public boolean isSottoScorta(){
        if(this.sogliaMinima == null){
            return false;
        }
        return this.qtaDisponibile <= this.sogliaMinima;
    }

    public void carica(int qta){
        if(qta <= 0){
            throw new IllegalArgumentException("La quantità deve essere maggiore di zero!");
        }

        this.qtaDisponibile += qta;
    }

    public void scarica(int qta){
        if(qta <= 0){
            throw new IllegalArgumentException("La quantità deve essere maggiore di zero!");
        }

        this.qtaDisponibile -= qta;
    }

    public void setCategoria(String categoria){
        this.categoria = categoria;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public void setDescrizione(String descrizione){
        this.descrizione = descrizione;
    }


    public boolean isEmpty(){
        return this.qtaDisponibile == 0;
    }

    // override
    @Override
    public String toString(){
        String alertScorta = isSottoScorta() ? "[!!!!] SOTTO SCORTA" : "";
        return "[" + id + "]" + nome + " - " + categoria + " • Qta. disp: " + qtaDisponibile + alertScorta;
    }
}
