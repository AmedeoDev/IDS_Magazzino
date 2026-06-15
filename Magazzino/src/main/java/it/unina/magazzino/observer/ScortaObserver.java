package it.unina.magazzino.observer;

import it.unina.magazzino.entity.Prodotto;

/**
 * Interfaccia Observer per il sistema di notifiche sotto scorta.
 * Implementata da tutte le classi che devono essere modificate
 * quando un prodotto scende al di sotto della soglia minima.
 * */

public interface ScortaObserver {

    /**
     * Metodo invocato quando un prodotto risulta sotto sccorta
     * @param prodotto il prodotto che è sceso al di sotto della soglia minima
     * */
    void onProdottoSottoScorta(Prodotto prodotto);
}
