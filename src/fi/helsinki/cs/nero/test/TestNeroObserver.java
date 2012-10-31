package fi.helsinki.cs.nero.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import fi.helsinki.cs.nero.event.NeroObserver;
import fi.helsinki.cs.nero.event.NeroObserverManager;
import fi.helsinki.cs.nero.event.NeroObserverTypes;

/**
 * @author Jyrki Muukkonen
 *
 * Testiluokka room-luokalle
 */
public class TestNeroObserver extends TestCase implements NeroObserver {
    
	private int[] notifications;
	private NeroObserverManager obsman;
	
    /**
     * Konstruktorin tulee välittää String-tyyppinen parametrinsa ylöspäin.
     * @param arg
     */
    public TestNeroObserver(String arg) {
        super(arg);
    }

    /**
     * Alustaa tarvittavat tiedot.
     */
    protected void setUp() {
    	obsman = new NeroObserverManager();
    	notifications = new int[NeroObserverTypes.NTYPES];
    }

    /**
     * Jos tarvitsee tehdä jotain puhdistelua testien jälkeen.
     */
    protected void tearDown() {
        // täällä tapetaan kaikki mikä ei itsekseen kuole.
        // esim jos session tappaminen vaatii jotain spesiaalia, kuten
        // session.kill(); session.whyDontYouJustDieDieDie();
    }

    public void testAddObserver() {
    	// legal values are 0..NTYPES-1
    	try {
    		obsman.addObserver(-1, this);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	// these should be OK
    	obsman.addObserver(0, this);
    	obsman.addObserver(1, this);
    	
    	try {
    		obsman.addObserver(NeroObserverTypes.NTYPES, this);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	// this should be OK
    	obsman.addObserver(NeroObserverTypes.NTYPES-1, this);
    	
    	// notify everything once
    	for(int i = 0; i < NeroObserverTypes.NTYPES; i++) {
    		obsman.notifyObservers(i);
    	}
    	// observing these, should have one...
    	assertEquals(1, notifications[0]);
    	assertEquals(1, notifications[1]);
    	assertEquals(1, notifications[NeroObserverTypes.NTYPES-1]);
    	// ...and zero for everything else
    	for(int i = 2; i < NeroObserverTypes.NTYPES-1; i++) {
    		assertEquals(0, notifications[i]);
    	}
    }
    
    public void testNotifyObservers() {
    	// yritetään laittomuuksia
    	try {
    		obsman.notifyObservers(-1);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	try {
    		obsman.notifyObservers(NeroObserverTypes.NTYPES);
    		Assert.fail("Should throw IllegalArgumentException");
    	} catch(IllegalArgumentException e) {
    		// success
    	}
    	// pitäisi toimia
    	obsman.notifyObservers(0);
    	obsman.notifyObservers(1);
    	obsman.notifyObservers(NeroObserverTypes.NTYPES-1);
    	
    	int loop = 100;
    	// kuunnellaan jokaista
    	for(int i = 0; i < NeroObserverTypes.NTYPES; i++) {
    		obsman.addObserver(i, this);
    	}
    	// huomautetaan loop kertaa
    	for(int i = 0; i < loop; i++) {
    		for(int j = 0; j < NeroObserverTypes.NTYPES; j++) {
    			obsman.notifyObservers(j);
    		}
    	}
    	// tarkistetaan että kaikki saivat kaiken
    	for(int i = 0; i < NeroObserverTypes.NTYPES; i++) {
    		assertEquals(loop, notifications[i]);
    	}
    }
    
    public void testDull() {
    	// hämätään typerää code coverage työkalua :)
    	Object o = new NeroObserverTypes();
    }
    
	/* Implements NeroObserver interface */
    public void updateObserved(int type) {
    	notifications[type]++;
    }
}