package fi.helsinki.cs.nero.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    @Override
    public void print(HashMap<Integer, Object[]> data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void print(TableModel model) {
        try {
            SpreadSheet.createEmpty(model).saveAs(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ODTReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ODTReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
