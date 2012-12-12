/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

import fi.helsinki.cs.nero.data.Kannykka;
import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.event.NeroObserverTypes;
import fi.helsinki.cs.nero.logic.Session;
import java.sql.SQLException;
import java.util.HashMap;

/**
 *
 * @author ssinisal
 */
public class PersonInfoFrame extends javax.swing.JFrame {

    /**
     * Creates new form PersonInfoFrame
     */
    private Person person;
    private boolean newPerson;
    private Session session;
    private HashMap<String, String> henkiloHash;
    private Kannykka kannykka = null;
    
    public PersonInfoFrame(Session session, Person person) {
        initComponents();
        this.person = person;
        this.newPerson = false;
        this.session = session;

        htunnusField.setText(person.getPersonID());
        etunimiField.setText(person.getEtunimi());
        sukunimiField.setText(person.getSukunimi());
        huoneLabel.setText(person.getRoom());
        hetuField.setText(person.getHetu());
        oppiarvoField.setText(person.getOppiarvo());
        titteliField.setText(person.getTitteli())
                ;
        if (person.getWorkPhone() != null) {
            if (person.getWorkPhone().startsWith("(09) 191")) {
                tyopuhelinLabel.setText(person.getWorkPhone());
            } else {
                tyopuhelinLabel.setText("(09) 191"+person.getWorkPhone());
            }
        }
        kotipuhelinField.setText(person.getHomePhone());
        kannykkaField.setText(person.getKannykka());
        omistajaField.setText(this.session.getKannykka(person.getPersonID()));
        katuosoiteField.setText(person.getAddress());
        postinumeroField.setText(person.getPostnumber());
        postitoimipaikkaField.setText(person.getPostitoimiPaikka());        
        sahkopostiField.setText(person.getSahkoposti());
        hallinnollinenkommenttiField.setText(person.getHallinnollinenKommentti());        
        ktunnusField.setText(person.getkTunnus());
        postilokeroField.setText(person.getPostilokeroHuone());
        //omistajaField.setText(this.session.getKannykat(person);
        
        if (person.getActivity().equals("K")) {
            aktiivinenButton.setSelected(true);
        } else {
            aktiivinenButton.setSelected(false);
        }
        if (person.getHyTyosuhde().equals("K")) {
            tyosuhdeButton.setSelected(true);
        } else {
            tyosuhdeButton.setSelected(false);
        }
        if (person.getHyPuhelinluettelossa().equals("K")) {
            puhelinluettelossaButton.setSelected(true);
        } else {
            puhelinluettelossaButton.setSelected(false);
        }
     
        this.setSize(600, 600);
        this.setResizable(false);
        MoreField.setVisible(false);
        this.setVisible(true);
    }
    
