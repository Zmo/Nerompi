package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.data.Room;
import fi.helsinki.cs.nero.event.NeroObserver;
import fi.helsinki.cs.nero.event.NeroObserverTypes;
import fi.helsinki.cs.nero.logic.Session;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.apache.batik.bridge.UpdateManager;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.svg.SVGLoadEventDispatcherAdapter;
import org.apache.batik.swing.svg.SVGLoadEventDispatcherEvent;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGLocatable;
import org.w3c.dom.svg.SVGRect;

/**
 * K‰yttˆliittym‰n karttakomponentti. Toteutukseen k‰ytet‰‰n Batikin
 * JSVGCanvas-k‰yttˆliittym‰komponenttia.
 * 
 * Toteutusta hankaloittaa se, ett‰ Batikin tietorakenteiden k‰sittely
 * t‰ytyy tehd‰ Batikin omasta s‰ikeest‰ k‰sin, kun taas muu sovellus
 * pyˆrii l‰hinn‰ Swingin tapahtumak‰sittelij‰s‰ikeess‰. T‰ytyy siis
 * tarvittaessa vaihtaa s‰ikeest‰ toiseen.
 * 
 * SVG-dokumentissa on huone-elementtej‰, joiden tila esitet‰‰n graafisesti.
 * Huoneille asetetaan CSS-luokka (attribuutti class), jonka j‰lkeen ulkoasu
 * m‰‰ritell‰‰n erillisessa CSS-tyylitiedostossa. Luokat ovat seuraavat:
 *  room        t‰m‰ on aina kaikilla huoneilla
 *  project     valitun projektin huone
 *  active      valittu huone
 *  filtered    hakuehtojen mukainen
 *  occupied    varattu
 *  partfree    osittain vapaa
 *  free        vapaa
 *  noposts     ei sis‰ll‰ yht‰‰n tyˆpistett‰
 * @author Osma Suominen
 */

public class Map extends JSVGCanvas implements NeroObserver {
	
	/**
	 * Tiedosto, joka sis‰lt‰‰ karttapohjan SVG-muodossa.
	 */
	private static final String SVGFILE = "data/kartta.svg";
	
	/**
	 * Kartan kerrosten tunnukset. Merkkijonon jokainen merkki vastaa
     * yht‰ kerrosta.
	 */
	private static final String FLOORS = "1234";
	
	/**
	 * Kartta-SVG:n nappien ryhm‰n id.
	 */
	private static final String BUTTONS_ID = "buttons";
	
	/**
	 * Kartta-SVG:n kerrosnappien id:iden alkuosa. Koko id on 
	 * BUTTON_PREFIX + FLOORS[i]
	 */
	private static final String BUTTON_PREFIX = "button";
	
	/**
	 * Kartta-SVG:n kerrosnappien class-attribuutti.
	 */
	private static final String BUTTON_CLASS = "button";
	
	/**
	 * Kartta-SVG:n kerrosten id:iden alkuosa. Koko id on 
	 * FLOOR_PREFIX + FLOORS[i]
	 */
	private static final String FLOOR_PREFIX = "floor";
	
	/**
	 * Kartta-SVG:n kerrosten ryhm‰n id.
	 */
	private static final String FLOORS_ID = "floors";
	
    /**
     * Huoneen nimitekstin vasemman reunan et‰isyys huoneen vasemmasta reunasta.
     */
    private static final int ROOMLABEL_OFFSET_X = 2;

    /**
     * Huoneen nimitekstin alareunan et‰isyys huoneen yl‰reunasta.
     */
    private static final int ROOMLABEL_OFFSET_Y = 12;
    
	/**
	 * Ladattu kartta SVG-dokumenttina.
	 */
	private SVGDocument document;
	
	/**
	 * Sovelluslogiikan sessio.
	 */
	private Session session;
	
