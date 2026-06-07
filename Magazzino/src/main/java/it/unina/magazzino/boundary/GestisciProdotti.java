package it.unina.magazzino.boundary;

import it.unina.magazzino.boundary.utils.StyleWMS;
import it.unina.magazzino.boundary.utils.ExcelExporter;
import it.unina.magazzino.control.ProdottoController;

import it.unina.magazzino.entity.Prodotto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

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

    private final String idResponsabile;

    // Indice colonna soglia minima (0-based)
    private static final int COL_QTY    = 3;
    private static final int COL_SOGLIA = 4;

    private static final String SOGLIA_ASSENTE = "—";

    private static final String[] COLONNE = {
            "ID", "Nome Prodotto", "Categoria", "Quantità", "Posizione", "Soglia Min."
    };

    public void caricaDatiDalDatabase(){
        tableModel.setRowCount(0);

        ProdottoController controller = new ProdottoController();
        List<Prodotto> inventario = controller.getAllProdotti();

        if(inventario != null){
            for(Prodotto p : inventario){

                String sogliaStr = (p.getSogliaMinima() > 0) ? String.valueOf(p.getSogliaMinima()) : SOGLIA_ASSENTE;

                Object[] riga = {
                        p.getID(),
                        p.getNome(),
                        p.getCategoria(),
                        p.getQtaDisponibile(),
                        p.getIdPos(),
                        sogliaStr,
                        p.getIdUtenteResponsabile()
                };

                tableModel.addRow(riga);

            }
        }
    }



    public GestisciProdotti(String idResponsabile) {
        this.idResponsabile = idResponsabile;
        setOpaque(false);
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        caricaDatiDalDatabase();
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

        tableModel = new DefaultTableModel(new Object[][]{}, COLONNE) {
            @Override
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
        dialog.setSize(440, 480);
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

        // Rimosso l'ID, i campi sono 6
        String[] labels = {"Nome", "Categoria", "Descrizione", "Quantità", "Soglia Min.", "Posizione"};
        JTextField[] fields = new JTextField[labels.length];

        // INDICE CORRETTO: La soglia ora si trova all'indice 4!
        final int DIALOG_INDEX_SOGLIA = 4;

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
                // Riassegniamo i valori considerando che la tabella visiva ha ancora l'ID alla colonna 0
                String val = "";
                if(i == 0) val = tableModel.getValueAt(modelRow, 1).toString(); // Nome
                if(i == 1) val = tableModel.getValueAt(modelRow, 2).toString(); // Categoria
                if(i == 2) val = "";                                            // Descrizione (Non presente in tabella)
                if(i == 3) val = tableModel.getValueAt(modelRow, 3).toString(); // Qty
                if(i == 4) val = tableModel.getValueAt(modelRow, 4).toString(); // Soglia
                if(i == 5) val = tableModel.getValueAt(modelRow, 5).toString(); // Posizione

                fields[i].setText(val);

                if (i == DIALOG_INDEX_SOGLIA && SOGLIA_ASSENTE.equals(val)) {
                    chkNessunaSoglia.setSelected(true);
                    fields[i].setEnabled(false);
                    fields[i].setText("");
                }
            }

            riga.add(lbl, BorderLayout.WEST);
            riga.add(fields[i], BorderLayout.CENTER);
            root.add(riga);

            // Ora la checkbox si legherà al campo giusto
            if (i == DIALOG_INDEX_SOGLIA) {
                root.add(Box.createVerticalStrut(2));
                JPanel chkWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
                chkWrap.setOpaque(false);
                chkWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
                chkWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
                chkNessunaSoglia.setAlignmentX(Component.LEFT_ALIGNMENT);
                chkWrap.add(chkNessunaSoglia);
                root.add(chkWrap);

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

            for (int i = 0; i < fields.length; i++) {
                boolean obbligatorio = fields[i].isEnabled() && i != DIALOG_INDEX_SOGLIA;
                if (obbligatorio && fields[i].getText().trim().isEmpty()) {
                    mostraAvviso("Il campo «" + labels[i] + "» è obbligatorio.");
                    fields[i].requestFocus();
                    return;
                }
            }

            if (fields[DIALOG_INDEX_SOGLIA].isEnabled()) {
                String s = fields[DIALOG_INDEX_SOGLIA].getText().trim();
                if (!s.isEmpty()) {
                    try { if (Integer.parseInt(s) < 0) throw new NumberFormatException(); }
                    catch (NumberFormatException ex) {
                        mostraAvviso("La soglia minima deve essere un numero intero ≥ 0.");
                        fields[DIALOG_INDEX_SOGLIA].requestFocus();
                        return;
                    }
                }
            }

            String sogliaFinal;
            if (!fields[DIALOG_INDEX_SOGLIA].isEnabled() || fields[DIALOG_INDEX_SOGLIA].getText().trim().isEmpty()) {
                sogliaFinal = SOGLIA_ASSENTE;
            } else {
                sogliaFinal = fields[DIALOG_INDEX_SOGLIA].getText().trim();
            }

            try {
                // Nuovi indici spostati
                String nome = fields[0].getText().trim();
                String categoria = fields[1].getText().trim();
                String descrizione = fields[2].getText().trim();
                int qtDisp = Integer.parseInt(fields[3].getText().trim());
                int sogliaMinima = sogliaFinal.equals(SOGLIA_ASSENTE) ? 0 : Integer.parseInt(sogliaFinal);
                String posizione = fields[5].getText().trim();

                ProdottoController pController = new ProdottoController();

                if (isModifica) {

                    String idProd = tableModel.getValueAt(modelRow, 0).toString();
                    boolean successo = pController.modificaProdotto(idProd, nome, categoria, descrizione, qtDisp, sogliaMinima, posizione, idResponsabile);

                    if(successo){
                        caricaDatiDalDatabase();
                        JOptionPane.showMessageDialog(dialog, "Prodotto modificato con successo", "Successo", JOptionPane.INFORMATION_MESSAGE);
                    }

                } else {
                    boolean successo = pController.registraNuovoProdotto(nome, categoria, descrizione, qtDisp, sogliaMinima, posizione, idResponsabile);

                    if(successo){
                        caricaDatiDalDatabase(); // Questo mostrerà l'ID autogenerato!
                        JOptionPane.showMessageDialog(dialog, "Prodotto salvato con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                    }
                }

                dialog.dispose();

            } catch (Exception ex){
                mostraAvviso(ex.getMessage());
            }
        });

        btnPanel.add(btnAnnulla);
        btnPanel.add(btnSalva);
        root.add(btnPanel);

        dialog.setContentPane(root);
        dialog.getRootPane().setDefaultButton(btnSalva);
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