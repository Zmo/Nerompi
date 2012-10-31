/*
 * Created on 28.11.2004
 */
package fi.helsinki.cs.nero.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

/**
 * @author Ville Sundberg
 */
public class PhoneLabelListener implements MouseListener {

    private JFrame mainFrame;
    
    public PhoneLabelListener(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
    
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent arg0) {
         if(arg0.getSource() instanceof PhoneLabel){
         	PhoneLabel pl = (PhoneLabel)arg0.getSource();
         	new PhonenumberFrame(pl.getSession(), mainFrame, pl.getPost());
         }
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent arg0) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
	}

}
