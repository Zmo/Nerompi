
package fi.helsinki.cs.nero.logic;
    
import fi.helsinki.cs.nero.data.Contract;
import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.PhoneNumber;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.data.Project;
import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.data.Room;
import fi.helsinki.cs.nero.data.TimeSlice;
import fi.helsinki.cs.nero.db.NeroDatabase;
import fi.helsinki.cs.nero.event.NeroObserver;
import fi.helsinki.cs.nero.event.NeroObserverManager;
import fi.helsinki.cs.nero.event.NeroObserverTypes;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Sessio, joka kuvaa kï¿½ynnissï¿½ olevan ohjelman tilaa. Toimii linkkinï¿½ kï¿½yttï¿½liittymï¿½n
 * ja tietokannan vï¿½lillï¿½. 
 */
public class Session {
	
	/**
	 * Pitï¿½ï¿½ kirjaa tarkkailijoista, jotka hoitavat pï¿½ivitykset kï¿½yttï¿½liittymï¿½n nï¿½kymï¿½ï¿½n.
	 */
    private NeroObserverManager obsman;
    
    /**
     * Tietokantaolio, jonka kautta tietokantaoperaatiot kulkevat.
     */
    private NeroDatabase db;

    
    /* henkilï¿½ihin (getFilteredPeople) vaikuttavat hakuehdot */
    
    /**
     * Aikajakso, hakuehto. Ei voi olla null.
     */
    private TimeSlice timescale;
    
    /**
     * Henkilï¿½n nimi, hakuehto. Voi olla tyhjï¿½ merkkijono, mutta ei voi olla null.
     */
	private String personName;
	
	/**
	 * Projekti, hakuehto. Voi olla null, jolloin projektia ei ole.
	 */
	private Project project;
	
	/**
	 * Nï¿½ytetï¿½ï¿½nkï¿½ vain pï¿½ï¿½ttyvï¿½t sopimukset? 
	 */
	private boolean showEndingContracts;
	
	/**
	 * Nï¿½ytetï¿½ï¿½nkï¿½ vain ne, joilta puuttuu tyï¿½piste? 
	 */
	private boolean withoutPost;
	
	/**
	 * Nï¿½ytetï¿½ï¿½nkï¿½ vain osa-aikaiset ?
	 */
	private boolean partTimeTeachersOnly;
        
        /**
         * Näytetäänkö vain aktiiviset työntekijät
         */
        private boolean activeEmployeesOnly;
        
        /**
         * Rajataanko henkilölistää voimassaolevan työsopimuksen perusteella
         */
        private boolean contract;
	
	/* huoneisiin (getFilteredRooms) vaikuttavat hakuehdot */
	/**
	 * Tarkasteltavan aikajakson osa, aikajanakomponentista muutettavissa.
     * Ei voi olla null.
	 */
	private TimeSlice timescaleSlice;
	
	private TimeSlice temporaryTimescaleSlice;
	
	/**
	 * Huoneen nimi, hakuehto. Voi olla tyhjï¿½ merkkijono. Ei voi olla null.
	 */
	private String roomName;
	
	/**
	 * Montako tyï¿½pistettï¿½ maksimissaan tyï¿½huoneessa, hakuehto.
     * Jos arvo on -1, ei tyï¿½pisteiden mï¿½ï¿½rï¿½ rajaa hakua.
	 */
	private int maxPosts;

	/**
	 * Aktiivinen huone, eli huone joka on valittu tarkasteltavaksi.
     * Voi olla null, jolloin aktiivista huonetta ei ole.
	 */
	private Room activeRoom;

	/**
	 * Vapaiden tyï¿½pisteiden minimimï¿½ï¿½rï¿½, jonka perusteella voidaan esittï¿½ï¿½ huone
	 * vapaana. Yleensï¿½ 1, mutta voi olla enemmï¿½n.
	 */
	private int freePosts;

	/**
	 * Viimeisin statusviesti.
	 */
	private String statusMessage;
	
	private int cursortype;
	private boolean cursorlocked;
	
	/**
	 * Konstruktori, joka asettaa hakuehdoille oletusarvot ohjelman kï¿½ynnistyessï¿½.
	 */
	public Session() {
            obsman = new NeroObserverManager();
            this.resetTimescale();		// vakioaikavï¿½li
            personName = "";            // ei henkilï¿½n nimeï¿½
            project = null;             // ei projektia
            showEndingContracts = true; // nï¿½ytetï¿½ï¿½n pï¿½ï¿½ttyvï¿½t sopimukset
            withoutPost = true;         // nï¿½ytetï¿½ï¿½n tyï¿½pisteettï¿½mï¿½t
            activeEmployeesOnly = true; // oletusarvoisesti näytetään vain aktiiviset henkilöt
            contract = false;           // oletusarvoisesti näytetään myös henkilöt, joilla ei ole voimassaolevaa työsopimusta
            roomName = "";              // ei huoneen nimeï¿½
            maxPosts = -1;              // ei tyï¿½pisteiden maksimimï¿½ï¿½rï¿½ï¿½
            activeRoom = null;          // ei aktiivista huonetta
            freePosts = 1;              // haetaan yhtï¿½ vapaata tyï¿½pistettï¿½
            statusMessage = new String("");
            cursortype = java.awt.Cursor.DEFAULT_CURSOR;
            cursorlocked = false;
	}
	
