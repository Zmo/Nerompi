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
public class Project implements Comparable {

	
	/**Projektin tunnus*/
	private final String projectID;
	
	/** Projektin nimi */
	private String projectName;
	
	/**Projektiin liittyv�t ty�sopimukset*/
	private Contract[] contracts;
		
	/**Projektin alkupvm ja loppupvm <code>TimeSlice</code> oliona.*/
	private TimeSlice timeSlice;

	 /**Projektin vastuuhenkil�*/
	private String projectManager;

	private Session session;
	
	
	/**
	 * Konstruktori. Saa parametrinaan session, johon projekti liittyy,
	 * projektin tunnuksen, projektiin liittyv�t sopimukset, projektin keston
	 * sek� vastuuhenkil�n.
	 * @param session Sessio <code>Session</code> oliona.
	 * @param projectID Projektille asetettava tunnus Stringin�.
	 * @param timeSlice Projektin kesto <code>TimeSlice</code> oliona.
	 * @param projectManager Projektin vastuuhenkil� <code>Person</code> oliona.
	 * @throws IllegalArgumentException Jos annettu Session tai projectID null.
	 */
	public Project(Session session, String projectID, String projectName,
					String projectManager, TimeSlice timeSlice)
	{
		
		if (session == null || projectID == null){
			throw new IllegalArgumentException();
		}
		
		this.session = session;
		this.projectID = projectID;
		this.projectName = (projectName == null ? "(tyhj� nimi)" : projectName);
		this.projectManager = (projectManager == null ? "(ei vastuuhenkil��)" : projectManager);
		this.timeSlice = timeSlice;
		this.contracts = null;
	}	
		
	/**
	 * Palauttaa projektin tunnuksen.
	 * @return projectID Tunnus Stringin�.
	 */
	public String getProjectID() {
		return this.projectID;
	}
	
	/**
	 * Palauttaa projektin vastuuhenkil�n.
	 * @return projectManager Vastuuhenkil� <code>Person</code> oliona.
	 */
	public String getProjectManager() {
		return projectManager;
	}
	
	/**
	 * Palauttaa projektin nimen.
	 * @return Projektin nimi Stringin�.
	 */
	public String getProjectName() {
		return projectName;
	}
	

	/**
	 * Palauttaa projektin alkup�iv�m��r�n ja loppupp�iv�m��r�n.
	 * @return endingDate P�iv�m��r�t <code>TimeSlice</code> oliona.
	 */
	public TimeSlice getTimeSlice(){
		return timeSlice;
		
	}
	
	public String toString() {
		return this.projectName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object obj) {
		Project p = (Project)obj;
		return projectName.compareTo(p.getProjectName());
	}
}
