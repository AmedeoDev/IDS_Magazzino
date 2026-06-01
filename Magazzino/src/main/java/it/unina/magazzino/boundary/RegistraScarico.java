package it.unina.magazzino.boundary;

import it.unina.magazzino.boundary.utils.StyleWMS;
import it.unina.magazzino.entity.Operatore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegistraScarico extends JFrame {

    private JTextField txtIdProdotto;
    private JTextField txtQuantita;
    private JButton btnConferma;
    private JButton btnDashboard; // Rinominato per coerenza e per evitare conflitti

    private Operatore operatoreLoggato;

    public RegistraScarico(Operatore operatore) {

        this.operatoreLoggato = operatore;

        setTitle("WMS PRO – Movimento di Scarico");
        setSize(420, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(StyleWMS.BIANCO);

        /* ── Header ── */
        JPanel header = new JPanel();
        header.setBackground(StyleWMS.BLU_ACCIAIO);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(25, 0, 25, 0));

        JLabel titolo = new JLabel("WMS PRO", SwingConstants.CENTER);
        titolo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titolo.setForeground(StyleWMS.BIANCO);
        titolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Registra Operazione di Scarico", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(StyleWMS.AZZURRO_LIGHT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titolo);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);

        /* ── Form ── */
        JPanel form = new JPanel();
        form.setBackground(StyleWMS.BIANCO);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(30, 50, 30, 50));

        form.add(label("ID Prodotto"));
        form.add(Box.createVerticalStrut(5));
        txtIdProdotto = new JTextField();
        stilizza(txtIdProdotto);
        form.add(txtIdProdotto);

        form.add(Box.createVerticalStrut(18));

        form.add(label("Quantità da prelevare"));
        form.add(Box.createVerticalStrut(5));
        txtQuantita = new JTextField();
        stilizza(txtQuantita);
        form.add(txtQuantita);

        form.add(Box.createVerticalStrut(30));

        // Bottone Conferma
        btnConferma = new JButton("Conferma Scarico") {
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
        btnConferma.setContentAreaFilled(false);
        btnConferma.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnConferma.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnConferma.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConferma.setBackground(StyleWMS.BLU_MEDIO);
        btnConferma.setForeground(StyleWMS.BIANCO);
        btnConferma.setFocusPainted(false);
        btnConferma.setBorderPainted(false);
        btnConferma.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        form.add(btnConferma);

        form.add(Box.createVerticalStrut(15));

        // Bottone Dashboard
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
        btnDashboard.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDashboard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnDashboard.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDashboard.setForeground(StyleWMS.BLU_ACCIAIO);
        btnDashboard.setFocusPainted(false);
        btnDashboard.setBorderPainted(false);
        btnDashboard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        form.add(btnDashboard);

        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        setContentPane(root);

        // Listeners
        btnConferma.addActionListener(e -> {
            // Nota: coerentemente con UC12, qui andrà integrata la logica di "Verifica Disponibilità Scorte"
            JOptionPane.showMessageDialog(this, "Scarico registrato con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
            txtIdProdotto.setText("");
            txtQuantita.setText("");
        });

        btnDashboard.addActionListener(e -> {
            this.dispose();
            DashboardOperatore dashboardOperatore = new DashboardOperatore(this.operatoreLoggato, "resources/assets/logoFinale.png");
            dashboardOperatore.setVisible(true);
        });
    }

    private JLabel label(String testo) {
        JLabel l = new JLabel(testo);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(StyleWMS.ANTRACITE);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void stilizza(JTextField c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(StyleWMS.AZZURRO_LIGHT),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }
}