	/**
	 * Asettaa tarkasteltavan aikavï¿½lin takaisin vakioksi,
	 * eli tï¿½stï¿½ pï¿½ivï¿½stï¿½ kolme kuukautta eteenpï¿½in
	 */
	public void resetTimescale() {
        Calendar nowCal = Calendar.getInstance();
        Calendar cal = new GregorianCalendar(nowCal.get(Calendar.YEAR),
                nowCal.get(Calendar.MONTH),
                nowCal.get(Calendar.DAY_OF_MONTH));
        Calendar cal2 = (Calendar)cal.clone();
        // tï¿½stï¿½ pï¿½ivï¿½stï¿½ kolme kuukautta eteenpï¿½in
        cal2.add(Calendar.MONTH, 3);
        this.timescale = new TimeSlice(cal.getTime(), cal2.getTime());
        // osa-aikavï¿½liksi koko aikavï¿½li
        this.timescaleSlice = this.timescale;
        obsman.notifyObservers(NeroObserverTypes.TIMESCALE);
        obsman.notifyObservers(NeroObserverTypes.TIMESCALESLICE);
	}
	
    /**
     * Asettaa kï¿½ytettï¿½vï¿½n tietokantayhteyden. Metodia voi kutsua vain kerran,
     * sen jï¿½lkeen se heittï¿½ï¿½ poikkeuksen.
     * @param db kï¿½ytettï¿½vï¿½ tietokantayhteys
     * @throws IllegalArgumentException jos annettu tk-yhteys on null
     * @throws IllegalStateException jos metodia kutsutaan uudelleen
     */
    public void setDatabase(NeroDatabase db) {
        if(db == null) {
            throw new IllegalArgumentException();
        }
        if(this.db != null) {
            throw new IllegalStateException();
        }
        this.db = db;
    }
    
    /**
     * Palauttaa kï¿½ytettï¿½vï¿½n tietokantayhteyden.
     * @return kï¿½ytettï¿½vï¿½ tietokantayhteys
     */
    public NeroDatabase getDatabase() {
    	return db;
    }
    
	/**
     * Asettaa tarkasteltavan aikavï¿½lin.
     * @param timescale uusi tarkasteltava aikavï¿½li
     * @throws IllegalArgumentException jos annettu aikavï¿½li on null
     */
    public void setFilterTimescale(TimeSlice timescale) {
        if(timescale == null) {
            throw new IllegalArgumentException();
        }
    	this.timescale = timescale;
        
        // jos osa-aikavï¿½li ei mahdu uuden aikavï¿½lin sisï¿½lle, typistï¿½ sitï¿½
        boolean sliceChanged = false;
        if(timescaleSlice.getStartDate().compareTo(timescale.getStartDate()) < 0) {
            timescaleSlice.setStartDate(timescale.getStartDate());
            sliceChanged = true;
        }
        if(timescaleSlice.getEndDate().compareTo(timescale.getEndDate()) > 0) {
            timescaleSlice.setEndDate(timescale.getEndDate());
            sliceChanged = true;
        }

    	// Tyhjennï¿½ tyï¿½pisteiden tallettama tieto niihin liittyvistï¿½ varauksista, koska
    	// aikavï¿½li on muuttunut ja sen vuoksi varaukset pitï¿½ï¿½ hakea uudelleen
        // NOTE junit-testien aikana db saattaa olla null
        if(db != null) {
        	Room[] rooms = db.getRooms();
        	for(int i=0; i<rooms.length; ++i) {
        		Post[] posts = rooms[i].getPosts();
        		for(int j=0; j<posts.length; ++j)
        			posts[j].clearReservations();
        	}
        }
    	
    	obsman.notifyObservers(NeroObserverTypes.TIMESCALE);
        if(sliceChanged) {
            obsman.notifyObservers(NeroObserverTypes.TIMESCALESLICE);
        }
    }

    /**
     * Palauttaa tarkasteltavan aikavï¿½lin.
     * @return Aikavï¿½li TimeSlice-oliona.
     */
    public TimeSlice getFilterTimescale() {
        return timescale;
    }

    /**
     * Asettaa hakuehtoja rajaavan projektin. Parametri voi olla null, jolloin projektia ei ole.
     * @param project Projekti
     */
    public void setFilterProject(Project project) {
    	this.project = project;
    	obsman.notifyObservers(NeroObserverTypes.FILTER_PROJECT);
    	 // tarvitaanko? kyllï¿½ tarvitaan, PersonScrollPane ei kuuntele projektia
    	obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
    }
    
    /**
     * Palauttaa hakuehtoja rajaavan projektin.
     * @return hakuehtoja rajaava projekti tai null, jos projektia ei ole
     */
    public Project getFilterProject() {
        return project;
    }

