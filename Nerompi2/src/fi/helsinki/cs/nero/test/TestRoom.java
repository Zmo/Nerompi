package fi.helsinki.cs.nero.test;



import java.util.Arrays;

import junit.framework.Assert;
import junit.framework.TestCase;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.data.Room;
import fi.helsinki.cs.nero.logic.Session;

/**
 * @author Jyrki Muukkonen
 *
 * Testiluokka room-luokalle
 */
public class TestRoom extends TestCase {
    
    /* tarvittavat muuttujat yms. */
	private Room room1, room2;
	private String id1, id2;
	private String building;
	private String floor;
	private String name1, name2;
	private double size1, size2;
	private String number1, number2;
	private String description1, description2;
	
	private Session session;
    
    /**
     * Konstruktorin tulee välittää String-tyyppinen parametrinsa ylöspäin.
     * @param arg
     */
    public TestRoom(String arg) {
        super(arg);
    }

    /**
     * Alustaa tarvittavat tiedot.
     */
    protected void setUp() {        	
    	session = new Session();
    	//session.setFilterFreePosts(10);
    	id1 = "1";
    	id2 = "2";
    	building = "Inpracticum";
    	floor = "K3";
    	number1 = "666";
    	number2 = null;
    	name1 = "Sauna";
    	name2 = "Siivouskomero";
    	size1 = 66.6;
    	size2 = 1.8;
    	description1 = "Hotter than hell";
    	description2 = null;
    	
    	room1 = new Room(session, id1, building, floor, number1, name1, size1, description1);
    	room2 = new Room(session, id2, building, floor, number2, name2, size2, description2);  	
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
    public void testGetRoomID() {
        assertNotNull(room1.getRoomID());
        assertNotNull(room2.getRoomID());
        assertEquals(this.id1, room1.getRoomID());
        assertEquals(this.id2, room2.getRoomID());
    }
    
    public void testGetBuildingName() {
    	assertEquals(this.building, room1.getBuildingName());
    }
    
    public void testGetFloor() {
    	assertEquals(this.floor, room1.getFloor());
    }
    
    public void testGetRoomNumber() {
    	assertEquals(this.number1, room1.getRoomNumber());
    }
    
    public void testGetRoomName() {
    	assertEquals(this.name1, room1.getRoomName());
    }
    
    public void testGetRoomSize() {
    	assertEquals(this.size1, room1.getRoomSize(), 0);
    }
    
    public void testGetDescription() {
    	// annettu ehjä description
    	assertNotNull(room1.getDescription());
    	assertEquals(this.description1, room1.getDescription());
    	// annettu null, pitäisi silti tulla string
    	assertNotNull(room2.getDescription());
    }
    
    public void testToString() {
    	assertNotNull(room1.toString());
    	assertNotNull(room2.toString());
    }
    
    public void testSetGetPosts() {
    	Post[] posts1 = null;
    	Post[] posts2 = null;
    	/* yritetään asettaa posteiksi null */
    	try {
    		room1.setPosts(null);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalStateException e) {
    		Assert.fail("Should not throw IllegalStateException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	/* työpisteitä ei asetettu, pitäisi lentää jotakin */
    	try {
    		posts1 = room1.getPosts();
    		Assert.fail("Should throw IllegalStateException");
    	} catch(IllegalStateException e) {
    		// success
    	}
    	/* asetetaan (tyhjä) työpistelista ja haetaan se */
    	posts1 = new Post[0];
    	try {
    		room1.setPosts(posts1);
    		posts2 = room1.getPosts();
    	} catch(Exception e) {
    		Assert.fail("Should not throw Exception");
    	}
    	/* toisen setPosts-kutsun pitäisi heittää poikkeus */
    	try {
    		room1.setPosts(posts1);
    		Assert.fail("Should throw IllegalStateException");
    	} catch(IllegalStateException e) {
    		// success
    	}
		/* pitäisi sisältää samat työpisteet. sortataan ja verrataan */
		Arrays.sort(posts1);
		Arrays.sort(posts2);
		assertTrue(Arrays.equals(posts1, posts2));
    	
    }

    public void testRoom() {
    	// sessio ei saa olla null
    	try {
    		room1 = new Room(null, id1, building, floor, number1, name1, size1, description1);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	// room id ei saa olla null
    	try {
    		room1 = new Room(session, null, building, floor, number1, name1, size1, description1);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}    
    	// muut saa olla nulleja?
    	room1 = new Room(session, id1, null, null, null, null, -666, null);    
    }
    
    public void testGetStatus() {
    	Post[] p1 = new Post[0];
    	Post[] p2 = new Post[1];
    	p2[0] = new Post(session, "p001", null, 1);
    	
    	room1.setPosts(p1);
    	room2.setPosts(p2);
    	assertEquals(room1.getStatus(), Room.NO_POSTS);
    	// heittää NullPointerException, koska tietokantayhteyttä ei ole. eikä tule.
    	// kuuluisi DbUnit-testeihin, joita ei ole...
    	//assertEquals(room2.getStatus(), Post.FREE);
    }
}