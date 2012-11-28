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
public class PhoneNumber implements Comparable {
    
	/**Puhelinnumeron tunnus*/
	private final String phoneNumberID;
	
	/**Työpiste, johon puhelinnumero liittyy.*/
	private final Post post;
	
	
	/**Itse numero*/
	private final String phoneNumber;

	private Session session;
        
        private Person person;
        
        private String personID;
	
	
	/**
	 * Konstruktori. Saa parametrinaan session, johon puhelinnumero liittyy,
	 * puhelinnumeron tunnuksen, työpisteen johon numero liittyy ja itse puhelinnumeron.
	 * @param session Sessio <code>Session</code> oliona.
	 * @param phoneNumberID Puhelinnumerolle asetettava tunnus Stringinä.
	 * @param post Työpiste, johon numero liitetään <code>Post</code> oliona.
	 * @param phoneNumber Numero Stringinä.
	 * @throws IllegalArgumentException Jos annettu Session tai phoneNumberID null.
	 */
	public PhoneNumber(Session session, String phoneNumberID, Post post, String phoneNumber, String personID){
				
		if (session == null || phoneNumberID == null) {
			throw new IllegalArgumentException();
		}
		this.person = person;
		this.session = session;
		this.phoneNumberID = phoneNumberID;
		this.post = post;
		this.phoneNumber = phoneNumber;
                this.personID = personID;
	}

	/**
	 * Vaihtoehtoinen konstruktori, jolla voi tehdä olemassaolevaan puhelinnumeroon
	 * perustuvan uuden puhelinnumeron, jolla on eri työpiste mutta muuten samat tiedot.
	 * @param template Olion pohja
	 * @param post uusi työpiste
	 */
	public PhoneNumber(PhoneNumber template, Post post, String personID) {
		this.session = template.session;
		this.phoneNumberID = template.phoneNumberID;
		this.phoneNumber = template.phoneNumber;
		this.post = post;
                this.personID = personID;
	}
	
	/**
	 * Palauttaa puhelinnumeron tunnuksen.
	 * @return phoneNumberID Tunnus Stringinä.
	 */
	public String getPhoneNumberID() {
		return this.phoneNumberID;
	}
	
	
	/**
	 * Palauttaa työpisteen, johon numero on liitetty.
	 * @return post työpiste <code>Post</code> oliona.
	 */
	public Post getPost() {
		return this.post;
	}
	
	
	/**
	 * Palauttaa puhelinnumeron.
	 * @return phoneNumber puhelinnumero Stringinä.
	 */
	public String getPhoneNumber(){
		
			return phoneNumber;
		
	}

        public String getPersonID() {
            return personID;
        }

        public void setPersonID(String personID) {
            this.personID = personID;
        }

	/**
	 * Laiska toString()
	 */
	public String toString() {
            if (this.post == null && this.person == null)
                return phoneNumber;
            
            else if(this.post == null)
                return phoneNumber + " " + this.person;
            
            else if (this.person == null)
                return phoneNumber + " (" + this.post + ")";
            
            else
                return phoneNumber + " (" + post + ")" + " " + this.person;
	}
	
	public int compareTo(Object obj) {
		PhoneNumber p = (PhoneNumber)obj;
		return this.phoneNumber.compareTo(p.getPhoneNumber());
	}
}
