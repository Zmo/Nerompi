package fi.helsinki.cs.nero.logic;

import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.data.Room;
import fi.helsinki.cs.nero.db.NeroDatabase;
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
        for (int i = 0; i < rooms.length; i++) {
            Vector<Object> row = new Vector<>();
            Room room = rooms[i];
            row.add(room.getRoomName());
            row.add(room.getFloor());
            row.add(room.getWing());
            row.add(room.getPosts().length);
            row.add(room.getRoomSize());
            row.add(room.getDescription());
            // avainvaraukset
            row.add("avainhl�1, avainhl�2");
            // ty�pistevaraukset
            row.add("varaus, varaus, varaus, varaus, varaus, varaus, varaus, varaus, varaus");
            roomTableData.add(row);
        }
        return roomTableData;
    }
    
    public Vector<Vector<Object>> getPeopleData() {
         Vector peopleTableData = new Vector<>();

        for (int i = 0; i < people.length; i++) {
            Vector<Object> row = new Vector<>();

            row.add(people[i].getName());
            row.add(people[i].getTitteli());
            String roomName = people[i].getRoom();
            Reservation reservation = people[i].getReservationForRoom(roomName);
            // jos varausta ei ole, n�ytet��n vain huonenumero
            // muuten n�ytet��n my�s ty�pisteen numero
            if (reservation == null) {
                row.add(roomName);
            } else {
                row.add(reservation.getTargetPost().toString());
            }
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
            // n�ytet��n t�m�nhetkisen huoneen varauksen p��ttymisp�iv�,
            // jos henkil�ll� on jokin voimassaoleva varaus t�h�n huoneeseen
            if (reservation == null) {
                row.add(null);
            } else {
                row.add(reservation.getLastDay());
            }
            row.add(people[i].getSahkoposti());
            row.add(people[i].getWorkPhone());

            if (people[i].getPostilokeroHuone() == null) {
                row.add("ei postilokeroa");
            } else {
                row.add(people[i].getPostilokeroHuone());
            }

            peopleTableData.add(i, row);
        }
        return peopleTableData;
    }
}
