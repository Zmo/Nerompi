package fi.helsinki.cs.nero.test;

import java.util.Date;

import junit.framework.Assert;
import junit.framework.TestCase;
import fi.helsinki.cs.nero.data.TimeSlice;

/**
 * @author Jyrki Muukkonen
 *
 * Testiluokka TimeSlice-luokalle
 */
public class TestTimeSlice extends TestCase {
    
    /* tarvittavat muuttujat yms. */
	private Date thisDate, date1, date2, date3, date4, date5;
    private TimeSlice timeSlice1, timeSlice2, timeSlice3, timeSlice4, timeSlice5;
    private TimeSlice january04;
	
    /**
     * Konstruktorin tulee v‰litt‰‰ String-tyyppinen parametrinsa ylˆsp‰in.
     * @param arg
     */
    public TestTimeSlice(String arg) {
        super(arg);
    }


    /**
     * Alustaa tarvittavat tiedot.
     */
    protected void setUp() {
    	/* deprecated, pit‰isi korvata Calenderilla... */
    	// XXX Kuukaudet on 0-11 !!!
    	thisDate = new Date(2004,11,5);
    	date1 = new Date(2004,0,1);
    	date2 = new Date(2004,4,5);
    	date3 = new Date(2004,9,10);
    	date4 = new Date(2004,11,12);
    	date5 = new Date(2005,9,10);
    	
    	timeSlice1 = new TimeSlice(thisDate, thisDate);
    	timeSlice2 = new TimeSlice(date1, date2);
    	timeSlice3 = new TimeSlice(date2, date3);
    	timeSlice4 = new TimeSlice(date1, date3);
    	timeSlice5 = new TimeSlice(date4, date5);
    	january04 = new TimeSlice(new Date(2004, 0, 1), new Date(2004, 0, 31));
    }

    /**
     * Jos tarvitsee tehd‰ jotain puhdistelua testien j‰lkeen.
     */
    protected void tearDown() {
        // t‰‰ll‰ tapetaan kaikki mik‰ ei itsekseen kuole.
        // esim jos session tappaminen vaatii jotain spesiaalia, kuten
        // session.kill(); session.whyDontYouJustDieDieDie();
    }
   
    public void testTimeSlice(){
    	try {
    		timeSlice1 = new TimeSlice(null, thisDate);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}

    	try {
    		timeSlice2 = new TimeSlice(thisDate, null);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}    	
    }    
     
    /* Testataan metodien palautusarvot nullien varalta */
    
    /**
     * N‰ist‰ mink‰‰n ei tulisi palauttaa null. 
     */
    public void testGetStartDate() {
        assertNotNull(timeSlice1.getStartDate());
        assertEquals(thisDate, timeSlice1.getStartDate());
    }
    
    public void testGetEndDate() {
    	assertNotNull(timeSlice1.getEndDate());
    	assertEquals(thisDate, timeSlice1.getStartDate());       
    }
    
    public void testGetSQLStartDate() {
        assertNotNull(timeSlice1.getSQLStartDate());
    }
    
    public void testGetSQLEndDate() {
    	assertNotNull(timeSlice1.getSQLEndDate());
    }    
    
    public void testCompareTo(){
    	// samat v‰lit
    	assertTrue(timeSlice1.compareTo(timeSlice1) == 0);
    	// alkaa aikaisemmin
    	assertTrue(timeSlice2.compareTo(timeSlice3) < 0);
    	// alkaa myˆhemmin
    	assertTrue(timeSlice1.compareTo(timeSlice2) > 0);
    	// alkaa samaan aikaan, loppuu aikaisemmin
    	assertTrue(timeSlice2.compareTo(timeSlice4) < 0);
    	// alkaa samaan aikaan, loppuu myˆhemmin
    	assertTrue(timeSlice4.compareTo(timeSlice2) > 0);
    }
    
    public void testSetStartDate(){
    	
    	timeSlice2.setStartDate(thisDate);
    	
    	assertSame(thisDate, timeSlice2.getStartDate());
    }
    
    public void testSetEndDate(){
    	
    	timeSlice2.setEndDate(thisDate);
    	
    	assertSame(thisDate, timeSlice2.getEndDate());
    }    
    

    public void testLength(){

        // length() on m‰‰ritetty palauttamaan yhden v‰hemm‰n :P
    	assertEquals(31 - 1, january04.length());
    }
    
    public void testContains(){
    	
    	timeSlice1 = new TimeSlice(date1, date3);
    	
    	assertTrue(timeSlice1.contains(date2));
    	assertTrue(timeSlice1.contains(timeSlice1.getStartDate()));
    	assertTrue(timeSlice1.contains(timeSlice1.getEndDate()));
    	assertFalse(timeSlice1.contains(date4));
    }

    
    public void testOverlaps(){
    	
    	timeSlice2 = new TimeSlice(date2, date4);
    	
    	assertTrue(timeSlice1.overlaps(timeSlice2));
    	
    	timeSlice2 = new TimeSlice(date4, date5);
    	
    	assertFalse(timeSlice1.overlaps(timeSlice2));
    }
    
    public void testEquals(){
    	assertTrue(timeSlice1.equals(timeSlice1));
    	assertFalse(timeSlice1.equals(timeSlice2));
    }
    
    public void testToString() {
    	assertNotNull(timeSlice1.toString());
    }

    public void testDaysBetween() {
    	
    		// 10.10.2004 - 12.12.2004
    		// Lokakuussa 31 p‰iv‰‰, marraskuussa 30.
    		// (31-10)+30+12 = 63.
    	
   	    	assertEquals(63, timeSlice4.daysBetween(timeSlice5));
    }
    public void testStartDayAfter() {
    		
    		// 1.1.2004 - 5.5.2004
    		// Tammikuu 31, helmi 29, maalis 31, huhti 30
    		// (31-1)+29+31+30+5 = 125

    	// No katsotaanpa!
    	assertEquals(125, timeSlice3.startDayAfter(timeSlice2.getStartDate()));
    	
    	// Ja toisin p‰in pit‰isi tulla negatiivista.
    	assertEquals(-125, timeSlice2.startDayAfter(timeSlice3.getStartDate()));
    	
    	/*
    	date1 = new Date(2004,0,1);
    	date2 = new Date(2004,4,5);
    	date3 = new Date(2004,9,10);
    	*/
    }
    public void testCommonDays() {
    	
    		// timeSlice3 = 5.5.2004 - 10.10.2004
    		// timeSlice4 = 1.1.2004 - 10.10.2004
    		// yhteisi‰ p‰ivi‰ siis 5.5.-10.10.
    	
    		// touko 31, kes‰ 30, hein‰ 31, elo 31, syys 30, loka 31
    		// (31-5)+30+31+31+30+10 = 158
    	
    	
    	// Kuinka ollakaan, t‰m‰ testi menee komeasti l‰pi, vaikka testattava metodi
    	// onkin perustavanlaatuisesti rikki! :D
    	assertEquals(158, timeSlice3.commonDays(timeSlice4));
    }
    
}