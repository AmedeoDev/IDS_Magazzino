package it.unina.magazzino;

import it.unina.magazzino.boundary.HomePage;

import javax.swing.*;

public class Main {

    public static void main(String[] args) throws Exception{

        // setLookAndFeel permette all'applicazione di usare il design del SO ospitante
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        SwingUtilities.invokeLater(() -> new HomePage().setVisible(true));
    }
}
