/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.BkEtatOp;
import com.abouna.lacussms.entities.UrlMessage;
import com.abouna.lacussms.main.App;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.main.MainMenuPanel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXSearchField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ABOUNA
 */
public class EtatOPParamPanel extends JPanel{
    private DefaultTableModel tableModel;
    private JTable table;
    private final JButton nouveau, modifier, supprimer;
    private final JButton filtre;
    private  MainMenuPanel parentPanel;
    private  LacusSmsService serviceManager;
    
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
        Image ajouImg = ImageIO.read(getClass().getResource("/images/Ajouter.png"));
        Image supprImg = ImageIO.read(getClass().getResource("/images/Cancel2.png"));
        Image modifImg = ImageIO.read(getClass().getResource("/images/OK.png"));
        nouveau = new JButton(new ImageIcon(ajouImg));
        nouveau.setToolTipText("Ajouter une nouvel etat");
        supprimer = new JButton(new ImageIcon(supprImg));
        supprimer.setToolTipText("Suprimer un etat");
        modifier = new JButton(new ImageIcon(modifImg));
        modifier.setToolTipText("Modifier un etat");
        filtre = new JButton("Filtrer");
        nouveau.addActionListener((ActionEvent ae) -> {
            Nouveau nouveau1 = new Nouveau(null);
            nouveau1.setSize(400, 200);
            nouveau1.setLocationRelativeTo(null);
            nouveau1.setModal(true);
            nouveau1.setResizable(false);
            nouveau1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            nouveau1.setVisible(true);
        });
        modifier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selected = table.getSelectedRow();
                if (selected >= 0) {
                    Integer id = (Integer) tableModel.getValueAt(selected, 0);
                    Nouveau nouveau1 = null;
                    try {
                        nouveau1 = new Nouveau(serviceManager.getBkEtatOpById(id));
                    } catch (Exception ex) {
                        Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    nouveau1.setSize(400, 200);
                    nouveau1.setLocationRelativeTo(null);
                    nouveau1.setModal(true);
                    nouveau1.setResizable(false);
                    nouveau1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    nouveau1.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Aucun élément n'est selectionné");
                }
            }
        });
        supprimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selected = table.getSelectedRow();
                if (selected >= 0) {
                    Integer id = (Integer) tableModel.getValueAt(selected, 0);
                    int res = JOptionPane.showConfirmDialog(null, "Etes vous sûr de suppimer l'etat courant?", "Confirmation",
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
                    JOptionPane.showMessageDialog(null, "Aucun élément selectionné");
                }
            }
        });
        JButton testBtn = new JButton("Test Connexion");
        testBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(App.testConnexion()){
                    JOptionPane.showMessageDialog(parentPanel, "Connexion réussie");
                }else{
                    JOptionPane.showMessageDialog(parentPanel, "Erreur lors de l'établissement de la connexion!");
                }
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
        searchField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
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
            serviceManager.getAllBkEtatOps().stream().forEach((a) -> {
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

        private final JButton okBtn, annulerBtn;
        private final JTextField valeurText;
        private final JCheckBox chkBox;
       
        private int c = 0, rang =0;

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
            JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Enrégistrer"), annulerBtn = new JButton("Annuler"));
            builder.append(buttonBar, builder.getColumnCount());
            add(BorderLayout.CENTER, builder.getPanel());
            
            if (etat != null) {
               valeurText.setText(etat.getValeur());
               if(etat.isActif())
                  chkBox.setSelected(true);
               else
                   chkBox.setSelected(false);
            }

            okBtn.addActionListener((ActionEvent ae) -> {
                BkEtatOp a = new BkEtatOp();
                
                if (!valeurText.getText().equals("")) {
                    a.setValeur(valeurText.getText());
                } else {
                    JOptionPane.showMessageDialog(null, "La valeur est obligatoire");
                    return;
                }
                
                if(chkBox.isSelected())
                    a.setActif(true);
                else
                    a.setActif(false);
                
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
                    parentPanel.setContenu(new EtatOPParamPanel());
                } catch (IOException ex) {
                    Logger.getLogger(EtatOPParamPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            annulerBtn.addActionListener((ActionEvent ae) -> {
                dispose();
                try {
                    parentPanel.setContenu(new EtatOPParamPanel());
                } catch (IOException ex) {
                    Logger.getLogger(EtatOPParamPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    } 
}
