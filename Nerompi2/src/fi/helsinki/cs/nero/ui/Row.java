package fi.helsinki.cs.nero.ui;


import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import fi.helsinki.cs.nero.data.Contract;
import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.data.RoomKeyReservation;
import fi.helsinki.cs.nero.data.TimeSlice;
import fi.helsinki.cs.nero.logic.Session;
import java.awt.Color;

/**
 * <p>
 * Pit�� sis�ll�ns� yhteen riviin liittyv�t TimelineElement oliot ja rivin
 * k�sittelyyn tarvittavat metodit.
 * </p>
 */
public class Row {
    
    /**
     * Tarkasteltavan aikajakson aloitusp�iv�.
     */
    private Date startDate;
    
    /**
     * Tarkasteltavan aikajakson lopetusp�iv�.
     */
    private Date endDate;
    
    /**
     * Riviss� esitett�v�t <code>TimelineElement</code>-oliot listana.
     */
    private LinkedList elements;
    
    /**
     * Iteraattori, mik�s muukaan.
     */
    private Iterator iterator;
    
    /**
     * T�t� elementti� rivill� edelt�nyt elementti.
     */
    private TimelineElement previous;
    
    /**
     * K�sittelyn alla oleva elementti.
     */
    private TimelineElement current;
    
    /**
     * Sessio
     */
    private Session session;
    
    /**
     * Viimeiksi lis�tty elementti.
     */
    private TimelineElement previousAdded;
    
    /**
     * K�ytett�v� skaala p�iv�/pikselit.
     */
    private double scale;
    
    /**
     * Henkil� jonka tietoja ollaan esitt�m�ss�.
     */
    private Person person = null;
    
    /**
     * 
     * Konstruktori.
     * @param scale K�ytett�v� skaala.
     * @param session Viite sessioon.
     * @param person Viite henkil��n jonka tietoja ollaan esitt�m�ss�.
     */
    public Row(double scale, Session session, Person person) {
      
        this.session = session;
        
        this.startDate = session.getFilterTimescale().getStartDate();
        this.endDate = session.getFilterTimescale().getEndDate();
        
        this.elements = new LinkedList();
        this.resetIterator();
 
        this.scale = scale;
        this.person = person;
    }
    
    /**
     * Asettaa iterattorin alkuun.
     *
     */
    public void resetIterator() {
        this.iterator = elements.iterator();
    }
    
    /**
     * Kertoo onko rivill� viel� elementti�.
     * @return <code>True</code> jos rivill� on elementtej�, <code>False</code> jos ei ole.
     */
    public boolean hasNext() {
        return iterator.hasNext();
    }
    
    /**
     * Siirt�� iteraattorin seuraavaan elementtiin rivill�.
     * @return Viitteen uuteen elementtiin.
     */
    public TimelineElement next() {
        this.previous = this.current; 
        this.current = (TimelineElement)iterator.next();
        return this.current;
    }
    
    /**
     * Lis�� riville ty�sopimuksen.
     * @param contract Ty�sopimus <code>Contract</code> oliona.
     * @return <code>True</code> jos lis�ys onnistui, <code>False jos ei onnistunut.</code>.
     */
    public boolean addContract(Contract contract) {
        return this.add(contract, false, false);
    }
    
    /**
     * Lis�� uuden varausjakson riville.
     * @param reservation Lis�tt�v� varaus <code>reservation</code> oliolle.
     * @param resizable
     * @return <code>True</code> jos varaus onnistui, <code>false</code< jos ei.
     */
    public boolean addReservation(Reservation reservation, boolean resizable) {
        return this.add(reservation, true, resizable);
    }
    
    public boolean addReservation(RoomKeyReservation reservation, boolean resizable) {
        return this.add(reservation, false, resizable);
    }
    
