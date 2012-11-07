package fi.helsinki.cs.nero.ui;

import java.awt.Color;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.TransferHandler;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.TimeSlice;
import fi.helsinki.cs.nero.logic.Session;

/**
 * Esitt�� tyhj�n aikajakson.
 */
public class UIEmpty extends TimelineElement {
    
    /**
     * Henkil� johon tyhj� jakso viittaa, k�ytet��n PostLabel luokassa.
     */
    private Person person = null;
    private static Color BG_COLOR = new Color(240,240,240);
    private static Border BORDER = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    
   /**
    * Luo uuden tyhj�n varausjakson.
    * @param person Henkil� johon elementti liittyy.
    * @param timeSlice Aikav�li.
    * @param scale K�ytett�v� skaala = p�iv�/pikselit.
    * @param session Viite sessioon.
    */
    public UIEmpty(Person person, TimeSlice timeSlice, double scale, Session session) {
    		
        super(timeSlice, scale, BG_COLOR, session);

        this.person = person;
        
        MouseListener listener = new DragMouseAdapter();
        this.addMouseListener(listener);
        this.addMouseMotionListener((MouseMotionListener) listener);
        this.setTransferHandler(new TransferHandler("texti"));

        this.setBorder(BORDER);
    }
       
    /**
     * Tallentaa tiedot tietokantaan.
     */
    public void storeToDB() {}
    
    /**
     * Palauttaa henkil�n.
     * @return Viite <code>Person</code> olioon.
     */
    public Person getPerson() {
        return this.person;
    }
}
