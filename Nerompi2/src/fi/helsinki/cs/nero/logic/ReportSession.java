package fi.helsinki.cs.nero.logic;

import java.util.Vector;

/**
 * Käsittelee Session tuottamaa dataa.
 * 
 * @author lpesola
 */
public interface ReportSession {

    /**
     * Tuottaa vektorin, joka sisältää kaikki ohjelman tuntemat huoneet
     * ja näihin liittyvän oleellisen datan.
     *
     * @return vektori, joka sisältää taulukon rivit vektoreina
     */
    Vector<Vector<Object>> getPeopleData();

    /**
     * Tuottaa vektorin, joka sisältää kaikki ohjelman tuntemat henkilöt
     * ja näihin liittyvän oleellisen datan.
     *
     * @return vektori, joka sisältää taulukon rivit vektoreina
     */
    Vector<Vector<Object>> getRoomData();

    Boolean getShowOnlyActiveEmployees();

    /**
     * Hakee Sessiolta henkilödatan uudestaan sen mukaan, pitääkö mukana olla
     * epäaktiiviset henkilöt vai ei.
     *
     * @param b
     */
    void setFilterActiveEmployees(boolean b);
    
}
