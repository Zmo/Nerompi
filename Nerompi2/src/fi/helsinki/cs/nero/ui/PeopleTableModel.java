package fi.helsinki.cs.nero.ui;

import javax.swing.DefaultRowSorter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author lpesola
 */
public class PeopleTableModel extends DefaultTableModel {

    final String dateColumnIdentifier;
    TableColumnModel columnModel;

    public PeopleTableModel(String identifier) {
        dateColumnIdentifier = identifier;
    }

    public void setColumnModel(TableColumnModel model) {
        columnModel = model;
    }

    @Override
    public Class getColumnClass(int index) {

        // jos dateColumnIdentifier heitt�� IllegalArgumentiExceptionin
        // niin t�ll�in p�iv�m��r�n sis�lt�v�� saraketta ei ole n�kyvill�
        // -> muut sarakkeet ovat tyyppi� String, joten voidaan palauttaa se
        
        try {
            int dateIndex = columnModel.getColumnIndex(dateColumnIdentifier);
            if (index == dateIndex) {
                return java.util.Date.class;
            } else {
                return String.class;
            }
        } catch (IllegalArgumentException ex) {
            System.out.println(ex);
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
//        columnModel.
//        return "foo";
//    }
}
