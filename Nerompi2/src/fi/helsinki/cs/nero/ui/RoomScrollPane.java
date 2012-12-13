/*
 * Created on Nov 6, 2004
 */
package fi.helsinki.cs.nero.ui;


import fi.helsinki.cs.nero.NeroApplication;
import fi.helsinki.cs.nero.data.PhoneNumber;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.data.Room;
import fi.helsinki.cs.nero.data.TimeSlice;
import fi.helsinki.cs.nero.event.NeroObserver;
import fi.helsinki.cs.nero.event.NeroObserverTypes;
import fi.helsinki.cs.nero.logic.Session;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 * Rakentaa aktiivisen huoneen tiedoista esityksen.
 */
public class RoomScrollPane extends JScrollPane implements NeroObserver {
  
    /**
     * Työpisteiden (PostReservations-oliot) esitykset listana..
     */
    private LinkedList postsList = null;
 
    /**
     * Viite aktiiviseen huoneeseen, eli esitettävä huone.
     */
    private Room activeRoom;
    
    /**
     * Käsiteltävä sessio.
     */
    private Session sessio = null;
    
    /**
     * Käytössä oleva rivin pituus.
     */
    private final int ROW_LENGTH;
    
    /**
     * Päivä/pikselit skaala.
     */
    private double DAY_IN_PIXELS;
    
    /**
     * Käytössä oleva aikaväli.
     */
    private TimeSlice timeScale = null;
    
    /**
     * Pääpaneli, staattinen jotta sen voi antaa yläluokan konstruktorille parametrina.
     */
    private static JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    
    /**
     * Paneli johon muut panelit lisätään.
     */
    private JPanel postsPanel = null;
    
    /**
     * ??
     */
    private JFrame mainFrame = null;
    
    private static Border raisedBevel = BorderFactory.createBevelBorder(BevelBorder.RAISED);
    private static Border raisedEtched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
    
    private static final Color ROOM_HEADER_BG = Color.LIGHT_GRAY;
    private static final Color POST_HEADER_BG = new Color(255,240,192);
    private static final Color BG = new Color(90,119,173);
	    
    private final static ImageIcon OY; 
    private final static ImageIcon OA;
    private final static ImageIcon VY; 
    private final static ImageIcon VA;

    static{
		OY = new ImageIcon(NeroApplication.getProperty("img_oy"));
		OA = new ImageIcon(NeroApplication.getProperty("img_oa"));
		VY = new ImageIcon(NeroApplication.getProperty("img_vy"));
		VA = new ImageIcon(NeroApplication.getProperty("img_va"));
    }
    
    /**
     * 
     * Konstruktori.
     * @param sessio Viite sessio olioon.
     * @param mainFrame ?
     */
    public RoomScrollPane(Session sessio, JFrame mainFrame) {
        super(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        this.sessio = sessio;
        this.mainFrame = mainFrame;
        this.timeScale = this.sessio.getFilterTimescale();
        this.activeRoom = this.sessio.getActiveRoom();
        this.postsPanel = new JPanel();
   
        //Kuuntelijat = mistä muutoksista ollaan kiinostuneita.
        this.sessio.registerObserver(NeroObserverTypes.ROOMS, this);
        this.sessio.registerObserver(NeroObserverTypes.ACTIVE_ROOM, this);
        this.sessio.registerObserver(NeroObserverTypes.RESERVATIONS, this);
        this.sessio.registerObserver(NeroObserverTypes.TIMESCALE, this);

    		BevelBorder mainBorder = 
    		    (BevelBorder)BorderFactory.createBevelBorder(BevelBorder.LOWERED);

        setBorder(mainBorder);

        int borderWidth = mainBorder.getBorderInsets(this).left +
            mainBorder.getBorderInsets(this).right;

        this.ROW_LENGTH = NeroUI.RIGHT_WIDTH - JMultiSlider.SIDE_WIDTH -
    		borderWidth - this.getVerticalScrollBar().getMaximumSize().width;

        this.getVerticalScrollBar().setUnitIncrement(16); // nopeuttaa scrollaamista
        this.generate();
        
        this.giveThisToSession();
        
    }
    
    /**
     * <p>
     * Generoi aktiivisesta huoneesta uuden graaffisen esityksen käyttöliittymään.
     * </p>
     *
     */
    private void generate() {
        
        long startTime = System.currentTimeMillis();
        		
        if(this.activeRoom == null) {
            // Aktiivinen huone on null, ei generoida huonetietoja.
            return;
        }
        
        if(this.timeScale.length() > 0) {
            DAY_IN_PIXELS = (1.0*ROW_LENGTH)/this.timeScale.length();
        } else {
            DAY_IN_PIXELS = 1;
        }
        
        //Poistetaan vanha postsPanel ja luodaan uusi tilalle.
        if(this.postsPanel != null) {
            this.postsPanel.removeAll();
        }
        
        Post[] posts = this.activeRoom.getPosts();
        this.postsList = new LinkedList();
        
        for(int i=0; i<posts.length; ++i) {
            PostsReservations postsReservations = new PostsReservations(posts[i], DAY_IN_PIXELS, sessio, null);
            postsList.add(postsReservations);
        }
        
        Iterator postsIterator = postsList.iterator();
        
        postsPanel.setLayout(new BoxLayout(postsPanel, BoxLayout.Y_AXIS));
        
        //Huoneen headeri.
        JPanel roomHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));

