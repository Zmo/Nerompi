
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
 * Sessio, joka kuvaa k�ynniss� olevan ohjelman tilaa. Toimii linkkin� k�ytt�liittym�n
 * ja tietokannan v�lill�. 
 */
public class Session {
	
	/**
	 * Pit�� kirjaa tarkkailijoista, jotka hoitavat p�ivitykset k�ytt�liittym�n n�kym��n.
	 */
    private NeroObserverManager obsman;
    
    /**
     * Tietokantaolio, jonka kautta tietokantaoperaatiot kulkevat.
     */
    private NeroDatabase db;

    
    /* henkil�ihin (getFilteredPeople) vaikuttavat hakuehdot */
    
    /**
     * Aikajakso, hakuehto. Ei voi olla null.
     */
    private TimeSlice timescale;
    
    /**
     * Henkil�n nimi, hakuehto. Voi olla tyhj� merkkijono, mutta ei voi olla null.
     */
	private String personName;
	
	/**
	 * Projekti, hakuehto. Voi olla null, jolloin projektia ei ole.
	 */
	private Project project;
	
	/**
	 * N�ytet��nk� vain p��ttyv�t sopimukset? 
	 */
	private boolean showEndingContracts;
	
	/**
	 * N�ytet��nk� vain ne, joilta puuttuu ty�piste? 
	 */
	private boolean withoutPost;
	
	/**
	 * N�ytet��nk� vain osa-aikaiset ?
	 */
	private boolean partTimeTeachersOnly;

	
	/* huoneisiin (getFilteredRooms) vaikuttavat hakuehdot */
	/**
	 * Tarkasteltavan aikajakson osa, aikajanakomponentista muutettavissa.
     * Ei voi olla null.
	 */
	private TimeSlice timescaleSlice;
	
	private TimeSlice temporaryTimescaleSlice;
	
	/**
	 * Huoneen nimi, hakuehto. Voi olla tyhj� merkkijono. Ei voi olla null.
	 */
	private String roomName;
	
	/**
	 * Montako ty�pistett� maksimissaan ty�huoneessa, hakuehto.
     * Jos arvo on -1, ei ty�pisteiden m��r� rajaa hakua.
	 */
	private int maxPosts;

	/**
	 * Aktiivinen huone, eli huone joka on valittu tarkasteltavaksi.
     * Voi olla null, jolloin aktiivista huonetta ei ole.
	 */
	private Room activeRoom;

	/**
	 * Vapaiden ty�pisteiden minimim��r�, jonka perusteella voidaan esitt�� huone
	 * vapaana. Yleens� 1, mutta voi olla enemm�n.
	 */
	private int freePosts;

	/**
	 * Viimeisin statusviesti.
	 */
	private String statusMessage;
	
	private int cursortype;
	private boolean cursorlocked;
	
	/**
	 * Konstruktori, joka asettaa hakuehdoille oletusarvot ohjelman k�ynnistyess�.
	 */
	public Session() {
        obsman = new NeroObserverManager();
        this.resetTimescale();		// vakioaikav�li
        personName = "";            // ei henkil�n nime�
        project = null;             // ei projektia
        showEndingContracts = true; // n�ytet��n p��ttyv�t sopimukset
        withoutPost = true;         // n�ytet��n ty�pisteett�m�t
        partTimeTeachersOnly = false;   // ei rajata sivut.tuntiop:iin
        roomName = "";              // ei huoneen nime�
        maxPosts = -1;              // ei ty�pisteiden maksimim��r��
        activeRoom = null;          // ei aktiivista huonetta
		freePosts = 1;              // haetaan yht� vapaata ty�pistett�
		statusMessage = new String("");
		cursortype = java.awt.Cursor.DEFAULT_CURSOR;
		cursorlocked = false;
	}
	
