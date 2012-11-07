/*
 * Created on Nov 6, 2004
 */
package fi.helsinki.cs.nero.ui;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.BevelBorder;

import fi.helsinki.cs.nero.data.Project;
import fi.helsinki.cs.nero.data.TimeSlice;
import fi.helsinki.cs.nero.logic.Session;

/**
 *  @author Ville Sundberg
 */
public class SearchPanel extends JPanel implements PropertyChangeListener, ActionListener {
	private TimeSlice slice;
	private Session session;
	private Calendar calendar;
	
	private JFormattedTextField startTimeField;
	private JFormattedTextField endTimeField;
	
	/**
	 * Privaatti sisäluokka aikavälin resetoimiseksi.
	 * Tarkoitettu vain ja ainoastaan aikaväliresetointipainikkeen(tm)
	 * kuuntelemista varten.
	 */
	private class ResetListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			session.resetTimescale();
			slice = session.getFilterTimescale();
			startTimeField.setValue(slice.getStartDate());
			endTimeField.setValue(slice.getEndDate());
		}
	}
	
	public SearchPanel(Session session) {
		this.session = session;
		this.slice = session.getFilterTimescale();
		this.calendar = Calendar.getInstance();
		
		SpringLayout spring = new SpringLayout();
		this.setLayout(spring);
		
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		Dimension d_whole = new Dimension(650, 175);
		Dimension d_toprow = new Dimension(280, 55);
		Dimension d_fullrow = new Dimension(630, 55);

		this.setMinimumSize(d_whole);
		this.setPreferredSize(d_whole);
		this.setMaximumSize(d_whole);
		
		//Checkboxien kuuntelija
		ButtonListener buttonListener = new ButtonListener(this.session);		
		
		//Luodaan hakukentät ja labelit sekä panelit, joiden sisälle em. komponentit sijoitetaan
		/* aikavälipalkki */
		JPanel timePanel = new JPanel();
		timePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		timePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "aikaväli"));
				
		this.startTimeField = new JFormattedTextField();
		startTimeField.setValue(slice.getStartDate());
		startTimeField.getDocument().putProperty("name", "startTime");
		startTimeField.addPropertyChangeListener(this);
		
		JLabel lineLabel = new JLabel(" - ");
		
		this.endTimeField = new JFormattedTextField();
		endTimeField.setValue(slice.getEndDate());
		endTimeField.getDocument().putProperty("name", "endTime");
		endTimeField.addPropertyChangeListener(this);
		
		JButton resetButton = new JButton("resetoi");
		resetButton.addActionListener(new ResetListener());
		
		timePanel.add(startTimeField);
		timePanel.add(lineLabel);
		timePanel.add(endTimeField);
		timePanel.add(resetButton);
		
		/* Perusjoukkopalkki */
		JPanel perusJoukkoPanel = new JPanel();
		perusJoukkoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		perusJoukkoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "perusjoukko"));
		
		//Ilman työpistettä haku
		JLabel noPostLabel = new JLabel("ilman työpistettä ");		
		JCheckBox noPostButton = new JCheckBox();
		noPostButton.setSelected(session.getFilterWithoutPost());		
		noPostButton.setActionCommand("noPost");
		noPostButton.addActionListener(buttonListener);	
		
		//Työsopimus päättymässä
		JLabel contractEndingLabel = new JLabel("sop. päättymässä");		
		JCheckBox contractEndingButton = new JCheckBox();
		contractEndingButton.setSelected(session.getFilterEndingContracts());
		contractEndingButton.setActionCommand("contractEnding");
		contractEndingButton.addActionListener(buttonListener);

		perusJoukkoPanel.add(noPostLabel);
		perusJoukkoPanel.add(noPostButton);
		perusJoukkoPanel.add(contractEndingLabel);
		perusJoukkoPanel.add(contractEndingButton);	
		
		/* Rajauspalkki */
		JPanel restrictionPanel = new JPanel();
		restrictionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		restrictionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "rajaus"));

		// Nimihaku
		JLabel personNameLabel = new JLabel("nimi:");
		//JLabel charLimitLabel = new JLabel("(väh. 3 merkkiä)      ");
		JTextField personNameField = new JTextField(15);
		personNameField.getDocument().putProperty("name", "personName");		
		personNameField.getDocument().addDocumentListener(new FilterEventListener(this.session));
		
		// Projektilista
		JLabel projectNameLabel = new JLabel("projekti:");
		JComboBox projectList = new JComboBox(session.getProjects());
		// Lisätään alkuun tyhjä valinta
		projectList.insertItemAt("-", 0);
		projectList.setSelectedIndex(0);
		projectList.setName("projectList");
		projectList.addActionListener(this);
		
		// Sivutoimiset
		JLabel partTimeLabel = new JLabel("sivutoimiset");
		JCheckBox partTimeButton = new JCheckBox();
		partTimeButton.setSelected(session.getFilterPartTimeTeachers());
		partTimeButton.setActionCommand("partTime");
		partTimeButton.addActionListener(buttonListener);
		
		restrictionPanel.add(personNameLabel);
		restrictionPanel.add(personNameField);		
		restrictionPanel.add(projectNameLabel);
		restrictionPanel.add(projectList);
		restrictionPanel.add(partTimeLabel);
		restrictionPanel.add(partTimeButton);
		
		/* Huonepalkki */
		JPanel roomPanel = new JPanel();
		roomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		roomPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "huone"));
	
		JLabel roomLabel = new JLabel("nro:");
		JLabel charLimitLabel2 = new JLabel("(väh. 2 merkkiä)");
		
		JTextField roomField = new JTextField(10);
		roomField.getDocument().putProperty("name","room");
		roomField.getDocument().addDocumentListener(new FilterEventListener(this.session));
						
		JLabel maxPostsLabel1 = new JLabel("enintään ");
		JLabel maxPostsLabel2 = new JLabel("työpistettä/huone");
		String[] numbers = { "-", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
		JComboBox maxPosts = new JComboBox(numbers);
		maxPosts.setName("maxPosts");
		maxPosts.addActionListener(this);
				
		JLabel minFreeLabel1 = new JLabel("   etsi");
		JLabel minFreeLabel2 = new JLabel("vapaata työpistettä    ");
		String[] numbers2 = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
		JComboBox minFree = new JComboBox(numbers2);
		minFree.setName("minFree");
		minFree.addActionListener(this);
		
		// lisätään huonehakukenttä
		roomPanel.add(roomLabel);
		roomPanel.add(roomField);
		//roomPanel.add(charLimitLabel2);

		// lisätään "ainakin n vapaata" valinta
		roomPanel.add(minFreeLabel1);
		roomPanel.add(minFree);
		roomPanel.add(minFreeLabel2);
		
		// lisätään "korkeintaan n työpistettä huoneessa" valinta
		roomPanel.add(maxPostsLabel1);
		roomPanel.add(maxPosts);
		roomPanel.add(maxPostsLabel2);
						
		TrashCan trashCan = new TrashCan(this.session);

		timePanel.setMinimumSize(d_toprow);
		timePanel.setPreferredSize(d_toprow);
		//timePanel.setMaximumSize(d_toprow);
		
		perusJoukkoPanel.setMinimumSize(d_toprow);
		perusJoukkoPanel.setPreferredSize(d_toprow);
		//perusJoukkoPanel.setMaximumSize(d_toprow);
		
		restrictionPanel.setMinimumSize(d_fullrow);
		restrictionPanel.setPreferredSize(d_fullrow);
		//restrictionPanel.setMaximumSize(d_fullrow);
		
		roomPanel.setMinimumSize(d_fullrow);
		roomPanel.setPreferredSize(d_fullrow);	
		//roomPanel.setMaximumSize(d_fullrow);
		
		this.add(timePanel);
		this.add(perusJoukkoPanel);
		this.add(roomPanel);
		this.add(restrictionPanel);
		this.add(trashCan);

		// SpringLayout-taikuudet
		// aikaväli vasempaan reunaan ylös
		spring.putConstraint(SpringLayout.WEST, timePanel, 5, SpringLayout.WEST, this);
		
		// perusjoukko aikavälin oikealle puolelle
		spring.putConstraint(SpringLayout.WEST, perusJoukkoPanel, 5, SpringLayout.EAST, timePanel);

		// rajaus, koko rivi aikavälin alapuolella
		spring.putConstraint(SpringLayout.WEST, restrictionPanel, 5, SpringLayout.WEST, this);
		spring.putConstraint(SpringLayout.NORTH, restrictionPanel, 2, SpringLayout.SOUTH, perusJoukkoPanel);

		// huonevalinta, koko rivi rajausboksin alapuolella
		spring.putConstraint(SpringLayout.WEST, roomPanel, 5, SpringLayout.WEST, this);
		spring.putConstraint(SpringLayout.NORTH, roomPanel, 2, SpringLayout.SOUTH, restrictionPanel);

		// roskis oikeaan ylänurkkaan
		spring.putConstraint(SpringLayout.EAST, trashCan, -5, SpringLayout.EAST, this);
	}
	
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 * kuuntelija FormattedTextFieldeille
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		
		if(((JFormattedTextField)arg0.getSource()).getDocument().getProperty("name").equals("startTime")){
			if(((Date)((JFormattedTextField)arg0.getSource()).getValue()).after(slice.getEndDate())){
				slice.setStartDate((Date)((JFormattedTextField)arg0.getSource()).getValue());
				calendar.setTime(slice.getStartDate());
				calendar.add(Calendar.MONTH, 1);
				slice.setEndDate(calendar.getTime());
				endTimeField.setValue(calendar.getTime());
				session.setFilterTimescale(slice);
			}
			if(((Date)startTimeField.getValue()).equals(slice.getStartDate())&&
					((Date)endTimeField.getValue()).equals(slice.getEndDate())){
				//System.out.println("ei turhaa päivitystä");				
			} else {
				slice.setStartDate((Date)((JFormattedTextField)arg0.getSource()).getValue());
				session.setFilterTimescale(slice);				
			}	
		}
		else if (((JFormattedTextField)arg0.getSource()).getDocument().getProperty("name").equals("endTime")){
			if(((Date)((JFormattedTextField)arg0.getSource()).getValue()).before(slice.getStartDate())){
				slice.setEndDate((Date)((JFormattedTextField)arg0.getSource()).getValue());
				calendar.setTime(slice.getEndDate());
				calendar.add(Calendar.MONTH, -1);
				slice.setStartDate(calendar.getTime());
				startTimeField.setValue(calendar.getTime());
				session.setFilterTimescale(slice);
				
			}
			if(((Date)startTimeField.getValue()).equals(slice.getStartDate())&&
					((Date)endTimeField.getValue()).equals(slice.getEndDate())){
			} else {
				slice.setEndDate((Date)((JFormattedTextField)arg0.getSource()).getValue());
				session.setFilterTimescale(slice);
			}	
		}
		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * ComboBoxien kuuntelija
	 */
	public void actionPerformed(ActionEvent arg0) {
		JComboBox cb = (JComboBox)arg0.getSource();
		if(cb.getName()=="projectList"){			
			if(cb.getSelectedIndex()==0 || cb.getSelectedIndex()==-1){ //"-" valittu, tai valinta ei ole listasta			
				session.setFilterProject(null);
			}
			else{
				session.setFilterProject((Project)cb.getSelectedItem());
			}
			
		}	
		else if(cb.getName()=="maxPosts"){			
			if(cb.getSelectedIndex()==0 || cb.getSelectedIndex()==-1){ //"-" valittu, tai valinta ei ole listasta			
				session.setFilterMaxPosts(-1);
			}
			else{
				session.setFilterMaxPosts(Integer.parseInt((String)cb.getSelectedItem()));   
			}
		}	
		else if(cb.getName()=="minFree"){			
			if(cb.getSelectedIndex()==0 || cb.getSelectedIndex()==-1){ //"-" valittu, tai valinta ei ole listasta			
				session.setFilterFreePosts(1);
			}
			else{
				session.setFilterFreePosts(Integer.parseInt((String)cb.getSelectedItem()));   
			}
		}			
		
		
	}
	
	
}
