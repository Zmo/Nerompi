/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author rkolagus
 */
public class UusiAvainvarausListener implements MouseListener {
    private UusiAvainvarausNappi avainNappi;
    
    public UusiAvainvarausListener(UusiAvainvarausNappi avainNappi){
        super();
        this.avainNappi = avainNappi;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.avainNappi.lisaaAvainVaraus();
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
    
}
