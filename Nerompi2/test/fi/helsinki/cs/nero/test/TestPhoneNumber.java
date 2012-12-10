
package fi.helsinki.cs.nero.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import fi.helsinki.cs.nero.data.PhoneNumber;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.data.Room;
import fi.helsinki.cs.nero.logic.Session;
import java.util.Calendar;

/**
 * @author Jyrki Muukkonen
 *
 * Testiluokka PhoneNumber-luokalle
 */
public class TestPhoneNumber extends TestCase {
    
    /* tarvittavat muuttujat yms. */
    private Session session;
    private Room room;
    private Post post1;
    private Post post2;
    private PhoneNumber number1;
    private PhoneNumber number2;
    private PhoneNumber numberWithoutPost;
    
    /**
     * Konstruktorin tulee välittää String-tyyppinen parametrinsa ylöspäin.
     * @param arg
     */
    public TestPhoneNumber(String arg) {
        super(arg);
    }

    /**
     * Alustaa tarvittavat tiedot.
     */
    protected void setUp() {
        session = new Session();
        room = null;
        post1 = new Post(session, "p1", room, 1, Calendar.getInstance().getTime());
        post2 = new Post(session, "p2", room, 2, Calendar.getInstance().getTime());
        number1 = new PhoneNumber(session, "1", post1, "911");
        number2 = new PhoneNumber(session, "2", post1, "112");
        numberWithoutPost = new PhoneNumber(session, "3", null, "118");
    }

    /**
     * Jos tarvitsee tehdä jotain puhdistelua testien jälkeen.
     */
    protected void tearDown() {
        // täällä tapetaan kaikki mikä ei itsekseen kuole.
        // esim jos session tappaminen vaatii jotain spesiaalia, kuten
        // session.kill(); session.whyDontYouJustDieDieDie();
    }
    
    /**
     * Ja vihdoin ihka ensimmäinen test case. Tartteis kommentoida paremmin.
     */
    public void testPhoneNumberClone() {
        String oldnumber = number1.getPhoneNumber();
        String oldid = number1.getPhoneNumberID();
        Post oldpost = number1.getPost();
        /* Kopioi muuten, mutta anna eri Post */
        PhoneNumber clone = new PhoneNumber(number1, post2);
        /* Numeron ja numeron ID:n pitäisi olla sama (ID:nkin? ehkäpä) */
        assertEquals(oldnumber, clone.getPhoneNumber());
        assertEquals(oldid, clone.getPhoneNumberID());
        /* Postin pitäisi olla eri */
        assertNotSame(oldpost, clone.getPost());
    }
    
    public void testPhoneNumberExceptions() {
    	PhoneNumber p1 = null;
    	try {
    		p1 = new PhoneNumber(null, "3", post1, "555-333");
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	try {
    		p1 = new PhoneNumber(this.session, null, post1, "555-333");
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    }
    
    public void testToString() {
    	/* puhelinnumero jolla Post */
    	assertNotNull(number1.toString());
    	/* puhelinnumero jolla ei Postia */
    	assertNotNull(numberWithoutPost.toString());
    }
    
    public void testCompareTo() {
    	try {
    		int i = number1.compareTo(new String("fail"));
    		Assert.fail("Should throw something");
    	} catch(ClassCastException e) {
    		// success
    	}
    	// "suurempi"
    	assertTrue(number1.compareTo(number2) > 0);
       	// "pienempi"
    	assertTrue(number2.compareTo(numberWithoutPost) < 0);
    	// sama
    	assertEquals(0, number1.compareTo(number1));
    }
}