        roomHeader.setBackground(ROOM_HEADER_BG);
        roomHeader.setBorder(raisedBevel);
        
        //Huoneen nimikyltti.
        JLabel roomLabel = new JLabel(
                this.activeRoom.getRoomName()
                +" "+activeRoom.getBuildingName()
                +" "+activeRoom.getRoomSize()
                +" m2"
        );
        roomHeader.add(roomLabel);
        
        //Huoneen työpisteen lisäysnappi.
        JButton addPostButton = new JButton("lisää työpiste");
        addPostButton.addActionListener(new ButtonListener(sessio));
        roomHeader.add(addPostButton);
        
        postsPanel.add(roomHeader);
       
        while(postsIterator.hasNext()) {
            
            PostsReservations postsReservations = (PostsReservations)postsIterator.next();
            LinkedList rows = postsReservations.getRows();
            Iterator rowIterator = rows.iterator();
            
            //Pyöristetty yläreuna
            JPanel extraHeader = new JPanel(new BorderLayout());
	    extraHeader.setBackground(BG);
	    JLabel l4 = new JLabel(VY);
	    JLabel l3 = new JLabel(OY);
	    
	    JPanel middlePiece = new JPanel();
	    middlePiece.setBackground(POST_HEADER_BG);
	    
	    extraHeader.add(BorderLayout.WEST, l4);
	    extraHeader.add(BorderLayout.CENTER, middlePiece);
	    extraHeader.add(BorderLayout.EAST, l3);
            
            postsPanel.add(extraHeader);

            //Työpisteen tiedot
            PostLabel postLabel = new PostLabel(sessio, postsReservations.getPost());
            postLabel.setBackground(POST_HEADER_BG);
            postsPanel.add(postLabel);
            
            PhoneLabel phoneLabel = new PhoneLabel(sessio, postsReservations.getPost());
            phoneLabel.addMouseListener(new PhoneLabelListener(mainFrame));
            postLabel.add(phoneLabel);
            
            PhoneNumber[] phoneArray = postsReservations.getPost().getPhonenumbers();
            StringBuffer sb = new StringBuffer();
            for(int i=0; i<phoneArray.length; ++i){
            	sb.append(phoneArray[i].getPhoneNumber());
            	if(i < phoneArray.length - 1) {
            		sb.append(", ");
            	}
            }
            
            JLabel phoneNumberLabel = new JLabel(sb.toString());
            phoneNumberLabel.setMaximumSize(new Dimension(200, 20));
            phoneNumberLabel.setMinimumSize(new Dimension(200, 20));
            phoneNumberLabel.setPreferredSize(new Dimension(200, 20));
            
            postLabel.add(phoneNumberLabel);
            
            while(rowIterator.hasNext()) {
                
                //Tehdään riville paneeli johon elementit tulee peräkkäin.
                RowPanel rowPanel = new RowPanel(ROW_LENGTH);
                Row row = (Row)rowIterator.next();
                
                row.resetIterator();
                           
                //Luodaan jokaisen rivin jokaista varausjaksoa koskeva JPanel.
                while (row.hasNext()) {
                    TimelineElement reservation = (TimelineElement) row.next();
                    rowPanel.add(reservation);
                }

                postsPanel.add(rowPanel);
            }
            
            //Pyöristetty alareuna.
            JPanel footer = new JPanel(new BorderLayout());
            footer.setBackground(BG);
            footer.setPreferredSize(new Dimension(ROW_LENGTH, 10));
	
            JLabel l = new JLabel(VA);
            JLabel l2 = new JLabel(OA);
	
            JPanel middlePanel = new JPanel();
            middlePanel.setBackground(POST_HEADER_BG);
            footer.add(BorderLayout.WEST, l);
            footer.add(BorderLayout.CENTER, middlePanel);
            footer.add(BorderLayout.EAST, l2);
			
            //loppuun vielä tyhjä suikale
            JPanel emptyRow = new JPanel();
            emptyRow.setBackground(BG);
            emptyRow.setPreferredSize(new Dimension(ROW_LENGTH, 6));
                                    
            postsPanel.add(footer);
            postsPanel.add(emptyRow);

            mainPanel.add(postsPanel);
        }
        // Avainvarausten näyttäminen
        JPanel extraHeader = new JPanel(new BorderLayout());
	extraHeader.setBackground(BG);	
	JLabel l4 = new JLabel(VY);
	JLabel l3 = new JLabel(OY);
	
