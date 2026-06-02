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
                Integer soglia = (Integer) result.getObject("soglia_minima");
                return new Prodotto(
                        result.getString("id"),
                        result.getString("categoria"),
                        result.getString("nome"),
                        result.getString("descrizione"),
                        result.getByte("qta_disponibile"),
                        soglia
                );
            }
        }
        return null;
    }
}
