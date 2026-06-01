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

/**
 * Pannello "Gestisci Prodotti" — RF04 / RF05 / RF06 / RF07
 *
 * Funzionalità:
 *  - Visualizzazione lista prodotti con ricerca/filtro (RF04)
 *  - Aggiunta nuovo prodotto tramite dialog (RF05) — soglia min. opzionale
 *  - Modifica prodotto selezionato (RF06)
 *  - Eliminazione prodotto con conferma (RF07)
 *  - Esportazione tabella in Excel .xlsx tramite Apache POI
 */
public class GestisciProdotti extends JPanel {

    private DefaultTableModel tableModel;
    private JTable tabella;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField campoCerca;

    // Indice colonna soglia minima (0-based)
    private static final int COL_QTY    = 3;
    private static final int COL_SOGLIA = 4;

    private static final String SOGLIA_ASSENTE = "—";

    private static final String[] COLONNE = {
            "ID", "Nome Prodotto", "Categoria", "Quantità", "Soglia Min.", "Posizione"
    };

    // Dati demo — soglia "—" = non impostata
    private Object[][] datiDemo = {
            {"000011", "Guanti latex",       "Sicurezza",   "3",   "10",          "A-01-S01"},
            {"000045", "Nastro da imballo",  "Imballaggio", "1",   "5",           "B-03-S02"},
            {"000078", "Scatole S",          "Imballaggio", "4",   "20",          "B-03-S03"},
            {"000102", "Etichette adesive",  "Cancelleria", "8",   "15",          "C-07-S01"},
            {"000134", "Pallets 80x120",     "Logistica",   "2",   "6",           "D-01-S00"},
            {"000156", "Bolla cartone",      "Imballaggio", "55",  SOGLIA_ASSENTE,"B-04-S01"},
            {"000178", "Elmetti gialli",     "Sicurezza",   "20",  "5",           "A-02-S01"},
            {"000201", "Guanti nitrile",     "Sicurezza",   "34",  SOGLIA_ASSENTE,"A-01-S02"},
            {"000222", "Cutter 18mm",        "Utensili",    "9",   "4",           "E-01-S01"},
            {"000244", "Fascette cavi",      "Elettrico",   "120", "30",          "F-02-S01"},
    };

