package fi.helsinki.cs.nero.logic;

import fi.helsinki.cs.nero.data.Contract;
import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.PhoneNumber;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.data.Project;
import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.data.Room;
import fi.helsinki.cs.nero.data.RoomKeyReservation;
import fi.helsinki.cs.nero.data.TimeSlice;
import fi.helsinki.cs.nero.db.NeroDatabase;
import fi.helsinki.cs.nero.event.NeroObserver;
import fi.helsinki.cs.nero.event.NeroObserverManager;
import fi.helsinki.cs.nero.event.NeroObserverTypes;
import fi.helsinki.cs.nero.ui.RoomScrollPane;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Sessio, joka kuvaa käynnissä olevan ohjelman tilaa. Toimii linkkinä
 * käyttöliittymän ja tietokannan välillä.
 */
public class Session {

    /**
     * Pitää kirjaa tarkkailijoista, jotka hoitavat päivitykset käyttöliittymän
     * näkymään.
     */
    private NeroObserverManager obsman;
    /**
     * Tietokantaolio, jonka kautta tietokantaoperaatiot kulkevat.
     */
    private NeroDatabase db;
    /* henkilöihin (getFilteredPeople) vaikuttavat hakuehdot */
    /**
     * Aikajakso, hakuehto. Ei voi olla null.
     */
    private TimeSlice timescale;

    /**
     * Henkilön nimi, hakuehto. Voi olla tyhjä merkkijono, mutta ei voi olla
     * null.
     */
    private String personName;
    /**
     * Projekti, hakuehto. Voi olla null, jolloin projektia ei ole.
     */
    private Project project;
    /**
     * Näytetäänkö vain päättyvät sopimukset?
     */
    private boolean showEndingContracts;
    /**
     * Näytetäänkö vain ne, joilta puuttuu työpiste?
     */
    private boolean withoutPost;
    /**
     * Näytetäänkö vain osa-aikaiset ?
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
     * Tarkasteltavan aikajakson osa, aikajanakomponentista muutettavissa. Ei
     * voi olla null.
     */
    private TimeSlice timescaleSlice;
    private TimeSlice temporaryTimescaleSlice;
    /**
     * Huoneen nimi, hakuehto. Voi olla tyhjä merkkijono. Ei voi olla null.
     */
    private String roomName;
    /**
     * Montako työpistettä maksimissaan työhuoneessa, hakuehto. Jos arvo on -1,
     * ei työpisteiden määrä rajaa hakua.
     */
    private int maxPosts;
    /**
     * Aktiivinen huone, eli huone joka on valittu tarkasteltavaksi. Voi olla
     * null, jolloin aktiivista huonetta ei ole.
     */
    private Room activeRoom;
    /**
     * Vapaiden tyï¿½pisteiden minimimï¿½ï¿½rï¿½, jonka perusteella voidaan
     * esittï¿½ï¿½ huone vapaana. Yleensï¿½ 1, mutta voi olla enemmï¿½n.
     */
    private int freePosts;
    /**
     * Viimeisin statusviesti.
     */
    private String statusMessage;
    private int cursortype;
    private boolean cursorlocked;
    public RoomScrollPane roomScrollPane;

    /**
     * Konstruktori, joka asettaa hakuehdoille oletusarvot ohjelman
     * kï¿½ynnistyessï¿½.
     */
    public Session() {
        obsman = new NeroObserverManager();
        this.resetTimescale();		// vakioaikavï¿½li
        personName = "";            // ei henkilï¿½n nimeï¿½
        project = null;             // ei projektia
        showEndingContracts = false; // nï¿½ytetï¿½ï¿½n pï¿½ï¿½ttyvï¿½t sopimukset
        withoutPost = false;         // nï¿½ytetï¿½ï¿½n tyï¿½pisteettï¿½mï¿½t
        activeEmployeesOnly = true; // oletusarvoisesti näytetään vain aktiiviset henkilöt
        contract = false;           // oletusarvoisesti näytetään myös henkilöt, joilla ei ole voimassaolevaa työsopimusta
        roomName = "";              // ei huoneen nimeï¿½
        maxPosts = -1;              // ei tyï¿½pisteiden maksimimï¿½ï¿½rï¿½ï¿½
        activeRoom = null;          // ei aktiivista huonetta
        freePosts = 1;              // haetaan yhtï¿½ vapaata tyï¿½pistettï¿½
        statusMessage = new String("");
        cursortype = java.awt.Cursor.DEFAULT_CURSOR;
        cursorlocked = false;
        this.roomScrollPane = null;
    }

