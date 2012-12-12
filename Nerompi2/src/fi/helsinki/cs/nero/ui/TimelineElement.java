package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Person;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.border.Border;

import fi.helsinki.cs.nero.data.Reservation;
import fi.helsinki.cs.nero.data.TimeSlice;
import fi.helsinki.cs.nero.logic.Session;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * <p>
 * K‰yttˆliittym‰ss‰ tietyn tyyppisen, aikav‰liin sidonnaisen asian
 * esitt‰miseen tarvittavien luokkien yl‰luokka.
 * </p>
 */
public class TimelineElement extends JPanel {
    
    /**
     * K‰sitelt‰v‰ aikav‰li.
     */
    private TimeSlice timeSlice;
    
    /**
     * Edellinen elementti ( aikajanalla = rivill‰).
     */
    private TimelineElement previous;
    
    /**
     * Seuraava elementti (aikajanalla = rivill‰).
     */
    private TimelineElement next;
    
    /**
     * Voiko elementin kokoa muuttaa.
     */
    protected boolean resizable;
    
    /**
     * K‰ytett‰v‰ skaalaa = p‰iv‰/pikselit.
     */
    protected double scale;
    // protected, jotta voidaan k‰ytt‰‰ DragMouseAdapterista yms
    
    /**
     * Viite sessioon.
     */
    protected Session session;
    
    /**
     * K‰ytett‰v‰ aikav‰li.
     */
    private TimeSlice timeScale;
    
    /**
     * Yksi p‰iv‰ millisekuntteina.
     */
    private final long ONE_DAY = 86400000;
    
    /**
     * K‰ytett‰v‰ TooltipManager.
     */
    private static ToolTipManager  manager = ToolTipManager.sharedInstance();

    /**
     * Kalenterinapit
     */
    private Kalenterinappi alkukalenteri;
    private Kalenterinappi loppukalenteri;
    private JPanel kalenterinapit;    
    
    private static Color BG_COLOR = new Color(39,177,39);
    private static Color HEADER_BG = new Color(255,240,192);
    /**
     * Konstruktori.
     * @param timeSlice Elementin esitt‰m‰n aikav‰lin pituus.
     * @param scale K‰ytett‰v‰ skaala = p‰iv‰/pikselit.
     * @param color Elementin v‰ri.
     * @param session Viite sessioon.
     */
    public TimelineElement(TimeSlice timeSlice, double scale, Color color, Session session) {
        
    	this.session = session;
        this.timeSlice = timeSlice;
        this.resizable = false;
        this.timeScale = session.getFilterTimescale();

        this.scale = scale;
       
        MouseListener listener = new DragMouseAdapter();
        this.addMouseListener(listener);
        this.addMouseMotionListener((MouseMotionListener) listener);
        this.setTransferHandler(new TransferHandler("texti"));  

        manager.setDismissDelay(10000);
        
        this.setBackground(color);
        this.ownResize();
    }
  
    /**
     * 
     * Konstruktori, k‰ytet‰‰n luodessa muuttumattoman kokoiset elementit.
     * @param timeSlice Elementin esitt‰m‰n aikav‰lin pituus.
     * @param scale K‰ytett‰v‰ skaala = p‰iv‰t/pikselit.
     * @param color Elementin v‰ri.
     * @param labelText Elementin p‰‰ll‰ esitett‰v‰ teksti.
     * @param session Viite sessioon.
     */
    public TimelineElement(TimeSlice timeSlice, double scale, Color color, String labelText, Session session) {
        
        this(timeSlice, scale, color, session);
        this.session = session;
        this.timeScale = session.getFilterTimescale();
        this.createLabel(labelText);
        
        this.setToolTipText("<html>" + labelText + "<br>" + timeSlice + "</html>");

        manager.setDismissDelay(10000);
    }
    /**
     * Henkilˆtietoikkunaa varten
     * @param timeSlice
     * @param scale
     * @param color
     * @param perp
     * @param session 
     */
    public TimelineElement(TimeSlice timeSlice, double scale, Color color, Person perp, Session session) {
        
        this(timeSlice, scale, color, session);
        this.session = session;
        this.timeScale = session.getFilterTimescale();
        this.createLabel(perp);
        
        this.setToolTipText("<html>" + perp + "<br>" + timeSlice + "</html>");

        manager.setDismissDelay(10000);
    }
    
