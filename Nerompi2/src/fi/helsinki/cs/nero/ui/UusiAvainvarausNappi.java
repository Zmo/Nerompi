/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.RoomKeyReservation;
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
            this.person.getSession().setStatusMessageNoPrint("Klikkaa haluttua huonetta ensin!");
        } else {
            Date alkuaika = this.person.getSession().getTimeScaleSlice().getStartDate();
            
            if (!(this.person.getSession().getActiveRoom().getRoomKeyReservations() == null)) {
                RoomKeyReservation[] avainVaraukset = this.person.getSession().getActiveRoom().getRoomKeyReservations();
                for (int a = 0; a < avainVaraukset.length; a++) {
                    if ((avainVaraukset[a].getReserver().equalsIgnoreCase(this.person.getName()))) {
                        alkuaika = avainVaraukset[a].getTimeSlice().getEndDate();
                    } else {
                        System.out.println(avainVaraukset[a].getReserver() + "\n -" + this.person.getName());
                    }
                }
            }
            if (!(alkuaika.before(this.person.getSession().getTimeScaleSlice().getEndDate()))) {
                this.person.getSession().setStatusMessageNoPrint("Henkil�ll� on jo avainvaraus tarkasteluajan loppuun asti!");
            } else {
                this.person.getSession().addRoomKeyReservation(this.person, this.person.getSession().getTimeScaleSlice());
            }

        }
    }
}
