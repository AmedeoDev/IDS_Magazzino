package it.unina.magazzino.boundary;

import it.unina.magazzino.boundary.utils.StyleWMS;
import it.unina.magazzino.entity.Operatore;
import it.unina.magazzino.entity.Responsabile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * DashboardResponsabile — versione aggiornata con navigazione funzionante.
 *
 * Sezioni disponibili:
 *  - Panoramica       → contenuto riepilogativo (RF10/RF11/RF13)
 *  - Gestisci Prodotti → GestisciProdotti (RF04–RF07)
 *  - Storico Movimenti → StoricoMovimenti (RF11/RF12)
 *  - Prodotti Sotto Scorta → ProdottiSottoScorta (RF10/RF08)
 *  - Andamento Magazzino → AndamentoMagazzino (RF13/RF14)
 */
public class DashboardResponsabile extends JFrame {

    private String sezioneAttiva = "Panoramica";

    // Area contenuto centrale — scambiata a ogni click sidebar
    private JPanel contenutoWrapper;

    private Responsabile responsabileLoggato;

    // Voci menu con riferimento ai label per aggiornare lo stile attivo
    private final String[] VOCI = {
            "Panoramica",
            "Gestisci Prodotti",
            "Storico Movimenti",
            "Prodotti Sotto Scorta",
            "Andamento Magazzino"
    };
    private final String[] ICONE = {"🏠","📦","📋","⚠️","📊"};

    private JPanel sidebarPanel;

    // ── Costruttore ──────────────────────────────────────────────
    public DashboardResponsabile(Responsabile responsabile, String logoPath) {

        this.responsabileLoggato = responsabile;

        String nomeUtente = responsabile.getNome() + " " + responsabile.getCognome();

        setTitle("Dashboard Responsabile — " + nomeUtente);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(860, 560));
        setLocationRelativeTo(null);
        getContentPane().setBackground(StyleWMS.GRIGIO_NEUTRO);
        setLayout(new BorderLayout());

