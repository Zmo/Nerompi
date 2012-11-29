/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.RoomKeyReservation;
import java.sql.SQLException;
import javax.swing.JButton;

/**
 *
 * @author rkolagus
 */
public class AvaimenpoistoNappi extends JButton{
    
    private RoomKeyReservation roomKeyReservation = null;
    private Person person;
    
    public AvaimenpoistoNappi(Person person, RoomKeyReservation roomKeyReservation){
        super();
        this.setText("Poista avainvaraus");
        this.roomKeyReservation = roomKeyReservation;
        this.person = person;
        this.addMouseListener(new AvaimenpoistoNappiListener(this));
    }
    
    public void poistaAvain() throws SQLException{
        if (this.roomKeyReservation == null){
            this.person.getSession().setStatusMessage(" - Virhe - AvaimenpoistoNappi: avainvarausta ei en‰‰ ole!");
            return;
        }
        this.person.getSession().deleteRoomkeyReservation(this.roomKeyReservation, this.person);
        this.person.getSession().setStatusMessage("Avainvaraus poistettu");
        this.roomKeyReservation = null;
    };
    
}
