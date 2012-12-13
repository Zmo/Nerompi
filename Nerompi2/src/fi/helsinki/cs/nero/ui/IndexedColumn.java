package fi.helsinki.cs.nero.ui;

import javax.swing.table.TableColumn;

/**
 * Taulukon sarake, joka muistaa alkuperäisen indeksinsä.
 * @author lpesola
 * @see ReportsWindow
 */
public class IndexedColumn {

    private Integer index;
    private TableColumn column;

    public IndexedColumn(Integer index, TableColumn column) {
        this.index = index;
        this.column = column;
    }
    
    public TableColumn getTableColumn() {
        return this.column;
    }
    
    public int getIndex() {
        return this.index;
    }
}
