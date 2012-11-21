package fi.helsinki.cs.nero.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
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
    // Save the data to an ODS file and open it.

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
        // - columnNames on näkyvät sarakkeet
        // - datana on näkyvien sarakkeiden data
        // --> saadaan puljaamalla hakemalla listasta

        //avaimella 0 saadaan sarakkeiden nimet
        Vector<Object> columnNames; 
        List remove = data.remove(0);
        columnNames = new Vector(remove);

        // lopuilla avaimilla saadaan loppu data
        Vector<Vector<Object>> rowData = new Vector<>(data.size());
        int i = 0;
        for (List list : data) {
            Vector<Object> vector = new Vector(list);
            rowData.add(i, vector);
            i++;
        }
        
        DefaultTableModel defaultTableModel = new DefaultTableModel(rowData, columnNames);
        print(defaultTableModel);
    }

}
