package fi.helsinki.cs.nero.ui;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 * DefaultTableModelin aliluokka, joka tarjoaa omat metodinsa sarakkeiden
 * tyypin m‰‰ritt‰miseen ja arvojen hakemiseen taulukosta.
 * Koska taulukon sarakemallin ja taulukkomallin v‰lill‰ ei ole mit‰‰n yhteytt‰,
 * se voi johtaa ongelmiin, jos sarakemallin mukaan n‰kyvill‰ on enemm‰n tai
 * v‰hemm‰n sarakkeita, kuin mit‰ taulukkomallin mielest‰. T‰lllˆin mallia
 * k‰ytt‰v‰t metodit saattavat saada tietoonsa v‰‰r‰‰ dataa tai v‰‰r‰n tyypin
 * taulukon sarakkeelle.
 * 
 * T‰m‰ on kierretty siten, ett‰ t‰ll‰ mallilla on viite sek‰ siihen taulukkoon,
 * jonka malli se on, ett‰ siihen sarakemalliin, joka taulukolla on.
 * On k‰ytt‰j‰n vastuulla, ett‰ n‰m‰ on asetettu (oikein). 
 * 
 * Jos n‰in on, voidaan k‰tev‰sti aina tarkistaa sarakemallilta, mit‰ sarakkeita
 * n‰kyviss‰ on. Lis‰ksi taulukkoa itse‰‰n voidaan k‰ytt‰‰ siihen, ett‰ muutetaan
 * indeksej‰ view->model ja model->view.
 * 
 * 
 * @author lpesola
 */
public class NeroTableModel extends DefaultTableModel {

    /**
     * M‰‰ritt‰‰, mink‰niminen column identifier on p‰iv‰m‰‰ri‰ sis‰lt‰v‰ll‰
     * sarakkeella
     */
    final String dateColumnIdentifier;
    private TableColumnModel columnModel;
    private JTable table;

    public NeroTableModel(String identifier) {
        dateColumnIdentifier = identifier;
    }

    public void setColumnModel(TableColumnModel model) {
        columnModel = model;
    }

    public void setTable(JTable t) {
        table = t;
    }

    /**
     * Tarkistaa mit‰ tyypi‰ sarakkeessa oleva data on.
     * Kaikissa sarakkeissa data on String-muodossa lukuunottamatta
     * sit‰ saraketta, jossa on p‰iv‰m‰‰ri‰ eli Date-tyyppi‰.
     * Tarkistetaan, onko annettu indeksi p‰iv‰m‰‰r‰-sarake; jos ei ole, voidaan
     * palauttaa String.
     * 
     * @param index, sarakkeen indeksi
     * @return luokka, jonka ilmentymi‰ sarakkeen data on
     * @throws IllegalArgumentException jos p‰iv‰m‰‰r‰n sis‰lt‰v‰ sarake 
     * ei ole t‰ll‰ hetkell‰ n‰kyviss‰
     */
    @Override
    public Class getColumnClass(int index) {

        // jos dateColumnIdentifier heitt‰‰ IllegalArgumentiExceptionin
        // niin t‰llˆin p‰iv‰m‰‰r‰n sis‰lt‰v‰‰ saraketta ei ole n‰kyvill‰       
        try {
            int dateIndex = table.convertColumnIndexToModel(columnModel.getColumnIndex(dateColumnIdentifier));
            if (index == dateIndex) {
                return java.util.Date.class;
            } else {
                return String.class;
            }
        } catch (IllegalArgumentException ex) {
            return String.class;
        }
    }
    
// t‰m‰ ilmeisesti aiheutti ongelmia
// k‰ytˆst‰ poistaminen taas ei n‰ytt‰isi vaikuttavan mitenk‰‰n..
//    /**
//     * Tarkistaa sarakemallilta, kuinka monta saraketta on n‰kyvill‰. 
//     * 
//     * @return t‰ll‰ hetkell‰ n‰kyvill‰ olevien sarakkeiden m‰‰r‰ 
//     */
//
//    @Override
//    public int getColumnCount() {
//        if (columnModel != null) {
//            return columnModel.getColumnCount();
//        } else {
//            return super.getColumnCount();
//        }
//    }


}
