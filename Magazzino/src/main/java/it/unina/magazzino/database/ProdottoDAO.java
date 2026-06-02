package it.unina.magazzino.database;

import it.unina.magazzino.entity.Prodotto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProdottoDAO {

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
