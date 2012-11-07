package fi.helsinki.cs.nero.test;

import java.sql.DatabaseMetaData;

import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;

import fi.helsinki.cs.nero.db.NeroDatabase;

/**
 * 
 * @author Jyrki Muukkonen
 * 
 * Pienehkö runko DbUnit-testeille. Ei saa (eikä oikeastaan voikaan) käyttää vielä.
 * XXX Aivan alkutekijöissään, ja vaikea saada kantaa jossa uskaltaisi ajaa.
 */
public class TestDBNeroDatabase extends DatabaseTestCase {
	
	private NeroDatabase nerodb;
    
	public TestDBNeroDatabase(String name) {
        super(name);
    }

    protected IDatabaseConnection getConnection() throws Exception {
    	/* NOTE Session-olio jostain? */
    	this.nerodb = new NeroDatabase(null,
    			"oracle.jdbc.driver.OracleDriver",
				"jdbc:oracle:thin:@kontti.helsinki.fi:1522:tkta",
    			"poistettu", "poistettu");
    	DatabaseMetaData dma = this.nerodb.getConnection().getMetaData();
    	System.out.println(dma.getDatabaseProductName());
		return new DatabaseConnection(this.nerodb.getConnection());
    }

    protected IDataSet getDataSet() throws Exception {
        //return new FlatXmlDataSet(new FileInputStream("dataset.xml"));
    	return null;
    }
    
    public void testFoob() {
    	assertTrue(true);
    }
}