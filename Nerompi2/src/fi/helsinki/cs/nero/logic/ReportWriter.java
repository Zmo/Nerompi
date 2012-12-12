package fi.helsinki.cs.nero.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import javax.swing.DefaultRowSorter;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 *
 * @author lpesola
 */
public class ReportWriter {

    JTable table;
    ReportPrinter printer;
    String structuredFileType;

    public ReportWriter(JTable jtable, String structuredFile) {
        table = jtable;
        structuredFileType = structuredFile;
    }

     /**
     * Antaa k‰skyn tulostaa n‰kyvill‰ olevan taulukon datan valitussa
     * formaatissa. Tarkistaa, mik‰ tiedostomuoto on valittu ja valitsee
     * tulostamiseen k‰ytetyn luokan sen mukaan. Antaa kirjoittajalle taulukon
     * n‰ytt‰m‰n datan listana.
     *
     * @param f, tiedosto, johon data kirjoitetaan
     */
    public void print(File f, String fileType) {
        
        if (fileType.equals(structuredFileType)) {
            printer = new ODTReportPrinter(f);
        } else {
            printer = new TxtReportPrinter(f);
        }
        printer.print(getTableDataAsList());
    }
    
    
     /**
     * K‰y l‰pi n‰kyvill‰ olevat sarakkeet ja palauttaa niiden indeksit. Pyyt‰‰
     * taulukolta sen sarakemallin ja pyyt‰‰ silt‰ sarakkeet (n‰m‰ ovat siis
     * n‰kyvill‰). Iteroi niiden l‰pi ja tarkistaa, mik‰ niiden indeksi on.
     * Lis‰‰ indeksin taulukkoon.
     *
     * @return taulukko t‰ll‰ hetkell‰ n‰kyvill‰ olevien sarakkeiden indekseist‰
     */
    private int[] listShownColumnsByIndex() {
        Enumeration<TableColumn> e = table.getColumnModel().getColumns();
        int[] neededIndexes = new int[table.getColumnCount()];
        int z = 0;
        while (e.hasMoreElements()) {
            String s = e.nextElement().getIdentifier().toString();
            neededIndexes[z] = table.getColumnModel().getColumnIndex(s);
            z++;
        }
        return neededIndexes;
    }
    
     /**
     * Hakee GUIn taulukossa t‰ll‰ hetkell‰ n‰kyvill‰ olevan datan. Luo listan,
     * jonka alkiot vastaavat taulukon rivej‰. Alkiot ovat listoja, joiden
     * alkiot vastaavat rivin sarakkeita. Hakee n‰kyvill‰ olevat sarakkeiden
     * nimet ja t‰m‰n j‰lkeen n‰kyvill‰ olevan datan ja yhdist‰‰ n‰m‰ yhdeksi
     * listaksi.
     *
     * @return lista listoja, joka kuvaa GUIn taulukossa t‰ll‰ hetkell‰
     * n‰kyvill‰ olevan datan
     * @see getShownColumnIdentifiers()
     * @see getShownColumnData()
     */
    private List<List> getTableDataAsList() {

        List<List> list = new ArrayList<>();
        /* - tarkista, mitk‰ sarakkeet ovat n‰kyvill‰
         * - ota talteen niiden nimet ja laita ensimm‰iseksi listaan
         * - hae data niist‰ sarakkeista, jotka ovat n‰kyvill‰
         */
        List columnIdentifiers = getShownColumnIdentifiers();
        list.add(0, columnIdentifiers);
        list.addAll(1, getShownColumnData());

        return list;
    }
    
    
     /**
     * Listaa kaikkien GUIn taulukossa n‰kyvill‰ olevien sarakkeiden otsakkeet.
     * Pyyt‰‰ mallilta kaikki sarakkeet ja k‰y l‰pi niiden otsaketiedot.
     *
     * @return lista n‰kyvill‰ olevien sarakkeiden otsikoista
     */
    private List getShownColumnIdentifiers() {

        Enumeration<TableColumn> e = table.getColumnModel().getColumns();
        int z = 0;
        List identifiers = new ArrayList();
        while (e.hasMoreElements()) {
            String s = e.nextElement().getIdentifier().toString();
            identifiers.add(z, s);
            z++;
        }
        return identifiers;
    }
    
     /**
     * Tuottaa kokoelman, joka sis‰lt‰‰ kaiken GUIn taulukossa n‰kyvill‰ olevan
     * datan. K‰y l‰pi kaikki n‰kyv‰t rivit (view) ja selvitt‰‰ niiden indeksin
     * alla olevassa taulukkomallissa (model). Selvitt‰‰ jokaisen n‰kyvill‰
     * olevan rivin sarakkeen indeksin mallissa (view -> model) ja hakee rivin
     * ja sarakkeen perusteella taulukosta n‰kyv‰n datan.
     *
     * @return kokoelma listoja. Kokoelma sis‰lt‰‰ kaiken n‰kyvill‰ olevan datan
     * ja se on samassa j‰rjestyksess‰ kuin se on taulukon n‰kym‰ss‰. Listan
     * alkiot vastaavat rivin sarakkeita ja kokoelman alkiot taulukon rivej‰.
     */
    private Collection<? extends List> getShownColumnData() {

        int[] neededIndexes = listShownColumnsByIndex();
        TableModel tableModel = table.getModel();
        DefaultRowSorter rs = (DefaultRowSorter) table.getRowSorter();
        int columnCount = table.getColumnCount();
        int rowCount = rs.getViewRowCount();
        ArrayList list = new ArrayList(rowCount);


        //TODO: ehk‰ tuon p‰iv‰m‰‰r‰n lyhent‰misen voi tehd‰ myˆskin jossain muualla
        for (int i = 0; i < rowCount; i++) {
            List rowList = new ArrayList(columnCount);
            int rowIndexInView = rs.convertRowIndexToModel(i);
            for (int j = 0; j < columnCount; j++) {
                Object o = tableModel.getValueAt(rowIndexInView,
                        table.convertColumnIndexToModel(neededIndexes[j]));
                String value;
                if (o == null) {
                    value = "";
                } else if (o.getClass() == Date.class) {
                    value = dateToShortString((Date) o);
                } else {
                    value = o.toString();
                }
                rowList.add(j, value);
            }
            list.add(rowList);
        }
        return list;
    }
    
     /**
     * Tuottaa Date-oliosta merkkijonon, joka kuvaa sen p‰iv‰m‰‰r‰n.
     *
     * @param date p‰iv‰m‰‰r‰, josta teksti muodostetaan
     * @return annettu p‰iv‰m‰‰r‰ merkkijonona, joka on muotoa pp.kk.vvvv
     */
    private String dateToShortString(Date date) {
        if (date != null) {
            String dateString = "";
            dateString = dateString.concat(date.getDate() + ".");
            dateString = dateString.concat((1 + date.getMonth()) + ".");
            dateString = dateString.concat(new Integer((date.getYear()) + 1900).toString());
            return dateString;
        } else {
            return null;
        }
    }
}
