/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.TimeSlice;
import java.beans.PropertyChangeEvent;
import java.util.Date;
import org.sourceforge.jcalendarbutton.*;

/**
 *
 * @author rkolagus
 */
public class Kalenterinappi extends JCalendarButton {

    private static final String[] kuulyhenteet = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private TimelineElement element;
    private boolean onkoAlku;
    private int[] viimeAika;

    public Kalenterinappi(Date dateTarget) {
        super(dateTarget);
        this.setIcon(null);
        this.setAikaTeksti();
        this.viimeAika = new int[3];
        this.setViimeAika();
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
            tulos = this.parseAika(evt.getNewValue().toString(), this.getTargetDate());
            if ((this.onkoAlku && (tulos.before(this.element.getLoppukalenteri().getTargetDate())))
                    || (!(this.onkoAlku) && (tulos.after(this.element.getAlkukalenteri().getTargetDate())))) {
                
//                /* AIKATARKASTUKSIA - TYÖN ALLA*/
//                for (int a = 0; a < this.element.getReservation().getReservingPerson()/*getTargetPost()*/.getReservations().length; a++) {
//                    System.out.println(this.element.getReservation().getReservingPerson().getReservations()[a].getTimeSlice().getStartDate() + " - "
//                            +  this.element.getReservation().getReservingPerson().getReservations()[a].getTimeSlice().getEndDate());
//                    if (this.element.getReservation().getReservingPerson()/*getTargetPost()*/.getReservations()[a].getReservationID().equals(this.element.getReservation().getReservationID())) {
//                    } else {
//                        
//                        TimeSlice vertausaikavali = this.element.getReservation().getReservingPerson().getReservations()[a].getTimeSlice();
//                        if (this.onkoAlku && vertausaikavali.getEndDate().after(tulos)) {
//                            System.out.println(tulos);
//                            System.out.println("PAIKKA 1");
//                            tulos = vertausaikavali.getEndDate();
//                        }
//                        else if (vertausaikavali.getStartDate().before(tulos)){
//                            System.out.println(tulos);
//                            System.out.println("PAIKKA 2");
//                            tulos = vertausaikavali.getStartDate();
//                        }
//                    }
//                }
//                /* /AIKATARKASTUKSIA */
                this.setTargetDate(tulos);
                this.setAikaTeksti();
                this.setViimeAika();
                System.out.println("Muutettu kohde: " + this.getTargetDate() + "\n - - Paivavalinta muuttui - -");
                this.element.storeToDB();

            }
            else {
                this.palautaViimeAika();
                System.out.println("VIRHE - TimelineElement - alkamispäivä yritetty siirtää loppumispäivän jälkeen.");
                // + Virhevalintakorjaus
            }
        }
    }
    
    private void setViimeAika(){
        this.viimeAika[0] = this.getTargetDate().getDate();
        this.viimeAika[1] = this.getTargetDate().getMonth();
        this.viimeAika[2] = this.getTargetDate().getYear();
    }
    
    private void palautaViimeAika(){
        Date korjaus = this.getTargetDate();
        korjaus.setDate(this.viimeAika[0]);
        korjaus.setMonth(this.viimeAika[1]);
        korjaus.setYear(this.viimeAika[2]);
        this.setTargetDate(korjaus);
    }
    
    private Date parseAika(String evt, Date date) {
        Date aika;
        aika = date;
        try {
            aika.setHours(12);
            aika.setDate(Integer.parseInt(evt.substring(8, 10)));
            int kuunumero = -1;
            for (int i = 0; i < kuulyhenteet.length && (kuunumero == -1); i++) {
                if (kuulyhenteet[i].equalsIgnoreCase(evt.substring(4, 7))) {
                    kuunumero = i;
                }
            }
            aika.setMonth(kuunumero);
            aika.setYear(Integer.parseInt(evt.substring(evt.length()-4, evt.length())) - 1900);
            return aika;
        } catch (Exception e) {
            System.out.println(" - VIRHE - Kalenterinappi.parseAika: " + e);
            return date;
        }
    }

    private void setAikaTeksti() {
        this.setText(this.getTargetDate().getDate() + "."
                + (1 + this.getTargetDate().getMonth()) + "."
                + (1900 + this.getTargetDate().getYear()));
    }
}
