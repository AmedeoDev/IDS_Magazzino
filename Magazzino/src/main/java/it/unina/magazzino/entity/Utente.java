package it.unina.magazzino.entity;

public abstract class Utente {

    private final String nome;
    private final String cognome;
    private final String email;
    private final String password;

    protected Utente(String nome, String cognome, String email, String password){
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
    }

    public String getNome() { return this.nome; }
    public String getCognome() { return this.cognome; }
    public String getEmail() { return  this.email; }
    public String getPassword() { return this.password; }

    public abstract String getRuolo();

    @Override
    public String toString(){
        return getNome() + " " + getCognome() + " " + getRuolo() + " [ " + getEmail() + " ] ";
    }

}