    /**
     * 
     * Konstruktori, k‰ytet‰‰n luomaan muutettavan kokoiset elementit.
     * @param timeSlice Elementin esitt‰m‰n aikav‰lin pituus.
     * @param scale K‰ytett‰v‰ skaala = p‰iv‰t/pikselit.
     * @param color Elementin v‰ri.
     * @param label Elementin p‰‰ll‰ esitett‰v‰ teksti.
     * @param session Viite sessioon.
     */
    public TimelineElement(String label, TimeSlice timeSlice, double scale, Color color, Session session) {
       
        this.timeSlice = timeSlice;
        this.scale = scale;
        this.session = session;
        this.timeScale = session.getFilterTimescale();
        this.resizable = true;

        this.setBackground(color);
        this.ownResize();
        Border blackline = BorderFactory.createLineBorder(Color.black);
        this.setBorder(blackline);
        this.kalenterinappipari(label);
        
        //Listenerit.
        MouseListener listener = new DragMouseAdapter();
        this.addMouseListener(listener);
        this.addMouseMotionListener((MouseMotionListener) listener);
        this.setTransferHandler(new TransferHandler("texti"));  

        this.createLabel(label);

        manager.setDismissDelay(10000);
        this.setToolTipText("<html>" + label + "<br>" + timeSlice + "</html>");
    }
    
    /**
     * Tyhj‰ konstruktori.
     */
    public TimelineElement() {
        super();
    }
    
    /**
     * NEROMPI-LISƒYS: KALENTERINAPPIPARI
     * @param label Varattu huone ja tyˆpiste 
     */

    private void kalenterinappipari(String label) {;
        
        JPanel paivat = new JPanel(new FlowLayout());


        this.alkukalenteri = new Kalenterinappi(this.timeSlice.getStartDate(), this, true);
        this.loppukalenteri = new Kalenterinappi(this.timeSlice.getEndDate(), this, false);
        
        JLabel huoneKentta = new JLabel(" ");
//        if (this.getTimeSlice().getStartDate().after(this.getTimeSlice().getEndDate())) {
//        } else {
            if (this.getTimeSlice().getStartDate() == null || this.getTimeSlice().getEndDate() == null) {
                huoneKentta.setText(" ");
                paivat.add(huoneKentta);
            }
            else {
                JLabel valimerkki = new JLabel(" - ");
                huoneKentta.setText(label + " ");
                paivat.setBackground(HEADER_BG);
                
                paivat.add(huoneKentta);
                paivat.add(alkukalenteri);
                paivat.add(valimerkki);
                paivat.add(loppukalenteri);

            }
//  }
        this.kalenterinapit = paivat;
    }

    public JPanel getKalenterinapit() {
        if (this.kalenterinapit == null) {
            return null;
        }
        return this.kalenterinapit;
    }
    public Kalenterinappi getAlkukalenteri(){
        return this.alkukalenteri;
    }

    public Kalenterinappi getLoppukalenteri(){
        return this.loppukalenteri;
    }
    
    public void updateNappiKalenteri(){
        //this.timeSlice.setStartDate(this.alkukalenteri.getTargetDate());
        //this.timeSlice.setEndDate(this.loppukalenteri.getTargetDate());
    }
    
/* /LISAYS*/
    
    /**
     * Luo t‰lle TimelineElementille labelin.
     * Asettaa labelin kohtaan josta sen tulisi aina n‰ky‰. 
     * @param text
     */
    public void createLabel(String text) {
        JLabel name = new JLabel(text);
        SpringLayout layout = new SpringLayout();
        this.setLayout(layout);
        this.add(name);
        // j‰tet‰‰n vakiona hieman rakoa
        int position = 5;

        // lasketaan alkaako sopimus ennen n‰ytett‰v‰‰ aikav‰li‰
        Date d1 = this.timeScale.getStartDate();
        Date d2 = this.timeSlice.getStartDate();
        long diff = (d1.getTime()-d2.getTime())/TimeSlice.ONEDAY;
        
        if(diff > 0) {
        		position = (int)(diff*scale);
        }
        // ja laitetaan label alkamaan oikeasta kohdasta,
        // eli labelin vasen reuna positionin verran thisin reunasta
        layout.putConstraint(SpringLayout.WEST, name, position,
        						SpringLayout.WEST, this);
    }
    /**
     * Henkilˆtietoikkunaa varten
     * @param pe 
     */
    public void createLabel(Person pe) {
        JLabel name = new PersonNameLabel(this.session, pe);
        String puhNumero = "";
        if (pe.getWorkPhone() != null){
            puhNumero = " (" + pe.getWorkPhone() + ")";
        }
        name.setText(pe.getName() + puhNumero);
        name.addMouseListener(new PersonNameLabelListener());
        SpringLayout layout = new SpringLayout();
        this.setLayout(layout);
        this.add(name);
        // j‰tet‰‰n vakiona hieman rakoa
        int position = 5;

        // lasketaan alkaako sopimus ennen n‰ytett‰v‰‰ aikav‰li‰
        Date d1 = this.timeScale.getStartDate();
        Date d2 = this.timeSlice.getStartDate();
        long diff = (d1.getTime()-d2.getTime())/TimeSlice.ONEDAY;
        
        if(diff > 0) {
        		position = (int)(diff*scale);
        }
        // ja laitetaan label alkamaan oikeasta kohdasta,
        // eli labelin vasen reuna positionin verran thisin reunasta
        layout.putConstraint(SpringLayout.WEST, name, position,
        						SpringLayout.WEST, this);
    }
    
