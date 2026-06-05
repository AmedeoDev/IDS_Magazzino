package it.unina.magazzino.database;

import it.unina.magazzino.entity.Movimento;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovimentoDAO {

    public List<Movimento> getMovimentiDegliOperatori(String idOperatore) throws SQLException {

        List<Movimento> storico = new ArrayList<>();

        String query = "SELECT m.IdMovimento, m.QtaProd, m.Data, m.TipoMovimento, m.IdProd, m.IdUtenteOperatore, p.Nome " +
                "FROM movimento m " +
                "JOIN prodotto p on m.IdProd = p.IdProd " +
                "WHERE m.IdUtenteOperatore = ? " +
                "ORDER BY m.Data DESC ";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setString(1, idOperatore);

            try (ResultSet rs = stmt.executeQuery()){
                while (rs.next()){

                    int idMovimento = rs.getInt("IdMovimento");
                    int qta = rs.getInt("QtaProd");
                    Date data = rs.getDate("Data");
                    String tipo = rs.getString("TipoMovimento");
                    String idProd = rs.getString("IdProd");
                    String IdOp = rs.getString("IdUtenteOperatore");

                    String nomeP = rs.getString("Nome");

                    Movimento m = new Movimento(idMovimento, qta, data, tipo, idProd, idOperatore, nomeP);
                    storico.add(m);
                }
            }
        }
        return storico;
    }

    public boolean inserisciMovimento(Movimento m) throws SQLException{

        String query = "INSERT INTO movimento (QtaProd, Data, TipoMovimento, IdProd, IdUtenteOperatore) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setInt(1, m.getQtaProdotto());
            stmt.setDate(2, (java.sql.Date) m.getData());
            stmt.setString(3, m.getTipoMovimento());
            stmt.setString(4, m.getIdProdotto());
            stmt.setString(5, m.getIdOperatore());

            return stmt.executeUpdate() > 0;


        }
    }
}
