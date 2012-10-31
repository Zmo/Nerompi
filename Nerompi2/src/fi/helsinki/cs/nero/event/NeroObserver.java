package fi.helsinki.cs.nero.event;

/**
 * Kuuntelijarajapinta. Määrittelee toiminnot, jotka Neron päivitystapahtumien
 * kuuntelijan tulee toteuttaa.
 */

public interface NeroObserver {

    /**
     * Kutsutaan sen kertomiseksi, ett? kuunneltava tieto on muuttunut.
     * @param type muuttuneen tiedon tyyppi
     */
    public void updateObserved(int type);
}
