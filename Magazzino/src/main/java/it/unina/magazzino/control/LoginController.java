package it.unina.magazzino.control;

import it.unina.magazzino.boundary.DashboardOperatore;
import it.unina.magazzino.boundary.DashboardResponsabile;
import it.unina.magazzino.database.UtenteDAO;
import it.unina.magazzino.entity.Utente;
import it.unina.magazzino.entity.Responsabile;
import it.unina.magazzino.entity.Operatore;

import java.sql.SQLException;

public class LoginController {

    public Utente effettuaLogin(String email, String password) throws Exception{
        if (email == null || email.trim().isEmpty()){
            throw new Exception("Il campo non può essere vuoto");
        } else if(password == null || password.trim().isEmpty()){
            throw new Exception("Il campo non può essere vuoto");
        }

        UtenteDAO ud = new UtenteDAO();
        Utente utenteTrovato = ud.effettuaLogin(email.trim(), password);

        if(utenteTrovato == null){
            throw new Exception("Credenziali errate o non trovate");
        }

        String ruolo = utenteTrovato.getRuolo();
        if("Operatore".equalsIgnoreCase(ruolo)){
            DashboardOperatore dashboardOperatore = new DashboardOperatore((Operatore)utenteTrovato, "");
            dashboardOperatore.setVisible(true);
        } else if("Responsabile".equalsIgnoreCase(ruolo)){
            DashboardResponsabile dashboardResponsabile = new DashboardResponsabile((Responsabile)utenteTrovato, "");
            dashboardResponsabile.setVisible(true);
        } else{
            throw new Exception("!!! IMPOSSIBILE ACCEDERE, RUOLO NON ESISTENTE !!!");
        }

        return utenteTrovato;

    }

    public void gestisciLogin(String email, String password){
        try{
            UtenteDAO ud = new UtenteDAO();
            Utente utenteLoggato = ud.effettuaLogin(email, password);

            if(utenteLoggato == null){
                System.out.println("Errore: credenziali errate");
                return;
            }

            String ruolo = utenteLoggato.getRuolo();
            if(ruolo.equals("Operatore")){
                DashboardOperatore dashboardOperatore = new DashboardOperatore((Operatore)utenteLoggato, "");
                dashboardOperatore.setVisible(true);
            } else if(ruolo.equals("Responsabile")){
                DashboardResponsabile dashboardResponsabile = new DashboardResponsabile((Responsabile)utenteLoggato, "");
                dashboardResponsabile.setVisible(true);
            }
        } catch(SQLException e){
            System.out.println("Errore di connessione al DB");
            e.printStackTrace();
        }
    }

    private Utente createUtenteMock(String email){
        if(email.equals("operatore@mail.it")){
            return new Operatore(
                    "Amedeo",
                    "Catanese",
                    "operatore@mail.it",
                    "12345",
                    "OOP-12"
            );
        }

        if(email.equals("admin@mail.it")){

            return new Responsabile(
                    "Luca",
                    "Rossi",
                    "admin@mail.it",
                    "123456",
                    "ORP-12"
            );
        }
        return null;
    }
}
