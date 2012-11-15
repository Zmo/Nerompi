package fi.helsinki.cs.nero.data;

import java.util.Arrays;
import java.util.Date;

import fi.helsinki.cs.nero.logic.Session;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/*
 * Created on Oct 22, 2004
 *
 */

/**
 * @author Johannes Kuusela
 *
 */
public class Person implements Comparable {

	/**Henkilön tunnus*/
	private final String personID;
	
	/**Henkilön nimi*/
	private final String name;			

    

	/**Henkilön työsopimukset*/
	private Contract[] contracts;
	
	/**Henkilön huonevaraukset.*/
	private Reservation[] reservations;
        
        /**Henkilön työhuone*/
        private String room;
        
        private String callName;
        private String activity;
        private String hetu;
        private String oppiarvo;
        private String titteli;
        private String workPhone;
        private String homePhone;
        private String address;
        private String postnumber;
        private String postitoimiPaikka;
        private String valvontaSaldo;
        private String sahkoposti;
        private String hallinnollinenKommentti;
        private String opiskelijaKommentti;
        private String kTunnus;
        private String kannykka;
        private String postilokeroHuone;
        private String hyTyosuhde;
        private String hyPuhelinluettelossa;
        private String etunimi;
        private String sukunimi;

    public String getEtunimi() {
        return etunimi;
    }

    public void setEtunimi(String etunimi) {
        this.etunimi = etunimi;
    }

    public String getSukunimi() {
        return sukunimi;
    }

    public void setSukunimi(String sukunimi) {
        this.sukunimi = sukunimi;
    }
        
	private Session session;
        
	
	
	/**
	 * Konstruktori. Saa parametrinaan session johon liittyy, henkilön tunnuksen,
	 * henkilön nimen, henkilön työsopimukset ja henkilön työpistevaraukset.
	 * @param session Sessio <code>Session</code> oliona.
	 * @param personID Henkilölle asetettava tunnus Stringinä.
	 * @param name Henkilölle asetettava nimi Stringinä.
	 * @param contracts Henkilön sopimukset <code>Contracts[]</code> oliona.
	 * @param reservations Henkilön työpistevaraukset <code>Reservations[]</code> oliona.
	 * @throws IllegalArgumentException Jos annettu Session tai personID null.
	 */
    public Person(Session session, HashMap<String, String> henkiloHash, Contract[] contracts, Reservation[] reservations) {
    
        
        if (session == null || henkiloHash.get("htunnus") == null || 
                henkiloHash.get("kokonimi")  == null) {
            
                throw new IllegalArgumentException();
        }	
        this.personID = henkiloHash.get("htunnus");
        this.name = henkiloHash.get("kokonimi");
        this.contracts = contracts;
        this.reservations = reservations;
        this.room = henkiloHash.get("huone_nro");
        this.callName = henkiloHash.get("kutsumanimi");
        
        if (henkiloHash.get("aktiivisuus") != null) {
            this.activity = henkiloHash.get("aktiivisuus");
        } else {
            this.activity = "E";
        }
        
        this.hetu = henkiloHash.get("hetu");
        this.oppiarvo = henkiloHash.get("oppiarvo");
        this.titteli = henkiloHash.get("titteli");
        this.workPhone = henkiloHash.get("puhelin_tyo");
        this.homePhone = henkiloHash.get("puhelin_koti");
        this.address = henkiloHash.get("katuosoite");
        this.postnumber = henkiloHash.get("postinro");
        this.postitoimiPaikka = henkiloHash.get("postitoimipaikka");
        this.valvontaSaldo = henkiloHash.get("valvontasaldo");
        this.sahkoposti = henkiloHash.get("sahkopostiosoite");
        this.hallinnollinenKommentti = henkiloHash.get("hallinnollinen_kommentti");
        this.opiskelijaKommentti = henkiloHash.get("opiskelija_kommentti");
        this.kTunnus = henkiloHash.get("ktunnus");
        this.kannykka = henkiloHash.get("kannykka");
        this.postilokeroHuone = henkiloHash.get("postilokerohuone");
        this.hyTyosuhde = henkiloHash.get("hy_tyosuhde");
        this.hyPuhelinluettelossa = henkiloHash.get("hy_puhelinluettelossa");
        this.etunimi = henkiloHash.get("etunimet");
        this.sukunimi = henkiloHash.get("sukunimi");
        this.session = session;                
    }
    
	
	/**
	 * Palauttaa henkilön tunnuksen.
	 * @return personID Tunnus Stringinä.
	 */
	public String getPersonID() {
		return this.personID;
	}
	
	
	/**
	 * Palauttaa henkilön nimen.
	 * @return Nimi Stringinä.
	 */
	public String getName() {
		return name;
	}
        public String getCallName() {
        return callName;
        }

