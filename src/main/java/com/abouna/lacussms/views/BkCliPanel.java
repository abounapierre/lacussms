/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.entities.BkCompCli;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.compoents.LacusButton;
import com.abouna.lacussms.views.compoents.LacusIcon;
import com.abouna.lacussms.views.compoents.ImportClientComponent;
import com.abouna.lacussms.views.main.MainMenuPanel;
import com.abouna.lacussms.views.utils.DialogUtils;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import org.jdesktop.swingx.JXSearchField;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SATELLITE
 */
public class BkCliPanel extends JPanel{
    private DefaultTableModel tableModel = null;
    private JTable table = null;
    private final MainMenuPanel parentPanel;
    private final LacusSmsService serviceManager;
    
    public BkCliPanel() throws IOException{
        serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        parentPanel = ApplicationConfig.getApplicationContext().getBean(MainMenuPanel.class);
        setLayout(new BorderLayout());
        JPanel haut = new JPanel();
        haut.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel lbl;
        lbl = new JLabel("GESTION DES CLIENTS");
        haut.add(lbl);
        lbl.setFont(new Font("Broadway", Font.BOLD, 30));
        add(BorderLayout.BEFORE_FIRST_LINE, haut);
        JPanel contenu = new JPanel();
        contenu.setLayout(new BorderLayout());
        JPanel bas = new JPanel();
        bas.setLayout(new FlowLayout());
        JButton nouveau = new JButton(new LacusIcon("/images/Ajouter.png"));
        nouveau.setToolTipText("Ajouter un nouveau client");
        JButton supprimer = new JButton(new LacusIcon("/images/Cancel2.png"));
        supprimer.setToolTipText("Suprimer un client");
        JButton modifier = new JButton(new LacusIcon("/images/OK.png"));
        modifier.setToolTipText("Modifier un client");
        JButton filtre = new JButton("Filtrer");
        nouveau.addActionListener(ae -> DialogUtils.initDialog(new Nouveau(null), BkCliPanel.this.getParent(), 400, 400));
        modifier.addActionListener(ae -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                String id = (String) tableModel.getValueAt(selected, 0);
                try {
                    DialogUtils.initDialog(new Nouveau(serviceManager.getBkCliById(id)), BkCliPanel.this.getParent(), 400, 400);
                } catch (Exception ex) {
                    Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(BkCliPanel.this.getParent(), "Aucun élément n'est selectionné");
            }
        });
        supprimer.addActionListener(ae -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                String id = (String) tableModel.getValueAt(selected, 0);
                int res = JOptionPane.showConfirmDialog(BkCliPanel.this.getParent(), "Etes vous sûr de suppimer le client courant?", "Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (res == JOptionPane.YES_OPTION) {
                    try {
                        serviceManager.supprimerBkCli(id);
                    } catch (Exception ex) {
                        Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    tableModel.removeRow(selected);
                }
            } else {
                JOptionPane.showMessageDialog(BkCliPanel.this.getParent(), "Aucun élément selectionné");
            }
        });
        bas.add(nouveau);
        bas.add(modifier);
        bas.add(supprimer);
        bas.add(new LacusButton(new LacusIcon("/images/excel.PNG"), a -> new ImportClientComponent(this)));
        JPanel filtrePanel = new JPanel();
        filtrePanel.setLayout(new FlowLayout());
        final JXSearchField searchField = new JXSearchField("Rechercher");
        searchField.setPreferredSize(new Dimension(500, 50));
        filtrePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), "Zone de recherche"));
        filtrePanel.add(searchField);
         filtrePanel.setBackground(new Color(166, 202, 240));
        searchField.addActionListener(e -> {
            String val;
            if (searchField.getText() != null) {
                try {
                    val = searchField.getText().toUpperCase();
                    tableModel.setNumRows(0);
                    List<BkCli> bkcliList = serviceManager.getBkCliByCriteria(val);
                    for (BkCli a : bkcliList) {
                        tableModel.addRow(new Object[]{
                            a.getCode(),
                            a.getLibelle(),
                            a.getNom(),
                            a.getPrenom(),
                            a.getPhone(),
                            a.getEmail(),
                            a.getLangue(),
                            a.isEnabled()
                        });

                    }
                } catch (Exception ex) {
                    Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        contenu.add(BorderLayout.AFTER_LAST_LINE, bas);
        contenu.add(BorderLayout.BEFORE_FIRST_LINE, filtrePanel);
        tableModel = new DefaultTableModel(new Object[]{"Code","Libellé", "Nom","Prénom","Téléphone","Email","Langue","Actif?"}, 0);

        table = new JTable(tableModel);
        table.setBackground(Color.WHITE);
        //table.getColumnModel().getColumn(2).setPreferredWidth(280);
        //table.removeColumn(table.getColumnModel().getColumn(0));
        contenu.add(BorderLayout.CENTER, new JScrollPane(table));
        add(BorderLayout.CENTER, contenu);
        try {
             for (BkCli a : serviceManager.getBkCliLimit(100)) {
                            tableModel.addRow(new Object[]{
                                a.getCode(),
                                a.getLibelle(),
                                a.getNom(),
                                a.getPrenom(),
                                a.getPhone(),
                                a.getEmail(),
                                a.getLangue(),
                                a.isEnabled()
                            });
            }
        } catch (Exception ex) {
            Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private class Nouveau extends JDialog {

        private final JTextField codeText;
        private final JTextField nameText;
        private final JTextField prenomText;
        private final JComboBox<String> libelleBox;
        private final JComboBox<String> langueBox;
        private final JTextField emailText;
        private final JTextField phoneText;
        private final JCheckBox chkBox;
        private final int c = 0;
        private final int rang =0;

        public Nouveau(final BkCli bkcli) {
            setTitle("NOUVEAU CLIENT");
            setModal(true);
            setLayout(new BorderLayout(10, 10));
            JPanel haut = new JPanel();
            JLabel lbl;
            haut.add(lbl = new JLabel("<html><font color = #012345 > AJOUT D'UN NOUVEAU CLIENT </font></html>"));
            lbl.setFont(new Font("Times New Roman", Font.ITALIC, 18));
            add(BorderLayout.BEFORE_FIRST_LINE, haut);
            DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 110dlu:", ""));
            builder.setDefaultDialogBorder();
            builder.append("Nméro de compte", codeText = new JTextField(50));
            builder.append("Nom", nameText = new JTextField(50));
            builder.append("Prénom", prenomText = new JTextField(10));
            builder.append("Libellé", libelleBox = new JComboBox<String>());
            builder.append("Téléphone", phoneText = new JTextField(50));
            builder.append("Email", emailText = new JTextField(50));
            builder.append("Langue", langueBox = new JComboBox<String>());
            builder.append("Actif?", chkBox = new JCheckBox());
            libelleBox.addItem("Mr");
            libelleBox.addItem("Mme");
            libelleBox.addItem("Mlle");
            langueBox.addItem("Français");
            langueBox.addItem("Anglais");

            JButton annulerBtn;
            JButton okBtn;
            JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Enrégistrer"), annulerBtn = new JButton("Annuler"));
            builder.append(buttonBar, builder.getColumnCount());
            add(BorderLayout.CENTER, builder.getPanel());
            
            if (bkcli != null) {
               codeText.setText(bkcli.getCode());
               nameText.setText(bkcli.getNom());
               prenomText.setText(bkcli.getPrenom());
               emailText.setText(bkcli.getEmail());
               phoneText.setText(Long.toString(bkcli.getPhone()));
               codeText.setEditable(false);
               if(bkcli.getLangue().equals("FR"))
                   langueBox.setSelectedIndex(0);
               else if(bkcli.getLangue().equals("EN"))
                   langueBox.setSelectedIndex(1);
                switch (bkcli.getLibelle()) {
                    case "Mr":
                        libelleBox.setSelectedIndex(0);
                        break;
                    case "Mme":
                        libelleBox.setSelectedIndex(1);
                        break;
                    case "Mlle":
                        libelleBox.setSelectedIndex(2);
                        break;
                }
                chkBox.setSelected(bkcli.isEnabled());
            }

            okBtn.addActionListener(ae -> {
                BkCli a = new BkCli();
                 if (!codeText.getText().isEmpty()) {
                     if(codeText.getText().length()>9)
                        a.setCode(codeText.getText().substring(3, 9).toUpperCase());
                     else
                        a.setCode(codeText.getText());
                } else {
                    JOptionPane.showMessageDialog(BkCliPanel.this.getParent(), "Le nom est obligatoire");
                }
                if (!nameText.getText().isEmpty()) {
                    a.setNom(nameText.getText().toUpperCase());
                } else {
                    JOptionPane.showMessageDialog(BkCliPanel.this.getParent(), "Le nom est obligatoire");
                }
                if (!phoneText.getText().isEmpty()) {
                    a.setPhone(Long.parseLong(phoneText.getText()));
                } else {
                    JOptionPane.showMessageDialog(BkCliPanel.this.getParent(), "Le téléphone est obligatoire");
                }
                a.setEnabled(false);
                if(chkBox.isSelected())
                    a.setEnabled(true);

                if(langueBox.getSelectedIndex() == 0)
                    a.setLangue("FR");
                else if(langueBox.getSelectedIndex() == 1)
                     a.setLangue("EN");

                a.setPrenom(prenomText.getText().toUpperCase());
                a.setLibelle((String) libelleBox.getSelectedItem());
                a.setEmail(emailText.getText());

                if (bkcli == null) {
                    try {
                        serviceManager.enregistrer(a);
                        if(serviceManager.getBkCompCliById(codeText.getText().toUpperCase())==null){
                            BkCompCli bkCompCli = new BkCompCli();
                            bkCompCli.setCli(a);
                            bkCompCli.setNumc(codeText.getText().toUpperCase());
                            bkCompCli.setEnabled(true);
                            serviceManager.enregistrer(bkCompCli);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    a.setCode(bkcli.getCode());
                    try {
                        serviceManager.modifier(a);
                    } catch (Exception ex) {
                        Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                dispose();
                try {
                    parentPanel.setContent(new BkCliPanel());
                } catch (IOException ex) {
                    Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            annulerBtn.addActionListener(ae -> {
                dispose();
                try {
                    parentPanel.setContent(new BkCliPanel());
                } catch (IOException ex) {
                    Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }
}
