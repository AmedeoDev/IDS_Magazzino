package it.unina.magazzino.boundary;

import it.unina.magazzino.boundary.utils.StyleWMS;

import it.unina.magazzino.control.LoginController;

import it.unina.magazzino.entity.Operatore;
import it.unina.magazzino.entity.Responsabile;
import it.unina.magazzino.entity.Utente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginPage extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnAccedi;

    public LoginPage() {

        setTitle("WMS PRO – Login");
        setSize(420, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Sfondo generale
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

        JLabel sub = new JLabel("Accedi al tuo account", SwingConstants.CENTER);
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
        form.setBorder(new EmptyBorder(35, 50, 25, 50));

        form.add(label("E-mail"));
        form.add(Box.createVerticalStrut(5));
        txtEmail = new JTextField();
        stilizza(txtEmail);
        form.add(txtEmail);

        form.add(Box.createVerticalStrut(18));

        form.add(label("Password"));
        form.add(Box.createVerticalStrut(5));
        txtPassword = new JPasswordField();
        stilizza(txtPassword);
        form.add(txtPassword);

        form.add(Box.createVerticalStrut(28));

        // Bottone con Animazione Hover
        btnAccedi = new JButton("Accedi") {
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

                // Sfondo dinamico al passaggio del mouse
                g2.setColor(hovered ? StyleWMS.BLU_ACCIAIO : StyleWMS.BLU_MEDIO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnAccedi.setContentAreaFilled(false);
        btnAccedi.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAccedi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnAccedi.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAccedi.setBackground(StyleWMS.BLU_MEDIO);
        btnAccedi.setForeground(StyleWMS.BIANCO);
        btnAccedi.setFocusPainted(false);
        btnAccedi.setBorderPainted(false);
        btnAccedi.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        form.add(btnAccedi);

        form.add(Box.createVerticalStrut(15));

        // Frase di reindirizzamento alla registrazione
        JPanel pnlRegistrati = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        pnlRegistrati.setBackground(StyleWMS.BIANCO);
        pnlRegistrati.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlRegistrati.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JLabel lblFrase = new JLabel("Non hai un account? ");
        lblFrase.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblFrase.setForeground(StyleWMS.GRIGIO_TESTO);

        JLabel lblLink = new JLabel("<html><u>Registrati</u></html>");
        lblLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLink.setForeground(StyleWMS.BLU_MEDIO);
        lblLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect e click per il link
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
                new RegistrationPage().setVisible(true);
            }
        });

        pnlRegistrati.add(lblFrase);
        pnlRegistrati.add(lblLink);
        form.add(pnlRegistrati);

        // Assemblaggio
        root.add(header, BorderLayout.NORTH);
        root.add(form,   BorderLayout.CENTER);
        setContentPane(root);

        // Listener Login
        getRootPane().setDefaultButton(btnAccedi);
        btnAccedi.addActionListener(e -> {
            String email = txtEmail.getText();
            String password = new String(txtPassword.getPassword());

            LoginController loginController = new LoginController();
            try {
                Utente utenteLoggato = loginController.effettuaLogin(email, password);
                this.dispose();
                if(utenteLoggato instanceof Operatore){
                    DashboardOperatore dashboardOperatore = new DashboardOperatore((Operatore)utenteLoggato, "");
                    dashboardOperatore.setVisible(true);
                } else if (utenteLoggato instanceof Responsabile){
                    JOptionPane.showMessageDialog(null,
                            "Benvenuto responsabile: " + utenteLoggato.getNome() + " " + utenteLoggato.getCognome(),
                         "Accesso Riuscito",
                            JOptionPane.INFORMATION_MESSAGE);
                    DashboardResponsabile dashboardResponsabile = new DashboardResponsabile((Responsabile)utenteLoggato, "");
                    dashboardResponsabile.setVisible(true);
                }
            } catch (Exception ex){
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Errore di autenticazione",
                        JOptionPane.ERROR_MESSAGE);
            }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}