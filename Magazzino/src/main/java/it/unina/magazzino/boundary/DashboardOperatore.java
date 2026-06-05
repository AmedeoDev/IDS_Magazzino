package it.unina.magazzino.boundary;

import it.unina.magazzino.boundary.utils.StyleWMS;
import it.unina.magazzino.control.ProdottoController;
import it.unina.magazzino.entity.Operatore;
import it.unina.magazzino.entity.Prodotto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;

public class DashboardOperatore extends JFrame {

    private Operatore operatoreLoggato;

    public DashboardOperatore(Operatore operatore, String logoPath) {

        this.operatoreLoggato = operatore;
        String email = operatore.getEmail();
        String nomeUtente = operatore.getNome() + " " + operatore.getCognome();

        // questo è solo per testare la risposta della UI
        String password = operatore.getPassword();

        setTitle("Dashboard Operatore — " + nomeUtente);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 620);
        setMinimumSize(new Dimension(520, 500));
        setLocationRelativeTo(null);
        getContentPane().setBackground(StyleWMS.GRIGIO_NEUTRO);
        setLayout(new BorderLayout());

        add(buildTopbar(nomeUtente, logoPath), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(20, 16, 16, 16));

        // Etichetta sezione operazioni
        body.add(buildSectionLabel("Operazioni disponibili"));
        body.add(Box.createVerticalStrut(10));

        // Bottoni operazione (blu, in risalto)
        body.add(buildOperationsPanel());
        body.add(Box.createVerticalStrut(20));

        // Separatore + etichetta prodotti
        body.add(buildSectionLabel("Prodotti in magazzino"));
        body.add(Box.createVerticalStrut(10));

        ProdottoController controller = new ProdottoController();
        List<Prodotto> prodotti = controller.getAllProdotti();

        if(prodotti != null && !prodotti.isEmpty()){
            body.add(buildProductsGrid(prodotti));
        } else {
            JLabel vuoto = new JLabel("Nessun prodotto presente in magazzino");
            vuoto.setFont(new Font("SansSerif", Font.ITALIC, 12));
            vuoto.setForeground(StyleWMS.GRIGIO_TESTO);
            body.add(vuoto);
        }

