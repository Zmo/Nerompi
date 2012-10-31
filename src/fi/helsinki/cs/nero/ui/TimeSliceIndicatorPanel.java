/*
 * Created on 25.11.2004
 *
 */
package fi.helsinki.cs.nero.ui;

import java.awt.Color;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import fi.helsinki.cs.nero.data.TimeSlice;
import fi.helsinki.cs.nero.event.NeroObserver;
import fi.helsinki.cs.nero.event.NeroObserverTypes;
import fi.helsinki.cs.nero.logic.Session;

/**
 * Komponentti, joka toteuttaa aikajakson reunat osoittavat pystyviivat.
 * Asetetaan muiden komponenttien p‰‰lle.
 * @author ozone
 */
public class TimeSliceIndicatorPanel extends JPanel implements NeroObserver {
    /**
     * Apupaneeli, joka on l‰pin‰kyv‰, mutta jonka reunat ovat pystyviivoja.
     * Venytet‰‰n siten, ett‰ peitt‰‰ valitun osa-aikav‰lin.
     */
    private JPanel indicator;
    
    /**
     * Sovelluslogiikan sessio.
     */
    private Session session;
    
    /**
     * Viivan yl‰reunan et‰isyys komponentin yl‰reunasta.
     */
    private static final int TOP = 30;
    
    private int maxWidth = 10;
    
    /**
     * Konstruktori, joka luo aikaindikaattorikomponentin.
     * @param session sovelluslogiikan sessio
     */
    
	public TimeSliceIndicatorPanel(Session session) {
		super();
        setOpaque(false);
        setLayout(null);
        this.session = session;

        indicator = new JPanel();
        indicator.setOpaque(false);
        indicator.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.GRAY));
        add(indicator);
        indicator.setBounds(JMultiSlider.SIDE_WIDTH, TOP,
                maxWidth,
                NeroUI.HEIGHT - TOP);
        session.registerObserver(NeroObserverTypes.TIMESCALE, this);
        session.registerObserver(NeroObserverTypes.TIMESCALESLICE, this);
        session.registerObserver(NeroObserverTypes.TIMESCALESLICEUPDATING, this);
    }

    /**
     * Palauttaa p‰iv‰m‰‰r‰n sijainnin aikajanalla, jolla on tietty
     * kokonaisleveys. Sijainti annetaan et‰isyyten‰ vasemmasta reunasta,
     * eli arvo on v‰lill‰ 0...(totalWidth-1).
     * @param totalWidth aikajanan kokonaisleveys
     * @param timescale aikojen vaihteluv‰li
     * @param date p‰iv‰m‰‰r‰ jonka sijainti halutaan
     * @return p‰iv‰m‰‰r‰n et‰isyys vasemmasta reunasta
     * @throws IllegalArgumentException jos p‰iv‰m‰‰r‰ ei kuulu vaihteluv‰liin
     */
    private int datePosition(int totalWidth, TimeSlice timescale, Date date) {
        assert(timescale.contains(date));
        long daysFromStart =
            (date.getTime() - timescale.getStartDate().getTime())
            / TimeSlice.ONEDAY;
        return Math.round(daysFromStart * totalWidth / timescale.length());
    }
    
	/* (non-Javadoc)
	 * @see fi.helsinki.cs.nero.event.NeroObserver#updateObserved(int)
	 */
	public void updateObserved(int type) {
		assert(type == NeroObserverTypes.TIMESCALE 
                || type == NeroObserverTypes.TIMESCALESLICE
                || type == NeroObserverTypes.TIMESCALESLICEUPDATING);
        TimeSlice timescale = session.getFilterTimescale();
        TimeSlice timescaleSlice = session.getTimeScaleSlice();
        int left = datePosition(maxWidth,
                timescale, timescaleSlice.getStartDate());
        int right = datePosition(maxWidth,
                timescale, timescaleSlice.getEndDate());
        
        indicator.setBounds(left + JMultiSlider.SIDE_WIDTH, TOP,
                right-left+1, NeroUI.HEIGHT - TOP);
	}

    /**
     * @param width
     */
    public void setMaxWidth(int width) {
        maxWidth = width;
        updateObserved(NeroObserverTypes.TIMESCALE);

    }
}
