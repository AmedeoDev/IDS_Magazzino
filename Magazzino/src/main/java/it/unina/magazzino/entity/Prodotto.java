package it.unina.magazzino.entity;

import java.math.BigDecimal;

/**
 * Rappresenta un prodotto presente nel magazzino
 * Implementa la logica di gestione delle scorte e della soglia minima
 * */

public class Prodotto {

    private final String id;
    private String categoria;
    private String nome;
    private String descrizione;
    private String IdPos;
    private String IdUtenteResponsabile;
    private int qtaDisponibile;
    private Integer sogliaMinima;


    /**
     * Costruisce un nuovo prodotto con i suoi attributi completi
     * @param id identificativo univico di ogni prodotto
     * @param categoria categoria merceologica del prodotto
     * @param nome nome del prodotto
     * @param descrizione descrizione dettagliata del prodotto
     * @param qtaDisponibile quantità attualmente disponibile del prodotto
     * @param IdPos codice della posizione in magazzino
     * @param IdUtenteResponsabile codice univoco del responsabile designato per il prodotto
     * @throws IllegalArgumentException se la quantità o la soglia sono negative
     * */

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

    /**
     * Verifica se la quantità attuale del prodotto è inferiore alla soglia minima impostata (se presente)
     * @return  true se la quantità disponibile è sotto scorta
     *          false se non lo è/se non è definita una soglia minima
     * */

    public boolean isSottoScorta(){
        if(this.sogliaMinima == null){
            return false;
        }
        return this.qtaDisponibile <= this.sogliaMinima;
    }
    /**
     * Aumenta la quantità disponibile del prodotto
     * @param qta quantità da aggiungere, necessariamente maggiore di zero (0)
     * @throws IllegalArgumentException se la quantità è minore di zero (0)
     * */
    public void carica(int qta){
        if(qta <= 0){
            throw new IllegalArgumentException("La quantità deve essere maggiore di zero!");
        }

        this.qtaDisponibile += qta;
    }

    /**
     * Diminuisce la quantità disponibile del prodotto
     * @param qta quantità da rimuovere, necessariamente maggiore zero (0)
     * @throws IllegalArgumentException se la quantità è minore di zero (0)
     * */

    public void scarica(int qta){
        if(qta <= 0){
            throw new IllegalArgumentException("La quantità deve essere maggiore di zero!");
        }
        if(qta > this.qtaDisponibile){
            throw new IllegalArgumentException("Quantità insufficiente");
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

    /**
     * Verifica se il prodotto ha esaurito le scorte
     * @return  true se ha esaurito tutte le scorte
     *          false altrimenti
     * */

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
