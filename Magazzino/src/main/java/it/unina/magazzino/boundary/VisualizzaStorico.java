package it.unina.magazzino.boundary;

import it.unina.magazzino.boundary.utils.StyleWMS;
import it.unina.magazzino.entity.Operatore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VisualizzaStorico extends JFrame {

    private JTable tableMovimenti;
    private JButton btnIndietro;

    private Operatore operatoreLoggato;

    public VisualizzaStorico(Operatore operatore) {
        this.operatoreLoggato = operatore;
        // Configurazioni Finestra coerenti con il resto del sistema
        setTitle("WMS PRO – Storico Personale Movimenti");
        setSize(520, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Pannello Radice
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(StyleWMS.BIANCO);

        /* ── Header Blu (Stile WMS PRO) ── */
        JPanel header = new JPanel();
        header.setBackground(StyleWMS.BLU_ACCIAIO);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(25, 0, 25, 0));

        JLabel titolo = new JLabel("WMS PRO", SwingConstants.CENTER);
        titolo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titolo.setForeground(StyleWMS.BIANCO);
        titolo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Storico Personale Movimenti", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(StyleWMS.AZZURRO_LIGHT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titolo);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);

        /* ── Contenitore Centrale (Form/Tabella) ── */
        JPanel body = new JPanel();
        body.setBackground(StyleWMS.BIANCO);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(20, 35, 20, 35));

        // Intestazione Sezione Interna
        JLabel lblSezione = new JLabel("Le tue operazioni registrate:");
        lblSezione.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSezione.setForeground(StyleWMS.ANTRACITE);
        lblSezione.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(lblSezione);
        body.add(Box.createVerticalStrut(15));

        /* ── Creazione e Stilizzazione Tabella (Dati mockati di esempio) ── */
        String[] colonne = {"Prodotto", "Tipo", "Quantità", "Data"};
        Object[][] datiEsempio = {
                {"Dentifricio (123456)", "Carico", "50", "01/01/2026"},
                {"Spazzolino (654321)", "Scarico", "12", "15/02/2026"},
                {"Sapone Mani (789101)", "Carico", "100", "18/03/2026"},
                {"Bagnoschiuma (112131)", "Scarico", "5", "10/04/2026"}
        };

        DefaultTableModel tableModel = new DefaultTableModel(datiEsempio, colonne) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabella in sola lettura
            }
        };

        tableMovimenti = new JTable(tableModel);
        tableMovimenti.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableMovimenti.setRowHeight(32);
        tableMovimenti.setGridColor(StyleWMS.AZZURRO_LIGHT);
        tableMovimenti.setSelectionBackground(StyleWMS.AZZURRO_LIGHT);
        tableMovimenti.setSelectionForeground(StyleWMS.ANTRACITE);
        tableMovimenti.setShowVerticalLines(false); // Design flat moderno senza linee verticali

        // Stile Intestazione della Tabella
        JTableHeader tableHeader = tableMovimenti.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setBackground(StyleWMS.GRIGIO_NEUTRO);
        tableHeader.setForeground(StyleWMS.ANTRACITE);
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, StyleWMS.AZZURRO_LIGHT));
        tableHeader.setReorderingAllowed(false);

        // ScrollPane per contenere la tabella
        JScrollPane scrollPane = new JScrollPane(tableMovimenti);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setBorder(BorderFactory.createLineBorder(StyleWMS.AZZURRO_LIGHT));
        scrollPane.getViewport().setBackground(StyleWMS.BIANCO);
        body.add(scrollPane);

        body.add(Box.createVerticalStrut(25));

        /* ── Pulsante "Torna alla Home" con Effetto Hover Coordinato ── */
        btnIndietro = new JButton("Torna alla Home") {
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

                // Animazione di stato: Scambia tra BLU_MEDIO e BLU_ACCIAIO
                g2.setColor(hovered ? StyleWMS.BLU_ACCIAIO : StyleWMS.BLU_MEDIO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnIndietro.setContentAreaFilled(false);
        btnIndietro.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnIndietro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnIndietro.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIndietro.setBackground(StyleWMS.BLU_MEDIO);
        btnIndietro.setForeground(StyleWMS.BIANCO);
        btnIndietro.setFocusPainted(false);
        btnIndietro.setBorderPainted(false);
        btnIndietro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Listener per la navigazione di ritorno alla HomePage
        btnIndietro.addActionListener(e -> {
            this.dispose();
            DashboardOperatore dashboardOperatore = new DashboardOperatore(this.operatoreLoggato, "resources/assets/logoFinale.png");
            dashboardOperatore.setVisible(true);
        });

        body.add(btnIndietro);

        // Assemblaggio finale dei componenti
        root.add(header, BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);
        setContentPane(root);
    }

    public static void main(String[] args) {
        // Mock Look and Feel di sistema per coerenza
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {

            Operatore operatoreTest = new Operatore(
                    "Francesco",
                    "Capasso",
                    "cap@mail.it",
                    "pswtest",
                    "TEST-003"
            );

            new VisualizzaStorico(operatoreTest).setVisible(true);


        });
    }
}