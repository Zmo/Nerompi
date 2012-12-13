/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.logic.Session;
import javax.swing.JLabel;

/**
 * Tarra henkilölle,
 * @author ssinisal
 */
public class PersonNameLabel extends JLabel {
    private Session session;
    private Person person;
    
    public PersonNameLabel(Session s, Person p) {
        this.session = s;
        this.person = p;
    }
    
    public Person getPerson() {
        return this.person;
    }
    public Session getSession() {
        return this.session;
    }
}