	/**
	 * Asettaa tarkasteltavan aikav�lin takaisin vakioksi,
	 * eli t�st� p�iv�st� kolme kuukautta eteenp�in
	 */
	public void resetTimescale() {
        Calendar nowCal = Calendar.getInstance();
        Calendar cal = new GregorianCalendar(nowCal.get(Calendar.YEAR),
                nowCal.get(Calendar.MONTH),
                nowCal.get(Calendar.DAY_OF_MONTH));
        Calendar cal2 = (Calendar)cal.clone();
        // t�st� p�iv�st� kolme kuukautta eteenp�in
        cal2.add(Calendar.MONTH, 3);
        this.timescale = new TimeSlice(cal.getTime(), cal2.getTime());
        // osa-aikav�liksi koko aikav�li
        this.timescaleSlice = this.timescale;
        obsman.notifyObservers(NeroObserverTypes.TIMESCALE);
        obsman.notifyObservers(NeroObserverTypes.TIMESCALESLICE);
	}
	
    /**
     * Asettaa k�ytett�v�n tietokantayhteyden. Metodia voi kutsua vain kerran,
     * sen j�lkeen se heitt�� poikkeuksen.
     * @param db k�ytett�v� tietokantayhteys
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
     * Palauttaa k�ytett�v�n tietokantayhteyden.
     * @return k�ytett�v� tietokantayhteys
     */
    public NeroDatabase getDatabase() {
    	return db;
    }
    
