package it.unina.magazzino.control;

import it.unina.magazzino.database.ProdottoDAO;
import it.unina.magazzino.entity.Operatore;
import it.unina.magazzino.entity.Posizione;
import it.unina.magazzino.entity.Prodotto;

import java.sql.SQLException;

public class MovimentoController {

    public void registraCarico(Operatore operatoreLoggato, Prodotto prodotto, Posizione posizione, int qta) throws Exception{

        if(qta <= 0){
            throw new Exception("La quantità deve essere maggiore di zero!");
        }

        if(posizione.isLibero()){
            posizione.depositaProdotto(prodotto);
        } else if(!posizione.getProdotto().getID().equals(prodotto.getID())){
            throw new Exception("Impossibile caricare: posizione già occupata!");
        }

        prodotto.carica(qta);

        System.out.println("Carico registrato con successo!");

    }

    public void registraScarico(Operatore operatoreLoggato, Prodotto prodotto, Posizione posizione, int qta) throws Exception{

        if(qta <= 0){
            throw new Exception("La quantità da scaricare non può essere minore di zero!");
        }

        if(posizione.isLibero()){
            throw new Exception("Errore durante lo scarico: la posizione non contiene alcun prodotto");
        } else if(!posizione.getProdotto().getID().equals(prodotto.getID())){
            throw new Exception("Impossibile scarica: la posizione contiene un articolo diverso { " + posizione.getProdotto().getID() + " }");
        }

        prodotto.scarica(qta);

        if(prodotto.isEmpty()){
            posizione.liberaPosizione();
            System.out.println("[SYSTEM] scorte esaurite. Liberata la posizione: " + posizione.getCodicePosizione());
        }

        System.out.println("Scarico di " + qta + " unità registrato con successo nel sistema");

    }
}
