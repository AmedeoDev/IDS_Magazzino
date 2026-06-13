package it.unina.magazzino.entity;

public class Posizione {

    private String codicePosizione;
    private String area;
    private String scaffale;

    private Prodotto prodotto;

    public Posizione(String codicePosizione, String area, String scaffale){

        if(codicePosizione == null || codicePosizione.trim().isEmpty()){
            throw new IllegalArgumentException("Il codice della posizione è obbligatorio");
        }

        if(area == null || area.trim().isEmpty()){
            throw new IllegalArgumentException("L'area è obbligatoria");
        }

        if(scaffale == null || scaffale.trim().isEmpty()){
            throw new IllegalArgumentException("Lo scaffale è necessario");
        }

        this.codicePosizione = codicePosizione;
        this.area = area;
        this.scaffale = scaffale;
        this.prodotto = null;
    }

    public String getCodicePosizione(){
        return this.codicePosizione;
    }

    public String getArea(){
        return this.area;
    }

    public String getScaffale(){
        return this.scaffale;
    }

    public Prodotto getProdotto(){
        return this.prodotto;
    }


    public boolean isLibero(){
        return this.prodotto == null;
    }

    public void depositaProdotto(Prodotto prodotto){
        if (prodotto == null){
            throw new IllegalArgumentException("Impossibile depositare un prodotto nullo");
        }
        if(!isLibero()){
            throw new IllegalArgumentException("La posizione " + codicePosizione + " è occupata!");
        }
        this.prodotto = prodotto;
    }

    public Prodotto liberaPosizione(){
        if(isLibero()){
            throw new IllegalStateException("La posizione " + codicePosizione + " è disponibile!");
        }
        Prodotto prodottoRimosso = this.prodotto;
        this.prodotto = null;
        return prodottoRimosso;
    }

    @Override
    public String toString(){
        String stato = isLibero() ? "[LIBERA]" : "[OCCUPATA da " + prodotto.getID() + "]";
        return "Posizione: " + codicePosizione + "{AREA= " + area + ", SCAFFALE= " + scaffale + "}" + "(È libero: " + stato + " )";
    }
}
