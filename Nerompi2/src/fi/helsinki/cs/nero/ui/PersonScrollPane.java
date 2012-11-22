/*
 * Created on Nov 6, 2004
 */
package fi.helsinki.cs.nero.ui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import fi.helsinki.cs.nero.NeroApplication;
import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.data.TimeSlice;
import fi.helsinki.cs.nero.event.NeroObserver;
import fi.helsinki.cs.nero.event.NeroObserverTypes;
import fi.helsinki.cs.nero.logic.Session;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextField;
import sun.util.calendar.CalendarDate;

/**
 * <p>
 * Luokka luo ja päivittää tarvittaessa esitettävien
 * henkilöiden graaffisen esitysken käyttöliittymässä.
 * </p>
 *  
 */
public class PersonScrollPane extends JScrollPane implements NeroObserver {
	
    /**
     * <p>
     * Pääpaneli johon kaikki muut komponentit liitetään, staattinen jotta
     * voidaan antaa parametrina yläluokan konstruktorille.
     * </p>
     */
	private static JPanel mainPanel;
	
	/**
	 * Lista jossa <code>PersonsContracts</code> oliot sijaitsevat.
	 */
	private LinkedList people;
	
	/**
	 * Ohjelmassa käsiteltävä aikajana.
	 */
	private TimeSlice timeScale;
	
	/**
	 * Viite sessioon.
	 */
	private Session sessio;
	
	/**
	 * Hakuehtoihin täsmäävät henkilöt.
	 */
	private Person[] persons;
	
	/**
	 * Paneli jossa henkilölista sitten sijaitsee.
	 */
	private JPanel personPanel;
	
	/**
	 * Rivin leveyteen käytettävissä oleva pikselimäärä.
	 */
	private final int ROW_LENGTH;
	
	/**
	 * Paljonko yksi päivä on pikseleissä.
	 */
	private double DAY_IN_PIXELS;
	
	/**
	 * Päivän pituus millisekuntteina.
	 */
	private static long oneDay = 86400000;
	
	private final static ImageIcon HAPPY; 
	private final static ImageIcon NOT_HAPPY;
	private final static ImageIcon OY; 
	private final static ImageIcon OA;
	private final static ImageIcon VY; 
	private final static ImageIcon VA;
	
