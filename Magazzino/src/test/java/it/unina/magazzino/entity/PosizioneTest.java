package it.unina.magazzino.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PosizioneTest {

    private Posizione posizione;
    private Prodotto prodotto;

    @BeforeEach
    void setUp(){
        posizione = new Posizione("POS-A1", "Area A", "Scaffale 1");
        prodotto = new Prodotto("P-001", "medicina", "nurofen", "compresse orosolubili",
                20, 5, "POS-A1", "RESP-001");
    }

    @Test
    void testPosizioneLibera(){
        assertTrue(posizione.isLibero());
    }

    @Test
    void testDepositaProdottoInPosizioneOccupata(){
        posizione.depositaProdotto(prodotto);
        assertFalse(posizione.isLibero());
    }

    @Test
    void testDepositaProdottoNulloEccezione(){
        assertThrows(IllegalArgumentException.class, () -> posizione.depositaProdotto(null));
    }

    @Test
    void testDepositoSuPosizioneOccupataEccezione(){
        posizione.depositaProdotto(prodotto);
        Prodotto nProd = new Prodotto("P-002", "strumenti da cucina", "mestolo", "mestolo in legno",
                20, 5, "POS-A1", "RESP-001");
        assertThrows(IllegalArgumentException.class, () -> posizione.depositaProdotto(nProd));
    }

    @Test
    void testLiberaPosizioneRestituisceProdotto(){
        posizione.depositaProdotto(prodotto);
        posizione.liberaPosizione();
        assertTrue(posizione.isLibero());
    }

    @Test
    void testPosizioneGiaLiberaEccezione(){
        assertThrows(IllegalStateException.class, () -> posizione.liberaPosizione());
    }

    @Test
    void testCostruttoreVuotoEccezione(){
        assertThrows(IllegalArgumentException.class, () -> new Posizione(
                "", "Area 1", "Scaffale 1"
        ));
    }

    @Test
    void testCostruttoreAreaVuotaEccezione(){
        assertThrows(IllegalArgumentException.class, () -> new Posizione(
                "POS-A1", "", "Scaffale 1"
        ));
    }

}
