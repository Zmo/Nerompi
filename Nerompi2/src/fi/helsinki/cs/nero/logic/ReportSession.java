/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.logic;

import java.util.Vector;

/**
 *
 * @author lpesola
 */
public interface ReportSession {

    /**
     * Tuottaa vektorin, joka sis�lt�� kaikki ohjelman tuntemat huoneet
     * ja n�ihin liittyv�n oleellisen datan.
     *
     * @return vektori, joka sis�lt�� taulukon rivit vektoreina
     */
    Vector<Vector<Object>> getPeopleData();

    /**
     * Tuottaa vektorin, joka sis�lt�� kaikki ohjelman tuntemat henkil�t
     * ja n�ihin liittyv�n oleellisen datan.
     *
     * @return vektori, joka sis�lt�� taulukon rivit vektoreina
     */
    Vector<Vector<Object>> getRoomData();

    Boolean getShowOnlyActiveEmployees();

    /**
     * Hakee Sessiolta henkil�datan uudestaan sen mukaan, pit��k� mukana olla
     * ep�aktiiviset henkil�t vai ei.
     *
     * @param b
     */
    void setFilterActiveEmployees(boolean b);
    
}
