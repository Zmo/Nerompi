/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Post;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

/**
 *
 * @author rkolagus
 */
public class UusiVarausMenuItemListener implements ActionListener {
    private UusiVarausPopup varausPopup;
    private Post post;
    
    public UusiVarausMenuItemListener(UusiVarausPopup varausPopup, Post post){
        super();
        this.varausPopup = varausPopup;
        this.post = post;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // room, post
        this.varausPopup.teeVaraus(this.varausPopup.person.getSession().getActiveRoom(), this.post);
    }
    
}
