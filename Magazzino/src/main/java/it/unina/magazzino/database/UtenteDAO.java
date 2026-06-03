package it.unina.magazzino.database;

import it.unina.magazzino.entity.Operatore;
import it.unina.magazzino.entity.Responsabile;
import it.unina.magazzino.entity.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtenteDAO {

    public boolean RegistraUtente(Utente nuovoUtente) throws SQLException{

        String query = "INSERT INTO utente (IdUtente, Nome, Cognome, Email, Password, ruolo) VALUES (?, ?, ?, ?, ?, ?)";

        String queryOperator = "INSERT INTO operatore (IdUtenteOperatore) VALUES (?)";
        String queryResp = "INSERT INTO responsabile (IdUtenteResponsabile) VALUES (?)";

        Connection conn = DBConnectionManager.getConnection();

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nuovoUtente.getID_Utenete());
                stmt.setString(2, nuovoUtente.getNome());
                stmt.setString(3, nuovoUtente.getCognome());
                stmt.setString(4, nuovoUtente.getEmail());
                stmt.setString(5, nuovoUtente.getPassword());
                stmt.setString(6, nuovoUtente.getRuolo());
                stmt.executeUpdate();
            }

            String queryRuolo = nuovoUtente instanceof Responsabile ? queryResp : queryOperator;
            try (){

            }
        }


    }



    public Utente effettuaLogin(String email, String password) throws SQLException {

        String query = "SELECT * FROM utente WHERE email = ? AND password = ?";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            // DEBUG: VEDO QUALI DATI SONO PASSATI

            System.out.println("[DEBUG] - Email: " + email);
            System.out.println("[DEBUG] - Password: " + password);

            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                String ruolo = result.getString("ruolo");
                String id = result.getString("IdUtente");
                String nome = result.getString("Nome");
                String cognome = result.getString("Cognome");

                String mailDB = result.getString("Email");
                String passDB = result.getString("Password");

                if ("Operatore".equalsIgnoreCase(ruolo)) {
                    return new Operatore(nome, cognome, mailDB, passDB, id);
                } else if ("Responsabile".equalsIgnoreCase(ruolo)) {
                    return new Responsabile(nome, cognome, mailDB, passDB, id);
                } else {
                    throw new SQLException("Ruolo non riconosciuto nel database: " + ruolo);
                }
            }
        }

        return null;
    }
}