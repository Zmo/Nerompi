/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Reservation;
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
        this.alustaViimeAika();
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
            tulos.setHours(0);
            if (((this.onkoAlku && (tulos.before(this.element.getLoppukalenteri().getTargetDate())))
                    || (!(this.onkoAlku) && (tulos.after(this.element.getAlkukalenteri().getTargetDate()))))
                    && ((this.lapikayntiVertailu(this.element.getReservation().getReservingPerson().getReservations(), tulos)))) {
                this.hyvaksyAikaMuutos(tulos);
            }
            else {
                this.palautaViimeAika();
                System.out.println("VIRHE - TimelineElement - alkamisp‰iv‰ yritetty siirt‰‰ loppumisp‰iv‰n j‰lkeen.");
            }
        }
    }
    
    private boolean lapikayntiVertailu(Reservation[] saadutVaraukset, Date kohde) {
        for (int a = 0; a < saadutVaraukset.length; a++) {
            System.out.println(saadutVaraukset[a].getTimeSlice().getStartDate() + " - "
                    + saadutVaraukset[a].getTimeSlice().getEndDate());
            if (saadutVaraukset[a].getReservationID().equals(this.element.getReservation().getReservationID())) {
            } else {
                TimeSlice vertausaikavali = saadutVaraukset[a].getTimeSlice();
                if ((this.onkoAlku && vertausaikavali.getEndDate().after(kohde) && this.element.getTimeSlice().getEndDate().after(vertausaikavali.getEndDate()))
                        || (!(this.onkoAlku) && vertausaikavali.getStartDate().before(kohde) && this.element.getTimeSlice().getStartDate().before(vertausaikavali.getStartDate()))) {
                    this.palautaViimeAika();
                    return false;
                }
            }
        }
        return true;
    }
    
    private void hyvaksyAikaMuutos(Date muutos){
                this.setTargetDate(muutos);
                this.setAikaTeksti();
                this.alustaViimeAika();
                System.out.println("Muutettu kohde: " + this.getTargetDate() + "\n - - Paivavalinta muuttui - -");
                this.element.storeToDB();
    }
    
    private void alustaViimeAika(){
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
        this.setAikaTeksti();
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
