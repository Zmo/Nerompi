package fi.helsinki.cs.nero.test;



import java.util.Date;

import junit.framework.Assert;
import junit.framework.TestCase;
import fi.helsinki.cs.nero.data.Contract;
import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Project;
import fi.helsinki.cs.nero.data.TimeSlice;
import fi.helsinki.cs.nero.logic.Session;

/**
 * @author Jyrki Muukkonen
 *
 * Testiluokka Contract-luokalle
 */
public class TestContract extends TestCase {
    
//    
//    /* tarvittavat muuttujat yms. */
//	private Contract contract1, contract2, contract3;
//	
//	private String contractID;
//
//	private Project project1, project2, project3;
//	private Person person1, person2, person3;
//	private TimeSlice timeSlice1, timeSlice2, timeSlice3;
//
//	private Session session;
//	
//	private String title;
//    
//    /**
//     * Konstruktorin tulee välittää String-tyyppinen parametrinsa ylöspäin.
//     * @param arg
//     */
//    public TestContract(String arg) {
//        super(arg);
//    }
//
//    /**
//     * Alustaa tarvittavat tiedot.
//     */
//    protected void setUp() {
//    	session = new Session();
//    	contractID = "Testcontract1";    	
//    	project1 = null;
//    	project2 = null;
//    	project3 = new Project(session, "prjktid", null, null, null);
//    	person1 = null;
//    	person2 = null;
//    	person3 = new Person(session, "007", "Bond, James", null, null);
//    	title = "nimike";
//    	timeSlice1 = new TimeSlice(new Date(2004, 0, 1), new Date(2005, 0, 1));
//    	timeSlice2 = new TimeSlice(new Date(2004, 0, 1), new Date(2005, 0, 1));
//		timeSlice3 = new TimeSlice(new Date(2003, 0, 1), new Date(2004, 11, 31));
//    	
//    	contract1 = new Contract(session, contractID, project1, person1, title, 100, timeSlice1);
//    	contract2 = new Contract(session, contractID, project2, person2, title, 100, timeSlice2);
//    	contract3 = new Contract(session, contractID, project3, person3, title, 100, timeSlice3);
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
//    public void testContract() {
//    	Contract temp;
//    	// sessio ei saa olla null
//    	try {
//    		temp = new Contract(null, contractID, project3, person3, title, 100, timeSlice3);
//    		Assert.fail("Should throw IllegalArgumentException");
//    	} catch(IllegalArgumentException e) {
//        	//success    		
//    	}
//    	// contractID ei saa olla null
//    	try {
//    		temp = new Contract(session, null, project3, person3, title, 100, timeSlice3);
//    		Assert.fail("Should throw IllegalArgumentException");
//    	} catch(IllegalArgumentException e) {
//        	//success    		
//    	}
//    	// muut saa olla? miksihän...
//    	temp = new Contract(session, contractID, null, null, null, -666, null); 	
//    }
//    
//    /* Testataan metodien palautusarvot nullien varalta */
//    
//    /**
//     * Näistä minkään ei tulisi palauttaa null. 
//     */
//    public void testGetContractID() {
//        assertNotNull(contract1.getContractID());
//        assertEquals(contractID, contract1.getContractID());
//    }
//    
//    public void testGetTimeSlice() {
//    	assertNotNull(contract1.getTimeSlice());
//    	assertEquals(timeSlice1, contract1.getTimeSlice());
//    }
//    
//    public void testWorkingPercentage() {
//    	int perc = contract1.getWorkingPercentage();
//    	assertTrue(perc >= 0);
//    	assertTrue(perc <= 100);
//    	assertTrue(perc == 100);
//    }
//    
//    public void testGetTitle() {
//    	assertNotNull(contract1.getTitle());
//    	assertEquals(title, contract1.getTitle());
//    }
//    
//    // käyttää suoraan TimeSlicen compareTo, joka on hieman paremmin testattu
//    public void testCompareTo() {
//    	assertTrue(contract1.compareTo(contract2) == 0);
//    	assertTrue(contract1.compareTo(contract3) > 0);
//    	assertTrue(contract3.compareTo(contract1) < 0);
//    }
//    
//    public void testCompareStartDates() {
//    	assertTrue(contract1.compareStartDates(contract2) == 0);
//    	assertTrue(contract1.compareStartDates(contract3) > 0);
//    	assertTrue(contract3.compareStartDates(contract1) < 0);
//    }
//    
//    public void testGetPerson() {
//    	assertNull(contract1.getPerson());
//    	assertNull(contract2.getPerson());
//    	assertNotNull(contract3.getPerson());
//    	assertEquals(person3, contract3.getPerson());
//    }
//    
//    public void testGetProject() {
//    	assertNull(contract1.getProject());
//    	assertNull(contract2.getProject());
//    	assertNotNull(contract3.getProject());
//    	assertEquals(project3, contract3.getProject());    	
//    }
//    
//    public void testToString() {
//    	// ilman projektia
//    	assertNotNull(contract1.toString());
//    	// projektin kanssa
//    	assertNotNull(contract3.toString());
//    }
//
    
}
