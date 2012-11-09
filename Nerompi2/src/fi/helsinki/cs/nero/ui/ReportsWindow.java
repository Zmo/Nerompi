/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.ui;

/**
 *
 * @author lpesola
 */
import fi.helsinki.cs.nero.data.Person;
import fi.helsinki.cs.nero.data.Room;
import fi.helsinki.cs.nero.db.NeroDatabase;
import fi.helsinki.cs.nero.logic.Session;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.ButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class ReportsWindow extends javax.swing.JFrame {
    
    private Session session;
    private ArrayList<JComponent> roomComponents;
    private ArrayList<JComponent> peopleComponents;
    private ArrayList<JComponent> lockerComponents;
    private DefaultTableModel roomTableModel;
    private DefaultTableModel peopleTableModel;
    private DefaultTableModel defaultLockerTable;
    private DefaultTableColumnModel roomColumnModel;
    private DefaultTableColumnModel peopleColumnModel;
    private DefaultTableColumnModel lockerColumnmodel;
    private HashMap<String, TableColumn> roomColumns;
    private HashMap<String, TableColumn> peopleColumns;
    private ButtonModel previouslySelectedButton;
    private ButtonModel roomButtonModel;
    private ButtonModel peopleButtonModel;
    private ButtonModel lockerButtonModel;
    private Room[] rooms;
    private Person[] people;
    // combobox models not used yet
    private DefaultComboBoxModel wingsModel;
    private DefaultComboBoxModel floorsModel;
    private int[] floors = new int[]{1, 2, 3};
    private char[] wings = new char[]{'A', 'B', 'C', 'D'};

    /**
     * Creates new form Reports
     */
    public ReportsWindow() {

        // koodia testausta varten, voi poistaa my�hemmin
        // toimiva sessio
        session = new Session();
        NeroDatabase db = new NeroDatabase(session,
                "oracle.jdbc.driver.OracleDriver",
                "jdbc:oracle:thin:@bodbacka:1521:test",
                "tk_testi", "tapaus2");
        session.setDatabase(db);
        // testikoodin loppu
        rooms = session.getRooms();
        people = session.getFilteredPeople();
        
        initComponents();
        initContainerData();
        initModels();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        viewButtons = new javax.swing.ButtonGroup();
        checkboxContainer = new javax.swing.JPanel();
        roomAttributes = new javax.swing.JPanel();
        showPostCount = new javax.swing.JCheckBox();
        showWing = new javax.swing.JCheckBox();
        showRoomName = new javax.swing.JCheckBox();
        showFloor = new javax.swing.JCheckBox();
        personAttributes = new javax.swing.JPanel();
        shoRoomAndPost = new javax.swing.JCheckBox();
        showEmail = new javax.swing.JCheckBox();
        showContracts = new javax.swing.JCheckBox();
        showPhone = new javax.swing.JCheckBox();
        lockerAttributes = new javax.swing.JPanel();
        showRoom = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        restrictionsContainer = new javax.swing.JPanel();
        wing = new javax.swing.JLabel();
        wingDropdown = new javax.swing.JComboBox();
        floor = new javax.swing.JLabel();
        floorDropdown = new javax.swing.JComboBox();
        lockerDropdown = new javax.swing.JComboBox();
        rajauksetHeader = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        restrictByName = new javax.swing.JTextField();
        peopleButton = new javax.swing.JRadioButton();
        saveButton = new javax.swing.JButton();
        tableContainer = new javax.swing.JScrollPane();
        Data = new javax.swing.JTable();
        lockerButton = new javax.swing.JRadioButton();
        roomButton = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        showPostCount.setText("Ty�pisteiden lkm");
        showPostCount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showPostCountActionPerformed(evt);
            }
        });

        showWing.setText("Siipi");
        showWing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showWingActionPerformed(evt);
            }
        });

        showRoomName.setText("Huoneen nimi");
        showRoomName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                showRoomNameMouseReleased(evt);
            }
        });

        showFloor.setText("Kerros");
        showFloor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                showFloorMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout roomAttributesLayout = new javax.swing.GroupLayout(roomAttributes);
        roomAttributes.setLayout(roomAttributesLayout);
        roomAttributesLayout.setHorizontalGroup(
            roomAttributesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roomAttributesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(roomAttributesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(showWing)
                    .addComponent(showFloor)
                    .addComponent(showPostCount)
                    .addComponent(showRoomName))
                .addContainerGap())
        );
        roomAttributesLayout.setVerticalGroup(
            roomAttributesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roomAttributesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(showFloor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showWing)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showPostCount)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showRoomName)
                .addContainerGap())
        );

        shoRoomAndPost.setText("Huone/ty�piste");

        showEmail.setText("S�hk�posti");

        showContracts.setText("Sopimukset");
        showContracts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                showContractsMouseReleased(evt);
            }
        });

        showPhone.setText("Puhelinnumero");

        javax.swing.GroupLayout personAttributesLayout = new javax.swing.GroupLayout(personAttributes);
        personAttributes.setLayout(personAttributesLayout);
        personAttributesLayout.setHorizontalGroup(
            personAttributesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(personAttributesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(personAttributesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(shoRoomAndPost)
                    .addComponent(showPhone)
                    .addComponent(showContracts)
                    .addComponent(showEmail))
                .addContainerGap())
        );
        personAttributesLayout.setVerticalGroup(
            personAttributesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(personAttributesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(shoRoomAndPost)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showPhone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showContracts)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showEmail)
                .addContainerGap())
        );

        showRoom.setText("Ty�huone");
        showRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showRoomActionPerformed(evt);
            }
        });

        jCheckBox2.setText("Puhelinnumero");

        javax.swing.GroupLayout lockerAttributesLayout = new javax.swing.GroupLayout(lockerAttributes);
        lockerAttributes.setLayout(lockerAttributesLayout);
        lockerAttributesLayout.setHorizontalGroup(
            lockerAttributesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lockerAttributesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lockerAttributesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(showRoom)
                    .addComponent(jCheckBox2))
                .addContainerGap())
        );
        lockerAttributesLayout.setVerticalGroup(
            lockerAttributesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lockerAttributesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(showRoom)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox2)
                .addContainerGap())
        );

        wing.setText("Siipi");

        wingDropdown.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "A", "B", "C", "D" }));
        wingDropdown.setToolTipText("");
        wingDropdown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wingDropdownActionPerformed(evt);
            }
        });

        floor.setText("Kerros");

        floorDropdown.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3" }));
        floorDropdown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                floorDropdownActionPerformed(evt);
            }
        });

        lockerDropdown.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Kaikki", "Lokerottomat", "Lokerolliset" }));

        rajauksetHeader.setText("Rajaukset");

        jLabel1.setText("N�yt�");

        jLabel2.setText("Varauksen loppu");

        jLabel3.setText("Nimi");

        javax.swing.GroupLayout restrictionsContainerLayout = new javax.swing.GroupLayout(restrictionsContainer);
        restrictionsContainer.setLayout(restrictionsContainerLayout);
        restrictionsContainerLayout.setHorizontalGroup(
            restrictionsContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(restrictionsContainerLayout.createSequentialGroup()
                .addComponent(rajauksetHeader)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(restrictionsContainerLayout.createSequentialGroup()
                .addGap(0, 12, Short.MAX_VALUE)
                .addGroup(restrictionsContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(floor)
                    .addComponent(wing))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(restrictionsContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(wingDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(floorDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addGroup(restrictionsContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(restrictionsContainerLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 515, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lockerDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(76, 76, 76))
                    .addGroup(restrictionsContainerLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(restrictByName, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        restrictionsContainerLayout.setVerticalGroup(
            restrictionsContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(restrictionsContainerLayout.createSequentialGroup()
                .addComponent(rajauksetHeader)
                .addGroup(restrictionsContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(restrictionsContainerLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(restrictionsContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lockerDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, restrictionsContainerLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(restrictionsContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(wingDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(wing)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(restrictionsContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(floorDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(floor)
                            .addComponent(jLabel3)
                            .addComponent(restrictByName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(49, 49, 49))))
        );

        javax.swing.GroupLayout checkboxContainerLayout = new javax.swing.GroupLayout(checkboxContainer);
        checkboxContainer.setLayout(checkboxContainerLayout);
        checkboxContainerLayout.setHorizontalGroup(
            checkboxContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(checkboxContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(checkboxContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(checkboxContainerLayout.createSequentialGroup()
                        .addComponent(restrictionsContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(checkboxContainerLayout.createSequentialGroup()
                        .addComponent(personAttributes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(168, 168, 168)
                        .addComponent(roomAttributes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lockerAttributes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(115, 115, 115))))
        );
        checkboxContainerLayout.setVerticalGroup(
            checkboxContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(checkboxContainerLayout.createSequentialGroup()
                .addGroup(checkboxContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(personAttributes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roomAttributes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(checkboxContainerLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lockerAttributes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(38, 38, 38)
                .addComponent(restrictionsContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        viewButtons.add(peopleButton);
        peopleButton.setText("Henkil�t");
        peopleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                peopleButtonMouseReleased(evt);
            }
        });

        saveButton.setText("Tallenna raportti");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        Data.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tableContainer.setViewportView(Data);

        viewButtons.add(lockerButton);
        lockerButton.setText("Postilokerot");
        lockerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lockerButtonMouseReleased(evt);
            }
        });

        viewButtons.add(roomButton);
        roomButton.setText("Huoneet");
        roomButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                roomButtonMouseReleased(evt);
            }
        });
        roomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roomButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(saveButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(checkboxContainer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableContainer)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(peopleButton)
                .addGap(233, 233, 233)
                .addComponent(roomButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lockerButton)
                .addGap(160, 160, 160))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(roomButton)
                    .addComponent(peopleButton)
                    .addComponent(lockerButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(checkboxContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 769, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(saveButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void emptyContainer(JComponent comp) {
        comp.removeAll();
        comp.revalidate();
        comp.repaint();
    }
    
    private void redrawContainer(JComponent container) {
        container.revalidate();
        container.repaint();
    }
    
    private void insertContents(Collection<JComponent> col, JPanel panel) {
        for (JComponent j : col) {
            panel.add(j);
        }
        panel.revalidate();
        panel.repaint();
    }
    private void showPostCountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showPostCountActionPerformed
        if (showPostCount.isSelected()) {
            roomColumnModel.addColumn(roomColumns.get("postCount"));
            for (int i = 0; i < rooms.length; i++) {
                Object[] rowData = {rooms[i].getRoomNumber(), rooms[i].getFloor(),
                    rooms[i].getPosts().length};
                roomTableModel.insertRow(i, rowData);
            }
        } else {
            roomColumnModel.removeColumn(roomColumns.get("postCount"));
        }
    }//GEN-LAST:event_showPostCountActionPerformed
    
    private void roomButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_roomButtonMouseReleased
        
        if (previouslySelectedButton == null
                || previouslySelectedButton != roomButtonModel) {
            roomColumnModel.addColumn(roomColumns.get("roomNumber"));
            roomColumnModel.addColumn(roomColumns.get("floor"));
            Data.setColumnModel(roomColumnModel);
            Data.setModel(roomTableModel);
            for (int i = 0; i < rooms.length; i++) {
                Object[] rowData = {rooms[i].getRoomNumber(), rooms[i].getFloor()};
                roomTableModel.insertRow(i, rowData);
            }
        }
        // merkit��n t�m� viimeisimm�ksi napiksi, joka on ollut valittuna
        previouslySelectedButton = roomButton.getModel();
    }//GEN-LAST:event_roomButtonMouseReleased
    
    private void peopleButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_peopleButtonMouseReleased
        //piirr� taulukon n�kym� uusiksi vain jos jokin muu button groupin
        // nappi oli valittuna tai valintaa ei ollut lainkaan
        if (previouslySelectedButton == null
                || previouslySelectedButton != peopleButtonModel) {            
            peopleColumnModel.addColumn(peopleColumns.get("name"));
            Data.setColumnModel(peopleColumnModel);
            Data.setModel(peopleTableModel);
            for (int i = 0; i < people.length; i++) {
                Object[] rowData = {people[i].getName(), people[i].getPersonID()};
                peopleTableModel.insertRow(i, rowData);
            }
        }
        // merkit��n t�m� viimeisimm�ksi napiksi, joka on ollut valittuna
        previouslySelectedButton = peopleButtonModel;
    }//GEN-LAST:event_peopleButtonMouseReleased
    
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_saveButtonActionPerformed
    
    private void showWingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showWingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_showWingActionPerformed
    
    private void lockerButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lockerButtonMouseReleased
        //  emptyCheckboxContainer();
        Data.setModel(defaultLockerTable);
        previouslySelectedButton = lockerButtonModel;
        //       insertContents(lockerComponents, lockerAttributes);
        //     checkboxContainer.add(lockerAttributes);
        //    redrawContainer(lockerAttributes);
        //   redrawContainer(checkboxContainer);
    }//GEN-LAST:event_lockerButtonMouseReleased
    
    private void roomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roomButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_roomButtonActionPerformed
    
    private void floorDropdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_floorDropdownActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_floorDropdownActionPerformed
    
    private void showRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showRoomActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_showRoomActionPerformed
    
    private void wingDropdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wingDropdownActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_wingDropdownActionPerformed
    
    private void showRoomNameMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showRoomNameMouseReleased
        if (showRoomName.isSelected()) {
            roomColumnModel.addColumn(roomColumns.get("roomName"));
            for (int i = 0; i < rooms.length; i++) {
                Object[] rowData = {rooms[i].getRoomNumber(), rooms[i].getFloor(),
                    rooms[i].getPosts().length, rooms[i].getRoomName()};
                roomTableModel.insertRow(i, rowData);
            }
        } else {
            roomColumnModel.removeColumn(roomColumns.get("roomName"));
        }
    }//GEN-LAST:event_showRoomNameMouseReleased
    
    private void showContractsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showContractsMouseReleased
        if (showContracts.isSelected()) {
            peopleColumnModel.addColumn(peopleColumns.get("contracts"));
        } else {
            peopleColumnModel.removeColumn(peopleColumns.get("contracts"));
        }
    }//GEN-LAST:event_showContractsMouseReleased
    
    private void showFloorMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showFloorMouseReleased
        if (showFloor.isSelected()) {
            roomColumnModel.addColumn(roomColumns.get("floor"));
            for (int i = 0; i < rooms.length; i++) {
                Object[] rowData = {rooms[i].getRoomNumber(), rooms[i].getFloor(),
                    rooms[i].getPosts().length, rooms[i].getRoomName()};
                roomTableModel.insertRow(i, rowData);
            }
        } else {
            roomColumnModel.removeColumn(roomColumns.get("floor"));
        }
        
    }//GEN-LAST:event_showFloorMouseReleased

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
            java.util.logging.Logger.getLogger(ReportsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReportsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReportsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReportsWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ReportsWindow().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Data;
    private javax.swing.JPanel checkboxContainer;
    private javax.swing.JLabel floor;
    private javax.swing.JComboBox floorDropdown;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel lockerAttributes;
    private javax.swing.JRadioButton lockerButton;
    private javax.swing.JComboBox lockerDropdown;
    private javax.swing.JRadioButton peopleButton;
    private javax.swing.JPanel personAttributes;
    private javax.swing.JLabel rajauksetHeader;
    private javax.swing.JTextField restrictByName;
    private javax.swing.JPanel restrictionsContainer;
    private javax.swing.JPanel roomAttributes;
    private javax.swing.JRadioButton roomButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JCheckBox shoRoomAndPost;
    private javax.swing.JCheckBox showContracts;
    private javax.swing.JCheckBox showEmail;
    private javax.swing.JCheckBox showFloor;
    private javax.swing.JCheckBox showPhone;
    private javax.swing.JCheckBox showPostCount;
    private javax.swing.JCheckBox showRoom;
    private javax.swing.JCheckBox showRoomName;
    private javax.swing.JCheckBox showWing;
    private javax.swing.JScrollPane tableContainer;
    private javax.swing.ButtonGroup viewButtons;
    private javax.swing.JLabel wing;
    private javax.swing.JComboBox wingDropdown;
    // End of variables declaration//GEN-END:variables

    private void initContainerData() {

        /* Checkboxes for rooms view report*/
        roomComponents = new ArrayList<>();
        roomComponents.add(showPostCount);
        roomComponents.add(showFloor);
        roomComponents.add(showWing);
        roomComponents.add(showRoomName);

        /*Components for people view report*/
        peopleComponents = new ArrayList<>();
        peopleComponents.add(showRoomName);
        peopleComponents.add(showPostCount);
        peopleComponents.add(showWing);
        peopleComponents.add(showFloor);

        /*Components for post locker report*/
        lockerComponents = new ArrayList<>();
        lockerComponents.add(showRoom);
        lockerComponents.add(showPhone);
        
    }
    
    private void initModels() {

        /* Table Models */
        roomTableModel = new DefaultTableModel(new Object[][]{},
                new String[]{"Huoneen nro.", "Kerros"});
        peopleTableModel = new DefaultTableModel(new Object[][]{},
                new String[]{"Nimi"});
        defaultLockerTable = new DefaultTableModel(new Object[][]{},
                new String[]{"Nimi", "Postihuone"});

        /* Button Models */
        roomButtonModel = roomButton.getModel();
        peopleButtonModel = peopleButton.getModel();
        lockerButtonModel = lockerButton.getModel();

        /*ColumnModels & Columns for different views*/
        roomColumnModel = new DefaultTableColumnModel();
        roomColumns = new HashMap<>();
        roomColumns.put("roomNumber", new TableColumn());
        roomColumns.put("floor", new TableColumn());
        roomColumns.put("postCount", new TableColumn());
        roomColumns.put("roomName", new TableColumn());
        roomColumns.get("floor").setHeaderValue("Kerros");
        roomColumns.get("roomNumber").setHeaderValue("Huonenumero");
        roomColumns.get("postCount").setHeaderValue("Ty�pisteiden lkm");
        roomColumns.get("roomName").setHeaderValue("Huoneen nimi");
        
        peopleColumnModel = new DefaultTableColumnModel();
        peopleColumns = new HashMap<>();
        peopleColumns.put("name", new TableColumn());
        peopleColumns.put("personID", new TableColumn());
        peopleColumns.put("contracts", new TableColumn());
        peopleColumns.get("name").setHeaderValue("Nimi");
        peopleColumns.get("personID").setHeaderValue("Hetu");
        peopleColumns.get("contracts").setHeaderValue("Sopimukset");

        /*Dropdown menu models - currently not used*/
        wingsModel = new DefaultComboBoxModel();
        floorsModel = new DefaultComboBoxModel();
        for (int i = 0; i < floors.length; i++) {
            floorsModel.addElement(floors[i]);
        }
        for (int i = 0; i < floors.length; i++) {
            wingsModel.addElement(wings[i]);
        }
        
        
    }
}