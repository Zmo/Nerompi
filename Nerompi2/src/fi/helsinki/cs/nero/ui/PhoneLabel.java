/*
 * Created on 28.11.2004
 *
 */
package fi.helsinki.cs.nero.ui;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import fi.helsinki.cs.nero.NeroApplication;
import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Post;
import fi.helsinki.cs.nero.logic.Session;

/**
 * @author Ville Sundberg
 */
public class PhoneLabel extends JLabel {
    private Post post;
	private Session session;
	
	public PhoneLabel(Session s, Post p){
		super(new ImageIcon(NeroApplication.getProperty("img_phone")));
		this.post = p;
		this.session = s;
	}

	public Post getPost(){
		return this.post;
	}
	public Session getSession(){
		return this.session;
	}
}

