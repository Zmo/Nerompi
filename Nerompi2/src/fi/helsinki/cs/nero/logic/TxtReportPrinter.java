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
public class TxtReportPrinter implements ReportWriter {

    String fileName;
    File file;
    BufferedWriter writer;

    public TxtReportPrinter(String name) {
        fileName = name;
        file = new File(name);
        try {
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException ex) {
            Logger.getLogger(TxtReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public TxtReportPrinter(File f) {
        file = f;
        try {
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException ex) {
            Logger.getLogger(TxtReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(TxtReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void print(Object[][] tableData) {
        try {
            for (int i = 0; i < tableData.length; i++) {
                for (int j = 0; j < tableData[i].length; j++) {
                    Object entry = tableData[i][j];
                    if (entry != null) {
                        writer.append(entry.toString());
                    } else {
                        writer.append("ei ole");
                    }
                    writer.append(" ");
                }
                writer.newLine();
            }
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(TxtReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void print(Object[][] tableData, Enumeration<TableColumn> columns) {
        try {
            // lis‰t‰‰n sarakkeiden nimet
            while (columns.hasMoreElements()) {
                writer.append(columns.nextElement().getHeaderValue().toString() + " ");
            }
            writer.newLine();
            // kirjoitetaan data
            for (int i = 0; i < tableData.length; i++) {
                for (int j = 0; j < tableData[i].length; j++) {
                    Object entry = tableData[i][j];
                    if (entry != null) {
                        writer.append(entry.toString());
                    } else {
                        // jos kentt‰‰ ei ole m‰‰ritelty, tulostetaan jotain muuta
                        writer.append("ei ole");
                    }
                    writer.append(" ");
                }
                writer.newLine();
            }
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(TxtReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
