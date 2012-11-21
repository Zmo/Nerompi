/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Post;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JMenuItem;

/**
 *
 * @author rkolagus
 */
public class UusiVarausNappiListener implements MouseListener {
    Post[] posts;
    Person person;
    UusiVarausNappi varausNappi;
    
    public UusiVarausNappiListener(UusiVarausNappi varausNappi){
        super();
        this.varausNappi = varausNappi;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            if (!(this.person.getSession().getActiveRoom() == null)) {
                this.posts = this.person.getSession().getActiveRoom().getPosts();
            } else {
                this.posts = null;
            }
            UusiVarausPopup varausPopup = new UusiVarausPopup(this.person, this.posts);
            varausPopup.setLocation(this.varausNappi.getPopupLocation(e));
            for (int a = 0; a < this.posts.length; a++) {
                varausPopup.insert(new JMenuItem(posts[a].getPostID()), a);
            }
        } catch (Exception exc) {
            System.out.println(" - Virhe - UusiNappiVarausListener.mouseClicked: " + exc);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
    
}
