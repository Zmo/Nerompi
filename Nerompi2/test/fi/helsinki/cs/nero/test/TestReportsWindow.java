package fi.helsinki.cs.nero.test;

import fi.helsinki.cs.nero.ui.ReportsWindow;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;
import org.junit.Test;

/**
 *
 * @author lpesola
 */
public class TestReportsWindow extends FestSwingJUnitTestCase {

    private FrameFixture window;
    private StubReportSession trsession;

    @Override
    protected void onSetUp() {
        trsession = new StubReportSession();
        ReportsWindow frame = GuiActionRunner.execute(new GuiQuery<ReportsWindow>() {
            @Override
            protected ReportsWindow executeInEDT() {
                return new ReportsWindow(trsession);
            }
        });
        // IMPORTANT: note the call to 'robot()'
        // we must use the Robot from FestSwingTestngTestCase
        window = new FrameFixture(robot(), frame);
        window.show(); // shows the frame to test

    }

    @Test
    public void shouldShow4ColumnsWhenStarted() {
        window.table().requireColumnCount(4);
    }

    @Test
    public void shouldShowOnlyActivePeopleDataWhenStarted() {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        Vector<Vector<Object>> all = trsession.getPeopleData();
        String[][] contents = new String[1][4];
        Vector<Object> row = all.get(0);
        contents[0][0] = row.get(0).toString();
        contents[0][1] = row.get(1).toString();
        contents[0][2] = row.get(2).toString();
        contents[0][3] = df.format(row.get(6));
        window.table().requireContents(contents);
    }

    @Test
    public void shouldShowRoomDataWhenDataChanged() {
        window.comboBox("dataModeSelector").selectItem("Huoneet");
        String[][] contents = new String[3][5];
        Vector<Vector<Object>> roomData = trsession.getRoomData();
        Vector<Object> row = roomData.get(0);
        contents[0][0] = row.get(0).toString();
        contents[0][1] = row.get(1).toString();
        contents[0][2] = row.get(2).toString();
        contents[0][3] = row.get(3).toString();
        contents[0][4] = row.get(5).toString();
        row = roomData.get(1);
        contents[1][0] = row.get(0).toString();
        contents[1][1] = row.get(1).toString();
        contents[1][2] = row.get(2).toString();
        contents[1][3] = row.get(3).toString();
        contents[1][4] = row.get(5).toString();
        row = roomData.get(2);
        contents[2][0] = row.get(0).toString();
        contents[2][1] = row.get(1).toString();
        contents[2][2] = row.get(2).toString();
        contents[2][3] = row.get(3).toString();
        contents[2][4] = row.get(5).toString();

        window.table().requireContents(contents);
    }

    @Test
    public void shouldShowInactivePeopleDataWhenInactiveSelected() {
        window.checkBox("showInactive").click();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        Vector<Vector<Object>> all = trsession.getPeopleData();
        String[][] contents = new String[2][4];
        Vector<Object> row = all.get(0);
        contents[0][0] = row.get(0).toString();
        contents[0][1] = row.get(1).toString();
        contents[0][2] = row.get(2).toString();
        contents[0][3] = df.format(row.get(6));
        row = all.get(1);
        contents[1][0] = row.get(0).toString();
        contents[1][1] = row.get(1).toString();
        contents[1][2] = row.get(2).toString();
        contents[1][3] = "";
        
        window.table().requireContents(contents);

    }

    @Test
    public void shouldChangeCheckedBoxesWhenDataChanged() {
        window.comboBox("dataModeSelector").selectItem("Huoneet");
        window.checkBox("showFloor").requireSelected();
    }

    @Test
    public void shouldAddColumnWhenColumnCheckboxChecked() {
        window.comboBox("dataModeSelector").selectItem("Huoneet");
        int count = window.table().component().getColumnCount();
        window.checkBox("showRoomKeyReservations").click();
        window.table().requireColumnCount(count + 1);
    }
}