package fi.helsinki.cs.nero.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableModel;
import org.jopendocument.dom.OOUtils;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 *
 * @author lpesola
 */
public class XMLReportPrinter implements ReportWriter {
    // Save the data to an ODS file and open it.

    final File file;

    public XMLReportPrinter(File f) {
        file = f;
    }
    
    public void print(HashMap<Integer, Object[]> data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void print(TableModel model) {
        try {
            SpreadSheet.createEmpty(model).saveAs(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XMLReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XMLReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
