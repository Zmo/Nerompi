package fi.helsinki.cs.nero.ui;

import java.awt.Color;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import fi.helsinki.cs.nero.data.Contract;
import fi.helsinki.cs.nero.data.Project;
import fi.helsinki.cs.nero.logic.Session;

/**
 * K�ytt�liittym�ss� esitett�v� ty�sopimus.
 */
public class UIContract extends TimelineElement {
    
    /**
     * Ty�sopimus, jonka t�m� elementti esitt��.
     */
    private Contract contract = null;
    private static Color BG_COLOR = new Color(255,138,1);
    private static Color BG_COLOR_VACATION = new Color(90,119,173);
    private static Border BORDER = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);

    /**
     * 
     * Konstruktori.
     * @param contract Ty�sopimus.
     * @param scale K�ytett�v� skaala = �p�iv�/pikseli.
     * @param session Viite sessioon.
     */
    public UIContract(Contract contract, double scale, Session session) {
        super(contract.getTimeSlice(), scale, BG_COLOR, session);
        this.setBorder(BORDER);
        
        this.contract = contract;
    
        String labelText = contract.getTitle();
        String tooltipText = labelText + "<br>" + contract.getTimeSlice();

        Project project = this.contract.getProject();
        if(project != null) {
            labelText += " / " + project;
            tooltipText += "<br>" + project;
        }
        
        if(this.contract.getWorkingPercentage() < 100) { // virkavapaus
            setBackground(BG_COLOR_VACATION);
            tooltipText += "<br>Virkavapauden hoito: " + this.contract.getWorkingPercentage() + "%";
        }
        
        this.createLabel(labelText);
        this.setToolTipText("<html>" + tooltipText + "</html>");
        
        MouseListener listener = new DragMouseAdapter();
        this.addMouseListener(listener);
        this.addMouseMotionListener((MouseMotionListener) listener);
        this.setTransferHandler(new TransferHandler("texti"));
        
    }
    
    /**
     * Palauttaa viitteen elementin esitt�m��n sopimukseen.
     * @return Viite <code>Contract</code>-olioon, jonka tilan t�m� elementti esitt��.
     */
    public Contract getContract() {
        return this.contract;
    }
    
    /**
     * Tallentaa tiedot tietokantaan.
     */
    public void storeToDB() {}
}
