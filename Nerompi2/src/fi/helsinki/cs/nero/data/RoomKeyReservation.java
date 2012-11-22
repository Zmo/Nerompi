/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.data;

import fi.helsinki.cs.nero.logic.Session;

/**
 *
 * @author Zmo
 */
public class RoomKeyReservation {
    
    /**Avainvarauksen id*/
    private int reservationID;
    
    /**Huone, johon varaus kohdistuu*/
    private Room targetRoom;
    
    /**Henkilö, jolle varaus kuuluu*/
    private String reserver;
    
    /**Varauksen aikaväli*/
    private TimeSlice timeslice;
    
    private Session session;
    
    /**
     * Konstruktori
     * @param reservationID Varauksen id
     * @param targetRoom Huone, johon varaus kohdistuu
     * @param reserver Henkilö, jolle varaus kuuluu
     * @param timeslice Varauksen aikaväli
     * @param session sessio Session -oliona
     */
    public RoomKeyReservation(int reservationID, Room targetRoom, String reserver, TimeSlice timeslice, Session session) {
        this.reservationID = reservationID;
        this.targetRoom = targetRoom;
        this.reserver = reserver;
        this.timeslice = timeslice;
        this.session = session;
    }
    
    public int getReservationID() {
        return this.reservationID;
    }
    
    public Room getTargetRoom() {
        return this.targetRoom;
    }
    
    public String getReserver() {
        return this.reserver;
    }
    
    public TimeSlice getTimeSlice() {
        return this.timeslice;
    }
    
    public Session getSession() {
        return this.session;
    }
}
