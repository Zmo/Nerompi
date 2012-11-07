package fi.helsinki.cs.nero.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.TransferHandler;

import fi.helsinki.cs.nero.NeroApplication;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.logic.Session;

/**
 * Luokka esittää työpisteen tiedot = headerin.
 */
public class PostLabel extends TimelineElement{
    
    /**
     * Työpiste jonka tiedot esitetään.
     */
    private Post post;
    
    /**
     * Työpisteen ikoni.
     */
    private static final ImageIcon COMPUTER;
    
    static {
        COMPUTER = new ImageIcon(NeroApplication.getProperty("img_computer"));
    }
    
    /**
     * 
     * Konstuktori.
     * @param session Viite sessioon.
     * @param post Työpiste jonka tiedot esitetään.
     */
    public PostLabel(Session session, Post post) {

        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.post = post;
        this.session = session;
        
        MouseListener listener = new DragMouseAdapter();
        this.addMouseListener(listener);
        this.addMouseMotionListener((MouseMotionListener) listener);
        this.setTransferHandler(new TransferHandler("texti"));
        this.setBackground(Color.yellow);
        
        //Lisätään postin ikoni.
        JLabel icon = new JLabel(COMPUTER);
        icon.setMinimumSize(new Dimension(20, 20));
        icon.setPreferredSize(new Dimension(20, 20));
        icon.setMaximumSize(new Dimension(20, 20));
        
        this.add(icon);
        
        JLabel postLabel = new JLabel(this.post.toString());
        this.add(postLabel);
   
    }
    
    /**
     * <p>
     * Palauttaa viitteen tähän olioon, liittyy
     * käytettyyn Javan Drag&Drop tukeen.
     * </p>
     */
    public TimelineElement getTexti() {
        return this;
    }
    
    /**
     * <code>
     * Käsittelee tämän elementin päälle tulevat drop tapahtumat. 
     * Liittyy käytettyyn Javan Drag&Drop tukeen.
     * </code>
     * @param element Elementti joka pudotettiin tämän elementin päälle.
     */
    public void setTexti(TimelineElement element) {
           
    		//päälle vedettiin työsopimus, luodaan siihen sopiva varaus
        if(element instanceof UIContract) {
            UIContract contract = (UIContract)element;
            session.createReservation(this.post, contract.getContract());
            return;
        }

        // päälle vedettiin tyhjä elementti, luodaan koko slicen mittainen varaus
        if(element instanceof UIEmpty) {
            UIEmpty empty = (UIEmpty)element;
            session.createReservation(this.post, empty.getPerson(), element.getTimeSlice());
            return;
        }
    }
    
    /**
     * Palauttaa työpisteen jonka tiedot esitetään.
     * @return Viite <code>Post</code>-olioon.
     */
    public Post getPost() {
        return this.post;
    }
    
}
