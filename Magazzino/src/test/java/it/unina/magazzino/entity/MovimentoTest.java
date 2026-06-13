package it.unina.magazzino.entity;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MovimentoTest {

    @Test
    void testCostruttorConValoriCorretti(){
        Date now = new Date();
        Movimento movimento = new Movimento(10, now, "Carico", "P-001", "OPE-001");
        assertEquals(10, movimento.getQtaProdotto());
        assertEquals("Carico", movimento.getTipoMovimento());
        assertEquals("P-001", movimento.getIdProdotto());
        assertEquals("OPE-001", movimento.getIdOperatore());
    }

    @Test
    void testCostruttoreCompletoConValoriCorretti(){
        Date now = new Date();
        Movimento movimento = new Movimento(10, 5, now, "Scarico", "P-001", "OPE-001", "Elmetti");
        assertEquals(10, movimento.getIdMovimento());
        assertEquals("Elmetti", movimento.getNomeProdotto());
        assertEquals("Scarico", movimento.getTipoMovimento());
    }

    @Test
    void testAggiornaTipoMovimento(){
        Movimento movimento = new Movimento(5, new Date(), "Carico", "P-002", "OPE-002");
        movimento.setTipoMovimento("Scarico");
        assertEquals("Scarico", movimento.getTipoMovimento());
    }

}
