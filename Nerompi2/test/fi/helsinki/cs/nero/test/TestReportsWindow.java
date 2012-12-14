package fi.helsinki.cs.nero.test;

import fi.helsinki.cs.nero.ui.ReportsWindow;
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