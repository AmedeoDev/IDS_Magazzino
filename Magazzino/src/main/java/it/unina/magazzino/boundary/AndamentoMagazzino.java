package it.unina.magazzino.boundary;

import it.unina.magazzino.boundary.utils.StyleWMS;
import it.unina.magazzino.control.MovimentoController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import java.util.List;

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

    private BarChartPanel barChart;
    private JLabel lblPeriodo;
    private int periodoSelezionato = 7; // default 7 gg


    private JLabel kpiTotLbl, kpiOpeLbl;

    private JPanel topProdottiPanel;

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

        topProdottiPanel = new JPanel(new BorderLayout());
        topProdottiPanel.setOpaque(false);
        topProdottiPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topProdottiPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        centro.add(topProdottiPanel);

        JScrollPane scroll = new JScrollPane(centro,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        add(scroll, BorderLayout.CENTER);

        aggiornaVista();
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

        kpiTotLbl = kpiLbl("...", StyleWMS.BLU_ACCIAIO);
        kpiOpeLbl = kpiLbl("...", new Color(46, 125, 50));

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

        barChart = new BarChartPanel(new String[0], new int[0], new int[0]);
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

    private JPanel buildBarreTop(List<String[]> top) {
        JPanel barre = new JPanel();
        barre.setOpaque(false);
        barre.setLayout(new BoxLayout(barre, BoxLayout.Y_AXIS));

        if (top.isEmpty()) {
            JLabel nessuno = new JLabel("Nessun movimento nel periodo selezionato.");
            nessuno.setFont(new Font("SansSerif", Font.ITALIC, 12));
            nessuno.setForeground(StyleWMS.GRIGIO_TESTO);
            barre.add(nessuno);
            return barre;
        }

        Color[] colori = {StyleWMS.BLU_ACCIAIO, StyleWMS.BLU_MEDIO,
                new Color(46, 125, 50), new Color(123, 31, 162), new Color(180, 60, 10)};

        int max = Integer.parseInt(top.get(0)[1]);
        for (int i = 0; i < top.size(); i++) {
            int valore = Integer.parseInt(top.get(i)[1]);
            barre.add(buildBarRow(top.get(i)[0], valore, max, colori[i % colori.length]));
            barre.add(Box.createVerticalStrut(6));
        }
        return barre;
    }

    private JPanel buildBarRow(String nome, int valore, int max, Color colore) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nomeLbl = new JLabel(nome);
        nomeLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        nomeLbl.setForeground(StyleWMS.GRIGIO_TESTO);
        nomeLbl.setPreferredSize(new Dimension(160, 20));

        final float pct = max > 0 ? (float) valore / max : 0;
        JPanel barra = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 235, 245));
                g2.fill(new RoundRectangle2D.Float(0, 2, getWidth(), getHeight() - 4, 6, 6));
                int w = (int) (getWidth() * pct);
                if (w > 0) {
                    g2.setColor(colore);
                    g2.fill(new RoundRectangle2D.Float(0, 2, w, getHeight() - 4, 6, 6));
                }
                g2.dispose();
            }
        };
        barra.setOpaque(false);

        JLabel valLbl = new JLabel(String.valueOf(valore));
        valLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        valLbl.setForeground(colore);
        valLbl.setPreferredSize(new Dimension(34, 20));
        valLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(nomeLbl, BorderLayout.WEST);
        row.add(barra, BorderLayout.CENTER);
        row.add(valLbl, BorderLayout.EAST);
        return row;
    }


    // ── Aggiorna dati al cambio periodo ─────────────────────────
    private void aggiornaVista() {

        MovimentoController controller = new MovimentoController();

        kpiTotLbl.setText(String.valueOf(controller.countMovimentiPeriodo(periodoSelezionato)));
        kpiOpeLbl.setText(String.valueOf(controller.countOperatoriDistantiPeriodo(periodoSelezionato)));

        List<String[]> dati = controller.getDatiGrafico(periodoSelezionato);
        if(!dati.isEmpty()){
            String[] etichetta = dati.stream().map(r -> r[0]).toArray(String[]::new);
            int[] carichi = dati.stream().mapToInt(r -> Integer.parseInt(r[1])).toArray();
            int[] scarichi = dati.stream().mapToInt(r -> Integer.parseInt(r[2])).toArray();
            barChart.setDati(etichetta, carichi, scarichi);
        } else {
            barChart.setDati(new String[]{"Nessun dato"}, new int[0], new int[0]);
        }

        List<String[]> top = controller.getProdottiMovimentati(5, periodoSelezionato);
        topProdottiPanel.removeAll();

        DashboardOperatore.RoundPanel wrapper = new DashboardOperatore.RoundPanel(StyleWMS.BIANCO, StyleWMS.AZZURRO_LIGHT, 10);
        wrapper.setLayout(new BorderLayout(0, 10));
        wrapper.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel titolo = new JLabel("Prodotti più movimentati nel periodo");
        titolo.setFont(new Font("SansSerif", Font.BOLD, 13));
        wrapper.add(titolo, BorderLayout.NORTH);
        wrapper.add(buildBarreTop(top), BorderLayout.CENTER);

        topProdottiPanel.add(wrapper, BorderLayout.CENTER);
        topProdottiPanel.revalidate();
        topProdottiPanel.repaint();

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

            if(etichette == null || etichette.length == 0 || carichi == null || carichi.length == 0 || scarichi == null || scarichi.length == 0) return;


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
            int groupW = n > 1 ? (chartW - GROUP_GAP * (n-1)) / n : chartW;
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