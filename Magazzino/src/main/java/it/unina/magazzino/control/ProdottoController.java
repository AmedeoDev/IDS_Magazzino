package it.unina.magazzino.control;

import it.unina.magazzino.database.ProdottoDAO;
import it.unina.magazzino.entity.Prodotto;

import java.sql.SQLException;
import java.util.List;

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

    public boolean registraNuovoProdotto(String id, String nome, String categoria, String descrizione, int qtaDisp, int sogliaMinima, String idPos, String iDUtenteResp) throws Exception{

        // controlli di base
        if(id == null || id.trim().isEmpty()) throw new Exception("L'id non può essere vuoto");
        if(nome == null || nome.trim().isEmpty()) throw new Exception("Il nome non può essere vuoto");
        if(categoria == null || categoria.trim().isEmpty()) throw new Exception("La categoria non può essere vuota");
        if(qtaDisp <= 0) throw new Exception("Inserisci una quantità valida!");
        if(sogliaMinima < 0) throw new Exception("Inserisci una soglia minima valida!");

        Prodotto nProdotto = new Prodotto(id, categoria, nome, descrizione, qtaDisp, sogliaMinima, idPos, iDUtenteResp);

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
}
