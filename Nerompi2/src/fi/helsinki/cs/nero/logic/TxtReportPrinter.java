package fi.helsinki.cs.nero.logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableModel;

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

 //   @Override
 //   public void print(HashMap<Integer, Object[]> data) {
//        TreeSet<Integer> sortedSet = new TreeSet<>(data.keySet());
//
//        try {
//            for (Integer i : sortedSet) {
//                Object[] row = data.get(i);
//                for (int j = 0; j < row.length; j++) {
//                    if (row[j] != null) {
//                        writer.append(row[j].toString() + " | ");
//                    } else {
//                        writer.append("ei ole | ");
//                    }
//                }
//                writer.newLine();
//            }
//            writer.flush();
//        } catch (IOException ex) {
//            Logger.getLogger(TxtReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    
    @Override
    public void print(HashMap<Integer, Vector<Object>> data) {
        TreeSet<Integer> sortedSet = new TreeSet<>(data.keySet());

        try {
            for (Integer i : sortedSet) {
                Vector<Object> row = data.get(i);
                for (int j = 0; j < row.size(); j++) {
                    if (row.get(j) != null) {
                        writer.append(row.get(j).toString() + " | ");
                    } else {
                        writer.append("ei ole | ");
                    }
                }
                writer.newLine();
            }
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(TxtReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void print(TableModel model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private int getLongestWord() {

        return 0;
    }

    private String padWord(String original, int longest) {

        return "";
    }
}
