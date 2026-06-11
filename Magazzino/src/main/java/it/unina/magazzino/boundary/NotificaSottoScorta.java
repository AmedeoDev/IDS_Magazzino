package it.unina.magazzino.boundary;

import it.unina.magazzino.boundary.utils.StyleWMS;
import it.unina.magazzino.entity.Prodotto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class NotificaSottoScorta extends JDialog {

    private static final int DURATA_SECONDI = 5;
    private JLabel lblCountdown;
    private Timer timer;
    private int secondiRimasti = DURATA_SECONDI;

    public NotificaSottoScorta(JFrame parent, List<Prodotto> prodottiCritici) {
        super(parent, "⚠ Notifica Scorte", false); // false = non bloccante
        setUndecorated(true);
        setSize(420, Math.min(120 + prodottiCritici.size() * 36, 400));
        setLocationRelativeTo(parent);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(StyleWMS.BIANCO);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(new Color(198, 40, 40, 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 14, 14));
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(16, 18, 16, 18));

        // ── Header ──
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titoloRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titoloRow.setOpaque(false);

        JLabel icona = new JLabel("⚠");
        icona.setFont(new Font("SansSerif", Font.BOLD, 16));
        icona.setForeground(new Color(198, 40, 40));

        JLabel titolo = new JLabel("Prodotti sotto scorta");
        titolo.setFont(new Font("SansSerif", Font.BOLD, 14));
        titolo.setForeground(new Color(198, 40, 40));

        titoloRow.add(icona);
        titoloRow.add(titolo);

        // Countdown + bottone chiudi
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);

        lblCountdown = new JLabel("(" + DURATA_SECONDI + "s)");
        lblCountdown.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblCountdown.setForeground(StyleWMS.GRIGIO_TESTO);

        JButton btnChiudi = new JButton("✕") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220, 220, 220));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnChiudi.setFont(new Font("SansSerif", Font.BOLD, 10));
        btnChiudi.setForeground(StyleWMS.GRIGIO_TESTO);
        btnChiudi.setPreferredSize(new Dimension(22, 22));
        btnChiudi.setContentAreaFilled(false);
        btnChiudi.setBorderPainted(false);
        btnChiudi.setFocusPainted(false);
        btnChiudi.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnChiudi.addActionListener(e -> chiudi());

        rightPanel.add(lblCountdown);
        rightPanel.add(btnChiudi);

        header.add(titoloRow,  BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        // ── Lista prodotti ──
        JPanel lista = new JPanel();
        lista.setOpaque(false);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBorder(new EmptyBorder(10, 0, 0, 0));

        for (Prodotto p : prodottiCritici) {
            JPanel riga = new JPanel(new BorderLayout(8, 0));
            riga.setOpaque(false);
            riga.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            riga.setBorder(new EmptyBorder(2, 0, 2, 0));

            JLabel nome = new JLabel("• " + p.getNome());
            nome.setFont(new Font("SansSerif", Font.PLAIN, 12));
            nome.setForeground(StyleWMS.ANTRACITE);

            JLabel scorta = new JLabel(
                    "Disp: " + p.getQtaDisponibile() + "  |  Soglia: " + p.getSogliaMinima());
            scorta.setFont(new Font("SansSerif", Font.BOLD, 11));
            scorta.setForeground(new Color(198, 40, 40));

            riga.add(nome,   BorderLayout.WEST);
            riga.add(scorta, BorderLayout.EAST);
            lista.add(riga);
        }

        // ── Barra progresso countdown ──
        JProgressBar progressBar = new JProgressBar(0, DURATA_SECONDI * 10) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 240, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                int w = (int)((double) getValue() / getMaximum() * getWidth());
                g2.setColor(new Color(198, 40, 40));
                g2.fillRoundRect(0, 0, w, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        progressBar.setValue(DURATA_SECONDI * 10);
        progressBar.setPreferredSize(new Dimension(0, 4));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        progressBar.setBorderPainted(false);
        progressBar.setOpaque(false);

        root.add(header,      BorderLayout.NORTH);
        root.add(lista,       BorderLayout.CENTER);
        root.add(progressBar, BorderLayout.SOUTH);

        setContentPane(root);
        setBackground(new Color(0, 0, 0, 0));

        // ── Timer countdown ──
        timer = new Timer(100, null);
        final int[] tick = {DURATA_SECONDI * 10};
        timer.addActionListener(e -> {
            tick[0]--;
            progressBar.setValue(tick[0]);
            if (tick[0] % 10 == 0) {
                secondiRimasti = tick[0] / 10;
                lblCountdown.setText("(" + secondiRimasti + "s)");
            }
            if (tick[0] <= 0) chiudi();
        });

        // Posiziona in basso a destra rispetto al parent
        if (parent != null) {
            int x = parent.getX() + parent.getWidth()  - getWidth()  - 24;
            int y = parent.getY() + parent.getHeight() - getHeight() - 60;
            setLocation(x, y);
        }
    }

    private void chiudi() {
        if (timer != null) timer.stop();
        dispose();
    }

    public void mostra() {
        setVisible(true);
        timer.start();
    }
}