    /**
     * Lis�� uuden objektin riville, huom, privaatti metodi!
     * @param object Lis�tt�v� objekti.
     * @param reservation Onko lis�tt�v� objekti <code>UIReservation</code>-objekti.
     * @param resizable Saako lis�tt�v�n objektin kokoa muuttaa?
     * @return True jos lis�ys riville onnistui, false jos ei.
     */
    private boolean add(Object object, boolean reservation, boolean resizable) {
       
        TimeSlice newElement = null;
        
        if(object instanceof Reservation) {
            newElement = ((Reservation)object).getTimeSlice();
        } else if(object instanceof RoomKeyReservation) {
            newElement = ((RoomKeyReservation)object).getTimeSlice();
        } else {
            newElement = ((Contract)object).getTimeSlice();
        }
        
        Date newElementStartDate = newElement.getStartDate();
        Date newElementEndDate = newElement.getEndDate();
        
        //Tarkistetaanko loppuuko lis�tt�v� osa-aikav�li ennen k�sitelt�v�� aikav�li� tai
        //alkaako se k�sitelt�v�n aikav�lin j�lkeen.
        if(newElementEndDate.before(this.startDate) || newElementStartDate.after(this.endDate)) {   
            return false;
        }
        
        Iterator i = elements.iterator();
        
        //Tarkistetaan meneek� lis�tt�v� aikav�li jo listassa olevien aikav�lien kanssa limitt�in tai p��llekk�in.
        //previously added on aina joku muu elementti kuin tyhj� jos elements listassa on tavaraa.
        while(i.hasNext()) {
            TimeSlice oldSlice = ((TimelineElement)i.next()).getTimeSlice();
            
            Date sliceStartDate = oldSlice.getStartDate();
            Date sliceEndDate = oldSlice.getEndDate();
                
            //Jos alkuaika tai loppuaika on sama kuin rivill� jo olevan elemntin aijan on PAKKO olla p��lekk�in.
            if(newElementStartDate.equals(sliceStartDate) || newElementEndDate.equals(sliceEndDate)) {
                return false;
            }
            
            //Onko uuden elementin alkuaika vanhan j�lkeen mutta ennen vanhan loppua?
            if(newElementStartDate.after(sliceStartDate) && newElementStartDate.before(sliceEndDate)) {
                return false;
            }
            
            //Onko uuden elementin loppuaika vanhan alkuajan j�lkeen mutta ennen loppua.
            if(newElementEndDate.after(sliceStartDate) && newElementEndDate.before(sliceEndDate)) {
                return false;
            }
        }
 
        //Jos ei ole aikaisempaa elementti� vertaa aikav�lin alkuun.
        if(this.previousAdded == null) {
          
               TimelineElement empty = new UIEmpty(this.person, new TimeSlice(this.startDate, newElement.getStartDate()), scale, this.session);
               elements.add(empty);
              
               this.previousAdded = empty;
        } else {
           
                TimelineElement empty = new UIEmpty(this.person, new TimeSlice(previousAdded.getTimeSlice().getEndDate(), newElement.getStartDate()), scale, this.session);
                elements.add(empty);
                
                if(this.previousAdded != null) {
                    this.previousAdded.setNext(empty);
                }
                empty.setPrevious(this.previousAdded);
                this.previousAdded = empty;
           
        }
        
        TimelineElement element = null;
        if(object instanceof RoomKeyReservation) {
            RoomKeyReservation rkReservation = (RoomKeyReservation)object;
            element = new TimelineElement(rkReservation.getTimeSlice(), scale, new Color(39,177,39), rkReservation.getReserver(), rkReservation.getSession());
        } else if(reservation) {
            if(resizable) {
                element = new UIReservation((Reservation)object, this, scale);
            } else {  
                element = new UIReservation((Reservation)object, scale);
            }
        }
        else {
            element = new UIContract((Contract)object, scale, this.session);
        }
                  
        if(this.previousAdded != null) {
            this.previousAdded.setNext(element);
        }
       
        element.setPrevious(this.previousAdded);
        
        elements.add(element);
        this.previousAdded = element;
       
        return true;
    }
    
    public TimelineElement getPrevious() {
        return this.previous;
    }
    
    /**
     * Sulkee rivin lis��m�ll� loppuun riitt�v�n pituisen tyhj�n jakson.
     */
    public void closeRow() {
        if(elements.size() > 0) {
            TimelineElement post = new UIEmpty(this.person, new TimeSlice(previousAdded.getTimeSlice().getEndDate(), this.endDate), scale, this.session);
            elements.add(post);
            this.previousAdded.setNext(post);
        } else {
            TimelineElement post = new UIEmpty(this.person, new TimeSlice(this.startDate, this.endDate), scale, this.session);
            elements.add(post);
        }
    }
    
    /**
     * Korvaa yliluokan toString-metodin.
     */
    public String toString() {
        iterator = elements.iterator();
        int length = 0;
        
        String returnable = "";
        
        while(iterator.hasNext()) {
            TimelineElement element =  (TimelineElement)iterator.next();
            int help = element.getWidth();
            
            returnable += " Pituus:"+help+" ajassa:"+element.getTimeSlice().length();
            length += help;
        }
        return returnable +="\nRivin elementtien yhteispituus on:"+length;
    }
}