        add(buildTopbar(nomeUtente, logoPath), BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        sidebarPanel = buildSidebar();
        wrapper.add(sidebarPanel, BorderLayout.WEST);

        contenutoWrapper = new JPanel(new BorderLayout());
        contenutoWrapper.setOpaque(false);
        contenutoWrapper.add(buildPanoramica(), BorderLayout.CENTER);
        wrapper.add(contenutoWrapper, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════════
    // TOP BAR
    // ══════════════════════════════════════════════════════════════
    private JPanel buildTopbar(String nomeUtente, String logoPath) {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(StyleWMS.BLU_ACCIAIO);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JPanel avatar = buildAvatar(nomeUtente);
        JPanel testi = new JPanel();
        testi.setOpaque(false);
        testi.setLayout(new BoxLayout(testi, BoxLayout.Y_AXIS));
        JLabel nome = new JLabel("Benvenuto, " + nomeUtente);
        nome.setFont(new Font("SansSerif", Font.BOLD, 13));
        nome.setForeground(StyleWMS.BIANCO);
        JLabel ruolo = new JLabel("Responsabile di magazzino");
        ruolo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        ruolo.setForeground(new Color(0xD6, 0xE4, 0xF0));
        testi.add(nome);
        testi.add(ruolo);
        left.add(avatar);
        left.add(testi);

        JComponent logoWidget = buildLogoWidget(logoPath);
        bar.add(left, BorderLayout.WEST);
        bar.add(logoWidget, BorderLayout.EAST);
        return bar;
    }

    private JComponent buildLogoWidget(String logoPath) {
        if (logoPath != null) {
            try {
                java.net.URL imgURL = getClass().getResource(logoPath);
                if (imgURL != null) {
                    BufferedImage img = ImageIO.read(imgURL);
                    int h = 44;
                    int w = (int)((double) img.getWidth() / img.getHeight() * h);
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
        JLabel badge = new JLabel("WMS");
        badge.setFont(new Font("SansSerif", Font.BOLD, 12));
        badge.setForeground(new Color(0xD6, 0xE4, 0xF0));
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD6, 0xE4, 0xF0, 70), 1, true),
                new EmptyBorder(5, 12, 5, 12)));
        return badge;
    }

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

    // ══════════════════════════════════════════════════════════════
    // SIDEBAR — navigazione cablata
    // ══════════════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(20, 90, 170));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(215, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(24, 0, 24, 0));

        JLabel sezioneLbl = new JLabel("MENU");
        sezioneLbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        sezioneLbl.setForeground(new Color(0xD6, 0xE4, 0xF0, 140));
        sezioneLbl.setBorder(new EmptyBorder(0, 20, 10, 0));
        sezioneLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(sezioneLbl);

        for (int i = 0; i < VOCI.length; i++) {
            sidebar.add(voceMenu(sidebar, ICONE[i], VOCI[i]));
        }

        sidebar.add(Box.createVerticalGlue());

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 30));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(14));

        JPanel logoutCard = buildLogoutCard();
        logoutCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logoutCard);

        return sidebar;
    }

    /**
     * Voce menu con highlight attivo e navigazione funzionante.
     * Ogni click aggiorna sezioneAttiva, ridisegna la sidebar
     * e sostituisce il pannello centrale.
     */
    private JPanel voceMenu(JPanel sidebar, String icona, String testo) {
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
                            sidebar.repaint(); // aggiorna highlight
                        }
                    }
                });
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override protected void paintComponent(Graphics g) {
                boolean attivo = testo.equals(sezioneAttiva);
                if (attivo) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 25));
                    g2.fill(new RoundRectangle2D.Float(8, 0, getWidth() - 16, getHeight(), 12, 12));
                    g2.setColor(StyleWMS.AZZURRO_LIGHT);
                    g2.fillRect(0, 4, 4, getHeight() - 8);
                    g2.dispose();
                } else if (hovered) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 12));
                    g2.fill(new RoundRectangle2D.Float(8, 0, getWidth() - 16, getHeight(), 12, 12));
                    g2.dispose();
                }
            }
        };

        row.setOpaque(false);
        row.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        row.setBorder(new EmptyBorder(4, 16, 4, 16));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel ico = new JLabel(icona);
        ico.setFont(new Font("SansSerif", Font.PLAIN, 15));
        ico.setBorder(new EmptyBorder(0, 0, 0, 10));

        // Il testo viene reso bold se la voce è quella attiva al momento del repaint
        JLabel lbl = new JLabel(testo) {
            @Override public Font getFont() {
                return new Font("SansSerif",
                        testo.equals(sezioneAttiva) ? Font.BOLD : Font.PLAIN, 13);
            }
            @Override public Color getForeground() {
                return testo.equals(sezioneAttiva) ? StyleWMS.BIANCO : new Color(0xD6, 0xE4, 0xF0);
            }
        };

        row.add(ico);
        row.add(lbl);
        return row;
    }

    // ── Navigazione centrale ─────────────────────────────────────
    private void navigaA(String sezione) {
        JComponent pannello = switch (sezione) {
            case "Gestisci Prodotti"     -> new GestisciProdotti();
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

    // ══════════════════════════════════════════════════════════════
    // PANORAMICA (home) — invariata rispetto all'originale
    // ══════════════════════════════════════════════════════════════
    private JScrollPane buildPanoramica() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        content.add(buildSectionLabel("Panoramica"));
        content.add(Box.createVerticalStrut(12));
        content.add(buildKpiRow());
        content.add(Box.createVerticalStrut(20));
        content.add(buildSectionLabel("Ultimi Movimenti  ·  RF11"));
        content.add(Box.createVerticalStrut(8));
        content.add(buildTabellaMovimenti());
        content.add(Box.createVerticalStrut(20));
        content.add(buildSectionLabel("Prodotti Sotto Scorta  ·  RF10"));
        content.add(Box.createVerticalStrut(8));
        content.add(buildSottoScorta());
        content.add(Box.createVerticalStrut(20));
        content.add(buildSectionLabel("Andamento Magazzino  ·  RF13"));
        content.add(Box.createVerticalStrut(8));
        content.add(buildAndamento());
        content.add(Box.createVerticalStrut(16));
        content.add(buildFooter());

        JScrollPane scroll = new JScrollPane(content,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        return scroll;
    }

    // — KPI row —
    private JPanel buildKpiRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(kpiCard("Prodotti Totali",   "142", StyleWMS.BLU_ACCIAIO));
        row.add(kpiCard("Movimenti Oggi",     "27",  new Color(46, 125, 50)));
        row.add(kpiCard("Sotto Scorta",        "5",  new Color(198, 40, 40)));
        row.add(kpiCard("Operatori Attivi",    "8",  new Color(123, 31, 162)));
        return row;
    }

    private JPanel kpiCard(String titolo, String valore, Color colore) {
        DashboardOperatore.RoundPanel card =
                new DashboardOperatore.RoundPanel(StyleWMS.BIANCO, StyleWMS.AZZURRO_LIGHT, 10);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        JLabel lblV = new JLabel(valore);
        lblV.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblV.setForeground(colore);
        JLabel lblT = new JLabel(titolo);
        lblT.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblT.setForeground(StyleWMS.GRIGIO_TESTO);
        card.add(lblV);
        card.add(Box.createVerticalStrut(4));
        card.add(lblT);
        return card;
    }

    private JPanel buildTabellaMovimenti() {
        DashboardOperatore.RoundPanel panel =
                new DashboardOperatore.RoundPanel(StyleWMS.BIANCO, StyleWMS.AZZURRO_LIGHT, 10);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] col = {"ID Prodotto","Tipo","Quantità","Data","Operatore"};
        Object[][] dati = {
                {"123456","Carico",  "+50","01/06/2026","m.verdi@wms.it"},
                {"789012","Scarico", "-20","01/06/2026","a.bianchi@wms.it"},
                {"345678","Carico",  "+30","31/05/2026","m.verdi@wms.it"},
                {"901234","Scarico", "-5", "31/05/2026","l.neri@wms.it"},
                {"567890","Carico",  "+100","30/05/2026","a.bianchi@wms.it"},
        };
        panel.add(buildTable(col, dati), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSottoScorta() {
        DashboardOperatore.RoundPanel panel =
                new DashboardOperatore.RoundPanel(StyleWMS.BIANCO, new Color(198, 40, 40, 60), 10);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] col = {"ID","Nome Prodotto","Disponibile","Soglia Minima"};
        Object[][] dati = {
                {"000011","Guanti latex",     "3", "10"},
                {"000045","Nastro da imballo","1", "5"},
                {"000078","Scatole S",         "4", "20"},
                {"000102","Etichette",         "8", "15"},
                {"000134","Pallets",           "2", "6"},
        };
        panel.add(buildTable(col, dati), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildAndamento() {
        DashboardOperatore.RoundPanel panel =
                new DashboardOperatore.RoundPanel(StyleWMS.BIANCO, StyleWMS.AZZURRO_LIGHT, 10);
        panel.setLayout(new GridLayout(1, 3, 16, 0));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        panel.add(statBox("Operazioni totali (7gg)",  "89",                    StyleWMS.BLU_ACCIAIO));
        panel.add(statBox("Prodotti più movimentati", "Guanti latex, Scatole", StyleWMS.BLU_MEDIO));
        panel.add(statBox("Sotto scorta attualmente", "5",                     new Color(198,40,40)));
        return panel;
    }

    private JPanel statBox(String etichetta, String valore, Color colore) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel lv = new JLabel(valore);
        lv.setFont(new Font("SansSerif", Font.BOLD, 20));
        lv.setForeground(colore);
        JLabel le = new JLabel(etichetta);
        le.setFont(new Font("SansSerif", Font.PLAIN, 11));
        le.setForeground(StyleWMS.GRIGIO_TESTO);
        p.add(lv);
        p.add(Box.createVerticalStrut(4));
        p.add(le);
        return p;
    }

    private JPanel buildFooter() {
        DashboardOperatore.RoundPanel footer =
                new DashboardOperatore.RoundPanel(StyleWMS.BIANCO, StyleWMS.AZZURRO_LIGHT, 10);
        footer.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
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

    private JScrollPane buildTable(String[] colonne, Object[][] dati) {
        DefaultTableModel model = new DefaultTableModel(dati, colonne) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(26);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(235, 235, 235));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(StyleWMS.AZZURRO_LIGHT);
        table.getTableHeader().setForeground(StyleWMS.BLU_ACCIAIO);
        table.setBackground(StyleWMS.BIANCO);
        table.setSelectionBackground(StyleWMS.AZZURRO_LIGHT);
        table.setSelectionForeground(StyleWMS.BLU_ACCIAIO);
        JScrollPane sp = new JScrollPane(table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        sp.setPreferredSize(new Dimension(0, 160));
        return sp;
    }

    private JLabel buildSectionLabel(String testo) {
        JLabel lbl = new JLabel();
        lbl.setText("<html><span style='letter-spacing:2px'>" + testo.toUpperCase() + "</span></html>");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(new Color(0x88, 0x88, 0x88));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // ── Logout ───────────────────────────────────────────────────
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
                g2.setColor(hovered ? new Color(180,30,30) : new Color(198,40,40));
                g2.fill(new RoundRectangle2D.Float(8, 0, getWidth()-16, getHeight(), 12, 12));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.setBorder(new EmptyBorder(4, 20, 4, 16));
        JLabel ico = new JLabel("⏻");
        ico.setFont(new Font("SansSerif", Font.PLAIN, 14));
        ico.setForeground(StyleWMS.BIANCO);
        ico.setBorder(new EmptyBorder(0,0,0,10));
        JLabel lbl = new JLabel("Esci");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(StyleWMS.BIANCO);
        card.add(ico);
        card.add(lbl);
        return card;
    }

    // ── Helpers ──────────────────────────────────────────────────
    private String buildInitials(String nome) {
        String[] parti = nome.trim().split("\\s+");
        if (parti.length == 1) return parti[0].substring(0, Math.min(2, parti[0].length())).toUpperCase();
        return ("" + parti[0].charAt(0) + parti[parti.length-1].charAt(0)).toUpperCase();
    }

    // ── main ─────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            Responsabile respTesting = new Responsabile(
                    "Amedeo",
                    "Catanese Napolitano",
                    "amedeoSup@mail.it",
                    "123456",
                    "SUPER-001"
            );

            String logo = args.length > 1 ? args[1] : null;
            new DashboardResponsabile(respTesting, logo).setVisible(true);
        });
    }
}