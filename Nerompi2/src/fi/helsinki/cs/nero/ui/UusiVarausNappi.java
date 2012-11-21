/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Person;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;


/**
 *
 * @author rkolagus
 */
public class UusiVarausNappi extends JButton{
    Person person;
    
    public UusiVarausNappi(Person person){
        this.person = person;
        this.setText("Lis‰‰ varaus");
        this.addMouseListener(new UusiVarausNappiListener(this));
    }
}
