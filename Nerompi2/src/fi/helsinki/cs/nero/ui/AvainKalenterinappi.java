/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.RoomKeyReservation;
import java.beans.PropertyChangeEvent;
import java.util.Calendar;
import java.util.Date;
import net.sourceforge.jcalendarbutton.JCalendarButton;

/**
 *
 * @author rkolagus
 */
public class AvainKalenterinappi extends JCalendarButton{
    
    boolean onkoAlku;
    private RoomKeyReservation roomKeyReservation;
    private Person person;
    
    public AvainKalenterinappi(RoomKeyReservation roomKeyReservation, Person person, boolean onkoAlku) {
        super(null);
        if (onkoAlku){
            this.setTargetDate(roomKeyReservation.getTimeSlice().getStartDate());
        }
        else {
            this.setTargetDate(roomKeyReservation.getTimeSlice().getEndDate());
        }
        
        this.onkoAlku = onkoAlku;
        this.roomKeyReservation = roomKeyReservation;
        this.person = person;
        
        this.setIcon(null);
        this.setText(updateAikaTeksti(this.getTargetDate()));
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt){
        if (evt.getPropertyName().equalsIgnoreCase("date")){
            Date kohdeaika = (Date)evt.getNewValue();
            System.out.println("Huoneen " + this.roomKeyReservation.getTargetRoom().getRoomName() + " avainvarausta koitettiin muuttaa");
            
            // Verrataan tämän varauksen toiseen aikarajaan
            if ((this.onkoAlku && kohdeaika.after(this.roomKeyReservation.getTimeSlice().getEndDate())) || 
                    ((this.onkoAlku == false) && kohdeaika.before(this.roomKeyReservation.getTimeSlice().getStartDate()))){
                System.out.println(" -|- " + kohdeaika.toString() + 
                                 "\n -|- " + this.roomKeyReservation.getTimeSlice().getStartDate() + 
                                 "\n -|- " + this.roomKeyReservation.getTimeSlice().getEndDate() + 
                                 "\n -|-> " + this.onkoAlku);
                this.roomKeyReservation.getSession().setStatusMessage("Varauksen alkupäivän tulee olla ennen loppupäivää!");
                return;
            }
            
            // verrataan henkilön muihin avainvarauksiin
            /*
            RoomKeyReservation[] avainVaraukset = this.person.getRoomKeyReservations();
            for (int indeksi = 0; indeksi < avainVaraukset.length; indeksi++){
                if (avainVaraukset[indeksi].getTargetRoom() == this.roomKeyReservation.getTargetRoom()){
                    if (this.onkoAlku && (avainVaraukset[indeksi].getTimeSlice().)){}
                    else {}
                }
            }*/
            
            // Vaikutusten tekeminen kantaan ja sessioon
            /*
            if (this.onkoAlku){
            } 
            else {
            }
            */
            this.setTargetDate(kohdeaika);
            this.setText(updateAikaTeksti(this.getTargetDate()));
        }
    }
    
    private static String updateAikaTeksti(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String uusiTeksti = calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) +1) + "." + calendar.get(Calendar.YEAR);
        return uusiTeksti;
    }
    
    private void asioita(){}
}
