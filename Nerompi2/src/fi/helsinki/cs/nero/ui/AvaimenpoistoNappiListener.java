/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rkolagus
 */
public class AvaimenpoistoNappiListener implements MouseListener {
    AvaimenpoistoNappi nappi;
    public AvaimenpoistoNappiListener(AvaimenpoistoNappi nappi){
        this.nappi = nappi;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            nappi.poistaAvain();
        } catch (SQLException ex) {
            Logger.getLogger(AvaimenpoistoNappiListener.class.getName()).log(Level.SEVERE, null, ex);
        }
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
