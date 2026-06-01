package it.unina.magazzino.entity;

public class Responsabile extends Utente{

    public Responsabile(String nome, String cognome, String email, String password){
        super(nome, cognome, email, password);
    }

    @Override
    public String getRuolo(){
        return "Responsabile";
    }
}