    /**
     * Asettaa tarkasteltavan aikavï¿½lin takaisin vakioksi, eli tästä päivästä
     * kolme kuukautta eteenpäin
     */
    public void resetTimescale() {
        Calendar nowCal = Calendar.getInstance();
        Calendar cal = new GregorianCalendar(nowCal.get(Calendar.YEAR),
                nowCal.get(Calendar.MONTH),
                nowCal.get(Calendar.DAY_OF_MONTH));
        Calendar cal2 = (Calendar) cal.clone();
        // tästä päivästä kolme kuukautta eteenpäin
        cal2.add(Calendar.MONTH, 3);
        this.timescale = new TimeSlice(cal.getTime(), cal2.getTime());
        // osa-aikaväliksi koko aikaväli
        this.timescaleSlice = this.timescale;
        obsman.notifyObservers(NeroObserverTypes.TIMESCALE);
        obsman.notifyObservers(NeroObserverTypes.TIMESCALESLICE);
    }

    /**
     * Asettaa kï¿½ytettï¿½vï¿½n tietokantayhteyden. Metodia voi kutsua vain
     * kerran, sen jï¿½lkeen se heittï¿½ï¿½ poikkeuksen.
     *
     * @param db kï¿½ytettï¿½vï¿½ tietokantayhteys
     * Asettaa käytettävän tietokantayhteyden. Metodia voi kutsua vain kerran,
     * sen jälkeen se heittää poikkeuksen.
     * @param db käytettävä tietokantayhteys
     * @throws IllegalArgumentException jos annettu tk-yhteys on null
     * @throws IllegalStateException jos metodia kutsutaan uudelleen
     */
    public void setDatabase(NeroDatabase db) {
        if (db == null) {
            throw new IllegalArgumentException();
        }
        if (this.db != null) {
            throw new IllegalStateException();
        }
        this.db = db;
    }

    /**
     * Palauttaa kï¿½ytettï¿½vï¿½n tietokantayhteyden.
     * @return kï¿½ytettï¿½vï¿½ tietokantayhteys
     * Palauttaa käytettävän tietokantayhteyden.
     * @return käytettävä tietokantayhteys
     */
    public NeroDatabase getDatabase() {
        return db;
    }

    /**
     * Asettaa tarkasteltavan aikavï¿½lin.
     *
     * @param timescale uusi tarkasteltava aikavï¿½li
     * @throws IllegalArgumentException jos annettu aikavï¿½li on null
     */
    public void setFilterTimescale(TimeSlice timescale) {
        if (timescale == null) {
            throw new IllegalArgumentException();
        }
        this.timescale = timescale;

        // jos osa-aikavï¿½li ei mahdu uuden aikavï¿½lin sisï¿½lle, typistï¿½ sitï¿½
    	this.timescale = timescale;
        
        // jos osa-aikaväli ei mahdu uuden aikavälin sisälle, typistä sitä
        boolean sliceChanged = false;
        if (timescaleSlice.getStartDate().compareTo(timescale.getStartDate()) < 0) {
            timescaleSlice.setStartDate(timescale.getStartDate());
            sliceChanged = true;
        }
        if (timescaleSlice.getEndDate().compareTo(timescale.getEndDate()) > 0) {
            timescaleSlice.setEndDate(timescale.getEndDate());
            sliceChanged = true;
        }

        // Tyhjennï¿½ tyï¿½pisteiden tallettama tieto niihin liittyvistï¿½ varauksista, koska
        // aikavï¿½li on muuttunut ja sen vuoksi varaukset pitï¿½ï¿½ hakea uudelleen
    	// Tyhjennä työpisteiden tallettama tieto niihin liittyvistä varauksista, koska
    	// aikaväli on muuttunut ja sen vuoksi varaukset pitää hakea uudelleen
        // NOTE junit-testien aikana db saattaa olla null
        if (db != null) {
            Room[] rooms = db.getRooms();
            for (int i = 0; i < rooms.length; ++i) {
                Post[] posts = rooms[i].getPosts();
                for (int j = 0; j < posts.length; ++j) {
                    posts[j].clearReservations();
                }
            }
        }

        obsman.notifyObservers(NeroObserverTypes.TIMESCALE);
        if (sliceChanged) {
            obsman.notifyObservers(NeroObserverTypes.TIMESCALESLICE);
        }
    }

