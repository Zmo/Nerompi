/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Room;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author rkolagus
 */
public class UusiVarausListener implements MouseListener {
    
    UusiVarausNappi varausPopup;
    
    public UusiVarausListener(UusiVarausNappi popupMenu){
        super();
        this.varausPopup = popupMenu;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.varausPopup.naytaPopup(e);
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