	JPanel middlePiece = new JPanel();
	middlePiece.setBackground(POST_HEADER_BG);
	
	extraHeader.add(BorderLayout.WEST, l4);
	extraHeader.add(BorderLayout.CENTER, middlePiece);
	extraHeader.add(BorderLayout.EAST, l3);
        
        postsPanel.add(extraHeader);
        
        RoomKeyReservationLabel rkrlabel = new RoomKeyReservationLabel(sessio, this.activeRoom);
        rkrlabel.setBackground(POST_HEADER_BG);
        postsPanel.add(rkrlabel);
        if(this.activeRoom.getRoomKeyReservations().size() > 0) {
            RoomKeyReservations rkreservations = new RoomKeyReservations(this.activeRoom, DAY_IN_PIXELS, sessio, null);
        
            LinkedList rows = rkreservations.getRows();
            Iterator rowIterator = rows.iterator();
                
            while(rowIterator.hasNext()) {
                
                //Tehdään riville paneeli johon elementit tulee peräkkäin.
                RowPanel rowPanel = new RowPanel(ROW_LENGTH);      
                Row row = (Row)rowIterator.next();
                
                row.resetIterator();
            
                //Luodaan jokaisen rivin jokaista varausjaksoa koskeva JPanel.
                while(row.hasNext()) {
                    TimelineElement reservation = (TimelineElement)row.next();
                    reservation.setBorder(raisedEtched);
                    rowPanel.add(reservation);
                }
                postsPanel.add(rowPanel);
            }
        }
        // Pyöristetty alareuna
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG);
        footer.setPreferredSize(new Dimension(ROW_LENGTH, 10));
	
        JLabel l = new JLabel(VA);
        JLabel l2 = new JLabel(OA);
	
        JPanel middlePanel = new JPanel();
        middlePanel.setBackground(POST_HEADER_BG);
        footer.add(BorderLayout.WEST, l);
        footer.add(BorderLayout.CENTER, middlePanel);
        footer.add(BorderLayout.EAST, l2);
	
        //loppuun vielä tyhjä suikale
        JPanel emptyRow = new JPanel();
        emptyRow.setBackground(BG);
        emptyRow.setPreferredSize(new Dimension(ROW_LENGTH, 6));
        
        postsPanel.add(footer);
        postsPanel.add(emptyRow);
        
        //Asetetaan uusi post panel näkyviin.
        this.setViewportView(mainPanel);
    }
    
    /**
     * <p>
     * Toteuttaa rajapinnan jonka kautta komponentti saa
     * tiedot esitettävässä tiedossa tapahtuneista muutoksista.
     * </p>
     */
    public void updateObserved(int type) {
        //Aina haetaan päivitetty huone.
        this.activeRoom = this.sessio.getActiveRoom();
        
         /* timescale on syyt? p?ivitt?? */
        if(type == NeroObserverTypes.TIMESCALE) {
        	this.timeScale = sessio.getFilterTimescale();
        }
        
        this.generate();
    }
    public void giveThisToSession(){
        this.sessio.roomScrollPane = this;
    }
}