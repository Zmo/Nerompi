
package fi.helsinki.cs.nero.data;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import fi.helsinki.cs.nero.logic.Session;

/*
 * Created on Oct 22, 2004
 *
 */

/**
 * @author Johannes Kuusela
 *
 */
public class Post {
	/**
	 * Vakio, joka kertoo, että työpiste on kokonaan varattu tarkasteltavalla osa-aikavälillä.
	 */
	public static final int OCCUPIED = 1;
	/**
	 * Vakio, joka kertoo, että työpiste on osittain vapaa tarkasteltavalla osa-aikavälillä.
	 */
	public static final int PARTLY_FREE = 2;
	/**
	 * Vakio, joka kertoo, että työpiste on kokonaan vapaa tarkasteltavalla osa-aikavälillä.
	 */
	public static final int FREE = 3;
		
	/**Työpisteen tunnus*/
	private  String postID;

	/**Huone, johon työpiste kuuluu*/
	private final Room room;

	/**Työpisteen numero huoneen sisällä*/
	private final int postNumber;
	
	/**Työpisteen puhelinnumerot*/
	private PhoneNumber[] phoneNumbers;

	/**Työpisteeseen liitetyt työpistevaraukset (haetaan tarvittaessa)*/
	private Reservation[] reservations = null;
	
	private final Session session;
	
	
	/**
	 * Konstruktori. Saa parametrinaan session, johon työpiste liittyy, työpisteen
	 * tunnuksen sekä huoneen johon työpiste liittyy.
	 * Puhelinnumerot täytyy vielä erikseen asettaa työpisteelle setPhoneNumbers-metodilla
	 * ennen kuin työpistettä voidaan käyttää.
	 * @param session Sessio <code>Session</code> oliona.
	 * @param postID Työpisteelle asetettava tunnus Stringinä. Voi olla myös null jos ollaan luomassa uutta työpistettä.
	 * @param room Huone <code>Room</code> oliona.
	 * @param postNumber työpisteen numero huoneen sisällä (1,2,3...)
	 * @throws IllegalArgumentException Jos annettu Session on null.
	 */
	public Post(Session session, String postID, Room room, int postNumber){
		
		if (session == null){
			throw new IllegalArgumentException();
		}
		this.session = session;
		this.postID = postID;
		this.room = room;
		this.postNumber = postNumber;
	}
	
	
	/**
	 * Asettaa työpisteen puhelinnumerot. Metodia on kutsuttava täsmälleen kerran.
	 * @param phoneNumbers taulukko työpisteen puhelinnumeroista
	 * @throws IllegalArgumentException jos annettu taulukko on null
	 * @throws IllegalStateException jos metodia kutsutaan toistamiseen
	 */
	public void setPhoneNumbers(PhoneNumber[] phoneNumbers) {
		if(phoneNumbers == null)
			throw new IllegalArgumentException();
		if(this.phoneNumbers != null)
			throw new IllegalStateException();
		this.phoneNumbers = phoneNumbers;
	}
	
	/**
	 * Palauttaa työpisteen tunnuksen.
	 * @return postID Tunnus Stringinä.
	 */
	public String getPostID(){
		
		return this.postID;
	}	
	/**
	 * Palauttaa huoneen, jossa työpiste sijaitsee.
	 * @return huone Room-oliona
	 */
	public Room getRoom() {
		return this.room;
	}
	/**
	 * Palauttaa työpisteen numeron. Numero on huonekohtainen eli kunkin huoneen ensimmäinen
	 * työpiste on 1, seuraava 2 jne.
	 * @return työpisteen numero
	 */
	public int getPostNumber() {
		return this.postNumber;
	}	
	/**
	 * Palauttaa työpisteen puhelinnumerot.
	 * @return phoneNumbers Puhelinnumerot <code>PhoneNumber[]</code> oliona.
	 * @throws IllegalStateException jos puhelinnumeroita ei ole asetettu setPhoneNumbers-metodilla
	 */
	public PhoneNumber[] getPhonenumbers() {
		if(phoneNumbers == null)
			throw new IllegalStateException();
		return phoneNumbers;
	}
		
