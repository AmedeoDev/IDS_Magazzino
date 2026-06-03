package it.unina.magazzino.control;

import it.unina.magazzino.database.UtenteDAO;
import it.unina.magazzino.entity.Operatore;
import it.unina.magazzino.entity.Responsabile;
import it.unina.magazzino.entity.Utente;

import java.sql.SQLException;

public class RegistrationController {

    /*
     * Gestiamo la logica per garantire una corretta registrazione degli utenti
     * @throws Exception se la validazione fallisce o l'utente risulta già registrato
     * Ultime aggiunte:
     *  Ruolo placeholder — blocca se l'utente lascia "-- Seleziona --"
        Regex nome/cognome — accetta lettere italiane/accentate, spazi, apostrofi e trattini (es. D'Angelo, De Luca)
        Regex email — formato standard user@domain.ext
        Password più robusta — almeno una lettera e un numero, non solo lunghezza minima
     */


    public boolean registraNuovoUtente(String nome, String cognome, String email, String password, String confermaPsw, String ruolo) throws Exception {

        // 1. Campi vuoti
        if (nome == null || nome.trim().isEmpty()
                || cognome == null || cognome.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || confermaPsw == null || confermaPsw.trim().isEmpty()
                || ruolo == null || ruolo.trim().isEmpty()) {
            throw new Exception("Compila correttamente tutti i campi per la registrazione.");
        }

        // 2. Ruolo non selezionato (valore placeholder)
        if (ruolo.equals("-- Seleziona --")) {
            throw new Exception("Seleziona un ruolo valido.");
        }

        // 3. Nome e cognome: solo lettere, spazi, apostrofi e trattini
        if (!nome.trim().matches("[a-zA-ZÀ-ù '\\-]+")) {
            throw new Exception("Il nome non può contenere numeri o caratteri speciali.");
        }
        if (!cognome.trim().matches("[a-zA-ZÀ-ù '\\-]+")) {
            throw new Exception("Il cognome non può contenere numeri o caratteri speciali.");
        }

        // 4. Formato email
        if (!email.trim().matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            throw new Exception("Formato e-mail non valido.");
        }

        // 5. Password: minimo 6 caratteri, almeno una lettera e un numero
        if (password.length() < 6) {
            throw new Exception("Password troppo debole: minimo 6 caratteri.");
        }
        if (!password.matches(".*[a-zA-Z].*") || !password.matches(".*[0-9].*")) {
            throw new Exception("La password deve contenere almeno una lettera e un numero.");
        }

        // 6. Conferma password
        if (!password.equals(confermaPsw)) {
            throw new Exception("Le due password non coincidono.");
        }

        // 7. Email già esistente (mock — da sostituire con DB)
        if (email.equalsIgnoreCase("operatore@mail.it") || email.equalsIgnoreCase("admin@mail.it")) {
            throw new Exception("Questa mail è associata ad un utente già esistente.");
        }

        // 8. Creazione utente
        Utente nuovoUtente;
        String idAssociato = "ST-" + System.currentTimeMillis() % 1000;

        if (ruolo.equalsIgnoreCase("OPERATORE")) {
            nuovoUtente = new Operatore(nome.trim(), cognome.trim(), email.trim(), password, idAssociato);
        } else if (ruolo.equalsIgnoreCase("RESPONSABILE")) {
            nuovoUtente = new Responsabile(nome.trim(), cognome.trim(), email.trim(), password, idAssociato);
        } else {
            throw new Exception("Ruolo selezionato non valido.");
        }

        System.out.println("Registrazione avvenuta con successo...");
        System.out.println("Nuovo utente: " + nuovoUtente.getNome() + " " + nuovoUtente.getCognome()
                + " [ " + nuovoUtente.getID_Utenete() + " ] { " + nuovoUtente.getRuolo() + " }");

        // creiamo il nuovo utente

        Utente newUser = null;
        if(ruolo.equalsIgnoreCase("OPERATORE")){
            newUser = new Operatore(nome.trim(), cognome.trim(), email.trim(), password, idAssociato);
        } else if(ruolo.equalsIgnoreCase("RESPONSABILE")){
            newUser = new Responsabile(nome.trim(), cognome.trim(), email.trim(), password, idAssociato);
        }

        // N.B. ora interagiamo col database, eventuali errori ad esso legati vanno risolit qui...

        try{
            UtenteDAO dao = new UtenteDAO();
            boolean inserito = dao.RegistraUtente(newUser);

            if(!inserito){
                throw new Exception("Impossibile salvare l'utente, riprova più tardi");
            }
        } catch (SQLException e){
            // 1062 corrisponde al codice per la ridondanza dei dati
            if(e.getErrorCode() == 1062){
                throw new Exception("Questa mail è associata ad un utente già registrato");
            }

            // gestiamo altri errori
            System.out.println("Errore SQL: " + e.getMessage());
            throw new Exception("Errore di connesione al DB.");
        }

        System.out.println("Registrazione correttamente avvenuta");
        System.out.println("Nuovo utente salvato nel DB: " + nuovoUtente.getNome() + " " + nuovoUtente.getCognome()
                            + " [ " + nuovoUtente.getID_Utenete() + " ] { " + nuovoUtente.getRuolo() + " }");

        return true;
    }
}