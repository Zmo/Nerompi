package fi.helsinki.cs.nero.db;

import fi.helsinki.cs.nero.NeroApplication;
import fi.helsinki.cs.nero.data.Contract;
import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.PhoneNumber;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.data.Project;
import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.data.Room;
import fi.helsinki.cs.nero.data.RoomKeyReservation;
import fi.helsinki.cs.nero.data.TimeSlice;
import fi.helsinki.cs.nero.event.NeroObserver;
import fi.helsinki.cs.nero.event.NeroObserverTypes;
import fi.helsinki.cs.nero.logic.Session;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jyrki Muukkonen
 */
public class NeroDatabase implements NeroObserver {

    /**
     * Tietokantayhteys
     */
    private Connection connection;
    /**
     * Session johon tï¿½mï¿½ tietokantayhteys liittyy.
     */
    private Session session;
    /**
     * Kaikki jï¿½rjestelmï¿½n tuntemat huoneet tyï¿½pisteineen ja
     * puhelinnumeroineen. Hajautusrakenne, jossa avaimena on huoneen id ja
     * arvona huoneolio.
     */
    private Map rooms;
    /**
     * Kaikki jï¿½rjestelmï¿½n tuntemat projektit. Hajautusrakenne, jossa
     * avaimena on projektin id ja arvona projektiolio.
     */
    private Map projects;
    /**
     * Kaikki jï¿½rjestelmï¿½n tuntemat tyï¿½pisteet. Hajautusrakenne, jossa
     * avaimena on tyï¿½pisteen id ja arvona tyï¿½pisteolio.
     */
    private Map posts;
    /**
     * Kaikki jï¿½rjestelmï¿½n tuntemat puhelinnumerot. Hajautusrakenne, jossa
     * avaimena on tyï¿½pisteen id (tai "free") ja arvona vektori
     * puhelinnumeroista
     */
    private Map phoneNumbers;
    /**
     * Jï¿½rjestelmï¿½n tuntemia henkilï¿½itï¿½, jotka on jo ladattu
     * tietokannasta. Hajautusrakenne, jossa avaimena on henkilï¿½n tunniste
     * (htunnus) ja arvona henkilï¿½olio.
     */
    private Map people = new Hashtable();
    /**
     * Kaikki järjestelmän tuntemat avainvaraukset
     */
    private ArrayList<RoomKeyReservation> roomKeyReservations;
    /**
     * Tietojen esilataamisessa kï¿½ytetyt preparedStatementit
     */
    private PreparedStatement prepAllRooms;
    private PreparedStatement prepAllPosts;
    private PreparedStatement prepAllPhoneNumbers;
    /**
     * getReservations(Person person, TimeSlice timeslice)-metodin
     * kï¿½yttï¿½mï¿½t PreparedStatementit.
     */
    private PreparedStatement prepPersonReservations;
    /**
     * getRerservations(Post post, TimeSlice timeslice)-metodin kï¿½yttï¿½mï¿½t
     * PreparedStatementit.
     */
    private PreparedStatement prepPostReservations;
    /**
     * getPersons()-metodin kï¿½yttï¿½mï¿½t PreparedStatementit.
     */
    private PreparedStatement prepPersonContracts;
    /**
     * henkilötietohakuun käytettävä PreparedStatement.
     */
    private PreparedStatement prepPersonInfo;
    /**
     * getRooms(Project project, TimeSlice timescale)-metodin kï¿½yttï¿½mï¿½t
     * PreparedStatementit.
     */
    private PreparedStatement prepProjectRooms;
    /**
     * getRooms(String roomName, int maxPosts)-metodin kï¿½yttï¿½mï¿½t
     * PreparedStatementit.
     */
    private PreparedStatement prepNamedRoomsNormal, prepNamedRoomsWithMaxPosts;
    /**
     * Varausten kï¿½sittelyssï¿½ tarvittavat PreparedStatementit
     */
    private PreparedStatement prepNextReservationID;
    private PreparedStatement prepAddReservation;
    private PreparedStatement prepUpdateReservation;
    private PreparedStatement prepDeleteReservation;
    /**
     * getPeople()-metodissa kï¿½ytetyt PreparedStatementit talletetaan
     * hajautukseen, jossa avaimena on kyselyn SQL-koodi Stringinï¿½ ja arvona
     * PreparedStatement
     */
    private Map prepFilteredPeople = new Hashtable();
    /**
     * Tyï¿½pisteiden kï¿½sittelyssï¿½ tarvittavat PreparedStatementit
     */
    private PreparedStatement prepNextPostID;
    private PreparedStatement prepCreatePost;
    private PreparedStatement prepDeletePost;
    private PreparedStatement prepRoomKeyReservations;
    private PreparedStatement prepRoomKeyReservationName;
    /**
     * Puhelinnumeroihin liittyvï¿½t PreparedStatementit
     */
    private PreparedStatement prepPostPhoneNumbers;
    private PreparedStatement prepUpdatePhoneNumber;
    private HashMap<String, String> henkiloHash;
    private HashMap<String, String> varausHash;

    /**
     * Konstruktori. Luo yhteyden tietokantaan ja esilataa tiedot huoneista,
     * puhelinnumeroista sekï¿½ projekteista.
     *
     * @param session Sessio, johon tï¿½mï¿½ tietokantaolio liittyy
     * @param className Tietokanta-ajurin luokan nimi
     * @param connectionString Tietokantayhteyden nimi
     * @param username Kï¿½yttï¿½jï¿½tunnus
     * @param password Salasana
     */
    public NeroDatabase(Session session, String className,
            String connectionString, String username, String password) {
        this.session = session;
        // kuunnellaan aikajakson ja huonedatan muutoksia, jotta voidaan varmistua, etteivï¿½t
        // talletetut henkilï¿½oliot sisï¿½llï¿½ vanhentunutta tietoa
        session.registerObserver(NeroObserverTypes.TIMESCALE, this);
        session.registerObserver(NeroObserverTypes.ROOMS, this);

        session.waitState(true);
        try {
            this.connection = this.createConnection(className,
                    connectionString, username, password);
            this.loadRooms();
            this.loadPhoneNumbers();
            //this.loadProjects();
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe 1: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Tietokanta-ajuria ei lï¿½ydy.");
        }
        session.waitState(false);
    }

    /**
     * Luo tietokantayhteyden.
     *
     * @param className Tietokanta-ajurin luokan nimi
     * @param connectionString Tietokantayhteyden nimi
     * @param username Kï¿½yttï¿½jï¿½tunnus
     * @param password Salasana
     * @return Luotu tietokantayhteys
     * @throws SQLException Jos yhteyden luonti epï¿½onnistuu
     * @throws ClassNotFoundException Jos tietokanta-ajuria ei lï¿½ydy
     */
    private Connection createConnection(String className,
            String connectionString, String username, String password)
            throws SQLException, ClassNotFoundException {
        Connection conn = null;
        Class.forName(className);
        conn = DriverManager.getConnection(connectionString, username, password);
        return conn;
    }

