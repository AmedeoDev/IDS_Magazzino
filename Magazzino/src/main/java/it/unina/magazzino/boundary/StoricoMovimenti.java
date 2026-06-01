package it.unina.magazzino.boundary;

import it.unina.magazzino.boundary.utils.StyleWMS;
import it.unina.magazzino.boundary.utils.ExcelExporter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Pannello "Storico Movimenti" — RF11 / RF12
 *
 * Funzionalità:
 *  - Visualizzazione di tutti i movimenti di magazzino (RF11)
 *  - Filtro per tipo (Carico / Scarico / Tutti), data e operatore (RF12)
 *  - Badge colorati per distinguere carichi e scarichi a colpo d'occhio
 *  - Esportazione (placeholder) dello storico in CSV
 */
public class StoricoMovimenti extends JPanel {

    private DefaultTableModel tableModel;
    private JTable tabella;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField campoRicerca;
    private JComboBox<String> filtroTipo;
    private JTextField campoDataDal;
    private JTextField campoDataAl;

    private static final String[] COLONNE = {
            "#", "ID Prodotto", "Prodotto", "Tipo", "Quantità", "Data", "Operatore", "Note"
    };

    private static final Object[][] MOVIMENTI_DEMO = {
            {"0001","000011","Guanti latex",       "Carico",  "+50","01/06/2026","m.verdi@wms.it",    "Rifornimento"},
            {"0002","000045","Nastro da imballo",  "Scarico", "-20","01/06/2026","a.bianchi@wms.it",  "Ordine #4412"},
            {"0003","000078","Scatole S",           "Carico",  "+30","31/05/2026","m.verdi@wms.it",    "Rifornimento"},
            {"0004","000102","Etichette adesive",  "Scarico", "-5", "31/05/2026","l.neri@wms.it",     "Ordine #4411"},
            {"0005","000134","Pallets 80x120",      "Carico",  "+100","30/05/2026","a.bianchi@wms.it", "Rifornimento"},
            {"0006","000156","Bolla cartone",       "Scarico", "-12","30/05/2026","m.verdi@wms.it",    "Ordine #4410"},
            {"0007","000178","Elmetti gialli",      "Carico",  "+8", "29/05/2026","l.neri@wms.it",     "Urgenza"},
            {"0008","000201","Guanti nitrile",      "Carico",  "+40","29/05/2026","a.bianchi@wms.it",  "Rifornimento"},
            {"0009","000222","Cutter 18mm",         "Scarico", "-3", "28/05/2026","m.verdi@wms.it",    "Manutenzione"},
            {"0010","000244","Fascette cavi",       "Carico",  "+200","28/05/2026","l.neri@wms.it",    "Stock iniziale"},
            {"0011","000011","Guanti latex",        "Scarico", "-10","27/05/2026","a.bianchi@wms.it",  "Ordine #4409"},
            {"0012","000078","Scatole S",           "Scarico", "-15","27/05/2026","m.verdi@wms.it",    "Ordine #4408"},
    };