    /**
     * alauttaa tiedon siit‰, onko elementin koko muutettavissa vai ei.
     * @return <code>True</code> jos elementin koko on muutettavissa, <code>False</code> jos ei.
     */
    public boolean isResizable() {
        return this.resizable;
    }
    
    /**
     * Asettaa elementin koon vastaamaan sen esitt‰m‰n aikajanan pituuden.
     *
     */
    public void ownResize() {
        
        int length = this.timeSlice.length();
        
        //Laske aika = pituus alusta ekaan p‰iv‰‰n * scale
        
        long timeScaleStart = this.timeScale.getStartDate().getTime();
        
        long timeSliceStart = this.timeSlice.getStartDate().getTime();
        long timeSliceEnd = this.timeSlice.getEndDate().getTime();
        
        //Laske aika = pituus alusta vikaan p‰iv‰‰n = scale
        int fromStartToStart = Math.round((timeSliceStart-timeScaleStart)/ONE_DAY);
        int fromStartToEnd = Math.round((timeSliceEnd-timeScaleStart)/ONE_DAY);
        
        //Laske elmentin pituus noiden erotuksesta
        int pikseleitaEkaan = (int)Math.round(fromStartToStart*scale);
        int pikseleitaTokaan = (int)Math.round(fromStartToEnd*scale);
        
        
        this.setToolTipText(this.timeSlice.toString());
        
        int size = pikseleitaTokaan-pikseleitaEkaan;
        
        if(length == 0)
            size = 0;
        
        this.setMinimumSize(new Dimension(size, 20));
        this.setPreferredSize(new Dimension(size, 20));
        this.setMaximumSize(new Dimension(size, 20));
    }
    
    
    //**********Koon muuttamiseen tarvittava v‰lineistˆ.**********
    
    /**
     * Muuttaa kokoa kun elementti‰ vedet‰‰n oikeasta reunasta oikealle.
     */
    public void fromRightToRight(int value) {
        
        int days = (int)Math.round(value/scale);
        
        if(days > this.next.timeSlice.length())
            days = this.next.timeSlice.length();
        
        //Poistetaan edell‰ olevasta tarvittava m‰‰r‰ p‰ivi‰.
        this.addToNextStart(days);
        
        //Lis‰t‰‰n loppuaikaan haluttu m‰‰r‰ p‰ivi‰, eli aikajana pitenee.
        this.addTimeToEnd(days);
    }
    
    /**
     * Muuttaa kokoa kun elementti‰ vedet‰‰n oikeasta reunasta vasemmalle.
     */
    public void fromRightToLeft(int value) {
     
        int days = (int)Math.round(value/scale);
        
        //T‰m‰n uusi alkuaika.
        long time = this.timeSlice.getEndDate().getTime()-days*ONE_DAY;
        //Jos uusi alkuaika olisi ennen aikav‰lin alkuaikaa..
        if(time <= this.timeScale.getStartDate().getTime()+ONE_DAY) {
            days = (int)((this.timeSlice.getEndDate().getTime()-(this.timeScale.getStartDate().getTime()+ONE_DAY))/ONE_DAY);
        } else {
            if(days >= this.timeSlice.length()) {
                days = this.timeSlice.length()-2;
            }
        }
        
        this.takeTimeFromEnd(days);    
        //Poistetaan aikaa seuraavan alusta = palkki isonee.
        this.removeFromNextStart(days);
    }
    
