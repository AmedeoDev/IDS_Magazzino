package it.unina.magazzino.boundary;

import it.unina.magazzino.boundary.utils.StyleWMS;
import it.unina.magazzino.boundary.utils.ExcelExporter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Pannello "Prodotti Sotto Scorta" — RF10 / RF08
 *
 * Funzionalità:
 *  - Lista prodotti con quantità inferiore alla soglia minima (RF10)
 *  - Indicatore visivo della criticità (livello basso / critico)
 *  - Azione "Riordina" per avviare un ordine di rifornimento (RF08)
 *  - Riepilogo KPI: totale sotto scorta, media scarto, prodotto più critico
 */
public class ProdottiSottoScorta extends JPanel {

    private DefaultTableModel tableModel;
    private JTable tabella;

    private static final String[] COLONNE = {
            "ID", "Nome Prodotto", "Categoria", "Disponibile", "Soglia Min.", "Scarto", "Livello", "Azione"
    };

    // Demo: solo prodotti in stato critico (qty < soglia)
    private Object[][] datiDemo = {
            {"000011","Guanti latex",        "Sicurezza",   "3",  "10", "-7",  "Critico",  "Riordina"},
            {"000045","Nastro da imballo",   "Imballaggio", "1",  "5",  "-4",  "Critico",  "Riordina"},
            {"000078","Scatole S",            "Imballaggio", "4",  "20", "-16", "Critico",  "Riordina"},
            {"000102","Etichette adesive",   "Cancelleria", "8",  "15", "-7",  "Basso",    "Riordina"},
            {"000134","Pallets 80x120",       "Logistica",   "2",  "6",  "-4",  "Critico",  "Riordina"},
    };

