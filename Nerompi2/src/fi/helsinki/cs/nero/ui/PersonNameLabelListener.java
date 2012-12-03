/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author ssinisal
 */
public class PersonNameLabelListener implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent arg0) {
        if(arg0.getSource() instanceof PersonNameLabel){
         	PersonNameLabel pl = (PersonNameLabel)arg0.getSource();
         	new PersonInfoFrame(pl.getSession(), pl.getPerson());
         }
    }
    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
    
}