    /**
     * Muuttaa kokoa kun elementti‰ vedet‰‰n vasemmasta reunasta oikealle.
     */
    public void fromLeftToRight(int value) {
        
        int days = (int)Math.round(value/scale);
              
        //T‰m‰n uusi alkuaika.
        long time = this.timeSlice.getStartDate().getTime()+days*ONE_DAY;
        //Jos uusi alkuaika olisi ennen aikav‰lin alkuaikaa..
        if(time >= this.timeScale.getEndDate().getTime()-ONE_DAY) {
            days = (int)(((this.timeScale.getEndDate().getTime()-ONE_DAY)-this.timeSlice.getStartDate().getTime())/ONE_DAY);
        } else {
            if(days >= this.timeSlice.length()) {
                days = this.timeSlice.length()-2;
            }
        }

        //Lis‰t‰‰n alkuaikaan haluttu m‰‰r‰ p‰ivi‰, eli aikajana pienenee.
        this.addTimeToStart(days);
        
        //Lis‰t‰‰n edelliseen aikajanaan t‰st‰ poistetut p‰iv‰t.
        this.addToPreviousEnd(days);  
    }
    
    /**
     * 
     * @param value Muutoksen koko pikseleiss‰.
     */
    public void fromLeftToLeft(int value) {
        
        int days = (int)Math.round(value/scale);
                  
        //Tehd‰‰n vain jos edellisen elementin pituus on yli 0.        
        if(this.previous.timeSlice.length() > 0) {
            
            //Varmistetaan ettei poisteta enemm‰n kuin edellisell‰ on antaa.
            if(days > this.previous.timeSlice.length())
                days = this.previous.timeSlice.length();
            
            //Poistetaan aikaa t‰m‰n aikajanan alkuajasta.
            this.takeTimeFromStart(days);
        
            //Poistetaan aikaa edellisen loppuajasta.
            this.removeFromPreviousEnd(days);
            
        }
    }
    
    /**
     * Lis‰‰ aikaa seuraavaan elementin alkuun.
     * @param days Lis‰tt‰v‰ aika p‰iviss‰.
     */
    private void addToNextStart(int days) {
  
        long oldStartDate = this.next.timeSlice.getStartDate().getTime();   
        oldStartDate += days*ONE_DAY;
        this.next.timeSlice.setStartDate(new Date(oldStartDate));
        this.next.ownResize();
    }
    
    /**
     * Poistaa aikaa edellisen elementin alusta.
     * @param days Aika p‰iviss‰.
     */
    private void removeFromNextStart(int days) {
     
        long oldStartDate = this.next.timeSlice.getStartDate().getTime();   
        oldStartDate -= days*ONE_DAY;
        this.next.timeSlice.setStartDate(new Date(oldStartDate));
        this.next.ownResize();
    }
    
    /**
     * Lis‰‰ aikaa edellisen elementin loppuun.
     * @param days Aika p‰iviss‰.
     */
    private void addToPreviousEnd(int days) {

        long oldEndDate = this.previous.timeSlice.getEndDate().getTime();   
        oldEndDate += days*ONE_DAY;
        this.previous.timeSlice.setEndDate(new Date(oldEndDate));
        this.previous.ownResize();
    }
    
    /**
     * Poistaa aikaa edellisen elementin lopusta.
     * @param days Aika p‰iviss‰.
     */
    private void removeFromPreviousEnd(int days) {
        long oldEndDate = this.previous.timeSlice.getEndDate().getTime();   
        oldEndDate -= days*ONE_DAY;
        this.previous.timeSlice.setEndDate(new Date(oldEndDate));
      
        this.previous.ownResize();
    }
    

    //**********Ajan muuttamiseen tarvittava v‰lineistˆ.**********
    
    /**
     * Poistaa aikaa elementin lopusta.
     * @param days Aika p‰iviss‰.
     * @return Todellisuudessa poistettujen p‰ivien lukum‰‰r‰.
     */
    private int takeTimeFromEnd(int days) {
                
        //Viekˆ mahdollinen muutos ajan ulos aikav‰lilt‰.
        if((this.timeSlice.getEndDate().getTime()-days*ONE_DAY) > timeScale.getStartDate().getTime()) {
    
            long endDate = this.timeSlice.getEndDate().getTime();
            endDate -= ONE_DAY*days;
            this.timeSlice.setEndDate(new Date(endDate));
            this.ownResize();
            return days;
        
        } else {//Otetaan sen verran ku voidaan..

            int commonDays = this.timeScale.commonDays(this.timeSlice);
            long endDate = this.timeSlice.getEndDate().getTime();
            
            this.timeSlice.setEndDate(new Date(endDate-commonDays*ONE_DAY));
            this.ownResize();
            return commonDays;
        }  
    }
    
