package fi.helsinki.cs.nero.ui;

import java.util.Iterator;
import java.util.LinkedList;

import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.logic.Session;

/**
 * Esittää työpisteen varaukset.
 */
public class PostsReservations {
    
    /**
     * Työpisteen varauksiin esittäessä liittyvät erilliset rivit.
     */
    private LinkedList rows = new LinkedList();
    
    /**
     * Työpiste jonka tiedot esitetään.
     */
    private Post post = null;

    /**
     * 
     * Konstruktori.
     * @param post Työpiste jonka tiedot esitetään.
     * @param scale Käytettävä skaala päivä/pikselit.
     * @param session
     * @param person
     */
    public PostsReservations(Post post, double scale, Session session, Person person) {
        
        Reservation[] reservations = post.getReservations();
        this.post = post;
         
        Row reservationsRow = new Row(scale, session, person);
        this.rows.add(reservationsRow);
        
        for(int i=0; i<reservations.length; ++i) {

                boolean added = false;
                Iterator rowsIterator = this.rows.iterator();
                
                //Yritetään lisätä jokaiselle olemassaolevalle riville.
                int rivi = 1;
                while(rowsIterator.hasNext()) {
                             
                    Row row = (Row)rowsIterator.next();
                    
                    //Jos saadaan lisättyä
                    String personsName = reservations[i].getReservingPerson().getName();
                    
                    if(row.addReservation(reservations[i], false)) {
                    added = true;
                    break;
                    }       
               }
               
               if(!added) {//Luodaan uusi rivi jossa varausjakso esitetään.
                   Row newRow = new Row(scale, session, person);
                   String personsName = reservations[i].getReservingPerson().getName();
                   newRow.addReservation(reservations[i], false);
                   this.rows.add(newRow);
               }  
        }
        
        //Lopuksi suljetaan rivit = tehdään jokaisen perään tyhjän mittainen tyhjä jakso.
        Iterator i = this.rows.iterator();
        while(i.hasNext()) {
            int counter = 1;
            Row row = (Row)i.next();
            row.closeRow();
        }
    }
    
    /**
     * Palauttaa esitettävät rivit.
     * @return Viite <code>Row</code>-olioon.
     */
    public LinkedList getRows() {
        return this.rows;
    }
    
    /**
     * <p>
     * Palauttaa viiteen työpisteolioon, jonka varaustilanteeseen
     * tämä riviesitys liittyy. (Työpisteen varaustilanteen esittämiseen
     * voidaan käyttää useampikin <code<Row</code>-olio.
     * </p>
     * @return Viite <code>Post</code>-olioon.
     */
    public Post getPost() {
        return this.post;
    }
}