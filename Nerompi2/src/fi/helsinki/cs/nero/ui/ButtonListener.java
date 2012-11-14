/*
 * Created on Oct 21, 2004
 */
package fi.helsinki.cs.nero.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import fi.helsinki.cs.nero.logic.Session;

public class ButtonListener implements ActionListener {

	private Session session;

	public ButtonListener(Session session) {
		this.session = session;
	}

	public void actionPerformed(ActionEvent e) {

		//Ty�pisteen lis��minen huoneeseen.
		if (e.getActionCommand().equals("lis�� ty�piste")) {
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
	}
}