    /**
     * Lataa jï¿½rjestelmï¿½n tuntemat projektit projects-hajautukseen
     * myï¿½hempï¿½ï¿½ kï¿½yttï¿½ï¿½ varten.
     *
     * @throws SQLException
     */
//	private void loadProjects() throws SQLException {
//		this.projects = new Hashtable();
//
//		session.setStatusMessage("Ladataan projekteja...");
//		if(this.prepAllProjects == null) {
//			this.prepAllProjects = this.connection.prepareStatement(
//					"SELECT koodi, nimi, vastuuhenkilo, alkupvm,"
//					/* loppupvm voi olla null, kï¿½ytetï¿½ï¿½n 2099-12-31 */
//					+ " NVL(loppupvm, TO_DATE('2099-12-31', 'YYYY-MM-DD')) AS loppupvm"
//					+ " FROM PROJEKTI"
//					+ " ORDER BY nimi, alkupvm"
//			);
//		}
//		ResultSet rs = prepAllProjects.executeQuery();
//
//		while (rs.next()) {
//			TimeSlice slice = null;
//			Date start = rs.getDate("alkupvm");
//			Date end = rs.getDate("loppupvm");
//
//			Project p = new Project(this.session, rs.getString("koodi"),
//								rs.getString("nimi"),
//								rs.getString("vastuuhenkilo"), slice);
//			this.projects.put(rs.getString("koodi"), p);
//		}
//		rs.close();
//        session.setStatusMessage("Ladattu tiedot " + this.projects.size() + " projektista.");
//	}
    /**
     * Lataa jï¿½rjestelmï¿½n tuntemat huoneet tyï¿½pisteineen
     * rooms-hajautukseen myï¿½hempï¿½ï¿½ kï¿½yttï¿½ï¿½ varten.
     *
     * @throws SQLException
     */
    private void loadRooms() throws SQLException {
        this.rooms = new Hashtable();
        this.posts = new Hashtable();
        this.roomKeyReservations = new ArrayList();
        Collection roomPosts = new LinkedList();
        Collection phoneNumbers = new LinkedList();
        session.setStatusMessage("Ladataan huoneita...");
        if (this.prepAllRooms == null) {
            this.prepAllRooms = this.connection.prepareStatement(
                    "SELECT rh.id, rak.nimi AS rakenn_nimi, rh.kerros_numero, rh.numero,"
                    + " rh.nimi, rh.pinta_ala, rh.kuvaus"
                    + " FROM RHUONE rh, RAKENNUS rak"
                    + " WHERE rh.rakenn_tunnus = rak.tunnus");
        }
        if (this.prepAllPosts == null) {
            this.prepAllPosts = this.connection.prepareStatement(
                    /*
                     "SELECT tp.id as tp_id, puh.id as puh_id, puh.puhelinnumero"
                     + "FROM TYOPISTE tp, PUHELINNUMERO puh"
                     + " WHERE rhuone_id = ? AND puh.tp_id = tp.id"
                     + " ORDER BY tp.id, puh.puhelinnumero");
                     */
                    "SELECT id, lisayspvm FROM TYOPISTE WHERE rhuone_id = ? ORDER BY id");
        }
        if (this.prepPostPhoneNumbers == null) {
            this.prepPostPhoneNumbers = this.connection.prepareStatement(
                    "SELECT id, puhelinnumero, h_tunnus FROM PUHELINNUMERO WHERE tp_id = ?");
        }
        if (this.prepRoomKeyReservations == null) {
            this.prepRoomKeyReservations = this.connection.prepareStatement(
                    "SELECT id, htunnus, rhuone_id, alkupvm, loppupvm FROM HUONEVARAUS where RHUONE_ID=?");
        }
        if (this.prepRoomKeyReservationName == null) {
            this.prepRoomKeyReservationName = this.connection.prepareStatement(
                    "SELECT sukunimi, etunimet FROM HENKILO WHERE htunnus=?");
        }

        ResultSet rs = this.prepAllRooms.executeQuery();
        /* NOTE rhuone-taulussa on sekä "numero" että "huone_nro" kentät */
        int numbercount = 0;
        while (rs.next()) {


            Room room = new Room(this.session, rs.getString("id"),
                    rs.getString("rakenn_nimi"),
                    rs.getString("kerros_numero"),
                    rs.getString("numero"), rs.getString("nimi"),
                    rs.getDouble("pinta_ala"), rs.getString("kuvaus"));

            this.prepAllPosts.setString(1, rs.getString("id"));
            ResultSet prs = this.prepAllPosts.executeQuery();
            while (prs.next()) {
                Post post = new Post(this.session, prs.getString("id"), room, roomPosts.size() + 1, prs.getDate("lisayspvm"));

                /* haetaan puhelinnumerot (hidas, kolme sisï¿½kkï¿½istï¿½ prepared statementia
                 * mutta olkoot. */
                Collection numbers = new Vector();
                this.prepPostPhoneNumbers.setString(1, post.getPostID());
                ResultSet pnrs = this.prepPostPhoneNumbers.executeQuery();
                while (pnrs.next()) {
                    PhoneNumber pn = new PhoneNumber(this.session,
                            pnrs.getString("id"), post,
                            pnrs.getString("puhelinnumero"), pnrs.getString("h_tunnus"));
                    numbers.add(pn);
                    numbercount++;

                }
                pnrs.close();
                post.setPhoneNumbers((PhoneNumber[]) numbers.toArray(new PhoneNumber[0]));
                roomPosts.add(post);
                this.posts.put(prs.getString("id"), post);
            }
            prs.close();
            room.setPosts((Post[]) roomPosts.toArray(new Post[0]));
            prepRoomKeyReservations.setString(1, rs.getString("id"));
            ResultSet roomKeysResult = this.prepRoomKeyReservations.executeQuery();
            while (roomKeysResult.next()) {
                prepRoomKeyReservationName.setString(1, roomKeysResult.getString("HTUNNUS"));
                ResultSet nameResults = this.prepRoomKeyReservationName.executeQuery();
                if (roomKeysResult.getString("RHUONE_ID").equalsIgnoreCase(room.getRoomID())) {
                    TimeSlice timeslice = new TimeSlice(roomKeysResult.getTimestamp("ALKUPVM"), roomKeysResult.getTimestamp("LOPPUPVM"));
                    nameResults.next();
                    RoomKeyReservation keyReservation = new RoomKeyReservation(
                            roomKeysResult.getInt("ID"), room, roomKeysResult.getString("htunnus"),
                            nameResults.getString("SUKUNIMI") + " " + nameResults.getString("ETUNIMET"), timeslice, this.session);
                    room.addRoomKeyReservation(keyReservation);
                    this.roomKeyReservations.add(keyReservation);
                }

            }
      
            //roomKeysResult;
            roomPosts.clear();
            this.rooms.put(rs.getString("id"), room);
        }
		
		rs.close();
		session.setStatusMessage("Ladattu tiedot " + this.rooms.size() + " huoneesta.");
    }

	/**
	 * Lataa jï¿½rjestelmï¿½n tuntemat puhelinnumerot.
	 * Saa kutsua vasta loadRooms() jï¿½lkeen.
	 * @throws SQLException
	 */
	private void loadPhoneNumbers() throws SQLException {
		this.phoneNumbers = new Hashtable();

		session.setStatusMessage("Ladataan puhelinnumeroita...");
		if(this.prepAllPhoneNumbers == null) {
			this.prepAllPhoneNumbers = this.connection.prepareStatement(
					"SELECT id, puhelinnumero, tp_id, h_tunnus FROM PUHELINNUMERO"
			);
                        
		}             
                
		ResultSet rs = prepAllPhoneNumbers.executeQuery();
		
		int count = 0;
		while (rs.next()) {
			PhoneNumber pn;
			String tpid = rs.getString("tp_id");
			String pnid = rs.getString("id");
			String number = rs.getString("puhelinnumero");
                        String htunnus = rs.getString("h_tunnus");
                        
                        
			if(tpid == null) {
				pn = new PhoneNumber(this.session, pnid, null, number, htunnus);
				tpid = "free";
			} else {
				pn = new PhoneNumber(this.session, pnid, 
						(Post)this.posts.get(tpid), number, htunnus);
			}
			Collection tpn = (Collection)this.phoneNumbers.get(tpid);
			if(tpn == null) {
				tpn = new Vector();
				this.phoneNumbers.put(tpid, tpn);
			}
			tpn.add(pn);
			count++;
		}
		rs.close();
		session.setStatusMessage("Ladattu tiedot " + count + " puhelinnumerosta.");
	}
	

	/* --- Tyï¿½pistevarauksiin liittyvï¿½t metodit alkaa --- */ 
	
	/**
	 * Palauttaa parametrina annetun henkilï¿½n tyï¿½pistevaraukset, jotka
	 * leikkaavat parametrina annettua aikavï¿½liï¿½. Varaukset palautetaan
     * jï¿½rjestettynï¿½ ensisijaisesti alkuajankohdan, toissijaisesti loppuajankohdan
     * mukaan.
	 * 
	 * @param person
	 *            Henkilï¿½, jonka tyï¿½pistevarauksia haetaan.
	 * @param timeslice
	 *            Aikavï¿½li, jonka aikana varauksen tulee olla ainakin osittain
	 *            voimassa.
	 * @return varaukset <code>Reservation[]</code> oliona.
	 */