        public String getActivity() {
            return activity;
        }

        public String getHetu() {
            return hetu;
        }

        public String getOppiarvo() {
            return oppiarvo;
        }

        public String getTitteli() {
            return titteli;
        }

        public String getWorkPhone() {
            return workPhone;
        }

        public String getHomePhone() {
            return homePhone;
        }

        public String getAddress() {
            return address;
        }

        public String getPostnumber() {
            return postnumber;
        }

        public String getPostitoimiPaikka() {
            return postitoimiPaikka;
        }

        public String getValvontaSaldo() {
            return valvontaSaldo;
        }

        public String getSahkoposti() {
            return sahkoposti;
        }

        public String getHallinnollinenKommentti() {
            return hallinnollinenKommentti;
        }

        public String getOpiskelijaKommentti() {
            return opiskelijaKommentti;
        }

        public String getkTunnus() {
            return kTunnus;
        }

        public String getKannykka() {
            return kannykka;
        }

        public String getPostilokeroHuone() {
            return postilokeroHuone;
        }

        public String getHyTyosuhde() {
            return hyTyosuhde;
        }

        public String getHyPuhelinluettelossa() {
            return hyPuhelinluettelossa;
        }

        public void setContracts(Contract[] contracts) {
            this.contracts = contracts;
        }

        public void setReservations(Reservation[] reservations) {
            this.reservations = reservations;
        }

        public void setCallName(String callName) {
            this.callName = callName;
        }

        public void setActivity(String activity) {
            this.activity = activity;
        }

        public void setHetu(String hetu) {
            this.hetu = hetu;
        }

        public void setOppiarvo(String oppiarvo) {
            this.oppiarvo = oppiarvo;
        }

        public void setTitteli(String titteli) {
            this.titteli = titteli;
        }

        public void setWorkPhone(String workPhone) {
            this.workPhone = workPhone;
        }

