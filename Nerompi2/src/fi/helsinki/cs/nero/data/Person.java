package fi.helsinki.cs.nero.data;

import java.util.Arrays;
import java.util.Date;

import fi.helsinki.cs.nero.logic.Session;

/*
 * Created on Oct 22, 2004
 *
 */

/**
 * @author Johannes Kuusela
 *
 */
public class Person implements Comparable {

	/**Henkil�n tunnus*/
	private final String personID;
	
	/**Henkil�n nimi*/
	private final String name;			

	/**Henkil�n ty�sopimukset*/
	private Contract[] contracts;
	
	/**Henkil�n huonevaraukset.*/
	private Reservation[] reservations;

	private Session session;
	
	
	/**
	 * Konstruktori. Saa parametrinaan session johon liittyy, henkil�n tunnuksen,
	 * henkil�n nimen, henkil�n ty�sopimukset ja henkil�n ty�pistevaraukset.
	 * @param session Sessio <code>Session</code> oliona.
	 * @param personID Henkil�lle asetettava tunnus Stringin�.
	 * @param name Henkil�lle asetettava nimi Stringin�.
	 * @param contracts Henkil�n sopimukset <code>Contracts[]</code> oliona.
	 * @param reservations Henkil�n ty�pistevaraukset <code>Reservations[]</code> oliona.
	 * @throws IllegalArgumentException Jos annettu Session tai personID null.
	 */
	public Person(Session session, String personID, String name, Contract[] contracts,
			Reservation[] reservations)
	{
		if (session == null || personID == null || name == null) {
			throw new IllegalArgumentException();
		}	
		this.session = session;
		this.personID = personID;
		this.name = name;
		this.contracts = contracts;
		this.reservations = reservations;
	}
	
	
	/**
	 * Palauttaa henkil�n tunnuksen.
	 * @return personID Tunnus Stringin�.
	 */
	public String getPersonID() {
		return this.personID;
	}
	
	
	/**
	 * Palauttaa henkil�n nimen.
	 * @return Nimi Stringin�.
	 */
	public String getName() {
		return name;
	}
	
	
	/**
	 * Palauttaa henkil�n ty�sopimukset sessiossa m��r�tyll� aikav�lill�.
	 * @return projects Sopimukset <code>Contract[]</code> oliona.
	 */
	public Contract[] getContracts() {
		if(this.contracts == null)
			this.contracts = session.getContracts(this);
		return this.contracts;
	}
	
	
	/**
	 * Palauttaa henkil�n ty�pistevaraukset sessiossa m��r�tyll� aikav�lill�.
	 * @return reservations Varaukset <code>Reservation[]</code> oliona.
	 */
	public Reservation[] getReservations() {
		if(this.reservations == null)
			this.reservations = session.getReservations(this);
		return this.reservations;
	}
	
	
	/**
	 * Palauttaa true, jos henkil?ll? on ty?sopimusjaksoja, joiden aikana h?nell?
	 * ei ole my?s ty?pistevarausta valitulla aikav?lill?. 
	 * Kertoo siis joutuuko henkil? ty?skentelem??n Exactumin vessassa n?ill? n?kymin.
	 * @return hasToWorkInTheToilet kts. muuttujan kuvaava nimi.
	 */

	
	public boolean getStatus(){
		boolean  hasToWorkInTheToilet = false;
		int reservationIndex = 0;
		Date endingDate = session.getFilterTimescale().getEndDate(); 
		Date workingDate = session.getFilterTimescale().getStartDate(); 		
		
		/*Haetaan ty?pistevaraukset ja sopimukset aikav?lill? ja pistet??n j?rjestykseen */
		
        Contract[] contractsForThisPerson = getContracts();
        if(contractsForThisPerson.length == 0)
            return false; // ei sopimuksia, joten pakko olla tyytyv�inen

        Reservation[] reservationsForThisPerson = getReservations();
        if(reservationsForThisPerson.length == 0) { // ei varauksia
            // t�ytyy viel� tutkia sopimusjaksojen laatu; voi olla ett� ne ovat
            // vain virkavapauksia jolloin henkil� on tyytyv�inen
            return contractBetweenDates(contractsForThisPerson, workingDate, endingDate);
        }
        
		Arrays.sort(reservationsForThisPerson);
		Arrays.sort(contractsForThisPerson);
		
		
		/* K?yd??n l?pi varauksia, ja jos aikav?lilt? l?ytyy jakso, jolla ei ole
		 * varausta, katsotaan onko henkil?ll? silloin voimassaoleva sopimus. 
		 */
	
		Date nextReservationStarts = reservationsForThisPerson[reservationIndex].getTimeSlice().getStartDate();
		Date thisReservationEnds = reservationsForThisPerson[reservationIndex].getTimeSlice().getEndDate();
		
		
		if (workingDate.compareTo(nextReservationStarts) == -1){
			
			if(contractBetweenDates(contractsForThisPerson, workingDate, nextReservationStarts)){
				
				hasToWorkInTheToilet = true;
			}
		}
		
		
		while(!hasToWorkInTheToilet && nextReservationStarts.compareTo(endingDate)== -1 &&
				thisReservationEnds.compareTo(endingDate) == -1 && reservationIndex < reservationsForThisPerson.length ){
			
			// Haetaan varauksesta loppup?iv? ja sit? seuraavasta varauksesta 
			// alkup?iv?. Jos alkup?iv? on loppup?iv?n j?lkeen, varausten v?lill?
			// aukko, joka on tutkittava.
			
			thisReservationEnds = reservationsForThisPerson[reservationIndex].getTimeSlice().getEndDate();
			
			try{
				nextReservationStarts = reservationsForThisPerson[reservationIndex+1].getTimeSlice().getStartDate();
				
			}catch(ArrayIndexOutOfBoundsException e){
				nextReservationStarts = endingDate;
				
			}
			
			if(thisReservationEnds.compareTo(nextReservationStarts) == -1){
				
				if(contractBetweenDates(contractsForThisPerson, thisReservationEnds, nextReservationStarts)){					
					hasToWorkInTheToilet = true;
				}
			}
			
			
			
			reservationIndex++;
		}
		thisReservationEnds = reservations[reservations.length-1].getTimeSlice().getEndDate();
		
		if(thisReservationEnds.compareTo(endingDate) == -1){
			
			if(contractBetweenDates(contractsForThisPerson,thisReservationEnds, endingDate )){
				hasToWorkInTheToilet = true;
			}
		}
		
		return hasToWorkInTheToilet;
	}
	