    public ProdottiSottoScorta() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildBottomActions(), BorderLayout.SOUTH);
    }

    // ── Header: sezione label + KPI strip ────────────────────────
    private JPanel buildHeader() {
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));

        // Etichetta
        JLabel sezione = new JLabel("PRODOTTI SOTTO SCORTA  ·  RF10");
        sezione.setFont(new Font("SansSerif", Font.BOLD, 10));
        sezione.setForeground(new Color(0x88, 0x88, 0x88));
        sezione.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.add(sezione);
        wrap.add(Box.createVerticalStrut(10));

        // KPI strip (3 card)
        JPanel kpiRow = new JPanel(new GridLayout(1, 3, 12, 0));
        kpiRow.setOpaque(false);
        kpiRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        kpiRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        kpiRow.add(buildKpiCard("Prodotti sotto scorta", "5",  new Color(198,40,40)));
        kpiRow.add(buildKpiCard("Scarto medio",          "-7.6", new Color(180,60,10)));
        kpiRow.add(buildKpiCard("Prodotto più critico",  "Scatole S (−16)", StyleWMS.BLU_MEDIO));

        wrap.add(kpiRow);
        wrap.add(Box.createVerticalStrut(10));

        // Banner avviso
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        banner.setOpaque(false);
        banner.setBackground(new Color(255, 243, 205));
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 193, 7, 120), 1, true),
                new EmptyBorder(2, 6, 2, 6)));
        JLabel iconaAvviso = new JLabel("⚠");
        iconaAvviso.setFont(new Font("SansSerif", Font.PLAIN, 14));
        iconaAvviso.setForeground(new Color(180, 100, 0));
        JLabel testoAvviso = new JLabel(
                "Attenzione: 3 prodotti sono al di sotto del livello critico. Considera un riordino urgente.");
        testoAvviso.setFont(new Font("SansSerif", Font.PLAIN, 12));
        testoAvviso.setForeground(new Color(130, 70, 0));
        banner.add(iconaAvviso);
        banner.add(testoAvviso);
        banner.setAlignmentX(Component.LEFT_ALIGNMENT);
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        wrap.add(banner);
        wrap.add(Box.createVerticalStrut(4));
        return wrap;
    }

    private JPanel buildKpiCard(String label, String valore, Color colore) {
        DashboardOperatore.RoundPanel card =
                new DashboardOperatore.RoundPanel(StyleWMS.BIANCO, new Color(198,40,40,50), 10);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel v = new JLabel(valore);
        v.setFont(new Font("SansSerif", Font.BOLD, 22));
        v.setForeground(colore);

        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.PLAIN, 11));
        l.setForeground(StyleWMS.GRIGIO_TESTO);

        card.add(v);
        card.add(Box.createVerticalStrut(3));
        card.add(l);
        return card;
    }

    // ── Tabella prodotti sotto scorta ────────────────────────────
    private JPanel buildTablePanel() {
        DashboardOperatore.RoundPanel panel =
                new DashboardOperatore.RoundPanel(StyleWMS.BIANCO, new Color(198, 40, 40, 40), 10);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        tableModel = new DefaultTableModel(datiDemo, COLONNE) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabella = new JTable(tableModel);
        tabella.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tabella.setRowHeight(30);
        tabella.setShowHorizontalLines(true);
        tabella.setShowVerticalLines(false);
        tabella.setGridColor(new Color(240, 220, 220));
        tabella.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabella.getTableHeader().setBackground(new Color(255, 230, 230));
        tabella.getTableHeader().setForeground(new Color(160, 30, 30));
        tabella.setBackground(StyleWMS.BIANCO);
        tabella.setSelectionBackground(new Color(255, 220, 220));
        tabella.setSelectionForeground(new Color(160, 30, 30));
        tabella.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Larghezze colonne
        int[] widths = {70, 160, 110, 80, 85, 60, 80, 90};
        for (int i = 0; i < widths.length; i++)
            tabella.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Renderer colonna "Livello" → badge colorato
        tabella.getColumnModel().getColumn(6).setCellRenderer(
                new javax.swing.table.DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable t, Object v,
                                                                   boolean sel, boolean foc, int row, int col) {
                        JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                        String liv = v == null ? "" : v.toString();
                        lbl.setHorizontalAlignment(CENTER);
                        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
                        if (!sel) {
                            if ("Critico".equals(liv)) {
                                lbl.setForeground(Color.WHITE);
                                lbl.setBackground(new Color(198, 40, 40));
                            } else {
                                lbl.setForeground(new Color(160, 80, 0));
                                lbl.setBackground(new Color(255, 243, 205));
                            }
                        }
                        setOpaque(true);
                        return lbl;
                    }
                });

        // Renderer colonna "Azione" → bottone simulato
        tabella.getColumnModel().getColumn(7).setCellRenderer(
                new javax.swing.table.DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable t, Object v,
                                                                   boolean sel, boolean foc, int row, int col) {
                        JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                        lbl.setText("⟳  Riordina");
                        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
                        lbl.setForeground(StyleWMS.BLU_ACCIAIO);
                        lbl.setBackground(sel ? StyleWMS.AZZURRO_LIGHT : new Color(230, 240, 255));
                        lbl.setHorizontalAlignment(CENTER);
                        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        setOpaque(true);
                        return lbl;
                    }
                });

        // Click su colonna "Azione"
        tabella.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tabella.columnAtPoint(e.getPoint());
                int row = tabella.rowAtPoint(e.getPoint());
                if (col == 7 && row >= 0) {
                    String nomeProd = tableModel.getValueAt(row, 1).toString();
                    String qty      = tableModel.getValueAt(row, 3).toString();
                    String soglia   = tableModel.getValueAt(row, 4).toString();
                    int riordino    = Integer.parseInt(soglia) * 2; // logica placeholder
                    int conferma = JOptionPane.showConfirmDialog(ProdottiSottoScorta.this,
                            "<html>Prodotto: <b>" + nomeProd + "</b><br>" +
                                    "Quantità attuale: <b>" + qty + "</b>  — Soglia: <b>" + soglia + "</b><br><br>" +
                                    "Vuoi avviare un ordine di riordino per <b>" + riordino + " unità</b>?</html>",
                            "Conferma Riordino", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (conferma == JOptionPane.YES_OPTION) {
                        JOptionPane.showMessageDialog(ProdottiSottoScorta.this,
                                "Ordine di riordino registrato per «" + nomeProd + "» (" + riordino + " unità).",
                                "Riordino Inviato", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        JScrollPane sp = new JScrollPane(tabella,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        panel.add(sp, BorderLayout.CENTER);

        // Legenda
        JPanel legenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        legenda.setOpaque(false);
        legenda.add(buildLegendChip(new Color(198,40,40), Color.WHITE, "Critico"));
        legenda.add(buildLegendChip(new Color(255,243,205), new Color(160,80,0), "Basso"));
        panel.add(legenda, BorderLayout.SOUTH);
        return panel;
    }

    private JLabel buildLegendChip(Color bg, Color fg, String testo) {
        JLabel l = new JLabel("  " + testo + "  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(fg);
        l.setOpaque(false);
        l.setBorder(new EmptyBorder(2, 4, 2, 4));
        return l;
    }

    // ── Bottoni fondo ────────────────────────────────────────────
    private JPanel buildBottomActions() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bar.setOpaque(false);

        JButton btnTutti = GestisciProdotti.buildPrimaryButton("⟳  Riordina Tutti", new Color(198,40,40));
        btnTutti.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Ordini di riordino inviati per tutti i " + tableModel.getRowCount() + " prodotti critici.",
                        "Riordino Massivo", JOptionPane.INFORMATION_MESSAGE));
        bar.add(btnTutti);

        JButton btnEsporta = GestisciProdotti.buildPrimaryButton("⬇  Esporta Excel", StyleWMS.BLU_MEDIO);
        btnEsporta.addActionListener(e ->
                ExcelExporter.esporta(this, tableModel, "Prodotti Sotto Scorta", "sotto_scorta"));
        bar.add(btnEsporta);
        return bar;
    }
}