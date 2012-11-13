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

    public Kalenterinappi(Date dateTarget) {
        super(dateTarget);
        this.setIcon(null);
        this.asetaAikaTeksti();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equalsIgnoreCase("date")) {
            if (evt.getNewValue() == (this.getTargetDate())) {
                System.out.println("Paiva ei muuttunut");
            } else {
                Date tulos = new Date();
                tulos = this.getTargetDate();
                try {
                    tulos.setHours(12);
                    tulos.setDate(Integer.parseInt(evt.getNewValue().toString().substring(8, 10)));
                    int kuunumero = -1;
                    for (int i = 0; i < kuulyhenteet.length && (kuunumero == -1); i++) {
                        if (kuulyhenteet[i].equalsIgnoreCase(evt.getNewValue().toString().substring(4, 7))) {
                            kuunumero = i;
                        }
                    }
                    tulos.setMonth(kuunumero);
                    tulos.setYear(Integer.parseInt(evt.getNewValue().toString().substring(24, evt.getNewValue().toString().length())) - 1900);
                } catch (Exception e) {
                    System.out.println(" - VIRHE - Kalenterinappi.propertyChange: " + e);
                }

                this.setTargetDate(tulos);

                System.out.println("Muutettu kohde: " + this.getTargetDate());


                this.asetaAikaTeksti();
                System.out.println(" - - Paivavalinta muuttui - -");
            }
        }
    }

    private void asetaAikaTeksti() {
        this.setText(this.getTargetDate().getDate() + "."
                + (1 + this.getTargetDate().getMonth()) + "."
                + (1900 + this.getTargetDate().getYear()));
    }
}