	/**
	 * Tutkii onko annettujen p�ivien v�lill� henkil�ll� voimassaolevia ty�sopimuksia, 
	 * joiden aikana henkil� ei ole kokonaan virkavapaalla.
	 * HUOM: p�ivien v�lissa oltava v�hint��n yksi p�iv�, per�kk�iset p�iv�t = ei tehd� mit��n
	 * @param contractsForThisPerson Ty�sopimukset
	 * @param start alkupvm
	 * @param end loppupvm
	 * @return true tai false
	 */
	private boolean contractBetweenDates(Contract[] contractsForThisPerson, Date start, Date end){
		
		// ESIM: Sopimus joka loppuu 5. p�iv�n� ja sopimus, joka alkaa 6. p�iv�n� eiv�t
		// j�t� aukkoa, siisp� lopetetaan.
		long dif = Math.abs((start.getTime() - end.getTime()) / (1000*60*60*24));

		if(dif == 1){
			return false;
		}
		
		// tapaus 1: sopimus alkanut ennen aukkoa varauksissa ja loppuu sen j�lkeen
		// tapaus 2: sopimus alkanut aukon sis�ll� ja loppuu sen j�lkeen
		// tapaus 3: sopimus alkanut ennen aukkoa ja loppuu sen aikana
		// tapaus 4: sopimus alkaa ja loppuu aukon sis�ll�.
		
		// Tapaukset 2 ja 4: riitt�� tutkia alkaako aukon sis�ll� sopimuksia
		// Tapaukset 3 ja 4: riitt�� tutkia loppuuko aukon aikana sopimuksia
		// Tapaus 1: Tutkitaan alkoiko t�m� sopimus ennen aukkoa ja loppuuko se sen j�lkeen.
		
		
		for(int i=0; i<contractsForThisPerson.length;i++){
			if(contractsForThisPerson[i].getWorkingPercentage() == 0)
                continue; // skipataan jos on kokonaan virkavapaalla

			
			// HUOM: Sopimus voi alkaa samana p�iv�n� kuin aukko varauksissa loppuu,
			// ilman ett� henkil� on unhappy. 
			
			if(contractsForThisPerson[i].getTimeSlice().getStartDate().compareTo(start) >= 0 &&
					contractsForThisPerson[i].getTimeSlice().getStartDate().compareTo(end)	< 0){
					
				return true;
			}
			
			if(contractsForThisPerson[i].getTimeSlice().getStartDate().compareTo(start) < 0 &&
					contractsForThisPerson[i].getTimeSlice().getEndDate().compareTo(end) > 0){
				return true;
			}
			
			
			// HUOM: Sopimus voi loppua samana p�iv�n� kuin aukko varauksissa alkaa,
			// ilman ett� henkil� on unhappy
			
			if(contractsForThisPerson[i].getTimeSlice().getEndDate().compareTo(start) > 0 &&
					contractsForThisPerson[i].getTimeSlice().getEndDate().compareTo(end)	<= 0){
				return true;
			}
			
			
		}
		
		return false;
		
		
	}
	

		
	/**
	 * Palauttaa Person-olion merkkijonoesityksen
	 * @return merkkijonoesitys, esimerkiksi "Testi Teppo Antero"
	 */
	public String toString() {
		return this.name;
	}
	
	public int compareTo(Object o) {
		Person p = (Person)o;
		return this.name.compareTo(p.getName());
	}
	
}