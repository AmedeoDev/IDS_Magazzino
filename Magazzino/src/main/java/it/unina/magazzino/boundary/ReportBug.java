package it.unina.magazzino.boundary;

import it.unina.magazzino.boundary.utils.StyleWMS;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ReportBug extends JFrame {

    private JTextField  txtTitolo;
    private JComboBox<String> cmbCategoria;
    private JTextArea   txtDescrizione;
    private JButton     btnInvia;

    public ReportBug() {

        setTitle("WMS PRO – Segnala un Bug");
        setSize(480, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(StyleWMS.BIANCO);

        /* ── Header blu ── */
        JPanel header = new JPanel();
        header.setBackground(StyleWMS.BLU_ACCIAIO);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(30, 0, 30, 0));

        JLabel titolo = new JLabel("WMS PRO", SwingConstants.CENTER);
        titolo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titolo.setForeground(StyleWMS.BIANCO);
        titolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Segnalazione Bug", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(StyleWMS.AZZURRO_LIGHT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titolo);
        header.add(Box.createVerticalStrut(6));
        header.add(sub);

        /* ── Form ── */
        JPanel form = new JPanel();
        form.setBackground(StyleWMS.BIANCO);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(28, 50, 28, 50));

        /* Titolo bug */
        form.add(label("Titolo"));
        form.add(Box.createVerticalStrut(5));
        txtTitolo = new JTextField();
        stilizzaCampo(txtTitolo);
        form.add(txtTitolo);

        form.add(Box.createVerticalStrut(14));

        // Categoria
        form.add(label("Categoria"));
        form.add(Box.createVerticalStrut(5));
        cmbCategoria = new JComboBox<>(new String[]{
                "-- Seleziona --", "Interfaccia grafica", "Accesso / Login",
                "Gestione prodotti", "Movimenti magazzino", "Notifiche", "Altro"
        });
        cmbCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbCategoria.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbCategoria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cmbCategoria.setBackground(StyleWMS.BIANCO);
        form.add(cmbCategoria);

        form.add(Box.createVerticalStrut(14));

        /* Descrizione */
        form.add(label("Descrizione del problema"));
        form.add(Box.createVerticalStrut(5));

        txtDescrizione = new JTextArea();
        txtDescrizione.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescrizione.setLineWrap(true);
        txtDescrizione.setWrapStyleWord(true);
        txtDescrizione.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(StyleWMS.AZZURRO_LIGHT),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JScrollPane scrollDesc = new JScrollPane(txtDescrizione);
        scrollDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollDesc.setPreferredSize(new Dimension(0, 180));
        scrollDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        scrollDesc.setBorder(BorderFactory.createLineBorder(StyleWMS.AZZURRO_LIGHT));
        form.add(scrollDesc);

        form.add(Box.createVerticalStrut(24));

        /* Bottone */
        btnInvia = new JButton("Invia segnalazione"){
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnInvia.setContentAreaFilled(false);
        btnInvia.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnInvia.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnInvia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnInvia.setBackground(StyleWMS.BLU_MEDIO);
        btnInvia.setForeground(StyleWMS.BIANCO); // Testo bianco per contrasto
        btnInvia.setFocusPainted(false);
        btnInvia.setBorderPainted(false);
        btnInvia.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        form.add(btnInvia);

        /* ── Assemblaggio ── */
        root.add(header, BorderLayout.NORTH);
        root.add(form,   BorderLayout.CENTER);
        setContentPane(root);

        /* ── Listener ── */
        btnInvia.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Segnalazione inviata!\nUn operatore la analizzerà il prima possibile.",
                    "Grazie!",
                    JOptionPane.INFORMATION_MESSAGE
            );
            txtTitolo.setText("");
            cmbCategoria.setSelectedIndex(0);
            txtDescrizione.setText("");
        });
    }

    private JLabel label(String testo) {
        JLabel l = new JLabel(testo);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(StyleWMS.ANTRACITE);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void stilizzaCampo(JTextField c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(StyleWMS.AZZURRO_LIGHT),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReportBug().setVisible(true));
    }
}