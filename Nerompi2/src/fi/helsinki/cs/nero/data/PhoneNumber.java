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
	
	/**Ty�piste, johon puhelinnumero liittyy.*/
	private final Post post;
	
	
	/**Itse numero*/
	private final String phoneNumber;

	private Session session;
	
	
	/**
	 * Konstruktori. Saa parametrinaan session, johon puhelinnumero liittyy,
	 * puhelinnumeron tunnuksen, ty�pisteen johon numero liittyy ja itse puhelinnumeron.
	 * @param session Sessio <code>Session</code> oliona.
	 * @param phoneNumberID Puhelinnumerolle asetettava tunnus Stringin�.
	 * @param post Ty�piste, johon numero liitet��n <code>Post</code> oliona.
	 * @param phoneNumber Numero Stringin�.
	 * @throws IllegalArgumentException Jos annettu Session tai phoneNumberID null.
	 */
	public PhoneNumber(Session session, String phoneNumberID, Post post, String phoneNumber){
		
		
		if (session == null || phoneNumberID == null){
			throw new IllegalArgumentException();
		}
		
		this.session = session;
		this.phoneNumberID = phoneNumberID;
		this.post = post;
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Vaihtoehtoinen konstruktori, jolla voi tehd� olemassaolevaan puhelinnumeroon
	 * perustuvan uuden puhelinnumeron, jolla on eri ty�piste mutta muuten samat tiedot.
	 * @param template Olion pohja
	 * @param post uusi ty�piste
	 */
	public PhoneNumber(PhoneNumber template, Post post) {
		this.session = template.session;
		this.phoneNumberID = template.phoneNumberID;
		this.phoneNumber = template.phoneNumber;
		this.post = post;
	}
	
	/**
	 * Palauttaa puhelinnumeron tunnuksen.
	 * @return phoneNumberID Tunnus Stringin�.
	 */
	public String getPhoneNumberID() {
		return this.phoneNumberID;
	}
	
	
	/**
	 * Palauttaa ty�pisteen, johon numero on liitetty.
	 * @return post ty�piste <code>Post</code> oliona.
	 */
	public Post getPost() {
		return this.post;
	}
	
	
	/**
	 * Palauttaa puhelinnumeron.
	 * @return phoneNumber puhelinnumero Stringin�.
	 */
	public String getPhoneNumber(){
		
			return phoneNumber;
		
	}
	
	/**
	 * Laiska toString()
	 */
	public String toString() {
		if(post == null)
			return phoneNumber;
		else
			return phoneNumber + " (" + post + ")";
	}
	
	public int compareTo(Object obj) {
		PhoneNumber p = (PhoneNumber)obj;
		return this.phoneNumber.compareTo(p.getPhoneNumber());
	}
}