    /**
     * Asettaa hakuehtoja rajaavan henkilï¿½n nimen.
     * @param personName Henkilï¿½n nimi.
     * @throws IllegalArgumentException jos annettu nimi on null
     */
    public void setFilterPersonName(String personName) {
        if(personName == null) {
            throw new IllegalArgumentException();
        }
    	this.personName = personName;
    	obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
    }
    
    /**
     * Palauttaa hakuehtoja rajaavan henkilï¿½n nimi 
     * @return henkilï¿½n nimi
     */
    public String getFilterPersonName() {
    	return personName;
    }
    
    /**
     * Asettaa hakuehtoja rajaavan huoneen.
     * @param roomName Huoneen nimi Exactumin huonekoodien muodossa, esim A212, tai huoneen lempinimi, esim "sininen huone".
     * @throws IllegalArgumentException jos annettu nimi on null
     */
    public void setFilterRoomName(String roomName) {
        if(roomName == null) {
            throw new IllegalArgumentException();
        }
    	this.roomName = roomName;
    	obsman.notifyObservers(NeroObserverTypes.FILTER_ROOMS);
    }
    
    /**
     * Palauttaa hakuehtoja rajaavan huoneen
     * @return huoneen nimi, esim. "A212" tai "sininen huone"
     */
    public String getFilterRoomName() {
    	return roomName;
    }

    /**
     * Asettaa hakuehtoja rajaavan arvon sille, nï¿½ytetï¿½ï¿½nkï¿½ vain ne
     * henkilï¿½t joiden tyï¿½sopimukset ovat loppumassa tarkasteltavalla aikavï¿½lillï¿½.
     * @param showEndingContracts Rajataanko haku.
     */
    public void setFilterEndingContracts(boolean showEndingContracts) {
    	this.showEndingContracts = showEndingContracts;
    	obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
    }
    
    /**
     * Palauttaa tiedon siitï¿½, nï¿½ytetï¿½ï¿½nkï¿½ vain ne henkilï¿½t, joiden tyï¿½sopimukset ovat
     * loppumassa tarkasteltavalla aikavï¿½lillï¿½.
     * @return rajataanko haku
     */
    public boolean getFilterEndingContracts() {
    	return showEndingContracts;
    }

    /**
     * Asettaa hakuehtoja rajaavan huoneen tyï¿½pisteiden maksimimï¿½ï¿½rï¿½n. Jos parametri
     * on -1, ei tyï¿½pisteiden mï¿½ï¿½rï¿½ï¿½ rajata.
     * @param maxPosts Tyï¿½pisteiden lukumï¿½ï¿½rï¿½ tai -1
     * @throws IllegalArgumentException jos annetaan virheellinen lukumï¿½ï¿½rï¿½ (alle -1)
     */
    public void setFilterMaxPosts(int maxPosts) {
        if(maxPosts < -1) {
            throw new IllegalArgumentException();
        }
    	this.maxPosts = maxPosts;
    	obsman.notifyObservers(NeroObserverTypes.FILTER_ROOMS);
    }

    /**
     * Palauttaa hakuehtoja rajaavan huoneen tyï¿½pisteiden maksimimï¿½ï¿½rï¿½n.
     * @return tyï¿½pisteiden lukumï¿½ï¿½rï¿½ tai -1, jos mï¿½ï¿½rï¿½ï¿½ ei ole rajattu
     */
    public int getFilterMaxPosts() {
    	return maxPosts;
    }
    
    /**
     * Asettaa hakuehtoja rajaavan mï¿½rï¿½een, jonka mukaan nï¿½ytetï¿½ï¿½n
     * vain ne joilla ei ole tyï¿½pistevarausta aikavï¿½lin tyï¿½sopimusten
     * osalta.
     * @param withoutPost haetaanko vain ne henkilï¿½t, joilla ei ole tp-varausta
     */
    public void setFilterWithoutPost(boolean withoutPost) {
    	this.withoutPost = withoutPost;
    	obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
    }
    

    public boolean getFilterWithoutPost() {
    	return this.withoutPost;
    }
    
    
    
    
    /**
     * Asettaa ehdon, jonka mukaan vapaina esitetï¿½ï¿½n vain ne huoneet, joissa on vï¿½hintï¿½ï¿½n
     * x vapaata tyï¿½pistettï¿½.
     * @param freePosts
     * @throws IllegalArgumentException jos huonemï¿½ï¿½rï¿½ ei ole positiivinen luku
     */
    public void setFilterFreePosts(int freePosts) {
        if(freePosts <= 0) {
            throw new IllegalArgumentException();
        }
    	this.freePosts = freePosts;
    	obsman.notifyObservers(NeroObserverTypes.FILTER_ROOMS);
	}

	/**
	 * Palauttaa huoneessa olevien vapaiden tyï¿½pisteiden minimimï¿½ï¿½rï¿½n, joka on edellytyksenï¿½
	 * sille, ettï¿½ huone esitetï¿½ï¿½n vapaana.
	 * @return vapaiden tyï¿½pisteiden minimimï¿½ï¿½eï¿½
	 */
	public int getFilterFreePosts() {
		return freePosts;
	}
        
