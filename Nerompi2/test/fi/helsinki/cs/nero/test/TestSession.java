/*
 * NeroCode - TestSession.java
 * Created on 10.11.2004
 * 
 * 
 */
package fi.helsinki.cs.nero.test;

import java.util.Date;

import junit.framework.Assert;
import junit.framework.TestCase;
import fi.helsinki.cs.nero.data.PhoneNumber;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.data.Project;
import fi.helsinki.cs.nero.data.Room;
import fi.helsinki.cs.nero.data.TimeSlice;
import fi.helsinki.cs.nero.event.NeroObserver;
import fi.helsinki.cs.nero.event.NeroObserverManager;
import fi.helsinki.cs.nero.event.NeroObserverTypes;
import fi.helsinki.cs.nero.logic.Session;

/**
 * @author Timi Tuohenmaa
 *
 */
public class TestSession extends TestCase implements NeroObserver {
    
    private Session session;
    
    private TimeSlice timeSlice;
    
    private Project project;
    
    private Room room;
    
    private PhoneNumber[] phoneNumbers;
    
    private Post post;

    private NeroObserverManager obsman;
    
    private int[] notifications;
    
    /**
     * Konstruktorin tulee välittää String-tyyppinen parametrinsa ylöspäin.
     * @param arg
     */
    public TestSession(String arg) {
        super(arg);
    }

    /**
     * Alustaa tarvittavat tiedot.
     */
    protected void setUp() {
        session = new Session();
        // laskurit notificationeille
        notifications = new int[NeroObserverTypes.NTYPES];
        // EI kuunnella mitään vakiona, paremmat testit näin..
        /*
        for(int i = 0; i < NeroObserverTypes.NTYPES; i++) {
        	notifications[i] = 0;
        	session.registerObserver(i, this);
        }
        */
        
        timeSlice = new TimeSlice(new Date(2004, 0, 1), new Date(2004, 11, 31));
        
        project = new Project(session, "iidee", "nimi", "vastuuhlö", timeSlice);

        room = new Room(session, "id", "Inpracticum", "1", "A123", "Sininen huone",
        		6.5, "It's kinda gay");

        post = new Post(session, "iidee", room, 0);
        
        phoneNumbers = new PhoneNumber[2];
        
        phoneNumbers[0] = new PhoneNumber(session, "numero1", post, "1234567");
        phoneNumbers[1] = new PhoneNumber(session, "numero2", post, "2345678");
        
    }

    /**
     * Jos tarvitsee tehdä jotain puhdistelua testien jälkeen.
     */
    protected void tearDown() {
        
    }
    
