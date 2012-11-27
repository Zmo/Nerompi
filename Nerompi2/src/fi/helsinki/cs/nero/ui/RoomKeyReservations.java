
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Room;
import fi.helsinki.cs.nero.data.RoomKeyReservation;
import fi.helsinki.cs.nero.logic.Session;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Esitt‰‰ huoneen avainvaraukset
 * @author Simo
 */
public class RoomKeyReservations {
    /*Huoneen avainvarausten erilliset rivit*/
    private LinkedList rows;
    
    /*Kohdehuone*/
    Room room;
    
    public RoomKeyReservations(Room room, double scale, Session session, Person person) {
    
        this.room = room;
        this.rows = new LinkedList();
        
        ArrayList rkreservations = room.getRoomKeyReservations();
        
        Row reservationsRow = new Row(scale, session, person);
        this.rows.add(reservationsRow);
        
        for(int i=0; i<room.getRoomKeyReservations().size(); ++i) {
            boolean added = false;
            Iterator rowsIterator = this.rows.iterator();
            
            //Yritet‰‰n lis‰t‰ jokaiselle olemassaolevalle riville.
            while(rowsIterator.hasNext()) {            
                Row row = (Row)rowsIterator.next();

                if(row.addReservation(rkreservations.get(i), false)) {
                    added = true;
                    break;
                }
            }
            if(!added) { //Luodaan uusi rivi jossa varausjakso esitet‰‰n.
                Row newRow = new Row(scale, session, person);
                newRow.addReservation(rkreservations.get(i), false);
                this.rows.add(newRow);
            }
        }
        
        //Lopuksi suljetaan rivit = tehd‰‰n jokaisen per‰‰n tyhj‰n mittainen tyhj‰ jakso.
        Iterator i = this.rows.iterator();
        while(i.hasNext()) {
            Row row = (Row)i.next();
            row.closeRow();
        }
    }
    
    public LinkedList getRows() {
        return this.rows;
    }
    
    public Room getRoom() {
        return this.room;
    }
    
    
}
