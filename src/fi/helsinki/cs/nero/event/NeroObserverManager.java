package fi.helsinki.cs.nero.event;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Kuuntelijoiden hallinta
 * 
 * @author Osma Suominen
 */

public class NeroObserverManager {
    private LinkedList[] observers;

    public NeroObserverManager() {
        int n = NeroObserverTypes.NTYPES;
        observers = new LinkedList[n];
        for(int i=0; i<n; ++i)
            observers[i] = new LinkedList();
    }

    /**
     * Lisää annetun kuuntelijan kuuntelemaan tiettyä tapahtumatyyppiä.
     *
     * @param type tapahtumatyyppi
     * @param observer kuuntelijarajapinnan toteuttava olio
     */

    public void addObserver(int type, NeroObserver observer) {
    	if(type < 0 || type >= NeroObserverTypes.NTYPES)
    		throw new IllegalArgumentException();

    	observers[type].add(observer);
    }

    /**
     * Tiedottaa tietyn tapahtumatyypin kuuntelijoille tapahtumasta.
     *
     * @param type tapahtumatyyppi
     */

    public void notifyObservers(int type) {
    	if(type < 0 || type >= NeroObserverTypes.NTYPES)
    		throw new IllegalArgumentException();
    	
        ListIterator iter = observers[type].listIterator(0);
        while(iter.hasNext()) {
            NeroObserver obs = (NeroObserver) iter.next();
            obs.updateObserved(type);
        }
    }

}