    public void testFilterTimescale() {
    	// ei saa antaa nullia
    	try {
    		session.setFilterTimescale(null);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	session.setFilterTimescale(timeSlice);
    	assertNotNull(session.getFilterTimescale());
    	assertEquals(timeSlice, session.getFilterTimescale());
    	
    	// tarkistetaan osa-aikavälin muutoksia
    	TimeSlice sub = new TimeSlice(new Date(2004, 5, 1), new Date(2004, 8, 1));
    	// asetetaan osa-aikaväli samaksi kuin koko roska
    	session.setTimeScaleSlice(this.timeSlice, true);
    	// asetetaan koko väliksi pienempi, myös osa-aikavälin pitäisi kutistua
    	session.setFilterTimescale(sub);
    	assertEquals(sub, session.getTimeScaleSlice());
    	assertEquals(sub.getStartDate(), session.getTimeScaleSlice().getStartDate());
    	assertEquals(sub.getEndDate(), session.getTimeScaleSlice().getEndDate());
    	
    }
    
    public void testFilterProject() {
    	session.registerObserver(NeroObserverTypes.FILTER_PROJECT, this);
    	session.registerObserver(NeroObserverTypes.FILTER_PEOPLE, this);

    	session.setFilterProject(project);
    	assertNotNull(session.getFilterProject());
    	assertEquals(project, session.getFilterProject());    	
    	// saa antaa myös null
    	session.setFilterProject(null);
    	assertNull(session.getFilterProject());
    	
    	assertEquals(2, this.notifications[NeroObserverTypes.FILTER_PROJECT]);
    	assertEquals(2, this.notifications[NeroObserverTypes.FILTER_PEOPLE]);
    }
    
    public void testFilterPersonName() {
    	session.registerObserver(NeroObserverTypes.FILTER_PEOPLE, this);
    	// ei saa antaa nullia
    	try {
    		session.setFilterPersonName(null);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	String name = "Jaska";
    	session.setFilterPersonName(name);
    	assertNotNull(session.getFilterPersonName());
    	assertEquals(name, session.getFilterPersonName());
    	
    	assertEquals(1, this.notifications[NeroObserverTypes.FILTER_PEOPLE]);
    }
    
    public void testFilterRoomName() {
    	session.registerObserver(NeroObserverTypes.FILTER_ROOMS, this);
    	
    	// ei saa antaa nullia
    	try {
    		session.setFilterRoomName(null);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	String name = "A123";
    	session.setFilterRoomName(name);
    	assertNotNull(session.getFilterRoomName());
    	assertEquals(name, session.getFilterRoomName());

    	assertEquals(1, this.notifications[NeroObserverTypes.FILTER_ROOMS]);
    }
    
    public void testFilterEndingContracts() {
    	session.registerObserver(NeroObserverTypes.FILTER_PEOPLE, this);
    	
    	session.setFilterEndingContracts(true);
    	assertTrue(session.getFilterEndingContracts());
    	session.setFilterEndingContracts(false);
    	assertFalse(session.getFilterEndingContracts());

    	assertEquals(2, this.notifications[NeroObserverTypes.FILTER_PEOPLE]);
    }
    
    public void testFilterMaxPosts() {
    	session.registerObserver(NeroObserverTypes.FILTER_ROOMS, this);
    	
    	// sallitut arvot -1 ja isommat
    	// -1 tarkoittaa ei filtteröidä
    	try {
    		session.setFilterMaxPosts(-2);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	// sallittuja arvoja (suuremmatkin on, mutta koska ylärajaa ei ole, niin ei testata
    	int limit = 10;
    	for(int i = -1; i < limit; i++) {
    		session.setFilterMaxPosts(i);
    		assertEquals(i, session.getFilterMaxPosts());
    	}
    	assertEquals(limit + 1, this.notifications[NeroObserverTypes.FILTER_ROOMS]);
    }
    
    public void testFilterWithoutPost() {
    	session.registerObserver(NeroObserverTypes.FILTER_PEOPLE, this);
    	
    	session.setFilterWithoutPost(true);
    	assertTrue(session.getFilterWithoutPost());
    	session.setFilterWithoutPost(false);
    	assertFalse(session.getFilterWithoutPost());
    	
    	assertEquals(2, this.notifications[NeroObserverTypes.FILTER_PEOPLE]);
    }
    
    public void testFilterFreePosts() {
    	session.registerObserver(NeroObserverTypes.FILTER_ROOMS, this);
    	
    	// sallitut arvot 1 ja isommat
    	try {
    		session.setFilterFreePosts(-1);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	try {
    		session.setFilterFreePosts(0);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	// sallittuja arvoja (suuremmatkin on, mutta koska ylärajaa ei ole, niin ei testata
    	int limit = 10;
    	for(int i = 1; i < limit; i++) {
    		session.setFilterFreePosts(i);
    		assertEquals(i, session.getFilterFreePosts());
    	}
    	assertEquals(limit-1, this.notifications[NeroObserverTypes.FILTER_ROOMS]);
    }
    
    public void testFilterPartTimeTeachers() {
    	session.registerObserver(NeroObserverTypes.FILTER_PEOPLE, this);
    	
    	session.setFilterPartTimeTeachers(true);
    	assertTrue(session.getFilterPartTimeTeachers());
    	session.setFilterPartTimeTeachers(false);
    	assertFalse(session.getFilterPartTimeTeachers());

    	assertEquals(2, this.notifications[NeroObserverTypes.FILTER_PEOPLE]);
    }
    
    public void testTimeScaleSlice() {
    	session.setFilterTimescale(this.timeSlice);
    	
    	// rekisteröidytään kuuntelemaan, mutta vasta setFilterTimescalen jälkeen
    	session.registerObserver(NeroObserverTypes.TIMESCALESLICEUPDATING, this);
    	session.registerObserver(NeroObserverTypes.TIMESCALESLICE, this);
    	
    	TimeSlice slice = null;
    	// timeslice ei saa olla null, toinen parametri true tai false
    	try {
    		session.setTimeScaleSlice(null, true);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	try {
    		session.setTimeScaleSlice(null, false);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	
    	// ei saa alkaa ennen kokonaisväliä
		slice = new TimeSlice(new Date(2003, 11, 31), new Date(2004, 0, 1));
    	try {
    		session.setTimeScaleSlice(slice, true);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	try {
    		session.setTimeScaleSlice(slice, false);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	// saa alkaa samana päivänä
		slice = new TimeSlice(new Date(2004, 0, 1), new Date(2004, 0, 2));    	
		session.setTimeScaleSlice(slice, true);
		assertEquals(slice, session.getTimeScaleSlice());
		session.setTimeScaleSlice(slice, false);
		assertEquals(slice, session.getTimeScaleSlice());
    	
    	// ei saa loppua kokonaisvälin jälkeen
		slice = new TimeSlice(new Date(2004, 11, 1), new Date(2005, 0, 1));
    	try {
    		session.setTimeScaleSlice(slice, true);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	try {
    		session.setTimeScaleSlice(slice, false);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	// saa loppua samana päivänä
		slice = new TimeSlice(new Date(2004, 11, 1), new Date(2004, 11, 31));    	
		session.setTimeScaleSlice(slice, true);
		assertEquals(slice, session.getTimeScaleSlice());
		session.setTimeScaleSlice(slice, false);
		assertEquals(slice, session.getTimeScaleSlice());
    	
		// laillisia asetuksia kummankin tyypin kanssa 2kpl
		assertEquals(2, this.notifications[NeroObserverTypes.TIMESCALESLICEUPDATING]);
		assertEquals(2, this.notifications[NeroObserverTypes.TIMESCALESLICE]);
    }

    public void testActiveRoom() {
    	session.registerObserver(NeroObserverTypes.ACTIVE_ROOM, this);
    	
    	session.setActiveRoom(this.room);
    	assertNotNull(session.getActiveRoom());
    	assertSame(this.room, session.getActiveRoom());
    	// saa antaa myös nullin
    	session.setActiveRoom(null);
    	assertNull(session.getActiveRoom());

    	assertEquals(2, this.notifications[NeroObserverTypes.ACTIVE_ROOM]);
    }
    
    public void testRegisterObserver() {
    	// saa kuunnella 0 ... NTYPES-1
    	try {
    		session.registerObserver(-1, this);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	try {
    		session.registerObserver(NeroObserverTypes.NTYPES, this);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	// rekisteröidytään kaikille laillisille
        for(int i = 0; i < NeroObserverTypes.NTYPES; i++) {
        	// observer ei saa olla null, tietenkään
        	try {
        		session.registerObserver(i, null);
        		Assert.fail("Should throw IllegalArgumentException");
        	} catch(IllegalArgumentException e) {
        		// success
        	}
        	session.registerObserver(i, this);
        }
    	// ei tapaa päästä käsiksi sessionin privaattiin observermanageriin,
        // mutta sille on kyllä omat testinsä.
    }
    
    public void testStatusMessage() {
    	String test = "TestSession.testStatusMessage()";
    	session.registerObserver(NeroObserverTypes.STATUSBAR, this);
    	// pitäisi saada stringi, vaikkei mitään ole tehty
    	assertNotNull(session.getStatusMessage());
    	session.setStatusMessage(test);
    	assertNotNull(session.getStatusMessage());
    	assertEquals(test, session.getStatusMessage());
    	
    	// asetettu kerran
    	assertEquals(1, this.notifications[NeroObserverTypes.STATUSBAR]);
    }
    
    public void testWaitState() {
    	session.registerObserver(NeroObserverTypes.CURSORCHANGE, this);
    	
    	session.waitState(true);
    	assertEquals(java.awt.Cursor.WAIT_CURSOR, session.getCursorType());
    	assertEquals(1, this.notifications[NeroObserverTypes.CURSORCHANGE]);
    	
    	session.waitState(false);
    	assertEquals(java.awt.Cursor.DEFAULT_CURSOR, session.getCursorType());
    	assertEquals(2, this.notifications[NeroObserverTypes.CURSORCHANGE]);
    }
    
    /* ei db:tä, ei testejä näille. pitäisi joka tapauksessa tehdä dbunitin kanssa
    public void testGetProjects() {
    	assertNotNull(session.getProjects());
    }
    
    public void testGetRooms() {
    	assertNotNull(session.getRooms());
    }
    
    public void testGetFilteredRooms() {
    	assertNotNull(session.getFilteredRooms());
    }
    
    public void testGetProjectRooms() {
    	assertNotNull(session.getProjectRooms());
    }
    
    public void testGetFilteredPeople() {
    	assertNotNull(session.getFilteredPeople());
    }
    
    public void testGetPhoneNumbers() {
    	assertNotNull(session.getPhoneNumbers(post));
    }
    */
    
    /* lasketaan kuuntelut */
	public void updateObserved(int type) {
		notifications[type]++;		
	}
    
	// wanhat stattarit
    // method	32%  (16/50)
    // block	28%  (242/874)
    // line		34%  (74/220)
}