    /**
     * Voidaan rajata listattavia henkilöitä heidän aktiivisuuden perusteella
     * @param activeEmployeesOnly 
     */
    public void setFilterActiveEmployees(boolean activeEmployeesOnly) {
        this.activeEmployeesOnly = activeEmployeesOnly;
        obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
    }
    
    public boolean getFilterActiveEmployees() {
        return this.activeEmployeesOnly;
    }
    
    /**
     * Voidaan rajata listattavia henkilöitä voimassaolevan työsopimuksen perusteella
     * @param contract 
     */
    public void setFilterContract(boolean contract) {
        this.contract = contract;
        obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
    }
    
    public boolean getFilterContract() {
        return this.contract;
    }
    
    /**
     * Palauttaa listan jossa on kaikki jï¿½rjestelmï¿½n tuntemat projektit.
     * @return Jï¿½rjestelmï¿½n tuntemat projektit <code>Project[]</code>-oliona.
     */
    public Project[] getProjects() {
    	return db.getProjects();
    }

    /**
     * Asettaa tarkasteltavalta aikavï¿½liltï¿½ tarkasteltavan osa-aikavï¿½lin.
     * Metodi tarkistaa, ettï¿½ osa-aikavï¿½li on tarkisteltavan aikavï¿½lin sisï¿½llï¿½.
     * @param timescaleSlice Tarkasteltava osa-aikavï¿½li.
     * @param stillMoving Ei lï¿½hetï¿½ timescaleslicen muuttumisviestiï¿½ vaan toisenlaisen tiedottamaan, ettï¿½ ollaan liikuttelemassa arvoa
     * @throws IllegalArgumentException jos osa-aikavï¿½li ei ole aikavï¿½lin sisï¿½llï¿½ tai on null
     */
    public void setTimeScaleSlice(TimeSlice timescaleSlice, boolean stillMoving) {
    	if(timescaleSlice == null) {
            throw new IllegalArgumentException("osa-aikavï¿½li ei saa olla null");
        }
    	if(timescaleSlice.getStartDate().compareTo(
    			this.timescale.getStartDate()) < 0) {
            throw new IllegalArgumentException("osa-aikavï¿½li ei saa alkaa ennen aikavï¿½liï¿½");
        }
    	if(timescaleSlice.getEndDate().compareTo(
    			this.timescale.getEndDate()) > 0) {
            throw new IllegalArgumentException("osa-aikavï¿½li ei saa pï¿½ï¿½ttyï¿½ myï¿½hemmin kuin aikavï¿½li");
        }
    	this.timescaleSlice = timescaleSlice;
    	if (stillMoving) {
            obsman.notifyObservers(NeroObserverTypes.TIMESCALESLICEUPDATING);
        }
    	else {
            obsman.notifyObservers(NeroObserverTypes.TIMESCALESLICE);
        }
    }
    
    /**
     * Palauttaa tarkasteltavan osa-aikavï¿½lin.
     * @return tarkasteltava osa-aikavï¿½li TimeSlice-oliona
     */
    public TimeSlice getTimeScaleSlice() {
    	return timescaleSlice;
    }
    
    /**
     * Palauttaa kaikki jï¿½rjestelmï¿½n tuntemat huoneet.
     * @return taulukko huoneista
     */

    public Room[] getRooms() {
        return db.getRooms();
    }

    /**
     * Palauttaa hakuehtojen mukaiset huoneet.
     * @return taulukko hakuehtojen mukaisista huoneista
     */

    public Room[] getFilteredRooms() {
    	return db.getRooms(roomName, maxPosts);
    }

    /**
     * Palauttaa ne huoneet, joihin on sijoitettu hakuehtojen mukaisen
     * projektin tyï¿½ntekijï¿½itï¿½ tarkasteltavalla osa-aikavï¿½lillï¿½.
     * @return taulukko projektin huoneista
     */

    public Room[] getProjectRooms() {
        return db.getRooms(project, timescale);
    }

    /**
     * Palauttaa listan sen hetkisten hakuehtojen mukaisista henkilï¿½istï¿½.
     * @return taulukko henkilï¿½olioista
     */
    public Person[] getFilteredPeople() {
        return db.getPeople(timescale, personName, project, showEndingContracts, withoutPost, partTimeTeachersOnly);
    }
    
    /**
     * Palauttaa tyï¿½pisteen puhelinnumerot
     * @param post Tyï¿½piste jonka numerot haetaan
     * @return Puhelinnumerot <code>PhoneNumber[]</code> Oliona.
     * @throws IllegalArgumentException jos tyï¿½piste on null
     */
    public PhoneNumber[] getPhoneNumbers(Post post){
        if(post == null) {
            throw new IllegalArgumentException();
        }
    	return db.getPhoneNumbers(post);
    }
    
