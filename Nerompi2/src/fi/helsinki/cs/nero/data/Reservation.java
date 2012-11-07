package fi.helsinki.cs.nero.data;

import fi.helsinki.cs.nero.logic.Session;

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
	
	/**Työpiste, johon varaus tehdään*/
	private Post targetPost;

	/**Henkilö, jolle varataan*/
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
	 * varauksen tunnuksen, työpisteen johon varaus liittyy, henkilön johon
	 * varaus liittyy, varauksen aikavälin, varauksen viikkotunnit sekä varauksen
	 * kuvauksen.
	 * @param session Sessio <code>Session</code> oliona.
	 * @param reservationID Varauksen tunnus Stringinä.
	 * @param post Työpiste <code>Post</code> oliona.
	 * @param person Henkilö <code>Person</code> oliona.
	 * @param timeSlice Aikaväli <code>TimeSlice</code> oliona.
	 * @param weeklyHours Viikkotunnit.
	 * @param description Kuvaus Stringinä.
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
	 * Vaihtoehtoinen konstruktori, jolla tehdään olemassaolevaan varaukseen perustuva
	 * uusi varaus, jolla on eri aikaväli mutta muuten samat tiedot.
	 * @param template mallina käytettävä varaus
	 * @param timeSlice uusi aikaväli
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
	 * @return reservationID Tunnus Stringinä.
	 */
	public String getReservationID(){
		return this.reservationID;
	}
	
	
	/**
	 * Palauttaa henkilön, jolle työpiste on varattu.
	 * @return reservingPerson Henkilö <code>Person</code> oliona.
	 */
	public Person getReservingPerson() {
		return reservingPerson;
	}
	
	
	/**
	 * Palauttaa työpisteen, johon varaus kohdistuu.
	 * @return targetPost Työpiste <code>Post</code> oliona.
	 */
	public Post getTargetPost() {
		return targetPost;
	}

	
	/**
	 * Palauttaa varauksen alkamis- ja loppumispäivämäärän.
	 * @return startDate Ajat <code>TimeSlice</code> oliona.
	 */
	public TimeSlice getTimeSlice(){
		return timeSlice;
	}
	
	
	/**
	 * Palauttaa viikkotunnit.
	 * @return weeklyHours Viikkotunnit Stringinä.
	 */
	public double getWeeklyHours(){
		return this.weeklyHours;
	}
	
	
	/**
	 * Palauttaa varauksen selitteen.
	 * @return description Selite Stringinä.
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
