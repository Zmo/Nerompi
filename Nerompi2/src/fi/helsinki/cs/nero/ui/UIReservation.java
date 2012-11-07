package fi.helsinki.cs.nero.ui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.logic.Session;
/**
 * Esitt‰‰ yhden tyˆpistevarauksen.
 */
public class UIReservation extends TimelineElement {

    /**
     * Varaus jonka elementti esitt‰‰.
     */
    private Reservation reservation = null;    
    private static Color BG_COLOR = new Color(39,177,39);
    private static Border BORDER = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
    /**
     * Rivi jossa varaus sijaitsee.
     */
    Row row = null;
    
    /**
     * Luo varauksen jonka kokoa voi muuttaa!
     * @param reservation Varaus jonka elementti esitt‰‰.
     * @param row Rivi jossa elementti sijaitsee.
     * @param scale K‰ytett‰v‰ skaala = p‰iv‰/pikselit.
     */
    public UIReservation(Reservation reservation, Row row, double scale) {
        super(reservation.getTargetPost().toString(), reservation.getTimeSlice(), scale, BG_COLOR, reservation.getSession());
        this.setBorder(BORDER);
        this.reservation = reservation;
    }
    
    /**
     * Luo varauksen jonka kokoa ei voi muuttaa.
     * @param reservation Varaus joka esitet‰‰n.
     * @param scale K‰ytett‰v‰ skaala.
     */
    public UIReservation(Reservation reservation, double scale) {
        super(reservation.getTimeSlice(), scale, BG_COLOR, reservation.getReservingPerson().getName(), reservation.getSession());
        this.setBorder(BORDER);
        this.reservation = reservation;        

    }
    
    /**
     * Tallettaa elementintiedot tietokantaan.
     */
    public void storeToDB() {
        //Jos elementin kokoa saa muuttaa on se ylip‰‰t‰ns‰ voinut muuttua.
        if(this.resizable) {
            this.reservation.getSession().updateReservation(this.reservation);
        }
    }
    
    /**
     * Asettaa aktiivisen huoneen.
     */
    public void setActiveRoom(){
    		this.reservation.getSession().setActiveRoom(this.reservation.getTargetPost().getRoom());
    }
    
    /**
     * Palauttaa viitteen k‰ytett‰v‰‰n sessioon.
     * @return Viite <code>Session</code>-olioon.
     */
    public Session getSession() {
        return this.reservation.getSession();
    }
    
    /**
     * Palauttaa elementin esitt‰m‰n vqrauksen.
     * @return Viitteen <code>reservation</code> olioon.
     */
    public Reservation getReservation() {
        return this.reservation;
    }
}
