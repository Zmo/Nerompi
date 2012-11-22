
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Post;
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
public class UusiVarausPopup extends JButton {
    Person person;
    JPopupMenu popupMenu;
    Post[] posts;
    
    public UusiVarausPopup(Person person){
        this.setText("Uusi varaus");
        this.addMouseListener(new UusiVarausListener(this));
        this.person = person;
        this.popupMenu = new JPopupMenu("Uusi varaus");
    }
    
    public void naytaPopup(MouseEvent e){
        popupMenu = new JPopupMenu(); 
        if (this.person.getSession().getActiveRoom() == null) {
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
        System.out.println("Valittu kohdepaikka: " + room + "." + post.getPostNumber());

        Date alkamisPaiva;
        if (this.person.getLastReservation() == null) {
            alkamisPaiva = this.person.getSession().getTimeScaleSlice().getStartDate();
        } else {
            alkamisPaiva = this.person.getLastReservation().getTimeSlice().getEndDate();
        }
        if (!(alkamisPaiva.before(this.person.getSession().getTimeScaleSlice().getEndDate()))) {
            System.out.println(" - Virhe - UusiVarausPopup: Aikavälillä ei ole tilaa!");
        } else {
            this.person.getSession().createReservation(post, this.person, new TimeSlice(alkamisPaiva, this.person.getSession().getTimeScaleSlice().getEndDate()));
        }
    }
}