	/**
	 * Hajautusrakenne, joka tallettaa kaikki j‰rjestelm‰n tuntemat huoneet.
	 * Avaimena on huoneen numero (esim. "C132"), arvona huoneolio (Room).
	 */
	private java.util.Map roomsByNumber = null;
	
	/**
     * SVG:n XML-nimiavaruuden m‰‰ritt‰v‰ URI. K‰ytet‰‰n lyhennysmerkint‰n‰.
	 */
    private String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
    
    /**
     * Valittua huonetta vastaava SVG-dokumentin elementti. Null, jos
     * valittua huonetta ei ole.
     */
    private Element activeRoom = null;

    /**
     * Valitun kerroksen id. Jokin FLOORS-merkkijonon merkeist‰.
     */
    private char activeFloor;
    
    /**
     * Projektin huone-elementtej‰ s‰ilytt‰v‰ kokoelma. Kokoelman elementit
     * ovat SVG-dokumentin elementtej‰ (Element-olioita).
     */
    private Collection projectRooms = new LinkedList();
    
	/**
	 * Konstruktori, joka luo karttakomponentin.
	 * @param session sovelluslogiikan sessio
	 * @param frame Swingin p‰‰ikkuna
	 */
	public Map(Session session, JFrame frame) {
		super();
		this.session = session;
		roomsByNumber = new Hashtable();
		setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
		addSVGLoadEventDispatcherListener(new SVGLoadListener());
		setEnableRotateInteractor(false);
		frame.addWindowListener(new WindowOpenListener());
		session.registerObserver(NeroObserverTypes.ROOMS, this);
		session.registerObserver(NeroObserverTypes.TIMESCALESLICE, this);
		session.registerObserver(NeroObserverTypes.RESERVATIONS, this);
		session.registerObserver(NeroObserverTypes.ACTIVE_ROOM, this);
		session.registerObserver(NeroObserverTypes.FILTER_PROJECT, this);
		session.registerObserver(NeroObserverTypes.FILTER_ROOMS, this);
	}
	
	/* (non-Javadoc)
	 * @see fi.helsinki.cs.nero.event.NeroObserver#updateObserved(int)
	 */
	/* Synkronoitu metodi, koska sit‰ saatetaan kutsua joko Swingin tai Batikin s‰ikeest‰ */
	public synchronized void updateObserved(int type) {
        // Haetaan sessiolta tarvittavat tiedot niin kauan, kuin ollaan
        // oikeassa s‰ikeess‰. 
        
        Room[] filteredRooms = null;
        int[] roomStatuses = null;
        Room[] projectRooms = null;
        Room activeRoom = null;
        
        // tarkistetaan kuinka laaja p‰ivitys tarvitsee tehd‰
        switch(type) {
		case NeroObserverTypes.ROOMS:          // t‰ytyy p‰ivitt‰‰ kaikki
			Room[] rooms = session.getRooms();
		    for(int i=0; i < rooms.length; ++i)
		    	roomsByNumber.put(rooms[i].getRoomNumber(), rooms[i]);
            // fall-through
		case NeroObserverTypes.RESERVATIONS:   // t‰ytyy p‰ivitt‰‰ varaustiedot     
		case NeroObserverTypes.FILTER_ROOMS:
		case NeroObserverTypes.TIMESCALESLICE:
			filteredRooms = session.getFilteredRooms();
		    // Huoneiden tilakin haetaan valmiiksi int-taulukkoon,
		    // koska sen hakeminen voi aiheuttaa tietokantaoperaatioita, ja ne on
		    // syyt‰ tehd‰ Swing-s‰ikeess‰ eik‰ Batik-s‰ikeess‰.
		    roomStatuses = new int[filteredRooms.length];
		    for(int i=0; i<roomStatuses.length; ++i)
		    	roomStatuses[i] = filteredRooms[i].getStatus();
            // fall-through
		case NeroObserverTypes.FILTER_PROJECT: // t‰ytyy p‰ivitt‰‰ pari huonetta
		case NeroObserverTypes.ACTIVE_ROOM:
                    projectRooms = session.getProjectRooms();
		    activeRoom = session.getActiveRoom();
		    break;
        default:
            assert(false);
		}
        
        // t‰ytyy kopioida tiedot final-muuttujiin jotta niit‰ voidaan k‰sitell‰
        // toisessa luokassa ja s‰ikeess‰
        
        final Room[] finalFilteredRooms = filteredRooms;
        final int[] finalRoomStatuses = roomStatuses;
        final Room[] finalProjectRooms = projectRooms;
        final Room finalActiveRoom = activeRoom;
        // jos tapahtuma oli aktiivisen huoneen vaihto, vaihdetaan myˆs kerros tarvittaessa
        final boolean changeFloor = (type == NeroObserverTypes.ACTIVE_ROOM);
        
        // Kartan p‰ivitys tehd‰‰n Batikin tapahtumak‰sittelij‰s‰ikeess‰.
        UpdateManager updman = getUpdateManager();
        if(updman == null) {
        	// Karttaa ei ole viel‰ ladattu, joten sen p‰ivitt‰minen ei onnistu.
            return;
        }
		updman.getUpdateRunnableQueue().invokeLater(
				new Runnable() {
					public void run() {
                        if(finalFilteredRooms != null) {
                        	clearRoomStatus();
                        	updateFilteredRooms(finalFilteredRooms, finalRoomStatuses);
                        }
						updateProjectRooms(finalProjectRooms);
						updateActiveRoom(finalActiveRoom, changeFloor);
					}});
	}
	
