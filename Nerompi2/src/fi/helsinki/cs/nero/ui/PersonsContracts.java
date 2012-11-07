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
     * Henkil�n varaukset.
     */
    private Reservation[] reservations;
    
    /**
     * <p>
     * Henkil�n graaffisessa esityksess� tarvittavat rivit listana.
     * Lista sis�lt�� niin ty�pistevarausket kuin ty�sopimuksetkin.
     * </p>
     */
    private LinkedList rows;
    
    /**
     * Henkil� jonka tietoja k�sitell��n.
     */
    private Person person = null;
    
    /**
     * Konstruktori.
     * @param person Henkil� jonka tiedot kasataan.
     * @param timeScale K�sitelt�v� aikav�li.
     * @param scale K�ytett�v� skaala p�iv�/pikselit.
     * @param session Viite sessioon.
     */
    public PersonsContracts(Person person, TimeSlice timeScale, double scale, Session session) {
        
        this.person = person;
        this.reservations = this.person.getReservations();
        this.rows = new LinkedList();
        
        //Luodaan ja lis�t��n ensimm�inen rivi.
        Row reservationsRow = new Row(scale, session, this.person);
        rows.add(reservationsRow);
        
        if(this.reservations != null) {
            for(int i=0; i < this.reservations.length; ++i) {
                
                boolean alwaysTrue = reservationsRow.addReservation(this.reservations[i], true);
                
                if(!alwaysTrue) {
                	System.err.println("P��llekk�isi� varauksia:");
                    for(int j=0; j < this.reservations.length; j++)
                    	System.err.println(" - " + this.reservations[j]);
                }              
             }
        }
       
        //Sitten lis�t��n ty�sopimukset, aloitetaan uudella rivill�.
        Contract[] contracts = this.person.getContracts();
        
        if(contracts != null) {
           
            Row contractsRow = new Row(scale, session, this.person);
            rows.add(contractsRow);
      
            for(int i=0; i < contracts.length; ++i) {
                boolean added = false;
                Iterator rowsIterator = rows.listIterator(1);
                
                //Yritet��n lis�t� jokaiselle olemassaolevalle riville.
                while(rowsIterator.hasNext()) {
                    Row row = (Row)rowsIterator.next();
                  
                    //Jos saadaan lis�tty�
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
        //Lopuksi suljetaan rivit = tehd��n per��n tyhj�n mittainen tyhj� jakso.
        while(i.hasNext()) {
            int counter = 1;
            Row row = (Row)i.next();
            row.closeRow();
        }
    }
    
    /**
     * <p>
     * Palauttaa k�ytt�j�n graaffisen esityksen rivit.
     * </p>
     * @return Viite <code>LinkedList</code>-olioon joka sis�lt�� <code>Row</code>-olioita.
     */
    public LinkedList getRows() {
        return this.rows;
    }
    
    /**
     * <p>
     * Palauttaa kyseess� olevan henkil�n.
     * </p>
     * @return Viite <code>Person</code> olioon.
     */
    public Person getPerson() {
        return this.person;
    }
}
