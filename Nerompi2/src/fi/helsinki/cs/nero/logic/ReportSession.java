package fi.helsinki.cs.nero.logic;

import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.data.Room;
import fi.helsinki.cs.nero.data.RoomKeyReservation;
import fi.helsinki.cs.nero.db.NeroDatabase;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author lpesola
 */
public class ReportSession {

    private Session session;
    private Person[] people;
    private Room[] rooms;

    public ReportSession() {
        session = new Session();
        NeroDatabase db = new NeroDatabase(session,
                "oracle.jdbc.driver.OracleDriver",
                "jdbc:oracle:thin:@bodbacka:1521:test",
                "tk_testi", "tapaus2");
        session.setDatabase(db);

        people = session.getFilteredPeople();
        rooms = session.getRooms();
    }

    public Vector<Vector<Object>> getRoomData() {
        Vector roomTableData = new Vector<>();
        for (Room room: rooms) {
            Vector<Object> row = new Vector<>();
            row.add(room.getRoomName());
            row.add(room.getFloor());
            row.add(room.getWing());
            row.add(room.getPosts().length);
            row.add(room.getRoomSize());
            row.add(room.getDescription());
            // avainvaraukset
            row.add(roomKeyReservationsToString(room));
            // työpistevaraukset
            row.add(reservationsToString(room));
            roomTableData.add(row);
        }
        return roomTableData;
    }

    public Vector<Vector<Object>> getPeopleData() {
        Vector peopleTableData = new Vector<>();

        for (Person person: people) {
            Vector<Object> row = new Vector<>();

            row.add(person.getName());
            row.add(person.getTitteli());
            String roomName = person.getRoom();
            Reservation reservation = person.getReservationForRoom(roomName);
            // jos varausta ei ole, näytetään vain huonenumero
            // muuten näytetään myös työpisteen numero
            if (reservation == null) {
                row.add(roomName);
            } else {
                row.add(reservation.getTargetPost().toString());
            }
            // lisätään tiedot henkilön tämänhetkisestä huoneesta
            try {
                Room room = reservation.getTargetPost().getRoom();
                row.add(room.getFloor().toString());
                row.add(room.getWing());
                row.add(new Integer(room.getPosts().length).toString());
            } catch (NullPointerException ex) {
                row.add(null);
                row.add(null);
                row.add(null);
            }
            // näytetään tämänhetkisen huoneen varauksen päättymispäivä,
            // jos henkilöllä on jokin voimassaoleva varaus tähän huoneeseen
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
     * Hakee Sessiolta henkilödatan uudestaan sen mukaan, pitääkö mukana olla
     * epäaktiiviset henkilöt vai ei.
     *
     * @param b
     */
    public void setFilterActiveEmployees(boolean b) {
        session.setFilterActiveEmployees(b);
        people = session.getFilteredPeople();
    }

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

    private String reservationsToString(Room room) {
        Post[] posts = room.getPosts();
        String str = "";
        if (posts != null) {
            for (Post post: posts) {
                Reservation[] reservations = post.getReservations();

                
                for (int j = 0; j < reservations.length; j++) {
                    Reservation reservation = reservations[j];
                    if (j+1 == reservations.length) {
                        str = str.concat(reservation.getReservingPerson().toString());
                    } else {
                        str = str.concat(reservation.getReservingPerson().toString() + ", ");
                    }
                }
            }
        }
        return str;
    }
}
