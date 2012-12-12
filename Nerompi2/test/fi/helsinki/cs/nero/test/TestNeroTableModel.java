package fi.helsinki.cs.nero.test;

import fi.helsinki.cs.nero.ui.NeroTableModel;
import java.util.Date;
import java.util.Vector;
import javax.swing.JTable;
import junit.framework.TestCase;

/**
 *
 * @author lpesola
 */
public class TestNeroTableModel extends TestCase {
    
    private NeroTableModel model;
    private final String columnName;
    private JTable table;
    private Vector<Vector<Object>> testidata;
    private Vector<Object> testisarakkeet, rivi1, rivi2, rivi3;
    
    public TestNeroTableModel(String testName) {
        super(testName);
        columnName = "testisarake";
        
        
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testisarakkeet = new Vector<>();
        testisarakkeet.add("sarake1");
        testisarakkeet.add(columnName);
        testisarakkeet.add("sarake3");

        rivi1 = new Vector<>();
        rivi1.add("0, 0");
        rivi1.add(new Date());
        rivi1.add("0, 2");
        
        rivi2 = new Vector<>();
        rivi2.add("1, 0");
        rivi2.add(new Date());
        rivi2.add("1, 2");
        
        rivi3 = new Vector<>();
        rivi3.add("1, 0");
        rivi3.add(new Date());
        rivi3.add("1, 2");
        
        testidata = new Vector<>();
        testidata.add(rivi1);
        testidata.add(rivi2);
        testidata.add(rivi3);
                        
        model = new NeroTableModel(columnName);
        model.setDataVector(testidata, testisarakkeet);
        table = new JTable(model);
        model.setColumnModel(table.getColumnModel());
        model.setTable(table);
                        
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getColumnClass method, of class NeroTableModel.
     */
    public void testGetColumnClassColumnDateColumnExists() {
        Class result = model.getColumnClass(1);
        Class expResult = Date.class;
        assertEquals(expResult, result);
    }
    
        public void testGetColumnClassOther() {
        Class result = model.getColumnClass(0);
        Class expResult = String.class;
        assertEquals(expResult, result);
    }
    
        public void testGetColumnClassDateColumnDoesNotExists() {
        Class result = model.getColumnClass(5);
        Class expResult = String.class;
        assertEquals(expResult, result);
    }
}
