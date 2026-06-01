package it.unina.magazzino.control;

import it.unina.magazzino.entity.Utente;
import it.unina.magazzino.entity.Responsabile;
import it.unina.magazzino.entity.Operatore;

public class LoginController {

    public Utente effettuaLogin(String email, String password) throws Exception{
        if (email == null || email.trim().isEmpty()){
            throw new Exception("Il campo non può essere vuoto");
        } else if(password == null || password.trim().isEmpty()){
            throw new Exception("Il campo non può essere vuoto");
        }

        Utente utenteTrovato = createUtenteMock(email); // manca la parte di connessione al DB

        if(utenteTrovato == null){

            throw new Exception("Utente non trovato!");
        }

        if(!utenteTrovato.getPassword().equals(password)){

            throw new Exception("Credenziali non corrette");
        }

        return utenteTrovato;

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
