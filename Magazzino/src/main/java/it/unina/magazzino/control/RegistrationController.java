package it.unina.magazzino.control;

import it.unina.magazzino.entity.Operatore;
import it.unina.magazzino.entity.Responsabile;
import it.unina.magazzino.entity.Utente;

public class RegistrationController {

    /*
    * Gestiamo la logica per garantire una corretta registrazione degli utenti
    * @throws Exception se la validazione fallisce o l'utente risulta già registrato
    */

    public boolean registraNuovoUtente(String nome, String cognome, String email, String password, String confermaPsw,String ruolo) throws Exception {

        if(nome == null || nome.trim().isEmpty()
            || cognome == null || cognome.trim().isEmpty()
            || email == null || email.trim().isEmpty()
            || password == null || password.trim().isEmpty()
            || ruolo == null || ruolo.trim().isEmpty()
            || confermaPsw == null || confermaPsw.trim().isEmpty()) {

            throw new Exception("Compila correttamente i campi per la registrazione");
        }

        if (password.length() < 6){
            throw new Exception("Passord troppo debole, almeno 6 caratteri");
        }

        if(!password.equals(confermaPsw)){
            throw new Exception("Le due password non coincidono!");
        }

        if(email.equalsIgnoreCase("operatore@mail.it") || email.equalsIgnoreCase("admin@mail.it")){
            throw new Exception("Questa mail è associata ad un utente già esistente");
        }

        Utente nuovoUtente;
        String idAssociato = "USR-" + System.currentTimeMillis() % 1000;

        if(ruolo.equalsIgnoreCase("OPERATORE")){
            nuovoUtente = new Operatore(nome.trim(), cognome.trim(), email.trim(), password, idAssociato);
        } else if(ruolo.equalsIgnoreCase("RESPONSABILE")){
            nuovoUtente = new Responsabile(nome.trim(), cognome.trim(), email.trim(), password, idAssociato);
        } else {
            throw new Exception("Ruolo selezionato non esistente");
        }


        System.out.println("Registrazione avvenuta con successo...");
        System.out.println("Nuovo utente: " + nuovoUtente.getNome() + " " + nuovoUtente.getCognome() + " [ " + nuovoUtente.getID_Utenete() + " ]" + " { " + nuovoUtente.getRuolo() + " }");

        return true;
    }

}
