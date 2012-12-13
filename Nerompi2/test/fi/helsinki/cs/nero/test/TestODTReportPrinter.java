package fi.helsinki.cs.nero.test;

import fi.helsinki.cs.nero.logic.ODTReportPrinter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import junit.framework.TestCase;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 *
 * @author lpesola
 */
public class TestODTReportPrinter extends TestCase {

    public TestODTReportPrinter(String testName) {
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

    /**
     * Test of print method, of class ODTReportPrinter.
     */
    public void testPrint() throws IOException {
        File f = new File("ODTReportPrinter.ods");
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
        ArrayList<List<String>> expResult = new ArrayList<>();
        expResult.add(row);
        expResult.add(row2);
        expResult.add(row3);
        
        ODTReportPrinter instance = new ODTReportPrinter(f);
        instance.print(data);

        final Sheet sheet = SpreadSheet.createFromFile(f).getSheet(0);

        ArrayList<List<String>> result = new ArrayList<>();
        for (int i = 0; i < sheet.getColumnCount(); i++) {
            ArrayList<String> sheetRow = new ArrayList<>();
            for (int j = 0; j < sheet.getRowCount(); j++) {
                sheetRow.add(sheet.getCellAt(j, i).getValue().toString());
            }
            result.add(sheetRow);
        }
        assertEquals(expResult, result);

    }

    public void testEmpty() throws IOException {

        System.out.println("print");
        File f = new File("ODTReportPrinter.ods");
        List<List> data = new ArrayList<>();
        ODTReportPrinter instance = new ODTReportPrinter(f);
        boolean expResult = true;
        boolean result = false;
        try {
            instance.print(data);
        } catch (IndexOutOfBoundsException ex) {
            result = true;
        }

        assertEquals(expResult, result);
    }


    public void testPrintOneRow() throws IOException {
        File f = new File("ODTReportPrinter.ods");
        List<List> data = new ArrayList();
        List<String> row = new ArrayList();
        row.add("eka");
        row.add("toka");
        row.add("kolmas");

        data.add(row);
        ODTReportPrinter instance = new ODTReportPrinter(f);
        instance.print(data);

        boolean expResult = true;
        boolean result = false;
        Sheet sheet = SpreadSheet.createFromFile(f).getSheet(0);

        if (sheet.getCellAt("A1").getValue().toString().equals("eka")
                && sheet.getCellAt("B1").getValue().toString().equals("toka")
                && sheet.getCellAt("C1").getValue().toString().equals("kolmas")) {
            result = true;
        }

        assertEquals(expResult, result);
    }
}