	/**
	 * Apumetodi SVG-dokumentin elementin class-attribuutin p‰ivitt‰miseksi.
	 * Annettu arvo lis‰t‰‰n class-attribuutin listaan luokista. Metodia on
	 * kutsuttava Batikin tapahtumak‰sittelij‰s‰ikeest‰. Jos class-attribuutti
     * jo sis‰lt‰‰ annetun luokan, ei tehd‰ mit‰‰n.
	 * @param el elementti jonka class-attribuuttia muokataan
	 * @param newclass lis‰tt‰v‰ class-arvo
	 */
	private void addClass(Element el, String newclass) {
		String oldclass = el.getAttribute("class");
		if(oldclass.equals(""))
			el.setAttribute("class", newclass);
		else if(oldclass.indexOf(newclass) == -1)
			el.setAttribute("class", oldclass + " " + newclass);
	}

    /**
     * Apumetodi SVG-dokumentin elementin class-attribuutin p‰ivitt‰miseksi.
     * Annettu arvo posistetaan class-attribuutin luokista. Metodia on
     * kutsuttava Batikin tapahtumak‰sittelij‰s‰ikeest‰. Jos class-attribuutti
     * ei sis‰ll‰ annettua luokkaa, ei tehd‰ mit‰‰n.
     * @param el elementti jonka class-attribuuttia muokataan
     * @param newclass poistettava class-arvo
     */
    private void removeClass(Element el, String cl) {
        String oldclass = el.getAttribute("class");
        el.setAttribute("class", oldclass.replaceFirst(" ?" + cl, ""));
    }
    
	/**
	 * Palauttaa kaikkien kartalla n‰kyvien huoneiden tilan alkutilaan eli
	 * poistaa kaikki korostukset. Metodia on kutsuttava Batikin
	 * tapahtumak‰sittelij‰s‰ikeest‰.
	 */
	private void clearRoomStatus() {
		Iterator it =  roomsByNumber.values().iterator();
		while(it.hasNext()) {
			Room room = (Room) it.next();
			Element el = document.getElementById(room.getRoomNumber());
			if(el == null) {
				System.err.println("Huonetta " + room.getRoomNumber() + " ei lˆydy kartalta.");
				continue;
			}
			el.setAttribute("class", "room");
		}
	}
	