    /**
     * Lis‰‰ aikaa elementin loppuun.
     * @param days Aika p‰iviss‰.
     */
    private void addTimeToEnd(int days) {
             
        if((this.timeSlice.getEndDate().getTime()+days*ONE_DAY) < timeScale.getEndDate().getTime()) {
        
            long endDate = this.timeSlice.getEndDate().getTime();
            endDate += days*ONE_DAY;
            this.timeSlice.setEndDate(new Date(endDate)); 
            
            this.ownResize();

        } else {
            
            int commonDays = this.timeSlice.commonDays(this.timeScale);
            
            long endDate = this.timeSlice.getEndDate().getTime();
        
            this.timeSlice.setEndDate(new Date(endDate+commonDays*ONE_DAY));
            
            this.ownResize();
        }        
    }
    
    /**
     * Lis‰‰ aikaa elementin alkuun.
     * @param days Aika p‰iviss‰.
     */
    private void addTimeToStart(int days) {
                               
        if((this.timeSlice.getStartDate().getTime()+days*ONE_DAY) < timeScale.getEndDate().getTime()) {
        
            long startDate = this.timeSlice.getStartDate().getTime();
            startDate += days*ONE_DAY;
            this.timeSlice.setStartDate(new Date(startDate)); 
            
            this.ownResize();
            
        } else {
            
            int commonDays = this.timeSlice.commonDays(this.timeScale);
            
            long startDate = this.timeSlice.getStartDate().getTime();
        
            this.timeSlice.setStartDate(new Date(startDate+commonDays*ONE_DAY));
     
            this.ownResize();
        }
    }
    
    /**
     * Poistaa aikaa elementin alusta.
     * @param days Aika p‰iviss‰.
     * @return Todellisuudessa poistettujen p‰ivien m‰‰r‰.
     */
    private int takeTimeFromStart(int days) {

        long startDate = this.timeSlice.getStartDate().getTime();
        startDate -= days*ONE_DAY;
        this.timeSlice.setStartDate(new Date(startDate));  
        this.ownResize();
        
        return days;
    }
         
    /**
     * Asettaa elementin edellisen elementin.
     * @param previous Asetettava elementti.
     */
    public void setPrevious(TimelineElement previous) {
        this.previous = previous;
    }
        
    /**
     * Asettaa elementin seuraavan elementin.
     * @param next Asetettava elementti.
     */
    public void setNext(TimelineElement next) {
        this.next = next;
    }
       
    /**
     * <p>
     * Palauttaa viitteen this olioon.
     * Liittyy k‰ytettyyn Javan Drag&Drop k‰sittelij‰‰n.
     * </p>
     * @return Viitteen t‰h‰n olioon.
     */
    public TimelineElement getTexti() {
        return this;
    }
    
    /**
     * Palauttaa elementin esitt‰m‰n aikav‰lin.
     * @return Elementin esitt‰m‰ aikav‰li <code>TimeSlice</code>-oliona.
     */
    public TimeSlice getTimeSlice() {
        return this.timeSlice;
    }
    
    public Reservation getReservation(){
        return null;
    }
   
    /**
     * <p>
     * Tallettaa elementin tiedot tietokantaan.
     * </p>
     */
    public void storeToDB() {}; 
    
    /**
     * Asettaa aktiivisen huoneen.
     *
     */
    public void setActiveRoom() {};
         
    /**
     * <p>
     * Muuttaa elementin kokoa vastaamaan parametrina tullutta pituutta.
     * Liittyy k‰ytettyyn Javan Drag&Drop tukeen.
     * <p/>
     * @param length Uusi pituus.
     */
    public void setTexti(Integer length) {
    
        int koko = length.intValue();
        this.setMinimumSize(new Dimension((int)(this.scale*koko), 20));
        this.setPreferredSize(new Dimension((int)(this.scale*koko), 20));
        this.setMaximumSize(new Dimension((int)(this.scale*koko), 20));
        
        this.revalidate();
    }
}
