package fi.helsinki.cs.nero.test;



import java.util.Date;

import junit.framework.Assert;
import junit.framework.TestCase;
import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.data.TimeSlice;
import fi.helsinki.cs.nero.logic.Session;
import java.util.Calendar;

/**
 * @author Jyrki Muukkonen
 *
 * Testiluokka Reservation-luokalle
 */
public class TestReservation extends TestCase {
	
	private Reservation reservation1, reservation2;
    
	/**Varauksen tunnus*/
	private String reservationID;
	
	/**Työpiste, johon varaus tehdään*/
	private Post targetPost;

	/**Henkilö, jolle varataan*/
	private Person reservingPerson;
	
	/**Varauksen aikajakso*/
	private TimeSlice timeSlice;
	
	/**Varauksen viikkotunnit*/
	private double weeklyHours, weeklyHoursNegative;
		
	/**Varauksen selite*/
	private String description;

	private Session session;
    
    /**
     * Konstruktorin tulee välittää String-tyyppinen parametrinsa ylöspäin.
     * @param arg
     */
    public TestReservation(String arg) {
        super(arg);
    }


    /**
     * Alustaa tarvittavat tiedot.
     */
    protected void setUp() {
    	session = new Session();
    	reservationID = "Testreservation1";
    	targetPost = new Post(session, "p1", null, 1, Calendar.getInstance().getTime());
    	reservingPerson = new Person(session, "person_id", "Testaaja Teuvo", null, null);
    	weeklyHours = 0.0;
    	weeklyHoursNegative = -1.0;
    	description = "kuvaus";

    	timeSlice = new TimeSlice(new Date(), new Date());
    	
    	reservation1 = new Reservation(session, reservationID, targetPost, reservingPerson, timeSlice, weeklyHours, description);
    	reservation2 = new Reservation(session, reservationID, targetPost, reservingPerson, timeSlice, weeklyHours, description);
    }

    /**
     * Jos tarvitsee tehdä jotain puhdistelua testien jälkeen.
     */
    protected void tearDown() {
        // täällä tapetaan kaikki mikä ei itsekseen kuole.
        // esim jos session tappaminen vaatii jotain spesiaalia, kuten
        // session.kill(); session.whyDontYouJustDieDieDie();
    }
    
    
    /* Testataan metodien palautusarvot nullien varalta */
    
    /**
     * Näistä minkään ei tulisi palauttaa null. 
     */
    public void testGetReservationID() {
        assertNotNull(reservation1.getReservationID());
        assertEquals(this.reservationID, reservation1.getReservationID());
    }
    
    public void testGetTimeSlice() {
    	assertNotNull(reservation1.getTimeSlice());
    	assertEquals(this.timeSlice, reservation1.getTimeSlice());
    }
    
    
    public void testGetReservingPerson() {
    	assertNotNull(reservation1.getReservingPerson());
    	assertEquals(this.reservingPerson, reservation1.getReservingPerson());
    }

    
    public void testGetTargetPost() {
    	assertNotNull(reservation1.getTargetPost());
    	assertEquals(this.targetPost, reservation1.getTargetPost());        
    }
    
    /**
     * Muut palautusarvot
     *
     */
    
    public void testGetWeeklyHours() {
    	assertEquals(0.0,reservation1.getWeeklyHours(),0);
    	Reservation reservation3 = new Reservation(session, reservationID, targetPost, reservingPerson, timeSlice, weeklyHoursNegative, description);
    	assertEquals(0.0, reservation3.getWeeklyHours(),0);
    }
    
    
    /**
     * Testataan vertailu. Vertailun pitäisi palauttaa 0, eli kyseessä sama aikajakso 
     * 
     */
    public void testCompareTo(){
    	assertEquals(0, reservation1.compareTo(reservation2));
    	assertEquals(0, reservation2.compareTo(reservation1));
    }
    
    public void testClone() {
    	Reservation clone;
    	TimeSlice tsdiff = new TimeSlice(new Date(2004, 0, 1), new Date(2004, 11, 31));
    	clone = new Reservation(reservation1, tsdiff);
    	assertEquals(clone.getDescription(), reservation1.getDescription());
    	assertEquals(clone.getReservationID(), reservation1.getReservationID());
    	// assertEquals(float, float, delta)
    	assertEquals(clone.getWeeklyHours(), reservation1.getWeeklyHours(), 0);
    	// viitteet samoihin oliohin
    	assertSame(clone.getReservingPerson(), reservation1.getReservingPerson());
    	assertSame(clone.getSession(), reservation1.getSession());
    	assertSame(clone.getTargetPost(), reservation1.getTargetPost());
    	// viite eri timeslice-olioon
    	assertNotSame(clone.getTimeSlice(), reservation1.getTimeSlice());	
    }

    public void testToString() {
    	assertNotNull(reservation1.toString());
    }
    
    public void testReservationException() {
    	Reservation r = null;
    	// sessio ei saa olla null
    	try {
    		r = new Reservation(null, reservationID, targetPost,
    				reservingPerson, timeSlice, weeklyHours, description);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    }
    
}
