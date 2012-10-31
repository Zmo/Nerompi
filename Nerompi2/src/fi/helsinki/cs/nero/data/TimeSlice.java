package fi.helsinki.cs.nero.data;

//package fi.helsinki.cs.nero.data;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author Johannes Kuusela
 *
 */
public class TimeSlice implements Comparable {

	/**Aikajanan alkamisaika*/
	private Date startDate;
	
	/**Aikajanan loppumisaika*/
	private Date endDate;
	
	/**
	 * One day as a miliseconds.
	 */
	public static final long ONEDAY = 86400000;

	/**
	 * P‰iv‰m‰‰r‰n esitysmuoto ladataan luokkamuuttujaan.
	 */
	private static DateFormat dateFormat = DateFormat.getDateInstance();
	
	/**
	 * Konstruktori. Saa parametrinaan aikajakson alkamisajan ja loppumisajan.
	 * @param startDate Alkamisaika <code>Date</code> oliona.
	 * @param endDate Loppumisaika <code>Date</code> oliona.
	 * @throws IllegalArgumentException Jos annettu startDate tai endDate null.
	 */
	public TimeSlice(Date startDate, Date endDate){

		if (startDate == null || endDate == null){
			throw new IllegalArgumentException();
		}
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	
	/**
	 * Palauttaa aikajanan loppumisajan.
	 * @return endDate Loppumisaika <code>Date</code> oliona.
	 */
	public Date getEndDate() {
		return endDate;
	}
	
	
	/**
	 * Asettaa aikajanan loppumisajan.
	 * @param endDate Uusi loppumisaika <code>Date</code> oliona.
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	
	/**
	 * Palauttaa aikajanan alkamisajan
	 * @return startDate Alkamisaika <code>Date</code> oliona.
	 */
	public Date getStartDate() {
		return startDate;
	}
	
	/**
	 * Palauttaa aikajanan alkamisajan SQL-lauseeseen sopivassa muodossa
	 * @return Alkamisaika <code>java.sql.Date</code> oliona.
	 */
	public java.sql.Date getSQLStartDate() {
		return new java.sql.Date(this.startDate.getTime());
	}

	/**
	 * Palauttaa aikajanan loppumisajan SQL-lauseeseen sopivassa muodossa
	 * @return Loppumisaika <code>java.sql.Date</code> oliona.
	 */
	public java.sql.Date getSQLEndDate() {
		return new java.sql.Date(this.endDate.getTime());
	}	
	
	/**
	 * Asettaa aikajanan alkamisajan.
	 * @param startDate Uusi alkamisaika <code>Date</code> oliona.
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * <p>
	 * Kertoo aikav‰lin koon p‰iviss‰. Huom! Jos p‰iv‰t ovat samoja tulee tulokseksi 0
	 * Jos p‰iviss‰ on eroa 1, niin tulos on yksi (esim. 15.6.2004 - 16.6.2004).
	 * </p>
	 * @return P‰ivien lukum‰‰r‰.
	 */
	public int length() {
		
	    return Math.round(((this.endDate.getTime()-this.startDate.getTime())/ONEDAY));
	}
	
	/**
	 * <p>
	 * Kertoo kuinka paljon p‰iv‰‰ on t‰m‰n TimeSlicen loppup‰iv‰n ja toisen TimeSlicen 
	 * alkup‰iv‰n v‰liss‰. Jos toisen TimeSlicen alkup‰iv‰ on ennen 
	 * t‰m‰n TimeSlicen loppup‰iv‰‰, palauttaa negatiivisen arvon.
	 * </p>
	 * @param other
	 * @return P‰ivien lukum‰‰r‰ v‰lill‰ other.startDate - this.endDate.
	 */
	public int daysBetween(TimeSlice other) {
	    
	    long thisEndDate = this.endDate.getTime();
	    long otherStartDate = other.startDate.getTime();
	    
	    return Math.round(((otherStartDate-thisEndDate)/ONEDAY));
	}
	
	/**
	 * Kertoo paljonko on p‰ivi‰ t‰m‰n TimeSlicen alkup‰iv‰n ja toisen
	 * TimeSlicen loppup‰iv‰n v‰lill‰. Jos toisen olion loppup‰iv‰ on ennen 
	 * t‰m‰n olion alkup‰iv‰‰, palauttaa negatiivisen arvon.
	 * @return P‰ivien lukum‰‰r‰ v‰lill‰ this.startDate - other.endDate.
	 */
	public int commonDays(TimeSlice other) {
		
	    long thisStartDay = this.startDate.getTime();
	    long otherEndDay = other.endDate.getTime();
	    
	    return (int) ((otherEndDay-thisStartDay)/ONEDAY);
	}
	
	/**
	 * <p>
	 * Kertoo paljonko aikaa on this olion aloitusp‰iv‰n ja otherStartDate p‰iv‰n v‰liss‰.
     * Voi myˆs antaa negatiivisen tuloksen, jos otherStartDate on aiemmin kuin
     * olion aloitusp‰iv‰.
	 * </p>
	 * @param otherStartDate 
	 * @return V‰liss‰ olevien p‰ivien m‰‰r‰ kokonaislukuna.
	 */
	public int startDayAfter(Date otherStartDate) {

	    return (int) ((this.startDate.getTime()-otherStartDate.getTime())/ONEDAY);
	}

    /**
     * Palauttaa true jos ja vain jos annettu p‰iv‰ kuuluu t‰h‰n TimeSliceen.
     * SO. on sen alku- tai loppup‰iv‰ tai jokin niiden v‰liss‰ oleva p‰iv‰.
     * @param date P‰iv‰, jonka mukanaolo tarkistetaan.
     * @return Kuuluuko p‰iv‰ t‰h‰n TimeSliceen?
     */
    public boolean contains(Date date) {
        return (this.startDate.compareTo(date) <= 0 && date.compareTo(this.endDate) <= 0);
    }

    /**
     * Palauttaa true jos ja vain jos annetulla aikav‰lill‰ on jokin
     * p‰iv‰, joka kuuluu myˆs t‰h‰n aikav‰liin
     * @param other Aikav‰li, johon verrataan.
     * @return Osuvatko aikav‰lit p‰‰llekk‰in?
     */
    public boolean overlaps(TimeSlice other) {
        // Tarkistus on helpointa tehd? k??nteisesti: jos yksi jakso on
        // ennen toista tai p?invastoin, ne eiv?t ole p??llekk?in
    	return !(endDate.compareTo(other.startDate) < 0 ||
                 other.endDate.compareTo(startDate) < 0);
    }

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object obj) {
		TimeSlice t = (TimeSlice)obj;

		if (startDate.compareTo(t.startDate) < 0)
			return -1;
		
		if (startDate.compareTo(t.startDate) > 0)
			return 1;
		
		if (endDate.compareTo(t.endDate) < 0)
			return -1;

		if (endDate.compareTo(t.endDate) > 0)
			return 1;

		return 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (compareTo(obj) == 0)
			return true;
		
		return false;
	}

	public String toString() {
	    return dateFormat.format(startDate) + "-" + dateFormat.format(endDate);
	}
}
