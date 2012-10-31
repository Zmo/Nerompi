/*
 * Created on Oct 21, 2004
 */
package fi.helsinki.cs.nero.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import fi.helsinki.cs.nero.logic.Session;

/**
 *  @author Ville Sundberg
 */
public class FilterEventListener implements DocumentListener, ActionListener {
	
	/** henkilöhaun käynnistämiseen tarvittava minimi merkkimäärä */
	private final int PERSON_MIN_CHAR = 3;
	/** huonehaun käynnistämiseen tarvittava minimi merkkimäärä */
	private final int ROOM_MIN_CHAR = 2;
	/** ajastimen laukeamisaika */
	private final int TRIGGER_TIME = 1500;
	/** viimeisin tapahtuma */
	private DocumentEvent latestEvent;
	/** session, jonka metodeja kutsutaan*/
	private Session session;
	private Timer timer;
	
	public FilterEventListener(Session session){
		this.session = session;		
		timer = new Timer(TRIGGER_TIME, this);
		timer.setRepeats(false);
		timer.start();
		
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 * uuden muutoksen tullessa, se laitetaan viimeisimmäksi muutoikseksi ja käynnistetään ajastin uudelleen
	 */
	public void changedUpdate(DocumentEvent e) {
        latestEvent = e;
		timer.restart();
	}
	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 * uuden muutoksen tullessa, se laitetaan viimeisimmäksi muutoikseksi ja käynnistetään ajastin uudelleen
	 */
	public void insertUpdate(DocumentEvent e) {
        latestEvent = e;
		timer.restart();
	}
	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 * uuden muutoksen tullessa, se laitetaan viimeisimmäksi muutoikseksi ja käynnistetään ajastin uudelleen
	 */
	public void removeUpdate(DocumentEvent e) {
		latestEvent = e;
		timer.restart();
	}
	
	/**
	 * @param e DocumentEvent, joka on tapahtunut kentässä room tai personName.
	 * Metodi käsitteelee kootusti kaikki personName ja room kenttiin tulevat muutokset(molemmilla kentillä 
	 * on oma FilterEventListenerinsä).
	 */
	private void handleUpdate(DocumentEvent e){
		try{

			if(e.getDocument().getProperty("name").equals("personName")){
				if(e.getDocument().getLength()>= PERSON_MIN_CHAR){      		
					session.setFilterPersonName(e.getDocument().getText(0,e.getDocument().getLength()));
				}
				else if(e.getDocument().getLength()==0){
					session.setFilterPersonName("");
			    }
				
			}
			else if(e.getDocument().getProperty("name").equals("room")){
				if(e.getDocument().getLength()>=ROOM_MIN_CHAR){     
					session.setFilterRoomName(e.getDocument().getText(0,e.getDocument().getLength()));
				}
				else if(e.getDocument().getLength()==0){
					session.setFilterRoomName("");
				}
				
			} 		 		
		}
		catch(BadLocationException ble){
			//trust me, tänne ei tulla :)	
		}
		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * timerin "lauettua" se kutsuu tätä metodia. 
	 */

	public void actionPerformed(ActionEvent arg0) {
		if(latestEvent !=null)
         handleUpdate(latestEvent);				
	}	
	
}
