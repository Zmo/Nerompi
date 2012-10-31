/*
 * Created on Oct 21, 2004
 */
package fi.helsinki.cs.nero.ui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

public class DragMouseAdapter extends MouseAdapter implements MouseMotionListener {
    
    boolean resize;
    int pressedX;
    int draggedX;
    int width;
    JPanel component;
    private String oldmsg;
    
    public void mouseDragged(MouseEvent e) {
       
        this.draggedX = e.getX();
                
        if(resize) {
            
            TimelineElement element = (TimelineElement)this.component;
                   
            if(pressedX <= width/2) { //Vedetään vasemmalta puolelta.
                element.session.changeCursorType(Cursor.W_RESIZE_CURSOR);
                	
                if(draggedX < 0) { //Koko kasvaa = Jos ollaan vedetty palkin vasemman reunan ylitse
                    element.fromLeftToLeft(-draggedX);
                }
                
                //Koko pienenee
                if(draggedX > 0) {        
                    element.fromLeftToRight(draggedX);
                } 
            }
            else { //Vedetään oikealta puolelta.
                element.session.changeCursorType(Cursor.E_RESIZE_CURSOR);
                
                //Koko kasvaa.
                if(draggedX >= element.getWidth()) { //Jos on yli palkin oikean reunan ja painetun oikealla puolen.
                    int muutos = draggedX-element.getWidth();         
                    element.fromRightToRight(muutos);
                }
                //Koko pienenee.
                if(draggedX < element.getWidth()) {
                    int muutos = element.getWidth()-draggedX;
                    element.fromRightToLeft(muutos);
               }   
            }
            // pidetään kursori oikeanlaisena kunnes draggailu loppuu
            element.session.setCursorLock(true);
        
            // jotain viestiä jonnekin
            if(this.oldmsg == null) {
                this.oldmsg = element.session.getStatusMessage();
            }
            element.session.setStatusMessageNoPrint("Varauksen uusi pituus "
            		+ element.getTimeSlice());
            this.component.revalidate();
        }
    }
    
	public void mouseMoved(MouseEvent e) {
		int pos_x = e.getX();
		TimelineElement elem = (TimelineElement)e.getComponent();
    		
		if(!elem.isResizable()) {
    			return;
		}
    	
        if(pos_x > elem.getWidth()-10) {
            elem.session.changeCursorType(Cursor.E_RESIZE_CURSOR);
        } else if(pos_x < 10) {
        		elem.session.changeCursorType(Cursor.W_RESIZE_CURSOR);
        } else {
        		elem.session.changeCursorType(Cursor.DEFAULT_CURSOR);
        }
    }
	
	public void mouseExited(MouseEvent e) {
		TimelineElement elem = (TimelineElement)e.getComponent();
		// NOTE kaikilla pitäisi kyllä olla session...
    		if(elem.session != null) {
    		    elem.session.setCursorLock(false);
    		    elem.session.changeCursorType(Cursor.DEFAULT_CURSOR);
    		}
	}
    
    public void mousePressed(MouseEvent e) {
    	
	    	if(e.getButton() == MouseEvent.BUTTON1) {
	
	        Component component = e.getComponent();

	        this.component=(JPanel)component;
	        
	        int width = component.getWidth();
	        this.width = width;
	      
	        this.pressedX = e.getX();
	        
	        TimelineElement element = null;
	        
	        try {
	            element = (TimelineElement)component;
	        }
	        catch (ClassCastException x) {
	            return; // taidettiin painaa roskista
	        }
	        
	        if(element.isResizable()) {
	
	        		// 10px reunoista
	            if(pressedX <= 10 || pressedX-width >= -10) {
	                this.resize = true;            
	            }
	            if(element.getWidth() < 10) {
	                this.resize = true;
	            }
	        }
	        
	        if(!this.resize) {
	            JComponent c = (JComponent)e.getSource();
	            TransferHandler handler = c.getTransferHandler();
	            handler.exportAsDrag(c, e, TransferHandler.COPY);
	        }
	    } else {
	        mouseRightClicked(e);
	    	}
    }
    
    public void mouseRightClicked(MouseEvent e){
	    	Component component = e.getComponent();
	    	TimelineElement element = (TimelineElement)component;
	    	element.setActiveRoom();
    }
    
    public void mouseReleased(MouseEvent e) {
        
        if(this.resize) {
            this.resize = false;
            
            TimelineElement element = (TimelineElement)this.component;
            // muutetaan kantaan vain jos pituus muuttui
            if(this.width != element.getWidth()) {
            	element.storeToDB();
            } else {
            	// ei muutettu, palautetaan wanha viesti
            	element.session.setStatusMessageNoPrint(this.oldmsg);
            }
        }
        this.oldmsg = null;
    }
}
