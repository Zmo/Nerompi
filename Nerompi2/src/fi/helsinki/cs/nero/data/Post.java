
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
	 * Vakio, joka kertoo, ett� ty�piste on kokonaan varattu tarkasteltavalla osa-aikav�lill�.
	 */
	public static final int OCCUPIED = 1;
	/**
	 * Vakio, joka kertoo, ett� ty�piste on osittain vapaa tarkasteltavalla osa-aikav�lill�.
	 */
	public static final int PARTLY_FREE = 2;
	/**
	 * Vakio, joka kertoo, ett� ty�piste on kokonaan vapaa tarkasteltavalla osa-aikav�lill�.
	 */
	public static final int FREE = 3;
		
	/**Ty�pisteen tunnus*/
	private  String postID;

	/**Huone, johon ty�piste kuuluu*/
	private final Room room;

	/**Ty�pisteen numero huoneen sis�ll�*/
	private final int postNumber;
	
	/**Ty�pisteen puhelinnumerot*/
	private PhoneNumber[] phoneNumbers;

	/**Ty�pisteeseen liitetyt ty�pistevaraukset (haetaan tarvittaessa)*/
	private Reservation[] reservations = null;
	
	private final Session session;
	
	
	/**
	 * Konstruktori. Saa parametrinaan session, johon ty�piste liittyy, ty�pisteen
	 * tunnuksen sek� huoneen johon ty�piste liittyy.
	 * Puhelinnumerot t�ytyy viel� erikseen asettaa ty�pisteelle setPhoneNumbers-metodilla
	 * ennen kuin ty�pistett� voidaan k�ytt��.
	 * @param session Sessio <code>Session</code> oliona.
	 * @param postID Ty�pisteelle asetettava tunnus Stringin�. Voi olla my�s null jos ollaan luomassa uutta ty�pistett�.
	 * @param room Huone <code>Room</code> oliona.
	 * @param postNumber ty�pisteen numero huoneen sis�ll� (1,2,3...)
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
	 * Asettaa ty�pisteen puhelinnumerot. Metodia on kutsuttava t�sm�lleen kerran.
	 * @param phoneNumbers taulukko ty�pisteen puhelinnumeroista
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
	 * Palauttaa ty�pisteen tunnuksen.
	 * @return postID Tunnus Stringin�.
	 */
	public String getPostID(){
		
		return this.postID;
	}	
	/**
	 * Palauttaa huoneen, jossa ty�piste sijaitsee.
	 * @return huone Room-oliona
	 */
	public Room getRoom() {
		return this.room;
	}
	/**
	 * Palauttaa ty�pisteen numeron. Numero on huonekohtainen eli kunkin huoneen ensimm�inen
	 * ty�piste on 1, seuraava 2 jne.
	 * @return ty�pisteen numero
	 */
	public int getPostNumber() {
		return this.postNumber;
	}	
	/**
	 * Palauttaa ty�pisteen puhelinnumerot.
	 * @return phoneNumbers Puhelinnumerot <code>PhoneNumber[]</code> oliona.
	 * @throws IllegalStateException jos puhelinnumeroita ei ole asetettu setPhoneNumbers-metodilla
	 */
	public PhoneNumber[] getPhonenumbers() {
		if(phoneNumbers == null)
			throw new IllegalStateException();
		return phoneNumbers;
	}
		
	/**
	 * Palauttaa ty�pisteeseen liitetyt varaukset.
	 * @return Ty�pistevaraukset <code>Reservation[]</code> oliona.
	 */
	public Reservation[] getReservations(){
		if(reservations == null){
			reservations = session.getReservations(this);
                }
		return reservations;
	}
	
	/**
	 * Tyhjent�� ty�pisteen s�ilytt�m�n tiedon varauksista. Metodia kutsutaan, kun voidaan
	 * olettaa, ett� ty�pisteen tieto siihen liittyvist� varauksista on vanhentunut.
	 */
	public void clearReservations() {
		reservations = null;
	}
	
    /**
     * Palauttaa ne ty�pisteeseen liittyv�t varaukset, jotka kuuluvat annetulle osa-aikav�lille.
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
	 * Palauttaa ty�pisteen varaustilan tarkasteltavalla osa-aikav�lill�. Ty�piste on
	 * vapaa (FREE), jos siihen ei liity yht��n varauksia. Ty�piste on varattu (OCCUPIED),
	 * jos sille on olemassa varauksia, jotka peitt�v�t koko aikav�lin. Muutoin ty�piste
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
