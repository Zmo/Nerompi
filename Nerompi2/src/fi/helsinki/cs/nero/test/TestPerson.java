package fi.helsinki.cs.nero.test;


import java.util.Date;

import junit.framework.Assert;
import junit.framework.TestCase;
import fi.helsinki.cs.nero.data.Contract;
import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.data.TimeSlice;
import fi.helsinki.cs.nero.logic.Session;

/**
 * @author Jyrki Muukkonen
 *
 * Testiluokka Person-luokalle
 */
public class TestPerson extends TestCase {
//    
//    /* tarvittavat muuttujat yms. */
//	private Session session;
//
//	private Person person1, person2;
//	private String id1, id2;
//	private String name1, name2;			
//	private Contract[] cntr1, cntr2;
//	private Reservation[] rsrv1, rsrv2;
//    
//    /**
//     * Konstruktorin tulee välittää String-tyyppinen parametrinsa ylöspäin.
//     * @param arg
//     */
//    public TestPerson(String arg) {
//        super(arg);
//    }
//
//    /**
//     * Alustaa tarvittavat tiedot.
//     */
//    protected void setUp() {
//        session = new Session();
//    	id1 = "006";
//    	id2 = "007";
//    	name1 = "Trevelyan, Alec";
//    	name2 = "Bond, James";
//        cntr1 = new Contract[0];
//        cntr2 = null;
//        rsrv1 = new Reservation[0];
//        rsrv2 = null;
//        person1 = new Person(session, id1, name1, cntr1, rsrv1);
//        person2 = new Person(session, id2, name2, cntr2, rsrv2);
//    }
//
//    /**
//     * Jos tarvitsee tehdä jotain puhdistelua testien jälkeen.
//     */
//    protected void tearDown() {
//        // täällä tapetaan kaikki mikä ei itsekseen kuole.
//        // esim jos session tappaminen vaatii jotain spesiaalia, kuten
//        // session.kill(); session.whyDontYouJustDieDieDie();
//    }
//    
//
//    
//    public void testPerson(){
//    	// session ei saa olla null
//    	try {
//    		person1 = new Person(null, id1, name1, cntr1, rsrv1); 
//    		Assert.fail("Should throw IllegalArgumentException");
//    	} catch(IllegalArgumentException e){
//    		// success
//    	}
//    	// person id ei saa olla null
//    	try {
//    		person1 = new Person(session, null, name1, cntr1, rsrv1); 
//    		Assert.fail("Should throw IllegalArgumentException");
//    	} catch(IllegalArgumentException e){
//    		// success
//    	}
//    	// nimi ei saa olla null (entä tyhjä?)
//    	try {
//    		person1 = new Person(session, id1, null, cntr1, rsrv1); 
//    		Assert.fail("Should throw IllegalArgumentException");
//    	} catch(IllegalArgumentException e){
//    		// success
//    	}
//    }
//    
//    /* Testataan metodien palautusarvot nullien varalta */
//    
//    /**
//     * Näistä minkään ei tulisi palauttaa null. 
//     */
//    public void testGetPersonID() {
//
//        assertNotNull(person1.getPersonID());
//    }
//    
//    public void testGetName() {
//    	
//        assertNotNull(person1.getName());
//        
//    }
//     
//    public void testGetContracts() {
//    	
//    	assertNotNull(person1.getContracts());
//        
//    }
//    
//    public void testGetReservations() {
//    	assertNotNull(person1.getReservations());
//    	assertEquals(rsrv1, person1.getReservations());
//    }
//    
//    public void testToString() {
//    	assertNotNull(person1.toString());
//    	assertNotNull(person2.toString());
//    }
//    
//    public void testCompareTo() {
//    	// vertaa nimen mukaan, joten Bond tulee ennen Travelyania
//    	assertTrue(person2.compareTo(person1) < 0);
//    	assertTrue(person1.compareTo(person2) > 0);
//    	assertTrue(person1.compareTo(person1) == 0);
//    	assertTrue(person2.compareTo(person2) == 0);
//    	try {
//    		int i = person1.compareTo(new String("bogus"));
//    		Assert.fail("Should throw something");
//    	} catch(Exception e) {
//    		// success
//    	}
//    	
//    }
//    
//    /* tämä jos mikä on paha... */
//    public void testGetStatus() {
//    	// NOTE true tarkoittaa että _EI OLE TYYTYVÄINEN_
//    	
//    	TimeSlice jan04 = new TimeSlice(new Date(2004, 0, 1), new Date(2004, 0, 31));
// 
//    	// helpoin tapaus, ei sopimuksia. on siis tyytyväinen :)   	
//    	assertFalse(person1.getStatus());
//
//    	session.setFilterTimescale(jan04);
//    	/* ei voi tehdä ilman db:tä, sori
//    	Assert.fail("Ei tehty, eikä tehdä.");
//    	 */
//    }
//    
}
