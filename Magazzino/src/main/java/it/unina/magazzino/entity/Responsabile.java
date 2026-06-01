package it.unina.magazzino.entity;

public class Responsabile extends Utente{

    public Responsabile(String nome, String cognome, String email, String password, String ID_Utente){
        super(nome, cognome, email, password, ID_Utente);
    }

    @Override
    public String getRuolo(){
        return "Responsabile";
    }
}
