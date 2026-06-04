package it.unina.magazzino.boundary;

import it.unina.magazzino.boundary.utils.StyleWMS;
import it.unina.magazzino.entity.Operatore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Schermata "Registra Scarico".
 *
 * Flusso:
 *   1. L'operatore visualizza il catalogo dei prodotti in magazzino.
 *   2. Seleziona il prodotto desiderato dalla lista.
 *   3. Inserisce la quantità da prelevare.
 *   4. Il sistema verifica che la quantità non ecceda la disponibilità.
 *      - Se eccede: l'operazione viene ANNULLATA e l'operatore ne è informato;
 *        il responsabile riceve una notifica in background.
 *      - Se la disponibilità residua scende sotto la soglia minima: il responsabile
 *        viene notificato in background, ma l'operatore NON vede alcun avviso
 *        (la gestione del riordino non è di sua competenza).
 */
public class RegistraScarico extends JFrame {

    private final Map<String, String[]> catalogo = new LinkedHashMap<>();

    private Operatore operatoreLoggato;

    // ── Componenti UI ─────────────────────────────────────────────
    private DefaultListModel<String> listModel;
    private JList<String>            productList;
    private JLabel                   lblProdottoScelto;
    private JLabel                   lblDisponibilitaCorrente;
    private JLabel                   lblSogliaMinima;
    private JTextField               txtQuantita;
    private JButton                  btnConferma;
    private JButton                  btnDashboard;

    /** SKU (codice) del prodotto attualmente selezionato, null se nessuno. */
    private String skuSelezionato = null;

