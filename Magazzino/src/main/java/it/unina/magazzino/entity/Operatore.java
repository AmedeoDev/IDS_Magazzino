package it.unina.magazzino.entity;

public class Operatore extends Utente{

    public Operatore(String nome, String cognome, String email, String password, String ID_utente){
        super(nome, cognome, email, password, ID_utente);
    }

    @Override
    public String getRuolo() {
        return "Operatore";
    }
}
