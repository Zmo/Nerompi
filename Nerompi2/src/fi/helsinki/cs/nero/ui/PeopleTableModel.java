package fi.helsinki.cs.nero.ui;

import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author lpesola
 */
public class PeopleTableModel extends DefaultTableModel {

    final String dateColumnIdentifier;
    private TableColumnModel columnModel;
    private JTable table;

    public PeopleTableModel(String identifier) {
        dateColumnIdentifier = identifier;
    }

    public void setColumnModel(TableColumnModel model) {
        columnModel = model;
    }

    public void setTable(JTable t) {
        table = t;
    }

    @Override
    public Class getColumnClass(int index) {

        // jos dateColumnIdentifier heitt‰‰ IllegalArgumentiExceptionin
        // niin t‰llˆin p‰iv‰m‰‰r‰n sis‰lt‰v‰‰ saraketta ei ole n‰kyvill‰
        // -> muut sarakkeet ovat tyyppi‰ String, joten voidaan palauttaa se

        try {
            int dateIndex = columnModel.getColumnIndex(dateColumnIdentifier);
            if (index == dateIndex) {
                return java.util.Date.class;
            } else {
                return String.class;
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("P‰iv‰m‰‰r‰sarake ei n‰kyvill‰: " + ex);
            return String.class;
        }
    }
    

    @Override
    public int getColumnCount() {
        if (columnModel != null) {
            return columnModel.getColumnCount();
        } else {
            return super.getColumnCount();
        }
    }

//    @Override
//    public Object getValueAt(int row, int column) {
//        if (table != null) {
//
//
//            column = table.convertColumnIndexToModel(column);
//
//            Vector rowVector = (Vector) super.dataVector.elementAt(row);
//            return rowVector.elementAt(column);
//
//
//        } else {
//            return super.getValueAt(row, column);
//        }
//    }
}