	/**
	 * Merkkaa aktiivisen huoneen kartalle. Metodia on kutsuttava Batikin
	 * tapahtumak‰sittelij‰s‰ikeest‰.
	 * @param room uusi aktiivinen huone
	 * @param changeFloor vaihdetaanko myˆs kerrosta
	 */
	private void updateActiveRoom(Room room, boolean changeFloor) {
        if(room == null) return;
        // poistetaan vanha aktiivinen huone, jos sellainen on
        if(activeRoom != null)
            removeClass(activeRoom, "active");
		Element el = document.getElementById(room.getRoomNumber());
		assert(el != null);
		addClass(el, "active");
        activeRoom = el;
        // vaihda kerrosta niin, ett‰ aktiivinen huone on n‰kyvill‰
        if(changeFloor && room.getFloor().charAt(0) != activeFloor)
        	setActiveFloor(room.getFloor().charAt(0));
 	}
	
	/**
	 * Merkkaa projektin huoneet kartalle. Metodia on kutsuttava Batikin
	 * tapahtumak‰sittelij‰s‰ikeest‰.
	 */
	private void updateProjectRooms(Room[] rooms) {
        // poistetaan merkkaus entisist‰ projektihuoneista
		Iterator iter = projectRooms.iterator();
        while(iter.hasNext()) {
        	Element el = (Element)iter.next();
            removeClass(el, "project");
        }
        projectRooms.clear();

        // lis‰t‰‰n merkkaus uusiin projektihuoneisiin sek‰ niiden kerroksiin
        String floorsWithProjects = ""; // projektihuoneita sis‰lt‰vien kerrosten tunnukset
        for(int i=0; i<rooms.length; ++i) {
			Element el = document.getElementById(rooms[i].getRoomNumber());
			assert(el != null);
			addClass(el, "project");
            projectRooms.add(el);

            // lis‰t‰‰n tarvittaessa huoneen kerros listaan merkattavista kerroksista
            if(floorsWithProjects.indexOf(rooms[i].getFloor()) == -1)
            	floorsWithProjects += rooms[i].getFloor();
		}
        // merkataan kerrokset joissa oli projektin huoneita
        for(int i=0; i<floorsWithProjects.length(); ++i) {
        	Element el = document.getElementById(BUTTON_PREFIX + floorsWithProjects.charAt(i));
        	addClass(el, "project");
        }
	}
	
	/**
	 * Merkkaa hakuehtojen mukaiset huoneet kartalle. Metodia on kutsuttava 
	 * Batikin tapahtumak‰sittelij‰s‰ikeest‰.
	 */
	private void updateFilteredRooms(Room[] rooms, int[] roomStatuses) {
            // kerrosten tila: maksimiarvo huoneiden vapaustiloista
            // Room.NO_POSTS on oletustila, jota ei merkit‰
            int[] floorStatus = new int[FLOORS.length()];
            for(int i=0; i<floorStatus.length; ++i)
                floorStatus[i] = Room.NO_POSTS;

            for(int i=0; i<rooms.length; ++i) {
            	Element el = document.getElementById(rooms[i].getRoomNumber());
		assert(el != null);
                String cl = "";
		switch(roomStatuses[i]) {
                    case Room.NO_POSTS:
                        cl = "noposts"; break;
                    case Post.OCCUPIED:
                        cl = "occupied"; break;
                    case Post.PARTLY_FREE:
                        cl = "partfree"; break;
                    case Post.FREE:
                        cl = "free"; break;
                    default:
			assert(false);
            }
                el.setAttribute("class", "room filtered " + cl);
            
                // pidet‰‰n yll‰ kerroskohtaista maksimiarvoa statuksesta
                int floorIdx = FLOORS.indexOf(rooms[i].getFloor());
                if(floorStatus[floorIdx] < roomStatuses[i])
                floorStatus[floorIdx] = roomStatuses[i];
            }
        
            // merkkaa kerrosnappulat
            for(int i=0; i<floorStatus.length; ++i) {
                String cl = "";
                switch(floorStatus[i]) {
                    case Room.NO_POSTS:
                        break;
                    case Post.OCCUPIED:
                        cl = "occupied"; break;
                    case Post.PARTLY_FREE:
                        cl = "partfree"; break;
                    case Post.FREE:
                        cl = "free"; break;
                    default:
                        assert(false);
                }
            
                if(FLOORS.charAt(i) == activeFloor)
                    if(cl.equals(""))
            		cl = "active";
                    else
            		cl = cl + " " + "active";

                Element btn = document.getElementById(BUTTON_PREFIX + FLOORS.charAt(i));
                btn.setAttribute("class", BUTTON_CLASS + " " + cl); // BUTTON_CLASS == "button"
            }
        }
	
