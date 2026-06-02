package it.unina.magazzino.entity;

import java.time.LocalDateTime;

public class Notifica {

    private final String id;
    private final String messaggio;
    private final LocalDateTime data;
    private boolean letta;

    public Notifica(String id, String messaggio, LocalDateTime data){
        this.id = id;
        this.messaggio = messaggio;
        this.data = data;
    }

    public boolean setAsLetta() { return this.letta = true; }

    public String getId() { return this.id; }
    public String getMessaggio() { return this.messaggio; }
    public LocalDateTime getData() { return this.data; }
}
