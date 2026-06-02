package it.unina.magazzino.observer;


import it.unina.magazzino.entity.Prodotto;

public class NotificaScortaObserver implements  ScortaObserver{

    @Override
    public void onProdottoSottoScorta(Prodotto prodotto){
        String alert = "[ATTENZIONE] " + prodotto.getNome() +
                " è attualmente sotto scorta! Scorta rimanente: " + prodotto.getQtaDisponibile() +
                " unità. La soglia minima è: " + prodotto.getSogliaMinima();

        System.out.println("<<< [SISTEMA NOTIFICHE] >>> " + alert);
    }
}