    public GestisciProdotti() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);
    }

    // ── Toolbar ───────────────────────────────────────────────────
    private JPanel buildToolbar() {
        JLabel titolo = new JLabel("GESTIONE PRODOTTI  ·  RF04–RF07");
        titolo.setFont(new Font("SansSerif", Font.BOLD, 10));
        titolo.setForeground(new Color(0x88, 0x88, 0x88));

        campoCerca = new JTextField(18);
        campoCerca.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campoCerca.setForeground(StyleWMS.GRIGIO_TESTO);
        campoCerca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(StyleWMS.AZZURRO_LIGHT, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        campoCerca.setToolTipText("Cerca per nome, ID o categoria…");
        campoCerca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtra(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtra(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtra(); }
        });

        JButton btnAggiungi = buildPrimaryButton("＋  Aggiungi", StyleWMS.BLU_ACCIAIO);
        btnAggiungi.addActionListener(e -> apriDialogProdotto(null));

        JButton btnExport = buildPrimaryButton("⬇  Excel", new Color(46, 125, 50));
        btnExport.addActionListener(e ->
                ExcelExporter.esporta(this, tableModel, "Prodotti", "prodotti_magazzino"));

        JPanel destra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        destra.setOpaque(false);
        JLabel iconaCerca = new JLabel("🔍");
        iconaCerca.setFont(new Font("SansSerif", Font.PLAIN, 13));
        destra.add(iconaCerca);
        destra.add(campoCerca);
        destra.add(btnAggiungi);
        destra.add(btnExport);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(titolo, BorderLayout.WEST);
        top.add(destra, BorderLayout.EAST);
        return top;
    }

    // ── Tabella prodotti ─────────────────────────────────────────
    private JPanel buildTablePanel() {
        DashboardOperatore.RoundPanel panel =
                new DashboardOperatore.RoundPanel(StyleWMS.BIANCO, StyleWMS.AZZURRO_LIGHT, 10);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        tableModel = new DefaultTableModel(datiDemo, COLONNE) {
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

        tabella.getColumnModel().getColumn(0).setPreferredWidth(70);
        tabella.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabella.getColumnModel().getColumn(2).setPreferredWidth(110);
        tabella.getColumnModel().getColumn(3).setPreferredWidth(75);
        tabella.getColumnModel().getColumn(4).setPreferredWidth(90);
        tabella.getColumnModel().getColumn(5).setPreferredWidth(100);

        // Renderer: rosso solo se soglia valorizzata E qty < soglia
        tabella.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    int mr = t.convertRowIndexToModel(row);
                    String sogliaStr = tableModel.getValueAt(mr, COL_SOGLIA).toString().trim();
                    boolean sottoScorta = false;
                    if (!sogliaStr.isEmpty() && !SOGLIA_ASSENTE.equals(sogliaStr)) {
                        try {
                            int qty    = Integer.parseInt(tableModel.getValueAt(mr, COL_QTY).toString().trim());
                            int soglia = Integer.parseInt(sogliaStr);
                            sottoScorta = qty < soglia;
                        } catch (NumberFormatException ignored) {}
                    }
                    c.setBackground(sottoScorta ? new Color(255, 235, 235) : StyleWMS.BIANCO);
                    c.setForeground(sottoScorta ? new Color(180, 20, 20)   : StyleWMS.GRIGIO_TESTO);
                }
                // Colonna soglia: mostra "—" in grigio chiaro
                if (col == COL_SOGLIA && SOGLIA_ASSENTE.equals(v)) {
                    ((JLabel) c).setForeground(new Color(200, 200, 200));
                }
                return c;
            }
        });

        sorter = new TableRowSorter<>(tableModel);
        tabella.setRowSorter(sorter);

        JScrollPane sp = new JScrollPane(tabella,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        panel.add(sp, BorderLayout.CENTER);

        // Legenda
        JPanel legenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        legenda.setOpaque(false);
        JPanel pallino = new JPanel();
        pallino.setPreferredSize(new Dimension(12, 12));
        pallino.setBackground(new Color(255, 200, 200));
        JLabel lblLeg = new JLabel("Sotto soglia minima  ·  \"—\" = soglia non impostata");
        lblLeg.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblLeg.setForeground(new Color(150, 150, 150));
        legenda.add(pallino);
        legenda.add(lblLeg);
        panel.add(legenda, BorderLayout.SOUTH);
        return panel;
    }

    // ── Barra inferiore ──────────────────────────────────────────
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bar.setOpaque(false);

        JButton btnModifica = buildPrimaryButton("✏  Modifica", StyleWMS.BLU_MEDIO);
        btnModifica.addActionListener(e -> {
            int row = tabella.getSelectedRow();
            if (row < 0) { mostraAvviso("Seleziona prima un prodotto."); return; }
            apriDialogProdotto(tabella.convertRowIndexToModel(row));
        });

        JButton btnElimina = buildPrimaryButton("🗑  Elimina", new Color(198, 40, 40));
        btnElimina.addActionListener(e -> {
            int row = tabella.getSelectedRow();
            if (row < 0) { mostraAvviso("Seleziona prima un prodotto."); return; }
            int mr = tabella.convertRowIndexToModel(row);
            String nome = tableModel.getValueAt(mr, 1).toString();
            int ok = JOptionPane.showConfirmDialog(this,
                    "Confermi l'eliminazione di «" + nome + "»?",
                    "Elimina prodotto", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok == JOptionPane.YES_OPTION) tableModel.removeRow(mr);
        });

        bar.add(btnModifica);
        bar.add(btnElimina);
        return bar;
    }

    // ── Dialog aggiungi / modifica ───────────────────────────────
    private void apriDialogProdotto(Integer modelRow) {
        boolean isModifica = modelRow != null;
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                isModifica ? "Modifica Prodotto" : "Aggiungi Prodotto",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(440, 430);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel root = new JPanel();
        root.setBackground(StyleWMS.BIANCO);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel titoloLbl = new JLabel(isModifica ? "Modifica dati prodotto" : "Nuovo prodotto");
        titoloLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        titoloLbl.setForeground(StyleWMS.BLU_ACCIAIO);
        titoloLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(titoloLbl);
        root.add(Box.createVerticalStrut(20));

        // Campi — indice 4 è la soglia, trattata separatamente
        String[] labels = {"ID Prodotto", "Nome", "Categoria", "Quantità", "Soglia Min.", "Posizione"};
        JTextField[] fields = new JTextField[labels.length];

        // Checkbox "soglia non impostata" — visibile solo per il campo soglia
        JCheckBox chkNessunaSoglia = new JCheckBox("Non impostare soglia minima");
        chkNessunaSoglia.setFont(new Font("SansSerif", Font.PLAIN, 11));
        chkNessunaSoglia.setForeground(new Color(120, 120, 120));
        chkNessunaSoglia.setOpaque(false);

        for (int i = 0; i < labels.length; i++) {
            JPanel riga = new JPanel(new BorderLayout(8, 0));
            riga.setOpaque(false);
            riga.setAlignmentX(Component.LEFT_ALIGNMENT);
            riga.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lbl.setForeground(StyleWMS.GRIGIO_TESTO);
            lbl.setPreferredSize(new Dimension(110, 20));

            fields[i] = new JTextField();
            fields[i].setFont(new Font("SansSerif", Font.PLAIN, 13));
            fields[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(StyleWMS.AZZURRO_LIGHT, 1, true),
                    new EmptyBorder(6, 10, 6, 10)));

            if (isModifica) {
                String val = tableModel.getValueAt(modelRow, i).toString();
                fields[i].setText(val);
                if (i == COL_SOGLIA && SOGLIA_ASSENTE.equals(val)) {
                    chkNessunaSoglia.setSelected(true);
                    fields[i].setEnabled(false);
                    fields[i].setText("");
                }
            }
            if (i == 0 && isModifica) fields[i].setEnabled(false);

            riga.add(lbl, BorderLayout.WEST);
            riga.add(fields[i], BorderLayout.CENTER);
            root.add(riga);

            // Subito dopo il campo soglia: checkbox opzionale
            if (i == COL_SOGLIA) {
                root.add(Box.createVerticalStrut(2));
                JPanel chkWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
                chkWrap.setOpaque(false);
                chkWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
                chkWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
                chkNessunaSoglia.setAlignmentX(Component.LEFT_ALIGNMENT);
                chkWrap.add(chkNessunaSoglia);
                root.add(chkWrap);

                // Listener: abilita/disabilita il campo soglia
                final JTextField fieldSoglia = fields[i];
                chkNessunaSoglia.addActionListener(ev -> {
                    boolean noSoglia = chkNessunaSoglia.isSelected();
                    fieldSoglia.setEnabled(!noSoglia);
                    if (noSoglia) fieldSoglia.setText("");
                    else          fieldSoglia.requestFocus();
                });
            }
            root.add(Box.createVerticalStrut(6));
        }

        root.add(Box.createVerticalStrut(10));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnAnnulla = buildPrimaryButton("Annulla", new Color(160, 160, 160));
        btnAnnulla.addActionListener(e -> dialog.dispose());

        JButton btnSalva = buildPrimaryButton(isModifica ? "Salva" : "Aggiungi", StyleWMS.BLU_ACCIAIO);
        btnSalva.addActionListener(e -> {
            // Validazione: tutti i campi obbligatori tranne soglia (se disabilitata)
            for (int i = 0; i < fields.length; i++) {
                boolean obbligatorio = fields[i].isEnabled() && i != COL_SOGLIA;
                if (obbligatorio && fields[i].getText().trim().isEmpty()) {
                    mostraAvviso("Il campo «" + labels[i] + "» è obbligatorio.");
                    fields[i].requestFocus();
                    return;
                }
            }
            // Soglia: se abilitata deve essere un intero ≥ 0
            if (fields[COL_SOGLIA].isEnabled()) {
                String s = fields[COL_SOGLIA].getText().trim();
                if (!s.isEmpty()) {
                    try { if (Integer.parseInt(s) < 0) throw new NumberFormatException(); }
                    catch (NumberFormatException ex) {
                        mostraAvviso("La soglia minima deve essere un numero intero ≥ 0.");
                        fields[COL_SOGLIA].requestFocus();
                        return;
                    }
                }
            }

            // Determina valore soglia finale
            String sogliaFinal;
            if (!fields[COL_SOGLIA].isEnabled() || fields[COL_SOGLIA].getText().trim().isEmpty()) {
                sogliaFinal = SOGLIA_ASSENTE;
            } else {
                sogliaFinal = fields[COL_SOGLIA].getText().trim();
            }

            if (isModifica) {
                for (int i = 0; i < fields.length; i++) {
                    if (i == COL_SOGLIA)
                        tableModel.setValueAt(sogliaFinal, modelRow, i);
                    else
                        tableModel.setValueAt(fields[i].getText().trim(), modelRow, i);
                }
            } else {
                Object[] nuovaRiga = new Object[fields.length];
                for (int i = 0; i < fields.length; i++)
                    nuovaRiga[i] = (i == COL_SOGLIA) ? sogliaFinal : fields[i].getText().trim();
                tableModel.addRow(nuovaRiga);
            }
            dialog.dispose();
        });

        btnPanel.add(btnAnnulla);
        btnPanel.add(btnSalva);
        root.add(btnPanel);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    // ── Helpers ──────────────────────────────────────────────────
    private void filtra() {
        String t = campoCerca.getText().trim();
        sorter.setRowFilter(t.isEmpty() ? null : RowFilter.regexFilter("(?i)" + t));
    }

    private void mostraAvviso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Attenzione", JOptionPane.WARNING_MESSAGE);
    }

    /** Bottone arrotondato con hover — accessibile agli altri pannelli */
    static JButton buildPrimaryButton(String testo, Color colore) {
        JButton btn = new JButton(testo) {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                });
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? colore.darker() : colore);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(StyleWMS.BIANCO);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }
}