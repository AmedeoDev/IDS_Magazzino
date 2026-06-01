package it.unina.magazzino.entity;

import java.math.BigDecimal;

public class Prodotto {

    private final String id;
    private String categoria;
    private String nome;
    private String descrizione;
    private int qtaDisponibile;
    private Integer sogliaMinima;


    public Prodotto(String id, String categoria, String nome, String descrizione, int qtaDisponibile, Integer sogliaMinima){

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
    }

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
}
