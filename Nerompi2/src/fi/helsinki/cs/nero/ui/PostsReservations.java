package fi.helsinki.cs.nero.ui;

import java.util.Iterator;
import java.util.LinkedList;

import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.logic.Session;

/**
 * Esitt�� ty�pisteen varaukset.
 */
public class PostsReservations {
    
    /**
     * Ty�pisteen varauksiin esitt�ess� liittyv�t erilliset rivit.
     */
    private LinkedList rows = new LinkedList();
    
    /**
     * Ty�piste jonka tiedot esitet��n.
     */
    private Post post = null;

    /**
     * Konstruktori.
     * @param post Ty�piste jonka tiedot esitet��n.
     * @param scale K�ytett�v� skaala p�iv�/pikselit.
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
            
            //Yritet��n lis�t� jokaiselle olemassaolevalle riville.
            while(rowsIterator.hasNext()) {
                Row row = (Row)rowsIterator.next();
            
                //Jos saadaan lis�tty�
                if(row.addReservation(reservations[i], false)) {
                    added = true;
                    break;
                }
            }
            if(!added) { //Luodaan uusi rivi jossa varausjakso esitet��n.
                Row newRow = new Row(scale, session, person);
                newRow.addReservation(reservations[i], false);
                this.rows.add(newRow);
            }  
        }
        
        //Lopuksi suljetaan rivit = tehd��n jokaisen per��n tyhj�n mittainen tyhj� jakso.
        Iterator i = this.rows.iterator();
        while(i.hasNext()) {
            Row row = (Row)i.next();
            row.closeRow();
        }
    }
    
    /**
     * Palauttaa esitett�v�t rivit.
     * @return Viite <code>Row</code>-olioon.
     */
    public LinkedList getRows() {
        return this.rows;
    }
    
    /**
     * <p>
     * Palauttaa viiteen ty�pisteolioon, jonka varaustilanteeseen
     * t�m� riviesitys liittyy. (Ty�pisteen varaustilanteen esitt�miseen
     * voidaan k�ytt�� useampikin <code<Row</code>-olio.
     * </p>
     * @return Viite <code>Post</code>-olioon.
     */
    public Post getPost() {
        return this.post;
    }
}