    /**
     * Palauttaa tarkasteltavan aikavälin.
     *
     * @return Aikaväli TimeSlice-oliona.
     */
    public TimeSlice getFilterTimescale() {
        return timescale;
    }

    /**
     * Asettaa hakuehtoja rajaavan projektin. Parametri voi olla null, jolloin
     * projektia ei ole.
     *
     * @param project Projekti
     */
    public void setFilterProject(Project project) {
        this.project = project;
        obsman.notifyObservers(NeroObserverTypes.FILTER_PROJECT);
        // tarvitaanko? kyllä tarvitaan, PersonScrollPane ei kuuntele projektia
        obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
    }

    /**
     * Palauttaa hakuehtoja rajaavan projektin.
     *
     * @return hakuehtoja rajaava projekti tai null, jos projektia ei ole
     */
    public Project getFilterProject() {
        return project;
    }

    /**
     * Asettaa hakuehtoja rajaavan henkilön nimen.
     *
     * @param personName Henkilön nimi.
     * @throws IllegalArgumentException jos annettu nimi on null
     */
    public void setFilterPersonName(String personName) {
        if (personName == null) {
            throw new IllegalArgumentException();
        }
        this.personName = personName;
        obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
    }

    /**
     * Palauttaa hakuehtoja rajaavan henkilön nimi
     *
     * @return henkilön nimi
     */
    public String getFilterPersonName() {
        return personName;
    }

    /**
     * Asettaa hakuehtoja rajaavan huoneen.
     *
     * @param roomName Huoneen nimi Exactumin huonekoodien muodossa, esim A212,
     * tai huoneen lempinimi, esim "sininen huone".
     * @throws IllegalArgumentException jos annettu nimi on null
     */
    public void setFilterRoomName(String roomName) {
        if (roomName == null) {
            throw new IllegalArgumentException();
        }
        this.roomName = roomName;
        obsman.notifyObservers(NeroObserverTypes.FILTER_ROOMS);
    }

    /**
     * Palauttaa hakuehtoja rajaavan huoneen
     *
     * @return huoneen nimi, esim. "A212" tai "sininen huone"
     */
    public String getFilterRoomName() {
        return roomName;
    }

    /**
     * Asettaa hakuehtoja rajaavan arvon sille, näytetäänkö vain ne henkilöt
     * joiden työsopimukset ovat loppumassa tarkasteltavalla aikavälillä.
     *
     * @param showEndingContracts Rajataanko haku.
     */
    public void setFilterEndingContracts(boolean showEndingContracts) {
        this.showEndingContracts = showEndingContracts;
        obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
    }

    /**
     * Palauttaa tiedon siitä, näytetäänkö vain ne henkilöt, joiden
     * työsopimukset ovat loppumassa tarkasteltavalla aikavälillä.
     *
     * @return rajataanko haku
     */
    public boolean getFilterEndingContracts() {
        return showEndingContracts;
    }

    /**
     * Asettaa hakuehtoja rajaavan huoneen työpisteiden maksimimäärän. Jos
     * parametri on -1, ei työpisteiden määrää rajata.
     *
     * @param maxPosts Työpisteiden lukumäärä tai -1
     * @throws IllegalArgumentException jos annetaan virheellinen lukumäärä
     * (alle -1)
     */
    public void setFilterMaxPosts(int maxPosts) {
        if (maxPosts < -1) {
            throw new IllegalArgumentException();
        }
        this.maxPosts = maxPosts;
        obsman.notifyObservers(NeroObserverTypes.FILTER_ROOMS);
    }

    /**
     * Palauttaa hakuehtoja rajaavan huoneen työpisteiden maksimimäärän.
     *
     * @return työpisteiden lukumäärä tai -1, jos määrää ei ole rajattu
     */
    public int getFilterMaxPosts() {
        return maxPosts;
    }

    /**
     * Asettaa hakuehtoja rajaavan määreen, jonka mukaan näytetään vain ne
     * joilla ei ole työpistevarausta aikavälin työsopimusten osalta.
     *
     * @param withoutPost haetaanko vain ne henkilöt, joilla ei ole tp-varausta
     */
    public void setFilterWithoutPost(boolean withoutPost) {
        this.withoutPost = withoutPost;
        obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
    }

    public boolean getFilterWithoutPost() {
        return this.withoutPost;
    }

