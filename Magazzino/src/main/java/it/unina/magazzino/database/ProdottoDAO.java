package it.unina.magazzino.database;

import it.unina.magazzino.entity.Prodotto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdottoDAO {

    public boolean aggiungiProdotto(Prodotto p) throws SQLException{
        String query = "INSERT INTO prodotto (IdProd, Nome, Categoria, Descrizione, QtaDisp, SogliaMinima, IdPos, IdUtenteResponsabile) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)){


            // debug per problemi legati alla generazione dell'ID
            System.out.println(">>> DEBUG <<<");
            System.out.println("IdProd: " + p.getID() + " Lunghezza: " + (p.getID() != null ? p.getID().length() : 0));

            stmt.setString(1, p.getID());
            stmt.setString(2, p.getNome());
            stmt.setString(3, p.getCategoria());
            stmt.setString(4, p.getDescrizione());
            stmt.setInt(5, p.getQtaDisponibile());
            stmt.setInt(6, p.getSogliaMinima());
            stmt.setString(7, p.getIdPos());
            stmt.setString(8, p.getIdUtenteResponsabile());

            int righeInserite = stmt.executeUpdate();
            return righeInserite > 0;

        }
    }

    public List<Prodotto> getProdotti() throws SQLException{

        List<Prodotto> inventario = new ArrayList<>();
        String query = "SELECT * FROM prodotto";

        try (Connection con = DBConnectionManager.getConnection();
        PreparedStatement stmt = con.prepareStatement(query)){

            ResultSet result = stmt.executeQuery();

            while(result.next()){
                String id = result.getString("IdProd");
                String nome = result.getString("Nome");
                String categoria = result.getString("Categoria");
                String descrizione = result.getString("Descrizione");
                int qtaDisp = result.getInt("QtaDisp");
                int sogliaMinima = result.getInt("SogliaMinima");
                String idPos = result.getString("IdPos");
                String idUtenteResponsabile = result.getString("IdUtenteResponsabile");

                Prodotto p = new Prodotto(id, categoria, nome, descrizione, qtaDisp, sogliaMinima, idPos, idUtenteResponsabile);

                inventario.add(p);
            }
        }

        return inventario;

    }

    public Prodotto cercaConId(String id) throws SQLException{

        String query = "SELECT * FROM prodotto WHERE id = ?";

        try(Connection conn = DBConnectionManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setString(1, id);
            ResultSet result = stmt.executeQuery();

            if(result.next()){
                Integer soglia = (Integer) result.getObject("SogliaMinima");
                return new Prodotto(
                        result.getString("IdProd"),
                        result.getString("Categoria"),
                        result.getString("Nome"),
                        result.getString("Descrizione"),
                        result.getInt("QtaDisp"),
                        soglia,
                        result.getString("IdPos"),
                        result.getString("IdUtenteResponsabile")
                );
            }
        }
        return null;
    }
}