    public StoricoMovimenti() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildExportBar(), BorderLayout.SOUTH);
    }

    // ── Header: titolo + filtri ──────────────────────────────────
    private JPanel buildHeader() {
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));

        // Etichetta sezione
        JLabel sezione = new JLabel("STORICO MOVIMENTI  ·  RF11 / RF12");
        sezione.setFont(new Font("SansSerif", Font.BOLD, 10));
        sezione.setForeground(new Color(0x88, 0x88, 0x88));
        sezione.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.add(sezione);
        wrap.add(Box.createVerticalStrut(10));

        // Riga filtri
        DashboardOperatore.RoundPanel filtriCard =
                new DashboardOperatore.RoundPanel(StyleWMS.BIANCO, StyleWMS.AZZURRO_LIGHT, 10);
        filtriCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));
        filtriCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        filtriCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Ricerca libera
        filtriCard.add(filtroLabel("🔍 Cerca:"));
        campoRicerca = buildFiltroField("Nome, ID, operatore…", 160);
        filtriCard.add(campoRicerca);

        // Tipo movimento
        filtriCard.add(filtroLabel("Tipo:"));
        filtroTipo = new JComboBox<>(new String[]{"Tutti", "Carico", "Scarico"});
        filtroTipo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        filtroTipo.setBackground(StyleWMS.BIANCO);
        filtroTipo.addActionListener(e -> applicaFiltri());
        filtriCard.add(filtroTipo);

        // Filtro data dal/al
        filtriCard.add(filtroLabel("Dal:"));
        campoDataDal = buildFiltroField("gg/mm/aaaa", 100);
        filtriCard.add(campoDataDal);
        filtriCard.add(filtroLabel("Al:"));
        campoDataAl = buildFiltroField("gg/mm/aaaa", 100);
        filtriCard.add(campoDataAl);

        // Bottone applica filtri
        JButton btnFiltri = GestisciProdotti.buildPrimaryButton("Filtra", StyleWMS.BLU_ACCIAIO);
        btnFiltri.addActionListener(e -> applicaFiltri());
        filtriCard.add(btnFiltri);

        // Reset
        JButton btnReset = GestisciProdotti.buildPrimaryButton("✕ Reset", new Color(160,160,160));
        btnReset.addActionListener(e -> {
            campoRicerca.setText("");
            filtroTipo.setSelectedIndex(0);
            campoDataDal.setText("");
            campoDataAl.setText("");
            sorter.setRowFilter(null);
        });
        filtriCard.add(btnReset);

        wrap.add(filtriCard);
        wrap.add(Box.createVerticalStrut(4));
        return wrap;
    }

    private JLabel filtroLabel(String testo) {
        JLabel l = new JLabel(testo);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setForeground(StyleWMS.GRIGIO_TESTO);
        return l;
    }

    private JTextField buildFiltroField(String hint, int width) {
        JTextField f = new JTextField(hint) {
            { addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) { if (getText().equals(hint)) { setText(""); setForeground(StyleWMS.GRIGIO_TESTO); } }
                public void focusLost (FocusEvent e) { if (getText().isEmpty()) { setText(hint); setForeground(Color.LIGHT_GRAY); } }
            }); setForeground(Color.LIGHT_GRAY); }
        };
        f.setFont(new Font("SansSerif", Font.PLAIN, 12));
        f.setPreferredSize(new Dimension(width, 30));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(StyleWMS.AZZURRO_LIGHT, 1, true),
                new EmptyBorder(4, 8, 4, 8)));
        f.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { applicaFiltri(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { applicaFiltri(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        });
        return f;
    }

    // ── Tabella movimenti ────────────────────────────────────────
    private JPanel buildTablePanel() {
        DashboardOperatore.RoundPanel panel =
                new DashboardOperatore.RoundPanel(StyleWMS.BIANCO, StyleWMS.AZZURRO_LIGHT, 10);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        tableModel = new DefaultTableModel(MOVIMENTI_DEMO, COLONNE) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabella = new JTable(tableModel);
        tabella.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tabella.setRowHeight(28);
        tabella.setShowHorizontalLines(true);
        tabella.setShowVerticalLines(false);
        tabella.setGridColor(new Color(235, 235, 235));
        tabella.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabella.getTableHeader().setBackground(StyleWMS.AZZURRO_LIGHT);
        tabella.getTableHeader().setForeground(StyleWMS.BLU_ACCIAIO);
        tabella.setBackground(StyleWMS.BIANCO);
        tabella.setSelectionBackground(StyleWMS.AZZURRO_LIGHT);
        tabella.setSelectionForeground(StyleWMS.BLU_ACCIAIO);
        tabella.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Colonne larghezze
        int[] widths = {40, 80, 140, 75, 70, 95, 150, 140};
        for (int i = 0; i < widths.length; i++)
            tabella.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Renderer badge colorato per colonna "Tipo"
        tabella.getColumnModel().getColumn(3).setCellRenderer(
                new javax.swing.table.DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable t, Object v,
                                                                   boolean sel, boolean foc, int row, int col) {
                        JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                        String tipo = v == null ? "" : v.toString();
                        if (!sel) {
                            if ("Carico".equals(tipo)) {
                                lbl.setForeground(new Color(30, 130, 50));
                                lbl.setBackground(new Color(220, 255, 230));
                            } else {
                                lbl.setForeground(new Color(170, 30, 30));
                                lbl.setBackground(new Color(255, 230, 230));
                            }
                        }
                        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
                        lbl.setHorizontalAlignment(CENTER);
                        setOpaque(true);
                        return lbl;
                    }
                });

        sorter = new TableRowSorter<>(tableModel);
        tabella.setRowSorter(sorter);

        JScrollPane sp = new JScrollPane(tabella,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        panel.add(sp, BorderLayout.CENTER);

        // Counter righe visibili
        JLabel counter = new JLabel();
        counter.setFont(new Font("SansSerif", Font.ITALIC, 11));
        counter.setForeground(new Color(0x88, 0x88, 0x88));
        counter.setBorder(new EmptyBorder(6, 2, 0, 0));
        aggiornaCounter(counter);
        sorter.addRowSorterListener(e -> aggiornaCounter(counter));
        panel.add(counter, BorderLayout.SOUTH);

        return panel;
    }

    private void aggiornaCounter(JLabel lbl) {
        int vis = tabella.getRowCount();
        int tot = tableModel.getRowCount();
        lbl.setText("Visualizzati " + vis + " di " + tot + " movimenti");
    }

    // ── Barra esporta ────────────────────────────────────────────
    private JPanel buildExportBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bar.setOpaque(false);

        JButton btnExport = GestisciProdotti.buildPrimaryButton("⬇  Esporta Excel", StyleWMS.BLU_MEDIO);
        btnExport.addActionListener(e ->
                ExcelExporter.esporta(this, tableModel, "Storico Movimenti", "storico_movimenti"));
        bar.add(btnExport);
        return bar;
    }

    // ── Logica filtri ────────────────────────────────────────────
    private void applicaFiltri() {
        String testo = campoRicerca.getText().trim();
        String tipo  = (String) filtroTipo.getSelectedItem();
        String dal   = campoDataDal.getText().trim();
        String al    = campoDataAl.getText().trim();

        List<RowFilter<DefaultTableModel, Object>> filtri = new ArrayList<>();

        // Ricerca testo libero su colonne 1,2,6,7
        if (!testo.isEmpty() && !testo.equals("Nome, ID, operatore…")) {
            filtri.add(RowFilter.regexFilter("(?i)" + testo, 1, 2, 6, 7));
        }
        // Tipo movimento
        if (tipo != null && !"Tutti".equals(tipo)) {
            filtri.add(RowFilter.regexFilter("(?i)^" + tipo + "$", 3));
        }
        // Data semplice (filtro stringa — col 5)
        if (!dal.isEmpty() && !dal.equals("gg/mm/aaaa")) {
            filtri.add(RowFilter.regexFilter(dal, 5));
        }

        if (filtri.isEmpty()) {
            sorter.setRowFilter(null);
        } else if (filtri.size() == 1) {
            sorter.setRowFilter(filtri.get(0));
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filtri));
        }
    }
}