package it.unina.magazzino.control;

import it.unina.magazzino.database.MovimentoDAO;
import it.unina.magazzino.database.ProdottoDAO;
import it.unina.magazzino.entity.Movimento;
import it.unina.magazzino.entity.Operatore;
import it.unina.magazzino.entity.Posizione;
import it.unina.magazzino.entity.Prodotto;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

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

        Date data = new Date(System.currentTimeMillis());
        Movimento m = new Movimento(qta, data, "Carico", prodotto.getID(), operatoreLoggato.getID_Utenete());

        MovimentoDAO dao = new MovimentoDAO();
        dao.inserisciMovimento(m);

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

        Date data = new Date(System.currentTimeMillis());
        Movimento m = new Movimento(qta, data, "Scarico", prodotto.getID(), operatoreLoggato.getID_Utenete());

        MovimentoDAO dao = new MovimentoDAO();
        dao.inserisciMovimento(m);

        if(prodotto.isEmpty()){
            posizione.liberaPosizione();
            System.out.println("[SYSTEM] scorte esaurite. Liberata la posizione: " + posizione.getCodicePosizione());
        }

        System.out.println("Scarico di " + qta + " unità registrato con successo nel sistema");

    }

    public List<Movimento> getStoricoPersonale(String idOperatore){

        try {
            MovimentoDAO dao = new MovimentoDAO();
            return dao.getMovimentiDegliOperatori(idOperatore);
        } catch (Exception e){
            System.out.println("Errore in fase di caricamento del DB: " + e.getMessage());
            return null;
        }

    }

    public List<Movimento> getUltimiMovimenti(int n) {
        try {
            MovimentoDAO dao = new MovimentoDAO();
            return dao.getUltimiMovimenti(n);
        } catch (Exception e) {
            System.out.println("Errore getUltimiMovimenti: " + e.getMessage());
            return null;
        }
    }

    public List<Movimento> getMovimentiUltimi7Giorni() {
        try {
            MovimentoDAO dao = new MovimentoDAO();
            return dao.getMovimenti7Giorni();
        } catch (Exception e) {
            System.out.println("Errore getMovimentiUltimi7Giorni: " + e.getMessage());
            return null;
        }
    }

    public List<String> getProdottiPiuMovimentati(int n) {
        try {
            MovimentoDAO dao = new MovimentoDAO();
            return dao.getProdottiPiuMovimentati(n);
        } catch (Exception e) {
            System.out.println("Errore getProdottiPiuMovimentati: " + e.getMessage());
            return null;
        }
    }


    public List<Movimento> getProdottiOggi(){
        try {
            MovimentoDAO dao = new MovimentoDAO();
            return dao.getMovimentiOggi();
        } catch (Exception e){
            System.out.println("Erroree getMovimentiOggi: " + e.getMessage());
            return null;
        }
    }
}
