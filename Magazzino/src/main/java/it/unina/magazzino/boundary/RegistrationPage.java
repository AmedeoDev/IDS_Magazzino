package it.unina.magazzino.boundary;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegistrationPage extends JFrame {

    private JTextField     txtNome;
    private JTextField     txtCognome;
    private JTextField     txtEmail;
    private JPasswordField txtPassword;
    private JPasswordField txtConfermaPassword;
    private JComboBox<String> cmbRuolo;
    private JButton        btnRegistrati;

    public RegistrationPage() {

        setTitle("WMS PRO – Registrazione");
        setSize(420, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        /* ── Sfondo generale ── */
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // Header blu
        JPanel header = new JPanel();
        header.setBackground(new Color(30, 136, 229));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(30, 0, 30, 0));

        JLabel titolo = new JLabel("WMS PRO", SwingConstants.CENTER);
        titolo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titolo.setForeground(Color.WHITE);
        titolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Crea il tuo account", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(210, 230, 255));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titolo);
        header.add(Box.createVerticalStrut(6));
        header.add(sub);

        // Form centrale
        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(28, 50, 28, 50));

        form.add(label("Nome"));
        form.add(Box.createVerticalStrut(5));
        txtNome = new JTextField();
        stilizza(txtNome);
        form.add(txtNome);

        form.add(Box.createVerticalStrut(14));

        form.add(label("Cognome"));
        form.add(Box.createVerticalStrut(5));
        txtCognome = new JTextField();
        stilizza(txtCognome);
        form.add(txtCognome);

        form.add(Box.createVerticalStrut(14));

        form.add(label("E-mail"));
        form.add(Box.createVerticalStrut(5));
        txtEmail = new JTextField();
        stilizza(txtEmail);
        form.add(txtEmail);

        form.add(Box.createVerticalStrut(14));

        form.add(label("Password"));
        form.add(Box.createVerticalStrut(5));
        txtPassword = new JPasswordField();
        stilizza(txtPassword);
        form.add(txtPassword);

        form.add(Box.createVerticalStrut(14));

        form.add(label("Conferma Password"));
        form.add(Box.createVerticalStrut(5));
        txtConfermaPassword = new JPasswordField();
        stilizza(txtConfermaPassword);
        form.add(txtConfermaPassword);

        form.add(Box.createVerticalStrut(14));

        form.add(label("Ruolo"));
        form.add(Box.createVerticalStrut(5));
        cmbRuolo = new JComboBox<>(new String[]{"-- Seleziona --", "Operatore", "Responsabile"});
        cmbRuolo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbRuolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbRuolo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cmbRuolo.setBackground(Color.WHITE);
        form.add(cmbRuolo);

        form.add(Box.createVerticalStrut(24));

        btnRegistrati = new JButton("Registrati") {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            // 20 è il raggio dell'arco di curvatura
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.dispose();
            super.paintComponent(g);
        }
    };
        btnRegistrati.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegistrati.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnRegistrati.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrati.setBackground(new Color(30, 136, 229));
        btnRegistrati.setForeground(Color.BLACK);
        btnRegistrati.setFocusPainted(false);
        btnRegistrati.setBorderPainted(false);
        btnRegistrati.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        form.add(btnRegistrati);

        // Assemblaggio
        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(10);

        root.add(header, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        setContentPane(root);

        /* ── Listener (solo estetico: mostra dialog) ── */
        btnRegistrati.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Registrazione premuta!") //da sostituire con l'effettivo collegamento
        );
    }

    /* Label sopra ogni campo */
    private JLabel label(String testo) {
        JLabel l = new JLabel(testo);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(60, 60, 60));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    /* Stile uniforme per TextField e PasswordField */
    private void stilizza(JTextField c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistrationPage().setVisible(true));
    }
}