package fi.helsinki.cs.nero.ui;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.border.BevelBorder;

import fi.helsinki.cs.nero.NeroApplication;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.logic.Session;

/**
 * Toteuttaa Neron roskalaatikon.
 */
public class TrashCan extends JPanel{
    
    /**
     * Viite sessioon.
     */
    private Session sessio = null;
    
    /**
     * Roskalaatikon kuva.
     */
    private static ImageIcon TRASH_CAN;
    
    static {
        TRASH_CAN = new ImageIcon(NeroApplication.getProperty("img_trashcan"));
    } 

    /**
     * 
     * Konstruktori.
     * @param sessio Viite sessioon.
     */
    public TrashCan(Session sessio) {
        super(new FlowLayout(FlowLayout.CENTER));
        this.sessio = sessio;
        
        this.setTransferHandler(new TransferHandler("texti"));
        
        JLabel trashCan = new JLabel(TRASH_CAN);
        trashCan.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        this.add(trashCan);
    }
    
    /**
     * Liittyy k‰ytettyyn Javan Drag&Drop tukeen.
     * @return <code>null</code aina, koska roskista ei voi vet‰‰ minnekk‰‰n.
     */
    public TimelineElement getTexti() {
        return null;
    }
   
    /**
     * <p>
     * K‰sittelee roskiksen p‰‰lle vedetyt elementit,
     * liittyy k‰ytettyyn Drag&Drop tukeen.
     * </p>
     * @param element Elementti joka vedettiin t‰m‰n p‰‰lle.
     */
    public void setTexti(TimelineElement element) {
    	// j‰tet‰‰n printtailu yms session huoleksi
    	// vain varaukset ja tyˆpisteet kiinnostavat
    	// muita mahdollisia olisi esim UIContract, UIEmpty

        // PostLabel, yritet‰‰n siis poistaa tyˆpiste
        if(element instanceof PostLabel) {
            Post post = ((PostLabel)element).getPost();
            sessio.deletePost(post);
            return;
        }
        // UIReservation, yritet‰‰n siis poistaa varaus
        if(element instanceof UIReservation) {
            Reservation reservation = ((UIReservation)element).getReservation();
            this.sessio.deleteReservation(reservation);
            return;
        }
    }    

}


