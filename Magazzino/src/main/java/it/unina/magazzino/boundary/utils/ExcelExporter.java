package it.unina.magazzino.boundary.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExcelExporter {

    // Colori aziendali WMS in formato POI (RRGGBB senza #)
    private static final String BLU_ACCIAIO_HEX = "1565C0"; // StyleWMS.BLU_ACCIAIO
    private static final String BLU_MEDIO_HEX   = "1E88E5";
    private static final String AZZURRO_HEX      = "BBDEFB";
    private static final String BIANCO_HEX       = "FFFFFF";
    private static final String GRIGIO_HEADER    = "F5F7FA";
    private static final String ROSSO_ALERT      = "FFEBEB";
    private static final String ROSSO_TESTO      = "B71C1C";

    /**
     * Apre un JFileChooser, poi esporta la tabella in Excel.
     *
     * @param parent        componente Swing padre (per posizionare il dialog)
     * @param model         modello della tabella da esportare
     * @param sheetName     nome del foglio Excel (es. "Prodotti")
     * @param defaultName   nome file suggerito (senza estensione)
     */
    public static void esporta(Component parent,
                               DefaultTableModel model,
                               String sheetName,
                               String defaultName) {

        // ── Scelta del percorso di salvataggio ───────────────────
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salva come Excel");
        chooser.setSelectedFile(new File(
                defaultName + "_" +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) +
                        ".xlsx"));
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "File Excel (*.xlsx)", "xlsx"));

        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".xlsx"))
            file = new File(file.getAbsolutePath() + ".xlsx");

        // ── Creazione workbook ───────────────────────────────────
        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            XSSFSheet sheet = wb.createSheet(sheetName);

            // ── Stili ────────────────────────────────────────────
            CellStyle stileIntestazione = creaStileIntestazione(wb);
            CellStyle stileNormale      = creaStileNormale(wb);
            CellStyle stileAlternato    = creaStileAlternato(wb);
            CellStyle stileAlert        = creaStileAlert(wb);
            CellStyle stileNumerico     = creaStileNumerico(wb);

            // ── Riga intestazione ────────────────────────────────
            Row rigaIntestazione = sheet.createRow(0);
            rigaIntestazione.setHeight((short) 550); // ~28px

            for (int c = 0; c < model.getColumnCount(); c++) {
                Cell cell = rigaIntestazione.createCell(c);
                cell.setCellValue(model.getColumnName(c));
                cell.setCellStyle(stileIntestazione);
            }

            // ── Righe dati ───────────────────────────────────────
            for (int r = 0; r < model.getRowCount(); r++) {
                Row row = sheet.createRow(r + 1);
                row.setHeight((short) 450);

                // Rileva se la riga è "sotto scorta" (col 3 qty < col 4 soglia)
                // Applicabile solo quando ci sono almeno 5 colonne con quella semantica
                boolean isAlert = false;
                if (model.getColumnCount() >= 5) {
                    Object colSoglia = model.getValueAt(r, 4);
                    Object colQty    = model.getValueAt(r, 3);
                    if (colSoglia != null && colQty != null) {
                        String sogliaStr = colSoglia.toString().trim();
                        String qtyStr    = colQty.toString().trim();
                        if (!sogliaStr.isEmpty()) { // soglia valorizzata
                            try {
                                isAlert = Integer.parseInt(qtyStr) < Integer.parseInt(sogliaStr);
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }

                for (int c = 0; c < model.getColumnCount(); c++) {
                    Cell cell = row.createCell(c);
                    Object val = model.getValueAt(r, c);
                    String testo = (val == null) ? "" : val.toString();

                    // Prova a scrivere come numero se possibile
                    String stripped = testo.replace("+", "").replace("€", "").replace(".", "")
                            .replace(",", ".").trim();
                    try {
                        double d = Double.parseDouble(stripped);
                        cell.setCellValue(d);
                        CellStyle ns = wb.createCellStyle();
                        ns.cloneStyleFrom(isAlert ? stileAlert : (r % 2 == 0 ? stileNormale : stileAlternato));
                        // Formato numerico intero o decimale
                        DataFormat fmt = wb.createDataFormat();
                        ns.setDataFormat(stripped.contains(".") ? fmt.getFormat("0.00") : fmt.getFormat("0"));
                        cell.setCellStyle(ns);
                    } catch (NumberFormatException e) {
                        cell.setCellValue(testo);
                        cell.setCellStyle(isAlert ? stileAlert :
                                (r % 2 == 0 ? stileNormale : stileAlternato));
                    }
                }
            }

            // ── Autofit colonne ──────────────────────────────────
            for (int c = 0; c < model.getColumnCount(); c++) {
                sheet.autoSizeColumn(c);
                // padding minimo 12 caratteri, massimo 50
                int w = sheet.getColumnWidth(c);
                sheet.setColumnWidth(c, Math.min(Math.max(w + 512, 3000), 14000));
            }

            // ── Freeze riga intestazione ─────────────────────────
            sheet.createFreezePane(0, 1);

            // ── Riga totale in fondo ─────────────────────────────
            aggiungiRigaTotale(wb, sheet, model, stileIntestazione);

            // ── Metadati nel foglio ──────────────────────────────
            aggiungiMetadati(wb, sheet, sheetName, model.getRowCount());

            // ── Salvataggio ──────────────────────────────────────
            try (FileOutputStream fos = new FileOutputStream(file)) {
                wb.write(fos);
            }

            // ── Feedback utente con opzione "Apri" ───────────────
            File finalFile = file;
            int scelta = JOptionPane.showConfirmDialog(parent,
                    "<html>File salvato correttamente:<br><b>" + file.getAbsolutePath() +
                            "</b><br><br>Vuoi aprirlo adesso?</html>",
                    "Esportazione completata",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
            if (scelta == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(finalFile);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent,
                    "Errore durante l'esportazione:\n" + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Stili ─────────────────────────────────────────────────────

    private static CellStyle creaStileIntestazione(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(hexToRgb(BLU_ACCIAIO_HEX), null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBottomBorderColor(new XSSFColor(hexToRgb(BLU_MEDIO_HEX), null));

        XSSFFont f = wb.createFont();
        f.setBold(true);
        f.setColor(new XSSFColor(hexToRgb(BIANCO_HEX), null));
        f.setFontName("Arial");
        f.setFontHeightInPoints((short) 11);
        s.setFont(f);
        return s;
    }

    private static CellStyle creaStileNormale(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(hexToRgb(BIANCO_HEX), null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setBorderBottom(BorderStyle.HAIR);
        s.setBottomBorderColor(new XSSFColor(new byte[]{(byte)220,(byte)220,(byte)220}, null));

        XSSFFont f = wb.createFont();
        f.setFontName("Arial");
        f.setFontHeightInPoints((short) 10);
        s.setFont(f);
        return s;
    }

    private static CellStyle creaStileAlternato(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(hexToRgb(GRIGIO_HEADER), null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setBorderBottom(BorderStyle.HAIR);
        s.setBottomBorderColor(new XSSFColor(new byte[]{(byte)220,(byte)220,(byte)220}, null));

        XSSFFont f = wb.createFont();
        f.setFontName("Arial");
        f.setFontHeightInPoints((short) 10);
        s.setFont(f);
        return s;
    }

    private static CellStyle creaStileAlert(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(hexToRgb(ROSSO_ALERT), null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setBorderBottom(BorderStyle.HAIR);
        s.setBottomBorderColor(new XSSFColor(new byte[]{(byte)255,(byte)180,(byte)180}, null));

        XSSFFont f = wb.createFont();
        f.setFontName("Arial");
        f.setColor(new XSSFColor(hexToRgb(ROSSO_TESTO), null));
        f.setFontHeightInPoints((short) 10);
        s.setFont(f);
        return s;
    }

    private static CellStyle creaStileNumerico(XSSFWorkbook wb) {
        CellStyle s = creaStileNormale(wb);
        s.setAlignment(HorizontalAlignment.RIGHT);
        return s;
    }

    // ── Riga totale in fondo ─────────────────────────────────────
    private static void aggiungiRigaTotale(XSSFWorkbook wb, XSSFSheet sheet,
                                           DefaultTableModel model, CellStyle stileHead) {
        int totRow = model.getRowCount() + 1;
        Row row = sheet.createRow(totRow);
        row.setHeight((short) 500);

        XSSFCellStyle sTot = wb.createCellStyle();
        sTot.cloneStyleFrom(stileHead);
        sTot.setFillForegroundColor(new XSSFColor(hexToRgb(AZZURRO_HEX), null));
        XSSFFont fTot = wb.createFont();
        fTot.setBold(true);
        fTot.setFontName("Arial");
        fTot.setFontHeightInPoints((short) 10);
        fTot.setColor(new XSSFColor(hexToRgb(BLU_ACCIAIO_HEX), null));
        sTot.setFont(fTot);

        Cell label = row.createCell(0);
        label.setCellValue("TOTALE RIGHE: " + model.getRowCount());
        label.setCellStyle(sTot);

        // Somma automatica delle colonne numeriche
        for (int c = 1; c < model.getColumnCount(); c++) {
            Cell cell = row.createCell(c);
            boolean tuttiNumerici = true;
            for (int r = 0; r < model.getRowCount(); r++) {
                Object v = model.getValueAt(r, c);
                if (v == null || v.toString().trim().isEmpty()) continue;
                try { Double.parseDouble(v.toString().replace("+","").trim()); }
                catch (NumberFormatException e) { tuttiNumerici = false; break; }
            }
            if (tuttiNumerici && model.getRowCount() > 0) {
                // Lettera colonna Excel (A=0 → col c+1)
                String colLetter = colToLetter(c);
                cell.setCellFormula("SUM(" + colLetter + "2:" + colLetter + (model.getRowCount() + 1) + ")");
            }
            cell.setCellStyle(sTot);
        }
    }

    // ── Foglio metadati separato ──────────────────────────────────
    private static void aggiungiMetadati(XSSFWorkbook wb, XSSFSheet dataSheet,
                                         String nome, int righe) {
        XSSFSheet meta = wb.createSheet("Info");
        String[][] info = {
                {"Report",       nome},
                {"Esportato il", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))},
                {"Righe dati",   String.valueOf(righe)},
                {"Generato da",  "WMS — Sistema Gestione Magazzino"},
        };

        XSSFCellStyle sBold = wb.createCellStyle();
        XSSFFont fBold = wb.createFont();
        fBold.setBold(true); fBold.setFontName("Arial");
        sBold.setFont(fBold);

        for (int i = 0; i < info.length; i++) {
            Row r = meta.createRow(i);
            Cell k = r.createCell(0); k.setCellValue(info[i][0]); k.setCellStyle(sBold);
            Cell v = r.createCell(1); v.setCellValue(info[i][1]);
        }
        meta.autoSizeColumn(0);
        meta.autoSizeColumn(1);

        // Riporta il dataSheet come foglio attivo
        wb.setActiveSheet(wb.getSheetIndex(dataSheet));
        wb.setSheetOrder(meta.getSheetName(), wb.getNumberOfSheets() - 1);
    }

    // ── Helpers ──────────────────────────────────────────────────
    private static byte[] hexToRgb(String hex) {
        return new byte[]{
                (byte) Integer.parseInt(hex.substring(0, 2), 16),
                (byte) Integer.parseInt(hex.substring(2, 4), 16),
                (byte) Integer.parseInt(hex.substring(4, 6), 16)
        };
    }

    /** Converte indice colonna 0-based in lettera Excel (0=A, 25=Z, 26=AA…) */
    private static String colToLetter(int col) {
        StringBuilder sb = new StringBuilder();
        col++; // 1-based
        while (col > 0) {
            col--;
            sb.insert(0, (char) ('A' + col % 26));
            col /= 26;
        }
        return sb.toString();
    }
}