	public Reservation[] getReservations(Person person, TimeSlice timeslice) {
		Collection reservations = new Vector();
		this.session.waitState(true);
		try {
			if(this.prepPersonReservations ==  null) {
				this.prepPersonReservations = this.connection.prepareStatement(
						"SELECT DISTINCT tpv.id, tpv.alkupvm, tpv.loppupvm,"
						+ " tpv.viikkotunnit, tpv.selite, tpv.tpiste_id, tp.rhuone_id"
						+ " FROM TYOPISTEVARAUS tpv, TYOPISTE tp"
						+ " WHERE tpv.henklo_htunnus = ?"
						+ " AND tpv.tpiste_id = tp.id"
						+ " AND ? <= tpv.loppupvm AND ? >= tpv.alkupvm"
						+ " ORDER BY tpv.alkupvm, tpv.loppupvm"
				);
			}
			this.prepPersonReservations.setString(1, person.getPersonID());
			this.prepPersonReservations.setDate(2, timeslice.getSQLStartDate());
			this.prepPersonReservations.setDate(3, timeslice.getSQLEndDate());
			
			ResultSet rs = this.prepPersonReservations.executeQuery();
			while(rs.next()) {
				Date start = new Date(rs.getDate("alkupvm").getTime());
				Date end = new Date(rs.getDate("loppupvm").getTime());
				TimeSlice ts = new TimeSlice(start, end);
				Post post = (Post) this.posts.get(rs.getString("tpiste_id"));
				
				Reservation r = new Reservation(this.session,
						rs.getString("id"), post, person, ts,
						rs.getDouble("viikkotunnit"), rs.getString("selite"));
				reservations.add(r);
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Tietokantavirhe: " + e.getMessage());
		}
		this.session.waitState(false);
		return (Reservation[])reservations.toArray(new Reservation[0]);
	}
        
//        public ArrayList<HashMap<String, String>> getKannykat() {
//            HashMap hashMap;
//            ArrayList arry = new ArrayList<HashMap<String, String>>();
//            
//            try {
//            
//            }
//            roomPosts.clear();
//            this.rooms.put(rs.getString("id"), room);
//        
//        rs.close();
//        session.setStatusMessage("Ladattu tiedot " + this.rooms.size() + " huoneesta.");
//    }

    /**
     * Lataa jï¿½rjestelmï¿½n tuntemat puhelinnumerot. Saa kutsua vasta
     * loadRooms() jï¿½lkeen.
     *
     * @throws SQLException
     */
    


    /* --- Tyï¿½pistevarauksiin liittyvï¿½t metodit alkaa --- */
    /**
     * Palauttaa parametrina annetun henkilï¿½n tyï¿½pistevaraukset, jotka
     * leikkaavat parametrina annettua aikavï¿½liï¿½. Varaukset palautetaan
     * jï¿½rjestettynï¿½ ensisijaisesti alkuajankohdan, toissijaisesti
     * loppuajankohdan mukaan.
     *
     * @param person Henkilï¿½, jonka tyï¿½pistevarauksia haetaan.
     * @param timeslice Aikavï¿½li, jonka aikana varauksen tulee olla ainakin
     * osittain voimassa.
     * @return varaukset <code>Reservation[]</code> oliona.
     */
   

    public ArrayList<HashMap<String, String>> getKannykat() {
        HashMap hashMap;
        ArrayList arry = new ArrayList<HashMap<String, String>>();

        try {

            PreparedStatement prepKannykat = this.connection.prepareStatement("SELECT * FROM KANNYKKA");
            ResultSet rs = prepKannykat.executeQuery();
            while (rs.next()) {
                hashMap = new HashMap<String, String>();
                hashMap.put("puh_id", rs.getString("puh_id"));
                hashMap.put("numero", rs.getString("kannukka_numero"));
                hashMap.put("omistaja", rs.getString("omistaja"));
                arry.add(hashMap);
            }
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
        }
        return arry;
    }

    public void addKannykka(String kannukka, String htunnus, String omistaja, String tyo) {

        PreparedStatement prepAddKannykka;

        String sqlQuery = "INSERT INTO HUONEVARAUS (PUH_ID, KANNYKKA_NUMERO, HTUNNUS, OMISTAJA, TYO_NUMERO) VALUES ((SELECT MAX(PUH_ID) FROM KANNYKKA)+1, ?, ?, ?, ?)";

        try {
            prepAddKannykka = this.connection.prepareStatement(sqlQuery);

            prepAddKannykka.setString(1, kannukka);
            prepAddKannykka.setString(2, htunnus);
            prepAddKannykka.setString(3, omistaja);
            prepAddKannykka.setString(4, tyo);
            prepAddKannykka.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
        }

    }

    /**
     * Palauttaa parametrina annetun tyï¿½pisteen varaukset, jotka ovat ainakin
     * osittain pï¿½ï¿½llekï¿½in annetun aikavï¿½li kanssa.
     *
     * @param post Tyï¿½piste, jonka varauksia haetaan.
     * @param timeslice Aikavï¿½li, jolla varausten tulee olla ainakin osittain
     * voimassa.
     * @return Palautaa varaukset <code>Reservation[]</code> oliona.
     */
    public Reservation[] getReservations(Post post, TimeSlice timeslice) {
        Collection reservations = new Vector();
        this.session.waitState(true);
        try {
            if (this.prepPostReservations == null) {
                this.prepPostReservations = this.connection.prepareStatement(
                        "SELECT DISTINCT tpv.id, tpv.alkupvm, tpv.loppupvm,"
                        + " tpv.viikkotunnit, tpv.selite,"
                        + " h.htunnus, h.etunimet, h.sukunimi, h.huone_nro,"
                        + " h.kutsumanimi,"
                        + " h.aktiivisuus, h.hetu, h.oppiarvo, h.titteli,"
                        + " h.puhelin_tyo, h.puhelin_koti, h.katuosoite, h.katuosoite,"
                        + " h.postinro, h.postitoimipaikka, h.valvontasaldo, h.sahkopostiosoite,"
                        + " h.hallinnollinen_kommentti, h.opiskelija_kommentti, h.ktunnus,"
                        + " h.kannykka, h.postilokerohuone, h.hy_tyosuhde, h.hy_puhelinluettelossa"
                        + " FROM TYOPISTEVARAUS tpv, HENKILO h"
                        + " WHERE tpv.tpiste_id = ?"
                        + " AND tpv.henklo_htunnus = h.htunnus"
                        + " AND ? <= tpv.loppupvm AND ? >= tpv.alkupvm");
            }
            this.prepPostReservations.setString(1, post.getPostID());
            this.prepPostReservations.setDate(2, timeslice.getSQLStartDate());
            this.prepPostReservations.setDate(3, timeslice.getSQLEndDate());

            ResultSet rs = this.prepPostReservations.executeQuery();
            while (rs.next()) {
                Date start = new Date(rs.getDate("alkupvm").getTime());
                Date end = new Date(rs.getDate("loppupvm").getTime());
                TimeSlice ts = new TimeSlice(start, end);

                varausHash = new HashMap();
                varausHash.put("htunnus", rs.getString("htunnus"));
                varausHash.put("kokonimi", (rs.getString("sukunimi") + " " + rs.getString("etunimet")));
                varausHash.put("etunimet", rs.getString("etunimet"));
                varausHash.put("sukunimi", rs.getString("sukunimi"));
                varausHash.put("huone_nro", rs.getString("huone_nro"));
                varausHash.put("kutsumanimi", rs.getString("kutsumanimi"));
                varausHash.put("aktiivisuus", rs.getString("aktiivisuus"));
                varausHash.put("hetu", rs.getString("hetu"));
                varausHash.put("oppiarvo", rs.getString("oppiarvo"));
                varausHash.put("titteli", rs.getString("titteli"));
                varausHash.put("puhelin_tyo", rs.getString("puhelin_tyo"));
                varausHash.put("puhelin_koti", rs.getString("puhelin_koti"));
                varausHash.put("katuosoite", rs.getString("katuosoite"));
                varausHash.put("postinro", rs.getString("postinro"));
                varausHash.put("postitoimipaikka", rs.getString("postitoimipaikka"));
                varausHash.put("valvontasaldo", rs.getString("valvontasaldo"));
                varausHash.put("sahkopostiosoite", rs.getString("sahkopostiosoite"));
                varausHash.put("hallinnollinen_kommentti", rs.getString("hallinnollinen_kommentti"));
                varausHash.put("opiskelija_kommentti", rs.getString("opiskelija_kommentti"));
                varausHash.put("ktunnus", rs.getString("ktunnus"));
                varausHash.put("kannykka", rs.getString("kannykka"));
                varausHash.put("postilokerohuone", rs.getString("postilokerohuone"));
                varausHash.put("hy_tyosuhde", rs.getString("hy_tyosuhde"));
                varausHash.put("hy_puhelinluettelossa", rs.getString("hy_puhelinluettelossa"));


                Person person = new Person(this.session, varausHash, null, null);


//				Person person = new Person(this.session, rs.getString("htunnus"),
//                                        rs.getString("sukunimi")+" "+rs.getString("etunimet"),
//                                        rs.getString("etunimet"), rs.getString("sukunimi"),
//                                        null, null, rs.getString("huone_nro"), rs.getString("kutsumanimi"),
//                                        rs.getString("aktiivisuus"), rs.getString("hetu"), rs.getString("oppiarvo"),
//                                        rs.getString("titteli"), rs.getString("puhelin_tyo"), rs.getString("puhelin_koti"),
//                                        rs.getString("katuosoite"), rs.getString("postinro"), rs.getString("postitoimipaikka"),
//                                        rs.getString("valvontasaldo"), rs.getString("sahkopostiosoite"),
//                                        rs.getString("hallinnollinen_kommentti"), rs.getString("opiskelija_kommentti"),
//                                        rs.getString("ktunnus"), rs.getString("kannykka"), rs.getString("postilokerohuone"),
//                                        rs.getString("hy_tyosuhde"), rs.getString("hy_puhelinluettelossa"));

                Reservation r = new Reservation(this.session,
                        rs.getString("id"), post, person, ts,
                        rs.getDouble("viikkotunnit"), rs.getString("selite"));
                reservations.add(r);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
        }
        this.session.waitState(false);
        return (Reservation[]) reservations.toArray(new Reservation[0]);
    }

    /**
     * Lisï¿½ï¿½ kantaan parametrinï¿½ annetun varauksen.
     *
     * @param reservation Uusi varaus, joka lisï¿½tï¿½ï¿½n kantaan.
     * @return Onnistuiko lisï¿½ys.
     */
    public boolean createReservation(Reservation reservation) {
        boolean success = false;
        this.session.waitState(true);
        try {
            /* seuraava vapaa ID _tï¿½ytyy_ hakea ensin
             * jï¿½lkipolville: "SELECT * FROM USER_SEQUENCES" */
            if (this.prepNextReservationID == null) {
                this.prepNextReservationID = this.connection.prepareStatement(
                        "SELECT seq_tpvara_id.NEXTVAL FROM dual");
            }
            ResultSet rsid = this.prepNextReservationID.executeQuery();
            rsid.next();
            int nextID = rsid.getInt(1);
            rsid.close();
            // voisi asettaa annetulle Reservationille ID:n jos olisi tarve 

            if (this.prepAddReservation == null) {
                this.prepAddReservation = this.connection.prepareStatement(
                        " INSERT INTO tyopistevaraus"
                        + " (id, tpiste_id, henklo_htunnus, viikkotunnit,"
                        + "  selite, alkupvm, loppupvm, lisayspvm)"
                        /* TRUNC lyhentï¿½ï¿½ ajan pelkï¿½ksi pï¿½ivï¿½mï¿½ï¿½rï¿½ksi */
                        + " VALUES (?, ?, ?, ?, ?, TRUNC(?), TRUNC(?), SYSDATE)");
            }
            this.prepAddReservation.setInt(1, nextID);
            this.prepAddReservation.setString(2, reservation.getTargetPost().getPostID());
            this.prepAddReservation.setString(3, reservation.getReservingPerson().getPersonID());
            this.prepAddReservation.setInt(4, (int) (reservation.getWeeklyHours()));
            this.prepAddReservation.setString(5, reservation.getDescription());
            this.prepAddReservation.setDate(6, reservation.getTimeSlice().getSQLStartDate());
            this.prepAddReservation.setDate(7, reservation.getTimeSlice().getSQLEndDate());

            if (this.prepAddReservation.executeUpdate() > 0) {
                success = true;
            }
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
        }

        // poistetaan henkilï¿½n tiedot jotka ovat nyt vanhentuneet
        people.remove(reservation.getReservingPerson().getPersonID());
        this.session.waitState(false);
        return success;
    }

    /**
     * Poistaa kannasta parametrina annetun varauksen.
     *
     * @param reservation
     * @return Onnistuiko poisto.
     */
    public boolean deleteReservation(Reservation reservation) {
        boolean success = false;
        this.session.waitState(true);
        try {
            if (this.prepDeleteReservation == null) {
                this.prepDeleteReservation = this.connection.prepareStatement(
                        "DELETE FROM TYOPISTEVARAUS WHERE id = ?");
            }
            this.prepDeleteReservation.setString(1, reservation.getReservationID());
            int deletedRows = this.prepDeleteReservation.executeUpdate();
            if (deletedRows > 0) {
                success = true;
            }
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
        }

        // poistetaan henkilön tiedot jotka ovat nyt vanhentuneet
        people.remove(reservation.getReservingPerson().getPersonID());
        this.session.waitState(false);
        return success;
    }

    /**
     * Pï¿½ivittï¿½ï¿½ parametrina annetun varauksen kantaan.
     *
     * @param reservation muokattu varaus, joka halutaan tallettaa
     * @return Onnistuiko pï¿½ivitys.
     */
    public boolean updateReservation(Reservation reservation) {
        boolean success = false;
        this.session.waitState(true);
        try {
            if (this.prepUpdateReservation == null) {
                this.prepUpdateReservation = this.connection.prepareStatement(
                        "UPDATE TYOPISTEVARAUS"
                        + " SET tpiste_id  = ?, henklo_htunnus = ?,"
                        + " viikkotunnit = ?, selite = ?,"
                        + " alkupvm = TRUNC(?), loppupvm = TRUNC(?)"
                        + " WHERE id = ?");
            }
            this.prepUpdateReservation.setString(1, reservation.getTargetPost().getPostID());
            this.prepUpdateReservation.setString(2, reservation.getReservingPerson().getPersonID());
            this.prepUpdateReservation.setInt(3, (int) (reservation.getWeeklyHours()));
            this.prepUpdateReservation.setString(4, reservation.getDescription());
            this.prepUpdateReservation.setDate(5, reservation.getTimeSlice().getSQLStartDate());
            this.prepUpdateReservation.setDate(6, reservation.getTimeSlice().getSQLEndDate());
            this.prepUpdateReservation.setString(7, reservation.getReservationID());

            int updatedRows = this.prepUpdateReservation.executeUpdate();
            if (updatedRows > 0) {
                success = true;
            }
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
        }

        // poistetaan henkilï¿½n tiedot jotka ovat nyt vanhentuneet
        people.remove(reservation.getReservingPerson().getPersonID());
        // päivitetään henkilöiden huonetiedot
        this.updateRooms();
        this.session.waitState(false);
        return success;
    }

    /* --- Tyï¿½pistevarauksiin liittyvï¿½t metodit loppuu --- */

    /* --- Sopimuksiin liittyvï¿½t metodit alkaa --- */
    /**
     * Palauttaa parametrinï¿½ annetun henkilï¿½n tyï¿½sopimusjaksot tietyltï¿½
     * aikavï¿½liltï¿½.
     *
     * @param person Henkilï¿½, jonka sopimuksista ollaan kiinnostuneita.
     * @param timeslice Aikavï¿½li, jonka kannsa sopimukset ovat ainakin
     * osittain pï¿½ï¿½llekkï¿½in.
     * @return Sopimusjaksot <code>Contract[]</code> oliona.
     */
    public Contract[] getContracts(Person person, TimeSlice timeslice) {
        Collection contracts = new Vector();
        this.session.waitState(true);
        try {
            if (this.prepPersonContracts == null) {
                /* NOTE pelkï¿½stï¿½ï¿½n sopimusnumero ei ole yksiselitteinen
                 * Taulun primary key on kolmikko
                 * {sopimusnumero, henkloh_tunnus, alkupvm_jakso}
                 */
                this.prepPersonContracts = this.connection.prepareStatement(
                        "SELECT tsj.sopimusnumero, tsj.alkupvm_jakso,"
                        + " tsj.loppupvm_jakso, tsj.prjkti_koodi,"
                        + " tsj.nimike, tsj.vv_hoitoprosentti"
                        + " FROM TYOSOPIMUSJAKSO tsj"
                        + " WHERE tsj.henklo_htunnus = ?"
                        + " AND ? <= tsj.loppupvm_jakso"
                        + " AND ? >= tsj.alkupvm_jakso");
            }

            this.prepPersonContracts.setString(1, person.getPersonID());
            this.prepPersonContracts.setDate(2, timeslice.getSQLStartDate());
            this.prepPersonContracts.setDate(3, timeslice.getSQLEndDate());
            ResultSet rs = this.prepPersonContracts.executeQuery();

            while (rs.next()) {
                Project project = null;
                String projectID = rs.getString("prjkti_koodi");
                if (projectID != null) {
                    project = (Project) this.projects.get(projectID);
                }
                Date start = new Date(rs.getDate("alkupvm_jakso").getTime());
                Date end = new Date(rs.getDate("loppupvm_jakso").getTime());
                TimeSlice ts = new TimeSlice(start, end);
                int workingPercentage = rs.getInt("vv_hoitoprosentti");
                // kannassa vv_hoitoprosentti-kentï¿½n NULL merkitsee normaalia tyï¿½skentelyï¿½
                if (rs.wasNull()) {
                    workingPercentage = 100;
                }

                Contract contract = new Contract(this.session,
                        rs.getString("sopimusnumero"), project, person,
                        rs.getString("nimike"), workingPercentage,
                        ts);
                contracts.add(contract);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
        }
        this.session.waitState(false);
        return (Contract[]) contracts.toArray(new Contract[0]);
    }

    /* --- Sopimuksiin liittyvï¿½t metodit loppuu --- */

    /* --- Henkilï¿½ihin liittyvï¿½t metodit alkaa --- */
    /**
     * Palauttaa henkilöt jotka täyttävät parametreina annetut hakuehdot.
     * Hakuehtoja voi yhdistää, ja oliotyyppiset hakuehdot voivat olla null,
     * jolloin ne eivät rajaa tulosta.
     *
     * @param timescale Aikaväli, jota henkilöiden työsopimusjaksojen tule
     * leikata
     * @param personName Merkkijono, johon henkilön (suku?)nimeï¿½ verrataan.
     * @param partTimeTeachersOnly jos tosi, näytetään vain sivutoimiset
     * tuntiopettajat
     * @param withoutPost jos tosi, näytetään vain ne, joilla on
     * tyï¿½sopimusjakso ilman samanaikaista tyï¿½pistevarausta
     * @param showEndingContracts jos tosi, nï¿½ytetï¿½ï¿½n vain ne, joiden
     * viimeinen tyï¿½sopimusjakso on aikavï¿½lillï¿½
     * @param project projekti, jonka henkilï¿½t nï¿½ytetï¿½ï¿½n
     * @return henkilï¿½t <code>Person[]</code> oliona.
     */
    public Person[] getPeople(TimeSlice timescale, String personName,
            Project project, boolean showEndingContracts, boolean withoutPost,
            boolean partTimeTeachersOnly) {
        Collection filteredPeople = new LinkedList();
        this.session.waitState(true);

        session.setStatusMessage("Haetaan henkilöitä..." /*
                 + " (aikavï¿½li: " + timescale
                 + ", nimi: " + personName
                 + ", projekti: " + project
                 + ", loppuvat: " + showEndingContracts
                 + ", surulliset: " + withoutPost
                 + ", osa-aikaiset: " + partTimeTeachersOnly
                 + ")"
                 */);
        // Kootaan SQL-kysely paloista
        // Yhteinen alkuosa
        String sqlQuery = "SELECT DISTINCT h.htunnus, h.sukunimi,"
                + " h.etunimet, h.huone_nro, h.kutsumanimi,"
                + " h.aktiivisuus, h.hetu, h.oppiarvo, h.titteli,"
                + " h.puhelin_tyo, h.puhelin_koti, h.katuosoite, h.katuosoite,"
                + " h.postinro, h.postitoimipaikka, h.valvontasaldo, h.sahkopostiosoite,"
                + " h.hallinnollinen_kommentti, h.opiskelija_kommentti, h.ktunnus,"
                + " h.kannykka, h.postilokerohuone, h.hy_tyosuhde, h.hy_puhelinluettelossa,"
                + " max(tsj.loppupvm_jakso) as loppupvm"
                + " FROM TYOSOPIMUSJAKSO tsj, HENKILO h"
                + " WHERE (UPPER(h.sukunimi) LIKE UPPER(?)"
                + " OR UPPER(h.etunimet) LIKE UPPER(?))";

        // tï¿½smï¿½llisemmissï¿½ hauissa tï¿½ytyy katsoa tyï¿½sopimusjaksoa
        if (showEndingContracts || withoutPost
                || project != null || partTimeTeachersOnly) {
            sqlQuery += " AND tsj.henklo_htunnus = h.htunnus"
                    + " AND tsj.sopimustyyppi LIKE ?"
                    /* Oraclessa like-vertailu nulliin ei toimi,
                     * joten kï¿½ytetï¿½ï¿½n NVL()-funktiota */
                    + " AND NVL(tsj.prjkti_koodi, 'oraclesucks') LIKE ?"
                    + " AND ? <= tsj.loppupvm_jakso AND ? >= tsj.alkupvm_jakso";
        } else // tehdï¿½ï¿½n ulkoliitos eli saadaan myï¿½s henkilï¿½t ilman sopimusjaksoja
        {
            sqlQuery += " AND tsj.henklo_htunnus(+) = h.htunnus";
        }

        // Jos pyydetty tyï¿½pisteettï¿½mï¿½t mutta ei pï¿½ï¿½ttyviï¿½ sopimuksia,
        // tarkistetaan helpoin tapaus tï¿½ssï¿½ (yksi vï¿½hintï¿½ï¿½n
        // koko sopimusjakson peittï¿½vï¿½ varaus) ja loput tarkistetaan koodissa
        if (withoutPost && !showEndingContracts) {
            sqlQuery += " AND NOT EXISTS ("
                    + " SELECT id"
                    + " FROM tyopistevaraus"
                    + " WHERE henklo_htunnus = h.htunnus"
                    + " AND alkupvm <= greatest(tsj.alkupvm_jakso, ?)"
                    + " AND loppupvm >= least(tsj.loppupvm_jakso, ?)"
                    + ")";
        }

        // Yhteinen GROUP BY -osa
        sqlQuery += " GROUP BY h.htunnus, h.sukunimi, h.etunimet, h.huone_nro, h.kutsumanimi,"
                + " h.aktiivisuus, h.hetu, h.oppiarvo, h.titteli,"
                + " h.puhelin_tyo, h.puhelin_koti, h.katuosoite, h.katuosoite,"
                + " h.postinro, h.postitoimipaikka, h.valvontasaldo, h.sahkopostiosoite,"
                + " h.hallinnollinen_kommentti, h.opiskelija_kommentti, h.ktunnus,"
                + " h.kannykka, h.postilokerohuone, h.hy_tyosuhde, h.hy_puhelinluettelossa";

        // jos nï¿½ytetï¿½ï¿½n vain pï¿½ï¿½ttyvï¿½t sopimukset, niin voidaan rajata jo nyt,
        // mutta jos mukana ovat myï¿½s tyï¿½pisteettï¿½mï¿½t, niin tï¿½ytyy rajaus tehdï¿½ myï¿½hemmin
        if (showEndingContracts && !withoutPost) {
            sqlQuery += " HAVING MAX(loppupvm_jakso) BETWEEN ? AND ?";
        }

        // Yhteinen ORDER BY -osa
        sqlQuery += " ORDER BY h.sukunimi, h.etunimet";

        try {
            // katsotaan olisiko sqlQuerya vastaava PreparedStatement jo valmiina
            PreparedStatement prep = (PreparedStatement) prepFilteredPeople.get(sqlQuery);
            // jos ei ole, tehdï¿½ï¿½n ja pannaan talteen
            if (prep == null) {
                prep = this.connection.prepareStatement(sqlQuery);
                prepFilteredPeople.put(sqlQuery, prep);
            }
            
            long prepTime = System.currentTimeMillis();
            //System.err.println("Valmisteluun meni aikaa ms: " + (prepTime - startTime));

            // Asetetaan parametrit; kï¿½ytetï¿½ï¿½n laskuria apuna
            // Laskuria kasvatetaan jokaisen kï¿½yttï¿½kerran yhteydessï¿½ ++ -operaattorilla
            int paramNo = 1;
            // pï¿½ivï¿½mï¿½ï¿½riï¿½ on turha muodostaa joka kerta, joten otetaan talteen
            java.sql.Date start = timescale.getSQLStartDate();
            java.sql.Date end = timescale.getSQLEndDate();

            /* henkilï¿½n nimi, jos annettu */
            if (personName == null) {
                prep.setString(paramNo++, "%");
                prep.setString(paramNo++, "%");
            } else {
                prep.setString(paramNo++, "%" + personName + "%");
                prep.setString(paramNo++, "%" + personName + "%");
            }

            if (showEndingContracts || withoutPost
                    || project != null || partTimeTeachersOnly) {

                /* sopimustyyppi:
                 * S sivutoiminen, P pï¿½ï¿½toiminen, L laitostehtï¿½vï¿½, D dosentuuri */
                if (partTimeTeachersOnly) {
                    prep.setString(paramNo++, "S");
                } else {
                    prep.setString(paramNo++, "%");
                }
                /* projektia peliin */
                if (project == null) {
                    prep.setString(paramNo++, "%");
                } else {
                    prep.setString(paramNo++, project.getProjectID());
                }
                /* aikavï¿½li*/
                prep.setDate(paramNo++, start);
                prep.setDate(paramNo++, end);
            }

            /* tyï¿½pisteettï¿½mï¿½t */
            if (withoutPost && !showEndingContracts) {
                prep.setDate(paramNo++, start);
                prep.setDate(paramNo++, end);
            }

            /* pï¿½ï¿½ttyvï¿½t sopimukset */
            if (showEndingContracts && !withoutPost) {
                prep.setDate(paramNo++, start);
                prep.setDate(paramNo++, end);
            }

            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                // Yritetï¿½ï¿½n kï¿½yttï¿½ï¿½ tallessa olevaa henkilï¿½ï¿½
                Person person = (Person) people.get(rs.getString("htunnus"));
                if (person == null) { // ei lï¿½ytynyt, tï¿½ytyy luoda uusi
                    /* contracts ja reservations saavat olla null uudella henkilï¿½llï¿½ */
                    Contract[] contracts = null;
                    Date lastContractEnd = rs.getDate("loppupvm");
                    if (lastContractEnd == null || lastContractEnd.before(start)) {
                        // haa, tiedetï¿½ï¿½n ettei sopimusjaksoja ole aikavï¿½lillï¿½
                        // kerrotaan se Personille ettei se turhaan kysele
                        contracts = new Contract[0];
                    }
                    henkiloHash = new HashMap();
                    henkiloHash.put("htunnus", rs.getString("htunnus"));
                    henkiloHash.put("kokonimi", (rs.getString("sukunimi") + " " + rs.getString("etunimet")));
                    henkiloHash.put("etunimet", rs.getString("etunimet"));
                    henkiloHash.put("sukunimi", rs.getString("sukunimi"));
                    henkiloHash.put("huone_nro", rs.getString("huone_nro"));
                    henkiloHash.put("kutsumanimi", rs.getString("kutsumanimi"));
                    henkiloHash.put("aktiivisuus", rs.getString("aktiivisuus"));
                    henkiloHash.put("hetu", rs.getString("hetu"));
                    henkiloHash.put("oppiarvo", rs.getString("oppiarvo"));
                    henkiloHash.put("titteli", rs.getString("titteli"));
                    henkiloHash.put("puhelin_tyo", rs.getString("puhelin_tyo"));
                    henkiloHash.put("puhelin_koti", rs.getString("puhelin_koti"));
                    henkiloHash.put("katuosoite", rs.getString("katuosoite"));
                    henkiloHash.put("postinro", rs.getString("postinro"));
                    henkiloHash.put("postitoimipaikka", rs.getString("postitoimipaikka"));
                    henkiloHash.put("valvontasaldo", rs.getString("valvontasaldo"));
                    henkiloHash.put("sahkopostiosoite", rs.getString("sahkopostiosoite"));
                    henkiloHash.put("hallinnollinen_kommentti", rs.getString("hallinnollinen_kommentti"));
                    henkiloHash.put("opiskelija_kommentti", rs.getString("opiskelija_kommentti"));
                    henkiloHash.put("ktunnus", rs.getString("ktunnus"));
                    henkiloHash.put("kannykka", rs.getString("kannykka"));
                    henkiloHash.put("postilokerohuone", rs.getString("postilokerohuone"));
                    henkiloHash.put("hy_tyosuhde", rs.getString("hy_tyosuhde"));
                    henkiloHash.put("hy_puhelinluettelossa", rs.getString("hy_puhelinluettelossa"));
                    
                    
                    person = new Person(this.session, henkiloHash, contracts, null);
                    
                    for (RoomKeyReservation rkreservation : this.roomKeyReservations) {
                        if(rkreservation.getReserverID().equals(rs.getString("htunnus")))
                            person.addRoomKeyReservation(rkreservation);
                    }
                    people.put(rs.getString("htunnus"), person);
                }

                if (filterPerson(person, timescale, rs.getDate("loppupvm"), showEndingContracts, withoutPost, session.getFilterActiveEmployees(), session.getFilterContract())) {
                    filteredPeople.add(person);
                }
            }
            rs.close();
            //System.err.println("Kyselyn suorittamiseen meni aikaa ms: " + (System.currentTimeMillis() - prepTime));
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
        }
        this.session.waitState(false);
        session.setStatusMessage("Löytyi " + filteredPeople.size() + " henkilöä.");
        return (Person[]) filteredPeople.toArray(new Person[0]);
    }
    public void getPersonInfo(Person person) {
        
        String sqlQuery = "SELECT htunnus, sukunimi,"
                + " etunimet, huone_nro, kutsumanimi,"
                + " aktiivisuus, hetu, oppiarvo, titteli,"
                + " puhelin_tyo, puhelin_koti, katuosoite, katuosoite,"
                + " postinro, postitoimipaikka, valvontasaldo, sahkopostiosoite,"
                + " hallinnollinen_kommentti, opiskelija_kommentti, ktunnus,"
                + " kannykka, postilokerohuone, hy_tyosuhde, hy_puhelinluettelossa"
                + " FROM HENKILO WHERE h_tunnus = ?";
    }

    public void updatePersonInfo(Person person) throws SQLException {
        this.session.waitState(true);

        PreparedStatement prepModifyperson = this.connection.prepareStatement(
                " UPDATE henkilo"
                + " SET htunnus = ?, etunimet = ?, sukunimi = ?, kutsumanimi = ?, aktiivisuus = ?, huone_nro = ?,"
                + " hetu = ?, oppiarvo = ?, titteli = ?, puhelin_koti = ?, katuosoite = ?,"
                + " postinro = ?, postitoimipaikka = ?, sahkopostiosoite = ?, hallinnollinen_kommentti = ?,"
                + " ktunnus = ?, kannykka = ?, postilokerohuone = ?, hy_tyosuhde = ?, hy_puhelinluettelossa = ?"
                + " WHERE htunnus = ? AND etunimet = ? AND sukunimi = ?");

        prepModifyperson.setString(1, person.getPersonID());
        prepModifyperson.setString(2, person.getEtunimi());
        prepModifyperson.setString(3, person.getSukunimi());
        prepModifyperson.setString(4, person.getCallName());
        prepModifyperson.setString(5, person.getActivity());
        prepModifyperson.setString(6, person.getRoom());
        prepModifyperson.setString(7, person.getHetu());
        prepModifyperson.setString(8, person.getOppiarvo());
        prepModifyperson.setString(9, person.getTitteli());
        prepModifyperson.setString(10, person.getHomePhone());
        prepModifyperson.setString(11, person.getAddress());
        prepModifyperson.setString(12, person.getPostnumber());
        prepModifyperson.setString(13, person.getPostitoimiPaikka());
        prepModifyperson.setString(14, person.getSahkoposti());
        prepModifyperson.setString(15, person.getHallinnollinenKommentti());
        prepModifyperson.setString(16, person.getkTunnus());
        prepModifyperson.setString(17, person.getKannykka());
        prepModifyperson.setString(18, person.getPostilokeroHuone());
        prepModifyperson.setString(19, person.getHyTyosuhde());
        prepModifyperson.setString(20, person.getHyPuhelinluettelossa());
        prepModifyperson.setString(21, person.getPersonID());
        prepModifyperson.setString(22, person.getEtunimi());
        prepModifyperson.setString(23, person.getSukunimi());

        prepModifyperson.executeUpdate();


        this.session.waitState(false);
    }

    public void createPerson(Person person) throws SQLException {
        this.session.waitState(true);

        PreparedStatement prepCreateperson = this.connection.prepareStatement(
                " INSERT INTO henkilo (htunnus, etunimet, sukunimi, kutsumanimi, aktiivisuus, huone_nro,"
                + " hetu, oppiarvo, titteli, puhelin_koti, katuosoite,"
                + " postinro, postitoimipaikka, sahkopostiosoite, hallinnollinen_kommentti,"
                + " ktunnus, kannykka, postilokerohuone, hy_tyosuhde, hy_puhelinluettelossa)"
                + " VALUES (?, ?, ?, ?, ?,"
                + " ?, ?, ?, ?, ?, ?,"
                + " ?, ?, ?, ?, ?, ?,"
                + " ?, ?, ?)");

        prepCreateperson.setString(1, person.getPersonID());
        prepCreateperson.setString(2, person.getEtunimi());
        prepCreateperson.setString(3, person.getSukunimi());
        prepCreateperson.setString(4, person.getCallName());
        prepCreateperson.setString(5, person.getActivity());
        prepCreateperson.setString(6, person.getRoom());
        prepCreateperson.setString(7, person.getHetu());
        prepCreateperson.setString(8, person.getOppiarvo());
        prepCreateperson.setString(9, person.getTitteli());
        prepCreateperson.setString(10, person.getWorkPhone());
        prepCreateperson.setString(11, person.getHomePhone());
        prepCreateperson.setString(12, person.getAddress());
        prepCreateperson.setString(13, person.getPostnumber());
        prepCreateperson.setString(14, person.getPostitoimiPaikka());
        prepCreateperson.setString(15, person.getSahkoposti());
        prepCreateperson.setString(16, person.getHallinnollinenKommentti());
        prepCreateperson.setString(17, person.getkTunnus());
        prepCreateperson.setString(18, person.getKannykka());
        prepCreateperson.setString(19, person.getPostilokeroHuone());
        prepCreateperson.setString(20, person.getHyTyosuhde());
        prepCreateperson.setString(21, person.getHyPuhelinluettelossa());

        prepCreateperson.executeUpdate();


        this.session.waitState(false);
    }

    /**
     * Tarkista kuuluuko henkilï¿½ hakuehtojen mukaiseen listaan. Tarkistus
     * tehdï¿½ï¿½n, koska vastaavan seikan tarkistaminen SQL-kannassa on hyvin
     * vaikeaa ja hidasta.
     *
     * @param person henkilï¿½
     * @param timescale tarkasteltava aikavï¿½li
     * @param contractEndDate henkilï¿½
     * @param showEndingContracts halutaanko pï¿½ï¿½ttyvï¿½t tyï¿½sopimukset
     * @param withoutPost halutaanko tyï¿½pisteettï¿½mï¿½t
     * @return Sopivatko annetut hakuehdot henkilï¿½ï¿½n
     */
    private boolean filterPerson(Person person, TimeSlice timescale, java.sql.Date contractEndDate, boolean showEndingContracts, boolean withoutPost, boolean activeOnly, boolean contractsOnly) {
        // Tarkistetaan tarvitseeko henkilöä filtteröidä aktiivisuuden perusteella
        if (activeOnly && person.getActivity().equalsIgnoreCase("E")) {
            return false;
        }
        // Tarkistetaan, että tarvitseeko henkilöä filtteröidä työsopimuksen perusteella
        if (contractsOnly && person.getHyTyosuhde().equalsIgnoreCase("E")) {
            return false;
        }
        // jos ei pyydetty tyï¿½pisteettï¿½miï¿½, ei filtterï¿½intiï¿½ tarvita 
        if (!withoutPost) {
            return true;
        }
        // jos pyydettiin pï¿½ï¿½ttyviï¿½ sopimuksia, tarkistetaan ensin ne
        if (showEndingContracts && timescale.contains(contractEndDate)) {
            return true;
        }
        // muussa tapauksessa tï¿½ytyy tutkia tarkemmin henkilï¿½n varaukset
        return person.getStatus();
    }

    /**
     * Nerompi
     * Lisää tietokannan henkilo-taulun huone_nro-kenttään annettu arvo.
     *
     * @param person Henkilö, jolle huone lisätään
     * @param room Lisättävän huoneen nimi
     * @return Onnistuiko lisääminen
     */
    public boolean addRoomToPerson(Person person, Room room) {
        if (person.getRoom() != null) {
            this.removeRoomFromPerson(person);
        }
        String sqlQuery = "update henkilo"
                + " set HUONE_NRO=?"
                + " where HTUNNUS=?";
        try {
            PreparedStatement prep = this.connection.prepareStatement(sqlQuery);
            prep.setString(1, room.getRoomName());
            prep.setString(2, person.getPersonID());
            prep.executeQuery();
            person.setRoom(room.getRoomName());
            return true;
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
            return false;
        }
    }

    /**
     * Nerompi
     * Korvaa tietokannan henkilo-taulun huone_nro-kentän arvo
     * null-arvolla.
     *
     * @param person Henkilö, jolta huone poistetaan.
     * @return Onnistuiko poistaminen.
     */
    public boolean removeRoomFromPerson(Person person) {
        if (person.getRoom() == null) {
            return false;
        }
        String sqlQuery = "update henkilo"
                + " set huone_nro=null"
                + " where henkilo.htunnus=?";
        try {
            PreparedStatement prep = this.connection.prepareStatement(sqlQuery);
            prep.setString(1, person.getPersonID());
            prep.executeQuery();
            person.setRoom(null);
            return true;
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
            return false;
        }
    }

    /**
     * Nerompi
     * Päivittää henkilo-taulun tyopuhelinnumero-kentän
     *
     * @param name Päivitettävän henkilön nimi
     * @param number Henkilölle asetettava työpuhelinnumero
     * @return Onnistuiko päivittäminen
     */
    public boolean updateWorkPhone(String personID, String number) {
        String updatePhoneQuery = "update henkilo"
                + " set puhelin_tyo=?"
                + " where htunnus=?";
        try {
            PreparedStatement prep = this.connection.prepareStatement(updatePhoneQuery);
            prep.setString(1, number);
            prep.setString(2, personID);
            prep.executeQuery();
            return true;
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
            return false;
        }
    }

    /** Nerompi
     * Hakee tietokannasta umpeutuneet huonevaraukset ja alkaneet
     * huonevaraukset ja päivittää huoneen näille ihmisille.
     */
    public void updateRooms() {

        // Hakee työpistevaraukset joiden varaus on mennyt umpeen
        String selectPersonQuery = "select henklo_htunnus"
                + " from tyopistevaraus"
                + " where loppupvm<CURRENT_TIMESTAMP";

        // Poistaa huoneen annetulta henkilöltä
        String removeRoomQuery = "update henkilo"
                + " set huone_nro=null"
                + " where htunnus=?";

        // Hakee työpistevaraukset joiden varaus on alkanut, mutta ei ole vielä loppunut
        String selectPersonQuery2 = "select henklo_htunnus"
                + " from tyopistevaraus"
                + " where alkupvm<CURRENT_TIMESTAMP AND loppupvm>CURRENT_TIMESTAMP";

        // Hakee annetun henkilön työpistevaraukseen liittyvän huoneen numeron
        String selectRoomQuery = "select huone_nro"
                + " from huone"
                + " where huone_nro="
                + " (select huone_nro"
                + " from rhuone"
                + " where id="
                + " (select rhuone_id"
                + " from tyopiste"
                + " where id="
                + " (select tpiste_id"
                + " from tyopistevaraus"
                + " where henklo_htunnus=?"
                + " and alkupvm<CURRENT_TIMESTAMP AND loppupvm>CURRENT_TIMESTAMP)))";

        // Laittaa annetulle henkilölle annetun huoneen
        String setRoomQuery = "update henkilo"
                + " set huone_nro=?"
                + " where htunnus=?";

        PreparedStatement prep, prep2;
        try {
            ResultSet selectPersonResult = this.connection.prepareStatement(selectPersonQuery).executeQuery();
            while (selectPersonResult.next()) { // Poistaa huoneen niiltä, joiden varaus on mennyt umpeen
                prep = this.connection.prepareStatement(removeRoomQuery);
                prep.setString(1, selectPersonResult.getString("henklo_htunnus"));
                prep.executeQuery();
            }

            ResultSet selectPersonResult2 = this.connection.prepareStatement(selectPersonQuery2).executeQuery();

            while (selectPersonResult2.next()) { // Päivittää työhuonenumeron niille, joiden varaus on alkanut, mutta ei vielä loppunut
                prep2 = this.connection.prepareStatement(selectRoomQuery);
                prep2.setString(1, selectPersonResult2.getString("henklo_htunnus"));
                ResultSet selectRoomResult = prep2.executeQuery();
                selectRoomResult.next();

                prep = this.connection.prepareStatement(setRoomQuery);
                prep.setString(1, selectRoomResult.getString("huone_nro"));
                prep.setString(2, selectPersonResult2.getString("henklo_htunnus"));
                prep.executeQuery();
            }
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
        }
    }
    
    public void deleteRoomFromPerson(Person person) {
        String updateQuery="update henkilo set huone_nro=null where htunnus=?";
        
        try {
            PreparedStatement prep = this.connection.prepareStatement(updateQuery);
            prep.setString(1, person.getPersonID());
            prep.executeUpdate();
        } catch(SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
        }
    }

    /* --- Henkilï¿½ihin liittyvï¿½t metodit loppuu --- */

    /* --- Tyï¿½pisteisiin liittyvï¿½t metodit alkaa --- */
    /**
     * Poistaa tyï¿½pisteen.
     *
     * @param post Poistettava tyï¿½piste
     * @return Onnistuiko poisto.
     */
    public boolean deletePost(Post post) {
        boolean success = false;
        this.session.waitState(true);
        try {
            if (this.prepDeletePost == null) {
                this.prepDeletePost = this.connection.prepareStatement(
                        "DELETE FROM TYOPISTE WHERE id = ?");
            }
            this.prepDeletePost.setString(1, post.getPostID());
            int deletedRows = this.prepDeletePost.executeUpdate();
            if (deletedRows > 0) {
                success = true;
                // XXX Raskas operaatio
                loadRooms();
            }
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
        }
        this.session.waitState(false);
        return success;
    }
        

    /**
     * Lisää työpisteen huoneeseen
     *
     * @param post Työpiste joka lisätään.
     * @return Onnistuiko lisäys
     */
    public boolean createPost(Post post) {
        boolean success = false;
        this.session.waitState(true);
        try {
            /* seuraava vapaa ID _tï¿½ytyy_ hakea ensin */
            if (this.prepNextPostID == null) {
                this.prepNextPostID = this.connection.prepareStatement(
                        "SELECT seq_tpiste_id.NEXTVAL FROM dual");
            }
            ResultSet rsid = this.prepNextPostID.executeQuery();
            rsid.next();
            int nextID = rsid.getInt(1);
            rsid.close();

            if (this.prepCreatePost == null) {
                this.prepCreatePost = this.connection.prepareStatement(
                        " INSERT INTO tyopiste (id, rhuone_id, lisayspvm)"
                        + " VALUES (?, ?, SYSDATE)");
            }
            this.prepCreatePost.setInt(1, nextID);
            this.prepCreatePost.setString(2, post.getRoom().getRoomID());

            if (this.prepCreatePost.executeUpdate() > 0) {
                success = true;
                // XXX Raskas operaatio
                this.loadRooms();

            }
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
        }
        this.session.waitState(false);
        return success;
    }

    /* --- Tyï¿½pisteisiin liittyvï¿½t metodit loppuu --- */

    /* --- Työhuoneisiin liittyvät metodit alkaa --- */
    /**
     * Palauttaa kaikki jï¿½rjestelmï¿½n tuntemat huoneet.
     *
     * @return huoneet Room[]-oliona
     */
    public Room[] getRooms() {
        return (Room[]) rooms.values().toArray(new Room[0]);
    }

    /**
     * Hakee tietyn projektin henkilï¿½ille varatut huoneet tietyllï¿½
     * aikavï¿½lillï¿½. Jos projekti on null, palautetaan tyhjï¿½ huonetaulukko.
     *
     * @param project Projekti, jonka tyï¿½huoneita haetaan. Jos null,
     * palautetaan tyhjï¿½ lista.
     * @param timescale Aikavï¿½li, jota varauksien tulee leikata.
     * @return taulukko projektin huoneista
     */
    public Room[] getRooms(Project project, TimeSlice timescale) {
        if (project == null) {
            return new Room[0];
        }
        Collection rooms = new Vector();
        this.session.waitState(true);
        try {
            if (this.prepProjectRooms == null) {
                this.prepProjectRooms = this.connection.prepareStatement(
                        "SELECT h.ID AS huone FROM RHUONE h,"
                        + " TYOPISTE tp, TYOPISTEVARAUS tpv, TYOSOPIMUSJAKSO tsj"
                        + " WHERE h.ID = tp.RHUONE_ID AND tp.ID = tpv.TPISTE_ID"
                        + " AND tpv.HENKLO_HTUNNUS = tsj.HENKLO_HTUNNUS"
                        + " AND tsj.PRJKTI_KOODI = ?"
                        /* sekï¿½ tyï¿½pistevarauksen ettï¿½ tyï¿½sopimusjakson pï¿½ivï¿½mï¿½ï¿½rï¿½t
                         * pitï¿½ï¿½ olla oikein */
                        + " AND ? <= tsj.LOPPUPVM_JAKSO AND ? >= tsj.ALKUPVM_JAKSO"
                        + " AND ? <= tpv.LOPPUPVM AND ? >= tpv.ALKUPVM");
            }
            this.prepProjectRooms.setString(1, project.getProjectID());

            java.sql.Date start = timescale.getSQLStartDate();
            java.sql.Date end = timescale.getSQLEndDate();
            this.prepProjectRooms.setDate(2, start);
            this.prepProjectRooms.setDate(3, end);
            this.prepProjectRooms.setDate(4, start);
            this.prepProjectRooms.setDate(5, end);

            ResultSet rs = this.prepProjectRooms.executeQuery();
            while (rs.next()) {
                rooms.add(this.rooms.get(rs.getString("huone")));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
        }
        this.session.waitState(false);
        return (Room[]) rooms.toArray(new Room[0]);
    }

    /**
     * Palauttaa ne huoneet, jotka täyttävät annetut hakuehdot.
     *
     * @param roomFilter String jota etsitään huoneen nimestä ja
     * numerosta.
     * @param maxPosts Huoneen tyï¿½pisteiden maksimilukumï¿½ï¿½rï¿½. Jos
     * pienempi kuin 0, ei rajaa tulosta.
     * @return Hakuehdot tï¿½yttï¿½vï¿½t huoneet Room[]-oliona
     */
    public Room[] getRooms(String roomFilter, int maxPosts) {
        String queryNormal, queryWithMaxPosts;
        Collection temprooms = new Vector();
        this.session.waitState(true);
        /* SQL-kyselyn normaali muoto */
        queryNormal = "SELECT rhuone.id FROM rhuone"
                + " WHERE (UPPER(rhuone.numero) LIKE UPPER(?)"
                + " OR UPPER(rhuone.nimi) LIKE UPPER(?))";

        queryWithMaxPosts = "SELECT rhuone.id FROM rhuone, tyopiste"
                + " WHERE (UPPER(rhuone.numero) LIKE UPPER(?)"
                + " OR UPPER(rhuone.nimi) LIKE UPPER(?))"
                + " AND rhuone.id = tyopiste.rhuone_id"
                + " HAVING COUNT(tyopiste.id) <= ?"
                + " GROUP BY rhuone.id";

        try {
            if (this.prepNamedRoomsNormal == null) {
                this.prepNamedRoomsNormal =
                        this.connection.prepareStatement(queryNormal);
            }
            if (this.prepNamedRoomsWithMaxPosts == null) {
                this.prepNamedRoomsWithMaxPosts =
                        this.connection.prepareStatement(queryWithMaxPosts);
            }

            ResultSet rs;
            if (maxPosts < 0) {
                this.prepNamedRoomsNormal.setString(1, "%" + roomFilter + "%");
                this.prepNamedRoomsNormal.setString(2, "%" + roomFilter + "%");
                rs = this.prepNamedRoomsNormal.executeQuery();
            } else {
                this.prepNamedRoomsWithMaxPosts.setString(1, "%" + roomFilter + "%");
                this.prepNamedRoomsWithMaxPosts.setString(2, "%" + roomFilter + "%");
                this.prepNamedRoomsWithMaxPosts.setInt(3, maxPosts);
                rs = this.prepNamedRoomsWithMaxPosts.executeQuery();
            }
            while (rs.next()) {
                Room r = (Room) this.rooms.get(rs.getString("id"));
                temprooms.add(r);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
        }
        this.session.waitState(false);
        return (Room[]) temprooms.toArray(new Room[0]);
    }

    /**
     * Palauttaa huoneolion huone-id:n perusteella.
     *
     * @param roomID huoneen id
     * @return huoneolio tai null, jos kyseistä huonetta ei löydy
     */
    public Room getRoom(String roomID) {
        return (Room) rooms.get(roomID);
    }

    /**
     * Nerompi Palauttaa varaukset annettuun huoneeseen
     *
     * @param room huone, jonka varaukset halutaan hakea
     * @return Taulu varauksista
     */
    public RoomKeyReservation[] getRoomKeyReservations(Room room) {
        String sqlquery = "SELECT *"
                + " FROM HUONEVARAUS"
                + " WHERE RHUONE_ID=?";

        try {
            PreparedStatement prep = this.connection.prepareStatement(sqlquery);
            prep.setString(1, room.getRoomID());
            ResultSet rs = prep.executeQuery();
            ArrayList<RoomKeyReservation> arrayList = new ArrayList();
            while(rs.next()) {
                TimeSlice timeslice = new TimeSlice(rs.getDate("ALKUPVM"), rs.getDate("LOPPUPVM"));
                Person person = (Person) people.get(rs.getString("HTUNNUS"));
                arrayList.add(new RoomKeyReservation(rs.getInt("ID"), (Room) rooms.get(rs.getString("RHUONE_ID")), person.getPersonID(), person.getName(), timeslice, this.session));
            }
            RoomKeyReservation[] temp = new RoomKeyReservation[0];
            return arrayList.toArray(temp);
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
            return null;
        }
    }

	/* --- Huoneisiin liittyvät metodit loppuu --- */ 

	/* --- Puhelinnumeroihin liittyvät metodit alkaa --- */ 

	/**
	 * Palauttaa kaikki järjestelmän tuntemat puhelinnumerot järjestettynä
	 * PhoneNumber[] -taulukkona.
	 * @return PhoneNumber[] -taulukko.
	 */
	public PhoneNumber[] getAllPhoneNumbers() {
		Collection all = new Vector();
		Iterator iter = this.phoneNumbers.values().iterator();
		while(iter.hasNext()) {
			all.addAll((Collection)iter.next());
		}
		PhoneNumber[] numbers = (PhoneNumber[]) all.toArray(new PhoneNumber[0]);
		Arrays.sort(numbers);
		return numbers;
	}

	/**
	 * Päivittää tietokannassa olevan puhelinnumero-olion annetun mallin
	 * mukaiseksi ja päivittää puhelinnumeron työpisteen varaajalle jos sellainen on.
	 * 
	 * @param phone Uusi versio puhelinnumerosta (uusi työpiste id).
	 * @return Onnistuiko päivitys.
	 */
	public boolean updatePhoneNumber(PhoneNumber phone) {
            
            String getpersons = "select HENKLO_HTUNNUS"
                                    + " from TYOPISTEVARAUS"
                                    + " where ALKUPVM<CURRENT_TIMESTAMP"
                                    + " AND LOPPUPVM>CURRENT_TIMESTAMP"
                                    + " AND TPISTE_ID=?";
                      
            boolean success = false;
            this.session.waitState(true);
            PreparedStatement prep;
            Post post = phone.getPost();
            String personID = phone.getPersonID();
            
            
            //tarkistus, jos työpistenumero käytössä, niin voi lisätä yhden henkilönumeron
            try {
		if(this.prepUpdatePhoneNumber == null) {
                    this.prepUpdatePhoneNumber = this.connection.prepareStatement("UPDATE PUHELINNUMERO SET tp_id  = ?, h_tunnus = ? WHERE id = ?");
		}
		if(post == null) {
                    this.prepUpdatePhoneNumber.setString(1, "");
		} else {
                    this.prepUpdatePhoneNumber.setString(1, post.getPostID());
		}
                this.prepUpdatePhoneNumber.setString(2, personID);
		this.prepUpdatePhoneNumber.setString(3, phone.getPhoneNumberID());
                
                int updatedRows = this.prepUpdatePhoneNumber.executeUpdate();
		if(updatedRows > 0) { // TODO tehdään jotenkin erilailla kun poistetaan puhelinnumero työpisteestä
                    success = true;
                    if (post != null)    {

                        prep = this.connection.prepareStatement(getpersons);
                        prep.setString(1, post.getPostID());
                        ResultSet rs = prep.executeQuery();
                        rs.next();
                        if (!this.getKannykka("HENKLO_HTUNNUS")) {
                            if(rs.getString("HENKLO_HTUNNUS")!=null) {
                                this.updateWorkPhone(rs.getString("HENKLO_HTUNNUS"), phone.getPhoneNumber());
                    } else {
                        this.updateWorkPhone(personID, phone.getPhoneNumber());
                        }
                        /* XXX Raskas operaatio */
                        }
                    }
                        loadRooms();

                        loadPhoneNumbers();

                      

                }
                
            } catch (SQLException e) {
            	System.err.println("Tietokantavirhe: " + e.getMessage());
            }
            this.session.waitState(false);
            return success;
        }
    /* --- Puhelinnumeroihin liittyvät metodit loppuu --- */
        
    /* --- Avainvarauksiin liittyvät metodit alkaa --- */
        
    /**
     * Nerompi Lisää Huonevaraus -tauluun uuden huonevarauksen
     *
     * @param reservation lisättävä huonevaraus
     */
    public void addRoomKeyReservation(Room room, Person person, TimeSlice timeslice) {
        String selectQuery = "SELECT * FROM huonevaraus";
        
        String updateQuery1 = "INSERT INTO HUONEVARAUS (ID, HTUNNUS, RHUONE_ID, ALKUPVM, LOPPUPVM) VALUES (?, ?, ?, ?, ?)"; // (SELECT MAX(ID) FROM HUONEVARAUS)+1

        String updateQuery2 = "INSERT INTO HUONEVARAUS (ID, HTUNNUS, RHUONE_ID, ALKUPVM, LOPPUPVM) VALUES ((SELECT MAX(ID) FROM HUONEVARAUS)+1, ?, ?, ?, ?)"; 
        
        PreparedStatement prep;
        try {
            ResultSet rs = this.connection.prepareStatement(selectQuery).executeQuery();
            if(!rs.next()) {
                prep = this.connection.prepareStatement(updateQuery1);
                prep.setInt(1, 1);
                prep = this.connection.prepareStatement(updateQuery2);
                prep.setString(2, person.getPersonID());
                prep.setString(3, room.getRoomID());
                prep.setDate(4, timeslice.getSQLStartDate());
                prep.setDate(5, timeslice.getSQLEndDate());
                prep.executeUpdate();
            }
            else {
                prep = this.connection.prepareStatement(updateQuery2);
                prep.setString(1, person.getPersonID());
                prep.setString(2, room.getRoomID());
                prep.setDate(3, timeslice.getSQLStartDate());
                prep.setDate(4, timeslice.getSQLEndDate());
                prep.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
        }
    }
    /**
     * Poistaa annetun avainvarauksen tietokannasta
     * @param id poistettavan avainvarauksen id
     */
    public void deleteRoomKeyReservation(int id) {
        String deleteQuery = "DELETE FROM huonevaraus where id=?";

        PreparedStatement prep;

        try {
            prep = this.connection.prepareStatement(deleteQuery);
            prep.setInt(1, id);
            prep.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
        }
    }
    /**
     * Päivittää annetun avainvarauksen alku- ja loppupäivämäärän
     * @param roomKeyReservation muutettava avainvaraus
     * @return onnistuiko päivitys
     */
    public boolean modifyRoomKeyReservation(RoomKeyReservation roomKeyReservation) {
        String updateQuery = "UPDATE huonevaraus SET alkupvm=?, loppupvm=? where id=?";
        
        PreparedStatement prep;
        
        try {
            prep = this.connection.prepareStatement(updateQuery);
            prep.setDate(1, roomKeyReservation.getTimeSlice().getSQLStartDate());
            prep.setDate(2, roomKeyReservation.getTimeSlice().getSQLEndDate());
            prep.setInt(3, roomKeyReservation.getReservationID());
            prep.executeUpdate();
            return true;
        } catch(SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
            return false;
        }
    }

    /* --- Avainvarauksiin liittyvät metodit loppuu --- */

    /* --- Projekteihin liittyvï¿½t metodit alkaa --- */ 

	/**
	 * Palauttaa kaikki jï¿½rjestelmï¿½n tuntemat projektit jï¿½rjestettynï¿½
	 * Project[] -taulukkona.
	 * 
	 * @return projektit <code>Project[]</code> oliona
	 */
	public Project[] getProjects() {
		Project[] projs = (Project[]) projects.values().toArray(new Project[0]);
		/* HashTable hukkaa jï¿½rjestyksen, joten sortataan */
		Arrays.sort(projs);
		return projs;
	}

	/* --- Projekteihin liittyvï¿½t metodit loppuu --- */ 

	/* --- Muut metodit alkaa --- */ 

	/**
	 * Palauttaa tietokantayhteyden. Kï¿½ytï¿½ varovaisesti, tarkoitettu lï¿½hinnï¿½
	 * testejï¿½ varten.
	 * @return Yhteys <code>Connection</code> oliona.
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * NeroObserver-kuuntelija. Kï¿½ytï¿½nnï¿½ssï¿½ kuuntelee vain TIMESCALE ja ROOMS
	 * tyyppejï¿½, mutta ei tarkista mikï¿½ tyyppi vastaanotettiin.
	 * @param type Kuuntelijatyyppi, ei vaikutusta. 
	 * @see fi.helsinki.cs.nero.event.NeroObserver#updateObserved(int)
	 */
	public void updateObserved(int type) {
		// Aikajakso tai huonetiedot ovat muuttuneet. Tiedot henkilï¿½istï¿½ eivï¿½t enï¿½ï¿½
		// ole ajan tasalla.
		//System.err.println("DB: heitetï¿½ï¿½n pois tiedot henkilï¿½istï¿½");
		people.clear();
	}
        
	/**
	 * Main-metodi pienimuotoista testailua varten.
	 * @param args Komentoriviparametrit.
	 * @throws SQLException
	 */
    public static void main(String[] args) throws SQLException {
	NeroApplication.readIni(NeroApplication.DEFAULT_INI);
	NeroDatabase ndb = new NeroDatabase(new Session(),
        	NeroApplication.getProperty("db_class"),
        	NeroApplication.getProperty("db_connection"),
		NeroApplication.getProperty("db_username"),
		NeroApplication.getProperty("db_password"));
	/* tahtoo katsoa versiot, jotkut toimii ja jotkut ei. */
	System.out.println(ndb.connection.getMetaData().getDriverVersion());
	System.out.println(ndb.connection.getMetaData().getDatabaseProductVersion());
	// testailusï¿½lï¿½ poistettu, riippuvaista kannan vanhasta sisï¿½llï¿½stï¿½.
    	System.out.println("done.");
    }

    /**
     * Poistaa työpisteeltä puhelinnumeron
     *
     * @param phone Puhelinnumero, jolta poistetaan työpiste
     * @return Onnistuiko päivitys
     */
    public boolean getKannykka(String henklo_tunnus) {
        
        String getKannykka = "SELECT htunnus FROM KANNYKKA WHERE htunnus = ?";
        try {
            PreparedStatement p = this.connection.prepareStatement(getKannykka);
            p.setString(1, henklo_tunnus);
            ResultSet rs = p.executeQuery();
            
            while (rs.next()) {
                if (rs.getString("HTUNNUS") != null)
                        return true;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(NeroDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean removePhoneNumberFromPost(PhoneNumber phone) {
        if (phone.getPost() == null) {
            throw new IllegalArgumentException();
        }

        String updatePhoneNumber = "UPDATE PUHELINNUMERO SET tp_id='' WHERE id=?";
       

        String getpersons = "select HENKLO_HTUNNUS"
                + " from TYOPISTEVARAUS"
                + " where ALKUPVM<CURRENT_TIMESTAMP"
                + " AND LOPPUPVM>CURRENT_TIMESTAMP"
                + " AND TPISTE_ID=?";

        boolean success = false;
        this.session.waitState(true);
        PreparedStatement prep;

        try {
            prep = this.connection.prepareStatement(updatePhoneNumber);
            prep.setString(1, phone.getPhoneNumberID());
            int updatedRows = prep.executeUpdate();
            if (updatedRows > 0) {
                success = true;
                prep = this.connection.prepareStatement(getpersons);
                prep.setString(1, phone.getPost().getPostID());
                ResultSet rs = prep.executeQuery();
                while (rs.next()) {
                    //tarkistus tähän, onko tunnuksella kännykkää, jos ei niin päivitä numero tyhjäksi
                    if (!this.getKannykka(rs.getString("HENKLO_HTUNNUS"))) {
                        this.updateWorkPhone(rs.getString("HENKLO_HTUNNUS"), "");
                    }
//                    if (phone.getPersonID() != null) {
//                        this.updateWorkPhone(phone.getPersonID(), "");
//                    }
                }
                /* XXX Raskas operaatio */
                loadRooms();
                loadPhoneNumbers();
            }
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
        }
        this.session.waitState(false);
        return success;
    }
    public boolean removePhoneNumberFromPerson(PhoneNumber phone) {
        if (phone.getPersonID() == null) {
            System.out.println("henkilö ei saa olla null");
            return false;
        }       
        
        PreparedStatement prep;
        boolean success = false;
        phone.getPersonID();
        
        
        String updatePhoneNumber = "UPDATE PUHELINNUMERO SET h_tunnus='' WHERE id=?";
        
        try {
            prep = this.connection.prepareStatement(updatePhoneNumber);
            prep.setString(1, phone.getPhoneNumberID());
            int updatedRows = prep.executeUpdate();
            if (updatedRows > 0) {
                success = true;
                this.updateWorkPhone(phone.getPersonID(), "");
                /* XXX Raskas operaatio */
                loadRooms();
                loadPhoneNumbers();
            }
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe: " + e.getMessage());
        }
        this.session.waitState(false);
        return success;
        
    }
    /**
     * Palauttaa annetun tyï¿½huoneen puhelinnumerot.
     *
     * @param post Tyï¿½huone <code>Post</code> oliona.
     * @return Puhelinnumerot <code>PhoneNumber[]</code> oliona.
     */
    public PhoneNumber[] getPhoneNumbers(Post post) {
        /*
         // Menisi kutakuinkin nï¿½in jos kï¿½ytettï¿½isiin kantaa eikï¿½ omaa tietorakennetta
         Collection numbers = new Vector();

         try {
         if(this.prepPostPhoneNumbers == null) {
         this.prepPostPhoneNumbers = this.connection.prepareStatement(
         "SELECT id, puhelinnumero FROM PUHELINNUMERO WHERE tp_id = ?"
         );
         }
         this.prepPostPhoneNumbers.setString(1, post.getPostID());
         ResultSet rs = this.prepPostPhoneNumbers.executeQuery();
         while(rs.next()) {
         PhoneNumber pn = new PhoneNumber(this.session,
         rs.getString("id"), post,
         rs.getString("puhelinnumero"));
         numbers.add(pn);
         }
         rs.close();
         } catch (SQLException e) {
         System.err.println("Tietokantavirhe: " + e.getMessage());
         }
		
         System.out.println("humpappaa " + numbers.size());
         return (PhoneNumber[]) numbers.toArray(new PhoneNumber[0]);
         */
        String key = "free";
        if (post != null) {
            key = post.getPostID();
        }
        Collection c = (Collection) this.phoneNumbers.get(key);
        if (c == null) {
            return new PhoneNumber[0];
        }
        //yrittää tehdä null collectionista arrayn
        PhoneNumber[] numbers = (PhoneNumber[]) c.toArray(new PhoneNumber[0]);
        Arrays.sort(numbers);
        return numbers;
    }

    /* --- Muut metodit loppuu --- */

}
