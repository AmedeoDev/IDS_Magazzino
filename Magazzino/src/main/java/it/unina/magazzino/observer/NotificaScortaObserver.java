package it.unina.magazzino.observer;


import it.unina.magazzino.entity.Prodotto;

/**
 * Implementazione concreta di ScortaObserver
 * Genera un messaggio di allerta in console quando un prodotto
 * scende sotto la soglia minima di scorta
 * */

public class NotificaScortaObserver implements  ScortaObserver{

    /**
     * Stampa un messaggio di allerta con i dettagli del prodotto sotto scorta
     * @param prodotto prodotto che è sceso al di sotto della soglia
     * */

    @Override
    public void onProdottoSottoScorta(Prodotto prodotto){
        String alert = "[ATTENZIONE] " + prodotto.getNome() +
                " è attualmente sotto scorta! Scorta rimanente: " + prodotto.getQtaDisponibile() +
                " unità. La soglia minima è: " + prodotto.getSogliaMinima();

        System.out.println("<<< [SISTEMA NOTIFICHE] >>> " + alert);
    }
}
