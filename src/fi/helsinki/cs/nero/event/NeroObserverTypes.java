package fi.helsinki.cs.nero.event;

/**
 * Staattinen luettelo Neron tapahtumatyypeist�.
 *
 * @author Osma Suominen
 */

public class NeroObserverTypes {

	/**
	 * Ilmoittaa, ett� j�rjestelm�n tuntemat huoneet tai niihin liittyv�t tiedot
	 * (esim. ty�pisteet tai ty�pisteiden puhelinnumerot) ovat muuttuneet.
	 */
	
	public static final int ROOMS = 0;
	
    /**
     * Ilmoittaa, ett� aktiivinen huone tai siihen liittyv�t tiedot
     * (esim. ty�pisteet) ovat muuttuneet.
     */

    public static final int ACTIVE_ROOM = 1;

    /**
     * Ilmoittaa, ett� projekti-hakuehto on muuttunut.
     */

    public static final int FILTER_PROJECT = 2;

    /**
     * Ilmoittaa, ett� hakuehtojen rajaama henkil�lista on muuttunut.
     */

    public static final int FILTER_PEOPLE = 3;

    /**
     * Ilmoittaa, ett� hakuehtojen rajaama huonelista on muuttunut.
     */
    public static final int FILTER_ROOMS = 4;

    /**
     * Ilmoittaa, ett� hakuehtojen aikav�li on muuttunut.
     */

    public static final int TIMESCALE = 5;

    /**
     * Ilmoittaa, ett� tarkasteltava osa-aikav�li on muuttunut.
     */

    public static final int TIMESCALESLICE = 6;

    /**
     * Ilmoittaa, ett� ty�pisteisiin liittyv�t varaukset ovat muuttuneet.
     */
    public static final int RESERVATIONS = 7;
    
    /**
     * Ilmoittaa, ett� uusi viesti on saapunut viestipulikkaan.
     */
    public static final int STATUSBAR = 8;

    /**
     * Ilmoittaa, ett� slideria heilutellaan muttei ehk� p��stetty irti
     */
    public static final int TIMESCALESLICEUPDATING  = 9;
        
    /**
     * Ilmoittaa, ett� hiiren kursoria halutaan vaihtaa.
     * Uuden kursorin tyyppi voidaan hakea Session.getCursorType():ll�
     */
    public static final int CURSORCHANGE = 10;
    /**
     * Tapahtumatyyppien kokonaism��r�.
     */
    public static final int NTYPES = 11;
}
