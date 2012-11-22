package fi.helsinki.cs.nero.logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lpesola
 */
public class TxtReportPrinter implements ReportWriter {

    String fileName;
    File file;
    BufferedWriter writer;

    public TxtReportPrinter(File f) {
        file = f;
        try {
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException ex) {
            Logger.getLogger(TxtReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void print(List<List> data) {

        Integer lengths[] = getLongestWord(data);
        try {
            for (List list : data) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j) != null) {
                        writer.append(padWord(list.get(j), lengths[j]) + "| ");
                    } else {
                        writer.append(padWord("ei ole", lengths[j]) + "| ");
                    }
                }
                writer.newLine();
            }
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(TxtReportPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Integer[] getLongestWord(List<List> data) {
        Integer[] lengths = new Integer[data.get(0).size()];

        for (int i = 0; i < lengths.length; i++) {
            lengths[i] = data.get(0).get(i).toString().length();
        }
        for (Iterator<List> it = data.iterator(); it.hasNext();) {
            List list = it.next();
            for (int i = 0; i < list.size(); i++) {
                int wordLength;
                Object o = list.get(i);
                if (o == null) {
                    wordLength = 7;
                } else {
                    wordLength = o.toString().length();
                }
                if (lengths[i] < wordLength) {
                    lengths[i] = wordLength;
                }
            }
        }
        return lengths;
    }

    private String padWord(Object original, int longest) {
        String word = original.toString();
        while (word.length() < longest) {
            word = word.concat(" ");
        }
        return word;
    }
}