    /**
     * Palauttaa "kaikki" puhelinnumerot
     * ks. db:n vastaava metodi
     */
    public PhoneNumber[] getAllPhoneNumbers() {
    	return db.getAllPhoneNumbers();
    }

    /**
     * Palauttaa aktiivisen huoneen.
     * @return aktiivinen huone
     */
    public Room getActiveRoom() {
        return activeRoom;
    }

    /**
     * Asettaa valitun huoneen.
     * @param activeRoom uusi valittu huone, tai null, jos halutaan ettei valittua huonetta ole
     */

    public void setActiveRoom(Room activeRoom) {
    	this.activeRoom = activeRoom;
    	obsman.notifyObservers(NeroObserverTypes.ACTIVE_ROOM);
    }

    /**
     * Pï¿½ivittï¿½ï¿½ varausjakson esimerkin mukaiseksi.
     * @param reservation varausjakso, joka pï¿½ivitetï¿½ï¿½n
     * @throws IllegalArgumentException jos varaus on null
     */

    public void updateReservation(Reservation reservation) {
        if(reservation == null) {
            throw new IllegalArgumentException("varaus ei voi olla null");
        }
    	boolean success = db.updateReservation(reservation);
        if(!success) {
        	setStatusMessage("Tyï¿½pistevarauksen muuttaminen ei onnistunut.");
            return;
        }
    	// kerrotaan tyï¿½pisteelle ettï¿½ sen varaukset ovat muuttuneet
    	reservation.getTargetPost().clearReservations();
    	// varausten ja henkilï¿½iden tiedot ovat muuttuneet, ilmoitetaan kuuntelijoille
    	obsman.notifyObservers(NeroObserverTypes.RESERVATIONS);
    	// XXX PersonScrollPane on FILTER_PEOPLEn ainoa kuuntelija, ja se kuuntelee myï¿½s RESERVATIONSia. Joten turha...
    	//obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
    	setStatusMessage("Tyï¿½pistevarausta muutettu.");
    }

    /**
     * Poistaa varausjakson.
     * @param reservation poistettava varausjakso
     * @throws IllegalArgumentException jos tyï¿½piste on null
     */
    public void deleteReservation(Reservation reservation) {
        if(reservation == null) {
            throw new IllegalArgumentException();
        }
    	if(db.deleteReservation(reservation)) {
    		reservation.getTargetPost().clearReservations();
    		// varausten ja henkilï¿½iden tiedot ovat muuttuneet, ilmoitetaan kuuntelijoille
    		obsman.notifyObservers(NeroObserverTypes.RESERVATIONS);
        	// XXX PersonScrollPane on FILTER_PEOPLEn ainoa kuuntelija, ja se kuuntelee myï¿½s RESERVATIONSia. Joten turha...
    		//obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
    		setStatusMessage("Tyï¿½pistevaraus poistettu.");
                db.removeRoomFromPerson(reservation.getReservingPerson());
    	} else {
    		setStatusMessage("Tyï¿½pistevarauksen poistaminen epï¿½onnistui.");
    	}
    }

    /**
     * Luo uuden varauksen annettuun tyï¿½pisteeseen tyï¿½sopimusjakson
     * perusteella. Jos tyï¿½sopimusjakson henkilï¿½llï¿½ ei ole tyï¿½pistevarauksia
     * jakson ajaksi, tehdï¿½ï¿½n tyï¿½pistevaraus koko jakson mittaiseksi.
     * Jos henkilï¿½llï¿½ on varauksia osalle jaksosta, tehdï¿½ï¿½n tyï¿½pistevaraus
     * ensimmï¿½iselle varauksettomalle osalle tyï¿½sopimusjaksosta.
     * Jos henkilï¿½llï¿½ on varauksia koko jaksolle, ei tehdï¿½ mitï¿½ï¿½n.
     *
     * @param post tyï¿½piste, johon varaus luodaan
     * @param contract tyï¿½sopimusjakso, jonka perusteella varaus luodaan
     * @throws IllegalArgumentException jos tyï¿½piste tai sopimus on null
     */

    public void createReservation(Post post, Contract contract) {
        if(post == null) {
            throw new IllegalArgumentException("tyï¿½piste ei saa olla null");
        }
        if(contract == null) {
            throw new IllegalArgumentException("sopimus ei saa olla null");
        }
        createReservation(post, contract.getPerson(), contract.getTimeSlice());
        db.addRoomToPerson(contract.getPerson(), post.getRoom().getRoomName());
    }

    /**
     * Luo uuden varauksen annettuun tyï¿½pisteeseen tï¿½mï¿½nhetkiselle
     * osa-aikavï¿½lille. Varaus tehdï¿½ï¿½n henkilï¿½lle tyï¿½sopimusjaksoihin 
     * katsomatta. Jos henkilï¿½llï¿½ ei ole varauksia tarkasteltavalla 
     * osa-aikavï¿½lillï¿½, tehdï¿½ï¿½n varaus koko osa-aikavï¿½lin mittaiseksi.
     * Jos henkilï¿½llï¿½ on varauksia osalle osa-aikavï¿½listï¿½, tehdï¿½ï¿½n varaus
     * ensimmï¿½iselle varauksettomalle osalle osa-aikavï¿½listï¿½. Jos 
     * henkilï¿½llï¿½ on varauksia koko osa-aikavï¿½lille, ei tehdï¿½ mitï¿½ï¿½n.
     * 
     * @param post tyï¿½piste, johon varaus luodaan
     * @param person henkilï¿½, jolle varaus luodaan
     * @throws IllegalArgumentException jos tyï¿½piste tai henkilï¿½ on null
     */