	/**
	 * Asettaa aktiivisen kerroksen. Piilottaa muut kerrokset n‰kyvist‰.
	 * Metodia on kutsuttava Batikin tapahtumakuuntelijas‰ikeest‰.
	 * @param floorID kerroksen id 1-merkkisen‰ merkkijonona
	 * @note Kerroksen vaihto kartalla syˆ runsaasti muistia! T‰m‰ saattaa olla Batikin bugi.
	 */
	private void setActiveFloor(char floorID) {
		Element newfloor = document.getElementById(FLOOR_PREFIX + floorID);
		if(newfloor == null)
			throw new IllegalArgumentException("nonexistant floor id: " + floorID);

		// XXX T‰ss‰ kohtaa Batik tuntuu vuotavan muistia. Onneksi kerrosta ei vaihdeta
		// kovin usein.
		Element oldfloor = document.getElementById(FLOOR_PREFIX + activeFloor);
		if(oldfloor != null)
			oldfloor.setAttribute("display", "none");
		
		newfloor.removeAttribute("display");
		
		// p‰ivit‰ aktiivisen kerroksen osoittava nappula
		Element oldButton = document.getElementById(BUTTON_PREFIX + activeFloor);
		if(oldButton != null)
			removeClass(oldButton, "active");
		Element newButton = document.getElementById(BUTTON_PREFIX + floorID);
		addClass(newButton, "active");

		activeFloor = floorID;
	}
	