	/**
	 * Palauttaa työpisteeseen liitetyt varaukset.
	 * @return Työpistevaraukset <code>Reservation[]</code> oliona.
	 */
	public Reservation[] getReservations(){
		if(reservations == null){
			reservations = session.getReservations(this);
                }
		return reservations;
	}
	
	/**
	 * Tyhjentää työpisteen säilyttämän tiedon varauksista. Metodia kutsutaan, kun voidaan
	 * olettaa, että työpisteen tieto siihen liittyvistä varauksista on vanhentunut.
	 */
	public void clearReservations() {
		reservations = null;
	}
	
    /**
     * Palauttaa ne työpisteeseen liittyvät varaukset, jotka kuuluvat annetulle osa-aikavälille.
     * @return Collection-rajapinnan toteuttava joukko varauksia. Alkiot ovat Reservation-olioita.
     */
    private Collection getReservations(TimeSlice slice) {
        Reservation[] allReservations = getReservations();
        Collection reservationsInSlice = new LinkedList();
        for(int i=0; i<allReservations.length; ++i){
            if(allReservations[i].getTimeSlice().overlaps(slice)){
                reservationsInSlice.add(allReservations[i]);
            }
        }
        return reservationsInSlice;
    }
    
	/**
	 * Palauttaa työpisteen varaustilan tarkasteltavalla osa-aikavälillä. Työpiste on
	 * vapaa (FREE), jos siihen ei liity yhtään varauksia. Työpiste on varattu (OCCUPIED),
	 * jos sille on olemassa varauksia, jotka peittävät koko aikavälin. Muutoin työpiste
	 * on osittain vapaa (PARTLY_FREE).
	 * @return varaustila, joka on jokin vakioista OCCUPIED, PARTLY_FREE tai FREE
	 */
	public int getStatus() {
		
		TimeSlice sessionTimeScaleSlice = session.getTimeScaleSlice();
        Collection reservations = getReservations(sessionTimeScaleSlice);
        // t?h?n p?iv??n asti tiedet??n, ett? on varattu
        Date occupiedUntil = sessionTimeScaleSlice.getStartDate();
        
		// k?yd??n l?pi omat varaukset aikaj?rjestyksess?
        Iterator iter = reservations.iterator();

        // jos ei ole yht??n varauksia, palautetaan FREE
		if(!iter.hasNext())
			return FREE;
		
		//   - jos on varauksia, l?hdet??n liikkeelle timeScaleSlicen alkup??-pvm:st? ja
		//     k?yd??n l?pi varauksia p?ivitt?en samalla p?iv?m??r??: muistissa siis
		//     pidet??n sit? p?iv?m??r??, johon asti ainakin on t?ytt?.
		// Ensin katsotaan onko heti alussa vapaata
        Reservation res = (Reservation) iter.next();
		if (!res.getTimeSlice().contains(occupiedUntil))
			return PARTLY_FREE;

        occupiedUntil = res.getTimeSlice().getEndDate();
        
        while(iter.hasNext()) {
        	res = (Reservation) iter.next();
            // Tarkistetaan onko viimeisimm?n varausp?iv?n ja seuraavan varauksen v?liss? p?ivi?.
            // Jos on, on ty?pisteess? vapaa jakso joten se on osittain vapaa.
            if(res.getTimeSlice().startDayAfter(occupiedUntil) > 1)
                return PARTLY_FREE;
            // P?ivitet??n viimeisin varausp?iv?, jos se on v?hemm?n kuin varauksen loppu
            if(occupiedUntil.compareTo(res.getTimeSlice().getEndDate()) < 0)
                occupiedUntil = res.getTimeSlice().getEndDate();
        }
        
        // tarkistetaan onko viimeisen varauksen j?lkeen tilaa ennen aikav?lin p??ttymist?
        if(occupiedUntil.compareTo(sessionTimeScaleSlice.getEndDate()) < 0)
			return PARTLY_FREE;

		// jos p??st??n timeScaleSlicen loppuun ilman tyhji? hetki?, palautetaan OCCUPIED
		return OCCUPIED;
	}
	
	public String toString() {
		return room + ":" + postNumber;
	}
}
