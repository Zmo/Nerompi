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
 *
 * @author lpesola
 */
public class ODTReportPrinter implements ReportWriter {
    
    final File file;

    public ODTReportPrinter(File f) {
        file = f;
    }
    


    private void print(TableModel model) {
        try {
            SpreadSheet.createEmpty(model).saveAs(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ODTReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ODTReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void print(List<List> data) {

        // luodaan uusi TableModel, jolla
        // - columnNames on n‰kyv‰t sarakkeet
        // - datana on n‰kyvien sarakkeiden data
        // --> saadaan puljaamalla hakemalla listasta

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
