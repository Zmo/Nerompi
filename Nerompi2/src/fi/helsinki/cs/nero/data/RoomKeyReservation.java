
package fi.helsinki.cs.nero.data;

import fi.helsinki.cs.nero.logic.Session;

/**
 * Tiettyyn huoneeseen kohdistuva avainvaraus
 * @author Simo
 */
public class RoomKeyReservation {
    
    /**Avainvarauksen id*/
    private int reservationID;
    
    /**Huone, johon varaus kohdistuu*/
    private Room targetRoom;
    
    /**Varaajan htunnus*/
    private String reserverID;
    
    /**Varaajan nimi*/
    private String reserverName;
    
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
    public RoomKeyReservation(int reservationID, Room targetRoom, String reserverID, String reserverName, TimeSlice timeslice, Session session) {
        this.reservationID = reservationID;
        this.targetRoom = targetRoom;
        this.reserverID = reserverID;
        this.reserverName = reserverName;
        this.timeslice = timeslice;
        this.session = session;
    }
    
    public int getReservationID() {
        return this.reservationID;
    }
    
    public Room getTargetRoom() {
        return this.targetRoom;
    }
    
    public String getReserverID() {
        return this.reserverID;
    }
    
    public String getReserverName() {
        return this.reserverName;
    }
    
    public TimeSlice getTimeSlice() {
        return this.timeslice;
    }
    
    public Session getSession() {
        return this.session;
    }
}
