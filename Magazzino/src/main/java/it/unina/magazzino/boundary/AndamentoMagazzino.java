package it.unina.magazzino.boundary;

import it.unina.magazzino.boundary.utils.StyleWMS;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Pannello "Andamento Magazzino" — RF13 / RF14
 *
 * Funzionalità:
 *  - Grafici andamento movimenti (carichi/scarichi) negli ultimi 7 giorni (RF13)
 *  - Prodotti più movimentati nel periodo (RF13)
 *  - Indicatori riepilogo: operazioni totali, operatori attivi (RF14)
 *  - Selettore periodo: 7 giorni / 30 giorni / 90 giorni
 */
public class AndamentoMagazzino extends JPanel {

    // Dati demo per i 3 periodi
    private static final String[][] GIORNI_7 = {
            {"26/05","27/05","28/05","29/05","30/05","31/05","01/06"},
    };
    private static final int[] CARICHI_7  = {8,  14, 11, 18, 22, 15, 27};
    private static final int[] SCARICHI_7 = {5,  9,  7,  13, 12, 10, 20};

    private static final int[] CARICHI_30  = {60, 72, 55, 88, 95, 70, 110};
    private static final int[] SCARICHI_30 = {40, 50, 38, 65, 70, 52,  80};
    private static final String[][] GIORNI_30 = {{"Sett.1","Sett.2","Sett.3","Sett.4","Sett.5","Sett.6","Sett.7"}};

    private static final int[] CARICHI_90  = {200,230,215,260,280,240,310};
    private static final int[] SCARICHI_90 = {140,160,150,190,200,175,220};
    private static final String[][] GIORNI_90 = {{"Mese1W1","Mese1W2","Mese2W1","Mese2W2","Mese3W1","Mese3W2","Mese3W3"}};

    // Prodotti più movimentati
    private static final String[] TOP_PRODOTTI = {
            "Guanti latex", "Scatole S", "Nastro da imballo", "Pallets 80x120", "Etichette"
    };
    private static final int[] TOP_VALORI = {85, 71, 64, 58, 45};

    private BarChartPanel barChart;
    private JLabel lblPeriodo;
    private int periodoSelezionato = 7; // default 7 gg

    // KPI dinamici per periodo
    private static final int[] KPI_TOT = {115, 820, 2380};
    private static final int[] KPI_OPE = {8,   9,   11};

    private JLabel kpiTotLbl, kpiOpeLbl;

    public AndamentoMagazzino() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        centro.add(buildKpiRow());
        centro.add(Box.createVerticalStrut(14));
        centro.add(buildBarChartSection());
        centro.add(Box.createVerticalStrut(14));
        centro.add(buildTopProdottiSection());

        JScrollPane scroll = new JScrollPane(centro,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        add(scroll, BorderLayout.CENTER);
    }

