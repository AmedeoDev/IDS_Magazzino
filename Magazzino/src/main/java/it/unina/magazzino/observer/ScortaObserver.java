package it.unina.magazzino.observer;

import it.unina.magazzino.entity.Prodotto;

public interface ScortaObserver {

    void onProdottoSottoScorta(Prodotto prodotto);
}
