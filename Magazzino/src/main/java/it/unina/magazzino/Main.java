package it.unina.magazzino;

import it.unina.magazzino.boundary.HomePage;

import javax.swing.*;

public class Main {

    public static void main(String[] args){

        // inokeLater fa si che la nostra UI sia creata su un thread ad esso dedicato

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored){}
        });

        new HomePage();
    }
}
