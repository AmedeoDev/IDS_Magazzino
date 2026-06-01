package it.unina.magazzino.entity;

public abstract class Utente {

    private final String nome;
    private final String cognome;
    private final String email;
    private final String password;
    private final String ID_Utenete;

    protected Utente(String nome, String cognome, String email, String password, String ID_Utente){
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.ID_Utenete = ID_Utente;
    }

    public String getNome() { return this.nome; }
    public String getCognome() { return this.cognome; }
    public String getEmail() { return  this.email; }
    public String getPassword() { return this.password; }
    public String getID_Utenete() { return this.ID_Utenete; }

    public abstract String getRuolo();

    @Override
    public String toString(){
        return getNome() + " " + getCognome() + " " + getRuolo() + " [ " + getEmail() + " ] ";
    }
}