        public void setHomePhone(String homePhone) {
            this.homePhone = homePhone;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setPostnumber(String postnumber) {
            this.postnumber = postnumber;
        }

        public void setPostitoimiPaikka(String postitoimiPaikka) {
            this.postitoimiPaikka = postitoimiPaikka;
        }

        public void setValvontaSaldo(String valvontaSaldo) {
            this.valvontaSaldo = valvontaSaldo;
        }

        public void setSahkoposti(String sahkoposti) {
            this.sahkoposti = sahkoposti;
        }

        public void setHallinnollinenKommentti(String hallinnollinenKommentti) {
            this.hallinnollinenKommentti = hallinnollinenKommentti;
        }

        public void setOpiskelijaKommentti(String opiskelijaKommentti) {
            this.opiskelijaKommentti = opiskelijaKommentti;
        }

        public void setkTunnus(String kTunnus) {
            this.kTunnus = kTunnus;
        }

        public void setKannykka(String kannykka) {
            this.kannykka = kannykka;
        }

        public void setPostilokeroHuone(String postilokeroHuone) {
            this.postilokeroHuone = postilokeroHuone;
        }

        public void setHyTyosuhde(String hyTyosuhde) {
            this.hyTyosuhde = hyTyosuhde;
        }

        public void setHyPuhelinluettelossa(String hyPuhelinluettelossa) {
            this.hyPuhelinluettelossa = hyPuhelinluettelossa;
        }
        
        public Session getSession() {
        return session;
        }

        public void setSession(Session session) {
            this.session = session;
        }
	
        public String getRoom() {
            return this.room;
        }
        
        public void setRoom(String room) {
            this.room = room;
        }
        
	/**
	 * Palauttaa henkilön työsopimukset sessiossa määrätyllä aikavälillä.
	 * @return projects Sopimukset <code>Contract[]</code> oliona.
	 */
	public Contract[] getContracts() {
		if(this.contracts == null) {
                this.contracts = session.getContracts(this);
            }
		return this.contracts;
	}
	
        /**
         * Palauttaa henkilön työsopimukset String-muodossa
         * 
         */
        public String getContractLengthAsString() {
            if(this.contracts == null) {
                this.contracts = session.getContracts(this);
            } 
            
            String contractString = "";
            for (int i = 0; i < contracts.length; i++) {
                contractString = contractString.concat(" " + contracts[i].getTimeSlice());
            }
            
            return contractString;
        }
	
	/**
	 * Palauttaa henkilön työpistevaraukset sessiossa määrätyllä aikavälillä.
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
            return false; // ei sopimuksia, joten pakko olla tyytyväinen

        Reservation[] reservationsForThisPerson = getReservations();
        if(reservationsForThisPerson.length == 0) { // ei varauksia
            // täytyy vielä tutkia sopimusjaksojen laatu; voi olla että ne ovat
            // vain virkavapauksia jolloin henkilö on tyytyväinen
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
	 * Tutkii onko annettujen päivien välillä henkilöllä voimassaolevia työsopimuksia, 
	 * joiden aikana henkilö ei ole kokonaan virkavapaalla.
	 * HUOM: päivien välissa oltava vähintään yksi päivä, peräkkäiset päivät = ei tehdä mitään
	 * @param contractsForThisPerson Työsopimukset
	 * @param start alkupvm
	 * @param end loppupvm
	 * @return true tai false
	 */
	private boolean contractBetweenDates(Contract[] contractsForThisPerson, Date start, Date end){
		
		// ESIM: Sopimus joka loppuu 5. päivänä ja sopimus, joka alkaa 6. päivänä eivät
		// jätä aukkoa, siispä lopetetaan.
		long dif = Math.abs((start.getTime() - end.getTime()) / (1000*60*60*24));

		if(dif == 1){
			return false;
		}
		
		// tapaus 1: sopimus alkanut ennen aukkoa varauksissa ja loppuu sen jälkeen
		// tapaus 2: sopimus alkanut aukon sisällä ja loppuu sen jälkeen
		// tapaus 3: sopimus alkanut ennen aukkoa ja loppuu sen aikana
		// tapaus 4: sopimus alkaa ja loppuu aukon sisällä.
		
		// Tapaukset 2 ja 4: riittää tutkia alkaako aukon sisällä sopimuksia
		// Tapaukset 3 ja 4: riittää tutkia loppuuko aukon aikana sopimuksia
		// Tapaus 1: Tutkitaan alkoiko tämä sopimus ennen aukkoa ja loppuuko se sen jälkeen.
		
		
		for(int i=0; i<contractsForThisPerson.length;i++){
			if(contractsForThisPerson[i].getWorkingPercentage() == 0)
                continue; // skipataan jos on kokonaan virkavapaalla

			
			// HUOM: Sopimus voi alkaa samana päivänä kuin aukko varauksissa loppuu,
			// ilman että henkilö on unhappy. 
			
			if(contractsForThisPerson[i].getTimeSlice().getStartDate().compareTo(start) >= 0 &&
					contractsForThisPerson[i].getTimeSlice().getStartDate().compareTo(end)	< 0){
					
				return true;
			}
			
			if(contractsForThisPerson[i].getTimeSlice().getStartDate().compareTo(start) < 0 &&
					contractsForThisPerson[i].getTimeSlice().getEndDate().compareTo(end) > 0){
				return true;
			}
			
			
			// HUOM: Sopimus voi loppua samana päivänä kuin aukko varauksissa alkaa,
			// ilman että henkilö on unhappy
			
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
