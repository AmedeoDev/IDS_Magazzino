package it.unina.magazzino.boundary;

import it.unina.magazzino.boundary.LoginPage;
import it.unina.magazzino.boundary.RegistrationPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;


public class HomePage extends JFrame {

    // palette colori --> deprecato, per gli altri file si fa uso del file apposito "StyleWMS"
    private static final Color BLU_ACCIAIO   = new Color(0x1B, 0x4F, 0x8A);
    private static final Color BLU_MEDIO     = new Color(0x2E, 0x6D, 0xB4);
    private static final Color AZZURRO_LIGHT = new Color(0xD6, 0xE4, 0xF0);
    private static final Color GRIGIO_NEUTRO = new Color(0xF4, 0xF6, 0xF8);
    private static final Color ANTRACITE     = new Color(0x2C, 0x3E, 0x50);
    private static final Color BIANCO        = Color.WHITE;
    private static final Color GRIGIO_TESTO  = new Color(0xAA, 0xAA, 0xAA);

    public HomePage() {
        setTitle("WMS — Benvenuto");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(420, 480);
        setLocationRelativeTo(null);
        getContentPane().setBackground(GRIGIO_NEUTRO);
        setLayout(new GridBagLayout());

        // card centrale con angoli arrotondati su sfondo grigio
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BIANCO);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 24, 24));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(340, 380));

        // header blu in cima alla card con arrotondamento solo in alto
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BLU_ACCIAIO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 20, 24, 24);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(32, 32, 36, 32));

        JLabel badge = new JLabel("WMS");
        badge.setFont(new Font("SansSerif", Font.PLAIN, 11));
        badge.setForeground(AZZURRO_LIGHT);
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD6, 0xE4, 0xF0, 80), 1, true),
                new EmptyBorder(3, 12, 3, 12)
        ));

        JLabel titolo = new JLabel("Benvenuto");
        titolo.setFont(new Font("Serif", Font.BOLD, 32));
        titolo.setForeground(BIANCO);
        titolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sottotitolo = new JLabel("Warehouse Management System");
        sottotitolo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sottotitolo.setForeground(new Color(0xD6, 0xE4, 0xF0, 180));
        sottotitolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(badge);
        header.add(Box.createVerticalStrut(12));
        header.add(titolo);
        header.add(Box.createVerticalStrut(6));
        header.add(sottotitolo);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(24, 28, 28, 28));

        // pulsante "secondario" (false) per andare al login
        RoundedButton btnAccedi = new RoundedButton("Accedi", false); // (per l'aspetto visivo del bottone, riga 205): primary == false -> Accedi
        btnAccedi.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAccedi.addActionListener(e -> onAccedi()); //per sapere quando il bottone viene premuto, "e" contiene info
        //sull'evento, tipo timestamp... nel nostro caso non viene usato perchè ci interessa sapere solo se il click è avvenuto

        JPanel sep = buildSeparator();
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);

        // pulsante "primario" (true) per andare alla registrazione
        RoundedButton btnRegistrati = new RoundedButton("Registrati", true); //(per l'aspetto visivo del bottone, riga 205): primary == true -> Registrati
        btnRegistrati.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegistrati.addActionListener(e -> onRegistrati()); //per sapere quando il bottone viene premuto, "e" contiene info
        //sull'evento, tipo timestamp... nel nostro caso non viene usato perchè ci interessa sapere solo se il click è avvenuto

        body.add(btnAccedi);
        body.add(Box.createVerticalStrut(10));
        body.add(sep);
        body.add(Box.createVerticalStrut(10));
        body.add(btnRegistrati);

        card.add(header, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(2, 2, 2, 2));
        wrapper.add(card);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        add(wrapper, gbc);
    }

    private JPanel buildSeparator() {
        //separatore tra accedi e registrati
        JPanel p = new JPanel(new GridBagLayout()) {
            @Override public Dimension getPreferredSize() {
                return new Dimension(280, 20);
            }
            @Override public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, 20);
            }
        };
        p.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();

        JSeparator left = new JSeparator();
        left.setForeground(AZZURRO_LIGHT);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        p.add(left, c);

        JLabel lbl = new JLabel("  oppure  ");
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(GRIGIO_TESTO);
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        p.add(lbl, c);

        JSeparator right = new JSeparator();
        right.setForeground(AZZURRO_LIGHT);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        p.add(right, c);

        return p;
    }

    private void onAccedi() {
        // chiude la HomePage e apre la LoginPage
        this.dispose();
        LoginPage loginPage = new LoginPage();
        loginPage.setVisible(true);
    }

    private void onRegistrati() {
        // chiude la HomePage e apre la RegistrationPage
        this.dispose();
        RegistrationPage registrationPage = new RegistrationPage();
        registrationPage.setVisible(true);
    }

    static class RoundedButton extends JButton {
        private final boolean primary; //CONVENZIONE USATA (per l'aspetto visivo del bottone, riga 205): primary == true -> Registrati; primary == false -> Accedi
        private boolean hovered = false;

        RoundedButton(String text, boolean primary) {
            super(text);
            this.primary = primary;
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font("SansSerif", Font.PLAIN, 14));
            setPreferredSize(new Dimension(284, 46));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setForeground(primary ? Color.WHITE : BLU_ACCIAIO);

            //per fare in modo che quando passa il mouse il colore cambia leggermente dobbiamo "ascoltare" la posizione del mopuse
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            //parte grafica dei due bottoni:
            if (primary) { //primary == true -> Registrati
                // sfondo blu più chiaro al passaggio del mouse
                g2.setColor(hovered ? BLU_MEDIO : BLU_ACCIAIO);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            } else { //primary == false -> Accedi
                // sfondo leggermente colorato all'hover
                g2.setColor(hovered ? GRIGIO_NEUTRO : BIANCO);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(hovered ? BLU_MEDIO : AZZURRO_LIGHT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0.75f, 0.75f, getWidth()-1.5f, getHeight()-1.5f, 12, 12));
            }

            // disegna il testo centrato manualmente
            FontMetrics fm = g2.getFontMetrics(getFont());
            int x = (getWidth()  - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.setColor(getForeground());
            g2.setFont(getFont());
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new HomePage().setVisible(true);
        });
    }
}