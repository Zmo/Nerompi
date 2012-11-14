/*
 * Created on 14.11.2004
 *
 */
package fi.helsinki.cs.nero;

import fi.helsinki.cs.nero.db.NeroDatabase;
import fi.helsinki.cs.nero.logic.Session;
import fi.helsinki.cs.nero.ui.NeroUI;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

/**
 * Nero-sovelluksen p��ohjelma.
 * @author Osma Suominen
 */
public class NeroApplication {

    /**
     * Oletuskonfiguraatiotiedoston nimi
     */

    public static final String DEFAULT_INI = "/cs/fs/home/rkolagus/Desktop/nero.ini"; 


    /**
     * Konfiguraation parsiva olio
     */
    private static Properties properties;

    /**
     * Parsintafunktio konfiguraatiotiedostolle
     * @param filename tiedoston nimi
     */
    public static void readIni(String filename) {
        System.out.println("Luetaan asetukset tiedostosta " + filename);
        try {
            properties = new Properties();
            properties.load(new FileInputStream(filename));
        }
        catch (FileNotFoundException x) {
            /* TODO laitetaanko joku ikkuna ilmoittamaan virheest�? */
            System.out.println("Tiedostoa ei l�ydy!");
            System.exit(1);
        }
        catch (Exception x) {
            /* TODO laitetaanko joku ikkuna ilmoittamaan virheest�? */
            System.out.println("Virhe luettaessa asetuksia tiedostosta: " +
                    x.toString());
            System.exit(1);
        }
    }
    
    /**
     * Palauttaa konfiguraatiotiedostossa olevien kenttien arvot
     * @param name kent�n nimi
     * @return kent�n arvo
     */
    public static String getProperty(String name) {
        return properties.getProperty(name);
    }
    
    /**
     * Ajaa Nero-sovelluksen. K�ynnist�� ohjelman eri komponentit ja
     * liitt�� ne toisiinsa.
     * @param args komentoriviparametrit
     */
	public static void main(String[] args) {
	    if (args.length > 0) {
                readIni(args[0]);
            }
	    else {
                readIni(DEFAULT_INI);
            }
	    
        Session session = new Session();
        NeroDatabase db = new NeroDatabase(session,
        		getProperty("db_class"), getProperty("db_connection"),
        		getProperty("db_username"), getProperty("db_password"));
        session.setDatabase(db);
        db.updateRooms();
        NeroUI ui = new NeroUI(session);
	}
}
