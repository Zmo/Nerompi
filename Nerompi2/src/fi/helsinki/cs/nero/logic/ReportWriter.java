/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.logic;

import java.util.HashMap;
import javax.swing.table.TableModel;

/**
 *
 * @author lpesola
 */
public interface ReportWriter {
    
    void print(HashMap<Integer, Object[]> data);
    void print(TableModel model);
}
