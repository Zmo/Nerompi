package fi.helsinki.cs.nero.test;

import fi.helsinki.cs.nero.logic.ReportSession;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author lpesola
 */
public class StubReportSession implements ReportSession {

    Vector<Vector<Object>> peopleData;
    Vector<Vector<Object>> activePeopleData;
    Vector<Vector<Object>> inactivePeopleData;
    Vector<Vector<Object>> roomData;
    private Boolean showOnlyActive;

    public StubReportSession() {
        roomData = new Vector<>();
        peopleData = new Vector<>();
        activePeopleData = new Vector<>();
        inactivePeopleData = new Vector<>();
        initRooms();
        initActivePeople();
        initInactivePeople();
        peopleData = activePeopleData;
        showOnlyActive = true;
    }

    @Override
    public Vector<Vector<Object>> getPeopleData() {
        return peopleData;
    }

    @Override
    public Vector<Vector<Object>> getRoomData() {
        return roomData;
    }

    @Override
    public Boolean getShowOnlyActiveEmployees() {
        return showOnlyActive;
    }

    @Override
    public void setFilterActiveEmployees(boolean b) {
        showOnlyActive = b;
        if (showOnlyActive) {
            peopleData = activePeopleData;
        } else {
            peopleData = inactivePeopleData;
        }
    }

    private void initRooms() {

        Vector<Object> rivi1 = new Vector<>();
        rivi1.add("C222");
        rivi1.add(2);
        rivi1.add("C");
        rivi1.add(3);
        rivi1.add(6.4);
        rivi1.add("testihuone 1");
        rivi1.add("avainvaraus");
        rivi1.add("työpistevaraus1, työpistevaraus2");
        Vector<Object> rivi2 = new Vector<>();
        rivi2.add("D221");
        rivi2.add(2);
        rivi2.add("D");
        rivi2.add(3);
        rivi2.add(2.0);
        rivi2.add("testihuone 2");
        rivi2.add("avainvaraus1, avainvaraus2");
        rivi2.add("työpistevaraus");
        Vector<Object> rivi3 = new Vector<>();
        rivi3.add("B321");
        rivi3.add(3);
        rivi3.add("B");
        rivi3.add(3);
        rivi3.add(10.0);
        rivi3.add("testihuone 1");
        rivi3.add("avainvaraus");
        rivi3.add("työpistevaraus1, työpistevaraus2");
        roomData.add(rivi1);
        roomData.add(rivi2);
        roomData.add(rivi3);


    }

    private void initActivePeople() {
        Vector<Object> rivi1 = new Vector<>();
        rivi1.add("1 testityyyppi");
        rivi1.add("titteli");
        rivi1.add("työpiste foo");
        rivi1.add("kerros foo");
        rivi1.add("siipi foo");
        rivi1.add("x työpistettä");
        rivi1.add(new Date());
        rivi1.add("foo@foo.fi");
        rivi1.add("55555");
        rivi1.add("postihuone");
        activePeopleData.add(rivi1);
    }

    private void initInactivePeople() {
        Vector<Object> rivi1 = new Vector<>();
        rivi1.add("1 testityyyppi");
        rivi1.add("titteli");
        rivi1.add("työpiste foo");
        rivi1.add("kerros foo");
        rivi1.add("siipi foo");
        rivi1.add("x työpistettä");
        rivi1.add(new Date());
        rivi1.add("foo@foo.fi");
        rivi1.add("55555");
        rivi1.add("postihuone");
        inactivePeopleData.add(rivi1);
        Vector<Object> rivi2 = new Vector<>();
        rivi2.add("2 testityyyppi");
        rivi2.add("titteli");
        rivi2.add(" ");
        rivi2.add(null);
        rivi2.add(null);
        rivi2.add(null);
        rivi2.add(null);
        rivi2.add("");
        rivi2.add("");
        rivi2.add("ei lokeroa");
        inactivePeopleData.add(rivi2);
    }
}
