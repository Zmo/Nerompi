package fi.helsinki.cs.nero.data;

import fi.helsinki.cs.nero.logic.Session;

//package fi.helsinki.cs.nero.data;
/*
 * Created on Oct 22, 2004
 *
 */

/**
 * @author Johannes Kuusela
 *
 */
public class Contract implements Comparable {

	
	/**Sopimuksen tunnus*/
	private final String contractID;
	
	/** Projekti, johon sopimus liittyy.*/
	private final Project project;
	
	/** Henkil�, jota sopimus koskee */
	private final Person person;
	
	/** Henkil�n nimike t�ss� ty�sopimuksessa. */
	private final String title;
	
	/** Ty�n hoitoprosentti. Normaaleissa sopimusjaksoissa 100; alle 100 merkitsee osittaista tai kokonaista virkavapautta. */
	private int workingPercentage;
	
	/**Sopimuksen kesto*/
	private final TimeSlice timeSlice;

	private final Session session;
	
	
	/**
	 * Konstruktori. Saa parametrinaan session johon liittyy, sopimuksen tunnuksen, 
	 * projektin johon liittyy, sek� aikav�lin jolle sopimus tehd��n.
	 * @param session Sessio <code>Session</code> oliona.
	 * @param contractID Sopimukselle asetettava tunnus Stringin�.
	 * @param project Projekti, johon sopimus tehd��n <code>Project</code> oliona.
	 * @param timeSlice Aikav�li <code>TimeSlice</code> oliona.
	 * @throws IllegalArgumentException Jos annettu Session tai contractID null.
	 */

	public Contract(Session session, String contractID, Project project, Person person, String title, int workingPercentage, TimeSlice timeSlice){
		
		if (session == null || contractID == null){
			throw new IllegalArgumentException();
		}
		
		this.session = session;
		this.contractID = contractID;
		this.project = project;
		this.person = person;
		this.title = title;
		this.workingPercentage = workingPercentage;
		this.timeSlice = timeSlice;
		
	}
	
	/**
	 * Palauttaa projektin, johon sopimus liittyy, 
	 * tai null jos sopimus ei liity mihink��n projektiin.
	 * @return project Projekti <code>Project</code> oliona.
	 */
	public Project getProject() {
		
		return project;
	}

	
	/** Palauttaa henkil�n, jota sopimus koskee.
	 * @return person Henkil� <code>Person</code> oliona.
	 */
	public Person getPerson() {
		return person;
	}
	
	/**
	 * Palauttaa sopimuksen teht�v�nimikkeen.
	 * @return teht�v�nimike
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Palauttaa ty�skentelyprosentin t�ss� sopimusjaksossa. Normaalisti 100, virkavapauksissa v�hemm�n.
	 * @return ty�skentelyprosentti v�lilt� 0..100
	 */
	public int getWorkingPercentage() {
		return workingPercentage;
	}

	/**
	 * Palauttaa sopimuksen tunnuksen.
	 * @return contractID Tunnus Stringin�.
	 */
	public String getContractID() {
		return this.contractID;
	}
	
	
	/**
	 * Palauttaa sopimuksen keston.
	 * @return timeSlice Kesto <code>TimeSlice</code> oliona.
	 */
	public TimeSlice getTimeSlice(){
		
		return this.timeSlice;
	}

	/**
	 * Palauttaa: 
	 * 0, jos t�m�n sopimuksen alkup�iv� = toisen sopimuksen alkup�iv�
	 * <0, jos t�m� sopimus alkaa ennen toista sopimusta
	 * >0, jos t�m� sopimus alkaa toisen sopimuksen j�lkeen
	 * @param other Toinen sopimus <code>Contract</code> oliona.
	 * @return 
	 */
	public int compareStartDates(Contract other){
		return timeSlice.getStartDate().compareTo(other.timeSlice.getStartDate());
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object obj) {
		Contract c = (Contract)obj;
		return timeSlice.compareTo(c.getTimeSlice());
	}
	
	public String toString() {
		if(project != null)
			return person + " " + timeSlice + " " + project;
		else
			return person + " " + timeSlice;
	}
}