	private static Border raisedBevel = BorderFactory.createBevelBorder(BevelBorder.RAISED);
	private static Border loweredBevel = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
	private static Border raisedEtched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);       
	private static Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
	
	private static final Color HEADER_BG = new Color(255,240,192);
	private static final Color BG = new Color(90,119,173);
	
	//Staattinen alustuslohko.
	static {
		HAPPY = new ImageIcon(NeroApplication.getProperty("img_happy"));
		NOT_HAPPY = new ImageIcon(NeroApplication.getProperty("img_unhappy"));
		OY = new ImageIcon(NeroApplication.getProperty("img_oy"));
		OA = new ImageIcon(NeroApplication.getProperty("img_oa"));
		VY = new ImageIcon(NeroApplication.getProperty("img_vy"));
		VA = new ImageIcon(NeroApplication.getProperty("img_va"));
		mainPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
	}
	
	/**
	 * Konstruktori.
	 * @param sessio Viite sessioon.
	 * @param dateSlider ?
	 * @param tsip ?
	 */
	public PersonScrollPane(Session sessio, DateSlider dateSlider,
			TimeSliceIndicatorPanel tsip) {
		
		super(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
                this.sessio = sessio;
		this.timeScale = this.sessio.getFilterTimescale();
		this.persons = sessio.getFilteredPeople();
		
		//Kuuntelijat
		this.sessio.registerObserver(NeroObserverTypes.FILTER_PEOPLE, this);
		this.sessio.registerObserver(NeroObserverTypes.RESERVATIONS, this);
		this.sessio.registerObserver(NeroObserverTypes.TIMESCALE, this);
		
		this.personPanel = new JPanel();
		this.personPanel.setLayout(new BoxLayout(this.personPanel, BoxLayout.Y_AXIS));
		
		mainPanel.add(this.personPanel);
		
		BevelBorder mainBorder =
			(BevelBorder)BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		
		setBorder(mainBorder);
		
		int borderWidth = mainBorder.getBorderInsets(this).left +
		mainBorder.getBorderInsets(this).right;
		
		this.ROW_LENGTH = NeroUI.RIGHT_WIDTH - JMultiSlider.SIDE_WIDTH -
		borderWidth - this.getVerticalScrollBar().getMaximumSize().width;
		
		dateSlider.setSliderWidth(this.ROW_LENGTH + 1);
		tsip.setMaxWidth(this.ROW_LENGTH + 1);
                this.getVerticalScrollBar().setUnitIncrement(16); // nopeuttaa scrollausta
                
		updateScale();
		this.generate(this.sessio);
	}
	
	/**
	 * Päivittää esitettävien henkilöiden esityksen käyttöliittymässä.
	 *
	 */
	private void generate(Session sessio) {
		
		long startTime = System.currentTimeMillis();        
		this.people = new LinkedList();
		
		//Poistetaan vanha personPanel ja luodaan uusi tilalle.
		if(this.personPanel != null) {
			this.personPanel.removeAll();
		}
		
		//Kasataan henkilöiden työsopimukset siten, että yhden henkilön työsopimukset on PersonsContracts olioina (Riveittäin siis).
		long while1Start = System.currentTimeMillis();
		for(int i=0; i<this.persons.length; ++i) {
			
			Reservation[] reservations = persons[i].getReservations();
			
			PersonsContracts person = new PersonsContracts(persons[i], this.timeScale, DAY_IN_PIXELS, sessio);
			people.add(person);
		}
		
		//Tehdään jokaiselle persons contracts oliolle vastaavat rivihässäkkä (JPanel) ja liitetään se tähän näkyviin.
		Iterator peopleIterator = people.iterator();
		long while2Start = System.currentTimeMillis();
		
		boolean firstPerson = true;
		
		while(peopleIterator.hasNext()) {
			
			//Henkilön headerit ja rivit sisällänsä pitävä paneli, eli yhden henkilön esitys.
			JPanel personsInfo = new JPanel();
			personsInfo.setLayout(new BoxLayout(personsInfo, BoxLayout.Y_AXIS));
			
			//header
			JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
			header.setBackground(HEADER_BG);
			
			PersonsContracts personIterator = (PersonsContracts)peopleIterator.next();
			LinkedList rows = personIterator.getRows();
			Iterator rowIterator = rows.iterator();
			
			JLabel faceIcon;
			
			//Lisätään naamankuva..
			if(personIterator.getPerson().getStatus()) {
				faceIcon = new JLabel(NOT_HAPPY);
			} else {
				faceIcon = new JLabel(HAPPY);
			}
			
			faceIcon.setMinimumSize(new Dimension(20, 20));
			faceIcon.setPreferredSize(new Dimension(20, 20));
			faceIcon.setMaximumSize(new Dimension(20, 20));
			faceIcon.setBorder(raisedBevel);
			
			header.add(faceIcon);
			
			//Lisätään henkilön yhteystiedot.
                        PersonNameLabel personNameLabel = new PersonNameLabel(this.sessio, personIterator.getPerson());
                        personNameLabel.setText(personIterator.getPerson().getName());
                        if (personIterator.getPerson().getTitteli() != null){
                            personNameLabel.setText(personNameLabel.getText() + ", " + personIterator.getPerson().getTitteli());
                        }
                        personNameLabel.addMouseListener(new PersonNameLabelListener());
                        header.add(personNameLabel);
                        
                        UusiVarausPopup varausNappi = new UusiVarausPopup(personIterator.getPerson());
                        header.add(varausNappi);

                        
//			JLabel labelText = new JLabel(personIterator.getPerson().getName());             
//			header.add(labelText);
//                        
//                        Lisätään nappula joka avaa tieto ikkunan henkilöstä
//                        JButton tiedot = new JButton(personIterator.getPerson().getPersonID());
//                        header.add(tiedot);
//                        nappulan toiminnallisuus
//                        ButtonListener listener = new ButtonListener(this.sessio);
//                        tiedot.setActionCommand("henkilonLisatiedot");
//                        tiedot.addActionListener(listener);
//                                (new ActionListener() 
//                        {
//                            //Mieti uudestaan koko setti herp a derp a durr
//                            public void actionPerformed(ActionEvent e)
//                            {
//                                //Execute when button is pressed
//                                //painetun nappulan tietoja
//                                JButton personIdButton = (JButton)e.getSource();
//                                
//                                for (int i = 0; i < persons.length; i++) {                          
//                                    if (persons[i].getPersonID().equals(personIdButton.getText())) {           
//                                        new PersonInfoFrame(persons[i]);
//                                    }
//                                }
//                                //hae id
//                                //new PersonInfoFrame(e.getSource().);
//                            }
//                        });

			//Extraheader ylimmäksi, myös extraheader pitää laittaa panelin sisään Layout syistä
			//Extraheader2 on extraeaderin jatkopalanen
			JPanel extraHeader = new JPanel(new BorderLayout());
			extraHeader.setBackground(BG);
			
			JLabel l4 = new JLabel(VY);
			JLabel l3 = new JLabel(OY);
			
			JPanel middlePiece = new JPanel();
			middlePiece.setBackground(HEADER_BG);
			
			extraHeader.add(BorderLayout.WEST, l4);
			extraHeader.add(BorderLayout.CENTER, middlePiece);
			extraHeader.add(BorderLayout.EAST, l3);
						
			personsInfo.add(extraHeader);
			personsInfo.add(header);
			
			while(rowIterator.hasNext()) {
				
				//Tehdään riville paneeli johon elementit tulee peräkkäin.
				RowPanel rowPanel = new RowPanel(ROW_LENGTH);        
				Row row = (Row)rowIterator.next();
				boolean firstContract = true;
				
				row.resetIterator();
				int korkeus = 0;
                                JPanel nappiRivit = new JPanel();
				while(row.hasNext()) {
                                    TimelineElement post = (TimelineElement) row.next();
                                    
                                    if (post.getKalenterinapit() != null) {
                                        nappiRivit.add(post.getKalenterinapit());
                                        korkeus++;
                                    }
                                        // rowPanel.add(post);
				}
                                nappiRivit.setLayout(new GridLayout(korkeus, 1));
                                korkeus = korkeus * 28;
                                rowPanel.add(nappiRivit);
                                rowPanel.setMinimumSize(new Dimension(ROW_LENGTH, korkeus));
                                rowPanel.setPreferredSize(new Dimension(ROW_LENGTH, korkeus));
                                rowPanel.setMaximumSize(new Dimension(ROW_LENGTH, korkeus));
				personsInfo.add(rowPanel);
			}
			this.personPanel.add(BorderLayout.CENTER, personsInfo);
			
			//footer
			JPanel footer = new JPanel(new BorderLayout());
			footer.setBackground(BG);
			footer.setPreferredSize(new Dimension(ROW_LENGTH, 10));
			
			JLabel l = new JLabel(VA);
			JLabel l2 = new JLabel(OA);
			
			JPanel middlePanel = new JPanel();
			middlePanel.setBackground(HEADER_BG);
			footer.add(BorderLayout.WEST, l);
			footer.add(BorderLayout.CENTER, middlePanel);
			footer.add(BorderLayout.EAST, l2);
			
			//loppuun vielä tyhjä suikale
			JPanel emptyRow = new JPanel();
			emptyRow.setBackground(BG);
			emptyRow.setPreferredSize(new Dimension(ROW_LENGTH, 6));
			
			this.personPanel.add(footer);
			this.personPanel.add(emptyRow);
			
		}
	
		//Asetetaan uusi person panel näkyviin.	
		this.setViewportView(mainPanel);
		long time = System.currentTimeMillis()-startTime;
	}
	
	/**
	 * <p>
	 * Toteuttaa rajapinnan jonka kautta saadaan Nerossa tapahtuneet muutokset.
	 * </p>
	 * @param type Tapahtuman tyyppi.
	 */
        
	public void updateObserved(int type) {
		//Jos aikaväli on muuttunut.
		if(type == NeroObserverTypes.TIMESCALE) {        
			this.timeScale = this.sessio.getFilterTimescale();
			this.updateScale();
			this.persons = sessio.getFilteredPeople();
			this.generate(sessio);
			return; 
		}
		
		//Jos näytettävät ihmiset on muuttuneet.
		if(type == NeroObserverTypes.FILTER_PEOPLE || type == NeroObserverTypes.RESERVATIONS) {
			this.persons = sessio.getFilteredPeople();
			this.generate(sessio);
			return; 
		}
	}
	
	/**
	 * <p>
	 * Päivittää scaalan päivä/pikselit.
	 * </p>
	 */
	private void updateScale() {
		if(this.timeScale.length() > 0) {
			DAY_IN_PIXELS = (1.0*ROW_LENGTH)/this.timeScale.length();
		} else {
			DAY_IN_PIXELS = 1;
		}
	}
}
