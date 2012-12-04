package fi.helsinki.cs.nero.logic;

import java.util.List;

/**
 * M‰‰ritt‰‰ mit‰ metodeita tallentamisesta/tulostamisesta 
 * vastaavien luokkien on toteutettava.
 *
 * @author lpesola
 */
public interface ReportWriter {

    /**
     * Tallentaa tiedostoon saamansa datan.
     * 
     * @param data taulukko datasta, joka tallennetaan
     */
    void print(List<List> data);
}
