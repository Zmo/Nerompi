/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.data.TimeSlice;
import java.beans.PropertyChangeEvent;
import java.util.Calendar;
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
            Calendar tulos = Calendar.getInstance();
            tulos.setTime(this.parseAika(evt.getNewValue().toString(), this.getTargetDate()));
            tulos.set(tulos.HOUR_OF_DAY, 0);
            // Tuloksen tunti nollaksi!
            if (((this.onkoAlku && (tulos.getTime().before(this.element.getLoppukalenteri().getTargetDate())))
                    || (!(this.onkoAlku) && (tulos.getTime().after(this.element.getAlkukalenteri().getTargetDate()))))
                    && ((this.lapikayntiVertailu(this.element.getReservation().getReservingPerson().getReservations(), tulos.getTime())))) {
                this.hyvaksyAikaMuutos(tulos);
            }
            else {
                this.palautaViimeAika();
                this.element.session.setStatusMessage("Varauksia ei voi laittaa p‰‰llekk‰in!");
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
    
    private void hyvaksyAikaMuutos(Calendar muutos){
                this.setTargetDate(muutos.getTime());
                this.setAikaTeksti();
                this.alustaViimeAika();
                System.out.println("Muutettu kohde: " + this.getTargetDate() + "\n - - Paivavalinta muuttui - -");
                this.element.storeToDB();
    }
    
    private void alustaViimeAika(){
        Calendar kopio = Calendar.getInstance();
        kopio.setTime(this.getTargetDate());
        this.viimeAika[0] = kopio.get(kopio.DAY_OF_MONTH);
        this.viimeAika[1] = kopio.get(kopio.MONTH);
        this.viimeAika[2] = kopio.get(kopio.YEAR);
    }
    
    private void palautaViimeAika(){
        Calendar korjaus = Calendar.getInstance();
        korjaus.setTime(this.getTargetDate());
        korjaus.set(this.viimeAika[2], this.viimeAika[1], this.viimeAika[0]);
        this.setTargetDate(korjaus.getTime());
        this.setAikaTeksti();
    }
    
    private Date parseAika(String evt, Date date) {
        Calendar aika = Calendar.getInstance();
        aika.setTime(date);
        try {
            aika.set(aika.HOUR_OF_DAY, 0);
            int kuunumero = -1;
            for (int i = 0; i < kuulyhenteet.length && (kuunumero == -1); i++) {
                if (kuulyhenteet[i].equalsIgnoreCase(evt.substring(4, 7))) {
                    kuunumero = i;
                }
            }
            aika.set(Integer.parseInt(evt.substring(evt.length()-4, evt.length())), kuunumero, Integer.parseInt(evt.substring(8, 10)));
            return aika.getTime();
        } catch (Exception e) {
            System.out.println(" - VIRHE - Kalenterinappi.parseAika: " + e);
            return date;
        }
    }

    private void setAikaTeksti() {
        Calendar tuloste = Calendar.getInstance();
        tuloste.setTime(this.getTargetDate());
        this.setText(tuloste.get(Calendar.DAY_OF_MONTH) + "." + 
                    (tuloste.get(Calendar.MONTH)+1) + "." + 
                     tuloste.get(Calendar.YEAR));
    }
}
