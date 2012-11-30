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
        
        this.roomKeyReservation = roomKeyReservation;
        this.person = person;
        
        this.setIcon(null);
        this.setText(updateAikaTeksti(this.getTargetDate()));
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt){
        if (evt.getPropertyName().equalsIgnoreCase("date")){
            System.out.println("Huoneen " + this.roomKeyReservation.getTargetRoom().getRoomName() + " avainvarausta koitettiin muuttaa");
            this.setTargetDate((Date)evt.getNewValue());
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
