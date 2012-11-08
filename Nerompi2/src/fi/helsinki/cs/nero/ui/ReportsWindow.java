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
import java.util.HashMap;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class ReportsWindow extends javax.swing.JFrame {

    private Session session;
    private ArrayList<JCheckBox> roomComponents;
    private ArrayList<JCheckBox> peopleComponents;
    private ArrayList<JCheckBox> lockerComponents;
    private TableColumnModel roomColumnModel;
    private TableColumnModel peopleColumnModel;
    private TableColumnModel lockerColumnModel;
    private HashMap<String, IndexedColumn> hiddenRoomColumns;
    private HashMap<String, IndexedColumn> hiddenPeopleColumns;
    private HashMap<String, IndexedColumn> hiddenLockerColumns;
    private Room[] rooms;
    private Person[] people;
    private Vector<Vector<String>> roomData;
    private Vector<String> roomColumnNames;
    private Vector<Vector<String>> peopleData;
    private Vector<String> peopleColumnNames;
    private Vector<Vector<String>> lockerData;
    private Vector<String> lockerColumnNames;
    private TableRowSorter<TableModel> rowSorter;
    
    // combobox models not used yet
    private DefaultComboBoxModel wingsModel;
    private DefaultComboBoxModel floorsModel;
    private int[] floors = new int[]{1, 2, 3};
    private char[] wings = new char[]{'A', 'B', 'C', 'D'};

    /**
     * Creates new form Reports
     */
    public ReportsWindow() {

        // koodia testausta varten, voi poistaa kun t�m� ikkuna
        // integroidaan muuhun k�liin
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
        initColumnData();

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
        showRoomAndPost = new javax.swing.JCheckBox();
        showEmail = new javax.swing.JCheckBox();
        showContracts = new javax.swing.JCheckBox();
        showPhone = new javax.swing.JCheckBox();
        lockerAttributes = new javax.swing.JPanel();
        showRoom = new javax.swing.JCheckBox();
        showPhone2 = new javax.swing.JCheckBox();
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

        showRoomAndPost.setText("Huone/ty�piste");
        showRoomAndPost.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                showRoomAndPostMouseReleased(evt);
            }
        });

        showEmail.setText("S�hk�posti");

        showContracts.setText("Sopimus");
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
                    .addComponent(showRoomAndPost)
                    .addComponent(showPhone)
                    .addComponent(showContracts)
                    .addComponent(showEmail))
                .addContainerGap())
        );
        personAttributesLayout.setVerticalGroup(
            personAttributesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(personAttributesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(showRoomAndPost)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showPhone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showContracts)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showEmail)
                .addContainerGap())
        );

        showRoom.setText("Ty�huone");
        showRoom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                showRoomMouseReleased(evt);
            }
        });

        showPhone2.setText("Puhelinnumero");
        showPhone2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                showPhone2MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout lockerAttributesLayout = new javax.swing.GroupLayout(lockerAttributes);
        lockerAttributes.setLayout(lockerAttributesLayout);
        lockerAttributesLayout.setHorizontalGroup(
            lockerAttributesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lockerAttributesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lockerAttributesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(showRoom)
                    .addComponent(showPhone2))
                .addContainerGap())
        );
        lockerAttributesLayout.setVerticalGroup(
            lockerAttributesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lockerAttributesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(showRoom)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showPhone2)
                .addContainerGap())
        );

        wing.setText("Siipi");

        wingDropdown.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "A", "B", "C", "D" }));
        wingDropdown.setToolTipText("");

        floor.setText("Kerros");

        floorDropdown.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3" }));
        floorDropdown.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                floorDropdownItemStateChanged(evt);
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
        Data.setEnabled(false);
        Data.setRowSelectionAllowed(false);
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

    private void showPostCountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showPostCountActionPerformed
        if (showPostCount.isSelected()) {
            showColumn("Ty�pisteiden lkm", roomColumnModel, hiddenRoomColumns);
        } else {
            hideColumn("Ty�pisteiden lkm", roomColumnModel, hiddenRoomColumns);
        }
    }//GEN-LAST:event_showPostCountActionPerformed

    private void roomButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_roomButtonMouseReleased
        Data = new JTable(roomData, roomColumnNames);
        roomColumnModel = Data.getColumnModel();
        setSelected(roomComponents);
        addSorter();
        tableContainer.setViewportView(Data);
    }//GEN-LAST:event_roomButtonMouseReleased

    private void peopleButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_peopleButtonMouseReleased
        Data = new JTable(peopleData, peopleColumnNames);
        peopleColumnModel = Data.getColumnModel();
        setSelected(peopleComponents);
        addSorter();
        tableContainer.setViewportView(Data);
    }//GEN-LAST:event_peopleButtonMouseReleased

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_saveButtonActionPerformed

    private void lockerButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lockerButtonMouseReleased
        Data = new JTable(lockerData, lockerColumnNames);
        lockerColumnModel = Data.getColumnModel();
        setSelected(lockerComponents);
        addSorter();
        tableContainer.setViewportView(Data);

    }//GEN-LAST:event_lockerButtonMouseReleased

    private void showRoomNameMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showRoomNameMouseReleased
        if (showRoomName.isSelected()) {
            showColumn("Nimi", roomColumnModel, hiddenRoomColumns);
        } else {
            hideColumn("Nimi", roomColumnModel, hiddenRoomColumns);
        }
    }//GEN-LAST:event_showRoomNameMouseReleased

    private void showContractsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showContractsMouseReleased
        if (showContracts.isSelected()) {
            showColumn("Sopimus", peopleColumnModel, hiddenRoomColumns);
        } else {
            hideColumn("Sopimus", peopleColumnModel, hiddenRoomColumns);
        }
    }//GEN-LAST:event_showContractsMouseReleased

    private void showFloorMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showFloorMouseReleased
        if (showFloor.isSelected()) {
            showColumn("Kerros", roomColumnModel, hiddenRoomColumns);
        } else {
            hideColumn("Kerros", roomColumnModel, hiddenRoomColumns);
        }

    }//GEN-LAST:event_showFloorMouseReleased

    private void showRoomAndPostMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showRoomAndPostMouseReleased
        if (showRoomAndPost.isSelected()) {
            showColumn("Huone", peopleColumnModel, hiddenPeopleColumns);
        } else {
            hideColumn("Huone", peopleColumnModel, hiddenPeopleColumns);
        }
    }//GEN-LAST:event_showRoomAndPostMouseReleased

    private void showRoomMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showRoomMouseReleased
        if (showRoom.isSelected()){
            showColumn("Huone", lockerColumnModel, hiddenLockerColumns);            
        } else {
            hideColumn("Huone", lockerColumnModel, hiddenLockerColumns);
        }
    }//GEN-LAST:event_showRoomMouseReleased

    private void showPhone2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showPhone2MouseReleased
        if (showPhone2.isSelected()){
            showColumn("Puhelinnumero", lockerColumnModel, hiddenLockerColumns);            
        } else {
            hideColumn("Puhelinnumero", lockerColumnModel, hiddenLockerColumns);
        }
    }//GEN-LAST:event_showPhone2MouseReleased

    private void floorDropdownItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_floorDropdownItemStateChanged
        //floorDropdown.
    }//GEN-LAST:event_floorDropdownItemStateChanged

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
    private javax.swing.JCheckBox showContracts;
    private javax.swing.JCheckBox showEmail;
    private javax.swing.JCheckBox showFloor;
    private javax.swing.JCheckBox showPhone;
    private javax.swing.JCheckBox showPhone2;
    private javax.swing.JCheckBox showPostCount;
    private javax.swing.JCheckBox showRoom;
    private javax.swing.JCheckBox showRoomAndPost;
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
        peopleComponents.add(showEmail);
        peopleComponents.add(showPhone);
        peopleComponents.add(showRoomAndPost);
        peopleComponents.add(showContracts);

        /*Components for post locker report*/
        lockerComponents = new ArrayList<>();
        lockerComponents.add(showRoom);
        //TODO: vaihda tuo muuttujanimi...
        lockerComponents.add(showPhone2);

    }

    private void initModels() {


        /*ColumnModels for different views*/
        hiddenRoomColumns = new HashMap<>();
        hiddenPeopleColumns = new HashMap<>();
        hiddenLockerColumns = new HashMap<>();

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

    private void initColumnData() {
        
        //TODO: erota nimet ja identifierit toisistaan, ettei tule skandiongelmia?
        //TODO: muuta Vectorin tyyppi Objectiksi, ett� kaikilla sarakkeilla voi olla oikea tyyppi -> voidaan filtter�id� j�rkev�sti

        // alustetaan data huoneiden tietojen n�ytt�mist� varten
        // ideana se, ett� data taustalla pysyy aina samana ja se sidotaan
        // tiettyihin sarakkeihin (sarakkeiden identifierit tulevat nimivektorista)
        // k�ytt�j�n inputista riippuen n�ytet��n tai piilotetaan tietty sarake,
        // mutta data taustalla pysyy samana
        
        // huone-tarkastelun data ja sarakkeet
        roomData = new Vector<>();
        for (int i = 0; i < rooms.length; i++) {
            Vector<String> v = new Vector<>();
            v.add(rooms[i].getRoomNumber().toString());
            v.add(rooms[i].getFloor().toString());
            v.add(new Integer(rooms[i].getPosts().length).toString());
            v.add(rooms[i].getRoomName());
            roomData.add(i, v);
        }

        roomColumnNames = new Vector<>();
        roomColumnNames.add("Huoneen nro");
        roomColumnNames.add("Kerros");
        roomColumnNames.add("Ty�pisteiden lkm");
        roomColumnNames.add("Nimi");
        
        // henkil�-tarkastelun data ja sarakkeet
        // laitetaan samalla data my�s postilokero-n�kym�n dataan
        peopleData = new Vector<>();
        lockerData = new Vector<>();       
        for (int i = 0; i < people.length; i++) {
            Vector<String> peopleRow = new Vector<>();
            Vector<String> l = new Vector<>();
            l.add(people[i].getName());
            l.add(people[i].getPostilokeroHuone());
            l.add(people[i].getRoom());
            l.add(people[i].getWorkPhone());
            peopleRow.add(people[i].getName());
            peopleRow.add(people[i].getRoom());
            peopleRow.add(people[i].getContractLengthAsString());
            peopleData.add(i, peopleRow);
        }

        peopleColumnNames = new Vector<>();
        peopleColumnNames.add("Nimi");
        peopleColumnNames.add("Huone");
        peopleColumnNames.add("Sopimus");

        // postilokero-n�kym�n sarakkeet        
        lockerColumnNames = new Vector<>();
        lockerColumnNames.add("K�ytt�j�");
        lockerColumnNames.add("Postihuone");
        lockerColumnNames.add("Huone");
        lockerColumnNames.add("Puhelinnumero");
        
        
    }

    private void showColumn(String name, TableColumnModel model,
            HashMap<String, IndexedColumn> hiddenColumns) {
        IndexedColumn column = hiddenColumns.remove(name);
        if (column != null) {
            model.addColumn(column.getTableColumn());
            int lastColumn = model.getColumnCount() - 1;
            if (column.getIndex() < lastColumn) {
                model.moveColumn(lastColumn, column.getIndex());
            }
        }
    }

    private void hideColumn(String name, TableColumnModel model,
            HashMap<String, IndexedColumn> hiddenColumns) {
        int index = model.getColumnIndex(name);
        TableColumn newColumn = model.getColumn(index);
        IndexedColumn ic = new IndexedColumn(index, newColumn);
        hiddenColumns.put(name, ic);
        model.removeColumn(newColumn);
    }

    private void setSelected(ArrayList<JCheckBox> components) {
        for (JCheckBox jcomp : components) {
            jcomp.setSelected(true);
        }
    }
    
    private void addSorter() {
        rowSorter = new TableRowSorter<>(Data.getModel());
        Data.setRowSorter(rowSorter);
    }
}
