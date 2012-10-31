/*
 * Created on 21.11.2004
 *
 */
package fi.helsinki.cs.nero.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fi.helsinki.cs.nero.data.TimeSlice;
import fi.helsinki.cs.nero.event.NeroObserver;
import fi.helsinki.cs.nero.event.NeroObserverTypes;
import fi.helsinki.cs.nero.logic.Session;

/**
 * Apuluokka JMultiSliderin ja k‰yttˆliittym‰n v‰lille, joka muuttaa
 * Sliderin koot sopiviksi Observerin niin vaatiessa ja v‰litt‰‰ Sliderin
 * k‰ytˆn Sessionille takaisin. N‰ytt‰‰ lis‰ksi s‰‰timien m‰‰ritt‰m‰t p‰iv‰ykset.
 * @author Timi
 * 
 */
public class DateSlider extends JPanel implements NeroObserver {

    private Session session;
    
    private TimeSlice timeScale;
    
    private JMultiSlider slider;
    
    private JLabel leftDateLabel, rightDateLabel;
    
    private JPanel p, dummyPanel;

    private DateFormat formatter;

    /**
     * Kuuntelijaluokka, joka v‰litt‰‰ Sliderin k‰ytˆn Sessionille
     * @author Timi
     *
     */
    private class DateChangeListener implements ChangeListener {

        /**
         * Konstruktori joka luo kuuntelijan kuuntelemaan Sliderin k‰yttˆ‰
         */
        public DateChangeListener() {
        }
        
        /* (non-Javadoc)
         * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
         */
        public void stateChanged(ChangeEvent e) {
			JMultiSlider slider = (JMultiSlider)e.getSource();
			leftDateLabel.setText(formatter.format(getDateLeft() ) );
			rightDateLabel.setText(formatter.format(getDateRight() ) );
			
			// poistutaan mik‰li sliderin muuttaminen on meneill‰‰n

			TimeSlice ts = new TimeSlice(getDateLeft(), getDateRight());
			if (slider.getValueIsAdjusting())
			    session.setTimeScaleSlice(ts, true);
			else
			    session.setTimeScaleSlice(ts, false);
        }
    }
    
    /**
     * Konstruktori, joka alustaa muuttujat ja v‰litt‰‰ sessionin EventListenerille.
     * Ilmoittaa itsens‰ myˆs Sessionin Observerille.
     * @param session viite sessioniin
     */
    public DateSlider(Session session, int width) {
        super(new BorderLayout());
        
        p = new JPanel(new BorderLayout());
        dummyPanel = new JPanel();
        
        this.session = session;
        
        slider = new JMultiSlider(0,1);

        formatter = DateFormat.getDateInstance(
		        DateFormat.SHORT, Locale.getDefault());
        
        slider.setPreferredSize(new Dimension(width, 20));
        session.registerObserver(NeroObserverTypes.TIMESCALE, this);
        updateObserved(NeroObserverTypes.TIMESCALE);

        leftDateLabel = new JLabel(formatter.format(getDateLeft() ) );
        rightDateLabel = new JLabel(formatter.format(getDateRight() ) );

        p.add(leftDateLabel, BorderLayout.WEST);
        p.add(rightDateLabel, BorderLayout.EAST);
        p.add(slider, BorderLayout.SOUTH);

        add(p, BorderLayout.CENTER);
        add(dummyPanel, BorderLayout.EAST);
        
        DateChangeListener dateChangeListener = new DateChangeListener();
		slider.addChangeListener(dateChangeListener);
    }
    
    /**
     * Muuttaa p‰iv‰ykset ja laskee montako p‰iv‰‰ niiden v‰liss‰ on ja tekee
     * Sliderista oikein mittaisen. P‰ivitt‰‰ samalla s‰‰timet oikeisiin kohtiin. 
     * @param startDate alkup‰iv‰ys (kellonaika on syyt‰ olla 0:00)
     * @param endDate loppup‰iv‰ys (kellonaika on syyt‰ olla 0:00)
     */
    private void setDates(TimeSlice timeScale, TimeSlice timeScaleSlice) {
        long maxVal = (timeScale.getEndDate().getTime() -
        		timeScale.getStartDate().getTime()) / TimeSlice.ONEDAY;
        long leftPos = (timeScaleSlice.getStartDate().getTime() -
        		timeScale.getStartDate().getTime()) / TimeSlice.ONEDAY;
        long rightPos = (timeScaleSlice.getEndDate().getTime() -
        		timeScale.getStartDate().getTime()) / TimeSlice.ONEDAY;
        
        this.timeScale = timeScale;
        slider.setValuesAndRange((int)leftPos, (int)rightPos, 0, (int)maxVal);
    }

    /**
     * Palauttaa vasemman s‰‰timen kellonajan.
     * @return vasemman s‰‰timen kellonaika
     */
    private Date getDateLeft() {
        GregorianCalendar startCal = new GregorianCalendar();
        startCal.setTime(timeScale.getStartDate());
        GregorianCalendar c = new GregorianCalendar(
                startCal.get(Calendar.YEAR),
                startCal.get(Calendar.MONTH),
                startCal.get(Calendar.DAY_OF_MONTH) + slider.getValueLeft());

        return c.getTime();
    }

    /**
     * Palauttaa oikean s‰‰timen kellonajan.
     * @return oikean s‰‰timen kellonaika
     */
    private Date getDateRight() {
        GregorianCalendar startCal = new GregorianCalendar();
        startCal.setTime(timeScale.getStartDate());
        GregorianCalendar c = new GregorianCalendar(
                startCal.get(Calendar.YEAR),
                startCal.get(Calendar.MONTH),
                startCal.get(Calendar.DAY_OF_MONTH) + slider.getValueRight());

        return c.getTime();
    }

    /**
     * Asettaa leveyden sliderille, jotta se saadaan sopivaan kokoon
     * PersonScrollPanea varten
     * @param width leveys
     */
    public void setSliderWidth(int width) {
        int sliderWidth = width + JMultiSlider.SIDE_WIDTH * 2;
        slider.setPreferredSize(
                new Dimension(sliderWidth, 20));
        dummyPanel.setPreferredSize(new Dimension(
                NeroUI.RIGHT_WIDTH - sliderWidth, 20));
    }

    /* (non-Javadoc)
     * @see fi.helsinki.cs.nero.event.NeroObserver#updateObserved(int)
     */
    public void updateObserved(int type) {
        TimeSlice ts = session.getFilterTimescale();
        TimeSlice tss = session.getTimeScaleSlice();
        setDates(ts, tss);
        this.repaint();
    }

}