    // ── Header + selettore periodo ───────────────────────────────
    private JPanel buildHeader() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);

        JLabel sezione = new JLabel("ANDAMENTO MAGAZZINO  ·  RF13 / RF14");
        sezione.setFont(new Font("SansSerif", Font.BOLD, 10));
        sezione.setForeground(new Color(0x88, 0x88, 0x88));

        // Toggle periodo
        JPanel toggle = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        toggle.setOpaque(false);

        for (String p : new String[]{"7 giorni", "30 giorni", "90 giorni"}) {
            int gg = Integer.parseInt(p.split(" ")[0]);
            JButton btn = buildToggleButton(p, gg == periodoSelezionato);
            btn.addActionListener(e -> {
                periodoSelezionato = gg;
                aggiornaVista();
                for (Component c : toggle.getComponents()) {
                    if (c instanceof JButton b) {
                        int bg2 = Integer.parseInt(b.getActionCommand());
                        boolean sel2 = bg2 == periodoSelezionato;
                        b.setBackground(sel2 ? StyleWMS.BLU_ACCIAIO : new Color(230,236,244));
                        b.setForeground(sel2 ? Color.WHITE : StyleWMS.BLU_ACCIAIO);
                    }
                }
            });
            btn.setActionCommand(String.valueOf(gg));
            toggle.add(btn);
        }

        wrap.add(sezione, BorderLayout.WEST);
        wrap.add(toggle, BorderLayout.EAST);
        return wrap;
    }

    private JButton buildToggleButton(String testo, boolean attivo) {
        JButton btn = new JButton(testo) {
            {
                setContentAreaFilled(true);
                setFocusPainted(false);
                setBorderPainted(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setBackground(attivo ? StyleWMS.BLU_ACCIAIO : new Color(230,236,244));
        btn.setForeground(attivo ? Color.WHITE : StyleWMS.BLU_ACCIAIO);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(StyleWMS.AZZURRO_LIGHT, 1, true),
                new EmptyBorder(5, 14, 5, 14)));
        return btn;
    }

    // ── KPI row ──────────────────────────────────────────────────
    private JPanel buildKpiRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 12, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

        kpiTotLbl = kpiLbl(String.valueOf(KPI_TOT[0]), StyleWMS.BLU_ACCIAIO);
        kpiOpeLbl = kpiLbl(String.valueOf(KPI_OPE[0]), new Color(46,125,50));

        row.add(buildKpiCard("Operazioni periodo",  kpiTotLbl, "📦"));
        row.add(buildKpiCard("Operatori coinvolti", kpiOpeLbl, "👷"));
        return row;
    }

    private JLabel kpiLbl(String val, Color col) {
        JLabel l = new JLabel(val);
        l.setFont(new Font("SansSerif", Font.BOLD, 26));
        l.setForeground(col);
        return l;
    }

    private JPanel buildKpiCard(String label, JLabel valLbl, String icona) {
        DashboardOperatore.RoundPanel card =
                new DashboardOperatore.RoundPanel(StyleWMS.BIANCO, StyleWMS.AZZURRO_LIGHT, 10);
        card.setLayout(new BorderLayout(8, 0));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel ico = new JLabel(icona);
        ico.setFont(new Font("SansSerif", Font.PLAIN, 24));
        ico.setVerticalAlignment(SwingConstants.CENTER);

        JPanel testi = new JPanel();
        testi.setOpaque(false);
        testi.setLayout(new BoxLayout(testi, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(StyleWMS.GRIGIO_TESTO);

        testi.add(valLbl);
        testi.add(Box.createVerticalStrut(2));
        testi.add(lbl);

        card.add(ico, BorderLayout.WEST);
        card.add(testi, BorderLayout.CENTER);
        return card;
    }

    // ── Grafico a barre carichi/scarichi ─────────────────────────
    private JPanel buildBarChartSection() {
        DashboardOperatore.RoundPanel panel =
                new DashboardOperatore.RoundPanel(StyleWMS.BIANCO, StyleWMS.AZZURRO_LIGHT, 10);
        panel.setLayout(new BorderLayout(0, 8));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        JPanel titoloRiga = new JPanel(new BorderLayout());
        titoloRiga.setOpaque(false);

        JLabel titolo = new JLabel("Movimenti giornalieri");
        titolo.setFont(new Font("SansSerif", Font.BOLD, 13));
        titolo.setForeground(StyleWMS.BLU_ACCIAIO);

        JPanel legenda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        legenda.setOpaque(false);
        legenda.add(chipLegenda(StyleWMS.BLU_ACCIAIO, "Carichi"));
        legenda.add(chipLegenda(new Color(46,125,50), "Scarichi"));

        titoloRiga.add(titolo, BorderLayout.WEST);
        titoloRiga.add(legenda, BorderLayout.EAST);

        barChart = new BarChartPanel(GIORNI_7[0], CARICHI_7, SCARICHI_7);
        barChart.setPreferredSize(new Dimension(0, 180));

        panel.add(titoloRiga, BorderLayout.NORTH);
        panel.add(barChart, BorderLayout.CENTER);
        return panel;
    }

    private JLabel chipLegenda(Color col, String testo) {
        JLabel l = new JLabel("  " + testo);
        l.setFont(new Font("SansSerif", Font.PLAIN, 11));
        l.setForeground(StyleWMS.GRIGIO_TESTO);
        l.setIcon(new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(col);
                g.fillRoundRect(x, y+2, 10, 10, 3, 3);
            }
            public int getIconWidth()  { return 12; }
            public int getIconHeight() { return 14; }
        });
        return l;
    }

    // ── Top prodotti movimentati ─────────────────────────────────
    private JPanel buildTopProdottiSection() {
        DashboardOperatore.RoundPanel panel =
                new DashboardOperatore.RoundPanel(StyleWMS.BIANCO, StyleWMS.AZZURRO_LIGHT, 10);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel titolo = new JLabel("Prodotti più movimentati nel periodo");
        titolo.setFont(new Font("SansSerif", Font.BOLD, 13));
        titolo.setForeground(StyleWMS.BLU_ACCIAIO);
        panel.add(titolo, BorderLayout.NORTH);

        JPanel barre = new JPanel();
        barre.setOpaque(false);
        barre.setLayout(new BoxLayout(barre, BoxLayout.Y_AXIS));

        int max = TOP_VALORI[0];
        for (int i = 0; i < TOP_PRODOTTI.length; i++) {
            barre.add(buildBarRow(TOP_PRODOTTI[i], TOP_VALORI[i], max, i));
            barre.add(Box.createVerticalStrut(6));
        }
        panel.add(barre, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBarRow(String nome, int valore, int max, int idx) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nomeLbl = new JLabel(nome);
        nomeLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        nomeLbl.setForeground(StyleWMS.GRIGIO_TESTO);
        nomeLbl.setPreferredSize(new Dimension(160, 20));

        Color[] colori = {StyleWMS.BLU_ACCIAIO, StyleWMS.BLU_MEDIO, new Color(46,125,50),
                new Color(123,31,162), new Color(180,60,10)};
        final Color c = colori[idx % colori.length];
        final float pct = (float) valore / max;

        JPanel barra = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 235, 245));
                g2.fill(new RoundRectangle2D.Float(0, 2, getWidth(), getHeight()-4, 6, 6));
                int w = (int)(getWidth() * pct);
                if (w > 0) {
                    g2.setColor(c);
                    g2.fill(new RoundRectangle2D.Float(0, 2, w, getHeight()-4, 6, 6));
                }
                g2.dispose();
            }
        };
        barra.setOpaque(false);

        JLabel valLbl = new JLabel(String.valueOf(valore));
        valLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        valLbl.setForeground(c);
        valLbl.setPreferredSize(new Dimension(34, 20));
        valLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(nomeLbl, BorderLayout.WEST);
        row.add(barra, BorderLayout.CENTER);
        row.add(valLbl, BorderLayout.EAST);
        return row;
    }

    // ── Aggiorna dati al cambio periodo ─────────────────────────
    private void aggiornaVista() {
        int idx = periodoSelezionato == 7 ? 0 : periodoSelezionato == 30 ? 1 : 2;
        int[] carichi  = idx == 0 ? CARICHI_7  : idx == 1 ? CARICHI_30  : CARICHI_90;
        int[] scarichi = idx == 0 ? SCARICHI_7 : idx == 1 ? SCARICHI_30 : SCARICHI_90;
        String[] etich = idx == 0 ? GIORNI_7[0] : idx == 1 ? GIORNI_30[0] : GIORNI_90[0];

        barChart.setDati(etich, carichi, scarichi);
        kpiTotLbl.setText(String.valueOf(KPI_TOT[idx]));
        kpiOpeLbl.setText(String.valueOf(KPI_OPE[idx]));
    }

    // ══════════════════════════════════════════════════════════════
    // Componente grafico a barre doppia (Carichi / Scarichi)
    // ══════════════════════════════════════════════════════════════
    static class BarChartPanel extends JPanel {

        private String[] etichette;
        private int[]    carichi;
        private int[]    scarichi;

        private static final Color COL_CARICO  = StyleWMS.BLU_ACCIAIO;
        private static final Color COL_SCARICO = new Color(46, 125, 50);
        private static final int   BAR_GAP     = 3;
        private static final int   GROUP_GAP   = 14;
        private static final int   PAD_LEFT    = 38;
        private static final int   PAD_BOTTOM  = 28;
        private static final int   PAD_TOP     = 16;
        private static final int   PAD_RIGHT   = 16;

        BarChartPanel(String[] etichette, int[] carichi, int[] scarichi) {
            this.etichette = etichette;
            this.carichi   = carichi;
            this.scarichi  = scarichi;
            setOpaque(false);
        }

        void setDati(String[] etichette, int[] carichi, int[] scarichi) {
            this.etichette = etichette;
            this.carichi   = carichi;
            this.scarichi  = scarichi;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int W = getWidth();
            int H = getHeight();
            int chartW = W - PAD_LEFT - PAD_RIGHT;
            int chartH = H - PAD_BOTTOM - PAD_TOP;

            int maxV = 1;
            for (int v : carichi)  maxV = Math.max(maxV, v);
            for (int v : scarichi) maxV = Math.max(maxV, v);
            maxV = (int)(maxV * 1.15);

            int n = etichette.length;
            int groupW = (chartW - GROUP_GAP * (n - 1)) / n;
            int barW   = (groupW - BAR_GAP) / 2;

            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            for (int step = 0; step <= 4; step++) {
                int yLine = PAD_TOP + chartH - (int)((float) step / 4 * chartH);
                int valLine = maxV * step / 4;
                g2.setColor(new Color(220, 225, 235));
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        1f, new float[]{4f, 4f}, 0));
                g2.drawLine(PAD_LEFT, yLine, PAD_LEFT + chartW, yLine);
                g2.setColor(new Color(160, 160, 160));
                g2.setStroke(new BasicStroke(1));
                g2.drawString(String.valueOf(valLine), 2, yLine + 4);
            }

            for (int i = 0; i < n; i++) {
                int x0 = PAD_LEFT + i * (groupW + GROUP_GAP);

                int hC = (int)((float) carichi[i] / maxV * chartH);
                int yC = PAD_TOP + chartH - hC;
                g2.setColor(COL_CARICO);
                g2.fill(new RoundRectangle2D.Float(x0, yC, barW, hC, 4, 4));

                int hS = (int)((float) scarichi[i] / maxV * chartH);
                int yS = PAD_TOP + chartH - hS;
                g2.setColor(COL_SCARICO);
                g2.fill(new RoundRectangle2D.Float(x0 + barW + BAR_GAP, yS, barW, hS, 4, 4));

                g2.setColor(new Color(120,120,120));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                FontMetrics fm = g2.getFontMetrics();
                String lbl = etichette[i];
                int lx = x0 + groupW / 2 - fm.stringWidth(lbl) / 2;
                g2.drawString(lbl, lx, PAD_TOP + chartH + 16);
            }

            g2.setColor(new Color(200, 205, 215));
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(PAD_LEFT, PAD_TOP, PAD_LEFT, PAD_TOP + chartH);
            g2.drawLine(PAD_LEFT, PAD_TOP + chartH, PAD_LEFT + chartW, PAD_TOP + chartH);

            g2.dispose();
        }
    }
}