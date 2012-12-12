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
 * Vastaa txt-muotoon tallentamisesta.
 *
 * @author lpesola
 */
public class TxtReportPrinter implements ReportPrinter {

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

    /**
     * Kirjoittaa konstruktorissa m‰‰riteltyyn tiedostoon saamansa datan.
     * Jotta sarakkeiden pituus voidaan tasata, t‰ytyy selvitt‰‰, mik‰ on
     * kunkin sarakkeen pisin sana. Lyhyempien sanojen per‰‰n voidaan t‰llˆin
     * laittaa v‰lilyˆntej‰, niin tiedoston ulkoasu pysyy siistin‰.
     * 
     * K‰yd‰‰n l‰pi lista, jossa data on ja lis‰t‰‰n kaikki siell‰ oleva
     * data tiedostoon. 
     * 
     * @param data data, joka tiedostoon tallennetaan
     */
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

    /**
     * Selvitt‰‰, mik‰ on parametrina saadun "matriisin" kunkin sarakkeen
     * pisin merkkijono.
     * K‰y l‰pi listan ja tarkistaa jokaisen alilistan alkion kohdalla, onko se
     * pidempi kuin toistaiseksi pisin tunnettu sana samalla sarakkeella.
     * Metodi olettaa, ett‰ kaikki rivit ovat saman mittaisia.
     * 
     * @param data taulukko, jonka sarakkeiden pituudet selvitet‰‰n
     * @return taulukko joka on samanpituinen kuin parametrina saadun listan 
     * sis‰lt‰mien listojen pituus ja jonka alkiot kuvaavat sit‰, mik‰ on pisin sana
     * vastaavalla "sarakkeella". 
     */
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

    /**
     * Lis‰‰ merkkijonon per‰‰n tyhji‰ v‰lilyˆntej‰ siten, ett‰ merkkijono
     * on vaadittavan pituinen.
     * 
     * @param original 
     * @param longest merkkijonon tarvittava pituus
     * @return jos alkuper‰isen merkkijonon pituus on lyhempi kuin m‰‰ritelty
     * pisin sana, palauttaa alkuper‰isen merkkijonon, jonka per‰‰n on lis‰tty 
     * tarvittava m‰‰r‰ tyhji‰ merkkej‰; muutoin palauttaa alkuper‰isen 
     * merkkijonon
     */
    private String padWord(Object original, int longest) {
        String word = original.toString();
        while (word.length() < longest) {
            word = word.concat(" ");
        }
        return word;
    }
}
