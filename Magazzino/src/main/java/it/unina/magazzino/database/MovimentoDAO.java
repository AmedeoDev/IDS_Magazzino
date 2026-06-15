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

    public List<Movimento> getMovimentiOggi() throws SQLException {
        List<Movimento> lista = new ArrayList<>();
        String query = "SELECT m.*, p.Nome FROM movimento m " +
                "JOIN prodotto p ON m.IdProd = p.IdProd " +
                "WHERE DATE(m.Data) = CURDATE() ORDER BY m.Data DESC";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Movimento(rs.getInt("IdMovimento"), rs.getInt("QtaProd"),
                        rs.getDate("Data"), rs.getString("TipoMovimento"),
                        rs.getString("IdProd"), rs.getString("IdUtenteOperatore"),
                        rs.getString("Nome")));
            }
        }
        return lista;
    }

    public List<Movimento> getUltimiMovimenti(int n) throws SQLException {
        List<Movimento> lista = new ArrayList<>();
        String query = "SELECT m.*, p.Nome FROM movimento m " +
                "JOIN prodotto p ON m.IdProd = p.IdProd " +
                "ORDER BY m.Data DESC LIMIT ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, n);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Movimento(rs.getInt("IdMovimento"),
                            rs.getInt("QtaProd"),
                            rs.getDate("Data"),
                            rs.getString("TipoMovimento"),
                            rs.getString("IdUtenteOperatore"),
                            rs.getString("IdProd"),
                            rs.getString("Nome")));
                }
            }
        }
        return lista;
    }

    public List<Movimento> getMovimenti7Giorni() throws SQLException {
        List<Movimento> lista = new ArrayList<>();
        String query = "SELECT m.*, p.Nome FROM movimento m " +
                "JOIN prodotto p ON m.IdProd = p.IdProd " +
                "WHERE m.Data >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) ORDER BY m.Data DESC";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Movimento(rs.getInt("IdMovimento"), rs.getInt("QtaProd"),
                        rs.getDate("Data"), rs.getString("TipoMovimento"),
                        rs.getString("IdProd"), rs.getString("IdUtenteOperatore"),
                        rs.getString("Nome")));
            }
        }
        return lista;
    }

    public List<String> getProdottiPiuMovimentati(int n) throws SQLException {
        List<String> lista = new ArrayList<>();
        String query = "SELECT p.Nome, COUNT(*) as freq FROM movimento m " +
                "JOIN prodotto p ON m.IdProd = p.IdProd " +
                "GROUP BY m.IdProd, p.Nome ORDER BY freq DESC LIMIT ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, n);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(rs.getString("Nome"));
            }
        }
        return lista;
    }


    public List<Movimento> getMovimentoIeri() throws SQLException {
        List<Movimento> lista = new ArrayList<>();
        String query = "SELECT m.*, p.Nome FROM movimento m " +
                "JOIN prodotto p ON m.IdProd = p.IdProd " +
                "WHERE DATE(m.Data) = DATE_SUB(CURDATE(), INTERVAL 1 DAY) " +
                "ORDER BY m.Data DESC";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Movimento(rs.getInt("IdMovimento"), rs.getInt("QtaProd"),
                        rs.getDate("Data"), rs.getString("TipoMovimento"),
                        rs.getString("IdProd"), rs.getString("IdUtenteOperatore"),
                        rs.getString("Nome")));   // [FIX] era "Nomee"
            }
        }
        return lista;
    }

    public int countMovimentiPerPeriodo(int giorni) throws SQLException{
        String query = "SELECT COUNT(*) FROM movimento WHERE Data >= DATE_SUB(CURDATE(), INTERVAL ? DAY)";
        try (Connection conn = DBConnectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, giorni);
            try (ResultSet rs = stmt.executeQuery()){
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public int countOperatoriDistintiPeriodo(int giorni) throws SQLException {
        String query = "SELECT COUNT(DISTINCT IdUtenteOperatore) FROM movimento WHERE Data >= DATE_SUB(CURDATE(), INTERVAL ? DAY)";
        try (Connection conn = DBConnectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, giorni);
            try (ResultSet rs = stmt.executeQuery()){
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public List<String[]> getMovimentiGiornalieri(int giorni) throws SQLException{
        String query = "SELECT DATE_FORMAT(Data, '%d/%m') AS giorno, " +
                "SUM(CASE WHEN TipoMovimento = 'Carico' THEN 1 ELSE 0 END) AS carichi, " +
                "SUM(CASE WHEN TipoMovimento = 'Scarico' THEN 1 ELSE 0 END) AS scarichi " +
                "FROM movimento WHERE Data >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                "GROUP BY giorno ORDER BY MIN(Data) ASC";

        try (Connection conn = DBConnectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setInt(1, giorni);
            List<String[]> result = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    result.add(new String[]{
                            rs.getString("giorno"),
                            rs.getString("carichi"),
                            rs.getString("scarichi"),
                    });
                }
            }
            return result;
        }
    }

    public List<String[]> getMovimentiSettimanali(int giorni) throws SQLException{
        String query = "SELECT " +
                "YEAR(Data) AS anno, WEEK(Data) AS settimana, " +
                "SUM(CASE WHEN TipoMovimento = 'Carico' THEN 1 ELSE 0 END) AS carichi, " +
                "SUM(CASE WHEN TipoMovimento = 'Scarico' THEN 1 ELSE 0 END) AS scarichi " +
                "FROM movimento " +
                "WHERE Data >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                "GROUP BY YEAR(Data), WEEK(Data) " +
                "ORDER BY YEAR(Data) ASC, WEEK(Data) ASC";

        try (Connection conn = DBConnectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, giorni);
            List<String[]> ris = new ArrayList<>();
            int contatore = 1;
            try (ResultSet rs = stmt.executeQuery()){
                while (rs.next()){
                    ris.add(new String[]{
                            "Sett. " + contatore++,
                            rs.getString("carichi"),
                            rs.getString("scarichi")
                    });
                }
            }
            return ris;
        }
    }

    public List<String[]> getMovimentiMensili(int giorni) throws SQLException{
        String query = "SELECT DATE_FORMAT(Data, '%b %Y') AS periodo, " +
                "SUM(CASE WHEN TipoMovimento = 'Carico' THEN 1 ELSE 0 END) AS carico, " +
                "SUM(CASE WHEN TipoMovimento = 'Scarico' THEN 1 ELSE 0 END) AS scarico " +
                "FROM movimento WHERE Data >= DATE_SUB(CURDATE(), INTERVAL ? DAY) GROUP BY periodo ORDER BY MIN(Data) ASC";

        try (Connection conn = DBConnectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, giorni);
            List<String[]> ris = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()){
                while(rs.next()) {
                    ris.add(new String[]{
                            rs.getString("periodo"),
                            rs.getString("carico"),
                            rs.getString("scarico"),
                    });
                }
            }
            return ris;
        }
    }

    public List<String[]> getProdottiPiuMovimentati(int n, int giorni) throws SQLException{
        String query = "SELECT p.Nome, COUNT(*) AS freq FROM movimento m " +
                "JOIN prodotto p on m.IdProd = p.IdProd " +
                "WHERE m.Data >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                "GROUP BY m.IdProd, p.Nome ORDER BY freq DESC LIMIT ?";

        try (Connection conn = DBConnectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setInt(1, giorni);
            stmt.setInt(2, n);
            List<String[]> risultato = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    risultato.add(new String[]{
                            rs.getString("Nome"),
                            rs.getString("freq"),
                    });
                }
            }
            return risultato;
        }
    }
}
