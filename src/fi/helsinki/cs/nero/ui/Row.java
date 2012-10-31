package fi.helsinki.cs.nero.ui;


import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import fi.helsinki.cs.nero.data.Contract;
import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.data.TimeSlice;
import fi.helsinki.cs.nero.logic.Session;

/**
 * <p>
 * Pitää sisällänsä yhteen riviin liittyvät TimelineElement oliot ja rivin
 * käsittelyyn tarvittavat metodit.
 * </p>
 */
public class Row {
    
    /**
     * Tarkasteltavan aikajakson aloituspäivä.
     */
    private Date startDate;
    
    /**
     * Tarkasteltavan aikajakson lopetuspäivä.
     */
    private Date endDate;
    
    /**
     * Rivissä esitettävät <code>TimelineElement</code>-oliot listana.
     */
    private LinkedList elements;
    
    /**
     * Iteraattori, mikäs muukaan.
     */
    private Iterator iterator;
    
    /**
     * Tätä elementtiä rivillä edeltänyt elementti.
     */
    private TimelineElement previous;
    
    /**
     * Käsittelyn alla oleva elementti.
     */
    private TimelineElement current;
    
    /**
     * Sessio
     */
    private Session session;
    
    /**
     * Viimeiksi lisätty elementti.
     */
    private TimelineElement previousAdded;
    
    /**
     * Käytettävä skaala päivä/pikselit.
     */
    private double scale;
    
    /**
     * Henkilö jonka tietoja ollaan esittämässä.
     */
    private Person person = null;
    
    /**
     * 
     * Konstruktori.
     * @param scale Käytettävä skaala.
     * @param session Viite sessioon.
     * @param person Viite henkilöön jonka tietoja ollaan esittämässä.
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
     * Kertoo onko rivillä vielä elementtiä.
     * @return <code>True</code> jos rivillä on elementtejä, <code>False</code> jos ei ole.
     */
    public boolean hasNext() {
        return iterator.hasNext();
    }
    
    /**
     * Siirtää iteraattorin seuraavaan elementtiin rivillä.
     * @return Viitteen uuteen elementtiin.
     */
    public TimelineElement next() {
        this.previous = this.current; 
        this.current = (TimelineElement)iterator.next();
        return this.current;
    }
    
    /**
     * Lisää riville työsopimuksen.
     * @param contract Työsopimus <code>Contract</code> oliona.
     * @return <code>True</code> jos lisäys onnistui, <code>False jos ei onnistunut.</code>.
     */
    public boolean addContract(Contract contract) {
        return this.add(contract, false, false);
    }
    
    /**
     * Lisää uuden varausjakson riville.
     * @param reservation Lisättävä varaus <code>reservation</code> oliolle.
     * @param resizable
     * @return <code>True</code> jos varaus onnistui, <code>false</code< jos ei.
     */
    public boolean addReservation(Reservation reservation, boolean resizable) {
        return this.add(reservation, true, resizable);
    }
    
    /**
     * Lisää uuden objektin riville, huom, privaatti metodi!
     * @param object Lisättävä objekti.
     * @param reservation Onko lisättävä objekti <code>UIReservation</code>-objekti.
     * @param resizable Saako lisättävän objektin kokoa muuttaa?
     * @return True jos lisäys riville onnistui, false jos ei.
     */
    private boolean add(Object object, boolean reservation, boolean resizable) {
       
        TimeSlice newElement = null;
        
        if(object instanceof Reservation) {
            newElement = ((Reservation)object).getTimeSlice();
        } else {
            newElement = ((Contract)object).getTimeSlice();
        }
        
        Date newElementStartDate = newElement.getStartDate();
        Date newElementEndDate = newElement.getEndDate();
        
        //Tarkistetaanko loppuuko lisättävä osa-aikaväli ennen käsiteltävää aikaväliä tai
        //alkaako se käsiteltävän aikavälin jälkeen.
        if(newElementEndDate.before(this.startDate) || newElementStartDate.after(this.endDate)) {   
            return false;
        }
        
        Iterator i = elements.iterator();
        
        //Tarkistetaan meneekö lisättävä aikaväli jo listassa olevien aikavälien kanssa limittäin tai päällekkäin.
        //previously added on aina joku muu elementti kuin tyhjä jos elements listassa on tavaraa.
        while(i.hasNext()) {
            TimeSlice oldSlice = ((TimelineElement)i.next()).getTimeSlice();
            
            Date sliceStartDate = oldSlice.getStartDate();
            Date sliceEndDate = oldSlice.getEndDate();
                
            //Jos alkuaika tai loppuaika on sama kuin rivillä jo olevan elemntin aijan on PAKKO olla päälekkäin.
            if(newElementStartDate.equals(sliceStartDate) || newElementEndDate.equals(sliceEndDate)) {
                return false;
            }
            
            //Onko uuden elementin alkuaika vanhan jälkeen mutta ennen vanhan loppua?
            if(newElementStartDate.after(sliceStartDate) && newElementStartDate.before(sliceEndDate)) {
                return false;
            }
            
            //Onko uuden elementin loppuaika vanhan alkuajan jälkeen mutta ennen loppua.
            if(newElementEndDate.after(sliceStartDate) && newElementEndDate.before(sliceEndDate)) {
                return false;
            }
        }
 
        //Jos ei ole aikaisempaa elementtiä vertaa aikavälin alkuun.
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
        
        if(reservation) {
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
     * Sulkee rivin lisäämällä loppuun riittävän pituisen tyhjän jakson.
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
