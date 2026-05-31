package it.unina.magazzino.boundary;

import it.unina.magazzino.boundary.utils.StyleWMS;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        setSize(420, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        /* ── Sfondo generale ── */
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(StyleWMS.BIANCO);

        // Header con BLU_ACCIAIO
        JPanel header = new JPanel();
        header.setBackground(StyleWMS.BLU_ACCIAIO);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(30, 0, 30, 0));

        JLabel titolo = new JLabel("WMS PRO", SwingConstants.CENTER);
        titolo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titolo.setForeground(StyleWMS.BIANCO);
        titolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Crea il tuo account", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(StyleWMS.AZZURRO_LIGHT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titolo);
        header.add(Box.createVerticalStrut(6));
        header.add(sub);

        // Form centrale
        JPanel form = new JPanel();
        form.setBackground(StyleWMS.BIANCO);
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
        cmbRuolo.setBackground(StyleWMS.BIANCO);
        form.add(cmbRuolo);

        form.add(Box.createVerticalStrut(24));

        // Bottone con Animazione Hover
        btnRegistrati = new JButton("Registrati") {
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

                // Sfondo dinamico: BLU_ACCIAIO al passaggio del mouse
                g2.setColor(hovered ? StyleWMS.BLU_ACCIAIO : StyleWMS.BLU_MEDIO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnRegistrati.setContentAreaFilled(false);
        btnRegistrati.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegistrati.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnRegistrati.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrati.setBackground(StyleWMS.BLU_MEDIO);
        btnRegistrati.setForeground(StyleWMS.BIANCO);
        btnRegistrati.setFocusPainted(false);
        btnRegistrati.setBorderPainted(false);
        btnRegistrati.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        form.add(btnRegistrati);

        form.add(Box.createVerticalStrut(15));

        // Frase di reindirizzamento al Login
        JPanel pnlLogin = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlLogin.setBackground(StyleWMS.BIANCO);
        pnlLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JLabel lblFrase = new JLabel("Hai già un account? ");
        lblFrase.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblFrase.setForeground(StyleWMS.GRIGIO_TESTO);

        JLabel lblLink = new JLabel("<html><u>Effettua il Login</u></html>");
        lblLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLink.setForeground(StyleWMS.BLU_MEDIO);
        lblLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect e click per il link di login
        lblLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                lblLink.setForeground(StyleWMS.BLU_ACCIAIO);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lblLink.setForeground(StyleWMS.BLU_MEDIO);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginPage().setVisible(true);
            }
        });

        pnlLogin.add(lblFrase);
        pnlLogin.add(lblLink);
        form.add(pnlLogin);

        // Assemblaggio
        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(10);

        root.add(header, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        setContentPane(root);

        btnRegistrati.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Registrazione premuta!")
        );
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistrationPage().setVisible(true));
    }
}