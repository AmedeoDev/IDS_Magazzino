package it.unina.magazzino.control;

import it.unina.magazzino.entity.Operatore;
import it.unina.magazzino.entity.Posizione;
import it.unina.magazzino.entity.Prodotto;

public class MovimentoController {

    /*

    public void registraCarico(Operatore operatoreLoggato, Prodotto prodotto, Posizione posizione, int qta) throws Exception{

        if(qta <= 0){
            throw new Exception("La quantità deve essere maggiore di zero!");
        }

        // da implementare --> funzione carica() in Prodotto


        if(posizione.isLibero()){
            // implementare .deposita() in Prodotto
        } else if(!posizione.getProdotto().getID().equals(prodotto.getID())){
            throw new Exception("Impossibile caricare: posizione occupata");
        }


        System.out.println("Carico registrati con successo dall'operatore: " + operatoreLoggato);
    }

    public void registraScarico(Operatore operatoreLoggato, Prodotto prodotto, Posizione posizione, int qta){
        prodotto.scarica(qta);

        if(prodotto.isEsaurito()){
            posizione.svuotaPosizione();
        }
    }
    
    */
}
