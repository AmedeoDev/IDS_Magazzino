package it.unina.magazzino.entity;

public class Operatore extends Utente{

    public Operatore(String nome, String cognome, String email, String password){
        super(nome, cognome, email, password);
    }

    @Override
    public String getRuolo() {
        return "Operatore";
    }
}
