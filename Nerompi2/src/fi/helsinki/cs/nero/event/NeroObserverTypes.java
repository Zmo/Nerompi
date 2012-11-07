package fi.helsinki.cs.nero.event;

/**
 * Staattinen luettelo Neron tapahtumatyypeistä.
 *
 * @author Osma Suominen
 */

public class NeroObserverTypes {

	/**
	 * Ilmoittaa, että järjestelmän tuntemat huoneet tai niihin liittyvät tiedot
	 * (esim. työpisteet tai työpisteiden puhelinnumerot) ovat muuttuneet.
	 */
	
	public static final int ROOMS = 0;
	
    /**
     * Ilmoittaa, että aktiivinen huone tai siihen liittyvät tiedot
     * (esim. työpisteet) ovat muuttuneet.
     */

    public static final int ACTIVE_ROOM = 1;

    /**
     * Ilmoittaa, että projekti-hakuehto on muuttunut.
     */

    public static final int FILTER_PROJECT = 2;

    /**
     * Ilmoittaa, että hakuehtojen rajaama henkilölista on muuttunut.
     */

    public static final int FILTER_PEOPLE = 3;

    /**
     * Ilmoittaa, että hakuehtojen rajaama huonelista on muuttunut.
     */
    public static final int FILTER_ROOMS = 4;

    /**
     * Ilmoittaa, että hakuehtojen aikaväli on muuttunut.
     */

    public static final int TIMESCALE = 5;

    /**
     * Ilmoittaa, että tarkasteltava osa-aikaväli on muuttunut.
     */

    public static final int TIMESCALESLICE = 6;

    /**
     * Ilmoittaa, että työpisteisiin liittyvät varaukset ovat muuttuneet.
     */
    public static final int RESERVATIONS = 7;
    
    /**
     * Ilmoittaa, että uusi viesti on saapunut viestipulikkaan.
     */
    public static final int STATUSBAR = 8;

    /**
     * Ilmoittaa, että slideria heilutellaan muttei ehkä päästetty irti
     */
    public static final int TIMESCALESLICEUPDATING  = 9;
        
    /**
     * Ilmoittaa, että hiiren kursoria halutaan vaihtaa.
     * Uuden kursorin tyyppi voidaan hakea Session.getCursorType():llä
     */
    public static final int CURSORCHANGE = 10;
    /**
     * Tapahtumatyyppien kokonaismäärä.
     */
    public static final int NTYPES = 11;
}
