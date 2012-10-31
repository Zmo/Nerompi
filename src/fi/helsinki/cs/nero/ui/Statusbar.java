/*
 * Created on 27.11.2004
 *
 */
package fi.helsinki.cs.nero.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import fi.helsinki.cs.nero.event.NeroObserver;
import fi.helsinki.cs.nero.event.NeroObserverTypes;
import fi.helsinki.cs.nero.logic.Session;

/**
 * Yksinkertainen viestilaatikko, joka lukee viestin Observerin niin
 * halutessa ja n‰ytt‰‰ sen laatikossaan.
 * @author Timi
 */
public class Statusbar extends JPanel implements NeroObserver {

    private JLabel label;
    
    private Session session;

    /**
     * Konstruktori
     * @param session sessio-luokka
     * @param width toivottu leveys
     */
    public Statusbar(Session session, int width) {
        super(new BorderLayout());
        this.session = session;
        label = new JLabel();
        add(label, BorderLayout.CENTER);
        setBorder(new BevelBorder(BevelBorder.LOWERED));
        setMaximumSize(new Dimension(width, 20));
        label.setPreferredSize(new Dimension(width - 4, 18));

        session.registerObserver(NeroObserverTypes.STATUSBAR, this);
    }

    /* (non-Javadoc)
     * @see fi.helsinki.cs.nero.event.NeroObserver#updateObserved(int)
     */
    public void updateObserved(int type) {
        label.setText(session.getStatusMessage());
    }
}
