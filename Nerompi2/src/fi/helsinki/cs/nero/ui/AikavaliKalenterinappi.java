/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import java.beans.PropertyChangeEvent;
import java.util.Date;

/**
 *
 * @author rkolagus
 */
public class AikavaliKalenterinappi extends Kalenterinappi {

    boolean onkoAlku;
    SearchPanel searchPanel;

    public AikavaliKalenterinappi(Date date, Boolean onkoAlku, SearchPanel searchPanel) {
        super(date);
        this.onkoAlku = onkoAlku;
        this.searchPanel = searchPanel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        /*if (((JFormattedTextField) arg0.getSource()).getDocument().getProperty("name").equals("startTime")) {
            if (((Date) ((JFormattedTextField) arg0.getSource()).getValue()).after(slice.getEndDate())) {
                slice.setStartDate((Date) ((JFormattedTextField) arg0.getSource()).getValue());
                calendar.setTime(slice.getStartDate());
                calendar.add(Calendar.MONTH, 1);
                slice.setEndDate(calendar.getTime());
                endTimeField.setValue(calendar.getTime());
                session.setFilterTimescale(slice);
            }
            if (((Date) startTimeField.getValue()).equals(slice.getStartDate())
                    && ((Date) endTimeField.getValue()).equals(slice.getEndDate())) {
                //System.out.println("ei turhaa päivitystä");				
            } else {
                slice.setStartDate((Date) ((JFormattedTextField) arg0.getSource()).getValue());
                session.setFilterTimescale(slice);
            }
        } else if (((JFormattedTextField) arg0.getSource()).getDocument().getProperty("name").equals("endTime")) {
            if (((Date) ((JFormattedTextField) arg0.getSource()).getValue()).before(slice.getStartDate())) {
                slice.setEndDate((Date) ((JFormattedTextField) arg0.getSource()).getValue());
                calendar.setTime(slice.getEndDate());
                calendar.add(Calendar.MONTH, -1);
                slice.setStartDate(calendar.getTime());
                startTimeField.setValue(calendar.getTime());
                session.setFilterTimescale(slice);

            }
            if (((Date) startTimeField.getValue()).equals(slice.getStartDate())
                    && ((Date) endTimeField.getValue()).equals(slice.getEndDate())) {
            } else {
                slice.setEndDate((Date) ((JFormattedTextField) arg0.getSource()).getValue());
                session.setFilterTimescale(slice);
            }
        }*/
    }
}
