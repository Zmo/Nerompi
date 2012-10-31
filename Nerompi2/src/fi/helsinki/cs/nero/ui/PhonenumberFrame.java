/*
 * Created on 19.11.2004
 *
 */
package fi.helsinki.cs.nero.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fi.helsinki.cs.nero.data.PhoneNumber;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.event.NeroObserver;
import fi.helsinki.cs.nero.event.NeroObserverTypes;
import fi.helsinki.cs.nero.logic.Session;

/**
 * @author Ville Sundberg
 *
 */
public class PhonenumberFrame extends JDialog
	implements ListSelectionListener, ActionListener, NeroObserver
{
	private Session session;
	private Post post;
	private JList postNumbers;
	private Vector postNumbersVector;
	private JList allNumbers;
	private Vector allNumbersVector;
	
	public PhonenumberFrame(Session session, JFrame mainFrame, Post post){
		super(mainFrame, "Valitse numerot...", true);
		this.session = session;
		this.post = post;
        
		// kuunnellaan waitstateja
		session.registerObserver(NeroObserverTypes.CURSORCHANGE, this);
		
        //kaikki mahdolliset puhelinnumerot listaan ja lista scrollpaneliin	
	    allNumbersVector = new Vector();
	    PhoneNumber[] numberArray = session.getAllPhoneNumbers();
	    for(int i=0; i < numberArray.length; i++){
    	    allNumbersVector.add(numberArray[i]);
	    }	
	    
		this.allNumbers = new JList(allNumbersVector);		
		allNumbers.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		allNumbers.setName("allNumbers");
		allNumbers.addListSelectionListener(this);
		
		JScrollPane listScroller = new JScrollPane(allNumbers);
		listScroller.setPreferredSize(new Dimension(250, 100));
		
		JPanel leftPanel = new JPanel();
		leftPanel.setPreferredSize(new Dimension(260, 200));
		leftPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		JLabel leftHeader = new JLabel("kaikki puhelinnumerot:");
		
		leftPanel.add(leftHeader);
		leftPanel.add(listScroller);

	    postNumbersVector = new Vector();
		PhoneNumber[] numberArray2 = post.getPhonenumbers(); 

		for(int i=0; i < numberArray2.length; i++){
    	    postNumbersVector.add(numberArray2[i]);
	    }	
		
		this.postNumbers = new JList(postNumbersVector);		
		postNumbers.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		postNumbers.setName("postNumbers");
		postNumbers.addListSelectionListener(this);
		
		JScrollPane listScroller2 = new JScrollPane(postNumbers);
		listScroller2.setPreferredSize(new Dimension(250, 150));
		
		JPanel rightPanel = new JPanel();
		rightPanel.setPreferredSize(new Dimension(260, 200));
		rightPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		JLabel rightHeader = new JLabel("työpisteeseen "+ post.toString()+" liitetyt numerot:");
		
		rightPanel.add(rightHeader);
		rightPanel.add(listScroller2);
		
		//buttonit ja  niille paneli
		JButton releaseButton = new JButton("<- vapauta -");
		releaseButton.setName("release");
		releaseButton.addActionListener(this);
		releaseButton.setPreferredSize(new Dimension(100, 30));
		
		JButton reserveButton = new JButton("- varaa ->");
		reserveButton.setName("reserve");
		reserveButton.addActionListener(this);
		reserveButton.setPreferredSize(new Dimension(100, 30));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(100,100));
		buttonPanel.add(releaseButton);
		buttonPanel.add(reserveButton);	
		
		JButton closeButton = new JButton("Sulje");
		closeButton.setName("close");
		closeButton.addActionListener(this);
		closeButton.setPreferredSize(new Dimension(100, 30));
		
		this.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));
		this.getContentPane().add(leftPanel);
		this.getContentPane().add(buttonPanel);
		this.getContentPane().add(rightPanel);
		this.getContentPane().add(closeButton);
		
		this.setSize(650, 300);
		this.setVisible(true);	
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	//ei käytössä
	public void valueChanged(ListSelectionEvent arg0) {
		if(((JList)arg0.getSource()).getName()=="allNumbers"){
			
		}	
		else if(((JList)arg0.getSource()).getName()=="postNumbers"){
			
		}
		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		if(((JButton)arg0.getSource()).getName()== "reserve"){
			if(allNumbers.getSelectedValue()!=null){
				PhoneNumber p = (PhoneNumber)allNumbers.getSelectedValue();				
				session.addPhoneNumber(post, p);
				
				//tyhjennetään vektorit
				allNumbersVector.removeAllElements();
				postNumbersVector.removeAllElements();
				
				//päivitetään kaikki tiedot sessiolta (eli kannasta asti)
				PhoneNumber[] pn = session.getPhoneNumbers(post);
				for(int i=0; i < pn.length; i++){
		    	    postNumbersVector.add(pn[i]);
			    }	
			    PhoneNumber[] pn2 = session.getAllPhoneNumbers();
			    for(int i=0; i < pn2.length; i++){
		    	    allNumbersVector.add(pn2[i]);
			    }	
								
				allNumbers.setListData(allNumbersVector);				
				postNumbers.setListData(postNumbersVector);
			}
		}
		else if(((JButton)arg0.getSource()).getName()== "release"){
			if(postNumbers.getSelectedValue()!=null){
				PhoneNumber p = (PhoneNumber)postNumbers.getSelectedValue();
				session.deletePhoneNumber(p);

				//tyhjennetään vektorit
				allNumbersVector.removeAllElements();
				postNumbersVector.removeAllElements();
				
				//päivitetään kaikki tiedot sessiolta (eli kannasta asti)
				PhoneNumber[] pn = session.getPhoneNumbers(post);
				for(int i=0; i < pn.length; i++){
		    	    postNumbersVector.add(pn[i]);
			    }	
			    PhoneNumber[] pn2 = session.getAllPhoneNumbers();
			    for(int i=0; i < pn2.length; i++){
		    	    allNumbersVector.add(pn2[i]);
			    }	
				
				allNumbers.setListData(allNumbersVector);
				postNumbers.setListData(postNumbersVector);		
			}
		}
		else if (((JButton)arg0.getSource()).getName()== "close"){
		    setVisible(false);
            dispose();
		}
		
	}

	public void updateObserved(int type) {
		switch(type) {
		case NeroObserverTypes.CURSORCHANGE: 
			this.setCursor(Cursor.getPredefinedCursor(this.session.getCursorType()));
			break;
		default:
			// unknown event, shouldn't be listening...
			break;
		}
	}
	
}
