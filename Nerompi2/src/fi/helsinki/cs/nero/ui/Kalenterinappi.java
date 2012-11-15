/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import java.beans.PropertyChangeEvent;
import java.util.Date;
import org.sourceforge.jcalendarbutton.*;

/**
 *
 * @author rkolagus
 */
public class Kalenterinappi extends JCalendarButton {

    private static String[] kuulyhenteet = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private TimelineElement element;
    private boolean onkoAlku;

    public Kalenterinappi(Date dateTarget) {
        super(dateTarget);
        this.setIcon(null);
        this.asetaAikaTeksti();
    }

    public Kalenterinappi(Date dateTarget, TimelineElement element, boolean onkoAlku) {
        this(dateTarget);
        this.element = element;
        this.onkoAlku = onkoAlku;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equalsIgnoreCase("date")) {
            Date tulos;
            tulos = this.parseAika(evt);
            if ((this.onkoAlku && this.element.onkoAlkuEnnenLoppua(tulos, this.element.getLoppukalenteri().getTargetDate()))
                    || (!(this.onkoAlku) && this.element.onkoAlkuEnnenLoppua(this.element.getAlkukalenteri().getTargetDate(), tulos))) {
                this.setTargetDate(tulos);
                this.asetaAikaTeksti();
                System.out.println("Muutettu kohde: " + this.getTargetDate() + "\n - - Paivavalinta muuttui - -");
                
                /* AIKATARKASTUKSIA - TYÖN ALLA*/
                /*for (int a = 0; a < this.element.getReservation().getReservingPerson().getReservations().length; a++) {
                    if (this.element.getReservation().getReservingPerson().getReservations()[a].getReservationID().equals(this.element.getReservation().getReservationID())) {
                    } else {
                        if (this.onkoAlku && this.element.getReservation().getReservingPerson().getReservations()[a].getTimeSlice().getEndDate().after(tulos)) {
                            tulos = this.element.getReservation().getReservingPerson().getReservations()[a].getTimeSlice().getEndDate();
                        }
                    }


                }*/
                /* /AIKATARKASTUKSIA */
                this.element.storeToDB();
            }
            else {
                System.out.println("VIRHE - TimelineElement - alkamispäivä yritetty siirtää loppumispäivän jälkeen.");
                
            }
        }
    }

    private Date parseAika(PropertyChangeEvent evt) {
        Date aika;
        aika = this.getTargetDate();
        try {
            aika.setHours(12);
            aika.setDate(Integer.parseInt(evt.getNewValue().toString().substring(8, 10)));
            int kuunumero = -1;
            for (int i = 0; i < kuulyhenteet.length && (kuunumero == -1); i++) {
                if (kuulyhenteet[i].equalsIgnoreCase(evt.getNewValue().toString().substring(4, 7))) {
                    kuunumero = i;
                }
            }
            aika.setMonth(kuunumero);
            aika.setYear(Integer.parseInt(evt.getNewValue().toString().substring(24, evt.getNewValue().toString().length())) - 1900);
            return aika;
        } catch (Exception e) {
            System.out.println(" - VIRHE - Kalenterinappi.propertyChange: " + e);
            return this.getTargetDate();
        }
    }

    public void asetaAikaTeksti() {
        this.setText(this.getTargetDate().getDate() + "."
                + (1 + this.getTargetDate().getMonth()) + "."
                + (1900 + this.getTargetDate().getYear()));
    }
}
