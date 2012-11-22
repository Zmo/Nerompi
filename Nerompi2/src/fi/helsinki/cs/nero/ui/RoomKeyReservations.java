/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Room;
import fi.helsinki.cs.nero.data.RoomKeyReservation;
import fi.helsinki.cs.nero.logic.Session;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author skolppo
 */
public class RoomKeyReservations {
    
    private LinkedList rows;
    
    Room room;
    
    public RoomKeyReservations(Room room, double scale, Session session, Person person) {
    
        this.room = room;
        this.rows = new LinkedList();
        
        RoomKeyReservation[] rkreservations = room.getRoomKeyReservations();
        
        Row reservationsRow = new Row(scale, session, person);
        this.rows.add(reservationsRow);
        
        for(int i=0; i<room.getRoomKeyReservationNumber(); ++i) {
            boolean added = false;
            Iterator rowsIterator = this.rows.iterator();
            
            //Yritetään lisätä jokaiselle olemassaolevalle riville.
            while(rowsIterator.hasNext()) {            
                Row row = (Row)rowsIterator.next();
                
                if(row.addReservation(rkreservations[i], false)) {
                    added = true;
                    break;
                }
            }
            if(!added) { //Luodaan uusi rivi jossa varausjakso esitetään.
                Row newRow = new Row(scale, session, person);
                newRow.addReservation(rkreservations[i], false);
                this.rows.add(newRow);
            }
        }
        
        //Lopuksi suljetaan rivit = tehdään jokaisen perään tyhjän mittainen tyhjä jakso.
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
