package it.unina.magazzino.boundary;

import it.unina.magazzino.boundary.utils.StyleWMS;
import it.unina.magazzino.boundary.utils.ExcelExporter;
import it.unina.magazzino.control.MovimentoController;
import it.unina.magazzino.entity.Movimento;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

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

    public StoricoMovimenti() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildExportBar(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));

        JLabel sezione = new JLabel("STORICO MOVIMENTI  ·  RF11 / RF12");
        sezione.setFont(new Font("SansSerif", Font.BOLD, 10));
        sezione.setForeground(new Color(0x88, 0x88, 0x88));
        sezione.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.add(sezione);
        wrap.add(Box.createVerticalStrut(10));

        DashboardOperatore.RoundPanel filtriCard =
                new DashboardOperatore.RoundPanel(StyleWMS.BIANCO, StyleWMS.AZZURRO_LIGHT, 10);
        filtriCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));
        filtriCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        filtriCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        filtriCard.add(filtroLabel("🔍 Cerca:"));
        campoRicerca = buildFiltroField("Nome, ID, operatore…", 160);
        filtriCard.add(campoRicerca);

        filtriCard.add(filtroLabel("Tipo:"));
        filtroTipo = new JComboBox<>(new String[]{"Tutti", "Carico", "Scarico"});
        filtroTipo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        filtroTipo.setBackground(StyleWMS.BIANCO);
        // ogni cambio di selezione del tipo riesegue il filtro sulla tabella
        filtroTipo.addActionListener(e -> applicaFiltri());
        filtriCard.add(filtroTipo);

        filtriCard.add(filtroLabel("Dal:"));
        campoDataDal = buildFiltroField("gg/mm/aaaa", 100);
        filtriCard.add(campoDataDal);
        filtriCard.add(filtroLabel("Al:"));
        campoDataAl = buildFiltroField("gg/mm/aaaa", 100);
        filtriCard.add(campoDataAl);

        // bottone che applica manualmente tutti i filtri impostati
        JButton btnFiltri = GestisciProdotti.buildPrimaryButton("Filtra", StyleWMS.BLU_ACCIAIO);
        btnFiltri.addActionListener(e -> applicaFiltri());
        filtriCard.add(btnFiltri);

        // reset: pulisce tutti i campi e mostra di nuovo tutta la tabella
        JButton btnReset = GestisciProdotti.buildPrimaryButton("✕ Reset", new Color(160, 160, 160));
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

    // crea un'etichetta stilizzata per le etichette dei filtri
    private JLabel filtroLabel(String testo) {
        JLabel l = new JLabel(testo);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setForeground(StyleWMS.GRIGIO_TESTO);
        return l;
    }

    private JTextField buildFiltroField(String hint, int width) {
        // mostra il testo suggerito quando il campo è vuoto e non ha il focus
        JTextField f = new JTextField(hint) {
            {
                addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent e) {
                        if (getText().equals(hint)) { setText(""); setForeground(StyleWMS.GRIGIO_TESTO); }
                    }
                    public void focusLost(FocusEvent e) {
                        if (getText().isEmpty()) { setText(hint); setForeground(Color.LIGHT_GRAY); }
                    }
                });
                setForeground(Color.LIGHT_GRAY);
            }
        };
        f.setFont(new Font("SansSerif", Font.PLAIN, 12));
        f.setPreferredSize(new Dimension(width, 30));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(StyleWMS.AZZURRO_LIGHT, 1, true),
                new EmptyBorder(4, 8, 4, 8)));
        // il filtro si aggiorna in tempo reale a ogni carattere digitato
        f.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { applicaFiltri(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { applicaFiltri(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        });
        return f;
    }

    private JPanel buildTablePanel() {
        DashboardOperatore.RoundPanel panel =
                new DashboardOperatore.RoundPanel(StyleWMS.BIANCO, StyleWMS.AZZURRO_LIGHT, 10);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // tabella non editabile: lo storico è solo in lettura
        tableModel = new DefaultTableModel(new Object[][]{}, COLONNE) {
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

        int[] widths = {40, 80, 140, 75, 70, 95, 150, 140};
        for (int i = 0; i < widths.length; i++)
            tabella.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // renderer della colonna "Tipo": verde per Carico, rosso per Scarico
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

        // contatore righe visibili: si aggiorna ogni volta che cambia l'ordinamento o il filtro
        JLabel counter = new JLabel();
        counter.setFont(new Font("SansSerif", Font.ITALIC, 11));
        counter.setForeground(new Color(0x88, 0x88, 0x88));
        counter.setBorder(new EmptyBorder(6, 2, 0, 0));
        aggiornaCounter(counter);
        sorter.addRowSorterListener(e -> aggiornaCounter(counter));
        panel.add(counter, BorderLayout.SOUTH);

        // carica i dati dal database al primo avvio del pannello
        caricaDatiDalDB();

        return panel;
    }

    private void caricaDatiDalDB() {
        tableModel.setRowCount(0);
        try {
            // prende gli ultimi 200 movimenti per non appesantire la tabella
            List<Movimento> movimenti = new MovimentoController().getUltimiMovimenti(200);
            if (movimenti != null && !movimenti.isEmpty()) {
                int contatore = 1;
                for (Movimento m : movimenti) {
                    // aggiunge + o - alla quantità per distinguere visivamente carico e scarico
                    String segno = "Carico".equals(m.getTipoMovimento()) ? "+" : "-";
                    tableModel.addRow(new Object[]{
                            String.format("%04d", contatore++),
                            m.getIdProdotto(),
                            m.getNomeProdotto(),
                            m.getTipoMovimento(),
                            segno + m.getQtaProdotto(),
                            m.getData().toString(),
                            m.getIdOperatore(),
                            ""
                    });
                }
            } else {
                tableModel.addRow(new Object[]{"-", "-", "Nessun movimento registrato", "-", "-", "-", "-", "-"});
            }
        } catch (Exception e) {
            tableModel.addRow(new Object[]{"-", "-", "Errore caricamento dati", "-", "-", "-", "-", "-"});
            System.out.println("Errore caricaDatiDalDB: " + e.getMessage());
        }
    }

    private void aggiornaCounter(JLabel lbl) {
        int vis = tabella.getRowCount();
        int tot = tableModel.getRowCount();
        lbl.setText("Visualizzati " + vis + " di " + tot + " movimenti");
    }

    private JPanel buildExportBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bar.setOpaque(false);

        // esporta i dati visibili nella tabella in un file Excel
        JButton btnExport = GestisciProdotti.buildPrimaryButton("⬇  Esporta Excel", StyleWMS.BLU_MEDIO);
        btnExport.addActionListener(e ->
                ExcelExporter.esporta(this, tableModel, "Storico Movimenti", "storico_movimenti"));
        bar.add(btnExport);
        return bar;
    }

    private void applicaFiltri() {
        String testo = campoRicerca.getText().trim();
        String tipo  = (String) filtroTipo.getSelectedItem();
        String dal   = campoDataDal.getText().trim();
        String al    = campoDataAl.getText().trim();

        List<RowFilter<DefaultTableModel, Object>> filtri = new ArrayList<>();

        // filtro testo libero: cerca su nome prodotto, ID, operatore e note
        if (!testo.isEmpty() && !testo.equals("Nome, ID, operatore…")) {
            filtri.add(RowFilter.regexFilter("(?i)" + testo, 1, 2, 6, 7));
        }
        // filtro tipo: mostra solo Carico o solo Scarico se selezionato
        if (tipo != null && !"Tutti".equals(tipo)) {
            filtri.add(RowFilter.regexFilter("(?i)^" + tipo + "$", 3));
        }
        // filtro data: confronto semplice su stringa (col 5)
        if (!dal.isEmpty() && !dal.equals("gg/mm/aaaa")) {
            filtri.add(RowFilter.regexFilter(dal, 5));
        }

        if (filtri.isEmpty()) {
            sorter.setRowFilter(null);
        } else if (filtri.size() == 1) {
            sorter.setRowFilter(filtri.get(0));
        } else {
            // combina tutti i filtri attivi con AND
            sorter.setRowFilter(RowFilter.andFilter(filtri));
        }
    }
}