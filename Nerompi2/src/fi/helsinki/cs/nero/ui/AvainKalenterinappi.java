/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.RoomKeyReservation;
import fi.helsinki.cs.nero.data.TimeSlice;
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
            Calendar kohdeaika = Calendar.getInstance();
            kohdeaika.setTime((Date)evt.getNewValue());
            kohdeaika.set(Calendar.HOUR_OF_DAY, 0);
            
            // Verrataan t�m�n varauksen toiseen aikarajaan
            if ((this.onkoAlku && kohdeaika.getTime().after(this.roomKeyReservation.getTimeSlice().getEndDate())) || 
                    ((this.onkoAlku == false) && kohdeaika.getTime().before(this.roomKeyReservation.getTimeSlice().getStartDate()))){
                /* Tulosteita debuggausta varten */
                /*
                System.out.println(" -|- Kohdeaika: " + kohdeaika.getTime().toString() + 
                                 "\n -|- Alkuaika:  " + this.roomKeyReservation.getTimeSlice().getStartDate() + 
                                 "\n -|- Loppuaika: " + this.roomKeyReservation.getTimeSlice().getEndDate() + 
                                 "\n -|- Onko muutettu aika alkuaika: " + this.onkoAlku);
                 */
                this.roomKeyReservation.getSession().setStatusMessage("Varauksen alkup�iv�n tulee olla ennen loppup�iv��!");
                return;
            }
            
            // verrataan henkil�n muihin avainvarauksiin
            
            RoomKeyReservation[] avainVaraukset = this.person.getRoomKeyReservations();
            for (int indeksi = 0; indeksi < avainVaraukset.length; indeksi++){
                if (avainVaraukset[indeksi] == this.roomKeyReservation){
                }
                else if (avainVaraukset[indeksi].getTargetRoom() == this.roomKeyReservation.getTargetRoom()) {
                    if (avainVaraukset[indeksi].getTimeSlice().contains(kohdeaika.getTime())) {
                        if ((this.onkoAlku && kohdeaika.getTime().before(avainVaraukset[indeksi].getTimeSlice().getEndDate())) ||
                                (!(this.onkoAlku) && kohdeaika.getTime().after(avainVaraukset[indeksi].getTimeSlice().getStartDate()))) {
                            this.roomKeyReservation.getSession().setStatusMessage("Avainvarauksia ei voi laittaa p��llekk�in!");
                            return;
                        }
                    }
                    else {
                    }
                }
            }
            
            TimeSlice uusiTimeSlice;
            if (this.onkoAlku){
                uusiTimeSlice = new TimeSlice(kohdeaika.getTime(), this.roomKeyReservation.getTimeSlice().getEndDate());
            } 
            else {
                uusiTimeSlice = new TimeSlice(this.roomKeyReservation.getTimeSlice().getStartDate(), kohdeaika.getTime());
            }
            this.roomKeyReservation.setTimeSlice(uusiTimeSlice);
            this.roomKeyReservation.getSession().modifyRoomKeyReservation(roomKeyReservation);
            this.roomKeyReservation.getSession().setStatusMessage("Huonevarauksen aikav�li� muutettu");
            // db- ja sessiomuutokset
            this.setTargetDate(kohdeaika.getTime());
            this.setText(updateAikaTeksti(this.getTargetDate()));
        }
    }
    
    private static String updateAikaTeksti(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String uusiTeksti = calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) +1) + "." + calendar.get(Calendar.YEAR);
        return uusiTeksti;
    }
}
