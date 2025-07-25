/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.BkAgence;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.main.MainMenuPanel;
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
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SATELLITE
 */

public class BkAgencePanel extends JPanel{
 private DefaultTableModel tableModel;
    private JTable table;
    private final MainMenuPanel parentPanel;
    private final LacusSmsService serviceManager;
    
    public BkAgencePanel() throws IOException{
        serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        parentPanel = ApplicationConfig.getApplicationContext().getBean(MainMenuPanel.class);
        setLayout(new BorderLayout());
        JPanel haut = new JPanel();
        haut.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel lbl;
        lbl = new JLabel("GESTION DES AGENCES");
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
        JButton filtre = new JButton("Filtrer");
        nouveau.addActionListener(ae -> {
            DialogUtils.initDialog(new Nouveau(null), BkAgencePanel.this.getParent(), 400, 300);
        });
        modifier.addActionListener((ActionEvent ae) -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                String id = (String) tableModel.getValueAt(selected, 0);
                try {
                    DialogUtils.initDialog(new Nouveau(serviceManager.getBkAgenceById(id)), BkAgencePanel.this.getParent(), 400, 300);
                } catch (Exception ex) {
                    Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(BkAgencePanel.this.getParent(), "Aucun élément n'est selectionné");
            }
        });

        supprimer.addActionListener((ActionEvent ae) -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                String id = (String) tableModel.getValueAt(selected, 0);
                int res = JOptionPane.showConfirmDialog(BkAgencePanel.this.getParent(), "Etes vous sûr de suppimer le client courant?", "Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (res == JOptionPane.YES_OPTION) {
                    try {
                        serviceManager.supprimerBkAgence(id);
                    } catch (Exception ex) {
                        Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    tableModel.removeRow(selected);
                }
            } else {
                JOptionPane.showMessageDialog(BkAgencePanel.this.getParent(), "Aucun élément selectionné");
            }
        });
        bas.add(nouveau);
        bas.add(modifier);
        bas.add(supprimer);
        JPanel filtrePanel = new JPanel();
        filtrePanel.setLayout(new FlowLayout());
        final JXSearchField searchField = new JXSearchField("Rechercher");
        searchField.setPreferredSize(new Dimension(500, 50));
        filtrePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), "Zone de recherche"));
        filtrePanel.add(searchField);
         filtrePanel.setBackground(new Color(166, 202, 240));
        searchField.addActionListener((ActionEvent e) -> {
            String val = null;
            if (searchField.getText() != null) {
                try {
                    val = searchField.getText().toUpperCase();
                    tableModel.setNumRows(0);
                    List<BkAgence> bkcliList = serviceManager.getAllBkAgences();
                    for (BkAgence a : bkcliList) {
                        tableModel.addRow(new Object[]{
                            a.getNuma(),
                            a.getNoma(),
                            a.getAddra()
                        });
                        
                    }
                } catch (Exception ex) {
                    Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        contenu.add(BorderLayout.AFTER_LAST_LINE, bas);
        contenu.add(BorderLayout.BEFORE_FIRST_LINE, filtrePanel);
        tableModel = new DefaultTableModel(new Object[]{"Code", "Nom","Adresse"}, 0);

        table = new JTable(tableModel);
        table.setBackground(Color.WHITE);
        //table.getColumnModel().getColumn(2).setPreferredWidth(280);
        //table.removeColumn(table.getColumnModel().getColumn(0));
        contenu.add(BorderLayout.CENTER, new JScrollPane(table));
        add(BorderLayout.CENTER, contenu);
        try {
            serviceManager.getAllBkAgences().forEach((a) -> {
                tableModel.addRow(new Object[]{
                    a.getNuma(),
                    a.getNoma(),
                    a.getAddra()
                });
            });
        } catch (Exception ex) {
            Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class Nouveau extends JDialog {

        private final JButton okBtn, annulerBtn;
        private final JTextField codeText;
        private final JTextField nameText;
        private final JTextField adrText;
       
        private int c = 0, rang =0;

        public Nouveau(final BkAgence bkagence) {
            setTitle("NOUVELLE AGENCE");
            setModal(true);
            setLayout(new BorderLayout(10, 10));
            JPanel haut = new JPanel();
            JLabel lbl;
            haut.add(lbl = new JLabel("<html><font color = #012345 > AJOUT D'UNE NOUVELLE AGENCE </font></html>"));
            lbl.setFont(new Font("Times New Roman", Font.ITALIC, 18));
            add(BorderLayout.BEFORE_FIRST_LINE, haut);
            DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 110dlu:", ""));
            builder.setDefaultDialogBorder();
            builder.append("Code Agence", codeText = new JTextField(50));
            builder.append("Nom", nameText = new JTextField(50));
            builder.append("Addresse", adrText = new JTextField(10));
            
           
            JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Enrégistrer"), annulerBtn = new JButton("Annuler"));
            builder.append(buttonBar, builder.getColumnCount());
            add(BorderLayout.CENTER, builder.getPanel());
            
            if (bkagence != null) {
               codeText.setText(bkagence.getNuma());
               nameText.setText(bkagence.getNoma());
               adrText.setText(bkagence.getAddra());
               
            }

            okBtn.addActionListener((ActionEvent ae) -> {
                BkAgence a = new BkAgence();
                if (!codeText.getText().equals("")) {
                    a.setNuma(codeText.getText());
                } else {
                    JOptionPane.showMessageDialog(BkAgencePanel.this.getParent(), "Le code est obligatoire");
                }
                if (!nameText.getText().equals("")) {
                    a.setNoma(nameText.getText());
                } else {
                    JOptionPane.showMessageDialog(BkAgencePanel.this.getParent(), "Le nom est obligatoire");
                }
                a.setAddra(adrText.getText());
                
                
                if (bkagence == null) {
                    try {
                        serviceManager.enregistrer(a);
                    } catch (Exception ex) {
                        Logger.getLogger(BkAgencePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    a.setNuma(bkagence.getNuma());
                    try {
                        serviceManager.modifier(a);
                    } catch (Exception ex) {
                        Logger.getLogger(BkAgencePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                dispose();
                try {
                    parentPanel.setContent(new BkAgencePanel());
                } catch (IOException ex) {
                    Logger.getLogger(BkAgencePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            annulerBtn.addActionListener((ActionEvent ae) -> {
                dispose();
                try {
                    parentPanel.setContent(new BkAgencePanel());
                } catch (IOException ex) {
                    Logger.getLogger(BkAgencePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }
}
