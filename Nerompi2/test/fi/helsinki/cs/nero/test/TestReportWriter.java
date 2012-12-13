package fi.helsinki.cs.nero.test;

import fi.helsinki.cs.nero.logic.ODTReportPrinter;
import fi.helsinki.cs.nero.logic.ReportWriter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.JTable;
import junit.framework.TestCase;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 *
 * @author lpesola
 */
public class TestReportWriter extends TestCase {

    private final String structuredFileType = "ods";
    private Vector<Vector<Object>> data;
    private Vector<Object> columns;

    public TestReportWriter(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        columns = new Vector<>();
        data = new Vector<>();
        columns.add("eka");
        columns.add("toka");
        Vector<Object> row1 = new Vector<>();
        row1.add("0.0");
        row1.add("0.1");
        data.add(row1);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPrintODS() {
        File f = new File("ReportWriterTest.ods");

        JTable table = new JTable(data, columns);
        table.setAutoCreateRowSorter(true);
        ReportWriter instance = new ReportWriter(table, structuredFileType);
        instance.print(f, "ods");
        boolean expResult = true;
        boolean result = f.exists() && f.isFile();
        assertEquals(expResult, result);
    }

    public void testPrintTXT() {
        File f = new File("ReportWriterTest.txt");
        JTable table = new JTable(data, columns);
        table.setAutoCreateRowSorter(true);
        ReportWriter instance = new ReportWriter(table, structuredFileType);
        instance.print(f, "txt");
        boolean expResult = true;
        boolean result = f.exists() && f.isFile();
        assertEquals(expResult, result);
    }

    public void testODSContainsCorrectDate() throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date today = new Date();
        String expResult = dateFormat.format(today);
        

        File f = new File("TestReportWriter.ods");
        Vector<Vector<Object>> dateData = new Vector();
        Vector<Object> row = new Vector();
        row.add("eka");
        row.add(today);
        dateData.add(row);
        dateData.add(row);
        
        JTable table = new JTable(dateData, columns);
        table.setAutoCreateRowSorter(true);

        ReportWriter instance = new ReportWriter(table, "ods");
        instance.print(f, "ods");

        Sheet sheet = SpreadSheet.createFromFile(f).getSheet(0);
        String result = sheet.getCellAt(1, 1).getValue().toString();
        assertEquals(expResult, result);

    }
    
    // jotain pitäs viel tehdä, missä on sortattu
    // ja filteröity taulukkoa
}
