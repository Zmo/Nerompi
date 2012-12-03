/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.RoomKeyReservation;
import fi.helsinki.cs.nero.data.TimeSlice;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JButton;

/**
 *
 * @author rkolagus
 */
public class UusiAvainvarausNappi extends JButton {

    public Person person;

    public UusiAvainvarausNappi(Person person) {
        super();
        this.person = person;
        this.setText("Uusi avain");
        this.addMouseListener(new UusiAvainvarausListener(this));
    }

    public void lisaaAvainVaraus() {
        if (this.person.getSession().getActiveRoom() == null) {
            this.person.getSession().setStatusMessage("Klikkaa haluttua huonetta ensin!");
        } else {
            Date alkuaika = this.person.getSession().getTimeScaleSlice().getStartDate();
            
            if (this.person.getSession().getActiveRoom().getRoomKeyReservations() != null) {
                ArrayList avainVaraukset = this.person.getSession().getActiveRoom().getRoomKeyReservations();
                for (int a = 0; a < avainVaraukset.size(); a++) {
                    RoomKeyReservation avainVaraus = (RoomKeyReservation) avainVaraukset.get(a);
                    if (avainVaraus.getReserverName().equalsIgnoreCase(this.person.getName()) && !(avainVaraus.getTimeSlice().getEndDate().before(alkuaika))) {
                        alkuaika = avainVaraus.getTimeSlice().getEndDate();
                    }
                    else {
                    }
                }
            }
            if (!(alkuaika.before(this.person.getSession().getTimeScaleSlice().getEndDate()))) {
                this.person.getSession().setStatusMessage("Henkil�ll� on jo avainvaraus tarkasteluajan loppuun asti!");
            } else {
                this.person.getSession().addRoomKeyReservation(this.person, new TimeSlice(alkuaika, this.person.getSession().getTimeScaleSlice().getEndDate()));
                this.person.getSession().setStatusMessage("Avainvaraus luotu huoneeseen " + this.person.getSession().getActiveRoom() + ".");
            }
        }
    }
}
