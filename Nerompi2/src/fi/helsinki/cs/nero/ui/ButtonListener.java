/*
 * Created on Oct 21, 2004
 */
package fi.helsinki.cs.nero.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import fi.helsinki.cs.nero.logic.Session;
import javax.swing.JButton;

public class ButtonListener implements ActionListener {

	private Session session;

	public ButtonListener(Session session) {
		this.session = session;
	}

	public void actionPerformed(ActionEvent e) {

		//Työpisteen lisääminen huoneeseen.
		if (e.getActionCommand().equals("lisää työpiste")) {
			this.session.createPost();
			return;
		}

		if (e.getActionCommand().equals("noPost")) {
			if (((JCheckBox) e.getSource()).isSelected()) {
				session.setFilterWithoutPost(true);
			} else {
				session.setFilterWithoutPost(false);
			}
			return;
		}

		if (e.getActionCommand().equals("contractEnding")) {
			if (((JCheckBox) e.getSource()).isSelected()) {
				session.setFilterEndingContracts(true);
			} else {
				session.setFilterEndingContracts(false);
			}
			return;
		}

		if (e.getActionCommand().equals("active")) {
			if (((JCheckBox) e.getSource()).isSelected()) {
				session.setFilterActiveEmployees(true);
			} else {
				session.setFilterActiveEmployees(false);
			}
			return;
		}
                
                if(e.getActionCommand().equals("contract")) {
                    if (((JCheckBox) e.getSource()).isSelected()) {
				session.setFilterContract(true);
			} else {
				session.setFilterContract(false);
			}
			return;
                }
                
                if(e.getActionCommand().equals("addPerson")) {
                    PersonInfoFrame infoframe = new PersonInfoFrame(this.session);
                }
                
                if(e.getActionCommand().equals("reports")) {
                    ReportsWindow reports = new ReportsWindow();
                }
                
//                if(e.getActionCommand().equals("henkilonLisatiedot")) {
//                    JButton personIdButton = (JButton)e.getSource();
//                    PersonInfoFrame infoframe = new PersonInfoFrame(personIdButton.getText());
//                }
	}
}