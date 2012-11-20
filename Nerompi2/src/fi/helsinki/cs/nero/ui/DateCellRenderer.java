package fi.helsinki.cs.nero.ui;


import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


public class DateCellRenderer extends DefaultTableCellRenderer {

    /**
     * CellRenderer, joka osaa muotoilla p‰iv‰m‰‰r‰n oikein.
     * 
     *
     * @param table
     * @param value
     * @param isSelected
     * @param hasFocus
     * @param row
     * @param column
     * @return
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof Date) {
        // dateformatin avulla m‰‰ritell‰‰n muotoilu
            String strDate = new SimpleDateFormat("dd.MM.yyyy").format((Date) value);
            this.setText(strDate);
        }

        return this;
    }
}
