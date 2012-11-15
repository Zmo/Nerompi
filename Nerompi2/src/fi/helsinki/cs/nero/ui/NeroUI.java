package fi.helsinki.cs.nero.ui;


import com.jgoodies.plaf.plastic.Plastic3DLookAndFeel;
import com.jgoodies.plaf.plastic.theme.DesertBluer;
import fi.helsinki.cs.nero.event.NeroObserver;
import fi.helsinki.cs.nero.event.NeroObserverTypes;
import fi.helsinki.cs.nero.logic.Session;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;

public class NeroUI {
    /**
     * Sovelluslogiikan sessio-olio.
     */
    private Session session;
    
    private JScrollPane fullScrollPane;

    protected static final int LEFT_WIDTH = 650;
    protected static final int RIGHT_WIDTH = 580;
    protected static final int HEIGHT = 920;
    
    private class CursorObserver implements NeroObserver {

        public CursorObserver() {
            session.registerObserver(NeroObserverTypes.CURSORCHANGE, this);
        }
        
        /* (non-Javadoc)
         * @see fi.helsinki.cs.nero.event.NeroObserver#updateObserved(int)
         */
        @Override
        public void updateObserved(int type) {
        	switch(type) {
            case NeroObserverTypes.CURSORCHANGE:
            	setCursorType(session.getCursorType());
            	break;
        	}
        }
        
    }
    
	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        CursorObserver cursorObserver = new CursorObserver();

        //Create and set up the window.
        JFrame frame = new JFrame("Nero");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, HEIGHT + 40);
        frame.setResizable(true);
        
        // Tehdään valikkopalkki, siihen nappi ja nappiin valikko
        JMenuItem addPerson = new JMenuItem("Lisää henkilö");
        JMenuItem reports = new JMenuItem("Raportit");
        addPerson.setActionCommand("addPerson");
        reports.setActionCommand("reports");
        ButtonListener listener = new ButtonListener(this.session);
        addPerson.addActionListener(listener);
        reports.addActionListener(listener);
        
        JMenu menu = new JMenu("Valikko");
        menu.add(addPerson);
        menu.add(reports);
        
        JMenuBar menubar = new JMenuBar();
        menubar.add(menu);
        
        frame.add(menubar);
        frame.setJMenuBar(menubar);
        
	try{
            Plastic3DLookAndFeel.setMyCurrentTheme(new DesertBluer());
            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
            SwingUtilities.updateComponentTreeUI(frame);
	}
	catch(Exception e) {
		   //pitï¿½iskï¿½s sitten jotain muka tehï¿½?
	}
        
        JPanel fullPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        
        fullPanel.setMinimumSize(new Dimension(LEFT_WIDTH + RIGHT_WIDTH, HEIGHT));
        fullPanel.setPreferredSize(new Dimension(LEFT_WIDTH + RIGHT_WIDTH, HEIGHT));
        fullPanel.setMaximumSize(new Dimension(LEFT_WIDTH + RIGHT_WIDTH, HEIGHT));

        fullScrollPane = new JScrollPane(fullPanel);
        frame.getContentPane().add(fullScrollPane);
      
        //Alustetaan ja lisï¿½tï¿½ï¿½n vasen puolisko.
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        
        left.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        left.setMinimumSize(new Dimension(LEFT_WIDTH, HEIGHT));
        left.setPreferredSize(new Dimension(LEFT_WIDTH, HEIGHT));
        left.setMaximumSize(new Dimension(LEFT_WIDTH, HEIGHT));
        fullPanel.add(left);

        //Alustetaan ja lisï¿½tï¿½ï¿½n oikea puolisko.
        //Luodaan kerrostettu nï¿½kymï¿½, jossa aikajakson osoittavat pystyviivat ovat
        //muiden komponenttien pï¿½ï¿½llï¿½.
        JLayeredPane rightLayers = new JLayeredPane();
        rightLayers.setMinimumSize(new Dimension(RIGHT_WIDTH, HEIGHT));
        rightLayers.setPreferredSize(new Dimension(RIGHT_WIDTH, HEIGHT));
        rightLayers.setMaximumSize(new Dimension(RIGHT_WIDTH, HEIGHT));
        
        //Luodaan oikean puoliskon alempi osa.
        JPanel right = new JPanel(new BorderLayout());
        rightLayers.add(right, new Integer(0));
        right.setBounds(0, 0, RIGHT_WIDTH, HEIGHT);
        
        TimeSliceIndicatorPanel timeIndicatorPanel =
            new TimeSliceIndicatorPanel(session);
        rightLayers.add(timeIndicatorPanel, new Integer(1));
        timeIndicatorPanel.setBounds(0, 0, RIGHT_WIDTH, HEIGHT);
        
        //Luodaan oikean puoliskon ylempi osa.
        fullPanel.add(rightLayers);

        //Luodaan ja lisï¿½tï¿½ï¿½n hakupaneeli.
        left.add(new SearchPanel(this.session));
        
        //Luodaan ja lisï¿½tï¿½ï¿½n karttakomponentti.

        left.add(new Map(session, frame));

        // lisï¿½tï¿½ï¿½n statusbar
        left.add(new Statusbar(session, LEFT_WIDTH));

        //Luodaan ja lisï¿½tï¿½ï¿½n aikajana.
        DateSlider dateSlider = new DateSlider(session, RIGHT_WIDTH); 
        right.add(dateSlider, BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new PersonScrollPane(session, dateSlider, timeIndicatorPanel),
                new RoomScrollPane(session, frame)
        );
        splitPane.setBorder(null);
        splitPane.setBackground(new Color(175, 175, 175));
        splitPane.setMinimumSize(new Dimension(RIGHT_WIDTH, HEIGHT - 50));
        splitPane.setPreferredSize(new Dimension(RIGHT_WIDTH, HEIGHT - 50));
        splitPane.setMaximumSize(new Dimension(RIGHT_WIDTH, HEIGHT - 50));
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(600);

        // dummyPanel varaa tilaa sitï¿½ varten, ettï¿½ Slider saadaan oikeaan kohtaan 
        JPanel dummyPanel = new JPanel();
        dummyPanel.setPreferredSize(new Dimension(JMultiSlider.SIDE_WIDTH-1, 1));
        right.add(dummyPanel, BorderLayout.WEST);

        right.add(splitPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }
    
    public void setCursorWaiting() {
    		this.setCursorType(Cursor.WAIT_CURSOR);
    }
    
    public void setCursorNormal() {
    		this.setCursorType(Cursor.DEFAULT_CURSOR);
    }
    
    public void setCursorType(int type) {
    	fullScrollPane.setCursor(Cursor.getPredefinedCursor(type));
	}
    
    public NeroUI(Session session) {
        	this.session = session;    
        // Luo ohjelman kï¿½yttï¿½liittymï¿½ Swingin tapahtumankï¿½sittelijï¿½sï¿½ikeessï¿½
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
