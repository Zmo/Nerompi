package fi.helsinki.cs.nero.db;

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
	 * Kaikki jï¿½rjestelmï¿½n tuntemat huoneet tyï¿½pisteineen ja puhelinnumeroineen.
	 * Hajautusrakenne, jossa avaimena on huoneen id ja arvona huoneolio.
	 */
	private Map rooms;

	/**
	 * Kaikki jï¿½rjestelmï¿½n tuntemat projektit. Hajautusrakenne, jossa avaimena
	 * on projektin id ja arvona projektiolio.
	 */
	private Map projects;
	
	/**
	 * Kaikki jï¿½rjestelmï¿½n tuntemat tyï¿½pisteet. Hajautusrakenne, jossa avaimena
	 * on tyï¿½pisteen id ja arvona tyï¿½pisteolio.
	 */
	private Map posts;
	
	/**
	 * Kaikki jï¿½rjestelmï¿½n tuntemat puhelinnumerot. Hajautusrakenne, jossa
	 * avaimena on tyï¿½pisteen id (tai "free") ja arvona vektori puhelinnumeroista
	 */
	private Map phoneNumbers;
	
	/**
	 * Jï¿½rjestelmï¿½n tuntemia henkilï¿½itï¿½, jotka on jo ladattu tietokannasta. Hajautusrakenne,
	 * jossa avaimena on henkilï¿½n tunniste (htunnus) ja arvona henkilï¿½olio.
	 */
	private Map people = new Hashtable();

	/**
	 * Tietojen esilataamisessa kï¿½ytetyt preparedStatementit
	 */
	private PreparedStatement prepAllProjects;
	private PreparedStatement prepAllRooms;
	private PreparedStatement prepAllPosts;
	private PreparedStatement prepAllPhoneNumbers;
	/**
	 * getReservations(Person person, TimeSlice timeslice)-metodin kï¿½yttï¿½mï¿½t
	 * PreparedStatementit.
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
	 * getPeople()-metodissa kï¿½ytetyt PreparedStatementit talletetaan hajautukseen,
	 * jossa avaimena on kyselyn SQL-koodi Stringinï¿½ ja arvona PreparedStatement
	 */
	private Map prepFilteredPeople = new Hashtable();
	/**
	 * Tyï¿½pisteiden kï¿½sittelyssï¿½ tarvittavat PreparedStatementit
	 */
	private PreparedStatement prepNextPostID;
	private PreparedStatement prepCreatePost;
	private PreparedStatement prepDeletePost;
	/**
	 * Puhelinnumeroihin liittyvï¿½t PreparedStatementit
	 */
	private PreparedStatement prepPostPhoneNumbers;
	private PreparedStatement prepUpdatePhoneNumber;
	
	/**
	 * Konstruktori. Luo yhteyden tietokantaan ja esilataa tiedot huoneista,
	 * puhelinnumeroista sekï¿½ projekteista.
	 * @param session Sessio, johon tï¿½mï¿½ tietokantaolio liittyy
	 * @param className Tietokanta-ajurin luokan nimi
	 * @param connectionString Tietokantayhteyden nimi
	 * @param username Kï¿½yttï¿½jï¿½tunnus
	 * @param password Salasana
	 */
	public NeroDatabase(Session session, String className,
			String connectionString, String username, String password)
	{
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
			this.loadProjects();
		} catch(SQLException e) {
			System.err.println("Tietokantavirhe: " + e.getMessage());
		} catch(ClassNotFoundException e) {
			System.err.println("Tietokanta-ajuria ei lï¿½ydy.");
		}
		session.waitState(false);
	}
	
	/**
	 * Luo tietokantayhteyden.
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
			throws SQLException, ClassNotFoundException
	{
		Connection conn = null;
		Class.forName(className);
		conn = DriverManager.getConnection(connectionString, username, password);
		return conn;
	}

	/**
	 * Lataa jï¿½rjestelmï¿½n tuntemat projektit projects-hajautukseen myï¿½hempï¿½ï¿½
	 * kï¿½yttï¿½ï¿½ varten.
	 * @throws SQLException
	 */
	private void loadProjects() throws SQLException {
		this.projects = new Hashtable();

		session.setStatusMessage("Ladataan projekteja...");
		if(this.prepAllProjects == null) {
			this.prepAllProjects = this.connection.prepareStatement(
					"SELECT koodi, nimi, vastuuhenkilo, alkupvm,"
					/* loppupvm voi olla null, kï¿½ytetï¿½ï¿½n 2099-12-31 */
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
	 * Lataa jï¿½rjestelmï¿½n tuntemat huoneet tyï¿½pisteineen
	 * rooms-hajautukseen myï¿½hempï¿½ï¿½ kï¿½yttï¿½ï¿½ varten.
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
		/* NOTE rhuone-taulussa on sekï¿½ "numero" ettï¿½ "huone_nro" kentï¿½t */
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
				
				/* haetaan puhelinnumerot (hidas, kolme sisï¿½kkï¿½istï¿½ prepared statementia
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
	 * Lataa jï¿½rjestelmï¿½n tuntemat puhelinnumerot.
	 * Saa kutsua vasta loadRooms() jï¿½lkeen.
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

    /**
	 * Palauttaa parametrina annetun tyï¿½pisteen varaukset, jotka ovat ainakin
	 * osittain pï¿½ï¿½llekï¿½in annetun aikavï¿½li kanssa.
	 * 
	 * @param post
	 *            Tyï¿½piste, jonka varauksia haetaan.
	 * @param timeslice
	 *            Aikavï¿½li, jolla varausten tulee olla ainakin osittain
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
                                        null, null, rs.getString("huone_nro"), rs.getString("kutsumanimi"),
                                        rs.getString("aktiivisuus"), rs.getString("hetu"), rs.getString("oppiarvo"),
                                        rs.getString("titteli"), rs.getString("puhelin_tyo"), rs.getString("puhelin_koti"),
                                        rs.getString("katuosoite"), rs.getString("postinro"), rs.getString("postitoimipaikka"),
                                        rs.getString("valvontasaldo"), rs.getString("sahkopostiosoite"),
                                        rs.getString("hallinnollinen_kommentti"), rs.getString("opiskelija_kommentti"),
                                        rs.getString("ktunnus"), rs.getString("kannykka"), rs.getString("postilokerohuone"),
                                        rs.getString("hy_tyosuhde"), rs.getString("hy_puhelinluettelossa"));
				
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
	 * Lisï¿½ï¿½ kantaan parametrinï¿½ annetun varauksen.
	 * 
	 * @param reservation
	 *            Uusi varaus, joka lisï¿½tï¿½ï¿½n kantaan.
	 * @return Onnistuiko lisï¿½ys.
	 */
	public boolean createReservation(Reservation reservation) {
		boolean success = false;
		this.session.waitState(true);
		try {
			/* seuraava vapaa ID _tï¿½ytyy_ hakea ensin
			 * jï¿½lkipolville: "SELECT * FROM USER_SEQUENCES" */
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
                                        /* TRUNC lyhentï¿½ï¿½ ajan pelkï¿½ksi pï¿½ivï¿½mï¿½ï¿½rï¿½ksi */
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

		// poistetaan henkilï¿½n tiedot jotka ovat nyt vanhentuneet
		people.remove(reservation.getReservingPerson().getPersonID());
		this.session.waitState(false);
		return success;
	}

	/**
	 * Pï¿½ivittï¿½ï¿½ parametrina annetun varauksen kantaan.
	 * 
	 * @param reservation
	 *            muokattu varaus, joka halutaan tallettaa
	 * @return Onnistuiko pï¿½ivitys.
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
		
		// poistetaan henkilï¿½n tiedot jotka ovat nyt vanhentuneet
		people.remove(reservation.getReservingPerson().getPersonID());
		this.session.waitState(false);
		return success;
	}
        
	/* --- Tyï¿½pistevarauksiin liittyvï¿½t metodit loppuu --- */ 

	/* --- Sopimuksiin liittyvï¿½t metodit alkaa --- */ 

	/**
	 * Palauttaa parametrinï¿½ annetun henkilï¿½n tyï¿½sopimusjaksot tietyltï¿½
	 * aikavï¿½liltï¿½.
	 * 
	 * @param person
	 *            Henkilï¿½, jonka sopimuksista ollaan kiinnostuneita.
	 * @param timeslice
	 *            Aikavï¿½li, jonka kannsa sopimukset ovat ainakin osittain
	 *            pï¿½ï¿½llekkï¿½in.
	 * @return Sopimusjaksot <code>Contract[]</code> oliona.
	 */
	public Contract[] getContracts(Person person, TimeSlice timeslice) {
		Collection contracts = new Vector();
		this.session.waitState(true);
		try {
			if(this.prepPersonContracts == null) {
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
                // kannassa vv_hoitoprosentti-kentï¿½n NULL merkitsee normaalia tyï¿½skentelyï¿½
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

	/* --- Sopimuksiin liittyvï¿½t metodit loppuu --- */ 

	/* --- Henkilï¿½ihin liittyvï¿½t metodit alkaa --- */ 

	/**
	 * Palauttaa henkilï¿½t jotka tï¿½yttï¿½vï¿½t parametreinï¿½ annetut hakuehdot.
	 * Hakuehtoja voi yhdistï¿½ï¿½, ja oliotyyppiset hakuehdot voivat olla null,
	 * jolloin ne eivï¿½t rajaa tulosta.
	 * 
	 * @param timescale
	 *            Aikavï¿½li, jota henkilï¿½iden tyï¿½sopimusjaksojen tule leikata
	 * @param personName
	 *            Merkkijono, johon henkilï¿½n (suku?)nimeï¿½ verrataan.
	 * @param partTimeTeachersOnly
	 *            jos tosi, nï¿½ytetï¿½ï¿½n vain sivutoimiset tuntiopettajat
	 * @param withoutPost
	 *            jos tosi, nï¿½ytetï¿½ï¿½n vain ne, joilla on tyï¿½sopimusjakso ilman
	 *            samanaikaista tyï¿½pistevarausta
	 * @param showEndingContracts
	 *            jos tosi, nï¿½ytetï¿½ï¿½n vain ne, joiden viimeinen tyï¿½sopimusjakso
	 *            on aikavï¿½lillï¿½
	 * @param project
	 *            projekti, jonka henkilï¿½t nï¿½ytetï¿½ï¿½n
	 * @return henkilï¿½t <code>Person[]</code> oliona.
	 */
	public Person[] getPeople(TimeSlice timescale, String personName,
			Project project, boolean showEndingContracts, boolean withoutPost,
			boolean partTimeTeachersOnly
	) {
		Collection filteredPeople = new LinkedList();
		long startTime = System.currentTimeMillis();
		this.session.waitState(true);
		
		session.setStatusMessage("Haetaan henkilï¿½itï¿½..."
				/*
				+ " (aikavï¿½li: " + timescale
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
		if(showEndingContracts || withoutPost ||
				project != null || partTimeTeachersOnly)
			sqlQuery += " AND tsj.henklo_htunnus = h.htunnus"
				+ " AND tsj.sopimustyyppi LIKE ?"
				/* Oraclessa like-vertailu nulliin ei toimi,
				 * joten kï¿½ytetï¿½ï¿½n NVL()-funktiota */
				+ " AND NVL(tsj.prjkti_koodi, 'oraclesucks') LIKE ?"
				+ " AND ? <= tsj.loppupvm_jakso AND ? >= tsj.alkupvm_jakso";
		else // tehdï¿½ï¿½n ulkoliitos eli saadaan myï¿½s henkilï¿½t ilman sopimusjaksoja
			sqlQuery += " AND tsj.henklo_htunnus(+) = h.htunnus";
        
		// Jos pyydetty tyï¿½pisteettï¿½mï¿½t mutta ei pï¿½ï¿½ttyviï¿½ sopimuksia,
		// tarkistetaan helpoin tapaus tï¿½ssï¿½ (yksi vï¿½hintï¿½ï¿½n
		// koko sopimusjakson peittï¿½vï¿½ varaus) ja loput tarkistetaan koodissa
		if(withoutPost && !showEndingContracts)
			sqlQuery += " AND NOT EXISTS ("
				+ " SELECT id"
				+ " FROM tyopistevaraus"
				+ " WHERE henklo_htunnus = h.htunnus"
				+ " AND alkupvm <= greatest(tsj.alkupvm_jakso, ?)"
				+ " AND loppupvm >= least(tsj.loppupvm_jakso, ?)"
				+ ")";
		
		// Yhteinen GROUP BY -osa
		sqlQuery += " GROUP BY h.htunnus, h.sukunimi, h.etunimet, h.huone_nro, h.kutsumanimi,"
                    + " h.aktiivisuus, h.hetu, h.oppiarvo, h.titteli,"
                    + " h.puhelin_tyo, h.puhelin_koti, h.katuosoite, h.katuosoite,"
                    + " h.postinro, h.postitoimipaikka, h.valvontasaldo, h.sahkopostiosoite,"
                    + " h.hallinnollinen_kommentti, h.opiskelija_kommentti, h.ktunnus,"
                    + " h.kannykka, h.postilokerohuone, h.hy_tyosuhde, h.hy_puhelinluettelossa";
		
		// jos nï¿½ytetï¿½ï¿½n vain pï¿½ï¿½ttyvï¿½t sopimukset, niin voidaan rajata jo nyt,
		// mutta jos mukana ovat myï¿½s tyï¿½pisteettï¿½mï¿½t, niin tï¿½ytyy rajaus tehdï¿½ myï¿½hemmin
		if(showEndingContracts && !withoutPost)
			sqlQuery += " HAVING MAX(loppupvm_jakso) BETWEEN ? AND ?";
	    
		// Yhteinen ORDER BY -osa
        sqlQuery += " ORDER BY h.sukunimi, h.etunimet";
		
		try {
			// katsotaan olisiko sqlQuerya vastaava PreparedStatement jo valmiina
			PreparedStatement prep = (PreparedStatement) prepFilteredPeople.get(sqlQuery);
			// jos ei ole, tehdï¿½ï¿½n ja pannaan talteen
			if(prep == null) {
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
				 * S sivutoiminen, P pï¿½ï¿½toiminen, L laitostehtï¿½vï¿½, D dosentuuri */
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
				/* aikavï¿½li*/
				prep.setDate(paramNo++, start);
				prep.setDate(paramNo++, end);
			}

			/* tyï¿½pisteettï¿½mï¿½t */
			if(withoutPost && !showEndingContracts) {
				prep.setDate(paramNo++, start);
				prep.setDate(paramNo++, end);
			}

			/* pï¿½ï¿½ttyvï¿½t sopimukset */
			if(showEndingContracts && !withoutPost) {
				prep.setDate(paramNo++, start);
				prep.setDate(paramNo++, end);
			}

			ResultSet rs = prep.executeQuery();
			while(rs.next()) {
				// Yritetï¿½ï¿½n kï¿½yttï¿½ï¿½ tallessa olevaa henkilï¿½ï¿½
				Person person = (Person) people.get(rs.getString("htunnus"));
				if(person == null) { // ei lï¿½ytynyt, tï¿½ytyy luoda uusi
                    /* contracts ja reservations saavat olla null uudella henkilï¿½llï¿½ */
                    Contract[] contracts = null;
                    Date lastContractEnd = rs.getDate("loppupvm");
                    if(lastContractEnd == null || lastContractEnd.before(start)) {
                        // haa, tiedetï¿½ï¿½n ettei sopimusjaksoja ole aikavï¿½lillï¿½
                        // kerrotaan se Personille ettei se turhaan kysele
                        contracts = new Contract[0];
                    }
                   	person = new Person(this.session, 
                   			rs.getString("htunnus"),
                                        rs.getString("sukunimi")+" "+rs.getString("etunimet"),
                                        contracts, null, rs.getString("huone_nro"), rs.getString("kutsumanimi"),
                                        rs.getString("aktiivisuus"), rs.getString("hetu"), rs.getString("oppiarvo"),
                                        rs.getString("titteli"), rs.getString("puhelin_tyo"), rs.getString("puhelin_koti"),
                                        rs.getString("katuosoite"), rs.getString("postinro"), rs.getString("postitoimipaikka"),
                                        rs.getString("valvontasaldo"), rs.getString("sahkopostiosoite"),
                                        rs.getString("hallinnollinen_kommentti"), rs.getString("opiskelija_kommentti"),
                                        rs.getString("ktunnus"), rs.getString("kannykka"), rs.getString("postilokerohuone"),
                                        rs.getString("hy_tyosuhde"), rs.getString("hy_puhelinluettelossa"));
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
		session.setStatusMessage("Lï¿½ytyi " + filteredPeople.size() + " henkilï¿½ï¿½.");
		return (Person[]) filteredPeople.toArray(new Person[0]);
	}
        public void savePersonInfo(Person person) {
            boolean success = false;
            this.session.waitState(true);
            try {
                PreparedStatement prepNextPersonId = this.connection.prepareStatement("SELECT * FROM henkilo WHERE htunnus="+person.getPersonID());       
                
                PreparedStatement prepModifyperson = this.connection.prepareStatement(
                        " INSERT INTO henkilo"
                        + " (htunnus, etunumet, sukunimi, kutsumanimi, aktiivisuus, huone_nro,"
                        + " hetu, oppiarvo, titteli, puhelin_tyo, puhelin_koti, katuosoite,"
                        + " postinro, postitoimipaikka, valvontasaldo, sahkopostiosoite, hallinnollinen_kommentti"
                        + " opiskelija_kommentti, ktunnus, kannykka, postilokerohuone, hy_tyosuhde, hy_puhelinluettelossa)"
                        /* TRUNC lyhentï¿½ï¿½ ajan pelkï¿½ksi pï¿½ivï¿½mï¿½ï¿½rï¿½ksi */
                        // 23 arvoa
                        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                );
                /**      
                this.prepAddReservation.setString(1, nextID);
                this.prepAddReservation.setString(2, reservation.getTargetPost().getPostID());
                this.prepAddReservation.setString(3, reservation.getReservingPerson().getPersonID());
                this.prepAddReservation.SetString(4, reservation.getWeeklyHours());
                this.prepAddReservation.setString(5, reservation.getDescription());
                this.prepAddReservation.setString(6, reservation.getTimeSlice().getSQLStartDate());
                this.prepAddReservation.setString(7, reservation.getTimeSlice().getSQLEndDate());

                if(this.prepAddReservation.executeUpdate() > 0) {
                        success = true;
                }
                * */
            } catch (SQLException e) {
			System.err.println("Tietokantavirhe: " + e.getMessage());
            }
        }

	/**
	 * Tarkista kuuluuko henkilï¿½ hakuehtojen mukaiseen listaan. Tarkistus tehdï¿½ï¿½n, koska
	 * vastaavan seikan tarkistaminen SQL-kannassa on hyvin vaikeaa ja hidasta.
	 * @param person henkilï¿½
	 * @param timescale tarkasteltava aikavï¿½li
	 * @param contractEndDate henkilï¿½
	 * @param showEndingContracts halutaanko pï¿½ï¿½ttyvï¿½t tyï¿½sopimukset
	 * @param withoutPost halutaanko tyï¿½pisteettï¿½mï¿½t
	 * @return Sopivatko annetut hakuehdot henkilï¿½ï¿½n
	 */
	private boolean filterPerson(Person person, TimeSlice timescale, java.sql.Date contractEndDate, boolean showEndingContracts, boolean withoutPost) {
		// jos ei pyydetty tyï¿½pisteettï¿½miï¿½, ei filtterï¿½intiï¿½ tarvita 
		if(!withoutPost)
			return true;
		// jos pyydettiin pï¿½ï¿½ttyviï¿½ sopimuksia, tarkistetaan ensin ne
		if(showEndingContracts && timescale.contains(contractEndDate))
			return true;
		// muussa tapauksessa tï¿½ytyy tutkia tarkemmin henkilï¿½n varaukset
		return person.getStatus();
	}
        
        /** Nerompi
         * Lisää tietokannan henkilo-taulun huone_nro-kenttään annettu arvo.
         * @param person Henkilö, jolle huone lisätään
         * @param room Lisättävän huoneen nimi
         * @return Onnistuiko lisääminen
         */
        public boolean addRoomToPerson(Person person, String room) {
            if(person.getRoom()!=null) {
                this.removeRoomFromPerson(person);
            }
            String sqlQuery = "update henkilo"
                        + " set HUONE_NRO=?"
                        + " where HTUNNUS=?";
            try {
                PreparedStatement prep = this.connection.prepareStatement(sqlQuery);
                prep.setString(1, room);
                prep.setString(2, person.getPersonID());
                prep.executeQuery();
                person.setRoom(room);
                return true;
            } catch(SQLException e) {
                System.err.println("Tietokantavirhe: " + e.getMessage());
                return false;
            }
        }
        /** Nerompi
         * Korvaa tietokannan henkilo-taulun huone_nro-kentän arvo null-arvolla.
         * @param person Henkilö, jolta huone poistetaan.
         * @return Onnistuiko poistaminen.
         */
        
        public boolean removeRoomFromPerson(Person person) {
            if(person.getRoom()==null) {
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
            } catch(SQLException e) {
                System.err.println("Tietokantavirhe: " + e.getMessage());
                return false;
            }
        }
        
        /** Nerompi
         * Hakee tietokannasta umpeutuneet huonevaraukset ja poistaa huoneen näiden varausten henkilöiltä.
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
                while(selectPersonResult.next()) { // Poistaa huoneen niiltä, joiden varaus on mennyt umpeen
                    prep = this.connection.prepareStatement(removeRoomQuery);
                    prep.setString(1, selectPersonResult.getString("henklo_htunnus"));
                    prep.executeQuery();
                }
                
                ResultSet selectPersonResult2 = this.connection.prepareStatement(selectPersonQuery2).executeQuery();
                
                while(selectPersonResult2.next()) { // Päivittää työhuonenumeron niille, joiden varaus on alkanut, mutta ei vielä loppnut
                    prep2 = this.connection.prepareStatement(selectRoomQuery);
                    prep2.setString(1, selectPersonResult2.getString("henklo_htunnus"));
                    ResultSet selectRoomResult = prep2.executeQuery();
                    selectRoomResult.next();
                    
                    prep = this.connection.prepareStatement(setRoomQuery);
                    prep.setString(1, selectRoomResult.getString("huone_nro"));
                    prep.setString(2, selectPersonResult2.getString("henklo_htunnus"));
                    prep.executeQuery();
                }
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
	 * Lisï¿½ï¿½ tyï¿½pisteen huoneeseen
	 * 
	 * @param post Tyï¿½piste joka lisï¿½tï¿½ï¿½n.
	 * @return Onnistuiko lisï¿½ys
	 */
	public boolean createPost(Post post) {
		boolean success = false;
		this.session.waitState(true);
		try {
			/* seuraava vapaa ID _tï¿½ytyy_ hakea ensin */
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

	/* --- Tyï¿½pisteisiin liittyvï¿½t metodit loppuu --- */ 

	/* --- Tyï¿½huoneisiin liittyvï¿½t metodit alkaa --- */ 

	/**
	 * Palauttaa kaikki jï¿½rjestelmï¿½n tuntemat huoneet.
	 * 
	 * @return huoneet Room[]-oliona
	 */
	public Room[] getRooms() {
		return (Room[]) rooms.values().toArray(new Room[0]);
	}

	/**
	 * Hakee tietyn projektin henkilï¿½ille varatut huoneet tietyllï¿½ aikavï¿½lillï¿½.
	 * Jos projekti on null, palautetaan tyhjï¿½ huonetaulukko.
	 * 
	 * @param project Projekti, jonka tyï¿½huoneita haetaan. Jos null,
	 * palautetaan tyhjï¿½ lista.
	 * @param timescale Aikavï¿½li, jota varauksien tulee leikata.
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
					/* sekï¿½ tyï¿½pistevarauksen ettï¿½ tyï¿½sopimusjakson pï¿½ivï¿½mï¿½ï¿½rï¿½t
					 * pitï¿½ï¿½ olla oikein */
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
	 * Palauttaa ne huoneet, jotka tï¿½yttï¿½vï¿½t annetut hakuehdot.
	 * 
	 * @param roomFilter
	 *            String jota etsitï¿½ï¿½n huoneen nimestï¿½ ja numerosta.           
	 * @param maxPosts
	 *            Huoneen tyï¿½pisteiden maksimilukumï¿½ï¿½rï¿½. Jos pienempi kuin 0, ei rajaa tulosta.
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
	 * @return huoneolio tai null, jos kyseistï¿½ huonetta ei lï¿½ydy
	 */
	public Room getRoom(String roomID) {
		return (Room) rooms.get(roomID);
	}

	/* --- Huoneisiin liittyvï¿½t metodit loppuu --- */ 

	/* --- Puhelinnumeroihin liittyvï¿½t metodit alkaa --- */ 

	/**
	 * Palauttaa kaikki jï¿½rjestelmï¿½n tuntemat puhelinnumerot jï¿½rjestettynï¿½
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
	 * Pï¿½ivittï¿½ï¿½ tietokannassa olevan puhelinnumero-olion annetun mallin
	 * mukaiseksi.
	 * 
	 * @param phone Uusi versio puhelinnumerosta (uusi tyï¿½piste id).
	 * @return Onnistuiko pï¿½ivitys.
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
	 * Palauttaa annetun tyï¿½huoneen puhelinnumerot.
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
		if(post != null) {
			key = post.getPostID();
		} 
		Collection c = (Collection)this.phoneNumbers.get(key);
                if (c == null) {
                    return new PhoneNumber[0];
                }
                //yrittää tehdä null collectionista arrayn
		PhoneNumber[] numbers = (PhoneNumber[])c.toArray(new PhoneNumber[0]);
		Arrays.sort(numbers);
		return numbers;
	}
	
	/* --- Puhelinnumeroihin liittyvï¿½t metodit loppuu --- */ 

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
		System.out.println(ndb.connection.getMetaData()
				.getDatabaseProductVersion());
		// testailusï¿½lï¿½ poistettu, riippuvaista kannan vanhasta sisï¿½llï¿½stï¿½.
		System.out.println("done.");
	}

	/* --- Muut metodit loppuu --- */

}