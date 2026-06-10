package it.unina.magazzino.control;

import it.unina.magazzino.database.MovimentoDAO;
import it.unina.magazzino.database.ProdottoDAO;
import it.unina.magazzino.entity.Movimento;
import it.unina.magazzino.entity.Prodotto;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProdottoController {


    public List<Prodotto> getAllProdotti(){

        try {
            ProdottoDAO pdao = new ProdottoDAO();
            return pdao.getProdotti();
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getPosizioniDisponibili(){
        try {
            ProdottoDAO dao = new ProdottoDAO();
            return dao.getPosizioniLibere();
        } catch (SQLException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean aggiornaQuantitaProdotto(String idProd, int nQta){
        try {
            ProdottoDAO dao = new ProdottoDAO();
            return dao.aggiornaQuantita(idProd, nQta);
        } catch (SQLException e){
            System.out.println("Errore di aggiornamento: " + e.getMessage());
            return false;
        }
    }

    public boolean registraNuovoProdotto(String nome, String categoria, String descrizione, int qtaDisp, int sogliaMinima, String idPos, String iDUtenteResp) throws Exception{

        // controlli di base
        if(nome == null || nome.trim().isEmpty()) throw new Exception("Il nome non può essere vuoto");
        if(categoria == null || categoria.trim().isEmpty()) throw new Exception("La categoria non può essere vuota");
        if(qtaDisp <= 0) throw new Exception("Inserisci una quantità valida!");
        if(sogliaMinima < 0) throw new Exception("Inserisci una soglia minima valida!");


        String idGenerato = "P-" + UUID.randomUUID().toString().substring(0, 2).toUpperCase();

        Prodotto nProdotto = new Prodotto(idGenerato, categoria, nome, descrizione, qtaDisp, sogliaMinima, idPos, iDUtenteResp);

        try{

            ProdottoDAO pdao = new ProdottoDAO();
            boolean success = pdao.aggiungiProdotto(nProdotto);
            if(!success){
                throw new Exception("Impossible salvare il prodotto.\nRiprova più tardi");
            }
            return true;
        } catch (SQLException e){
            // err.code 1062 indica la presenza di un duplicato nel magazzino
            if(e.getErrorCode() == 1062){ throw new Exception("Errore: è presente un prodotto con lo stesso ID"); }
            throw new Exception("Errore: impossibile connettersi al database.\n[ERR-TYPE: " + e.getMessage() + " ]");
        }
    }

    public boolean modificaProdotto(String idProd, String nome, String categoria, String descrizione, int qtDisp, int sogliaMinima, String posizione, String idResponsabile) {

        try {

            System.out.println("[DEBUG modificaProdotto]");
            System.out.println("  idProd:        " + idProd);
            System.out.println("  nome:          " + nome);
            System.out.println("  categoria:     " + categoria);
            System.out.println("  descrizione:   " + descrizione);
            System.out.println("  qtDisp:        " + qtDisp);
            System.out.println("  sogliaMinima:  " + sogliaMinima);
            System.out.println("  posizione:     " + posizione);
            System.out.println("  idResponsabile:" + idResponsabile);

            ProdottoDAO dao = new ProdottoDAO();
            return dao.aggiornaProdottoCompleto(idProd, nome, categoria, descrizione, qtDisp, sogliaMinima, posizione, idResponsabile);
        } catch (Exception e){
            System.out.println("Errore modifica prodotto: " + e.getMessage());
            return false;
        }

    }

    public List<Movimento> getMovimentiOggi() {

        try {
            MovimentoDAO dao = new MovimentoDAO();
            return dao.getMovimentiOggi();
        } catch (Exception e){
            System.out.println("Errore getMovimentiOggi: " + e.getMessage());
            return null;
        }

    }

    public List<Movimento> getMovimentiIeri() {
        try {
            MovimentoDAO dao = new MovimentoDAO();
            return dao.getMovimentoIeri();
        } catch (Exception e){
            System.out.println("Errore getMovimentoIeri: " + e.getMessage());
            return null;
        }
    }
}
