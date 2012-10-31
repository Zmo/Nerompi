package fi.helsinki.cs.nero.ui;

import java.util.Iterator;
import java.util.LinkedList;

import fi.helsinki.cs.nero.data.Contract;
import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.data.TimeSlice;
import fi.helsinki.cs.nero.logic.Session;

public class PersonsContracts {
    
    /**
     * Henkilön varaukset.
     */
    private Reservation[] reservations;
    
    /**
     * <p>
     * Henkilön graaffisessa esityksessä tarvittavat rivit listana.
     * Lista sisältää niin työpistevarausket kuin työsopimuksetkin.
     * </p>
     */
    private LinkedList rows;
    
    /**
     * Henkilö jonka tietoja käsitellään.
     */
    private Person person = null;
    
    /**
     * Konstruktori.
     * @param person Henkilö jonka tiedot kasataan.
     * @param timeScale Käsiteltävä aikaväli.
     * @param scale Käytettävä skaala päivä/pikselit.
     * @param session Viite sessioon.
     */
    public PersonsContracts(Person person, TimeSlice timeScale, double scale, Session session) {
        
        this.person = person;
        this.reservations = this.person.getReservations();
        this.rows = new LinkedList();
        
        //Luodaan ja lisätään ensimmäinen rivi.
        Row reservationsRow = new Row(scale, session, this.person);
        rows.add(reservationsRow);
        
        if(this.reservations != null) {
            for(int i=0; i < this.reservations.length; ++i) {
                
                boolean alwaysTrue = reservationsRow.addReservation(this.reservations[i], true);
                
                if(!alwaysTrue) {
                	System.err.println("Päällekkäisiä varauksia:");
                    for(int j=0; j < this.reservations.length; j++)
                    	System.err.println(" - " + this.reservations[j]);
                }              
             }
        }
       
        //Sitten lisätään työsopimukset, aloitetaan uudella rivillä.
        Contract[] contracts = this.person.getContracts();
        
        if(contracts != null) {
           
            Row contractsRow = new Row(scale, session, this.person);
            rows.add(contractsRow);
      
            for(int i=0; i < contracts.length; ++i) {
                boolean added = false;
                Iterator rowsIterator = rows.listIterator(1);
                
                //Yritetään lisätä jokaiselle olemassaolevalle riville.
                while(rowsIterator.hasNext()) {
                    Row row = (Row)rowsIterator.next();
                  
                    //Jos saadaan lisättyä
                    if(row.addContract(contracts[i])) {
                        added = true;
                        break;
                    }     
                }
                if(!added) {
                    Row newRow = new Row(scale, session, this.person);
                    newRow.addContract(contracts[i]);
                    rows.add(newRow);
                }  
            }
        }
        
        
        Iterator i = this.rows.iterator();
        //Lopuksi suljetaan rivit = tehdään perään tyhjän mittainen tyhjä jakso.
        while(i.hasNext()) {
            int counter = 1;
            Row row = (Row)i.next();
            row.closeRow();
        }
    }
    
    /**
     * <p>
     * Palauttaa käyttäjän graaffisen esityksen rivit.
     * </p>
     * @return Viite <code>LinkedList</code>-olioon joka sisältää <code>Row</code>-olioita.
     */
    public LinkedList getRows() {
        return this.rows;
    }
    
    /**
     * <p>
     * Palauttaa kyseessä olevan henkilön.
     * </p>
     * @return Viite <code>Person</code> olioon.
     */
    public Person getPerson() {
        return this.person;
    }
}
