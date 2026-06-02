package org.example;

import it.unina.magazzino.boundary.HomePage;

// il main sarà usato esclusivamente per caricare la home page, il resto è demandato
// a tutte le altri classi


public class Main {
    static void main() {

        HomePage homePage = new HomePage();
        homePage.setVisible(true);
    }
}
