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
	
	/**Projektiin liittyvät työsopimukset*/
	private Contract[] contracts;
		
	/**Projektin alkupvm ja loppupvm <code>TimeSlice</code> oliona.*/
	private TimeSlice timeSlice;

	 /**Projektin vastuuhenkilö*/
	private String projectManager;

	private Session session;
	
	
	/**
	 * Konstruktori. Saa parametrinaan session, johon projekti liittyy,
	 * projektin tunnuksen, projektiin liittyvät sopimukset, projektin keston
	 * sekä vastuuhenkilön.
	 * @param session Sessio <code>Session</code> oliona.
	 * @param projectID Projektille asetettava tunnus Stringinä.
	 * @param timeSlice Projektin kesto <code>TimeSlice</code> oliona.
	 * @param projectManager Projektin vastuuhenkilö <code>Person</code> oliona.
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
		this.projectName = (projectName == null ? "(tyhjä nimi)" : projectName);
		this.projectManager = (projectManager == null ? "(ei vastuuhenkilöä)" : projectManager);
		this.timeSlice = timeSlice;
		this.contracts = null;
	}	
		
	/**
	 * Palauttaa projektin tunnuksen.
	 * @return projectID Tunnus Stringinä.
	 */
	public String getProjectID() {
		return this.projectID;
	}
	
	/**
	 * Palauttaa projektin vastuuhenkilön.
	 * @return projectManager Vastuuhenkilö <code>Person</code> oliona.
	 */
	public String getProjectManager() {
		return projectManager;
	}
	
	/**
	 * Palauttaa projektin nimen.
	 * @return Projektin nimi Stringinä.
	 */
	public String getProjectName() {
		return projectName;
	}
	

	/**
	 * Palauttaa projektin alkupäivämäärän ja loppuppäivämäärän.
	 * @return endingDate Päivämäärät <code>TimeSlice</code> oliona.
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
