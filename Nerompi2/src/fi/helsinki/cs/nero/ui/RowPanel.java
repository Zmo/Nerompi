package fi.helsinki.cs.nero.ui;

import java.awt.Dimension;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * Muinaisj‰‰nne, pit‰isi poistaa jos aikaa olisi.
 */
public class RowPanel extends JPanel {
   
    /**
     * Rivin elementit.
     */
    private LinkedList elements;
    
    /**
     * Konstruktori.
     * @param length Rivin pituus.
     */
    public RowPanel(int length) {
        
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS)); 
        this.setMinimumSize(new Dimension(length, 20));
        this.setPreferredSize(new Dimension(length, 20));
        this.setMaximumSize(new Dimension(length, 20));
        
        this.elements = new LinkedList();
    }  
}
