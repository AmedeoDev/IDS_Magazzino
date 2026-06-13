package it.unina.magazzino.control;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RegistrationControllerTest {

    private RegistrationController controller;

    @BeforeEach
    void setUp(){
        controller = new RegistrationController();
    }

    @Test
    void testCampiVuotiEccezione(){
        Exception e = assertThrows(Exception.class, () -> controller.registraNuovoUtente(
                "", "", "", "", "", "Operatore"));
        assertEquals("Compila correttamente tutti i campi per la registrazione.", e.getMessage());
    }

    @Test
    void testRuoloDefaultLanciaEccezione(){
        Exception e = assertThrows(Exception.class, () -> controller.registraNuovoUtente(
                "Amedeo", "Catanese Napolitano", "amedeo@test.it", "TestPsw123", "TestPsw123",
                "-- Seleziona --"
        ));
        assertEquals("Seleziona un ruolo valido.", e.getMessage());
    }

    @Test
    void testNomeConNumeriEccezione(){
        Exception e = assertThrows(Exception.class, () -> controller.registraNuovoUtente(
                "Francesco12", "Capasso", "francesco@test.it", "TestPsw123", "TestPsw123", "Operatore"
        ));
        assertEquals("Il nome non può contenere numeri o caratteri speciali.", e.getMessage());
    }

    @Test
    void testEmailConFormatoErratoEccezione(){
        Exception e = assertThrows(Exception.class, () -> controller.registraNuovoUtente(
                "Fabrizio", "Centrella", "fabriziomail", "TestPsw123", "TestPsw123", "Operatore"
        ));
        assertEquals("Formato e-mail non valido.", e.getMessage());
    }

    @Test
    void testPasswordSenzaNumeriEccezione(){
        Exception e = assertThrows(Exception.class, () -> controller.registraNuovoUtente(
                "Amedeo", "Catanese Napolitano", "amedeo@test.it", "TestPsw", "TestPsw", "Responsabile"
        ));
        assertEquals("La password deve contenere almeno una lettera e un numero.", e.getMessage());
    }

    @Test
    void testPasswordDebole(){
        Exception e = assertThrows(Exception.class, () -> controller.registraNuovoUtente(
                "Francesco", "Capasso", "francesco@test.it", "psw", "psw", "Responsabile"
        ));
        assertEquals("Password troppo debole: minimo 6 caratteri.", e.getMessage());
    }

    @Test
    void testPasswordDiverseEccezione(){
        Exception e = assertThrows(Exception.class, () -> controller.registraNuovoUtente(
                "Fabrizio", "Centrella", "fabrizio@mail.it", "TestPsw123", "TestPsw1234", "Responsabile"
        ));
        assertEquals("Le due password non coincidono.", e.getMessage());
    }

    @Test
    void testRuoloNonValidoEccezione(){
        Exception e = assertThrows(Exception.class, () -> controller.registraNuovoUtente(
                "Test", "Test", "test@test.it", "TestPsw123", "TestPsw123", "Meccanico"
        ));
        assertEquals("Ruolo selezionato non valido.", e.getMessage());
    }

}