    // ── Costruttore ───────────────────────────────────────────────
    public RegistraScarico(Operatore operatore) {
        this.operatoreLoggato = operatore;
        inizializzaCatalogo();

        setTitle("WMS PRO – Movimento di Scarico");
        setSize(520, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(StyleWMS.BIANCO);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildBody(),   BorderLayout.CENTER);
        root.add(buildFooterButtons(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ── Dati di esempio (da sostituire con chiamate al DAO) ───────
    private void inizializzaCatalogo() {
        // Formato: nome | quantità | sogliaMinima
        catalogo.put("001", new String[]{"Prodotto A",   "42", "10"});
        catalogo.put("002", new String[]{"Prodotto B",    "17",  "5"});
        catalogo.put("003", new String[]{"Prodotto C",   "88", "20"});
        catalogo.put("004", new String[]{"Prodotto D",    "5",  "8"});
        catalogo.put("005", new String[]{"Prodotto E", "31", "10"});
        catalogo.put("006", new String[]{"Prodotto F",    "60", "15"});
    }

    // ── Header blu ────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(StyleWMS.BLU_ACCIAIO);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(22, 0, 22, 0));

        JLabel titolo = new JLabel("WMS PRO", SwingConstants.CENTER);
        titolo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titolo.setForeground(StyleWMS.BIANCO);
        titolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Registra Operazione di Scarico", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(StyleWMS.AZZURRO_LIGHT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titolo);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        return header;
    }

    // ── Corpo principale ──────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setBackground(StyleWMS.BIANCO);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(20, 30, 10, 30));

        // 1. Catalogo prodotti
        body.add(sectionLabel("1. Seleziona prodotto"));
        body.add(Box.createVerticalStrut(8));
        body.add(buildProductList());
        body.add(Box.createVerticalStrut(12));

        // 2. Riepilogo prodotto selezionato
        body.add(buildProductSummaryPanel());
        body.add(Box.createVerticalStrut(16));

        // 3. Quantità
        body.add(sectionLabel("2. Quantità da prelevare"));
        body.add(Box.createVerticalStrut(6));
        txtQuantita = new JTextField();
        stilizza(txtQuantita);
        body.add(txtQuantita);
        body.add(Box.createVerticalStrut(20));

        // 4. Bottone conferma
        btnConferma = buildPrimaryButton("Conferma Scarico");
        body.add(btnConferma);

        btnConferma.addActionListener(e -> onConferma());

        return body;
    }

    // ── Lista prodotti con scroll ─────────────────────────────────
    private JScrollPane buildProductList() {
        listModel = new DefaultListModel<>();
        for (Map.Entry<String, String[]> entry : catalogo.entrySet()) {
            listModel.addElement(buildVoceList(entry.getKey(), entry.getValue()));
        }

        productList = new JList<>(listModel);
        productList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productList.setForeground(StyleWMS.ANTRACITE);
        productList.setBackground(new Color(0xF5, 0xF8, 0xFF));
        productList.setSelectionBackground(StyleWMS.BLU_ACCIAIO);
        productList.setSelectionForeground(StyleWMS.BIANCO);
        productList.setFixedCellHeight(32);
        productList.setBorder(new EmptyBorder(4, 8, 4, 8));
        productList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        productList.addListSelectionListener(this::onProductSelected);

        JScrollPane scroll = new JScrollPane(productList);
        scroll.setPreferredSize(new Dimension(Integer.MAX_VALUE, 160));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setBorder(BorderFactory.createLineBorder(StyleWMS.AZZURRO_LIGHT));
        return scroll;
    }

    /** Costruisce la stringa da visualizzare nella JList per un prodotto. */
    private String buildVoceList(String sku, String[] dati) {
        return sku + "  |  " + dati[0] + "  (disponibili: " + dati[1] + "  |  soglia: " + dati[2] + ")";
    }

    // ── Pannello riepilogo prodotto selezionato
    private JPanel buildProductSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 0, 2));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));

        lblProdottoScelto = new JLabel("Nessun prodotto selezionato");
        lblProdottoScelto.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblProdottoScelto.setForeground(StyleWMS.BLU_ACCIAIO);

        lblDisponibilitaCorrente = new JLabel("");
        lblDisponibilitaCorrente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDisponibilitaCorrente.setForeground(StyleWMS.GRIGIO_TESTO);

        lblSogliaMinima = new JLabel("");
        lblSogliaMinima.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSogliaMinima.setForeground(new Color(0xCC, 0x55, 0x00));

        panel.add(lblProdottoScelto);
        panel.add(lblDisponibilitaCorrente);
        panel.add(lblSogliaMinima);
        return panel;
    }

    // ── Bottoni in fondo ──────────────────────────────────────────
    private JPanel buildFooterButtons() {
        JPanel panel = new JPanel();
        panel.setBackground(StyleWMS.BIANCO);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(0, 30, 20, 30));

        btnDashboard = buildSecondaryButton("Torna alla Dashboard");
        panel.add(btnDashboard);

        btnDashboard.addActionListener(e -> {
            this.dispose();
            new DashboardOperatore(this.operatoreLoggato, "resources/assets/logoFinale.png").setVisible(true);
        });

        return panel;
    }

    // ── Logica di selezione prodotto
    private void onProductSelected(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int idx = productList.getSelectedIndex();
        if (idx < 0) {
            skuSelezionato = null;
            lblProdottoScelto.setText("Nessun prodotto selezionato");
            lblDisponibilitaCorrente.setText("");
            lblSogliaMinima.setText("");
            return;
        }
        skuSelezionato = (String) catalogo.keySet().toArray()[idx];
        String[] dati = catalogo.get(skuSelezionato);
        lblProdottoScelto.setText(dati[0] + "  [" + skuSelezionato + "]");
        lblDisponibilitaCorrente.setText("Disponibilità attuale: " + dati[1] + " unità");
        lblSogliaMinima.setText("Soglia minima: " + dati[2] + " unità");
    }

    // ── Logica conferma scarico
    private void onConferma() {
        // Validazione: prodotto selezionato
        if (skuSelezionato == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleziona un prodotto dalla lista prima di confermare.",
                    "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validazione: quantità numerica positiva
        String qtyStr = txtQuantita.getText().trim();
        int quantita;
        try {
            quantita = Integer.parseInt(qtyStr);
            if (quantita <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Inserisci una quantità numerica positiva.",
                    "Errore input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] dati          = catalogo.get(skuSelezionato);
        int disponibile        = Integer.parseInt(dati[1]);
        int sogliaMin          = Integer.parseInt(dati[2]);

        // ── MODIFICA 1 ────────────────────────────────────────────────────────────
        // Controllo: quantità richiesta > disponibile → blocca l'operazione.
        // L'operatore vede solo che l'operazione è annullata; la notifica al
        // responsabile avviene in background senza mostrare nulla all'operatore.
        // PRIMA: notificaResponsabile(...) mostrava un JOptionPane con parent=this
        //        (visibile all'operatore) e nel testo diceva "il responsabile è stato notificato".
        // DOPO:  JOptionPane pulito solo per l'operatore + chiamata silenziosa in background.
        if (quantita > disponibile) {
            JOptionPane.showMessageDialog(this,
                    "Lo scarico di " + quantita + " unità di \"" + dati[0] + "\" supera\n" +
                            "la disponibilità attuale (" + disponibile + " unità).\n\n" +
                            "Operazione ANNULLATA.",
                    "Scorte insufficienti", JOptionPane.ERROR_MESSAGE);
            notificaResponsabileInBackground(dati[0], disponibile, -1);
            return;
        }
        // ── FINE MODIFICA 1 ───────────────────────────────────────────────────────

        // Aggiorna disponibilità
        int nuovaQty = disponibile - quantita;
        dati[1] = String.valueOf(nuovaQty);

        // Aggiorna voce nella JList
        int idx = productList.getSelectedIndex();
        listModel.set(idx, buildVoceList(skuSelezionato, dati));
        lblDisponibilitaCorrente.setText("Disponibilità attuale: " + dati[1] + " unità");

        JOptionPane.showMessageDialog(this,
                "Scarico di " + quantita + " unità di \"" + dati[0] + "\" registrato.\n" +
                        "Nuova disponibilità: " + nuovaQty + " unità.",
                "Scarico completato", JOptionPane.INFORMATION_MESSAGE);

        // ── MODIFICA 2 ────────────────────────────────────────────────────────────
        // Controllo soglia minima: se la disponibilità residua scende sotto soglia,
        // il responsabile viene notificato in background.
        // L'operatore NON vede alcun dialogo: la gestione del riordino non gli compete.
        // PRIMA: notificaResponsabile(...) mostrava un JOptionPane all'operatore.
        // DOPO:  chiamata silenziosa a notificaResponsabileInBackground().
        if (nuovaQty < sogliaMin) {
            notificaResponsabileInBackground(dati[0], nuovaQty, sogliaMin);
        }
        // ── FINE MODIFICA 2 ───────────────────────────────────────────────────────

        txtQuantita.setText("");
    }

    // ── MODIFICA 3 ────────────────────────────────────────────────────────────
    /**
     * Invia la notifica al responsabile senza mostrare alcun dialogo all'operatore.
     * In produzione sostituire il log con una chiamata al servizio di notifica reale
     * (REST endpoint, email, push notification, ecc.).
     *
     * @param nomeProdotto  Nome del prodotto coinvolto
     * @param qtaAttuale    Quantità attuale residua in magazzino
     * @param sogliaMinima  Soglia minima configurata; -1 indica "scorte esaurite"
     */
    private void notificaResponsabileInBackground(String nomeProdotto, int qtaAttuale, int sogliaMinima) {
        String messaggio;
        if (sogliaMinima < 0) {
            messaggio = "[SCORTE INSUFFICIENTI] Tentativo di scarico su \"" + nomeProdotto
                    + "\" con disponibilità esaurita (" + qtaAttuale + " unità). Operazione bloccata.";
        } else {
            messaggio = "[SOGLIA MINIMA] \"" + nomeProdotto + "\" è scesa a " + qtaAttuale
                    + " unità (soglia: " + sogliaMinima + "). Valutare riordino.";
        }
        // TODO: sostituire con chiamata al servizio di notifica reale
        System.out.println("[NOTIFICA RESPONSABILE] " + messaggio);
    }
    // ── FINE MODIFICA 3

    // ── Builders bottoni
    private JButton buildPrimaryButton(String testo) {
        JButton btn = new JButton(testo) {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? StyleWMS.BLU_ACCIAIO : StyleWMS.BLU_MEDIO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(StyleWMS.BIANCO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton buildSecondaryButton(String testo) {
        JButton btn = new JButton(testo) {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? StyleWMS.GRIGIO_NEUTRO : StyleWMS.BIANCO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(StyleWMS.AZZURRO_LIGHT);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(StyleWMS.BLU_ACCIAIO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Helpers
    private JLabel sectionLabel(String testo) {
        JLabel l = new JLabel(testo);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(StyleWMS.ANTRACITE);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void stilizza(JTextField c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(StyleWMS.AZZURRO_LIGHT),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }
}