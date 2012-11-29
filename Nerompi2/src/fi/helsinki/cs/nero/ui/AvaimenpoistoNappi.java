/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.RoomKeyReservation;
import fi.helsinki.cs.nero.logic.Session;
import javax.swing.JButton;

/**
 *
 * @author rkolagus
 */
public class AvaimenpoistoNappi extends JButton{
    
    private RoomKeyReservation roomKeyReservation = null;
    private Session sessio;
    
    public AvaimenpoistoNappi(RoomKeyReservation roomKeyReservation){
        super();
        this.setText("Poista avainvaraus");
        this.roomKeyReservation = roomKeyReservation;
        this.sessio = roomKeyReservation.getSession();
        this.addMouseListener(new AvaimenpoistoNappiListener(this));
    }
    
    public void poistaAvain(){
        if (this.roomKeyReservation == null){
            this.sessio.setStatusMessage(" - Virhe - AvaimenpoistoNappi: avainvarausta ei en‰‰ ole!");
            return;
        }
        this.sessio.deleteRoomkeyReservation(this.roomKeyReservation);
        this.sessio.setStatusMessage("Avainvaraus poistettu");
        this.roomKeyReservation = null;
    };
    
}
