package fi.helsinki.cs.nero.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import fi.helsinki.cs.nero.NeroApplication;
import fi.helsinki.cs.nero.data.Contract;
import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.PhoneNumber;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.data.Project;
import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.data.Room;
import fi.helsinki.cs.nero.data.TimeSlice;
import fi.helsinki.cs.nero.event.NeroObserver;
import fi.helsinki.cs.nero.event.NeroObserverTypes;
import fi.helsinki.cs.nero.logic.Session;

/**
 * @author Jyrki Muukkonen
 */ 
 
public class NeroDatabase implements NeroObserver {
	/**
	 * Tietokantayhteys
	 */
	private Connection connection;

	/**
	 * Session johon tämä tietokantayhteys liittyy.
	 */
	private Session session;

	/**
	 * Kaikki järjestelmän tuntemat huoneet työpisteineen ja puhelinnumeroineen.
	 * Hajautusrakenne, jossa avaimena on huoneen id ja arvona huoneolio.
	 */
	private Map rooms;

	/**
	 * Kaikki järjestelmän tuntemat projektit. Hajautusrakenne, jossa avaimena
	 * on projektin id ja arvona projektiolio.
	 */
	private Map projects;
	
	/**
	 * Kaikki järjestelmän tuntemat työpisteet. Hajautusrakenne, jossa avaimena
	 * on työpisteen id ja arvona työpisteolio.
	 */
	private Map posts;
	
	/**
	 * Kaikki järjestelmän tuntemat puhelinnumerot. Hajautusrakenne, jossa
	 * avaimena on työpisteen id (tai "free") ja arvona vektori puhelinnumeroista
	 */
	private Map phoneNumbers;
	
	/**
	 * Järjestelmän tuntemia henkilöitä, jotka on jo ladattu tietokannasta. Hajautusrakenne,
	 * jossa avaimena on henkilön tunniste (htunnus) ja arvona henkilöolio.
	 */
	private Map people = new Hashtable();

	/**
	 * Tietojen esilataamisessa käytetyt preparedStatementit
	 */
	private PreparedStatement prepAllProjects;
	private PreparedStatement prepAllRooms;
	private PreparedStatement prepAllPosts;
	private PreparedStatement prepAllPhoneNumbers;
	/**
	 * getReservations(Person person, TimeSlice timeslice)-metodin käyttämät
	 * PreparedStatementit.
	 */
	private PreparedStatement prepPersonReservations;
	/**
	 * getRerservations(Post post, TimeSlice timeslice)-metodin käyttämät
	 * PreparedStatementit.
	 */
	private PreparedStatement prepPostReservations;
	/**
	 * getPersons()-metodin käyttämät PreparedStatementit.
	 */
	private PreparedStatement prepPersonContracts;
	/**
	 * getRooms(Project project, TimeSlice timescale)-metodin käyttämät
	 * PreparedStatementit.
	 */
	private PreparedStatement prepProjectRooms;
	/**
	 * getRooms(String roomName, int maxPosts)-metodin käyttämät
	 * PreparedStatementit.
	 */
	private PreparedStatement prepNamedRoomsNormal, prepNamedRoomsWithMaxPosts;
	/**
	 * Varausten käsittelyssä tarvittavat PreparedStatementit
	 */
	private PreparedStatement prepNextReservationID;
	private PreparedStatement prepAddReservation;
	private PreparedStatement prepUpdateReservation;
	private PreparedStatement prepDeleteReservation;
	/**
	 * getPeople()-metodissa käytetyt PreparedStatementit talletetaan hajautukseen,
	 * jossa avaimena on kyselyn SQL-koodi Stringinä ja arvona PreparedStatement
	 */
	private Map prepFilteredPeople = new Hashtable();
	/**
	 * Työpisteiden käsittelyssä tarvittavat PreparedStatementit
	 */
	private PreparedStatement prepNextPostID;
	private PreparedStatement prepCreatePost;
	private PreparedStatement prepDeletePost;
	/**
	 * Puhelinnumeroihin liittyvät PreparedStatementit
	 */
	private PreparedStatement prepPostPhoneNumbers;
	private PreparedStatement prepUpdatePhoneNumber;
	
	/**
	 * Konstruktori. Luo yhteyden tietokantaan ja esilataa tiedot huoneista,
	 * puhelinnumeroista sekä projekteista.
	 * @param session Sessio, johon tämä tietokantaolio liittyy
	 * @param className Tietokanta-ajurin luokan nimi
	 * @param connectionString Tietokantayhteyden nimi
	 * @param username Käyttäjätunnus
	 * @param password Salasana
	 */
	public NeroDatabase(Session session, String className,
			String connectionString, String username, String password)
	{
		this.session = session;
		// kuunnellaan aikajakson ja huonedatan muutoksia, jotta voidaan varmistua, etteivät
		// talletetut henkilöoliot sisällä vanhentunutta tietoa
		session.registerObserver(NeroObserverTypes.TIMESCALE, this);
		session.registerObserver(NeroObserverTypes.ROOMS, this);
		
		session.waitState(true);
		try {	
			this.connection = this.createConnection(className,
					connectionString, username, password);
			this.loadRooms();
			this.loadPhoneNumbers();
			this.loadProjects();
		} catch(SQLException e) {
			System.err.println("Tietokantavirhe: " + e.getMessage());
		} catch(ClassNotFoundException e) {
			System.err.println("Tietokanta-ajuria ei löydy.");
		}
		session.waitState(false);
	}
	
