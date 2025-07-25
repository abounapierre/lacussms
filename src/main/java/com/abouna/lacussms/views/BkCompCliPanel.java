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
import com.abouna.lacussms.views.main.MainMenuPanel;
import com.abouna.lacussms.views.utils.DialogUtils;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import org.jdesktop.swingx.JXSearchField;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SATELLITE
 */

public class BkCompCliPanel extends JPanel{
    private DefaultTableModel tableModel;
    private JTable table;
    private final MainMenuPanel parentPanel;
    private final LacusSmsService serviceManager;
    private final JFileChooser fc = new JFileChooser();
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BkCompCliPanel.class);
    
    public BkCompCliPanel() throws IOException{
        serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        parentPanel = ApplicationConfig.getApplicationContext().getBean(MainMenuPanel.class);
        setLayout(new BorderLayout());
        JPanel haut = new JPanel();
        haut.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel lbl;
        lbl = new JLabel("GESTION DES COMPTES CLIENTS");
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
        Image excelImg = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/excel.PNG")));
        JButton exportBtn = new JButton(new ImageIcon(excelImg));
        JButton nouveau = new JButton(new ImageIcon(ajouImg));
        exportBtn.setToolTipText("Exporter la liste des comptes Excel");
        nouveau.setToolTipText("Ajouter un nouveau compte client");
        JButton supprimer = new JButton(new ImageIcon(supprImg));
        supprimer.setToolTipText("Suprimer un compte client");
        JButton modifier = new JButton(new ImageIcon(modifImg));
        modifier.setToolTipText("Modifier un compte client");
        JButton filtre = new JButton("Filtrer");
        nouveau.addActionListener(ae -> DialogUtils.initDialog(new Nouveau(null), BkCompCliPanel.this.getParent(), 400, 400));
        modifier.addActionListener(ae -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                String id = (String) tableModel.getValueAt(selected, 0);
                try {
                    DialogUtils.initDialog(new Nouveau(serviceManager.getBkCompCliById(id)), BkCompCliPanel.this.getParent(), 400, 400);
                } catch (Exception ex) {
                    Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(BkCompCliPanel.this.getParent(), "Aucun élément n'est selectionné");
            }
        });
        supprimer.addActionListener(ae -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                String id = (String) tableModel.getValueAt(selected, 0);
                int res = JOptionPane.showConfirmDialog(BkCompCliPanel.this.getParent(), "Etes vous sûr de suppimer le compte client courant?", "Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (res == JOptionPane.YES_OPTION) {
                    try {
                        serviceManager.supprimerBkCompCli(id);
                    } catch (Exception ex) {
                        Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    tableModel.removeRow(selected);
                }
            } else {
                JOptionPane.showMessageDialog(BkCompCliPanel.this.getParent(), "Aucun élément selectionné");
            }
        });
        
        exportBtn.addActionListener(e -> {

            int val_retour = fc.showSaveDialog(BkCompCliPanel.this);
            if (val_retour == JFileChooser.APPROVE_OPTION) {
                File fichier = fc.getSelectedFile();
                final String path = fichier.getAbsolutePath() + ".xls";
                int response = JOptionPane.showConfirmDialog(BkCompCliPanel.this.getParent(), "<html>Rapport généré avec success!!<br>Voulez vous l'ouvrir?", "Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    try {
                        File pdfFile = new File(path);
                        if (pdfFile.exists()) {
                            if (Desktop.isDesktopSupported()) {
                                Desktop.getDesktop().open(pdfFile);
                            } else {
                                JOptionPane.showMessageDialog(BkCompCliPanel.this.getParent(), "Ce type de fichier n'est pas pris en charge");
                                logger.info("Awt Desktop is not supported!");
                            }
                        } else {
                            JOptionPane.showMessageDialog(BkCompCliPanel.this.getParent(), "Ce fichier n'existe pas");
                            logger.error("File is not exists!");
                        }
                    } catch (IOException | HeadlessException ignored) {
                    }
                }
            }
        });
        bas.add(nouveau);
        bas.add(modifier);
        bas.add(supprimer);
        bas.add(exportBtn);
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
                    List<BkCompCli> bkCompCliList = serviceManager.getBkCompCliByCriteria(val);
                    for (BkCompCli a : bkCompCliList) {
                        tableModel.addRow(new Object[]{
                            a.getNumc(),
                            a.getCli()==null?"":a.getCli().getNom() + " " + a.getCli().getPrenom(),
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
        tableModel = new DefaultTableModel(new Object[]{"Num Compte","Client","Actif?"}, 0);

        table = new JTable(tableModel);
        table.setBackground(Color.WHITE);
        //table.getColumnModel().getColumn(2).setPreferredWidth(280);
        //table.removeColumn(table.getColumnModel().getColumn(0));
        contenu.add(BorderLayout.CENTER, new JScrollPane(table));
        add(BorderLayout.CENTER, contenu);
        try {
             for (BkCompCli a : serviceManager.getBkCompCliLimit(100)) {
                            tableModel.addRow(new Object[]{
                                a.getNumc(),
                                a.getCli()==null?"":a.getCli().getNom() + " " + a.getCli().getPrenom(),
                                a.isEnabled()
                            });

                        }
        } catch (Exception ex) {
            Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class Nouveau extends JDialog {

        private final JTextField codeText;
        private final JComboBox<BkCli> clientBox; 
        private final JCheckBox chkBox;
        private int c = 0, rang =0;

        public Nouveau(final BkCompCli bkCompCli) {
            setTitle("NOUVEAU COMPTE CLIENT");
            setModal(true);
            setLayout(new BorderLayout(10, 10));
            JPanel haut = new JPanel();
            JLabel lbl;
            haut.add(lbl = new JLabel("<html><font color = #012345 > AJOUT D'UN NOUVEAU COMPTE CLIENT </font></html>"));
            lbl.setFont(new Font("Times New Roman", Font.ITALIC, 18));
            add(BorderLayout.BEFORE_FIRST_LINE, haut);
            DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 110dlu:", ""));
            builder.setDefaultDialogBorder();
            builder.append("Num de compte", codeText = new JTextField(50));
            builder.append("Client", clientBox = new JComboBox<BkCli>());
            builder.append("Actif?", chkBox = new JCheckBox());
            
            for(BkCli bkCli : serviceManager.getAllCli()){
                clientBox.addItem(bkCli);
                if(bkCompCli != null){
                   if(bkCompCli.getCli().getCode().equals(bkCli.getCode())){
                    rang = c;
                } 
                }
                c++;
            }

            JButton okBtn;
            JButton annulerBtn;
            JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Enrégistrer"), annulerBtn = new JButton("Annuler"));
            builder.append(buttonBar, builder.getColumnCount());
            add(BorderLayout.CENTER, builder.getPanel());
            
            if (bkCompCli != null) {
               codeText.setText(bkCompCli.getNumc());
               codeText.setEditable(false);
               clientBox.setSelectedIndex(0);

                chkBox.setSelected(bkCompCli.isEnabled());
            }

            okBtn.addActionListener(ae -> {
                BkCompCli a = new BkCompCli();
                 if (!codeText.getText().isEmpty()) {
                    a.setNumc(codeText.getText());
                } else {
                    JOptionPane.showMessageDialog(null, "Le num compte est obligatoire");
                }

                a.setEnabled(false);
                if(chkBox.isSelected())
                    a.setEnabled(true);

                a.setCli((BkCli) clientBox.getSelectedItem());

                if (bkCompCli == null) {
                    try {
                        serviceManager.enregistrer(a);
                    } catch (Exception ex) {
                        Logger.getLogger(BkCompCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    a.setNumc(bkCompCli.getNumc());
                    try {
                        serviceManager.modifier(a);
                    } catch (Exception ex) {
                        Logger.getLogger(BkCompCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                dispose();
                try {
                    parentPanel.setContent(new BkCompCliPanel());
                } catch (IOException ex) {
                    Logger.getLogger(BkCompCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            annulerBtn.addActionListener((ActionEvent ae) -> {
                dispose();
                try {
                    parentPanel.setContent(new BkCompCliPanel());
                } catch (IOException ex) {
                    Logger.getLogger(BkCompCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }
}
