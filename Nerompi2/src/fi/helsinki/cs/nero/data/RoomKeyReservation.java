/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.data;

/**
 *
 * @author skolppo
 */
public class RoomKeyReservation {
    
    private int reservationID;
    
    private Room targetRoom;
    
    private Person reserver;
    
    private TimeSlice timeslice;
    
    
    public RoomKeyReservation(int reservationID, Room targetRoom, Person reserver, TimeSlice timeslice) {
        this.reservationID = reservationID;
        this.targetRoom = targetRoom;
        this.reserver = reserver;
        this.timeslice = timeslice;
    }
    
    public int getReservationID() {
        return this.reservationID;
    }
    
    public Room getTargetRoom() {
        return this.targetRoom;
    }
    
    public Person getReserver() {
        return this.reserver;
    }
    
    public TimeSlice getTimeSlice() {
        return this.timeslice;
    }
}
