package it.unina.magazzino.boundary;

import it.unina.magazzino.boundary.utils.StyleWMS;
import it.unina.magazzino.entity.Operatore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EffettuaOperazioni extends JFrame {

    private JButton btnCarico;
    private JButton btnScarico;
    private JButton btnDashboard;

    Operatore operatoreCorrente;

    public EffettuaOperazioni(Operatore operatore) {

        // salva l'operatore loggato per passarlo alle schermate successive
        this.operatoreCorrente = operatore;
        setTitle("WMS PRO – Gestione Movimenti");
        setSize(440, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(StyleWMS.BIANCO);

        JPanel header = new JPanel();
        header.setBackground(StyleWMS.BLU_ACCIAIO);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(25, 0, 25, 0));

        JLabel titolo = new JLabel("WMS PRO", SwingConstants.CENTER);
        titolo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titolo.setForeground(StyleWMS.BIANCO);
        titolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Seleziona Tipo di Movimento", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(StyleWMS.AZZURRO_LIGHT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titolo);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);

        JPanel body = new JPanel();
        body.setBackground(StyleWMS.BIANCO);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(40, 50, 30, 50));

        // bottone carico: cambia colore al passaggio del mouse
        btnCarico = new JButton("REGISTRA CARICO") {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? StyleWMS.BLU_ACCIAIO : StyleWMS.BLU_MEDIO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCarico.setContentAreaFilled(false);
        btnCarico.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCarico.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnCarico.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCarico.setForeground(StyleWMS.BIANCO);
        btnCarico.setFocusPainted(false);
        btnCarico.setBorderPainted(false);
        btnCarico.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        body.add(btnCarico);

        body.add(Box.createVerticalStrut(20));

        // bottone scarico: stesso stile del carico
        btnScarico = new JButton("REGISTRA SCARICO") {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? StyleWMS.BLU_ACCIAIO : StyleWMS.BLU_MEDIO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnScarico.setContentAreaFilled(false);
        btnScarico.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnScarico.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnScarico.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnScarico.setForeground(StyleWMS.BIANCO);
        btnScarico.setFocusPainted(false);
        btnScarico.setBorderPainted(false);
        btnScarico.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        body.add(btnScarico);

        body.add(Box.createVerticalStrut(45));

        // bottone secondario con bordo: porta alla dashboard senza registrare nulla
        btnDashboard = new JButton("Torna alla Dashboard") {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? StyleWMS.GRIGIO_NEUTRO : StyleWMS.BIANCO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(StyleWMS.AZZURRO_LIGHT);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnDashboard.setContentAreaFilled(false);
        btnDashboard.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDashboard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnDashboard.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDashboard.setForeground(StyleWMS.BLU_ACCIAIO);
        btnDashboard.setFocusPainted(false);
        btnDashboard.setBorderPainted(false);
        btnDashboard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        body.add(btnDashboard);

        root.add(header, BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);
        setContentPane(root);

        // apre la schermata di registrazione carico passando l'operatore corrente
        btnCarico.addActionListener(e -> {
            this.dispose();
            new RegistraCarico(this.operatoreCorrente).setVisible(true);
        });

        // apre la schermata di registrazione scarico passando l'operatore corrente
        btnScarico.addActionListener(e -> {
            this.dispose();
            new RegistraScarico(this.operatoreCorrente).setVisible(true);
        });

        // torna alla dashboard dell'operatore senza registrare alcun movimento
        btnDashboard.addActionListener(e -> {
            this.dispose();
            DashboardOperatore dashboardOperatore = new DashboardOperatore(this.operatoreCorrente, "resources/assets/logoFinale.png");
            dashboardOperatore.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Operatore opTest = new Operatore(
                    "Amedeo", "catanese", "am@mail.it", "testpw", "TEST-002"
            );
            new EffettuaOperazioni(opTest).setVisible(true);
        });
    }
}