/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.BkEtatOp;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.main.MainMenuPanel;
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
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ABOUNA
 */
public class EtatOPParamPanel extends JPanel{
    private DefaultTableModel tableModel;
    private JTable table;
    private final MainMenuPanel parentPanel;
    private final LacusSmsService serviceManager;
    
    public EtatOPParamPanel() throws IOException{
        serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        parentPanel = ApplicationConfig.getApplicationContext().getBean(MainMenuPanel.class);
        setLayout(new BorderLayout());
        JPanel haut = new JPanel();
        haut.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel lbl;
        lbl = new JLabel("PARAMETRAGE DES FILTRES SUR ETAT OPERATION");
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
        nouveau.setToolTipText("Ajouter une nouvel etat");
        JButton supprimer = new JButton(new ImageIcon(supprImg));
        supprimer.setToolTipText("Suprimer un etat");
        JButton modifier = new JButton(new ImageIcon(modifImg));
        modifier.setToolTipText("Modifier un etat");
        JButton filtre = new JButton("Filtrer");
        nouveau.addActionListener((ActionEvent ae) -> {
            DialogUtils.initDialog(new EtatOPParamPanel.Nouveau(null), EtatOPParamPanel.this.getParent(), 400, 200);
        });
        modifier.addActionListener(ae -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                Integer id = (Integer) tableModel.getValueAt(selected, 0);
                try {
                    DialogUtils.initDialog(new Nouveau(serviceManager.getBkEtatOpById(id)), EtatOPParamPanel.this.getParent(), 400, 200);
                } catch (Exception ex) {
                    Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(EtatOPParamPanel.this.getParent(), "Aucun élément n'est selectionné");
            }
        });
        supprimer.addActionListener(ae -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                Integer id = (Integer) tableModel.getValueAt(selected, 0);
                int res = JOptionPane.showConfirmDialog(EtatOPParamPanel.this.getParent(), "Etes vous sûr de suppimer l'etat courant?", "Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (res == JOptionPane.YES_OPTION) {
                    try {
                        serviceManager.supprimerBkEtatOp(id);
                    } catch (Exception ex) {
                        Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    tableModel.removeRow(selected);
                }
            } else {
                JOptionPane.showMessageDialog(EtatOPParamPanel.this.getParent(), "Aucun élément selectionné");
            }
        });
        JButton testBtn = new JButton("Test Connexion");
        testBtn.addActionListener(e -> {
            if(Utils.testConnexion(serviceManager, ConstantUtils.SECRET_KEY) != null){
                JOptionPane.showMessageDialog(parentPanel, "Connexion réussie");
            }else{
                JOptionPane.showMessageDialog(parentPanel, "Erreur lors de l'établissement de la connexion!");
            }
        });
        bas.add(nouveau);
        bas.add(modifier);
        bas.add(supprimer);
        //bas.add(testBtn);
        JPanel filtrePanel = new JPanel();
        filtrePanel.setLayout(new FlowLayout());
        final JXSearchField searchField = new JXSearchField("Rechercher");
        searchField.setPreferredSize(new Dimension(500, 50));
        filtrePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), "Zone de recherche"));
        filtrePanel.add(searchField);
         filtrePanel.setBackground(new Color(166, 202, 240));
        searchField.addActionListener(e -> {
            String val = null;
            if (searchField.getText() != null) {
                try {
                    val = searchField.getText().toUpperCase();
                    tableModel.setNumRows(0);
                    List<BkEtatOp> bkEtatOpList = serviceManager.getAllBkEtatOps();
                    for (BkEtatOp a : bkEtatOpList) {
                        tableModel.addRow(new Object[]{
                            a.getId(),
                            a.getValeur(),
                            a.isActif()
                        });

                    }
                } catch (Exception ex) {
                    Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        contenu.add(BorderLayout.AFTER_LAST_LINE, bas);
        contenu.add(BorderLayout.BEFORE_FIRST_LINE, filtrePanel);
        tableModel = new DefaultTableModel(new Object[]{"id", "Valeur","Actif ?"}, 0);

        table = new JTable(tableModel);
        table.setBackground(Color.WHITE);
        table.getColumnModel().getColumn(1).setPreferredWidth(350);
        //table.removeColumn(table.getColumnModel().getColumn(0));
        contenu.add(BorderLayout.CENTER, new JScrollPane(table));
        add(BorderLayout.CENTER, contenu);
        try {
            serviceManager.getAllBkEtatOps().forEach((a) -> {
                tableModel.addRow(new Object[]{
                    a.getId(),
                    a.getValeur(),
                    a.isActif()
                });
            });
        } catch (Exception ex) {
            Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class Nouveau extends JDialog {

        private final JTextField valeurText;
        private final JCheckBox chkBox;
       
        private final int c = 0;
        private final int rang =0;

        public Nouveau(final BkEtatOp etat) {
            setTitle("NOUVEL ETAT OPERATION");
            setModal(true);
            setLayout(new BorderLayout(10, 10));
            JPanel haut = new JPanel();
            JLabel lbl;
            haut.add(lbl = new JLabel("<html><font color = #012345 > AJOUT D'UN NOUVEL ETAT </font></html>"));
            lbl.setFont(new Font("Times New Roman", Font.ITALIC, 18));
            add(BorderLayout.BEFORE_FIRST_LINE, haut);
            DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 110dlu:", ""));
            builder.setDefaultDialogBorder();
            builder.append("Valeur", valeurText = new JTextField(10));
            builder.append("est actif?", chkBox = new JCheckBox());
            JButton annulerBtn;
            JButton okBtn;
            JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Enrégistrer"), annulerBtn = new JButton("Annuler"));
            builder.append(buttonBar, builder.getColumnCount());
            add(BorderLayout.CENTER, builder.getPanel());
            
            if (etat != null) {
               valeurText.setText(etat.getValeur());
                chkBox.setSelected(etat.isActif());
            }

            okBtn.addActionListener((ActionEvent ae) -> {
                BkEtatOp a = new BkEtatOp();
                
                if (!valeurText.getText().isEmpty()) {
                    a.setValeur(valeurText.getText());
                } else {
                    JOptionPane.showMessageDialog(EtatOPParamPanel.this.getParent(), "La valeur est obligatoire");
                    return;
                }

                a.setActif(chkBox.isSelected());
                
                if (etat == null) {
                    try {
                        serviceManager.enregistrer(a);
                    } catch (Exception ex) {
                        Logger.getLogger(EtatOPParamPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    a.setId(etat.getId());
                    try {
                        serviceManager.modifier(a);
                    } catch (Exception ex) {
                        Logger.getLogger(EtatOPParamPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                dispose();
                try {
                    parentPanel.setContent(new EtatOPParamPanel());
                } catch (IOException ex) {
                    Logger.getLogger(EtatOPParamPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            annulerBtn.addActionListener((ActionEvent ae) -> {
                dispose();
                try {
                    parentPanel.setContent(new EtatOPParamPanel());
                } catch (IOException ex) {
                    Logger.getLogger(EtatOPParamPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    } 
}
