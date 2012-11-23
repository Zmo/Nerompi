
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
import fi.helsinki.cs.nero.logic.Session;

/**
 * Esitt�� huoneen avainvarausten tiedot eli avainvarausten headerin
 * 
 * @author Simo
 */
public class RoomKeyReservationLabel extends TimelineElement {
    
    /**Kohdehuone*/
    private Room room;
    
    /**Avaimen ikoni*/
    private static final ImageIcon KEY;
    
    static {
        KEY = new ImageIcon(NeroApplication.getProperty("img_key"));
    }
    
    /**
     * 
     * Konstuktori.
     * @param session Viite sessioon.
     * @param post Ty�piste jonka tiedot esitet��n.
     */
    public RoomKeyReservationLabel(Session session, Room room) {

        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.session = session;
        
        MouseListener listener = new DragMouseAdapter();
        this.addMouseListener(listener);
        this.addMouseMotionListener((MouseMotionListener) listener);
        this.setTransferHandler(new TransferHandler("texti"));
        this.setBackground(Color.yellow);
        
        // Lis�t��n avainikoni
        JLabel icon = new JLabel(KEY);
        icon.setMinimumSize(new Dimension(40, 20));
        icon.setPreferredSize(new Dimension(40, 20));
        icon.setMaximumSize(new Dimension(40, 20));
        
        this.add(icon);
        
        JLabel roomname = new JLabel("Avainavaraukset huoneeseen "+this.session.getActiveRoom().getRoomName());
        this.add(roomname);       
        
        //JLabel postLabel = new JLabel(this.room.toString());
        //this.add(postLabel);
   
    }
    
    /**
     * <p>
     * Palauttaa viitteen t�h�n olioon, liittyy
     * k�ytettyyn Javan Drag&Drop tukeen.
     * </p>
     */
    public TimelineElement getTexti() {
        return this;
    }
    
    /**
     * <code>
     * K�sittelee t�m�n elementin p��lle tulevat drop tapahtumat. 
     * Liittyy k�ytettyyn Javan Drag&Drop tukeen.
     * </code>
     * @param element Elementti joka pudotettiin t�m�n elementin p��lle.
     */
    public void setTexti(TimelineElement element) {
    
        //p��lle vedettiin ty�sopimus, luodaan varaus
        if(element instanceof UIContract) {
            UIContract contract = (UIContract)element;
            session.addRoomKeyReservation(contract.getContract().getPerson(), element.getTimeSlice());
            return;
        }

        // p��lle vedettiin tyhj� elementti, luodaan varaus
        if(element instanceof UIEmpty) {
            UIEmpty empty = (UIEmpty)element;
            session.addRoomKeyReservation(empty.getPerson(), element.getTimeSlice());
            return;
        }
    }
    
    /**
     * Palauttaa ty�pisteen jonka tiedot esitet��n.
     * @return Viite <code>Post</code>-olioon.
     */
    public Room getRoom() {
        return this.room;
    }
    
}
