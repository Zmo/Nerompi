/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.logic;

import java.util.Enumeration;
import javax.swing.table.TableColumn;

/**
 *
 * @author lpesola
 */
public interface ReportWriter {

    void print(Object[][] tableData, Enumeration<TableColumn> columns);
    
}
