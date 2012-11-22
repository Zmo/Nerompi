/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.data;

import fi.helsinki.cs.nero.logic.Session;

/**
 *
 * @author skolppo
 */
public class RoomKeyReservation {
    
    private int reservationID;
    
    private Room targetRoom;
    
    private String reserver;
    
    private TimeSlice timeslice;
    
    private Session session;
    
    
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
