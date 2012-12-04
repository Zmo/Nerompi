package fi.helsinki.cs.nero.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 * Vastaa tallentamisesta ods-tiedostoon.
 * 
 * @author lpesola
 */
public class ODTReportPrinter implements ReportWriter {
    
    final File file;

    public ODTReportPrinter(File f) {
        file = f;
    }
    

    /**
     * Luo uuden ods-tiedoston ja tallentaa saamansa datan siihen.
     * 
     * @param model malli, jonka pohjalta ods-taulukko luodaan.
     */
    private void print(TableModel model) {
        try {
            SpreadSheet.createEmpty(model).saveAs(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ODTReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ODTReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Muodostaa taulukkomallin saamastaan datasta.
     * Luo uuden TableModelin, johon saadaan sarakkeiden nimi -vektori
     * sek‰ data-vektori parametrina saatavasta listasta.
     * 
     * @param data, data josta malli muoodstetaan
     */
    @Override
    public void print(List<List> data) {

        //1. lista sis‰lt‰‰ sarakkeiden nimet
        Vector<String> columnNames; 
        List remove = data.remove(0);
        columnNames = new Vector(remove);

        // loppuosa listaa sis‰lt‰‰ datan
        Vector<Vector<String>> rowData = new Vector<>(data.size());
        int i = 0;
        for (Iterator<List> it = data.iterator(); it.hasNext();) {
            List list = it.next();
            Vector<String> vector = new Vector(list);
            rowData.add(i, vector);
            i++;
        }
        
        DefaultTableModel defaultTableModel = new DefaultTableModel(rowData, columnNames);
        print(defaultTableModel);
    }

}
