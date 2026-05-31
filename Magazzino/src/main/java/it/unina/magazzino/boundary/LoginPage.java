package it.unina.magazzino.boundary;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginPage extends JFrame {

    private JTextField     txtEmail;
    private JPasswordField txtPassword;
    private JButton        btnAccedi;

    public LoginPage() {

        setTitle("WMS PRO – Login");
        setSize(420, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Sfondo generale
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

        JLabel sub = new JLabel("Accedi al tuo account", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(210, 230, 255));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titolo);
        header.add(Box.createVerticalStrut(6));
        header.add(sub);

        //Form centrale
        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(35, 50, 35, 50));

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

        btnAccedi = new JButton("Accedi") {
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
        btnAccedi.setContentAreaFilled(false); // Evita il rettangolo standard di sfondo
        btnAccedi.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAccedi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnAccedi.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAccedi.setBackground(new Color(30, 136, 229));
        btnAccedi.setForeground(Color.BLACK);
        btnAccedi.setFocusPainted(false);
        btnAccedi.setBorderPainted(false);
        btnAccedi.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        form.add(btnAccedi);

        // Assemblaggio
        root.add(header, BorderLayout.NORTH);
        root.add(form,   BorderLayout.CENTER);
        setContentPane(root);

        //Listener (solo estetico: mostra dialog)
        getRootPane().setDefaultButton(btnAccedi);
        btnAccedi.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Login premuto!")
        );
    }

    // Label corsiva sopra ogni campo
    private JLabel label(String testo) {
        JLabel l = new JLabel(testo);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(60, 60, 60));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // Stile uniforme per TextField e PasswordField
    private void stilizza(JTextField c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}