    public void createReservation(Post post, Person person) {
    	createReservation(post, person, timescaleSlice);
        db.addRoomToPerson(person, post.getRoom().getRoomName());
    }
    
    /**
     * Luo uuden varauksen annettuun tyï¿½pisteeseen annetulle aikavï¿½lille.
     * Varaus tehdï¿½ï¿½n henkilï¿½lle tyï¿½sopimusjaksoihin katsomatta. Jos
     * henkilï¿½llï¿½ ei ole varauksia annetulla aikavï¿½lillï¿½, tehdï¿½ï¿½n 
     * varaus koko aikavï¿½lin mittaiseksi. Jos henkilï¿½llï¿½ on varauksia osalle
     * aikavï¿½listï¿½, tehdï¿½ï¿½n varaus ensimmï¿½iselle varauksettomalle
     * osalle aikavï¿½listï¿½. Jos henkilï¿½llï¿½ on varauksia koko aikavï¿½lille,
     * ei tehdï¿½ mitï¿½ï¿½n.
     * 
     * @param post tyï¿½piste, johon varaus luodaan
     * @param person henkilï¿½, jolle varaus luodaan
     * @param timeSlice aikavï¿½li, jolle varaus luodaan
     * @throws IllegalArgumentException jos tyï¿½piste, henkilï¿½ tai aikavï¿½li on null
     */
    
    public void createReservation(Post post, Person person, TimeSlice timeSlice) {
        if(post == null) {
            throw new IllegalArgumentException("tyï¿½piste ei saa olla null");
        }
        if(person == null) {
            throw new IllegalArgumentException("henkilï¿½ ei saa olla null");
        }
        if(timescaleSlice == null) {
            throw new IllegalArgumentException("aikavï¿½li ei saa olla null");
        }
        
        // Rajataan tarvittaessa aikavï¿½li tarkasteltavan aikajakson sisï¿½lle
        Date start = timeSlice.getStartDate();
        Date end = timeSlice.getEndDate();
        if(!timescale.contains(start)) {
            start = timescale.getStartDate();
        }
        if(!timescale.contains(end)) {
            end = timescale.getEndDate();
        }
        
        // Etsitï¿½ï¿½n vapaa jakso
        Reservation[] res = person.getReservations();
        int i;
        for(i=0; i<res.length && res[i].getTimeSlice().contains(start); ++i) {
        	// siirretï¿½ï¿½n alkupï¿½ivï¿½ varauksen pï¿½ï¿½ttymistï¿½ seuraavaan pï¿½ivï¿½ï¿½n
        	start = new Date(res[i].getTimeSlice().getEndDate().getTime() + TimeSlice.ONEDAY);
        }
        
        if(i < res.length) { // varauksia on vielï¿½ lisï¿½ï¿½
        	// siirretï¿½ï¿½n loppupï¿½ivï¿½ seuraavan varauksen alkamista edeltï¿½vï¿½ï¿½n pï¿½ivï¿½ï¿½n
        	end = new Date(res[i].getTimeSlice().getStartDate().getTime() - TimeSlice.ONEDAY);
        }

        TimeSlice reservationTime = new TimeSlice(start, end);
        if(reservationTime.length() < 1) {
        	setStatusMessage("Henkilï¿½llï¿½ on jo tyï¿½piste aikavï¿½lillï¿½ " + timeSlice);
        	return;
        }
        
        Reservation newRes = new Reservation(this, null, post, person, reservationTime, 0.0, "");
        if(db.createReservation(newRes)) {
        	newRes.getTargetPost().clearReservations();
        	// huoneiden ja henkilï¿½iden tiedot ovat muuttuneet, ilmoitetaan kuuntelijoille
        	obsman.notifyObservers(NeroObserverTypes.RESERVATIONS);
        	// XXX PersonScrollPane on FILTER_PEOPLEn ainoa kuuntelija, ja se kuuntelee myï¿½s RESERVATIONSia. Joten turha...
        	//obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);    	
            setStatusMessage("Tyï¿½pistevaraus luotu.");
            db.addRoomToPerson(person, post.getRoom().getRoomName());
        } else {
            setStatusMessage("Tyï¿½pistevarauksen luonti epï¿½onnistui.");
        }
    }
    	
    /**
     * Poistaa tyï¿½pisteen.
     *
     * @param post poistettava tyï¿½piste
     * @throws IllegalArgumentException jos tyï¿½piste on null
     */