	/**
	 * Luo tietokantayhteyden.
	 * @param className Tietokanta-ajurin luokan nimi
	 * @param connectionString Tietokantayhteyden nimi
	 * @param username Käyttäjätunnus
	 * @param password Salasana
	 * @return Luotu tietokantayhteys
	 * @throws SQLException Jos yhteyden luonti epäonnistuu
	 * @throws ClassNotFoundException Jos tietokanta-ajuria ei löydy
	 */
	private Connection createConnection(String className,
			String connectionString, String username, String password)
			throws SQLException, ClassNotFoundException
	{
		Connection conn = null;
		Class.forName(className);
		conn = DriverManager.getConnection(connectionString, username, password);
		return conn;
	}

	/**
	 * Lataa järjestelmän tuntemat projektit projects-hajautukseen myöhempää
	 * käyttöä varten.
	 * @throws SQLException
	 */
	private void loadProjects() throws SQLException {
		this.projects = new Hashtable();

		session.setStatusMessage("Ladataan projekteja...");
		if(this.prepAllProjects == null) {
			this.prepAllProjects = this.connection.prepareStatement(
					"SELECT koodi, nimi, vastuuhenkilo, alkupvm,"
					/* loppupvm voi olla null, käytetään 2099-12-31 */
					+ " NVL(loppupvm, TO_DATE('2099-12-31', 'YYYY-MM-DD')) AS loppupvm"
					+ " FROM PROJEKTI"
					+ " ORDER BY nimi, alkupvm"
			);
		}
		ResultSet rs = prepAllProjects.executeQuery();

		while (rs.next()) {
			TimeSlice slice = null;
			Date start = rs.getDate("alkupvm");
			Date end = rs.getDate("loppupvm");

			Project p = new Project(this.session, rs.getString("koodi"),
								rs.getString("nimi"),
								rs.getString("vastuuhenkilo"), slice);
			this.projects.put(rs.getString("koodi"), p);
		}
		rs.close();
        session.setStatusMessage("Ladattu tiedot " + this.projects.size() + " projektista.");
	}

	/**
	 * Lataa järjestelmän tuntemat huoneet työpisteineen
	 * rooms-hajautukseen myöhempää käyttöä varten.
	 * @throws SQLException
	 */
	private void loadRooms() throws SQLException {
		this.rooms = new Hashtable();
		this.posts = new Hashtable();
		Collection roomPosts = new LinkedList();
		Collection phoneNumbers = new LinkedList();

		session.setStatusMessage("Ladataan huoneita...");
		if(this.prepAllRooms == null) {
			this.prepAllRooms = this.connection.prepareStatement(
					"SELECT rh.id, rak.nimi AS rakenn_nimi, rh.kerros_numero, rh.numero,"
					+ " rh.nimi, rh.pinta_ala, rh.kuvaus"
					+ " FROM RHUONE rh, RAKENNUS rak"
					+ " WHERE rh.rakenn_tunnus = rak.tunnus"
					);
		}
		if(this.prepAllPosts == null) {
			this.prepAllPosts = this.connection.prepareStatement(
					/*
					"SELECT tp.id as tp_id, puh.id as puh_id, puh.puhelinnumero"
					+ "FROM TYOPISTE tp, PUHELINNUMERO puh"
					+ " WHERE rhuone_id = ? AND puh.tp_id = tp.id"
					+ " ORDER BY tp.id, puh.puhelinnumero");
					*/
					"SELECT id FROM TYOPISTE WHERE rhuone_id = ? ORDER BY id"
			);
		}
		if(this.prepPostPhoneNumbers == null) {
			this.prepPostPhoneNumbers = this.connection.prepareStatement(
					"SELECT id, puhelinnumero FROM PUHELINNUMERO WHERE tp_id = ?"
			);
		}
		
		ResultSet rs = this.prepAllRooms.executeQuery();
		/* NOTE rhuone-taulussa on sekä "numero" että "huone_nro" kentät */
		int numbercount = 0;
		while(rs.next()) {
			Room room = new Room(this.session, rs.getString("id"),
					rs.getString("rakenn_nimi"),
					rs.getString("kerros_numero"),
					rs.getString("numero"), rs.getString("nimi"),
					rs.getDouble("pinta_ala"), rs.getString("kuvaus"));

			this.prepAllPosts.setString(1, rs.getString("id"));
			ResultSet prs = this.prepAllPosts.executeQuery();
			while(prs.next()) {
				Post post = new Post(this.session, prs.getString("id"), room, roomPosts.size()+1);
				
				/* haetaan puhelinnumerot (hidas, kolme sisäkkäistä prepared statementia
				 * mutta olkoot. */
				Collection numbers = new Vector();
				this.prepPostPhoneNumbers.setString(1, post.getPostID());
				ResultSet pnrs = this.prepPostPhoneNumbers.executeQuery();
				while(pnrs.next()) {
					PhoneNumber pn = new PhoneNumber(this.session,
							pnrs.getString("id"), post,
							pnrs.getString("puhelinnumero"));
					numbers.add(pn);
					numbercount++;
				}
				pnrs.close();
				post.setPhoneNumbers((PhoneNumber[])numbers.toArray(new PhoneNumber[0]));
				roomPosts.add(post);
				this.posts.put(prs.getString("id"), post);
			}
			prs.close();
			room.setPosts((Post[]) roomPosts.toArray(new Post[0]));
			roomPosts.clear();
			this.rooms.put(rs.getString("id"), room);
		}
		rs.close();
		session.setStatusMessage("Ladattu tiedot " + this.rooms.size() + " huoneesta.");
	}

