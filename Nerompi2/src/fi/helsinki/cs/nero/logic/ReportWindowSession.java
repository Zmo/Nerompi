package fi.helsinki.cs.nero.logic;

import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.data.Room;
import fi.helsinki.cs.nero.data.RoomKeyReservation;
import fi.helsinki.cs.nero.ui.ReportsWindow;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

/**
 * Session ja ReportsWindown'n v�liss� toimiva luokka, joka tarjoaa
 * ReportsWindow'lle datan sellaisessa muodossa, joka sopii sille, 
 * toteuttaa rajapinnan ReportSession.
 * Ideana l�hinn� se, ettei GUIssa tarvitse olla metodeita, jotka ovat 
 * oleellisesti vain erin�isten taulukoiden iterointia ja datan muokkaamista
 * sopivaan esitysmuotoon.
 * 
 * 
 * @author lpesola
 */
public class ReportWindowSession implements ReportSession {

    private Session session;
    private Person[] people;
    private Room[] rooms;
    /**
     * true jos n�ytet��n vain aktiiviset
     */
    private Boolean showOnlyActiveEmployees;


    public ReportWindowSession(Session s) {
        session = s;
        people = session.getFilteredPeople();
        rooms = session.getRooms();
        showOnlyActiveEmployees = session.getFilterActiveEmployees();
        ReportsWindow reports = new ReportsWindow(this);
    }

    /**
     * Tuottaa vektorin, joka sis�lt�� kaikki ohjelman tuntemat henkil�t
     * ja n�ihin liittyv�n oleellisen datan.
     * 
     * @return vektori, joka sis�lt�� taulukon rivit vektoreina
     */
    @Override
    public Vector<Vector<Object>> getRoomData() {
        Vector roomTableData = new Vector<>();
        for (Room room : rooms) {
            Vector<Object> row = new Vector<>();
            row.add(room.getRoomName());
            row.add(room.getFloor());
            row.add(room.getWing());
            row.add(room.getPosts().length);
            row.add(room.getRoomSize());
            row.add(room.getDescription());
            // avainvaraukset
            row.add(roomKeyReservationsToString(room));
            // ty�pistevaraukset
            row.add(reservationsToString(room));
            roomTableData.add(row);
        }
        return roomTableData;
    }
    
     /**
     * Tuottaa vektorin, joka sis�lt�� kaikki ohjelman tuntemat huoneet
     * ja n�ihin liittyv�n oleellisen datan.
     * 
     * @return vektori, joka sis�lt�� taulukon rivit vektoreina
     */

    @Override
    public Vector<Vector<Object>> getPeopleData() {
        Vector peopleTableData = new Vector<>();

        for (Person person : people) {
            Vector<Object> row = new Vector<>();

            row.add(person.getName());
            row.add(person.getTitteli());
            String roomName = person.getRoom();
            Reservation reservation = person.getReservationForRoom(roomName);
            // jos varausta ei ole, n�ytet��n vain huonenumero
            // muuten n�ytet��n my�s ty�pisteen numero
            if (reservation == null) {
                row.add(roomName);
            } else {
                row.add(reservation.getTargetPost().toString());
            }
            // lis�t��n tiedot henkil�n t�m�nhetkisest� huoneesta: siipi, kerros
            try {
                Room room = reservation.getTargetPost().getRoom();
                row.add(room.getFloor().toString());
                row.add(room.getWing());
                // huoneessa olevien ty�pisteiden lukum��r�
                row.add(new Integer(room.getPosts().length).toString());
            } catch (NullPointerException ex) {
                row.add(null);
                row.add(null);
                row.add(null);
            }
            // n�ytet��n t�m�nhetkisen huoneen varauksen p��ttymisp�iv�,
            // jos henkil�ll� on jokin voimassaoleva varaus t�h�n huoneeseen
            if (reservation == null) {
                row.add(null);
            } else {
                row.add(reservation.getLastDay());
            }
            row.add(person.getSahkoposti());
            row.add(person.getWorkPhone());

            if (person.getPostilokeroHuone() == null) {
                row.add("ei postilokeroa");
            } else {
                row.add(person.getPostilokeroHuone());
            }
            peopleTableData.add(row);
        }
        return peopleTableData;
    }

    /**
     * Hakee Sessiolta henkil�datan uudestaan sen mukaan, pit��k� mukana olla
     * ep�aktiiviset henkil�t vai ei.
     *
     * @param b
     */
    @Override
    public void setFilterActiveEmployees(boolean b) {
        session.setFilterActiveEmployees(b);
        showOnlyActiveEmployees = b;
        people = session.getFilteredPeople();
    }

    @Override
    public Boolean getShowOnlyActiveEmployees() {
        return showOnlyActiveEmployees;
    }
    /**
     * Muotoilee huoneen kaikki avainvaraukset yhdeksi Stringiksi.
     * 
     * @param room huone, jonka avainvarauksia k�sitell��n
     * @return avainvaraukset listattuna yhdess� Stringiss�
     */
    private String roomKeyReservationsToString(Room room) {
        ArrayList<RoomKeyReservation> reservations = room.getRoomKeyReservations();
        if (reservations == null || reservations.isEmpty()) {
            return "";
        } else {
            String str = "";
            for (Iterator<RoomKeyReservation> it = reservations.iterator(); it.hasNext();) {
                RoomKeyReservation r = it.next();
                String reserver = r.getReserverName();
                if (reserver != null) {
                    if (!it.hasNext()) {
                        str = str.concat(reserver);
                    } else {
                        str = str.concat(reserver + ", ");
                    }
                }
            }
            return str;
        }
    }

    /**
     * Muotoilee huoneen kaikki ty�pistevaraukset yhdeksi Stringiksi.
     * @param room huone, jonka varauksia k�sitell��n
     * @return ty�pistevaraukset listattuna yhdess� Stringiss�
     */
    private String reservationsToString(Room room) {
        Post[] posts = room.getPosts();
        String str = "";
        if (posts != null) {
            for (Post post : posts) {
                Reservation[] reservations = post.getReservations();
                if (reservations != null && reservations.length > 0) {
                    str = str.concat(post.getPostNumber() + ": ");
                    int lastIndex = reservations.length -1;
                    for (int j = 0; j < lastIndex-1; j++) {
                        Reservation reservation = reservations[j];
                        str = str.concat(reservation.getReservingPerson().toString() + ", ");
                    }
                    str = str + reservations[lastIndex].getReservingPerson().toString()+" ";
                }
            }
        }
        return str;
    }
    
}
