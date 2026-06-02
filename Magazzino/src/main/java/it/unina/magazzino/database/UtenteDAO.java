package it.unina.magazzino.database;

import it.unina.magazzino.entity.Operatore;
import it.unina.magazzino.entity.Responsabile;
import it.unina.magazzino.entity.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtenteDAO {

    // AGGIUNTO: "throws SQLException" per delegare l'errore al Controller
    public Utente effettuaLogin(String email, String password) throws SQLException {

        String query = "SELECT * FROM utente WHERE email = ? AND password = ?";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                String ruolo = result.getString("ruolo");
                String id = result.getString("IdUtente");
                String nome = result.getString("Nome");
                String cognome = result.getString("Cognome");

                // CAMBIATO: Nomi variabili modificati per evitare conflitti coi parametri del metodo
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