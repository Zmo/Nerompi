
package fi.helsinki.cs.nero.test;

import fi.helsinki.cs.nero.logic.TxtReportPrinter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 *
 * @author lpesola
 */
public class TxtReportPrinterTest extends TestCase {
    
    public TxtReportPrinterTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void testPrintEmpty() {
        File f = new File("TxtReportPrinterTest.txt");
        List<List> data = new ArrayList();
        TxtReportPrinter instance = new TxtReportPrinter(f);
        Boolean result = false;
        Boolean expResult = true;
        try {
            instance.print(data);
        } catch (Exception ex) {
            result = true;
        }
        assertEquals(expResult, result);
    }
    
        public void testPrintOneRow() {
        try {
            File f = new File("TxtReportPrinterTest.txt");
            List<List> data = new ArrayList();
            List<String> row = new ArrayList();
            row.add("eka");
            row.add("toka");
            row.add("kolmas");
            data.add(row);
            TxtReportPrinter instance = new TxtReportPrinter(f);
            instance.print(data);
            
            String result = readFile(f);
            String expResult = "eka| toka| kolmas| ";
            assertEquals(expResult, result);
        } catch (FileNotFoundException ex) {
            fail();
        }
    }
        
        public void testPrint() {
        try {
            File f = new File("TxtReportPrinterTest.txt");
            List<List> data = new ArrayList();
            List<String> row = new ArrayList();
            row.add("eka");
            row.add("toka");
            row.add("kolmas");

            List<String> row2 = new ArrayList();
            row2.add("eka2");
            row2.add("toka2");
            row2.add("kolmas2");
            
            List<String> row3 = new ArrayList();
            row3.add("eka3");
            row3.add("toka3");
            row3.add("kolmas3");
            
            data.add(row);
            data.add(row2);
            data.add(row3);
            TxtReportPrinter instance = new TxtReportPrinter(f);
            instance.print(data);
            
            String result = readFile(f);
            String expResult = "eka | toka | kolmas | \n"
                             + "eka2| toka2| kolmas2| \n"
                             + "eka3| toka3| kolmas3| \n";
            
            System.out.println("expected: "+expResult);
            System.out.println("result: "+result);
                    
            
            assertEquals(expResult, result);
        } catch (FileNotFoundException ex) {
            fail();
        }
    }
        
        public void testPrintOneColumn() {
        System.out.println("print");
        List<List> data = null;
        TxtReportPrinter instance = null;
        instance.print(data);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
        
        public void testEqualColumnWidths() {
        System.out.println("print");
        List<List> data = null;
        TxtReportPrinter instance = null;
        instance.print(data);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    private String readFile(File f) throws FileNotFoundException {
        String str = "";
        try {
            Scanner scanner = new Scanner(new FileReader(f));
            while (scanner.hasNextLine()) {
                str = str.concat(scanner.nextLine()+"\n");                
            }
        } catch (FileNotFoundException ex) {
            throw ex;
        }
        return str;
    }
        

}
