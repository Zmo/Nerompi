package fi.helsinki.cs.nero.test;


import java.util.Date;

import junit.framework.Assert;
import junit.framework.TestCase;
import fi.helsinki.cs.nero.data.Contract;
import fi.helsinki.cs.nero.data.Project;
import fi.helsinki.cs.nero.data.TimeSlice;
import fi.helsinki.cs.nero.logic.Session;

/**
 * @author Jyrki Muukkonen
 *
 * Testiluokka Project-luokalle
 */
public class TestProject extends TestCase {
    
    /* tarvittavat muuttujat yms. */
	private Project project1, project2, project3;
	
	private String projectID;

	private String projectManager;
	
	private Contract[] contracts;

	private TimeSlice timeSlice;

	private Session session;
    
    /**
     * Konstruktorin tulee v‰litt‰‰ String-tyyppinen parametrinsa ylˆsp‰in.
     * @param arg
     */
    public TestProject(String arg) {
        super(arg);
    }


    /**
     * Alustaa tarvittavat tiedot.
     */
    protected void setUp() {
    	session = new Session();    	

    	projectID = "p1";
    	contracts = null;
    	timeSlice = new TimeSlice(new Date(), new Date());
    	
    	project1 = new Project(session, projectID, "projekti1", "manageri1", timeSlice);
    	project2 = new Project(session, "p2", "projekti2", "manageri2", timeSlice);
    	project3 = new Project(session, "p3", null, null, null);
    }

    /**
     * Jos tarvitsee tehd‰ jotain puhdistelua testien j‰lkeen.
     */
    protected void tearDown() {
        // t‰‰ll‰ tapetaan kaikki mik‰ ei itsekseen kuole.
        // esim jos session tappaminen vaatii jotain spesiaalia, kuten
        // session.kill(); session.whyDontYouJustDieDieDie();
    }
    
    
    /* Testataan metodien palautusarvot nullien varalta */
    
    /**
     * N‰ist‰ mink‰‰n ei tulisi palauttaa null. 
     */
    public void testGetProjectID() {
        assertNotNull(project1.getProjectID());
    }
    
    public void testGetTimeSlice() {
    	assertNotNull(project1.getTimeSlice());
    	assertEquals(this.timeSlice, project1.getTimeSlice());
    }
    
    public void testProject() {
    	Project ptemp = null;
    	// sessio ei saa olla null
    	try {
    		ptemp = new Project(null, "x", "x", "x", null);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	// project id ei saa olla null
    	try {
    		ptemp = new Project(this.session, null, "x", "x", null);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    }
    
    public void testCompareTo() {
    	try {
    		int i = project1.compareTo(new String("bogus"));
    		Assert.fail("Should throw ClassCastException");
    	} catch(ClassCastException e) {
    		// success
    	}
    	// "pienempi"
    	assertTrue(project1.compareTo(project2) < 0);
    	// "suurempi"
    	assertTrue(project2.compareTo(project1) > 0);
    	// sama
    	assertTrue(project1.compareTo(project1) == 0); 
    }
    
    public void testGetProjectManager() {
    	assertNotNull(project1.getProjectManager());
    	assertNotNull(project2.getProjectManager());
    	assertNotNull(project3.getProjectManager());
    }

    public void testGetProjectName() {
    	assertNotNull(project1.getProjectName());
    	assertNotNull(project2.getProjectName());
    	assertNotNull(project3.getProjectName());
    }
    
    public void testToString() {
    	assertNotNull(project1.toString());
    	assertNotNull(project2.toString());
    	assertNotNull(project3.toString());
    }
}