        body.add(buildProductsGrid(prodotti));
        body.add(Box.createVerticalStrut(16));
        body.add(buildFooter());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        add(scroll, BorderLayout.CENTER);
    }

    // ── Topbar blu ────────────────────────────────────────────────
    private JPanel buildTopbar(String nomeUtente, String logoPath) {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(StyleWMS.BLU_ACCIAIO);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Sinistra: avatar + nome
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JPanel avatar = buildAvatar(nomeUtente);
        JPanel testi = new JPanel();
        testi.setOpaque(false);
        testi.setLayout(new BoxLayout(testi, BoxLayout.Y_AXIS));

        JLabel nome = new JLabel("Benvenuto, " + nomeUtente);
        nome.setFont(new Font("SansSerif", Font.BOLD, 13));
        nome.setForeground(StyleWMS.BIANCO);

        JLabel ruolo = new JLabel("Operatore di magazzino");
        ruolo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        ruolo.setForeground(new Color(0xD6, 0xE4, 0xF0));

        testi.add(nome);
        testi.add(ruolo);
        left.add(avatar);
        left.add(testi);

        // Destra: logo immagine oppure badge testuale
        JComponent logoWidget = buildLogoWidget(logoPath);

        bar.add(left, BorderLayout.WEST);
        bar.add(logoWidget, BorderLayout.EAST);
        return bar;
    }

    /** Tenta di caricare il logo da file; se fallisce usa il stampa un plain text. */
    private JComponent buildLogoWidget(String logoPath) {
        if (logoPath != null) {
            try {
                java.net.URL imgURL = getClass().getResource(logoPath);
                // Ridimensiona mantenendo le proporzioni, altezza max 44px
                if(imgURL != null){
                    BufferedImage img = ImageIO.read(imgURL);
                    int h = 44;
                    int w = (int)((double) img.getWidth() / img.getHeight() * h);
                    Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                    JLabel lbl = new JLabel(new ImageIcon(scaled));
                    lbl.setBorder(new EmptyBorder(0, 0, 0, 4));
                    return lbl;
                }
            } catch (Exception ignored) {
                // File non trovato o non leggibile → fallback al badge
            }
        }
        return buildTextBadge();
    }

    /** Badge testuale "WMS" usato quando il logo non è disponibile. */
    private JLabel buildTextBadge() {
        JLabel badge = new JLabel("WMS");
        badge.setFont(new Font("SansSerif", Font.BOLD, 12));
        badge.setForeground(new Color(0xD6, 0xE4, 0xF0));
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD6, 0xE4, 0xF0, 70), 1, true),
                new EmptyBorder(5, 12, 5, 12)
        ));
        return badge;
    }

    /** Cerchio con iniziali dell'utente. */
    private JPanel buildAvatar(String nomeUtente) {
        String iniziali = buildInitials(nomeUtente);
        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(StyleWMS.AZZURRO_LIGHT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(36, 36); }
        };
        avatar.setOpaque(false);
        avatar.setLayout(new GridBagLayout());
        JLabel lbl = new JLabel(iniziali);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(StyleWMS.BLU_ACCIAIO);
        avatar.add(lbl);
        return avatar;
    }

    // ── Etichetta sezione ─────────────────────────────────────────
    private JLabel buildSectionLabel(String testo) {
        JLabel lbl = new JLabel(testo.toUpperCase());
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(new Color(0x88, 0x88, 0x88));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        // lettera-spaziatura simulata con HTML
        lbl.setText("<html><span style='letter-spacing:2px'>" + testo.toUpperCase() + "</span></html>");
        return lbl;
    }

    // ── Pannello operazioni (blu, in risalto) ─────────────────────
    private JPanel buildOperationsPanel() {
        JPanel p = new JPanel(new GridLayout(1, 2, 12, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(buildOpCard("Visualizza storico",  "Movimenti passati", "📋", () -> onStorico()));
        p.add(buildOpCard("Effettua operazioni", "Carico / scarico",  "⇄",  () -> onOperazioni()));
        return p;
    }

    private JPanel buildOpCard(String titolo, String sub, String iconTxt, Runnable azione) {
        // Pannello blu con angoli arrotondati
        JPanel card = new JPanel() {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) { azione.run(); }
                });
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? StyleWMS.BLU_MEDIO : StyleWMS.BLU_ACCIAIO);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(new EmptyBorder(16, 16, 14, 16));

        // Icona + testi in alto
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setOpaque(false);

        JPanel iconBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xFF, 0xFF, 0xFF, 30));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(36, 36); }
        };
        iconBox.setOpaque(false);
        iconBox.setLayout(new GridBagLayout());
        JLabel iconLbl = new JLabel(iconTxt);
        iconLbl.setFont(new Font("SansSerif", Font.PLAIN, 18));
        iconLbl.setForeground(StyleWMS.AZZURRO_LIGHT);
        iconBox.add(iconLbl);

        JPanel testi = new JPanel();
        testi.setOpaque(false);
        testi.setLayout(new BoxLayout(testi, BoxLayout.Y_AXIS));
        JLabel lTitolo = new JLabel(titolo);
        lTitolo.setFont(new Font("SansSerif", Font.BOLD, 13));
        lTitolo.setForeground(StyleWMS.BIANCO);
        JLabel lSub = new JLabel(sub);
        lSub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lSub.setForeground(new Color(0xD6, 0xE4, 0xF0, 180));
        testi.add(lTitolo);
        testi.add(lSub);

        top.add(iconBox);
        top.add(testi);

        // Freccia in basso a destra
        JLabel arrow = new JLabel("→");
        arrow.setFont(new Font("SansSerif", Font.BOLD, 16));
        arrow.setForeground(new Color(0xD6, 0xE4, 0xF0, 120));
        arrow.setHorizontalAlignment(SwingConstants.RIGHT);

        card.add(top, BorderLayout.CENTER);
        card.add(arrow, BorderLayout.SOUTH);
        return card;
    }

    // ── Griglia prodotti ──────────────────────────────────────────
    private JPanel buildProductsGrid(List<Prodotto> prodotti) {
        JPanel grid = new JPanel(new GridLayout(0, 2, 10, 8));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (Prodotto p : prodotti) grid.add(buildProductCard(p));
        return grid;
    }

    private JPanel buildProductCard(Prodotto p) {
        JPanel card = new RoundPanel(StyleWMS.BIANCO, StyleWMS.AZZURRO_LIGHT, 10);
        card.setLayout(new BorderLayout(10, 0));
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel iconBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(StyleWMS.AZZURRO_LIGHT);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 7, 7));
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(34, 34); }
        };
        iconBox.setOpaque(false);
        iconBox.setLayout(new GridBagLayout());
        JLabel ico = new JLabel("▣");
        ico.setFont(new Font("SansSerif", Font.PLAIN, 14));
        ico.setForeground(StyleWMS.BLU_ACCIAIO);
        iconBox.add(ico);

        JPanel testi = new JPanel();
        testi.setOpaque(false);
        testi.setLayout(new BoxLayout(testi, BoxLayout.Y_AXIS));
        JLabel nome = new JLabel(p.getNome());
        nome.setFont(new Font("SansSerif", Font.BOLD, 12));
        nome.setForeground(StyleWMS.ANTRACITE);
        JLabel sku = new JLabel(p.getID());
        sku.setFont(new Font("SansSerif", Font.PLAIN, 10));
        sku.setForeground(StyleWMS.GRIGIO_TESTO);
        testi.add(nome);
        testi.add(sku);

        JLabel qty = new JLabel(String.valueOf(p.getQtaDisponibile()));
        qty.setFont(new Font("SansSerif", Font.BOLD, 12));
        qty.setForeground(StyleWMS.BLU_ACCIAIO);
        qty.setBackground(StyleWMS.AZZURRO_LIGHT);
        qty.setOpaque(true);
        qty.setBorder(new EmptyBorder(2, 8, 2, 8));

        card.add(iconBox, BorderLayout.WEST);
        card.add(testi, BorderLayout.CENTER);
        card.add(qty, BorderLayout.EAST);
        return card;
    }

    // ── Footer ────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new RoundPanel(StyleWMS.BIANCO, StyleWMS.AZZURRO_LIGHT, 10);
        footer.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel icon = new JLabel("ℹ");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 13));
        icon.setForeground(StyleWMS.BLU_MEDIO);

        JLabel msg = new JLabel("Hai riscontrato qualche problema?");
        msg.setFont(new Font("SansSerif", Font.PLAIN, 12));
        msg.setForeground(StyleWMS.GRIGIO_TESTO);

        JLabel link = new JLabel("<html><u>Invia un report</u></html>");
        link.setFont(new Font("SansSerif", Font.BOLD, 12));
        link.setForeground(StyleWMS.BLU_MEDIO);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { onReport(); }
        });

        footer.add(icon);
        footer.add(msg);
        footer.add(link);
        return footer;
    }

    // ── Helpers ───────────────────────────────────────────────────
    private String buildInitials(String nome) {
        String[] parti = nome.trim().split("\\s+");
        if (parti.length == 1) return parti[0].substring(0, Math.min(2, parti[0].length())).toUpperCase();
        return ("" + parti[0].charAt(0) + parti[parti.length - 1].charAt(0)).toUpperCase();
    }

    // ── Azioni ────────────────────────────────────────────────────
    private void onStorico(){
        this.dispose();
        VisualizzaStorico visualizzaStorico = new VisualizzaStorico(this.operatoreLoggato);
        visualizzaStorico.setVisible(true);
    }

    // Collegamento effettuato alla classe EffettuaOperazioni
    private void onOperazioni() {
        this.dispose();
        EffettuaOperazioni effettuaOperazioni = new EffettuaOperazioni(this.operatoreLoggato);
        effettuaOperazioni.setVisible(true);
    }

    private void onReport(){
        this.dispose();
        ReportBug reportBug = new ReportBug();
        reportBug.setVisible(true);
    }

    // ── RoundPanel ────────────────────────────────────────────────
    static class RoundPanel extends JPanel {
        private Color bg;
        private final Color border;
        private final int radius;
        RoundPanel(Color bg, Color border, int radius) {
            this.bg = bg; this.border = border; this.radius = radius;
            setOpaque(false);
        }
        @Override public void setBackground(Color c) { this.bg = c; }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius * 2, radius * 2));
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(0.75f, 0.75f, getWidth() - 1.5f, getHeight() - 1.5f, radius * 2, radius * 2));
            g2.dispose();
        }
    }

    // ── main ──────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}

            Operatore operatoreTest = new Operatore(
                    "Duce",
                    "Mussolini",
                    "duce@mail.it",
                    "888888",
                    "DUCE_1"
            );

            String logoPath = args.length > 1 ? args[1] : null;

            new DashboardOperatore(operatoreTest, logoPath).setVisible(true);
        });
    }
}