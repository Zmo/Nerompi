/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.NeroApplication;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.logic.Session;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.TransferHandler;

import fi.helsinki.cs.nero.NeroApplication;
import fi.helsinki.cs.nero.data.Room;
import fi.helsinki.cs.nero.data.RoomKeyReservation;
import fi.helsinki.cs.nero.logic.Session;

/**
 *
 * @author Zmo
 */
public class RoomKeyReservationLabel extends TimelineElement{

    private RoomKeyReservation roomkeyreservations;
    
    private Room room;
    
    /**
     * Avaimen ikoni.
     */
    private static final ImageIcon KEY;
    
    static {
        KEY = new ImageIcon(NeroApplication.getProperty("img_key"));
    }
    
    /**
     * 
     * Konstuktori.
     * @param session Viite sessioon.
     * @param post Tyˆpiste jonka tiedot esitet‰‰n.
     */
    public RoomKeyReservationLabel(Session session, Room room) {

        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.session = session;
        
        MouseListener listener = new DragMouseAdapter();
        this.addMouseListener(listener);
        this.addMouseMotionListener((MouseMotionListener) listener);
        this.setTransferHandler(new TransferHandler("texti"));
        this.setBackground(Color.yellow);
        
        //Lis‰t‰‰n postin ikoni.
        JLabel icon = new JLabel(KEY);
        icon.setMinimumSize(new Dimension(20, 20));
        icon.setPreferredSize(new Dimension(20, 20));
        icon.setMaximumSize(new Dimension(20, 20));
        
        this.add(icon);
        
        //JLabel postLabel = new JLabel(this.room.toString());
        //this.add(postLabel);
   
    }
    
    /**
     * <p>
     * Palauttaa viitteen t‰h‰n olioon, liittyy
     * k‰ytettyyn Javan Drag&Drop tukeen.
     * </p>
     */
    public TimelineElement getTexti() {
        return this;
    }
    
    /**
     * <code>
     * K‰sittelee t‰m‰n elementin p‰‰lle tulevat drop tapahtumat. 
     * Liittyy k‰ytettyyn Javan Drag&Drop tukeen.
     * </code>
     * @param element Elementti joka pudotettiin t‰m‰n elementin p‰‰lle.
     */
    public void setTexti(TimelineElement element) {
           
    	//p‰‰lle vedettiin tyˆsopimus, luodaan varaus
        if(element instanceof UIContract) {
            UIContract contract = (UIContract)element;
            session.addRoomKeyReservation(contract.getContract().getPerson(), element.getTimeSlice());
            return;
        }

        // p‰‰lle vedettiin tyhj‰ elementti, luodaan varaus
        if(element instanceof UIEmpty) {
            UIEmpty empty = (UIEmpty)element;
            session.addRoomKeyReservation(empty.getPerson(), element.getTimeSlice());
            return;
        }
    }
    
    /**
     * Palauttaa tyˆpisteen jonka tiedot esitet‰‰n.
     * @return Viite <code>Post</code>-olioon.
     */
    public Room getRoom() {
        return this.room;
    }
    
}