    /**
     * Asettaa ehdon, jonka mukaan vapaina esitetään vain ne huoneet, joissa on
     * vähintään x vapaata työpistettä.
     *
     * @param freePosts
     * @throws IllegalArgumentException jos huonemäärä ei ole positiivinen luku
     */
    public void setFilterFreePosts(int freePosts) {
        if (freePosts <= 0) {
            throw new IllegalArgumentException();
        }
        this.freePosts = freePosts;
        obsman.notifyObservers(NeroObserverTypes.FILTER_ROOMS);
    }

    /**
     * Palauttaa huoneessa olevien vapaiden työpisteiden minimimäärän, joka on
     * edellytyksenä sille, että huone esitetään vapaana.
     *
     * @return vapaiden työpisteiden minimimäärä
     */
    public int getFilterFreePosts() {
        return freePosts;
    }

    /**
     * Voidaan rajata listattavia henkilöitä heidän aktiivisuuden perusteella
     *
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
     * Voidaan rajata listattavia henkilöitä voimassaolevan työsopimuksen
     * perusteella
     *
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
     * Palauttaa listan jossa on kaikki järjestelmän tuntemat projektit.
     *
     * @return Järjestelmän tuntemat projektit <code>Project[]</code>-oliona.
     */
    public Project[] getProjects() {
        return db.getProjects();
    }

    /**
     * Asettaa tarkasteltavalta aikaväliltä tarkasteltavan osa-aikavälin. Metodi
     * tarkistaa, että osa-aikaväli on tarkisteltavan aikavälin sisällä.
     *
     * @param timescaleSlice Tarkasteltava osa-aikaväli.
     * @param stillMoving Ei lähetä timescaleslicen muuttumisviestiä vaan
     * toisenlaisen tiedottamaan, että ollaan liikuttelemassa arvoa
     * @throws IllegalArgumentException jos osa-aikaväli ei ole aikavälin
     * sisällä tai on null
     */
    public void setTimeScaleSlice(TimeSlice timescaleSlice, boolean stillMoving) {
        if (timescaleSlice == null) {
            throw new IllegalArgumentException("osa-aikaväli ei saa olla null");
        }
        if (timescaleSlice.getStartDate().compareTo(
                this.timescale.getStartDate()) < 0) {
            throw new IllegalArgumentException("osa-aikaväli ei saa alkaa ennen aikaväliä");
        }
        if (timescaleSlice.getEndDate().compareTo(
                this.timescale.getEndDate()) > 0) {
            throw new IllegalArgumentException("osa-aikaväli ei saa päättyä myähemmin kuin aikaväli");
        }
        this.timescaleSlice = timescaleSlice;
        if (stillMoving) {
            obsman.notifyObservers(NeroObserverTypes.TIMESCALESLICEUPDATING);
        } else {
            obsman.notifyObservers(NeroObserverTypes.TIMESCALESLICE);
        }
    }

    /**
     * Palauttaa tarkasteltavan osa-aikavälin.
     *
     * @return tarkasteltava osa-aikaväli TimeSlice-oliona
     */
    public TimeSlice getTimeScaleSlice() {
        return timescaleSlice;
    }

    /**
     * Palauttaa kaikki järjestelmän tuntemat huoneet.
     *
     * @return taulukko huoneista
     */
    public Room[] getRooms() {
        return db.getRooms();
    }

    /**
     * Palauttaa hakuehtojen mukaiset huoneet.
     *
     * @return taulukko hakuehtojen mukaisista huoneista
     */
    public Room[] getFilteredRooms() {
        return db.getRooms(roomName, maxPosts);
    }

    /**
     * Palauttaa ne huoneet, joihin on sijoitettu hakuehtojen mukaisen projektin
     * työntekijöitä tarkasteltavalla osa-aikavälillä.
     *
     * @return taulukko projektin huoneista
     */
    public Room[] getProjectRooms() {
        return db.getRooms(project, timescale);
    }

    /**
     * Palauttaa listan sen hetkisten hakuehtojen mukaisista henkilöistä.
     *
     * @return taulukko henkilöolioista
     */
    public Person[] getFilteredPeople() {
        return db.getPeople(timescale, personName, project, showEndingContracts, withoutPost, partTimeTeachersOnly);
    }

