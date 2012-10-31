package fi.helsinki.cs.nero.test;



import java.util.Arrays;

import junit.framework.Assert;
import junit.framework.TestCase;
import fi.helsinki.cs.nero.data.PhoneNumber;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.data.Room;
import fi.helsinki.cs.nero.logic.Session;

/**
 * @author Jyrki Muukkonen
 *
 * Testiluokka room-luokalle
 */
public class TestPost extends TestCase {
    
    /* tarvittavat muuttujat yms. */
	private Session session;
	private Room room;
	private String id1, id2;
	private int pnumber1, pnumber2;
	private Post post1, post2;
	private Post[] postarray;
    
    /**
     * Konstruktorin tulee välittää String-tyyppinen parametrinsa ylöspäin.
     * @param arg
     */
    public TestPost(String arg) {
        super(arg);
    }

    /**
     * Alustaa tarvittavat tiedot.
     */
    protected void setUp() {        	
    	session = new Session();
    	room = new Room(session, "roomid", null, null, null, null, 42.0, null);
    	id1 = "post001";
    	id2 = null;
    	pnumber1 = 1;
    	pnumber2 = 2;
    	post1 = new Post(session, id1, room, pnumber1);
    	post2 = new Post(session, id2, room, pnumber2);
    	postarray = new Post[]{post1, post2};
    	room.setPosts(postarray);
    }

    /**
     * Jos tarvitsee tehdä jotain puhdistelua testien jälkeen.
     */
    protected void tearDown() {
        // täällä tapetaan kaikki mikä ei itsekseen kuole.
        // esim jos session tappaminen vaatii jotain spesiaalia, kuten
        // session.kill(); session.whyDontYouJustDieDieDie();
    }
    
    public void testPost() {
    	// sessio ei saa olla null
    	try {
    		post1 = new Post(null, id1, room, pnumber1);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// succcess
    	}
    }
    
    public void testGetPostID() {
    	// post id ei ole null
    	assertNotNull(post1.getPostID());
    	assertEquals(id1, post1.getPostID());
    	// post id on null
    	assertNull(post2.getPostID());
    }
    
    public void testGetRoom() {
    	assertNotNull(post1.getRoom());
    	assertSame(room, post1.getRoom());
    	assertNotNull(post2.getRoom());
    	assertSame(room, post2.getRoom());
    	assertSame(post1.getRoom(), post2.getRoom());
    }
    
    public void testGetPostNumber() {
    	assertEquals(pnumber1, post1.getPostNumber());
    	assertEquals(pnumber2, post2.getPostNumber());
    }
    
    public void testToString() {
    	assertNotNull(post1.toString());
    	assertNotNull(post2.toString());
    }
    
    public void testSetPhoneNumbers() {
    	PhoneNumber[] pns = new PhoneNumber[0];
    	// ei saa antaa nullia
    	try {
    		post1.setPhoneNumbers(null);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	// saa antaa taulukon (joka nyt vaan sattuu olemaan tyhjä)
    	post1.setPhoneNumbers(pns);
    	// ei saa antaa kuin kerran
    	try {
    		post1.setPhoneNumbers(pns);
    		Assert.fail("Should throw IllegalStateException");
    	} catch(IllegalStateException e) {
    		// success
    	}
    }
    
    public void testGetPhoneNumbers() {
    	PhoneNumber[] pns = null;
    	// ei saa ottaa jos ei anna ensin...
    	try {
    		pns = post1.getPhonenumbers();
    		Assert.fail("Should throw IllegalStateException");
    	} catch(IllegalStateException e) {
    		// success
    	}
    	PhoneNumber pn = new PhoneNumber(session, "id1", post1, "0700-123123");
    	PhoneNumber[] pns_ret = null;
    	pns = new PhoneNumber[1];
    	pns[0] = pn;
    	post1.setPhoneNumbers(pns);
    	pns_ret = post1.getPhonenumbers();
    	assertEquals(pns.length, pns_ret.length);
		Arrays.sort(pns);
		Arrays.sort(pns_ret);
		assertTrue(Arrays.equals(pns, pns_ret));
    }
}