    public PersonInfoFrame(Session session) {
        initComponents();
        this.newPerson = true;
        this.session = session;
        
        puhelinluettelossaButton.setSelected(false);
        tyosuhdeButton.setSelected(false);
        aktiivinenButton.setSelected(false);
        
        this.setSize(600, 600);
        this.setResizable(false);
        MoreField.setVisible(false);
        this.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        etunimiField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        ktunnusField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        saveButton = new javax.swing.JButton();
        postilokeroField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        MoreButton = new javax.swing.JToggleButton();
        CancelButton = new javax.swing.JButton();
        MoreField = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        oppiarvoField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        katuosoiteField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        postinumeroField = new javax.swing.JTextField();
        postitoimipaikkaField = new javax.swing.JTextField();
        sahkopostiField = new javax.swing.JTextField();
        hallinnollinenkommenttiField = new javax.swing.JTextField();
        htunnusField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        hetuField = new javax.swing.JTextField();
        sukunimiField = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        titteliField = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        kotipuhelinField = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        errorMessageLabel = new javax.swing.JLabel();
        huoneLabel = new javax.swing.JLabel();
        aktiivinenButton = new javax.swing.JRadioButton();
        kannykkaField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        omistajaField = new javax.swing.JTextField();
        tyopuhelinLabel = new javax.swing.JLabel();
        tyosuhdeButton = new javax.swing.JRadioButton();
        puhelinluettelossaButton = new javax.swing.JRadioButton();
        lisaapoistaButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("tietoruutu");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFocusTraversalPolicyProvider(true);

        jLabel1.setText("Etunimi");

        jLabel2.setText("Huone:");

        jLabel4.setText("K�ytt�j�tunnus");

        saveButton.setText("Tallenna");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        jLabel5.setText("Postilokerohuone");

        MoreButton.setText("N�yt� lis�tiedot");
        MoreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MoreButtonActionPerformed(evt);
            }
        });

        CancelButton.setText("Poistu");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });

        jLabel6.setText("Oppiarvo");

        jLabel8.setText("Hetu");

        jLabel9.setText("Katuosoite");

        jLabel10.setText("Postinumero");

        htunnusField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                htunnusFieldActionPerformed(evt);
            }
        });

        jLabel11.setText("H_tunnus");

        jLabel13.setText("Hallinnollinen kommentti");

        jLabel14.setText("S�hk�posti");

        jLabel15.setText("Postitoimipaikka");

        javax.swing.GroupLayout MoreFieldLayout = new javax.swing.GroupLayout(MoreField);
        MoreField.setLayout(MoreFieldLayout);
        MoreFieldLayout.setHorizontalGroup(
            MoreFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MoreFieldLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MoreFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MoreFieldLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addContainerGap(72, Short.MAX_VALUE))
                    .addGroup(MoreFieldLayout.createSequentialGroup()
                        .addGroup(MoreFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(MoreFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(hallinnollinenkommenttiField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(postinumeroField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(hetuField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(MoreFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(MoreFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(postitoimipaikkaField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                                        .addComponent(htunnusField, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(oppiarvoField, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(katuosoiteField, javax.swing.GroupLayout.Alignment.LEADING))
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel14)
                                    .addComponent(sahkopostiField, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel11)
                            .addComponent(jLabel13))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        MoreFieldLayout.setVerticalGroup(
            MoreFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MoreFieldLayout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(oppiarvoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hetuField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(htunnusField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(katuosoiteField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel15)
                .addGap(9, 9, 9)
                .addComponent(postitoimipaikkaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(postinumeroField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sahkopostiField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hallinnollinenkommenttiField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel16.setText("Sukunimi");

        jLabel17.setText("Titteli");

        jLabel18.setText("K�nnykk�");

        jLabel20.setText("Kotipuhelin");

        jLabel21.setText("Ty�puhelin:");

        errorMessageLabel.setForeground(new java.awt.Color(255, 0, 0));
        errorMessageLabel.setToolTipText("");

        aktiivinenButton.setText("Aktiivinen");

        jLabel3.setText("K�nnyk�n omistaja");

        tyosuhdeButton.setText("HY:n Ty�suhteessa");
        tyosuhdeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tyosuhdeButtonActionPerformed(evt);
            }
        });

        puhelinluettelossaButton.setText("HY:n Puhelinluettelossa");
        puhelinluettelossaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                puhelinluettelossaButtonActionPerformed(evt);
            }
        });

        lisaapoistaButton.setText("Varaa ty�numero");
        lisaapoistaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lisaapoistaButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(CancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(MoreButton, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(errorMessageLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4)
                                .addComponent(jLabel17)
                                .addComponent(jLabel18)
                                .addComponent(jLabel3)
                                .addComponent(jLabel20)
                                .addComponent(kotipuhelinField, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(omistajaField, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(kannykkaField, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel16)
                                        .addComponent(tyopuhelinLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(titteliField, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(ktunnusField, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1)
                                        .addComponent(etunimiField, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(sukunimiField, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel21)
                                        .addComponent(jLabel5)
                                        .addComponent(postilokeroField, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lisaapoistaButton)
                                        .addComponent(jLabel2)
                                        .addComponent(huoneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(aktiivinenButton)
                                        .addComponent(tyosuhdeButton)
                                        .addComponent(puhelinluettelossaButton))))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(MoreField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(146, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(etunimiField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sukunimiField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(huoneLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(36, 36, 36)
                                .addComponent(aktiivinenButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ktunnusField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tyosuhdeButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(titteliField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(puhelinluettelossaButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(postilokeroField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(kannykkaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(omistajaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(kotipuhelinField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(tyopuhelinLabel))
                            .addComponent(lisaapoistaButton)))
                    .addComponent(MoreField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addComponent(errorMessageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(CancelButton)
                    .addComponent(MoreButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * Tallenna-nappulaa painettaessa tallettaa henkil�n ja kannyk�n tiedot tietokantaan
     * @param evt 
     */
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        // TODO add your handling code here:
        errorMessageLabel.setText("");
        henkiloHash = new HashMap<String, String>();
        
        henkiloHash.put("htunnus", htunnusField.getText());
        henkiloHash.put("kokonimi", (etunimiField.getText()+" "+sukunimiField.getText()));
        henkiloHash.put("etunimet", etunimiField.getText());
        henkiloHash.put("sukunimi", sukunimiField.getText());
        henkiloHash.put("huone_nro", huoneLabel.getText());
        henkiloHash.put("kutsumanimi", "");
        
        if (aktiivinenButton.isSelected()) {
            henkiloHash.put("aktiivisuus", "K");
        } else {
            henkiloHash.put("aktiivisuus", "E");
        }       
        henkiloHash.put("hetu", hetuField.getText());
        henkiloHash.put("oppiarvo", oppiarvoField.getText());
        henkiloHash.put("titteli", titteliField.getText());
        henkiloHash.put("puhelin_tyo", tyopuhelinLabel.getText());
        henkiloHash.put("puhelin_koti", kotipuhelinField.getText());
        
        if (!kannykkaField.getText().equals("") && !omistajaField.getText().equals("")) {
            henkiloHash.put("kannykka", kannykkaField.getText());
            kannykka = new Kannykka(htunnusField.getText(), kannykkaField.getText(), omistajaField.getText());
            
        }
        henkiloHash.put("katuosoite", katuosoiteField.getText());
        henkiloHash.put("postinro", postinumeroField.getText());
        henkiloHash.put("postitoimipaikka", postitoimipaikkaField.getText());
        henkiloHash.put("sahkopostiosoite", sahkopostiField.getText());
        henkiloHash.put("hallinnollinen_kommentti", hallinnollinenkommenttiField.getText());
        henkiloHash.put("ktunnus", ktunnusField.getText());
        henkiloHash.put("postilokerohuone", postilokeroField.getText());
        
        if (puhelinluettelossaButton.isSelected()) {
            henkiloHash.put("hy_puhelinluettelossa", "K");
        } else {
            henkiloHash.put("hy_puhelinluettelossa", "E");
        }     
        if (tyosuhdeButton.isSelected()) {
            henkiloHash.put("hy_tyosuhde", "K");
        } else {
            henkiloHash.put("hy_tyosuhde", "E");
        }       
        
        this.person = new Person(this.session, henkiloHash, null, null);
        
        //nero dblt� exeptiot handuun, sy�tteen tarkistus
        try {
            
        if(this.newPerson) {
            person.getSession().saveNewPerson(this.person);
            if (kannykka != null) {
                person.getSession().addKannykka(kannykka);
            }
            errorMessageLabel.setText("Henkil� lis�tty tietokantaan");
        } else {
            person.getSession().updatePerson(person);
            if (kannykka != null) {
                person.getSession().addKannykka(kannykka);
            }
            errorMessageLabel.setText("Muutokset tallennettu tietokantaan");
        }
        
        } catch (SQLException e) { 
            errorMessageLabel.setText("Tietokantavirhe: " + e.getMessage());
        }
        
        //this.dispose();
    }//GEN-LAST:event_saveButtonActionPerformed
    /**
     * Lis�tietonapin on/off asento tuo tai piilottaa lis�tiedot p��ikkunasta
     * @param evt 
     */
    private void MoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MoreButtonActionPerformed
        if (MoreField.isVisible()) {
            //this.setSize(610, 400);
            MoreField.setVisible(false);
        } else {
            //this.setSize(900, 400);
            MoreField.setVisible(true);
        }
    }//GEN-LAST:event_MoreButtonActionPerformed

    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
        
        this.dispose();
    }//GEN-LAST:event_CancelButtonActionPerformed

    private void lisaapoistaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lisaapoistaButtonActionPerformed
        if (this.newPerson) {
            errorMessageLabel.setText("Luo ensin henkil�");
            return;
        }
        AltPhonenumberDialog apd = new AltPhonenumberDialog(this, true, this.session, this.person);
    }//GEN-LAST:event_lisaapoistaButtonActionPerformed

    private void htunnusFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_htunnusFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_htunnusFieldActionPerformed

    private void puhelinluettelossaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_puhelinluettelossaButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_puhelinluettelossaButtonActionPerformed

    private void tyosuhdeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tyosuhdeButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tyosuhdeButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PersonInfoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PersonInfoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PersonInfoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PersonInfoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new PersonInfoFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CancelButton;
    private javax.swing.JToggleButton MoreButton;
    private javax.swing.JPanel MoreField;
    private javax.swing.JRadioButton aktiivinenButton;
    private javax.swing.JLabel errorMessageLabel;
    private javax.swing.JTextField etunimiField;
    private javax.swing.JTextField hallinnollinenkommenttiField;
    private javax.swing.JTextField hetuField;
    private javax.swing.JTextField htunnusField;
    private javax.swing.JLabel huoneLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField kannykkaField;
    private javax.swing.JTextField katuosoiteField;
    private javax.swing.JTextField kotipuhelinField;
    private javax.swing.JTextField ktunnusField;
    private javax.swing.JButton lisaapoistaButton;
    private javax.swing.JTextField omistajaField;
    private javax.swing.JTextField oppiarvoField;
    private javax.swing.JTextField postilokeroField;
    private javax.swing.JTextField postinumeroField;
    private javax.swing.JTextField postitoimipaikkaField;
    private javax.swing.JRadioButton puhelinluettelossaButton;
    private javax.swing.JTextField sahkopostiField;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField sukunimiField;
    private javax.swing.JTextField titteliField;
    private javax.swing.JLabel tyopuhelinLabel;
    private javax.swing.JRadioButton tyosuhdeButton;
    // End of variables declaration//GEN-END:variables
}