	/**
	 * Lataa järjestelmän tuntemat puhelinnumerot.
	 * Saa kutsua vasta loadRooms() jälkeen.
	 * @throws SQLException
	 */
	private void loadPhoneNumbers() throws SQLException {
		this.phoneNumbers = new Hashtable();

		session.setStatusMessage("Ladataan puhelinnumeroita...");
		if(this.prepAllPhoneNumbers == null) {
			this.prepAllPhoneNumbers = this.connection.prepareStatement(
					"SELECT id, puhelinnumero, tp_id FROM PUHELINNUMERO"
			);
		}
		ResultSet rs = prepAllPhoneNumbers.executeQuery();
		
		int count = 0;
		while (rs.next()) {
			PhoneNumber pn;
			String tpid = rs.getString("tp_id");
			String pnid = rs.getString("id");
			String number = rs.getString("puhelinnumero");
			if(tpid == null) {
				pn = new PhoneNumber(this.session, pnid, null, number);
				tpid = "free";
			} else {
				pn = new PhoneNumber(this.session, pnid, 
						(Post)this.posts.get(tpid), number);
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
	

	/* --- Työpistevarauksiin liittyvät metodit alkaa --- */ 
	
	/**
	 * Palauttaa parametrina annetun henkilön työpistevaraukset, jotka
	 * leikkaavat parametrina annettua aikaväliä. Varaukset palautetaan
     * järjestettynä ensisijaisesti alkuajankohdan, toissijaisesti loppuajankohdan
     * mukaan.
	 * 
	 * @param person
	 *            Henkilö, jonka työpistevarauksia haetaan.
	 * @param timeslice
	 *            Aikaväli, jonka aikana varauksen tulee olla ainakin osittain
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

    /**
	 * Palauttaa parametrina annetun työpisteen varaukset, jotka ovat ainakin
	 * osittain päällekäin annetun aikaväli kanssa.
	 * 
	 * @param post
	 *            Työpiste, jonka varauksia haetaan.
	 * @param timeslice
	 *            Aikaväli, jolla varausten tulee olla ainakin osittain
	 *            voimassa.
	 * @return Palautaa varaukset <code>Reservation[]</code> oliona.
	 */
	public Reservation[] getReservations(Post post, TimeSlice timeslice) {
		Collection reservations = new Vector();
		this.session.waitState(true);
		try {
            if(this.prepPostReservations == null)
	            this.prepPostReservations = this.connection.prepareStatement(
					"SELECT DISTINCT tpv.id, tpv.alkupvm, tpv.loppupvm,"
					+ " tpv.viikkotunnit, tpv.selite,"
					+ " h.htunnus, h.etunimet, h.sukunimi"
					+ " FROM TYOPISTEVARAUS tpv, HENKILO h"
					+ " WHERE tpv.tpiste_id = ?"
					+ " AND tpv.henklo_htunnus = h.htunnus"
					+ " AND ? <= tpv.loppupvm AND ? >= tpv.alkupvm"
				);
			this.prepPostReservations.setString(1, post.getPostID());
			this.prepPostReservations.setDate(2, timeslice.getSQLStartDate());
			this.prepPostReservations.setDate(3, timeslice.getSQLEndDate());
			
			ResultSet rs = this.prepPostReservations.executeQuery();
			while(rs.next()) {
				Date start = new Date(rs.getDate("alkupvm").getTime());
				Date end = new Date(rs.getDate("loppupvm").getTime());
				TimeSlice ts = new TimeSlice(start, end);
				Person person = new Person(this.session, rs.getString("htunnus"),
						rs.getString("sukunimi")+" "+rs.getString("etunimet"),
						null, null);
				
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

	/**
	 * Lisää kantaan parametrinä annetun varauksen.
	 * 
	 * @param reservation
	 *            Uusi varaus, joka lisätään kantaan.
	 * @return Onnistuiko lisäys.
	 */
	public boolean createReservation(Reservation reservation) {
		boolean success = false;
		this.session.waitState(true);
		try {
			/* seuraava vapaa ID _täytyy_ hakea ensin
			 * jälkipolville: "SELECT * FROM USER_SEQUENCES" */
			if(this.prepNextReservationID == null) {
				this.prepNextReservationID = this.connection.prepareStatement(
						"SELECT seq_tpvara_id.NEXTVAL FROM dual"
				);
			}
			ResultSet rsid = this.prepNextReservationID.executeQuery();
			rsid.next();
			int nextID = rsid.getInt(1);
			rsid.close();
			// voisi asettaa annetulle Reservationille ID:n jos olisi tarve 
			
			if(this.prepAddReservation == null) {
				this.prepAddReservation = this.connection.prepareStatement(
						" INSERT INTO tyopistevaraus"
						+ " (id, tpiste_id, henklo_htunnus, viikkotunnit,"
						+ "  selite, alkupvm, loppupvm, lisayspvm)"
						/* TRUNC lyhentää ajan pelkäksi päivämääräksi */
						+ " VALUES (?, ?, ?, ?, ?, TRUNC(?), TRUNC(?), SYSDATE)"
				);
			}
			this.prepAddReservation.setInt(1, nextID);
			this.prepAddReservation.setString(2, reservation.getTargetPost().getPostID());
			this.prepAddReservation.setString(3, reservation.getReservingPerson().getPersonID());
			this.prepAddReservation.setInt(4, (int)(reservation.getWeeklyHours()));
			this.prepAddReservation.setString(5, reservation.getDescription());
			this.prepAddReservation.setDate(6, reservation.getTimeSlice().getSQLStartDate());
			this.prepAddReservation.setDate(7, reservation.getTimeSlice().getSQLEndDate());
			
			if(this.prepAddReservation.executeUpdate() > 0) {
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
	 * Poistaa kannasta parametrina annetun varauksen.
	 * 
	 * @param reservation
	 * @return Onnistuiko poisto.
	 */
	public boolean deleteReservation(Reservation reservation) {
		boolean success = false;
		this.session.waitState(true);
		try {
			if(this.prepDeleteReservation == null) {
				this.prepDeleteReservation = this.connection.prepareStatement(
						"DELETE FROM TYOPISTEVARAUS WHERE id = ?");
			}
			this.prepDeleteReservation.setString(1, 
					reservation.getReservationID());
			int deletedRows = this.prepDeleteReservation.executeUpdate();
			if(deletedRows > 0) {
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
	 * Päivittää parametrina annetun varauksen kantaan.
	 * 
	 * @param reservation
	 *            muokattu varaus, joka halutaan tallettaa
	 * @return Onnistuiko päivitys.
	 */
	public boolean updateReservation(Reservation reservation) {
		boolean success = false;
		this.session.waitState(true);
		try {
			if(this.prepUpdateReservation == null) {
				this.prepUpdateReservation = this.connection.prepareStatement(
						"UPDATE TYOPISTEVARAUS"
						+ " SET tpiste_id  = ?, henklo_htunnus = ?,"
						+ " viikkotunnit = ?, selite = ?,"
						+ " alkupvm = TRUNC(?), loppupvm = TRUNC(?)"
						+ " WHERE id = ?"
				);
			}
			this.prepUpdateReservation.setString(1, reservation.getTargetPost().getPostID());
			this.prepUpdateReservation.setString(2,	reservation.getReservingPerson().getPersonID());
			this.prepUpdateReservation.setInt(3, (int)(reservation.getWeeklyHours()));
			this.prepUpdateReservation.setString(4, reservation.getDescription());
			this.prepUpdateReservation.setDate(5, reservation.getTimeSlice().getSQLStartDate());
			this.prepUpdateReservation.setDate(6, reservation.getTimeSlice().getSQLEndDate());
			this.prepUpdateReservation.setString(7,	reservation.getReservationID());

			int updatedRows = this.prepUpdateReservation.executeUpdate();
			if(updatedRows > 0) {
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

	/* --- Työpistevarauksiin liittyvät metodit loppuu --- */ 

	/* --- Sopimuksiin liittyvät metodit alkaa --- */ 

	/**
	 * Palauttaa parametrinä annetun henkilön työsopimusjaksot tietyltä
	 * aikaväliltä.
	 * 
	 * @param person
	 *            Henkilö, jonka sopimuksista ollaan kiinnostuneita.
	 * @param timeslice
	 *            Aikaväli, jonka kannsa sopimukset ovat ainakin osittain
	 *            päällekkäin.
	 * @return Sopimusjaksot <code>Contract[]</code> oliona.
	 */
	public Contract[] getContracts(Person person, TimeSlice timeslice) {
		Collection contracts = new Vector();
		this.session.waitState(true);
		try {
			if(this.prepPersonContracts == null) {
				/* NOTE pelkästään sopimusnumero ei ole yksiselitteinen
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
						+ " AND ? >= tsj.alkupvm_jakso"
				);
			}
			
			this.prepPersonContracts.setString(1, person.getPersonID());
			this.prepPersonContracts.setDate(2, timeslice.getSQLStartDate());
			this.prepPersonContracts.setDate(3, timeslice.getSQLEndDate());
			ResultSet rs = this.prepPersonContracts.executeQuery();
	
			while(rs.next()) {
                Project project = null;
                String projectID = rs.getString("prjkti_koodi");
                if(projectID != null)
                	project = (Project)this.projects.get(projectID);
				Date start = new Date(rs.getDate("alkupvm_jakso").getTime());
				Date end = new Date(rs.getDate("loppupvm_jakso").getTime());
				TimeSlice ts = new TimeSlice(start, end);
                int workingPercentage = rs.getInt("vv_hoitoprosentti");
                // kannassa vv_hoitoprosentti-kentän NULL merkitsee normaalia työskentelyä
                if(rs.wasNull()) workingPercentage = 100;

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
		return (Contract[])contracts.toArray(new Contract[0]);
	}

	/* --- Sopimuksiin liittyvät metodit loppuu --- */ 

	/* --- Henkilöihin liittyvät metodit alkaa --- */ 

	/**
	 * Palauttaa henkilöt jotka täyttävät parametreinä annetut hakuehdot.
	 * Hakuehtoja voi yhdistää, ja oliotyyppiset hakuehdot voivat olla null,
	 * jolloin ne eivät rajaa tulosta.
	 * 
	 * @param timescale
	 *            Aikaväli, jota henkilöiden työsopimusjaksojen tule leikata
	 * @param personName
	 *            Merkkijono, johon henkilön (suku?)nimeä verrataan.
	 * @param partTimeTeachersOnly
	 *            jos tosi, näytetään vain sivutoimiset tuntiopettajat
	 * @param withoutPost
	 *            jos tosi, näytetään vain ne, joilla on työsopimusjakso ilman
	 *            samanaikaista työpistevarausta
	 * @param showEndingContracts
	 *            jos tosi, näytetään vain ne, joiden viimeinen työsopimusjakso
	 *            on aikavälillä
	 * @param project
	 *            projekti, jonka henkilöt näytetään
	 * @return henkilöt <code>Person[]</code> oliona.
	 */
	public Person[] getPeople(TimeSlice timescale, String personName,
			Project project, boolean showEndingContracts, boolean withoutPost,
			boolean partTimeTeachersOnly
	) {
		Collection filteredPeople = new LinkedList();
		long startTime = System.currentTimeMillis();
		this.session.waitState(true);
		
		session.setStatusMessage("Haetaan henkilöitä..."
				/*
				+ " (aikaväli: " + timescale
				+ ", nimi: " + personName
				+ ", projekti: " + project
				+ ", loppuvat: " + showEndingContracts
				+ ", surulliset: " + withoutPost
				+ ", osa-aikaiset: " + partTimeTeachersOnly
				+ ")"
				*/
		);

		// Kootaan SQL-kysely paloista
		// Yhteinen alkuosa
		String sqlQuery = "SELECT DISTINCT h.htunnus, h.sukunimi, h.etunimet, "
		    + "   max(tsj.loppupvm_jakso) as loppupvm"
			+ " FROM TYOSOPIMUSJAKSO tsj, HENKILO h"
			+ " WHERE (UPPER(h.sukunimi) LIKE UPPER(?)"
			+ " OR UPPER(h.etunimet) LIKE UPPER(?))";
		
		// täsmällisemmissä hauissa täytyy katsoa työsopimusjaksoa
		if(showEndingContracts || withoutPost ||
				project != null || partTimeTeachersOnly)
			sqlQuery += " AND tsj.henklo_htunnus = h.htunnus"
				+ " AND tsj.sopimustyyppi LIKE ?"
				/* Oraclessa like-vertailu nulliin ei toimi,
				 * joten käytetään NVL()-funktiota */
				+ " AND NVL(tsj.prjkti_koodi, 'oraclesucks') LIKE ?"
				+ " AND ? <= tsj.loppupvm_jakso AND ? >= tsj.alkupvm_jakso";
		else // tehdään ulkoliitos eli saadaan myös henkilöt ilman sopimusjaksoja
			sqlQuery += " AND tsj.henklo_htunnus(+) = h.htunnus";
        
		// Jos pyydetty työpisteettömät mutta ei päättyviä sopimuksia,
		// tarkistetaan helpoin tapaus tässä (yksi vähintään
		// koko sopimusjakson peittävä varaus) ja loput tarkistetaan koodissa
		if(withoutPost && !showEndingContracts)
			sqlQuery += " AND NOT EXISTS ("
				+ " SELECT id"
				+ " FROM tyopistevaraus"
				+ " WHERE henklo_htunnus = h.htunnus"
				+ " AND alkupvm <= greatest(tsj.alkupvm_jakso, ?)"
				+ " AND loppupvm >= least(tsj.loppupvm_jakso, ?)"
				+ ")";
		
		// Yhteinen GROUP BY -osa
		sqlQuery += " GROUP BY h.htunnus, h.sukunimi, h.etunimet";
		
		// jos näytetään vain päättyvät sopimukset, niin voidaan rajata jo nyt,
		// mutta jos mukana ovat myös työpisteettömät, niin täytyy rajaus tehdä myöhemmin
		if(showEndingContracts && !withoutPost)
			sqlQuery += " HAVING MAX(loppupvm_jakso) BETWEEN ? AND ?";
	    
		// Yhteinen ORDER BY -osa
        sqlQuery += " ORDER BY h.sukunimi, h.etunimet";
		
		try {
			// katsotaan olisiko sqlQuerya vastaava PreparedStatement jo valmiina
			PreparedStatement prep = (PreparedStatement) prepFilteredPeople.get(sqlQuery);
			// jos ei ole, tehdään ja pannaan talteen
			if(prep == null) {
				prep = this.connection.prepareStatement(sqlQuery);
				prepFilteredPeople.put(sqlQuery, prep);
			}
			long prepTime = System.currentTimeMillis();
			//System.err.println("Valmisteluun meni aikaa ms: " + (prepTime - startTime));
			
			// Asetetaan parametrit; käytetään laskuria apuna
			// Laskuria kasvatetaan jokaisen käyttökerran yhteydessä ++ -operaattorilla
			int paramNo = 1;
			// päivämääriä on turha muodostaa joka kerta, joten otetaan talteen
			java.sql.Date start = timescale.getSQLStartDate();
			java.sql.Date end = timescale.getSQLEndDate();
			
			/* henkilön nimi, jos annettu */
			if(personName == null) {
				prep.setString(paramNo++, "%");
				prep.setString(paramNo++, "%");
			} else {
				prep.setString(paramNo++, "%"+personName+"%");
				prep.setString(paramNo++, "%"+personName+"%");
			}	
			
			if(showEndingContracts || withoutPost ||
					project != null || partTimeTeachersOnly) {
				
				/* sopimustyyppi:
				 * S sivutoiminen, P päätoiminen, L laitostehtävä, D dosentuuri */
				if(partTimeTeachersOnly) {
					prep.setString(paramNo++, "S");
				} else {
					prep.setString(paramNo++, "%");
				}
				/* projektia peliin */
				if(project == null) {
					prep.setString(paramNo++, "%");
				} else {
					prep.setString(paramNo++, project.getProjectID());
				}
				/* aikaväli*/
				prep.setDate(paramNo++, start);
				prep.setDate(paramNo++, end);
			}

			/* työpisteettömät */
			if(withoutPost && !showEndingContracts) {
				prep.setDate(paramNo++, start);
				prep.setDate(paramNo++, end);
			}

			/* päättyvät sopimukset */
			if(showEndingContracts && !withoutPost) {
				prep.setDate(paramNo++, start);
				prep.setDate(paramNo++, end);
			}

			ResultSet rs = prep.executeQuery();
			while(rs.next()) {
				// Yritetään käyttää tallessa olevaa henkilöä
				Person person = (Person) people.get(rs.getString("htunnus"));
				if(person == null) { // ei löytynyt, täytyy luoda uusi
                    /* contracts ja reservations saavat olla null uudella henkilöllä */
                    Contract[] contracts = null;
                    Date lastContractEnd = rs.getDate("loppupvm");
                    if(lastContractEnd == null || lastContractEnd.before(start)) {
                        // haa, tiedetään ettei sopimusjaksoja ole aikavälillä
                        // kerrotaan se Personille ettei se turhaan kysele
                        contracts = new Contract[0];
                    }
                   	person = new Person(this.session, 
                   			rs.getString("htunnus"),
							rs.getString("sukunimi")+" "+rs.getString("etunimet"),
							contracts, null);
					people.put(rs.getString("htunnus"), person);
				}

				if(filterPerson(person, timescale, rs.getDate("loppupvm"), showEndingContracts, withoutPost))
					filteredPeople.add(person);
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

	/**
	 * Tarkista kuuluuko henkilö hakuehtojen mukaiseen listaan. Tarkistus tehdään, koska
	 * vastaavan seikan tarkistaminen SQL-kannassa on hyvin vaikeaa ja hidasta.
	 * @param person henkilö
	 * @param timescale tarkasteltava aikaväli
	 * @param contractEndDate henkilö
	 * @param showEndingContracts halutaanko päättyvät työsopimukset
	 * @param withoutPost halutaanko työpisteettömät
	 * @return Sopivatko annetut hakuehdot henkilöön
	 */
	private boolean filterPerson(Person person, TimeSlice timescale, java.sql.Date contractEndDate, boolean showEndingContracts, boolean withoutPost) {
		// jos ei pyydetty työpisteettömiä, ei filtteröintiä tarvita 
		if(!withoutPost)
			return true;
		// jos pyydettiin päättyviä sopimuksia, tarkistetaan ensin ne
		if(showEndingContracts && timescale.contains(contractEndDate))
			return true;
		// muussa tapauksessa täytyy tutkia tarkemmin henkilön varaukset
		return person.getStatus();
	}

	/* --- Henkilöihin liittyvät metodit loppuu --- */ 

	/* --- Työpisteisiin liittyvät metodit alkaa --- */ 

	/**
	 * Poistaa työpisteen.
	 * 
	 * @param post Poistettava työpiste
	 * @return Onnistuiko poisto.
	 */
	public boolean deletePost(Post post) {
		boolean success = false;
		this.session.waitState(true);
		try {
			if(this.prepDeletePost == null) {
				this.prepDeletePost = this.connection.prepareStatement(
						"DELETE FROM TYOPISTE WHERE id = ?");
			}
			this.prepDeletePost.setString(1, post.getPostID());
			int deletedRows = this.prepDeletePost.executeUpdate();
			if(deletedRows > 0) {
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
			/* seuraava vapaa ID _täytyy_ hakea ensin */
			if(this.prepNextPostID == null) {
				this.prepNextPostID = this.connection.prepareStatement(
						"SELECT seq_tpiste_id.NEXTVAL FROM dual"
				);
			}
			ResultSet rsid = this.prepNextPostID.executeQuery();
			rsid.next();
			int nextID = rsid.getInt(1);
			rsid.close();
			
			if(this.prepCreatePost == null) {
				this.prepCreatePost = this.connection.prepareStatement(
						" INSERT INTO tyopiste (id, rhuone_id, lisayspvm)"
						+ " VALUES (?, ?, SYSDATE)"
				);
			}
			this.prepCreatePost.setInt(1, nextID);
			this.prepCreatePost.setString(2, post.getRoom().getRoomID());
			
			if(this.prepCreatePost.executeUpdate() > 0) {
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

	/* --- Työpisteisiin liittyvät metodit loppuu --- */ 

	/* --- Työhuoneisiin liittyvät metodit alkaa --- */ 

	/**
	 * Palauttaa kaikki järjestelmän tuntemat huoneet.
	 * 
	 * @return huoneet Room[]-oliona
	 */
	public Room[] getRooms() {
		return (Room[]) rooms.values().toArray(new Room[0]);
	}

	/**
	 * Hakee tietyn projektin henkilöille varatut huoneet tietyllä aikavälillä.
	 * Jos projekti on null, palautetaan tyhjä huonetaulukko.
	 * 
	 * @param project Projekti, jonka työhuoneita haetaan. Jos null,
	 * palautetaan tyhjä lista.
	 * @param timescale Aikaväli, jota varauksien tulee leikata.
	 * @return taulukko projektin huoneista
	 */
	public Room[] getRooms(Project project, TimeSlice timescale) {
		if(project == null)
			return new Room[0];
		Collection rooms = new Vector();
		this.session.waitState(true);
		try {
			if(this.prepProjectRooms == null) {
				this.prepProjectRooms = this.connection.prepareStatement(
					"SELECT h.ID AS huone FROM RHUONE h,"
					+ " TYOPISTE tp, TYOPISTEVARAUS tpv, TYOSOPIMUSJAKSO tsj"
					+ " WHERE h.ID = tp.RHUONE_ID AND tp.ID = tpv.TPISTE_ID"
					+ " AND tpv.HENKLO_HTUNNUS = tsj.HENKLO_HTUNNUS"
					+ " AND tsj.PRJKTI_KOODI = ?"
					/* sekä työpistevarauksen että työsopimusjakson päivämäärät
					 * pitää olla oikein */
					+ " AND ? <= tsj.LOPPUPVM_JAKSO AND ? >= tsj.ALKUPVM_JAKSO"
					+ " AND ? <= tpv.LOPPUPVM AND ? >= tpv.ALKUPVM" 
				);
			}
			this.prepProjectRooms.setString(1, project.getProjectID());
			
			java.sql.Date start = timescale.getSQLStartDate();
			java.sql.Date end = timescale.getSQLEndDate();
			this.prepProjectRooms.setDate(2, start);
			this.prepProjectRooms.setDate(3, end);
			this.prepProjectRooms.setDate(4, start);
			this.prepProjectRooms.setDate(5, end);
			
			ResultSet rs = this.prepProjectRooms.executeQuery();
			while(rs.next()) {
				rooms.add(this.rooms.get(rs.getString("huone")));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Tietokantavirhe: " + e.getMessage());
		}
		this.session.waitState(false);
		return (Room[])rooms.toArray(new Room[0]);
	}

	/**
	 * Palauttaa ne huoneet, jotka täyttävät annetut hakuehdot.
	 * 
	 * @param roomFilter
	 *            String jota etsitään huoneen nimestä ja numerosta.           
	 * @param maxPosts
	 *            Huoneen työpisteiden maksimilukumäärä. Jos pienempi kuin 0, ei rajaa tulosta.
	 * @return Hakuehdot täyttävät huoneet Room[]-oliona
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
			if(this.prepNamedRoomsNormal == null) {
				this.prepNamedRoomsNormal =
					this.connection.prepareStatement(queryNormal);
			}
			if(this.prepNamedRoomsWithMaxPosts == null) {
				this.prepNamedRoomsWithMaxPosts = 
					this.connection.prepareStatement(queryWithMaxPosts);
			}

			ResultSet rs;
			if(maxPosts < 0) {
				this.prepNamedRoomsNormal.setString(1, "%"+roomFilter+"%");
				this.prepNamedRoomsNormal.setString(2, "%"+roomFilter+"%");
				rs = this.prepNamedRoomsNormal.executeQuery();
			} else {
				this.prepNamedRoomsWithMaxPosts.setString(1, "%"+roomFilter+"%");
				this.prepNamedRoomsWithMaxPosts.setString(2, "%"+roomFilter+"%");
				this.prepNamedRoomsWithMaxPosts.setInt(3, maxPosts);
				rs = this.prepNamedRoomsWithMaxPosts.executeQuery();
			}
			while(rs.next()) {
				Room r = (Room)this.rooms.get(rs.getString("id"));
				temprooms.add(r);
			}
			rs.close();
		} catch(SQLException e) {
			System.err.println("Tietokantavirhe: " + e.getMessage());
		}
		this.session.waitState(false);
		return (Room[]) temprooms.toArray(new Room[0]);
	}

	/**
	 * Palauttaa huoneolion huone-id:n perusteella.
	 * @param roomID huoneen id
	 * @return huoneolio tai null, jos kyseistä huonetta ei löydy
	 */
	public Room getRoom(String roomID) {
		return (Room) rooms.get(roomID);
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
	 * mukaiseksi.
	 * 
	 * @param phone Uusi versio puhelinnumerosta (uusi työpiste id).
	 * @return Onnistuiko päivitys.
	 */
	public boolean updatePhoneNumber(PhoneNumber phone) {
		boolean success = false;
		this.session.waitState(true);
		try {
			if(this.prepUpdatePhoneNumber == null) {
				this.prepUpdatePhoneNumber = this.connection.prepareStatement(
						"UPDATE PUHELINNUMERO SET tp_id  = ? WHERE id = ?"
				);
			}
			Post post = phone.getPost();
			if(post == null) {
				this.prepUpdatePhoneNumber.setString(1, "");
			} else {
				this.prepUpdatePhoneNumber.setString(1, post.getPostID());
			}
			this.prepUpdatePhoneNumber.setString(2, phone.getPhoneNumberID());

			int updatedRows = this.prepUpdatePhoneNumber.executeUpdate();
			if(updatedRows > 0) {
				success = true;
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
	 * Palauttaa annetun työhuoneen puhelinnumerot.
	 * @param post Työhuone <code>Post</code> oliona.
	 * @return Puhelinnumerot <code>PhoneNumber[]</code> oliona.
	 */
	public PhoneNumber[] getPhoneNumbers(Post post) {
		/*
		// Menisi kutakuinkin näin jos käytettäisiin kantaa eikä omaa tietorakennetta
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
		if(post != null) {
			key = post.getPostID();
		} 
		Collection c = (Collection)this.phoneNumbers.get(key);
		PhoneNumber[] numbers = (PhoneNumber[])c.toArray(new PhoneNumber[0]);
		Arrays.sort(numbers);
		return numbers;
	}
	
	/* --- Puhelinnumeroihin liittyvät metodit loppuu --- */ 

	/* --- Projekteihin liittyvät metodit alkaa --- */ 

	/**
	 * Palauttaa kaikki järjestelmän tuntemat projektit järjestettynä
	 * Project[] -taulukkona.
	 * 
	 * @return projektit <code>Project[]</code> oliona
	 */
	public Project[] getProjects() {
		Project[] projs = (Project[]) projects.values().toArray(new Project[0]);
		/* HashTable hukkaa järjestyksen, joten sortataan */
		Arrays.sort(projs);
		return projs;
	}

	/* --- Projekteihin liittyvät metodit loppuu --- */ 

	/* --- Muut metodit alkaa --- */ 

	/**
	 * Palauttaa tietokantayhteyden. Käytä varovaisesti, tarkoitettu lähinnä
	 * testejä varten.
	 * @return Yhteys <code>Connection</code> oliona.
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * NeroObserver-kuuntelija. Käytännössä kuuntelee vain TIMESCALE ja ROOMS
	 * tyyppejä, mutta ei tarkista mikä tyyppi vastaanotettiin.
	 * @param type Kuuntelijatyyppi, ei vaikutusta. 
	 * @see fi.helsinki.cs.nero.event.NeroObserver#updateObserved(int)
	 */
	public void updateObserved(int type) {
		// Aikajakso tai huonetiedot ovat muuttuneet. Tiedot henkilöistä eivät enää
		// ole ajan tasalla.
		//System.err.println("DB: heitetään pois tiedot henkilöistä");
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
		System.out.println(ndb.connection.getMetaData()
				.getDatabaseProductVersion());
		// testailusälä poistettu, riippuvaista kannan vanhasta sisällöstä.
		System.out.println("done.");
	}

	/* --- Muut metodit loppuu --- */

}