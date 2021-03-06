
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.data.Room;
import fi.helsinki.cs.nero.data.TimeSlice;
import java.awt.event.MouseEvent;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
/**
 *
 * @author rkolagus
 */
public class UusiVarausNappi extends JButton {
    Person person;
    JPopupMenu popupMenu;
    Post[] posts;
    
    public UusiVarausNappi(Person person){
        this.setText("Uusi varaus");
        this.addMouseListener(new UusiVarausListener(this));
        this.person = person;
        this.popupMenu = new JPopupMenu("Uusi varaus");
    }
    
    public void naytaPopup(MouseEvent e){
        popupMenu = new JPopupMenu(); 
        if (this.person.getSession().getActiveRoom() == null) {
            this.person.getSession().setStatusMessageNoPrint("Klikkaa haluttua huonetta ensin!");
        } else {
            this.posts = this.person.getSession().getActiveRoom().getPosts();
            for (int a = 0; a < this.posts.length; a++) {
                JMenuItem menuTayte = new JMenuItem("" + this.posts[a]);
                menuTayte.addActionListener(new UusiVarausMenuItemListener(this, this.posts[a]));
                popupMenu.add(menuTayte);
            }
            this.popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    
    public void teeVaraus(Room room, Post post){
        Date alkamisPaiva = this.person.getSession().getTimeScaleSlice().getStartDate();
        Date loppumisPaiva = this.person.getSession().getTimeScaleSlice().getEndDate();
        
        for (Reservation reservation : this.person.getReservations()){
            if (reservation.getTimeSlice().overlaps(this.person.getSession().getTimeScaleSlice())){
                if (reservation.getTimeSlice().contains(alkamisPaiva)) {
                    alkamisPaiva = reservation.getTimeSlice().getEndDate();
                }
                if (reservation.getTimeSlice().contains(loppumisPaiva)){
                    loppumisPaiva = reservation.getTimeSlice().getStartDate();
                }
                if (alkamisPaiva.after(loppumisPaiva) || alkamisPaiva == loppumisPaiva){
                    alkamisPaiva = null;
                    break;
                }
            }
        }
        if (alkamisPaiva != null) {
            if (alkamisPaiva.before(this.person.getSession().getTimeScaleSlice().getEndDate())) {
                this.person.getSession().createReservation(post, this.person, new TimeSlice(alkamisPaiva, loppumisPaiva));
                return;
            }
        }
        this.person.getSession().setStatusMessage("Henkilöllä on jo varaukset koko tarkastellulle aikavälille!");
    }
}