	/**
     * Asettaa tarkasteltavan aikav�lin.
     * @param timescale uusi tarkasteltava aikav�li
     * @throws IllegalArgumentException jos annettu aikav�li on null
     */
    public void setFilterTimescale(TimeSlice timescale) {
        if(timescale == null) {
            throw new IllegalArgumentException();
        }
    	this.timescale = timescale;
        
        // jos osa-aikav�li ei mahdu uuden aikav�lin sis�lle, typist� sit�
        boolean sliceChanged = false;
        if(timescaleSlice.getStartDate().compareTo(timescale.getStartDate()) < 0) {
            timescaleSlice.setStartDate(timescale.getStartDate());
            sliceChanged = true;
        }
        if(timescaleSlice.getEndDate().compareTo(timescale.getEndDate()) > 0) {
            timescaleSlice.setEndDate(timescale.getEndDate());
            sliceChanged = true;
        }

    	// Tyhjenn� ty�pisteiden tallettama tieto niihin liittyvist� varauksista, koska
    	// aikav�li on muuttunut ja sen vuoksi varaukset pit�� hakea uudelleen
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
     * Palauttaa tarkasteltavan aikav�lin.
     * @return Aikav�li TimeSlice-oliona.
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
    	 // tarvitaanko? kyll� tarvitaan, PersonScrollPane ei kuuntele projektia
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
     * Asettaa hakuehtoja rajaavan henkil�n nimen.
     * @param personName Henkil�n nimi.
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
     * Palauttaa hakuehtoja rajaavan henkil�n nimi 
     * @return henkil�n nimi
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
     * Asettaa hakuehtoja rajaavan arvon sille, n�ytet��nk� vain ne
     * henkil�t joiden ty�sopimukset ovat loppumassa tarkasteltavalla aikav�lill�.
     * @param showEndingContracts Rajataanko haku.
     */
    public void setFilterEndingContracts(boolean showEndingContracts) {
    	this.showEndingContracts = showEndingContracts;
    	obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
    }
    
    /**
     * Palauttaa tiedon siit�, n�ytet��nk� vain ne henkil�t, joiden ty�sopimukset ovat
     * loppumassa tarkasteltavalla aikav�lill�.
     * @return rajataanko haku
     */
    public boolean getFilterEndingContracts() {
    	return showEndingContracts;
    }

    /**
     * Asettaa hakuehtoja rajaavan huoneen ty�pisteiden maksimim��r�n. Jos parametri
     * on -1, ei ty�pisteiden m��r�� rajata.
     * @param maxPosts Ty�pisteiden lukum��r� tai -1
     * @throws IllegalArgumentException jos annetaan virheellinen lukum��r� (alle -1)
     */
    public void setFilterMaxPosts(int maxPosts) {
        if(maxPosts < -1) {
            throw new IllegalArgumentException();
        }
    	this.maxPosts = maxPosts;
    	obsman.notifyObservers(NeroObserverTypes.FILTER_ROOMS);
    }

    /**
     * Palauttaa hakuehtoja rajaavan huoneen ty�pisteiden maksimim��r�n.
     * @return ty�pisteiden lukum��r� tai -1, jos m��r�� ei ole rajattu
     */
    public int getFilterMaxPosts() {
    	return maxPosts;
    }
    
    /**
     * Asettaa hakuehtoja rajaavan m�r�een, jonka mukaan n�ytet��n
     * vain ne joilla ei ole ty�pistevarausta aikav�lin ty�sopimusten
     * osalta.
     * @param withoutPost haetaanko vain ne henkil�t, joilla ei ole tp-varausta
     */
    public void setFilterWithoutPost(boolean withoutPost) {
    	this.withoutPost = withoutPost;
    	obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
    }
    

    public boolean getFilterWithoutPost() {
    	return this.withoutPost;
    }
    
    
    
    
    /**
     * Asettaa ehdon, jonka mukaan vapaina esitet��n vain ne huoneet, joissa on v�hint��n
     * x vapaata ty�pistett�.
     * @param freePosts
     * @throws IllegalArgumentException jos huonem��r� ei ole positiivinen luku
     */
    public void setFilterFreePosts(int freePosts) {
        if(freePosts <= 0) {
            throw new IllegalArgumentException();
        }
    	this.freePosts = freePosts;
    	obsman.notifyObservers(NeroObserverTypes.FILTER_ROOMS);
	}

	/**
	 * Palauttaa huoneessa olevien vapaiden ty�pisteiden minimim��r�n, joka on edellytyksen�
	 * sille, ett� huone esitet��n vapaana.
	 * @return vapaiden ty�pisteiden minimim��e�
	 */
	public int getFilterFreePosts() {
		return freePosts;
	}

    /**
     * Asettaa rajauksen, jonka mukaan n�ytet��n ainoastaan sivutoimiset tuntiopettajat.
     * @param partTimeTeachersOnly
     */
    public void setFilterPartTimeTeachers(boolean partTimeTeachersOnly) {
    	this.partTimeTeachersOnly = partTimeTeachersOnly;
    	obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
    }

    public boolean getFilterPartTimeTeachers() {
    	return this.partTimeTeachersOnly;
    }

    
    
    /**
     * Palauttaa listan jossa on kaikki j�rjestelm�n tuntemat projektit.
     * @return J�rjestelm�n tuntemat projektit <code>Project[]</code>-oliona.
     */
    public Project[] getProjects() {
    	return db.getProjects();
    }

    /**
     * Asettaa tarkasteltavalta aikav�lilt� tarkasteltavan osa-aikav�lin.
     * Metodi tarkistaa, ett� osa-aikav�li on tarkisteltavan aikav�lin sis�ll�.
     * @param timescaleSlice Tarkasteltava osa-aikav�li.
     * @param stillMoving Ei l�het� timescaleslicen muuttumisviesti� vaan toisenlaisen tiedottamaan, ett� ollaan liikuttelemassa arvoa
     * @throws IllegalArgumentException jos osa-aikav�li ei ole aikav�lin sis�ll� tai on null
     */
    public void setTimeScaleSlice(TimeSlice timescaleSlice, boolean stillMoving) {
    	if(timescaleSlice == null) {
            throw new IllegalArgumentException("osa-aikav�li ei saa olla null");
        }
    	if(timescaleSlice.getStartDate().compareTo(
    			this.timescale.getStartDate()) < 0) {
            throw new IllegalArgumentException("osa-aikav�li ei saa alkaa ennen aikav�li�");
        }
    	if(timescaleSlice.getEndDate().compareTo(
    			this.timescale.getEndDate()) > 0) {
            throw new IllegalArgumentException("osa-aikav�li ei saa p��tty� my�hemmin kuin aikav�li");
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
     * Palauttaa tarkasteltavan osa-aikav�lin.
     * @return tarkasteltava osa-aikav�li TimeSlice-oliona
     */
    public TimeSlice getTimeScaleSlice() {
    	return timescaleSlice;
    }
    
    /**
     * Palauttaa kaikki j�rjestelm�n tuntemat huoneet.
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
     * projektin ty�ntekij�it� tarkasteltavalla osa-aikav�lill�.
     * @return taulukko projektin huoneista
     */

    public Room[] getProjectRooms() {
        return db.getRooms(project, timescale);
    }

    /**
     * Palauttaa listan sen hetkisten hakuehtojen mukaisista henkil�ist�.
     * @return taulukko henkil�olioista
     */
    public Person[] getFilteredPeople() {
        return db.getPeople(timescale, personName, project, showEndingContracts, withoutPost, partTimeTeachersOnly);
    }
    
    /**
     * Palauttaa ty�pisteen puhelinnumerot
     * @param post Ty�piste jonka numerot haetaan
     * @return Puhelinnumerot <code>PhoneNumber[]</code> Oliona.
     * @throws IllegalArgumentException jos ty�piste on null
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
     * P�ivitt�� varausjakson esimerkin mukaiseksi.
     * @param reservation varausjakso, joka p�ivitet��n
     * @throws IllegalArgumentException jos varaus on null
     */

    public void updateReservation(Reservation reservation) {
        if(reservation == null) {
            throw new IllegalArgumentException("varaus ei voi olla null");
        }
    	boolean success = db.updateReservation(reservation);
        if(!success) {
        	setStatusMessage("Ty�pistevarauksen muuttaminen ei onnistunut.");
            return;
        }
    	// kerrotaan ty�pisteelle ett� sen varaukset ovat muuttuneet
    	reservation.getTargetPost().clearReservations();
    	// varausten ja henkil�iden tiedot ovat muuttuneet, ilmoitetaan kuuntelijoille
    	obsman.notifyObservers(NeroObserverTypes.RESERVATIONS);
    	// XXX PersonScrollPane on FILTER_PEOPLEn ainoa kuuntelija, ja se kuuntelee my�s RESERVATIONSia. Joten turha...
    	//obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
    	setStatusMessage("Ty�pistevarausta muutettu.");
    }

    /**
     * Poistaa varausjakson.
     * @param reservation poistettava varausjakso
     * @throws IllegalArgumentException jos ty�piste on null
     */
    public void deleteReservation(Reservation reservation) {
        if(reservation == null) {
            throw new IllegalArgumentException();
        }
    	if(db.deleteReservation(reservation)) {
    		reservation.getTargetPost().clearReservations();
    		// varausten ja henkil�iden tiedot ovat muuttuneet, ilmoitetaan kuuntelijoille
    		obsman.notifyObservers(NeroObserverTypes.RESERVATIONS);
        	// XXX PersonScrollPane on FILTER_PEOPLEn ainoa kuuntelija, ja se kuuntelee my�s RESERVATIONSia. Joten turha...
    		//obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
    		setStatusMessage("Ty�pistevaraus poistettu.");
    	} else {
    		setStatusMessage("Ty�pistevarauksen poistaminen ep�onnistui.");
    	}
    }

    /**
     * Luo uuden varauksen annettuun ty�pisteeseen ty�sopimusjakson
     * perusteella. Jos ty�sopimusjakson henkil�ll� ei ole ty�pistevarauksia
     * jakson ajaksi, tehd��n ty�pistevaraus koko jakson mittaiseksi.
     * Jos henkil�ll� on varauksia osalle jaksosta, tehd��n ty�pistevaraus
     * ensimm�iselle varauksettomalle osalle ty�sopimusjaksosta.
     * Jos henkil�ll� on varauksia koko jaksolle, ei tehd� mit��n.
     *
     * @param post ty�piste, johon varaus luodaan
     * @param contract ty�sopimusjakso, jonka perusteella varaus luodaan
     * @throws IllegalArgumentException jos ty�piste tai sopimus on null
     */

    public void createReservation(Post post, Contract contract) {
        if(post == null) {
            throw new IllegalArgumentException("ty�piste ei saa olla null");
        }
        if(contract == null) {
            throw new IllegalArgumentException("sopimus ei saa olla null");
        }
        createReservation(post, contract.getPerson(), contract.getTimeSlice());
    }

    /**
     * Luo uuden varauksen annettuun ty�pisteeseen t�m�nhetkiselle
     * osa-aikav�lille. Varaus tehd��n henkil�lle ty�sopimusjaksoihin 
     * katsomatta. Jos henkil�ll� ei ole varauksia tarkasteltavalla 
     * osa-aikav�lill�, tehd��n varaus koko osa-aikav�lin mittaiseksi.
     * Jos henkil�ll� on varauksia osalle osa-aikav�list�, tehd��n varaus
     * ensimm�iselle varauksettomalle osalle osa-aikav�list�. Jos 
     * henkil�ll� on varauksia koko osa-aikav�lille, ei tehd� mit��n.
     * 
     * @param post ty�piste, johon varaus luodaan
     * @param person henkil�, jolle varaus luodaan
     * @throws IllegalArgumentException jos ty�piste tai henkil� on null
     */

    public void createReservation(Post post, Person person) {
    	createReservation(post, person, timescaleSlice);
    }
    
    /**
     * Luo uuden varauksen annettuun ty�pisteeseen annetulle aikav�lille.
     * Varaus tehd��n henkil�lle ty�sopimusjaksoihin katsomatta. Jos
     * henkil�ll� ei ole varauksia annetulla aikav�lill�, tehd��n 
     * varaus koko aikav�lin mittaiseksi. Jos henkil�ll� on varauksia osalle
     * aikav�list�, tehd��n varaus ensimm�iselle varauksettomalle
     * osalle aikav�list�. Jos henkil�ll� on varauksia koko aikav�lille,
     * ei tehd� mit��n.
     * 
     * @param post ty�piste, johon varaus luodaan
     * @param person henkil�, jolle varaus luodaan
     * @param timeSlice aikav�li, jolle varaus luodaan
     * @throws IllegalArgumentException jos ty�piste, henkil� tai aikav�li on null
     */
    
    public void createReservation(Post post, Person person, TimeSlice timeSlice) {
        if(post == null) {
            throw new IllegalArgumentException("ty�piste ei saa olla null");
        }
        if(person == null) {
            throw new IllegalArgumentException("henkil� ei saa olla null");
        }
        if(timescaleSlice == null) {
            throw new IllegalArgumentException("aikav�li ei saa olla null");
        }
        
        // Rajataan tarvittaessa aikav�li tarkasteltavan aikajakson sis�lle
        Date start = timeSlice.getStartDate();
        Date end = timeSlice.getEndDate();
        if(!timescale.contains(start)) {
            start = timescale.getStartDate();
        }
        if(!timescale.contains(end)) {
            end = timescale.getEndDate();
        }
        
        // Etsit��n vapaa jakso
        Reservation[] res = person.getReservations();
        int i;
        for(i=0; i<res.length && res[i].getTimeSlice().contains(start); ++i) {
        	// siirret��n alkup�iv� varauksen p��ttymist� seuraavaan p�iv��n
        	start = new Date(res[i].getTimeSlice().getEndDate().getTime() + TimeSlice.ONEDAY);
        }
        
        if(i < res.length) { // varauksia on viel� lis��
        	// siirret��n loppup�iv� seuraavan varauksen alkamista edelt�v��n p�iv��n
        	end = new Date(res[i].getTimeSlice().getStartDate().getTime() - TimeSlice.ONEDAY);
        }

        TimeSlice reservationTime = new TimeSlice(start, end);
        if(reservationTime.length() < 1) {
        	setStatusMessage("Henkil�ll� on jo ty�piste aikav�lill� " + timeSlice);
        	return;
        }
        
        Reservation newRes = new Reservation(this, null, post, person, reservationTime, 0.0, "");
        if(db.createReservation(newRes)) {
        	newRes.getTargetPost().clearReservations();
        	// huoneiden ja henkil�iden tiedot ovat muuttuneet, ilmoitetaan kuuntelijoille
        	obsman.notifyObservers(NeroObserverTypes.RESERVATIONS);
        	// XXX PersonScrollPane on FILTER_PEOPLEn ainoa kuuntelija, ja se kuuntelee my�s RESERVATIONSia. Joten turha...
        	//obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);    	
            setStatusMessage("Ty�pistevaraus luotu.");
        } else {
            setStatusMessage("Ty�pistevarauksen luonti ep�onnistui.");
        }
    }
    	
    /**
     * Poistaa ty�pisteen.
     *
     * @param post poistettava ty�piste
     * @throws IllegalArgumentException jos ty�piste on null
     */

    public void deletePost(Post post) {
        if(post == null) {
            throw new IllegalArgumentException();
        }
        if(db.deletePost(post)) {
        	switchActiveRoom();
        	// nyt huoneiden tila on muuttunut, joten t�ytyy ilmoittaa kuuntelijoille
        	obsman.notifyObservers(NeroObserverTypes.ROOMS);
        	setStatusMessage("Ty�piste " + post + " poistettu.");
        } else {
        	setStatusMessage("Ty�pisteen " + post + " poistaminen ep�onnistui.");
        }
    }

	/**
     * Luo aktiiviseen(valittuun) huoneeseen uuden ty�pisteen.
     *
     */

    public void createPost() {
        if(this.activeRoom == null) {
            throw new IllegalArgumentException();
        }
    	Post newPost = new Post(this, null, this.activeRoom, 0);
    	if(db.createPost(newPost)) {
    		switchActiveRoom();
    		// nyt huoneiden tila on muuttunut, joten t�ytyy ilmoittaa kuuntelijoille
    		obsman.notifyObservers(NeroObserverTypes.ROOMS);
    		setStatusMessage("Uusi ty�piste luotu huoneeseen " + activeRoom);
    	} else {
    		setStatusMessage("Uuden ty�pisteen luominen ep�onnistui.");
        }
    }

    /**
	 * Vaihtaa aktiivisen huoneen kannasta haettuun uudempaan versioon. Metodia kutsutaan
	 * kun on todenn�k�ist�, ett� aktiivisen huoneen tiedot (mm. ty�pisteet) ovat
	 * vanhentuneet.
	 */
	private void switchActiveRoom() {
		// pyydet��n db:lt� uudempi versio samasta huoneesta
		String roomID = activeRoom.getRoomID();
		activeRoom = db.getRoom(roomID);
	}

    /**
     * Lis�� ty�pisteeseen puhelinnumeron. Jos puhelinnumero on jo
     * jollakin muulla ty�pisteell�, se siirtyy.
     *
     * @param post ty�piste
     * @param phone lis�tt�v� puhelinnumero
     * @throws IllegalArgumentException jos ty�piste tai puhelinnumero on null
     */

    public void addPhoneNumber(Post post, PhoneNumber phone) {
        if(post == null) {
            throw new IllegalArgumentException("ty�piste ei saa olla null");
        }
        if(phone == null) {
            throw new IllegalArgumentException("puhelinnumero ei saa olla null");
        }
    	// luodaan puhelinnumero-oliosta versio, joka viittaa uuteen ty�pisteeseen
    	PhoneNumber newPhone = new PhoneNumber(phone, post);
    	if(db.updatePhoneNumber(newPhone)) {
    		// jos ollaan n�ytt�m�ss� t�t� samaa huonetta, p�ivitet��n sen tiedot
    		if(this.activeRoom.getRoomID().equals(post.getRoom().getRoomID())) {
    			this.switchActiveRoom();
    		}
    		// nyt huoneiden tila on muuttunut, joten t�ytyy ilmoittaa kuuntelijoille
    		obsman.notifyObservers(NeroObserverTypes.ROOMS);
            setStatusMessage("Puhelinnumero liitetty ty�pisteeseen.");
    	} else {
            setStatusMessage("Puhelinnumeron liitt�minen ep�onnistui.");
        }
    }

    /**
     * Poistaa ty�pisteest� puhelinnumeron. Numero vapautuu.
     *
     * @param phone poistettava puhelinnumero
     * @throws IllegalArgumentException jos puhelinnumero on null
     */

    public void deletePhoneNumber(PhoneNumber phone) {
        if(phone == null) {
            throw new IllegalArgumentException();
        }
    	// luodaan puhelinnumero-oliosta versio, joka ei viittaa mihink��n ty�pisteeseen
    	PhoneNumber newPhone = new PhoneNumber(phone, null);
    	if(db.updatePhoneNumber(newPhone)) {
    		// ei tietoa ollaanko juuri t�t� n�ytt�m�ss, mutta p�ivitet��n silti
    		// vrt. tarkastukset updatePhoneNumberissa ^^
    		this.switchActiveRoom();
    		// nyt huoneiden tila on muuttunut, joten t�ytyy ilmoittaa kuuntelijoille
    		obsman.notifyObservers(NeroObserverTypes.ROOMS);
            setStatusMessage("Puhelinnumero poistettu ty�pisteest�.");
    	} else {
            setStatusMessage("Puhelinnumeron poistaminen ep�onnistui.");
    	}
    }

    /* Dataolioiden tarvitsemat tiedonhakuoperaatiot */
    
	/**
	 * Palauttaa henkil��n liittyv�t ty�sopimukset t�m�nhetkisell� aikav�lill�.
	 * @param person henkil�, jonka ty�sopimukset halutaan
	 * @return henkil�n ty�sopimukset
     * @throws IllegalArgumentException jos henkil� on null
	 */
	public Contract[] getContracts(Person person) {
        if(person == null) {
                throw new IllegalArgumentException();
            }
		return db.getContracts(person, timescale);
	}
	
	/**
	 *  Sama annetulle aikav�lille
	 * @param person henkil�, jonka ty�sopimukset halutaan
	 * @param timeslice aikav�li
	 * @return sopimukset
	 */
	public Contract[] getContracts(Person person, TimeSlice timeslice){
		return db.getContracts(person, timeslice);
	}

	/**
	 * Palauttaa henkil��n liittyv�t ty�pistevaraukset t�m�nhetkisell� aikav�lill�.
	 * @param person henkil�, jonka ty�pistevaraukset halutaan
	 * @return henkil�n ty�pistevaraukset
     * @throws IllegalArgumentException jos henkil� on null
	 */
	public Reservation[] getReservations(Person person) {
        if(person == null) {
                throw new IllegalArgumentException();
            }
        return db.getReservations(person, timescale);
	}

	/**
	 * Palauttaa ty�pisteeseen liittyv�t ty�pistevaraukset t�m�nhetkisell� aikav�lill�.
     * Varaukset palautetaan j�rjestettyn� ensisijaisesti alkuajankohdan, toissijaisesti
     * loppuajankohdan mukaan.
	 * @param post ty�piste, jonka ty�pistevaraukset halutaan
	 * @return ty�pisteen varaukset
     * @throws IllegalArgumentException jos ty�piste on null
	 */
	public Reservation[] getReservations(Post post) {
        if(post == null) {
                throw new IllegalArgumentException();
            }
		return db.getReservations(post, timescale);
	}

	/**
	 * Asettaa tekstimuotoisen viestin k�ytt�liittym�n n�ytett�v�ksi ja
	 * tulostaa sen System.out.println():ll�
	 * @param message viesti
	 */
	public void setStatusMessage(String message) {
	    statusMessage = message;
	    obsman.notifyObservers(NeroObserverTypes.STATUSBAR);
	    System.out.println(message);
	}
	
	/**
	 * Asettaa tekstimuotoisen viestin k�ytt�liittym�n n�ytett�v�ksi.
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
	
	/* Kuuntelijoihin liittyv�t operaatiot */
	
    /**
     * Rekister�i kuuntelijan jollekin tapahtumatyypille. Kuuntelijalle
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

}
