package it.unina.magazzino.entity;

import it.unina.magazzino.observer.NotificaScortaObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NotificaSottoScortaTest {

    private Prodotto prodottoConSoglia;
    private Prodotto prodottoNoSoglia;
    private NotificaScortaObserver observer;

    @BeforeEach
    void SetUp(){
        prodottoConSoglia = new Prodotto("P-001", "Sicurezza", "Guanti Latex", "Guanti monouso",
                15, 5, "POS-A1", "RESP-001");
        prodottoNoSoglia = new Prodotto("P-002", "Logistica", "Pallets",
                "Pallets Standard", 20, null, "POS-A2", "RESP-002");
        observer = new NotificaScortaObserver();
    }

    @Test
    void testNotificaNonParteSeQtaSopraScorta(){
        assertFalse(prodottoConSoglia.isSottoScorta());
    }

    @Test
    void testNotificaParteDopoScaricoChePortaSottoScorta(){
        prodottoConSoglia.scarica(11);
        assertTrue(prodottoConSoglia.isSottoScorta());
    }

    @Test
    void testNotificaNonParteSenzaSoglia(){
        prodottoNoSoglia.scarica(19);
        assertFalse(prodottoNoSoglia.isSottoScorta());
    }

    @Test
    void testNotificaNonScattaDopoCheTornaSopraLaSoglia(){
        prodottoConSoglia.scarica(11);
        assertTrue(prodottoConSoglia.isSottoScorta());
        prodottoConSoglia.carica(12);
        assertFalse(prodottoConSoglia.isSottoScorta());
    }

    // test che coinvolgono observer

    @Test
    void testObserverEsegueSenzaEccezioniSuProdottoSottoScorta(){
        prodottoConSoglia.scarica(11);
        assertDoesNotThrow(() -> observer.onProdottoSottoScorta(prodottoConSoglia));
    }

    @Test
    void testObserverEsegueSenzaEccezioniAncheSeQtaZero(){
        prodottoConSoglia.scarica(15);
        assertTrue(prodottoConSoglia.isEmpty());
        assertDoesNotThrow(() -> observer.onProdottoSottoScorta(prodottoConSoglia));
    }

    @Test
    void testObserverRiceveNomeCorrettoDelProdotto(){
        prodottoConSoglia.scarica(11);
        assertEquals("Guanti Latex", prodottoConSoglia.getNome());
        assertTrue(prodottoConSoglia.isSottoScorta());
    }

    @Test
    void testObserverRiceveSogliaCorrettaDelProdotto(){
        assertEquals(5, prodottoConSoglia.getSogliaMinima());
    }
}
