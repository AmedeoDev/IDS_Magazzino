package it.unina.magazzino.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProdottoTest {

    private Prodotto prodotto;

    @BeforeEach
    void setUp(){
        prodotto = new Prodotto("P-001", "Sicurezza", "Casco di sicurezza", "Casco da cantiere", 50, 10, "POS-A1", "RESP-001");
    }

    @Test
    void testCaricoConAumentoQta(){
        prodotto.carica(20);
        assertEquals(70, prodotto.getQtaDisponibile());
    }

    @Test
    void testCaricaConQtaZeroConEccezione(){
        assertThrows(IllegalArgumentException.class, () -> prodotto.carica(0));
    }

    @Test
    void testCaricaConQtaNegativaEccezione(){
        assertThrows(IllegalArgumentException.class, () -> prodotto.carica(-5));
    }

    @Test
    void testSottoScorta(){
        prodotto.scarica(45);
        assertTrue(prodotto.isSottoScorta());
    }

    @Test
    void testProdottoNonSottoScortaConQtaSuperiore(){
        assertFalse(prodotto.isSottoScorta());
    }

    @Test
    void testNonSottoScortaSenzaSogliaDefinita(){
        Prodotto testP = new Prodotto("P-002", "Abbigliamento", "Occhiali da Sole", "Occhiali comodi", 5, null, "POS-A2", "RESP-002");
        assertFalse(testP.isSottoScorta());
    }

    @Test
    void testVuotoConQtaZero(){
        prodotto.scarica(50);
        assertTrue(prodotto.isEmpty());
    }

    @Test
    void testCostuttoreConQuantitaNegativaEccezione(){
        assertThrows(IllegalArgumentException.class, () ->
                new Prodotto("P-003", "test", "prodotto finto", "descrizione",
                        -1, 10, "POS-A3", "RESP-003"));
    }

    @Test
    void testCostruttoreConSogliaNegativaEccezione(){
        assertThrows(IllegalArgumentException.class, () ->
                new Prodotto("P-004", "cibo", "pane", "pane di farina bianca",
                        10, -1, "POS-A1", "RESP-001"));
    }
}
