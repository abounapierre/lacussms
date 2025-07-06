/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.RemoteDB;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.main.BottomPanel;
import com.abouna.lacussms.views.main.MainMenuPanel;
import com.abouna.lacussms.views.tools.AES;
import com.abouna.lacussms.views.tools.ConstantUtils;
import com.abouna.lacussms.views.tools.Utils;
import com.abouna.lacussms.views.utils.DialogUtils;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import org.jdesktop.swingx.JXSearchField;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.abouna.lacussms.views.tools.ConstantUtils.H2_DRIVER_CLASS;
import static com.abouna.lacussms.views.tools.ConstantUtils.MYSQL_DRIVER_CLASS;
import static com.abouna.lacussms.views.tools.ConstantUtils.ORACLE_DRIVER_CLASS;
import static com.abouna.lacussms.views.tools.ConstantUtils.POSTGRESQL_DRIVER_CLASS;

/**
 *
 * @author SATELLITE
 */
public class RemoteDBPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private final MainMenuPanel parentPanel;
    private final LacusSmsService serviceManager;

    public RemoteDBPanel() throws IOException {
        serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        parentPanel = ApplicationConfig.getApplicationContext().getBean(MainMenuPanel.class);
        setLayout(new BorderLayout());
        JPanel haut = new JPanel();
        haut.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel lbl;
        lbl = new JLabel("GESTION DES DB DISTANTES");
        haut.add(lbl);
        lbl.setFont(new Font("Broadway", Font.BOLD, 30));
        add(BorderLayout.BEFORE_FIRST_LINE, haut);
        JPanel contenu = new JPanel();
        contenu.setLayout(new BorderLayout());
        JPanel bas = new JPanel();
        bas.setLayout(new FlowLayout());
        Image ajouImg = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/Ajouter.png")));
        Image supprImg = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/Cancel2.png")));
        Image modifImg = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/OK.png")));
        JButton nouveau = new JButton(new ImageIcon(ajouImg));
        nouveau.setToolTipText("Ajouter une nouvelle agence");
        JButton supprimer = new JButton(new ImageIcon(supprImg));
        supprimer.setToolTipText("Suprimer une agence");
        JButton modifier = new JButton(new ImageIcon(modifImg));
        modifier.setToolTipText("Modifier une agence");
        nouveau.addActionListener((ActionEvent ae) -> DialogUtils.initDialog(new Nouveau(null), RemoteDBPanel.this.getParent(), 500, 300));
        modifier.addActionListener((ActionEvent ae) -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                Integer id = (Integer) tableModel.getValueAt(selected, 0);
                try {
                    DialogUtils.initDialog(new RemoteDBPanel.Nouveau(serviceManager.getRemoteDBById(id)), RemoteDBPanel.this.getParent(), 500, 300);
                } catch (Exception ex) {
                    Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(RemoteDBPanel.this.getParent(), "Aucun élément n'est selectionné");
            }
        });
        supprimer.addActionListener((ActionEvent ae) -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                Integer id = (Integer) tableModel.getValueAt(selected, 0);
                int res = JOptionPane.showConfirmDialog(RemoteDBPanel.this.getParent(), "Etes vous sûr de suppimer le client courant?", "Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (res == JOptionPane.YES_OPTION) {
                    try {
                        serviceManager.supprimerRemoteDB(id);
                    } catch (Exception ex) {
                        Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    tableModel.removeRow(selected);
                }
            } else {
                JOptionPane.showMessageDialog(RemoteDBPanel.this.getParent(), "Aucun élément selectionné");
            }
        });
        JButton testBtn = getTestConnexionButton();
        bas.add(nouveau);
        bas.add(modifier);
        bas.add(supprimer);
        bas.add(testBtn);
        JPanel filtrePanel = new JPanel();
        filtrePanel.setLayout(new FlowLayout());
        final JXSearchField searchField = new JXSearchField("Rechercher");
        searchField.setPreferredSize(new Dimension(500, 50));
        filtrePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), "Zone de recherche"));
        filtrePanel.add(searchField);
        filtrePanel.setBackground(new Color(166, 202, 240));
        searchField.addActionListener((ActionEvent e) -> {
            if (searchField.getText() != null) {
                try {
                    tableModel.setNumRows(0);
                    serviceManager.getAllRemoteDB().forEach((a) -> tableModel.addRow(new Object[]{
                        a.getId(),
                        a.getUrl(),
                        a.getHostName(),
                        a.getName()
                    }));
                } catch (Exception ex) {
                    Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        contenu.add(BorderLayout.AFTER_LAST_LINE, bas);
        contenu.add(BorderLayout.BEFORE_FIRST_LINE, filtrePanel);
        tableModel = new DefaultTableModel(new Object[]{"id", "Url", "Hôte", "Nom"}, 0);

        table = new JTable(tableModel);
        table.setBackground(Color.WHITE);
        //table.getColumnModel().getColumn(2).setPreferredWidth(280);
        //table.removeColumn(table.getColumnModel().getColumn(0));
        contenu.add(BorderLayout.CENTER, new JScrollPane(table));
        add(BorderLayout.CENTER, contenu);
        try {
            serviceManager.getAllRemoteDB().forEach((a) -> tableModel.addRow(new Object[]{
                a.getId(),
                a.getUrl(),
                a.getHostName(),
                a.getName()
            }));
        } catch (Exception ex) {
            Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private JButton getTestConnexionButton() {
        JButton testBtn = new JButton("Test Connexion");
        testBtn.addActionListener((ActionEvent e) -> {
            com.abouna.lacussms.views.utils.Logger.info("### Test de connexion de la BD ###", RemoteDBPanel.class);
            if (Utils.testConnexion(serviceManager, ConstantUtils.SECRET_KEY) != null) {
                JOptionPane.showMessageDialog(parentPanel, "Connexion réussie");
                BottomPanel.settextLabel("Connexion réussie", Color.GREEN);
            } else {
                JOptionPane.showMessageDialog(parentPanel, "Erreur lors de l'établissement de la connexion!");
            }
        });
        return testBtn;
    }

    private class Nouveau extends JDialog {

        private final JTextField nomText;
        private final JTextField urlText;
        private final JPasswordField passwordText;
        private final JTextField hostText;
        private final JCheckBox chkBox;
        private final JComboBox<String> driverBox;

        public Nouveau(final RemoteDB remoteDB) {
            setTitle("CONFIGURATION DE LA BASE DE DONNEES DISTANTE");
            setModal(true);
            setLayout(new BorderLayout(10, 10));
            JPanel haut = new JPanel();
            JLabel lbl;
            haut.add(lbl = new JLabel("<html><font color = #012345 > AJOUT D'UNE NOUVELLE BD </font></html>"));
            lbl.setFont(new Font("Times New Roman", Font.ITALIC, 18));
            add(BorderLayout.BEFORE_FIRST_LINE, haut);
            DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 180dlu:", ""));
            builder.setDefaultDialogBorder();
            builder.append("SGBD", driverBox = new JComboBox<>());
            driverBox.addItem("");
            driverBox.addItem("Mysql");
            driverBox.addItem("Oracle");
            driverBox.addItem("H2DB");
            driverBox.addItem("PostgreSql");
            builder.append("URL", urlText = new JTextField(80));
            builder.append("Nom de l'hôte", hostText = new JTextField(30));
            builder.append("Nom utilisateur", nomText = new JTextField(50));
            builder.append("Mot de passe", passwordText = new JPasswordField(30));
            builder.append("Par défaut?", chkBox = new JCheckBox());

            urlText.setToolTipText("jdbc:oracle:thin:@localhost:1521:SID");

            JButton okBtn;
            JButton annulerBtn;
            JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Enrégistrer"), annulerBtn = new JButton("Annuler"));
            builder.append(buttonBar, builder.getColumnCount());
            add(BorderLayout.CENTER, builder.getPanel());

            if (remoteDB != null) {
                nomText.setText(remoteDB.getName());
                urlText.setText(remoteDB.getUrl());
                String original = remoteDB.getPassword();

                if (original != null) {
                    if (!original.isEmpty()) {
                        String decryptedString = AES.decrypt(remoteDB.getPassword(), ConstantUtils.SECRET_KEY);
                        passwordText.setText(decryptedString);
                    }
                }
                hostText.setText(remoteDB.getHostName());
                chkBox.setSelected(remoteDB.isParDefault());
                String driver = remoteDB.getDriverClassName();
                if (driver != null) {
                    switch (driver) {
                        case MYSQL_DRIVER_CLASS:
                            driverBox.setSelectedIndex(0);
                            break;
                        case ORACLE_DRIVER_CLASS:
                            driverBox.setSelectedIndex(1);
                            break;
                        case H2_DRIVER_CLASS:
                            driverBox.setSelectedIndex(2);
                            break;
                        case POSTGRESQL_DRIVER_CLASS:
                            driverBox.setSelectedIndex(3);
                            break;
                    }
                }
            }

            okBtn.addActionListener((ActionEvent ae) -> {
                RemoteDB a = new RemoteDB();
                if (!nomText.getText().isEmpty()) {
                    a.setName(nomText.getText());
                } else {
                    JOptionPane.showMessageDialog(RemoteDBPanel.this.getParent(), "Le nom est obligatoire");
                    return;
                }
                if (!urlText.getText().isEmpty()) {
                    a.setUrl(urlText.getText());
                } else {
                    JOptionPane.showMessageDialog(RemoteDBPanel.this.getParent(), "L'url est obligatoire");
                    return;
                }
                if (passwordText.getPassword().length != 0) {
                    String originalString = new String(passwordText.getPassword());
                    String encryptedString = AES.encrypt(originalString, ConstantUtils.SECRET_KEY);
                    a.setPassword(encryptedString);
                }

                if (!hostText.getText().isEmpty()) {
                    a.setHostName(hostText.getText());
                } else {
                    JOptionPane.showMessageDialog(RemoteDBPanel.this.getParent(), "Le nom de l'hote est obligatoire");
                    return;
                }

                a.setParDefault(chkBox.isSelected());

                if (a.isParDefault()) {
                    serviceManager.getAllRemoteDB().stream().peek((r) -> r.setParDefault(false)).forEach(serviceManager::modifier);
                }

                String driver = getDriverClass();

                a.setDriverClassName(driver);

                if (remoteDB == null) {
                    try {
                        serviceManager.enregistrer(a);
                    } catch (Exception ex) {
                        Logger.getLogger(RemoteDBPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    a.setId(remoteDB.getId());
                    try {
                        serviceManager.modifier(a);
                    } catch (Exception ex) {
                        Logger.getLogger(RemoteDBPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                dispose();
                try {
                    parentPanel.setContent(new RemoteDBPanel());
                    JOptionPane.showMessageDialog(parentPanel, "Enregistré avec success");
                } catch (IOException ex) {
                    Logger.getLogger(RemoteDBPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            annulerBtn.addActionListener((ActionEvent ae) -> {
                dispose();
                try {
                    parentPanel.setContent(new RemoteDBPanel());
                } catch (IOException ex) {
                    Logger.getLogger(RemoteDBPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }

        private String getDriverClass() {
            String driver = null;
            if (driverBox.getSelectedIndex() == 1) {
                driver = MYSQL_DRIVER_CLASS;
            }

            if (driverBox.getSelectedIndex() == 2) {
                driver = ORACLE_DRIVER_CLASS;
            }

            if (driverBox.getSelectedIndex() == 3) {
                driver = H2_DRIVER_CLASS;
            }

            if (driverBox.getSelectedIndex() == 4) {
                driver = POSTGRESQL_DRIVER_CLASS;
            }
            if (driverBox.getSelectedIndex() == 0) {
                driver = "";
            }
            return driver;
        }
    }
}