	/**
	 * Asettaa aktiivisen huoneen sovelluslogiikalle. Kutsu sovelluslogiikkaan
	 * tehd‰‰n Swingin tapahtumank‰sittelij‰s‰ikeess‰.
	 * @param room huone
	 */
	private void setActiveRoom(Room room) {
		if(room == null)
			throw new IllegalArgumentException("no room given");
		final Room activeRoom = room;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				session.setActiveRoom(activeRoom);
			}
		});
	}

    /**
     * Piirt‰‰ kaikkien huoneiden numerot kartalle. Piirt‰minen tapahtuu
     * etsim‰ll‰ huonetta vastaava elementti kartalta ja lis‰‰m‰ll‰
     * sen kohdalle tekstielementti.
     */
    private void showRoomNumbers() {
        // Ladataan huonetiedot
        updateObserved(NeroObserverTypes.ROOMS);

        // Merkit‰‰n tietokannasta lˆytyneiden huoneiden nimet kartalle.
        Iterator it = roomsByNumber.keySet().iterator();
        while(it.hasNext()) {
            String roomNumber = (String) it.next();
            Element el = document.getElementById(roomNumber);
            if(el == null) {
            	System.err.println("Huonetta " + roomNumber + " ei lˆydy kartalta.");
                continue;
            }

            // lasketaan koordinaatit ja etsit‰‰n vasen yl‰kulma + offset
            SVGRect rect = ((SVGLocatable)el).getBBox();
            float x = rect.getX() + ROOMLABEL_OFFSET_X;
            float y = rect.getY() + ROOMLABEL_OFFSET_Y;
            
            // luodaan tekstielementti ja lis‰t‰‰n kerrosryhm‰‰n
            Element label = document.createElementNS(svgNS, "text");
            label.appendChild(document.createTextNode(roomNumber));
            label.setAttributeNS(null, "x", ""+x);
            label.setAttributeNS(null, "y", ""+y);
            label.setAttributeNS(null, "class", "label");
            label.setAttributeNS(null, "pointer-events", "none");
            el.getParentNode().appendChild(label);
        }
        
    }
    
	/**
	 * Kuuntelija, jonka koodi suoritetaan, kun Swingin p‰‰ikkuna (JFrame) on
	 * avattu. Batik haluaa ladata SVG-dokumentin vasta kun ikkuna on jo auki.
	 * @author Osma Suominen
	 */
	private class WindowOpenListener extends WindowAdapter {
		public void windowOpened(WindowEvent e) {
			String uri = new File(SVGFILE).toURI().toString();
			setURI(uri);
		}
	}
	
	
	/**
	 * Kuuntelija, jonka koodi suoritetaan, kun SVG-dokumentin lataus on valmis.
	 * Asettaa tapahtumakuuntelijoita dokumentille.
	 * @author Osma Suominen
	 */
	
	private class SVGLoadListener extends SVGLoadEventDispatcherAdapter {
		
		public void svgLoadEventDispatchStarted (SVGLoadEventDispatcherEvent e) {
			document = getSVGDocument();
			
			// Aseta napit-ryhm‰lle tapahtumakuuntelijan, jolla vaihdetaan
			// kerrosta. K‰ytt‰‰ event capturea, jolloin yksi kuuntelija riitt‰‰.
			// Ei vaikuta text-elementteihin koska niill‰ on attribuutti
			// pointer-events="none"
			EventTarget buttons = (EventTarget) document.getElementById(BUTTONS_ID);
			buttons.addEventListener("click", new FloorClickHandler(), true);
			
			// Aseta kerrokset-ryhm‰lle tapahtumakuuntelija, joka k‰ytt‰‰ event
			// capturea eli kuulee my‰s lapsielementtien tapahtumat.
			EventTarget floors = (EventTarget) document.getElementById(FLOORS_ID);
			floors.addEventListener("click", new RoomClickHandler(), true);
            
            // piirret‰‰n huoneiden numerot kartalle
            showRoomNumbers();
			
			// piilottaa muut kuin ensimm‰isen kerroksen (huom. l‰htee i=1:st‰)
            for(int i=1; i<FLOORS.length(); ++i) {
            	Element floor = document.getElementById(FLOOR_PREFIX + FLOORS.charAt(i));
            	floor.setAttribute("display", "none");
            }
            // asettaa ensimm‰isen kerroksen aktiiviseksi
            setActiveFloor(FLOORS.charAt(0));
		}
	}
	
	/**
	 * Kuuntelija, jonka koodi suoritetaan kartan elementtej‰ klikattaessa.
	 * Asettaa sovelluslogiikalle aktiivisen huoneen, jos on klikattu
	 * huone-elementti‰, muutoin ei tee mit‰‰n.
	 * @author Osma Suominen
	 */
	private class RoomClickHandler implements EventListener {
		public void handleEvent(Event evt) {
			Element el = (Element) evt.getTarget();
			String id = el.getAttribute("id");
			if(id.equals("")) return;
            Room room = (Room) roomsByNumber.get(id);
			if(room == null) {
				// Klikattu huonetta, jota ei lˆydy tietokannasta.
				session.setStatusMessage("Huonetta " + id + " ei lˆydy tietokannasta.");
				return;
			}
			setActiveRoom(room);
		}
	}
	
	/**
	 * Kuuntelija, jonka koodi suoritetaan kerroksenvaihtonappeja klikattaessa.
	 * Vaihtaa kartalla n‰kyv‰n kerroksen.
	 * @author Osma Suominen
	 */
	
	private class FloorClickHandler implements EventListener {
		public void handleEvent(Event evt) {
			Element el = (Element) evt.getTarget();
			String id = el.getAttribute("id");
			setActiveFloor(id.replaceFirst(BUTTON_PREFIX, "").charAt(0));
		}
	}
}
