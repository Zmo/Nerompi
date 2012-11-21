
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Post;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
/**
 *
 * @author rkolagus
 */
public class UusiVarausPopup extends JPopupMenu {
    Person person;
    
    
    public UusiVarausPopup(Person person, Post[] posts){
        super();
        this.setLabel("Lis‰‰ varaus");
        this.person = person;
        JMenuItem asia = new JMenuItem("Valitse huone");
        asia.setEnabled(false);
        this.add(asia);
    }
    
    private void naytaTyopisteet(){
        Post[] posts = this.person.getSession().getActiveRoom().getPosts();
        for (int a = 0; a < posts.length; a++){
            this.add(posts[a].getPostID());
        }
    }
    
}
