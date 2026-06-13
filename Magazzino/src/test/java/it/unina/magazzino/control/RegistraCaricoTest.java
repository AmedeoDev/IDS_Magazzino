package it.unina.magazzino.control;

import it.unina.magazzino.entity.Prodotto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegistraCaricoTest {

    private Prodotto prodotto;
    private ProdottoController controller;

    @BeforeEach
    void SetUp(){
        prodotto = new Prodotto("P-001", "Sicurezza", "Guanti latex",
                "Guanti Monouso", 20, 10, "POS-A1", "RESP-001");
        controller = new ProdottoController();
    }

    @Test
    void testCaricoAumentaQtaDisponibile(){
        prodotto.carica((30));
        assertEquals(50, prodotto.getQtaDisponibile());
    }

    @Test
    void testCaricoConQtaZeroEccezione(){
        assertThrows(IllegalArgumentException.class, () -> prodotto.carica(0));
    }

    @Test
    void testCaricoConQtaNegativaEccezione(){
        assertThrows(IllegalArgumentException.class, () -> prodotto.carica(-10));
    }

    @Test
    void testDopoCaricoProdottoNonVuoto(){
        prodotto.carica(5);
        assertFalse(prodotto.isEmpty());
    }

    @Test
    void testDopoCaricoProdottoNonSottoScorta(){
        prodotto.scarica(15);
        assertTrue(prodotto.isSottoScorta());
        prodotto.carica(20);
        assertFalse(prodotto.isSottoScorta());
    }

    @Test
    void testRegistraNuovoProdottoNomeVuotoEccezione(){
        Exception e = assertThrows(Exception.class, () -> controller.registraNuovoProdotto(
                "", "test", "test", 10, 2, "POS-A1", "RESP-001"
        ));
        assertEquals("Il nome non può essere vuoto", e.getMessage());
    }

    @Test
    void testRegistraNuovoProdottoCategoriaVuotaEccezione(){
        Exception e = assertThrows(Exception.class, () -> controller.registraNuovoProdotto(
                "test", "", "test", 20, 5, "POS-A2", "RESP-002"
        ));
        assertEquals("La categoria non può essere vuota", e.getMessage());
    }

    @Test
    void testRegistraNuovoProdottoConQtaZeroEccezione(){
        Exception e = assertThrows(Exception.class, () -> controller.registraNuovoProdotto(
                "test", "test", "test", 0, 10, "POS-A3", "RESP-003"
        ));
        assertEquals("Inserisci una quantità valida!", e.getMessage());
    }

    @Test
    void testRegistraNuovoProdottoConQtaNegativaEccezione(){
        Exception e = assertThrows(Exception.class, () -> controller.registraNuovoProdotto(
                "test", "test", "test", -10, 5, "POS-A1", "RESP-001"
        ));
        assertEquals("Inserisci una quantità valida!", e.getMessage());
    }

    @Test
    void testRegistraNuovoProdottoConSogliaNegativaEccezione(){
        Exception e = assertThrows(Exception.class, () -> controller.registraNuovoProdotto(
                "test", "test", "test", 10, -5, "POS-A2", "RESP-002"
        ));
        assertEquals("Inserisci una soglia minima valida!", e.getMessage());
    }
}