    public void deletePost(Post post) {
        if(post == null) {
            throw new IllegalArgumentException();
        }
        if(db.deletePost(post)) {
        	switchActiveRoom();
        	// nyt huoneiden tila on muuttunut, joten tï¿½ytyy ilmoittaa kuuntelijoille
        	obsman.notifyObservers(NeroObserverTypes.ROOMS);
        	setStatusMessage("Tyï¿½piste " + post + " poistettu.");
        } else {
        	setStatusMessage("Tyï¿½pisteen " + post + " poistaminen epï¿½onnistui.");
        }
    }

	/**
     * Luo aktiiviseen(valittuun) huoneeseen uuden tyï¿½pisteen.
     *
     */

    public void createPost() {
        if(this.activeRoom == null) {
            throw new IllegalArgumentException();
        }
    	Post newPost = new Post(this, null, this.activeRoom, 0);
    	if(db.createPost(newPost)) {
    		switchActiveRoom();
    		// nyt huoneiden tila on muuttunut, joten tï¿½ytyy ilmoittaa kuuntelijoille
    		obsman.notifyObservers(NeroObserverTypes.ROOMS);
    		setStatusMessage("Uusi tyï¿½piste luotu huoneeseen " + activeRoom);
    	} else {
    		setStatusMessage("Uuden tyï¿½pisteen luominen epï¿½onnistui.");
        }
    }

    /**
	 * Vaihtaa aktiivisen huoneen kannasta haettuun uudempaan versioon. Metodia kutsutaan
	 * kun on todennï¿½kï¿½istï¿½, ettï¿½ aktiivisen huoneen tiedot (mm. tyï¿½pisteet) ovat
	 * vanhentuneet.
	 */
	private void switchActiveRoom() {
		// pyydetï¿½ï¿½n db:ltï¿½ uudempi versio samasta huoneesta
		String roomID = activeRoom.getRoomID();
		activeRoom = db.getRoom(roomID);
	}

    /**
     * Lisï¿½ï¿½ tyï¿½pisteeseen puhelinnumeron. Jos puhelinnumero on jo
     * jollakin muulla tyï¿½pisteellï¿½, se siirtyy.
     *
     * @param post tyï¿½piste
     * @param phone lisï¿½ttï¿½vï¿½ puhelinnumero
     * @throws IllegalArgumentException jos tyï¿½piste tai puhelinnumero on null
     */

    public void addPhoneNumber(Post post, PhoneNumber phone) {
        if(post == null) {
            throw new IllegalArgumentException("tyï¿½piste ei saa olla null");
        }
        if(phone == null) {
            throw new IllegalArgumentException("puhelinnumero ei saa olla null");
        }
    	// luodaan puhelinnumero-oliosta versio, joka viittaa uuteen tyï¿½pisteeseen
    	PhoneNumber newPhone = new PhoneNumber(phone, post);
    	if(db.updatePhoneNumber(newPhone)) {
    		// jos ollaan nï¿½yttï¿½mï¿½ssï¿½ tï¿½tï¿½ samaa huonetta, pï¿½ivitetï¿½ï¿½n sen tiedot
    		if(this.activeRoom.getRoomID().equals(post.getRoom().getRoomID())) {
    			this.switchActiveRoom();
    		}
    		// nyt huoneiden tila on muuttunut, joten tï¿½ytyy ilmoittaa kuuntelijoille
    		obsman.notifyObservers(NeroObserverTypes.ROOMS);
            setStatusMessage("Puhelinnumero liitetty tyï¿½pisteeseen.");
    	} else {
            setStatusMessage("Puhelinnumeron liittï¿½minen epï¿½onnistui.");
        }
    }

    /**
     * Poistaa tyï¿½pisteestï¿½ puhelinnumeron. Numero vapautuu.
     *
     * @param phone poistettava puhelinnumero
     * @throws IllegalArgumentException jos puhelinnumero on null
     */

    public void deletePhoneNumber(PhoneNumber phone) {
        if(phone == null) {
            throw new IllegalArgumentException();
        }
    	// luodaan puhelinnumero-oliosta versio, joka ei viittaa mihinkï¿½ï¿½n tyï¿½pisteeseen
    	PhoneNumber newPhone = new PhoneNumber(phone, null);
    	if(db.updatePhoneNumber(newPhone)) {
    		// ei tietoa ollaanko juuri tï¿½tï¿½ nï¿½yttï¿½mï¿½ss, mutta pï¿½ivitetï¿½ï¿½n silti
    		// vrt. tarkastukset updatePhoneNumberissa ^^
    		this.switchActiveRoom();
    		// nyt huoneiden tila on muuttunut, joten tï¿½ytyy ilmoittaa kuuntelijoille
    		obsman.notifyObservers(NeroObserverTypes.ROOMS);
            setStatusMessage("Puhelinnumero poistettu tyï¿½pisteestï¿½.");
    	} else {
            setStatusMessage("Puhelinnumeron poistaminen epï¿½onnistui.");
    	}
    }

    /* Dataolioiden tarvitsemat tiedonhakuoperaatiot */
    
