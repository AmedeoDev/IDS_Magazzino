package it.unina.magazzino.boundary;

import it.unina.magazzino.boundary.utils.StyleWMS;
import it.unina.magazzino.control.MovimentoController;
import it.unina.magazzino.control.ProdottoController;
import it.unina.magazzino.entity.Movimento;
import it.unina.magazzino.entity.Prodotto;
import it.unina.magazzino.entity.Responsabile;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;
import org.kordamp.ikonli.swing.FontIcon;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class DashboardResponsabile extends JFrame {

    // Colori locali derivati (trasparenze su palette StyleWMS)
    private static final Color GLASS_BORDER   = new Color(
            StyleWMS.BLU_ACCIAIO.getRed(),
            StyleWMS.BLU_ACCIAIO.getGreen(),
            StyleWMS.BLU_ACCIAIO.getBlue(), 60);
    private static final Color HOVER_BG       = new Color(
            StyleWMS.AZZURRO_LIGHT.getRed(),
            StyleWMS.AZZURRO_LIGHT.getGreen(),
            StyleWMS.AZZURRO_LIGHT.getBlue(), 40);
    private static final Color ACTIVE_BG      = new Color(
            StyleWMS.AZZURRO_LIGHT.getRed(),
            StyleWMS.AZZURRO_LIGHT.getGreen(),
            StyleWMS.AZZURRO_LIGHT.getBlue(), 25);
    private static final Color ROW_ODD        = new Color(0xF7, 0xFA, 0xFF);
    private static final Color ROW_EVEN       = StyleWMS.BIANCO;
    private static final Color SEL_BG         = new Color(
            StyleWMS.AZZURRO_LIGHT.getRed(),
            StyleWMS.AZZURRO_LIGHT.getGreen(),
            StyleWMS.AZZURRO_LIGHT.getBlue(), 120);
    private static final Color GRID_COLOR     = new Color(0xE8, 0xEF, 0xF8);
    private static final Color CARD_BORDER    = new Color(0xD6, 0xE4, 0xF0);
    private static final Color ALERT_BORDER   = new Color(198, 40, 40, 80);

    private static final Font FONT_MONO  = new Font("Monospaced", Font.BOLD, 11);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.PLAIN, 11);

    private String sezioneAttiva = "Panoramica";
    private JPanel contenutoWrapper;
    private Responsabile responsabileLoggato;

    private int notificheCount = 5;
    private JPanel sidebarPanel;

    private final String[] VOCI = {
            "Panoramica",
            "Gestisci Prodotti",
            "Storico Movimenti",
            "Prodotti Sotto Scorta",
            "Andamento Magazzino"
    };

    // ── Costruttore ──────────────────────────────────────────────
    public DashboardResponsabile(Responsabile responsabile, String logoPath) {
        this.responsabileLoggato = responsabile;
        String nomeUtente = responsabile.getNome() + " " + responsabile.getCognome();

        setTitle("WMS — Dashboard Responsabile");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1180, 740);
        setMinimumSize(new Dimension(920, 600));
        setLocationRelativeTo(null);
        setBackground(StyleWMS.GRIGIO_NEUTRO);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(StyleWMS.GRIGIO_NEUTRO);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Dot-grid texture sottile
                g2.setColor(new Color(
                        StyleWMS.BLU_ACCIAIO.getRed(),
                        StyleWMS.BLU_ACCIAIO.getGreen(),
                        StyleWMS.BLU_ACCIAIO.getBlue(), 12));
                for (int x = 0; x < getWidth(); x += 28)
                    for (int y = 0; y < getHeight(); y += 28)
                        g2.fillOval(x - 1, y - 1, 2, 2);
                g2.dispose();
            }
        };
        root.setOpaque(true);
        setContentPane(root);

        root.add(buildTopbar(nomeUtente, logoPath), BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        sidebarPanel = buildSidebar();
        wrapper.add(sidebarPanel, BorderLayout.WEST);

        contenutoWrapper = new JPanel(new BorderLayout());
        contenutoWrapper.setOpaque(false);
        contenutoWrapper.add(buildPanoramica(), BorderLayout.CENTER);
        wrapper.add(contenutoWrapper, BorderLayout.CENTER);

        root.add(wrapper, BorderLayout.CENTER);
    }

    // ══ NOTIFICHE ════════════════════════════════════════════════
    public void decrementaNotifica() {
        notificheCount = Math.max(0, notificheCount - 1);
        sidebarPanel.repaint();
    }

    public void azzeraNotifiche() {
        notificheCount = 0;
        sidebarPanel.repaint();
    }

    public int getNotificheCount() { return notificheCount; }

    // ══ TOP BAR ══════════════════════════════════════════════════
    private JPanel buildTopbar(String nomeUtente, String logoPath) {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Gradiente orizzontale sul BLU_ACCIAIO
                GradientPaint gp = new GradientPaint(
                        0, 0, StyleWMS.BLU_ACCIAIO,
                        getWidth(), 0, StyleWMS.BLU_MEDIO);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Linea inferiore accent
                g2.setColor(StyleWMS.AZZURRO_LIGHT);
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(13, 8, 13, 22));

        // — LEFT
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        left.add(buildAvatar(nomeUtente));

        // Separatore verticale
        JPanel divider = new JPanel() {
            @Override public Dimension getPreferredSize() { return new Dimension(1, 36); }
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 50));
                g.fillRect(0, 0, 1, getHeight());
            }
        };
        divider.setOpaque(false);
        left.add(divider);

        JPanel testi = new JPanel();
        testi.setOpaque(false);
        testi.setLayout(new BoxLayout(testi, BoxLayout.Y_AXIS));

        JLabel nome = new JLabel("Benvenuto, " + nomeUtente);
        nome.setFont(new Font("SansSerif", Font.BOLD, 13));
        nome.setForeground(StyleWMS.BIANCO);

        JPanel ruoloRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        ruoloRow.setOpaque(false);
        ruoloRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(StyleWMS.AZZURRO_LIGHT);
                g2.fillOval(0, 3, 6, 6);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(6, 12); }
        };
        dot.setOpaque(false);
        JLabel ruolo = new JLabel("Responsabile di magazzino");
        ruolo.setFont(FONT_LABEL);
        ruolo.setForeground(new Color(0xD6, 0xE4, 0xF0));
        ruoloRow.add(dot);
        ruoloRow.add(ruolo);

        testi.add(nome);
        testi.add(ruoloRow);
        left.add(testi);

        // — RIGHT
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        right.add(buildLogoWidget(logoPath));

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JComponent buildLogoWidget(String logoPath) {
        if (logoPath != null) {
            try {
                java.net.URL imgURL = getClass().getResource(logoPath);
                if (imgURL != null) {
                    BufferedImage img = ImageIO.read(imgURL);
                    int h = 40;
                    int w = (int) ((double) img.getWidth() / img.getHeight() * h);
                    Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                    JLabel lbl = new JLabel(new ImageIcon(scaled));
                    lbl.setBorder(new EmptyBorder(0, 0, 0, 4));
                    return lbl;
                }
            } catch (Exception ignored) {}
        }
        return buildTextBadge();
    }

    private JLabel buildTextBadge() {
        JLabel badge = new JLabel("WMS") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.setColor(new Color(0xD6, 0xE4, 0xF0, 90));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("SansSerif", Font.BOLD, 13));
        badge.setForeground(new Color(0xD6, 0xE4, 0xF0));
        badge.setBorder(new EmptyBorder(6, 14, 6, 14));
        badge.setOpaque(false);
        return badge;
    }

    private JPanel buildAvatar(String nomeUtente) {
        String iniziali = buildInitials(nomeUtente);
        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Anello esterno
                g2.setColor(new Color(
                        StyleWMS.AZZURRO_LIGHT.getRed(),
                        StyleWMS.AZZURRO_LIGHT.getGreen(),
                        StyleWMS.AZZURRO_LIGHT.getBlue(), 80));
                g2.fillOval(0, 0, getWidth(), getHeight());
                // Interno
                g2.setColor(StyleWMS.AZZURRO_LIGHT);
                g2.fillOval(3, 3, getWidth()-6, getHeight()-6);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(40, 40); }
        };
        avatar.setOpaque(false);
        avatar.setLayout(new GridBagLayout());
        JLabel lbl = new JLabel(iniziali);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(StyleWMS.BLU_ACCIAIO);
        avatar.add(lbl);
        return avatar;
    }

    // ══ SIDEBAR ══════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(20, 90, 170));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Bordo destro sfumato
                GradientPaint gp = new GradientPaint(
                        getWidth()-2, 0, new Color(255,255,255,0),
                        getWidth()-2, getHeight()/2, new Color(255,255,255,40));
                g2.setPaint(gp);
                g2.fillRect(getWidth()-2, 0, 2, getHeight());
                g2.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(228, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(28, 0, 28, 0));

        JLabel sezioneLbl = new JLabel("MENU");
        sezioneLbl.setFont(new Font(Font.MONOSPACED, Font.BOLD, 9));
        sezioneLbl.setForeground(new Color(0xD6, 0xE4, 0xF0, 130));
        sezioneLbl.setBorder(new EmptyBorder(0, 22, 14, 0));
        sezioneLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(sezioneLbl);

        for (String voce : VOCI) {
            sidebar.add(voceMenu(sidebar, voce));
            sidebar.add(Box.createVerticalStrut(2));
        }

        sidebar.add(Box.createVerticalGlue());

        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(
                        20, 0, new Color(255,255,255,0),
                        getWidth()/2, 0, new Color(255,255,255,40));
                g2.setPaint(gp);
                g2.fillRect(20, 0, getWidth()-40, 1);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(0, 1); }
        };
        sep.setOpaque(false);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(18));

        JPanel logoutCard = buildLogoutCard();
        logoutCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logoutCard);

        return sidebar;
    }

    private JPanel voceMenu(JPanel sidebar, String testo) {
        FontIcon fontIcon = switch (testo) {
            case "Panoramica"            -> FontIcon.of(Material2OutlinedAL.DASHBOARD,           18, StyleWMS.BIANCO_TALCO);
            case "Gestisci Prodotti"     -> FontIcon.of(Material2OutlinedAL.CATEGORY,            18, StyleWMS.BIANCO_TALCO);
            case "Storico Movimenti"     -> FontIcon.of(Material2OutlinedAL.HISTORY,             18, StyleWMS.BIANCO_TALCO);
            case "Prodotti Sotto Scorta" -> FontIcon.of(Material2OutlinedAL.ERROR_OUTLINE,       18, StyleWMS.BIANCO_TALCO);
            case "Andamento Magazzino"   -> FontIcon.of(Material2OutlinedMZ.SHOW_CHART,          18, StyleWMS.BIANCO_TALCO);
            default                      -> FontIcon.of(Material2OutlinedAL.FIBER_MANUAL_RECORD, 18, StyleWMS.BIANCO_TALCO);
        };

        final boolean hasBadge = "Prodotti Sotto Scorta".equals(testo);

        JPanel row = new JPanel() {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) {
                        if (!testo.equals(sezioneAttiva)) {
                            sezioneAttiva = testo;
                            navigaA(testo);
                            sidebar.repaint();
                        }
                    }
                });
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override protected void paintComponent(Graphics g) {
                boolean attivo = testo.equals(sezioneAttiva);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (attivo) {
                    g2.setColor(new Color(255, 255, 255, 25));
                    g2.fill(new RoundRectangle2D.Float(8, 2, getWidth()-16, getHeight()-4, 10, 10));
                    // Indicatore sinistro
                    g2.setColor(StyleWMS.AZZURRO_LIGHT);
                    g2.fillRoundRect(8, 6, 3, getHeight()-12, 3, 3);
                } else if (hovered) {
                    g2.setColor(new Color(255, 255, 255, 12));
                    g2.fill(new RoundRectangle2D.Float(8, 2, getWidth()-16, getHeight()-4, 10, 10));
                }

                // Badge notifica
                if (hasBadge && notificheCount > 0) {
                    String etichetta = String.valueOf(notificheCount);
                    Font badgeFont = new Font("SansSerif", Font.BOLD, 9);
                    g2.setFont(badgeFont);
                    FontMetrics fm = g2.getFontMetrics();
                    int textW = fm.stringWidth(etichetta);
                    int badgeDiam = Math.max(16, textW + 8);
                    int badgeH   = 16;
                    int badgeX   = getWidth() - badgeDiam - 14;
                    int badgeY   = (getHeight() - badgeH) / 2;

                    g2.setColor(new Color(0, 0, 0, 40));
                    g2.fillRoundRect(badgeX+1, badgeY+1, badgeDiam, badgeH, badgeH, badgeH);
                    g2.setColor(new Color(220, 30, 30));
                    g2.fillRoundRect(badgeX, badgeY, badgeDiam, badgeH, badgeH, badgeH);
                    g2.setColor(new Color(255, 255, 255, 180));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(badgeX, badgeY, badgeDiam-1, badgeH-1, badgeH, badgeH);
                    g2.setColor(Color.WHITE);
                    int textX = badgeX + (badgeDiam - textW) / 2;
                    int textY = badgeY + (badgeH + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(etichetta, textX, textY);
                }

                g2.dispose();
            }
        };

        row.setOpaque(false);
        row.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        row.setBorder(new EmptyBorder(4, 18, 4, 18));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel ico = new JLabel(fontIcon) {
            @Override public void paint(Graphics g) {
                fontIcon.setIconColor(
                        testo.equals(sezioneAttiva) ? StyleWMS.BIANCO : StyleWMS.BIANCO_TALCO);
                super.paint(g);
            }
        };
        ico.setBorder(new EmptyBorder(0, 0, 0, 12));

        JLabel lbl = new JLabel(testo) {
            @Override public Font getFont() {
                return new Font("SansSerif",
                        testo.equals(sezioneAttiva) ? Font.BOLD : Font.PLAIN, 13);
            }
            @Override public Color getForeground() {
                return testo.equals(sezioneAttiva) ? StyleWMS.BIANCO : StyleWMS.BIANCO_TALCO;
            }
        };

        row.add(ico);
        row.add(lbl);
        return row;
    }

    // ── Navigazione
    private void navigaA(String sezione) {
        JComponent pannello = switch (sezione) {
            case "Gestisci Prodotti"     -> new GestisciProdotti(responsabileLoggato.getID_Utenete());
            case "Storico Movimenti"     -> new StoricoMovimenti();
            case "Prodotti Sotto Scorta" -> new ProdottiSottoScorta();
            case "Andamento Magazzino"   -> new AndamentoMagazzino();
            default                      -> buildPanoramica();
        };
        contenutoWrapper.removeAll();
        contenutoWrapper.add(pannello, BorderLayout.CENTER);
        contenutoWrapper.revalidate();
        contenutoWrapper.repaint();
    }

    // ══ PANORAMICA ═══════════════════════════════════════════════
    private JScrollPane buildPanoramica() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(24, 24, 24, 24));

        content.add(buildPageHeader("Panoramica", "Riepilogo generale del magazzino"));
        content.add(Box.createVerticalStrut(20));
        content.add(buildKpiRow());
        content.add(Box.createVerticalStrut(28));
        content.add(buildSectionLabel("Ultimi Movimenti", "RF11"));
        content.add(Box.createVerticalStrut(10));
        content.add(buildTabellaMovimenti());
        content.add(Box.createVerticalStrut(28));
        content.add(buildSectionLabel("Prodotti Sotto Scorta", "RF10"));
        content.add(Box.createVerticalStrut(10));
        content.add(buildSottoScorta());
        content.add(Box.createVerticalStrut(28));
        content.add(buildSectionLabel("Andamento Magazzino", "RF13"));
        content.add(Box.createVerticalStrut(10));
        content.add(buildAndamento());
        content.add(Box.createVerticalStrut(20));
        content.add(buildFooter());
        content.add(Box.createVerticalStrut(16));

        JScrollPane scroll = new JScrollPane(content,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel buildPageHeader(String titolo, String sub) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel t = new JLabel(titolo);
        t.setFont(new Font("SansSerif", Font.BOLD, 22));
        t.setForeground(StyleWMS.BLU_ACCIAIO);

        JLabel s = new JLabel(sub);
        s.setFont(FONT_LABEL);
        s.setForeground(StyleWMS.GRIGIO_TESTO);
        s.setBorder(new EmptyBorder(3, 0, 0, 0));

        p.add(t);
        p.add(s);
        return p;
    }

    // ── KPI row
    private JPanel buildKpiRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 108));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);


        // prelevo i prodotti totali
        String prodottiTotali = "N/D";
        String subProdotti = ""; // questa variabile ci servirà per mostrare i movimenti nel magazzino
        try {
            List<Prodotto> prodotti = new ProdottoController().getAllProdotti();
            if(prodotti != null) prodottiTotali = String.valueOf(prodotti.size());
        } catch (Exception e) {}

        // prelevo i prodotti sotto scorta
        int prodottiSottoScorta = 0;
        try {
            List<Prodotto> sottoScorta = new ProdottoController().getAllProdotti();
            if(sottoScorta != null){
                for(Prodotto p : sottoScorta){
                    if(p.isSottoScorta()) prodottiSottoScorta++;
                }
            }
        } catch (Exception e) {}

        // prelevo i movimenti odierni

        String movimentiOggi = "N/D";
        String subMovimenti = "";
        try {
            // sviluppiamo getMovimentiOggi per questa funzionalità
            List<Movimento> oggi = new ProdottoController().getMovimentiOggi();
            List<Movimento> ieri = new ProdottoController().getMovimentiIeri();
            if(oggi != null) {
                movimentiOggi = String.valueOf(oggi.size());
                if(ieri != null){
                    int diff = oggi.size() - ieri.size();
                    if(diff > 0) subMovimenti = "↑ " + diff + " rispetto ieri";
                    else if(diff > 0) subMovimenti = "↓ " + diff + "rispetti ieri";
                    else subMovimenti = "movimenti invariati da ieri";
                }
            }
        } catch (Exception e) {}

        notificheCount = prodottiSottoScorta;
        sidebarPanel.repaint();

        row.add(kpiCard("Prodotti totali", prodottiTotali, StyleWMS.BLU_ACCIAIO, subProdotti));
        row.add(kpiCard("Movimenti oggi", movimentiOggi, new Color(46, 125, 50), subMovimenti));
        row.add(kpiCard("Sotto scorta", String.valueOf(prodottiSottoScorta), new Color(198, 40, 40), "⚠ Attenzione"));


        return row;
    }

    private JPanel kpiCard(String titolo, String valore, Color colore, String sub) {
        JPanel card = new JPanel() {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? new Color(0xEA, 0xF2, 0xFF) : StyleWMS.BIANCO);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(CARD_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 14, 14));
                // Striscia superiore colorata
                GradientPaint gp = new GradientPaint(0, 0,
                        new Color(colore.getRed(), colore.getGreen(), colore.getBlue(), 220),
                        getWidth(), 0,
                        new Color(colore.getRed(), colore.getGreen(), colore.getBlue(), 30));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), 4, 4, 4));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel lblV = new JLabel(valore);
        lblV.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblV.setForeground(colore);

        JLabel lblT = new JLabel(titolo);
        lblT.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblT.setForeground(StyleWMS.GRIGIO_TESTO);

        JLabel lblS = new JLabel(sub);
        lblS.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblS.setForeground(new Color(colore.getRed(), colore.getGreen(), colore.getBlue(), 160));

        card.add(lblV);
        card.add(Box.createVerticalStrut(2));
        card.add(lblT);
        card.add(Box.createVerticalStrut(4));
        card.add(lblS);
        return card;
    }

    private JPanel buildTabellaMovimenti() {
        JPanel panel = buildCard(StyleWMS.BIANCO, CARD_BORDER);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] col = {"ID Prodotto", "Tipo", "Quantità", "Data", "Operatore"};
        Object[][] dati = {};

        try {
            List<Movimento> recenti = new MovimentoController().getUltimiMovimenti(5);
            if(recenti != null && !recenti.isEmpty()){
                dati = new Object[recenti.size()][5];
                for(int i = 0; i < recenti.size(); ++i){
                    Movimento m = recenti.get(i);
                    String segno = "Carico".equals(m.getTipoMovimento()) ? "+" : "-";
                    dati[i][0] = m.getIdProdotto();
                    dati[i][1] = m.getTipoMovimento();
                    dati[i][2] = segno + m.getQtaProdotto();
                    dati[i][3] = m.getData().toString();
                    dati[i][4] = m.getIdOperatore();
                }
            } else {
                dati = new Object[][]{{"Nessun movimento", "-", "-", "-", "-"}};
            }
        } catch (Exception e){
            dati = new Object[][]{{"Errore caricamento", "-", "-", "-", "-"}};
        }


        panel.add(buildTable(col, dati), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSottoScorta() {
        JPanel panel = buildCard(new Color(0xFF, 0xFA, 0xFA), ALERT_BORDER);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] col = {"ID", "Nome Prodotto", "Disponibile", "Soglia Minima"};
        Object[][] dati = {};

        try {
            List<Prodotto> tuttiProdotti = new ProdottoController().getAllProdotti();
            List<Object[]> righe = new ArrayList<>();
            if(tuttiProdotti != null){
                for(Prodotto p : tuttiProdotti){
                    if(p.isSottoScorta()){
                        righe.add(new Object[]{
                                p.getID(),
                                p.getNome(),
                                String.valueOf(p.getQtaDisponibile()),
                                String.valueOf(p.getSogliaMinima())
                        });
                    }
                }
            }
            dati = righe.isEmpty() ? new Object[][]{{"Nessun prodotto sotto scorta", "-", "-", "-", "-"}}
            : righe.toArray(new Object[0][]);
        } catch (Exception e){
            dati = new Object[][]{{"Errore caricamento dati", "-", "-", "-", "-"}};
        }


        panel.add(buildTable(col, dati), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildAndamento() {
        JPanel panel = buildCard(StyleWMS.BIANCO, CARD_BORDER);
        panel.setLayout(new GridLayout(1, 3, 20, 0));
        panel.setBorder(new EmptyBorder(22, 22, 22, 22));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        panel.add(statBox("Operazioni totali (7gg)",  "89",                    StyleWMS.BLU_ACCIAIO));
        panel.add(statBox("Prodotti più movimentati", "Guanti, Scatole",       StyleWMS.BLU_MEDIO));
        panel.add(statBox("Sotto scorta attualmente", "5",                     new Color(198, 40, 40)));
        return panel;
    }

    private JPanel statBox(String etichetta, String valore, Color colore) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        // Accent line
        JPanel accent = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0,
                        new Color(colore.getRed(), colore.getGreen(), colore.getBlue(), 200),
                        50, 0,
                        new Color(colore.getRed(), colore.getGreen(), colore.getBlue(), 0));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), 3, 3, 3);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(50, 3); }
            @Override public Dimension getMaximumSize() { return new Dimension(50, 3); }
        };
        accent.setOpaque(false);

        JLabel lv = new JLabel(valore);
        lv.setFont(new Font("SansSerif", Font.BOLD, 24));
        lv.setForeground(colore);

        JLabel le = new JLabel(etichetta);
        le.setFont(FONT_LABEL);
        le.setForeground(StyleWMS.GRIGIO_TESTO);

        p.add(accent);
        p.add(Box.createVerticalStrut(8));
        p.add(lv);
        p.add(Box.createVerticalStrut(4));
        p.add(le);
        return p;
    }

    private JPanel buildFooter() {
        JPanel footer = buildCard(StyleWMS.BIANCO, CARD_BORDER);
        footer.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel icon = new JLabel("ℹ");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 13));
        icon.setForeground(StyleWMS.BLU_MEDIO);

        JLabel msg = new JLabel("Hai riscontrato qualche problema?");
        msg.setFont(FONT_LABEL);
        msg.setForeground(StyleWMS.GRIGIO_TESTO);

        JLabel link = new JLabel("<html><u>Invia un report</u></html>");
        link.setFont(new Font("SansSerif", Font.BOLD, 12));
        link.setForeground(StyleWMS.BLU_MEDIO);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                dispose();
                new ReportBug().setVisible(true);
            }
        });

        footer.add(icon);
        footer.add(msg);
        footer.add(link);
        return footer;
    }

    // ── Tabella
    private JScrollPane buildTable(String[] colonne, Object[][] dati) {
        DefaultTableModel model = new DefaultTableModel(dati, colonne) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model) {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                    c.setForeground(StyleWMS.GRIGIO_TESTO);
                }
                return c;
            }
        };
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(GRID_COLOR);
        table.setBackground(StyleWMS.BIANCO);
        table.setSelectionBackground(SEL_BG);
        table.setSelectionForeground(StyleWMS.BLU_ACCIAIO);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.setBackground(StyleWMS.AZZURRO_LIGHT);
        header.setForeground(StyleWMS.BLU_ACCIAIO);
        header.setOpaque(true);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, CARD_BORDER));

        JScrollPane sp = new JScrollPane(table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(BorderFactory.createLineBorder(GRID_COLOR, 1));
        sp.setPreferredSize(new Dimension(0, 168));
        sp.getViewport().setBackground(StyleWMS.BIANCO);
        return sp;
    }

    // ── Card builder generico con bordo colorato
    private JPanel buildCard(Color bg, Color border) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(border);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 14, 14));
                g2.dispose();
            }
        };
    }

    // ── Section label con chip ref
    private JPanel buildSectionLabel(String testo, String ref) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(testo.toUpperCase());
        lbl.setFont(new Font(Font.MONOSPACED, Font.BOLD, 10));
        lbl.setForeground(StyleWMS.GRIGIO_TESTO);

        JLabel chip = new JLabel(ref) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(StyleWMS.AZZURRO_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(new Font(Font.MONOSPACED, Font.BOLD, 9));
        chip.setForeground(StyleWMS.BLU_ACCIAIO);
        chip.setBorder(new EmptyBorder(2, 8, 2, 8));
        chip.setOpaque(false);

        p.add(lbl);
        p.add(chip);
        return p;
    }

    // ══ LOGOUT ═══════════════════════════════════════════════════
    private JPanel buildLogoutCard() {
        JPanel card = new JPanel() {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) {
                        JOptionPane.showMessageDialog(DashboardResponsabile.this, "Logout effettuato.");
                        dispose();
                        HomePage homePage = new HomePage();
                        homePage.setVisible(true);
                    }
                });
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = hovered ? new Color(180, 30, 30) : new Color(198, 40, 40);
                GradientPaint gp = new GradientPaint(0, 0, base,
                        getWidth(), 0, new Color(base.getRed(), base.getGreen(), base.getBlue(), 180));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(10, 2, getWidth()-20, getHeight()-4, 10, 10));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        card.setBorder(new EmptyBorder(6, 22, 6, 16));

        FontIcon logoutIcon = FontIcon.of(Material2OutlinedMZ.POWER_SETTINGS_NEW, 16, StyleWMS.BIANCO);
        JLabel ico = new JLabel(logoutIcon);
        ico.setBorder(new EmptyBorder(0, 0, 0, 10));

        JLabel lbl = new JLabel("Esci");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(StyleWMS.BIANCO);

        card.add(ico);
        card.add(lbl);
        return card;
    }

    // ── Helpers
    private String buildInitials(String nome) {
        String[] parti = nome.trim().split("\\s+");
        if (parti.length == 1)
            return parti[0].substring(0, Math.min(2, parti[0].length())).toUpperCase();
        return ("" + parti[0].charAt(0) + parti[parti.length - 1].charAt(0)).toUpperCase();
    }

    // ── main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            Responsabile respTesting = new Responsabile(
                    "Amedeo",
                    "Catanese Napolitano",
                    "amedeoSup@mail.it",
                    "123456",
                    "DEMO"
            );
            String logo = args.length > 1 ? args[1] : null;
            new DashboardResponsabile(respTesting, logo).setVisible(true);
        });
    }
}