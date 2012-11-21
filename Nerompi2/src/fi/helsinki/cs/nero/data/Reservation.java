package fi.helsinki.cs.nero.data;

import fi.helsinki.cs.nero.logic.Session;
import java.util.Date;

/*
 * Created on Oct 22, 2004
 *
 */

/**
 * @author Johannes Kuusela
 *
 */
public class Reservation implements Comparable {
	
	/**Varauksen tunnus*/
	private String reservationID;
	
	/**Ty�piste, johon varaus tehd��n*/
	private Post targetPost;

	/**Henkil�, jolle varataan*/
	private Person reservingPerson;
	
	/**Varauksen aikajakso*/
	private TimeSlice timeSlice;
	
	/**Varauksen viikkotunnit*/
	private double weeklyHours;
		
	/**Varauksen selite*/
	private String description;

	private Session session;
	
	/**
	 * Konstruktori. Saa parametrinaan session, johon varaus liittyy, 
	 * varauksen tunnuksen, ty�pisteen johon varaus liittyy, henkil�n johon
	 * varaus liittyy, varauksen aikav�lin, varauksen viikkotunnit sek� varauksen
	 * kuvauksen.
	 * @param session Sessio <code>Session</code> oliona.
	 * @param reservationID Varauksen tunnus Stringin�.
	 * @param post Ty�piste <code>Post</code> oliona.
	 * @param person Henkil� <code>Person</code> oliona.
	 * @param timeSlice Aikav�li <code>TimeSlice</code> oliona.
	 * @param weeklyHours Viikkotunnit.
	 * @param description Kuvaus Stringin�.
	 * @throws IllegalArgumentException Jos annettu Session tai reservationID null.
	 */
	public Reservation(Session session, String reservationID, Post post, Person person, TimeSlice timeSlice,
						double weeklyHours, String description){
		
		
		if (session == null){
			throw new IllegalArgumentException();
		}
		
		
		this.session = session;
		this.reservationID = reservationID;
		this.targetPost = post;
		this.reservingPerson = person;
		this.timeSlice = timeSlice;
		
		if(weeklyHours < 0){
			this.weeklyHours = 0;
			
		}else{
			this.weeklyHours = weeklyHours;
		}
			
		this.description = description;
		
	}
	
	
	/**
	 * Vaihtoehtoinen konstruktori, jolla tehd��n olemassaolevaan varaukseen perustuva
	 * uusi varaus, jolla on eri aikav�li mutta muuten samat tiedot.
	 * @param template mallina k�ytett�v� varaus
	 * @param timeSlice uusi aikav�li
	 */
	public Reservation(Reservation template, TimeSlice timeSlice) {
		session = template.session;
		reservationID = template.reservationID;
		targetPost = template.targetPost;
		reservingPerson = template.reservingPerson;
		weeklyHours = template.weeklyHours;
		description = template.description;
		this.timeSlice = timeSlice;
	}
	
	
	/**
	 * Palauttaa varauksen tunnuksen.
	 * @return reservationID Tunnus Stringin�.
	 */
	public String getReservationID(){
		return this.reservationID;
	}
	
	
	/**
	 * Palauttaa henkil�n, jolle ty�piste on varattu.
	 * @return reservingPerson Henkil� <code>Person</code> oliona.
	 */
	public Person getReservingPerson() {
		return reservingPerson;
	}
	
	
	/**
	 * Palauttaa ty�pisteen, johon varaus kohdistuu.
	 * @return targetPost Ty�piste <code>Post</code> oliona.
	 */
	public Post getTargetPost() {
		return targetPost;
	}

	
	/**
	 * Palauttaa varauksen alkamis- ja loppumisp�iv�m��r�n.
	 * @return startDate Ajat <code>TimeSlice</code> oliona.
	 */
	public TimeSlice getTimeSlice(){
		return timeSlice;
	}
	
	/**
         * Palauttaa varauksen loppumisp�iv�m��r�n.
         * @return loppumisp�iv� Date-oliona
         */
        public Date getLastDay() {
            return timeSlice.getEndDate();
        }
        
	/**
	 * Palauttaa viikkotunnit.
	 * @return weeklyHours Viikkotunnit Stringin�.
	 */
	public double getWeeklyHours(){
		return this.weeklyHours;
	}
	
	
	/**
	 * Palauttaa varauksen selitteen.
	 * @return description Selite Stringin�.
	 */
	public String getDescription() {
		
			return description;
	}
	
	public Session getSession() {
	    return this.session;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object obj) {
		
		Reservation r = (Reservation)obj;
		
		TimeSlice t = r.getTimeSlice();
		
		return this.timeSlice.compareTo(t);	
		
	}
	
	public String toString() {
		return targetPost + " " + reservingPerson + " " + timeSlice;
	}

}