	/**
	 * Palauttaa henkilï¿½ï¿½n liittyvï¿½t tyï¿½sopimukset tï¿½mï¿½nhetkisellï¿½ aikavï¿½lillï¿½.
	 * @param person henkilï¿½, jonka tyï¿½sopimukset halutaan
	 * @return henkilï¿½n tyï¿½sopimukset
     * @throws IllegalArgumentException jos henkilï¿½ on null
	 */
	public Contract[] getContracts(Person person) {
        if(person == null) {
                throw new IllegalArgumentException();
            }
		return db.getContracts(person, timescale);
	}
	
	/**
	 *  Sama annetulle aikavï¿½lille
	 * @param person henkilï¿½, jonka tyï¿½sopimukset halutaan
	 * @param timeslice aikavï¿½li
	 * @return sopimukset
	 */
	public Contract[] getContracts(Person person, TimeSlice timeslice){
		return db.getContracts(person, timeslice);
	}

	/**
	 * Palauttaa henkilï¿½ï¿½n liittyvï¿½t tyï¿½pistevaraukset tï¿½mï¿½nhetkisellï¿½ aikavï¿½lillï¿½.
	 * @param person henkilï¿½, jonka tyï¿½pistevaraukset halutaan
	 * @return henkilï¿½n tyï¿½pistevaraukset
     * @throws IllegalArgumentException jos henkilï¿½ on null
	 */
	public Reservation[] getReservations(Person person) {
        if(person == null) {
                throw new IllegalArgumentException();
            }
        return db.getReservations(person, timescale);
	}

	/**
	 * Palauttaa tyï¿½pisteeseen liittyvï¿½t tyï¿½pistevaraukset tï¿½mï¿½nhetkisellï¿½ aikavï¿½lillï¿½.
     * Varaukset palautetaan jï¿½rjestettynï¿½ ensisijaisesti alkuajankohdan, toissijaisesti
     * loppuajankohdan mukaan.
	 * @param post tyï¿½piste, jonka tyï¿½pistevaraukset halutaan
	 * @return tyï¿½pisteen varaukset
     * @throws IllegalArgumentException jos tyï¿½piste on null
	 */
	public Reservation[] getReservations(Post post) {
        if(post == null) {
                throw new IllegalArgumentException();
            }
		return db.getReservations(post, timescale);
	}

	/**
	 * Asettaa tekstimuotoisen viestin kï¿½yttï¿½liittymï¿½n nï¿½ytettï¿½vï¿½ksi ja
	 * tulostaa sen System.out.println():llï¿½
	 * @param message viesti
	 */
	public void setStatusMessage(String message) {
	    statusMessage = message;
	    obsman.notifyObservers(NeroObserverTypes.STATUSBAR);
	    System.out.println(message);
	}
	
	/**
	 * Asettaa tekstimuotoisen viestin kï¿½yttï¿½liittymï¿½n nï¿½ytettï¿½vï¿½ksi.
	 * @param message viesti
	 */
	public void setStatusMessageNoPrint(String message) {
	    statusMessage = message;
	    obsman.notifyObservers(NeroObserverTypes.STATUSBAR);		
	}
	
	/**
	 * Palauttaa asetetun tekstimuotoisen viestin.
	 * @return viesti
	 */
	public String getStatusMessage() {
	    return statusMessage;
	}
	
	public void waitState(boolean on) {
		if(on) {
			this.changeCursorType(java.awt.Cursor.WAIT_CURSOR);
		} else {
			this.changeCursorType(java.awt.Cursor.DEFAULT_CURSOR);			
		}
	}
	
	public void setCursorLock(boolean lock) {
		this.cursorlocked = lock;
	}
	
	public void changeCursorType(int cursortype) {
		// ei aseteta jos lukittu tai kursori oli jo sama
		if(this.cursorlocked || this.cursortype == cursortype) {
                return;
            }
		this.cursortype = cursortype;
		obsman.notifyObservers(NeroObserverTypes.CURSORCHANGE);
	}
	
	public int getCursorType() {
		return this.cursortype;
	}
	
	/* Kuuntelijoihin liittyvï¿½t operaatiot */
	
    /**
     * Rekisterï¿½i kuuntelijan jollekin tapahtumatyypille. Kuuntelijalle
     * ilmoitetaan, kun valituntyyppinen muutos tapahtuu.
     *
     * @param type kuuntelijan tyyppi
     * @param observer kuuntelijarajapinnan toteuttava olio
     * @throws IllegalArgumentException jos observer-parametri on null
     * @see fi.helsinki.cs.nero.event.NeroObserver
     * @see fi.helsinki.cs.nero.event.NeroObserverTypes
     */

    public void registerObserver(int type, NeroObserver observer) {
        if(observer == null) {
            throw new IllegalArgumentException();
        }
    	obsman.addObserver(type, observer);
    }
    public void saveNewPerson(Person person) {
        db.createPerson(person);
    }
    public void updatePerson(Person person) {
        db.updatePersonInfo(person);
    }

}
