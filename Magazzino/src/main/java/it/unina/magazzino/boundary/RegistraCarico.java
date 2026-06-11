package it.unina.magazzino.boundary;

import it.unina.magazzino.boundary.utils.StyleWMS;
import it.unina.magazzino.control.MovimentoController;
import it.unina.magazzino.control.ProdottoController;
import it.unina.magazzino.database.MovimentoDAO;
import it.unina.magazzino.entity.Movimento;
import it.unina.magazzino.entity.Operatore;
import it.unina.magazzino.entity.Prodotto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RegistraCarico extends JFrame {

    // mappa SKU → {nome, quantitàCorrente, sogliaMinima} caricata dal DB
    private final Map<String, String[]> catalogo = new LinkedHashMap<>();

    private Operatore operatoreLogged;

    private DefaultListModel<String> listModel;
    private JList<String> productList;
    private JLabel lblProdottoScelto;
    private JLabel lblDisponibilitaCorrente;
    private JTextField txtQuantita;
    private JButton btnConferma;
    private JButton btnDashboard;

    // SKU (ID) del prodotto selezionato nella lista, null se nessuno è selezionato
    private String skuSelezionato = null;

    public RegistraCarico(Operatore operatore) {
        this.operatoreLogged = operatore;

        // carica i prodotti dal DB prima di costruire l'interfaccia
        inizializzaCatalogo();

        setTitle("WMS PRO – Movimento di Carico");
        setSize(520, 640);
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

    private void inizializzaCatalogo() {
        // formato: nome | quantità corrente | soglia minima
        catalogo.clear();
        try {
            ProdottoController controller = new ProdottoController();
            List<Prodotto> inventario = controller.getAllProdotti();

            if (inventario != null) {
                for (Prodotto p : inventario) {
                    catalogo.put(p.getID(), new String[]{
                            p.getNome(),
                            String.valueOf(p.getQtaDisponibile()),
                            String.valueOf(p.getSogliaMinima())
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Errore durante il caricamento del catalogo: " + e.getMessage());
        }
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(StyleWMS.BLU_ACCIAIO);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(22, 0, 22, 0));

        JLabel titolo = new JLabel("WMS PRO", SwingConstants.CENTER);
        titolo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titolo.setForeground(StyleWMS.BIANCO);
        titolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Registra Operazione di Carico", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(StyleWMS.AZZURRO_LIGHT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titolo);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        return header;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setBackground(StyleWMS.BIANCO);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(20, 30, 10, 30));

        body.add(sectionLabel("1. Seleziona prodotto"));
        body.add(Box.createVerticalStrut(8));
        body.add(buildProductList());
        body.add(Box.createVerticalStrut(12));

        // riepilogo del prodotto selezionato: nome e disponibilità attuale
        body.add(buildProductSummaryPanel());
        body.add(Box.createVerticalStrut(16));

        body.add(sectionLabel("2. Quantità da caricare"));
        body.add(Box.createVerticalStrut(6));
        txtQuantita = new JTextField();
        stilizza(txtQuantita);
        body.add(txtQuantita);
        body.add(Box.createVerticalStrut(20));

        btnConferma = buildPrimaryButton("Conferma Carico");
        body.add(btnConferma);

        // al click su conferma esegue la validazione e il salvataggio del movimento
        btnConferma.addActionListener(e -> onConferma());

        return body;
    }

    private JScrollPane buildProductList() {
        listModel = new DefaultListModel<>();
        for (Map.Entry<String, String[]> entry : catalogo.entrySet()) {
            String sku  = entry.getKey();
            String nome = entry.getValue()[0];
            String qty  = entry.getValue()[1];
            listModel.addElement(sku + "  |  " + nome + "  (disponibili: " + qty + ")");
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

        // ogni volta che l'utente seleziona un prodotto aggiorna il riepilogo sotto
        productList.addListSelectionListener(this::onProductSelected);

        JScrollPane scroll = new JScrollPane(productList);
        scroll.setPreferredSize(new Dimension(Integer.MAX_VALUE, 160));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setBorder(BorderFactory.createLineBorder(StyleWMS.AZZURRO_LIGHT));
        return scroll;
    }

    private JPanel buildProductSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 2));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        lblProdottoScelto = new JLabel("Nessun prodotto selezionato");
        lblProdottoScelto.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblProdottoScelto.setForeground(StyleWMS.BLU_ACCIAIO);

        lblDisponibilitaCorrente = new JLabel("");
        lblDisponibilitaCorrente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDisponibilitaCorrente.setForeground(StyleWMS.GRIGIO_TESTO);

        panel.add(lblProdottoScelto);
        panel.add(lblDisponibilitaCorrente);
        return panel;
    }

    private JPanel buildFooterButtons() {
        JPanel panel = new JPanel();
        panel.setBackground(StyleWMS.BIANCO);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(0, 30, 20, 30));

        btnDashboard = buildSecondaryButton("Torna alla Dashboard");
        panel.add(btnDashboard);

        // torna alla dashboard senza salvare nulla
        btnDashboard.addActionListener(e -> {
            this.dispose();
            new DashboardOperatore(this.operatoreLogged, "resources/assets/logoFinale.png").setVisible(true);
        });

        return panel;
    }

    private void onProductSelected(ListSelectionEvent e) {
        // ignora gli eventi intermedi durante il trascinamento della selezione
        if (e.getValueIsAdjusting()) return;
        int idx = productList.getSelectedIndex();
        if (idx < 0) {
            skuSelezionato = null;
            lblProdottoScelto.setText("Nessun prodotto selezionato");
            lblDisponibilitaCorrente.setText("");
            return;
        }
        // ricava lo SKU dall'indice: l'ordine della mappa coincide con quello della lista
        skuSelezionato = (String) catalogo.keySet().toArray()[idx];
        String[] dati = catalogo.get(skuSelezionato);
        lblProdottoScelto.setText(dati[0] + "  [" + skuSelezionato + "]");
        lblDisponibilitaCorrente.setText("Disponibilità attuale: " + dati[1] + " unità");
    }

    private void onConferma() {
        // verifica che l'utente abbia selezionato un prodotto
        if (skuSelezionato == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleziona un prodotto dalla lista prima di confermare.",
                    "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // verifica che la quantità sia un intero positivo
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

        String[] dati = catalogo.get(skuSelezionato);
        int nuovaQty  = Integer.parseInt(dati[1]) + quantita;

        try {
            // aggiorna la quantità nel DB tramite il controller
            ProdottoController controller = new ProdottoController();
            boolean savingOk = controller.aggiornaQuantitaProdotto(skuSelezionato, nuovaQty);

            if (!savingOk) {
                JOptionPane.showMessageDialog(this, "Errore di caricamento");
                return;
            }

            // registra il movimento di carico nel DB
            Movimento movimento = new Movimento(
                    quantita, new Date(System.currentTimeMillis()),
                    "Carico", skuSelezionato, operatoreLogged.getID_Utenete()
            );
            new MovimentoDAO().inserisciMovimento(movimento);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Errore DB: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // aggiorna il catalogo in memoria e la voce corrispondente nella JList
        dati[1] = String.valueOf(nuovaQty);
        int idx = productList.getSelectedIndex();
        listModel.set(idx, skuSelezionato + "  |  " + dati[0] + "  (disponibili: " + dati[1] + ")");
        lblDisponibilitaCorrente.setText("Disponibilità attuale: " + dati[1] + " unità");

        JOptionPane.showMessageDialog(this,
                "Carico di " + quantita + " unità di \"" + dati[0] + "\" registrato con successo.\n" +
                        "Nuova disponibilità: " + nuovaQty + " unità.",
                "Carico completato", JOptionPane.INFORMATION_MESSAGE);

        txtQuantita.setText("");
    }

    // bottone pieno blu: usato per l'azione principale
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

    // bottone solo bordo: usato per le azioni secondarie come "torna indietro"
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

    private JLabel sectionLabel(String testo) {
        JLabel l = new JLabel(testo);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(StyleWMS.ANTRACITE);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // applica lo stile uniforme ai campi di testo
    private void stilizza(JTextField c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(StyleWMS.AZZURRO_LIGHT),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }
}