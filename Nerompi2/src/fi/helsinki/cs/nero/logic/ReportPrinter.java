
package fi.helsinki.cs.nero.logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableColumn;

/**
 *
 * @author lpesola
 */
public class ReportPrinter {
    
    String fileName;
    File file;
    BufferedWriter writer;
    
    public ReportPrinter(String name) {
        fileName = name;
        file = new File(name);
        try {
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException ex) {
            Logger.getLogger(ReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void print(Enumeration<TableColumn> columns) {
        try {
            while (columns.hasMoreElements()) {
            writer.append(columns.nextElement().getHeaderValue().toString()); 
            writer.newLine();
            }
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(ReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void print(Object[][] tableData) {
        try {
            for(int i = 0; i < tableData.length; i++) {
                for(int j = 0; j < tableData[i].length; j++) {
                writer.append(tableData[i][j].toString());
                writer.append(" ");
                }
            writer.newLine();
            }
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(ReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