    /**
     * Palauttaa työpisteen puhelinnumerot
     *
     * @param post Työpiste jonka numerot haetaan
     * @return Puhelinnumerot <code>PhoneNumber[]</code> Oliona.
     * @throws IllegalArgumentException jos työpiste on null
     */
    public PhoneNumber[] getPhoneNumbers(Post post) {
        if (post == null) {
            throw new IllegalArgumentException();
        }
        return db.getPhoneNumbers(post);
    }

    /**
     * Palauttaa "kaikki" puhelinnumerot ks. db:n vastaava metodi
     */
    public PhoneNumber[] getAllPhoneNumbers() {
        return db.getAllPhoneNumbers();
    }

    /**
     * Palauttaa aktiivisen huoneen.
     *
     * @return aktiivinen huone
     */
    public Room getActiveRoom() {
        return activeRoom;
    }

    /**
     * Asettaa valitun huoneen.
     *
     * @param activeRoom uusi valittu huone, tai null, jos halutaan ettei
     * valittua huonetta ole
     */
    public void setActiveRoom(Room activeRoom) {
        this.activeRoom = activeRoom;
        obsman.notifyObservers(NeroObserverTypes.ACTIVE_ROOM);
    }

    /**
     * Päivittää varausjakson esimerkin mukaiseksi.
     *
     * @param reservation varausjakso, joka päivitetään
     * @throws IllegalArgumentException jos varaus on null
     */
    public void updateReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("varaus ei voi olla null");
        }
        boolean success = db.updateReservation(reservation);
        if (!success) {
            setStatusMessage("Työpistevarauksen muuttaminen ei onnistunut.");
            return;
        }
        // kerrotaan työpisteelle että sen varaukset ovat muuttuneet
        reservation.getTargetPost().clearReservations();
        // varausten ja henkilöiden tiedot ovat muuttuneet, ilmoitetaan kuuntelijoille
        obsman.notifyObservers(NeroObserverTypes.RESERVATIONS);
        // XXX PersonScrollPane on FILTER_PEOPLEn ainoa kuuntelija, ja se kuuntelee myös RESERVATIONSia. Joten turha...
        //obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
        setStatusMessage("Työpistevarausta muutettu.");
        db.updateRooms();
    }

    /**
     * Poistaa varausjakson.
     *
     * @param reservation poistettava varausjakso
     * @throws IllegalArgumentException jos työpiste on null
     */
    public void deleteReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException();
        }
        if (db.deleteReservation(reservation)) {
            reservation.getTargetPost().clearReservations();
            // varausten ja henkilöiden tiedot ovat muuttuneet, ilmoitetaan kuuntelijoille
            obsman.notifyObservers(NeroObserverTypes.RESERVATIONS);
            // XXX PersonScrollPane on FILTER_PEOPLEn ainoa kuuntelija, ja se kuuntelee myös RESERVATIONSia. Joten turha...
            //obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
            setStatusMessage("Työpistevaraus poistettu.");
            db.updateRooms();
        } else {
            setStatusMessage("Työpistevarauksen poistaminen epäonnistui.");
        }
    }

    /**
     * Luo uuden varauksen annettuun työpisteeseen työsopimusjakson perusteella.
     * Jos työsopimusjakson henkilöllä ei ole työpistevarauksia jakson ajaksi,
     * tehdään työpistevaraus koko jakson mittaiseksi. Jos henkilöllä on
     * varauksia osalle jaksosta, tehdään työpistevaraus ensimmäiselle
     * varauksettomalle osalle työsopimusjaksosta. Jos henkilöllä on varauksia
     * koko jaksolle, ei tehdä mitään.
     *
     * @param post työpiste, johon varaus luodaan
     * @param contract työsopimusjakso, jonka perusteella varaus luodaan
     * @throws IllegalArgumentException jos työpiste tai sopimus on null
     */
    public void createReservation(Post post, Contract contract) {
        if (post == null) {
            throw new IllegalArgumentException("työpiste ei saa olla null");
        }
        if (contract == null) {
            throw new IllegalArgumentException("sopimus ei saa olla null");
        }
        createReservation(post, contract.getPerson(), contract.getTimeSlice());
        db.updateRooms();
    }

    /**
     * Luo uuden varauksen annettuun työpisteeseen tämänhetkiselle
     * osa-aikavälille. Varaus tehdään henkilölle työsopimusjaksoihin
     * katsomatta. Jos henkilöllä ei ole varauksia tarkasteltavalla
     * osa-aikavälillä, tehdään varaus koko osa-aikavälin mittaiseksi. Jos
     * henkilöllä on varauksia osalle osa-aikavälistä, tehdään varaus
     * ensimmäiselle varauksettomalle osalle osa-aikavälistä. Jos henkilöllä on
     * varauksia koko osa-aikavälille, ei tehdä mitään.
     *
     * @param post työpiste, johon varaus luodaan
     * @param person henkilö, jolle varaus luodaan
     * @throws IllegalArgumentException jos työpiste tai henkilö on null
     */
    public void createReservation(Post post, Person person) {
        createReservation(post, person, timescaleSlice);
        db.addRoomToPerson(person, post.getRoom().getRoomName());
    }

    /**
     * Luo uuden varauksen annettuun työpisteeseen annetulle aikavälille. Varaus
     * tehdään henkilölle työsopimusjaksoihin katsomatta. Jos henkilöllä ei ole
     * varauksia annetulla aikavälillä, tehdään varaus koko aikavälin
     * mittaiseksi. Jos henkilöllä on varauksia osalle aikavälistä, tehdään
     * varaus ensimmäiselle varauksettomalle osalle aikavälistä. Jos henkilöllä
     * on varauksia koko aikavälille, ei tehdä mitään.
     *
     * @param post työpiste, johon varaus luodaan
     * @param person henkilö, jolle varaus luodaan
     * @param timeSlice aikaväli, jolle varaus luodaan
     * @throws IllegalArgumentException jos työpiste, henkilö tai aikaväli on
     * null
     */
    public void createReservation(Post post, Person person, TimeSlice timeSlice) {
        if (post == null) {
            throw new IllegalArgumentException("työpiste ei saa olla null");
        }
        if (person == null) {
            throw new IllegalArgumentException("henkilö ei saa olla null");
        }
        if (timescaleSlice == null) {
            throw new IllegalArgumentException("aikaväli ei saa olla null");
        }

        // Rajataan tarvittaessa aikaväli tarkasteltavan aikajakson sisälle
        Date start = timeSlice.getStartDate();
        Date end = timeSlice.getEndDate();
        if (!timescale.contains(start)) {
            start = timescale.getStartDate();
        }
        if (!timescale.contains(end)) {
            end = timescale.getEndDate();
        }

        TimeSlice reservationTime = new TimeSlice(start, end);
        if (reservationTime.length() < 1) {
            System.out.println(" - Alku:   " + reservationTime.getStartDate()
                    + "\n - Loppu:  " + reservationTime.getEndDate()
                    + "\n - Pituus: " + reservationTime.length()
                    + "\n - Alkup. timeslice alku:  " + this.timescaleSlice.getStartDate()
                    + "\n - Alkup. timeslice loppu: " + this.timescaleSlice.getEndDate());
            setStatusMessage("Henkilöllä on jo työpiste aikavälillä " + timeSlice);
            return;
        }

        Reservation newRes = new Reservation(this, null, post, person, reservationTime, 0.0, "");
        if (db.createReservation(newRes)) {
            newRes.getTargetPost().clearReservations();
            // huoneiden ja henkilöiden tiedot ovat muuttuneet, ilmoitetaan kuuntelijoille
            obsman.notifyObservers(NeroObserverTypes.RESERVATIONS);
            // XXX PersonScrollPane on FILTER_PEOPLEn ainoa kuuntelija, ja se kuuntelee myös RESERVATIONSia. Joten turha...
            //obsman.notifyObservers(NeroObserverTypes.FILTER_PEOPLE);
            setStatusMessage("Työpistevaraus luotu.");
            db.addRoomToPerson(person, post.getRoom().getRoomName());
        } else {
            setStatusMessage("Työpistevarauksen luonti epäonnistui.");
        }
    }

    /**
     * Poistaa työpisteen.
     *
     * @param post poistettava työpiste
     * @throws IllegalArgumentException jos työpiste on null
     */
    public void deletePost(Post post) {
        if (post == null) {
            throw new IllegalArgumentException();
        }
        if (db.deletePost(post)) {
            switchActiveRoom();
            // nyt huoneiden tila on muuttunut, joten täytyy ilmoittaa kuuntelijoille
            obsman.notifyObservers(NeroObserverTypes.ROOMS);
            setStatusMessage("Työpiste " + post + " poistettu.");
        } else {
            setStatusMessage("Työpisteen " + post + " poistaminen epäonnistui.");
        }
    }

    /**
     * Luo aktiiviseen(valittuun) huoneeseen uuden työpisteen.
     *
     */
    public void createPost() {
        if (this.activeRoom == null) {
            throw new IllegalArgumentException();
        }
        Post newPost = new Post(this, null, this.activeRoom, 0);
        if (db.createPost(newPost)) {
            switchActiveRoom();
            // nyt huoneiden tila on muuttunut, joten täytyy ilmoittaa kuuntelijoille
            obsman.notifyObservers(NeroObserverTypes.ROOMS);
            setStatusMessage("Uusi työpiste luotu huoneeseen " + activeRoom);
        } else {
            setStatusMessage("Uuden työpisteen luominen epäonnistui.");
        }
    }

    /**
     * Vaihtaa aktiivisen huoneen kannasta haettuun uudempaan versioon. Metodia
     * kutsutaan kun on todennäköistä, että aktiivisen huoneen tiedot (mm.
     * työpisteet) ovat vanhentuneet.
     */
    private void switchActiveRoom() {
        // pyydetään db:ltä uudempi versio samasta huoneesta
        String roomID = activeRoom.getRoomID();
        activeRoom = db.getRoom(roomID);
    }

    /**
     * Lisää työpisteeseen puhelinnumeron. Jos puhelinnumero on jo jollakin
     * muulla työpisteellä, se siirtyy.
     *
     * @param post työpiste
     * @param phone lisättävä puhelinnumero
     * @throws IllegalArgumentException jos työpiste tai puhelinnumero on null
     */

    public void addPhoneNumber(Post post, PhoneNumber phone, String personID) {
        if(post == null && personID == null) {
            throw new IllegalArgumentException("työpiste ja henkilö eivät saa molemmat olla null");
        }
        if (phone == null) {
            throw new IllegalArgumentException("puhelinnumero ei saa olla null");
        }
    	// luodaan puhelinnumero-oliosta versio, joka viittaa uuteen työpisteeseen, tai uuteen henkilöön
            PhoneNumber newPhone = new PhoneNumber(phone, post, personID);
            if(db.updatePhoneNumber(newPhone)) {
                    // jos ollaan näyttämässä tätä samaa huonetta, päivitetään sen tiedot
                    if(this.activeRoom.getRoomID().equals(post.getRoom().getRoomID())) {
                            this.switchActiveRoom();
                    }
                    // nyt huoneiden tila on muuttunut, joten tï¿½ytyy ilmoittaa kuuntelijoille
                    obsman.notifyObservers(NeroObserverTypes.ROOMS);
                setStatusMessage("Puhelinnumero liitetty työpisteeseen.");
            } else {
                setStatusMessage("Puhelinnumeron liittäminen epäonnistui.");
            }
    }

    /**
     * Poistaa työpisteestä puhelinnumeron. Numero vapautuu.
     *
     * @param phone poistettava puhelinnumero
     * @throws IllegalArgumentException jos puhelinnumero on null
     */
    public void deletePhoneNumber(PhoneNumber phone) {
        if (phone == null) {
            throw new IllegalArgumentException();
        }
        // luodaan puhelinnumero-oliosta versio, joka ei viittaa mihinkään työpisteeseen
        //PhoneNumber newPhone = new PhoneNumber(phone, null);
        if (db.removePhoneNumberFromPost(phone)) {
            // ei tietoa ollaanko juuri tätä näyttämässä, mutta päivitetään silti
            // vrt. tarkastukset updatePhoneNumberissa ^^
            this.switchActiveRoom();
            // nyt huoneiden tila on muuttunut, joten täytyy ilmoittaa kuuntelijoille
            obsman.notifyObservers(NeroObserverTypes.ROOMS);
            setStatusMessage("Puhelinnumero poistettu työpisteestä.");
        } else {
            setStatusMessage("Puhelinnumeron poistaminen epäonnistui.");
        }
    }

    /* Dataolioiden tarvitsemat tiedonhakuoperaatiot */
    /**
     * Palauttaa henkilöön liittyvät työsopimukset tämänhetkisellä aikavälillä.
     *
     * @param person henkilö, jonka työsopimukset halutaan
     * @return henkilön työsopimukset
     * @throws IllegalArgumentException jos henkilö on null
     */
    public Contract[] getContracts(Person person) {
        if (person == null) {
            throw new IllegalArgumentException();
        }
        return db.getContracts(person, timescale);
    }

    /**
     * Sama annetulle aikavälille
     *
     * @param person henkilö, jonka työsopimukset halutaan
     * @param timeslice aikaväli
     * @return sopimukset
     */
    public Contract[] getContracts(Person person, TimeSlice timeslice) {
        return db.getContracts(person, timeslice);
    }

    /**
     * Palauttaa henkilöön liittyvät työpistevaraukset tämänhetkisellä
     * aikavälillä.
     *
     * @param person henkilö, jonka työpistevaraukset halutaan
     * @return henkilön työpistevaraukset
     * @throws IllegalArgumentException jos henkilö on null
     */
    public Reservation[] getReservations(Person person) {
        if (person == null) {
            throw new IllegalArgumentException();
        }
        return db.getReservations(person, timescale);
    }

    /**
     * Palauttaa työpisteeseen liittyvät työpistevaraukset tämänhetkisellä
     * aikavälillä. Varaukset palautetaan järjestettynä ensisijaisesti
     * alkuajankohdan, toissijaisesti loppuajankohdan mukaan.
     *
     * @param post työpiste, jonka työpistevaraukset halutaan
     * @return työpisteen varaukset
     * @throws IllegalArgumentException jos työpiste on null
     */
    public Reservation[] getReservations(Post post) {
        if (post == null) {
            throw new IllegalArgumentException();
        }
        return db.getReservations(post, timescale);
    }

    /**
     * Asettaa tekstimuotoisen viestin käyttöliittymän näytettäväksi ja tulostaa
     * sen System.out.println():lla
     *
     * @param message viesti
     */
    public void setStatusMessage(String message) {
        statusMessage = message;
        obsman.notifyObservers(NeroObserverTypes.STATUSBAR);
        System.out.println(message);
    }

    /**
     * Asettaa tekstimuotoisen viestin käyttöliittymän näytettäväksi.
     *
     * @param message viesti
     */
    public void setStatusMessageNoPrint(String message) {
        statusMessage = message;
        obsman.notifyObservers(NeroObserverTypes.STATUSBAR);
    }

    /**
     * Palauttaa asetetun tekstimuotoisen viestin.
     *
     * @return viesti
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    public void waitState(boolean on) {
        if (on) {
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
        if (this.cursorlocked || this.cursortype == cursortype) {
            return;
        }
        this.cursortype = cursortype;
        obsman.notifyObservers(NeroObserverTypes.CURSORCHANGE);
    }

    public int getCursorType() {
        return this.cursortype;
    }

    public RoomKeyReservation[] getRoomKeyReservations() {
        return db.getRoomKeyReservations(activeRoom);
    }

    public void addRoomKeyReservation(Person person, TimeSlice timeslice) {
        this.activeRoom.addRoomKeyReservation(new RoomKeyReservation(this.getActiveRoom().getRoomKeyReservations().size(), this.getActiveRoom(), person.getPersonID(), person.getName(), timeslice, this));
        db.addRoomKeyReservation(this.activeRoom, person, timeslice);
        this.roomScrollPane.updateObserved(NeroObserverTypes.ACTIVE_ROOM);
    }

    public void deleteRoomkeyReservation(RoomKeyReservation roomKeyReservation) {
        this.activeRoom.deleteRoomKeyReservation(roomKeyReservation);
        db.deleteRoomKeyReservation(roomKeyReservation.getReservationID());
        this.roomScrollPane.updateObserved(NeroObserverTypes.ACTIVE_ROOM);
    }
    /* Kuuntelijoihin liittyvät operaatiot */

    /**
     * Rekisteröi kuuntelijan jollekin tapahtumatyypille. Kuuntelijalle
     * ilmoitetaan, kun valituntyyppinen muutos tapahtuu.
     *
     * @param type kuuntelijan tyyppi
     * @param observer kuuntelijarajapinnan toteuttava olio
     * @throws IllegalArgumentException jos observer-parametri on null
     * @see fi.helsinki.cs.nero.event.NeroObserver
     * @see fi.helsinki.cs.nero.event.NeroObserverTypes
     */
    public ArrayList<HashMap<String, String>> getKannykat() {
        return this.db.getKannykat();
    }

    public void registerObserver(int type, NeroObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException();
        }
        obsman.addObserver(type, observer);
    }

    public void saveNewPerson(Person person) throws SQLException {
        db.createPerson(person);
    }

    public void updatePerson(Person person) throws SQLException {
        db.updatePersonInfo(person);
    }
}
