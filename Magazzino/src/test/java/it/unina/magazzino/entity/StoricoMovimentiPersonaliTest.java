package it.unina.magazzino.entity;

import it.unina.magazzino.control.MovimentoController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StoricoMovimentiPersonaliTest {

    private Movimento carico;
    private Movimento scarico;
    private MovimentoController controller;

    @BeforeEach
    void SetUp(){
        carico = new Movimento(1, 30, new Date(), "Carico", "OPE-001", "P-001", "Occhiali");
        scarico = new Movimento(2, 20, new Date(), "Scarico", "OPE-002", "P-002", "Guanti");
        controller = new MovimentoController();
    }

    @Test
    void testCaricoHaTipoCorretto(){
        assertEquals("Carico", carico.getTipoMovimento());
    }

    @Test
    void testScaricoHaTipoCorretto(){
        assertEquals("Scarico", scarico.getTipoMovimento());
    }

    @Test
    void testCaricoHaIdOperatoreCorretto(){
        assertEquals("OPE-001", carico.getIdOperatore());
    }

    @Test
    void testScaricoHaIdOperatoreCorretto(){
        assertEquals("OPE-002", scarico.getIdOperatore());
    }

    @Test
    void testCaricoHaNomeProdottoCorretto(){
        assertEquals("Occhiali", carico.getNomeProdotto());
    }

    @Test
    void testScaricoHaNomeProdottoCorretto(){
        assertEquals("Guanti", scarico.getNomeProdotto());
    }

    @Test
    void testCaricoHaQtaCorretta(){
        assertEquals(30, carico.getQtaProdotto());
    }

    @Test
    void testScaricoHaQtaCorretta(){
        assertEquals(20, scarico.getQtaProdotto());
    }

    @Test
    void testCaricoHaDataNonNulla(){
        assertNotNull(carico.getData());
    }

    @Test
    void testScaricoHaDataNonNulla(){
        assertNotNull(scarico.getData());
    }

    @Test
    void testMovimentoIdCorretto(){
        assertEquals(1, carico.getIdMovimento());
        assertEquals(2, scarico.getIdMovimento());
    }

    // test con controller - id nullo/vuoto

    @Test
    void testStoricoPersonaleConIdNulloRitornaListaVuota(){
        List<Movimento> risultato = controller.getStoricoPersonale(null);
        assertNotNull(risultato);
        assertTrue(risultato.isEmpty());
    }

    @Test
    void testStoricoPersonaleConIdVuotoRitornaListVuota(){
        List<Movimento> risultato = controller.getStoricoPersonale("");
        assertNotNull(risultato);
        assertTrue(risultato.isEmpty());
    }

    @Test
    void testSetterTipoMovimentoAggiorna(){
        carico.setTipoMovimento("Scarico");
        assertEquals("Scarico", carico.getTipoMovimento());
    }

    @Test
    void testSetterAggiornaQta(){
        scarico.setQtaProdotto(50);
        assertEquals(50, scarico.getQtaProdotto());
    }

}
