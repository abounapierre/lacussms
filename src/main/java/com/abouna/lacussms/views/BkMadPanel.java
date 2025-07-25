/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.*;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.main.MainMenuPanel;
import com.abouna.lacussms.views.utils.DialogUtils;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXSearchField;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SATELLITE
 */
public class BkMadPanel extends JPanel{
   private final DefaultTableModel tableModel;
    private final JTable table;

    private final MainMenuPanel parentPanel;
    private final LacusSmsService serviceManager;

    public BkMadPanel() throws IOException{
        serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        parentPanel = ApplicationConfig.getApplicationContext().getBean(MainMenuPanel.class);
        setLayout(new BorderLayout());
        JPanel haut = new JPanel();
        haut.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel lbl;
        lbl = new JLabel("GESTION DES MANDATS");
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
        JButton supprimer = getJButton(ajouImg, supprImg, modifImg);
        JButton purgerBtn = new JButton("Purger");
        purgerBtn.addActionListener((ActionEvent e) -> {
            DeleteBkMadPanel nouveau1 = null;
            nouveau1 = new DeleteBkMadPanel();
            nouveau1.setSize(450, 200);
            nouveau1.setLocationRelativeTo(null);
            nouveau1.setModal(true);
            nouveau1.setResizable(false);
            nouveau1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            nouveau1.setVisible(true);
        });
        //bas.add(nouveau);
        //bas.add(modifier);
        bas.add(supprimer);
        bas.add(purgerBtn);
        JPanel filtrePanel = new JPanel();
        //filtrePanel.setPreferredSize(new Dimension(500, 20));
        JPanel searchPanel = new JPanel(new FlowLayout());
        filtrePanel.setLayout(new FlowLayout());
        final JXSearchField searchField = new JXSearchField("Rechercher");
        searchField.setPreferredSize(new Dimension(500, 25));
        JLabel labelDate1 = new JLabel("DAte de début");
        JXDatePicker dateDeb = new JXDatePicker();
        JLabel labelDate2 = new JLabel("DAte de fin");
        JXDatePicker dateFin = new JXDatePicker();
        JButton filterBtn = new JButton("Filtrer");
        JButton printBtn = new JButton("Imprimer");
        searchPanel.add(labelDate1);
        searchPanel.add(dateDeb);
        searchPanel.add(labelDate2);
        searchPanel.add(dateFin);
        searchPanel.add(filterBtn);
        searchPanel.add(printBtn);
        filtrePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), "Zone de recherche"));
        filtrePanel.add(searchField);
        //filtrePanel.add(searchPanel,BorderLayout.SOUTH);
         filtrePanel.setBackground(new Color(166, 202, 240));
        searchField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String val = null;
                if (searchField.getText() != null) {
                    try {
                        val = searchField.getText().toUpperCase();
                        tableModel.setNumRows(0);
                        List<BkMad> bkeveList = serviceManager.getbkMadsByCriteria(val);
                        for (BkMad a : bkeveList) {
                            tableModel.addRow(new Object[]{
                                a.getId(),
                                a.getEve(),
                                a.getOpe()==null?"":a.getOpe().getLib(),
                                a.getMnt(),
                                a.getAd1p(),
                                a.getAd2p(),
                                a.getAge()==null?"":a.getAge().getNoma(),
                                a.getDco()==null?"":a.getDco(),
                                a.getDbd()==null?"":a.getDbd(),
                                a.getCtr(),
                                a.isSent()
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
        tableModel = new DefaultTableModel(new Object[]{"Id","Code","Opération","Montant","Num Exp.","Num Dest.","Agence","Date E","Date R","CTR","Traité?"}, 0);

        table = new JTable(tableModel);
        table.setBackground(Color.WHITE);
        //table.setBackground(Color.LIGHT_GRAY);
        //table.setFont(new Font("Comic Sans MS", 1, 14));
        //table.setForeground(Color.MAGENTA);
        //table.getColumnModel().getColumn(2).setPreferredWidth(280);
        //table.removeColumn(table.getColumnModel().getColumn(0));
        contenu.add(BorderLayout.CENTER, new JScrollPane(table));
        add(BorderLayout.CENTER, contenu);
        try {
             List<BkMad> bkeveList = serviceManager.getBkMadByLimit(500);
                        for (BkMad a : bkeveList) {
                            tableModel.addRow(new Object[]{
                                a.getId(),
                                a.getEve(),
                                a.getOpe()==null?"":a.getOpe().getLib(),
                                a.getMnt(),
                                a.getAd1p(),
                                a.getAd2p(),
                                a.getAge()==null?"":a.getAge().getNoma(),
                                a.getDco()==null?"":a.getDco(),
                                a.getDbd(),
                                a.getCtr(),
                                a.isSent()
                            });
            }
        } catch (Exception ex) {
            Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private JButton getJButton(Image ajouImg, Image supprImg, Image modifImg) {
        JButton nouveau = new JButton(new ImageIcon(ajouImg));
        nouveau.setToolTipText("Ajouter un nouvel évenement");
        JButton supprimer = new JButton(new ImageIcon(supprImg));
        supprimer.setToolTipText("Suprimer un evenement");
        JButton modifier = new JButton(new ImageIcon(modifImg));
        modifier.setToolTipText("Modifier un evenement");
        JButton filtre = new JButton("Filtrer");
        nouveau.addActionListener(ae -> {
            DialogUtils.initDialog(new Nouveau(null), BkMadPanel.this.getParent(), 400, 300);
        });
        modifier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selected = table.getSelectedRow();
                if (selected >= 0) {
                    Integer id = (Integer) tableModel.getValueAt(selected, 0);
                    try {
                        DialogUtils.initDialog(new Nouveau(serviceManager.getBkEveById(id)), BkMadPanel.this.getParent(), 400, 300);
                    } catch (Exception ex) {
                        Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(BkMadPanel.this.getParent(), "Aucun élément n'est selectionné");
                }
            }
        });
        supprimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selected = table.getSelectedRow();
                if (selected >= 0) {
                    Integer id = (Integer) tableModel.getValueAt(selected, 0);
                    int res = JOptionPane.showConfirmDialog(BkMadPanel.this.getParent(), "Etes vous sûr de suppimer l'évenement courant?", "Confirmation",
                            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    if (res == JOptionPane.YES_OPTION) {
                        try {
                            serviceManager.supprimerBkMad(id);
                        } catch (Exception ex) {
                            Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        tableModel.removeRow(selected);
                    }
                } else {
                    JOptionPane.showMessageDialog(BkMadPanel.this.getParent(), "Aucun élément selectionné");
                }
            }
        });
        return supprimer;
    }

    private class Nouveau extends JDialog {

        private final JTextField compteText;
        private final JComboBox<String> etatBox;
        private final JComboBox<BkOpe> bkOpeBox;
        private final JComboBox<BkCli> bkCliBox;
        private final JComboBox<BkAgence> bkAgenceBox;
        private final JTextField montText,codeText;
        private int c = 0, rang =0,c1 = 0, rang1 =0,c2=0,rang2=0;

        public Nouveau(final BkEve bkeve) {
            setTitle("NOUVEL EVENEMENT");
            setModal(true);
            setLayout(new BorderLayout(10, 10));
            JPanel haut = new JPanel();
            JLabel lbl;
            haut.add(lbl = new JLabel("<html><font color = #012345 > AJOUT D'UN NOUVEL EVENEMENT </font></html>"));
            lbl.setFont(new Font("Times New Roman", Font.ITALIC, 18));
            add(BorderLayout.BEFORE_FIRST_LINE, haut);
            DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 110dlu:", ""));
            builder.setDefaultDialogBorder();
            builder.append("Code ", codeText = new JTextField(50));
            builder.append("Agence", bkAgenceBox = new JComboBox<BkAgence>());
            builder.append("Client", bkCliBox = new JComboBox<BkCli>());
            builder.append("Opération", bkOpeBox = new JComboBox<BkOpe>());
            builder.append("Numéro compte", compteText = new JTextField(50));
            builder.append("Montant", montText = new JTextField(50));
            builder.append("Etat", etatBox = new JComboBox<String>());
            etatBox.addItem("VA");
            etatBox.addItem("AT");
            etatBox.addItem("FO");
            etatBox.addItem("VF");
            etatBox.addItem("IG");
            etatBox.addItem("IF");
            etatBox.addItem("AB");
            etatBox.addItem("AN");
            etatBox.addItem("TR");
            for(BkCli bkCli : serviceManager.getAllCli()){
                bkCliBox.addItem(bkCli);
                if(bkeve != null){
                   if(bkeve.getCli().getCode().equals(bkCli.getCode())){
                    rang = c;
                } 
                }
                c++;
            }
            for(BkAgence bkagence : serviceManager.getAllBkAgences()){
                bkAgenceBox.addItem(bkagence);
                if(bkeve != null){
                   if(bkeve.getBkAgence().getNuma().equals(bkagence.getNuma())){
                    rang2 = c2;
                } 
                }
                c++;
            }
            for(BkOpe bkOpe : serviceManager.getAllBkOpes()){
                bkOpeBox.addItem(bkOpe);
                if(bkeve != null){
                    if(bkeve.getOpe().getOpe().equals(bkOpe.getOpe())){
                      rang1 = c1;
                  }  
                }
                c1++;
            }
            JButton okBtn;
            JButton annulerBtn;
            JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Enrégistrer"), annulerBtn = new JButton("Annuler"));
            builder.append(buttonBar, builder.getColumnCount());
            add(BorderLayout.CENTER, builder.getPanel());
            
            if (bkeve != null) {
               codeText.setText(Integer.toString(bkeve.getId()));
               codeText.setEditable(false);
               compteText.setText(bkeve.getCompte());
               bkCliBox.setSelectedIndex(rang);
               bkOpeBox.setSelectedIndex(rang1);
               bkAgenceBox.setSelectedIndex(rang2);
               montText.setText(Double.toString(bkeve.getMont()));
                switch (bkeve.getEtat()) {
                    case "VA":
                        etatBox.setSelectedIndex(0);
                        break;
                    case "AT":
                        etatBox.setSelectedIndex(1);
                        break;
                    case "FO":
                    case "AB":
                    case "IF":
                    case "IG":
                    case "VF":
                        etatBox.setSelectedIndex(2);
                        break;
                    case "AN":
                        etatBox.setSelectedIndex(7);
                        break;
                    case "TR":
                        etatBox.setSelectedIndex(8);
                        break;
                }
            }

            okBtn.addActionListener(ae -> {
                BkEve a = new BkEve();
                 if (!compteText.getText().equals("")) {
                    a.setCompte(compteText.getText());
                } else {
                    JOptionPane.showMessageDialog(BkMadPanel.this.getParent(), "Le compte est obligatoire");
                    return;
                }
                 if (!montText.getText().equals("")) {
                    a.setMont(Double.parseDouble(montText.getText()));
                } else {
                    JOptionPane.showMessageDialog(BkMadPanel.this.getParent(), "Le compte est obligatoire");
                    return;
                }

                if (!codeText.getText().equals("")) {
                    a.setNumEve(codeText.getText());
                } else {
                    JOptionPane.showMessageDialog(BkMadPanel.this.getParent(), "Le code est obligatoire");
                    return;
                }

                a.setOpe((BkOpe) bkOpeBox.getSelectedItem());
                a.setEtat((String) etatBox.getSelectedItem());
                a.setCli((BkCli) bkCliBox.getSelectedItem());
                a.setBkAgence((BkAgence) bkAgenceBox.getSelectedItem());
                Date d = new Date();
                long l = d.getTime();
                a.setHsai(Long.toString(l));
                a.setSent(false);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
                a.setEventDate(d);
                a.setDVAB(format.format(d));
                a.setId(serviceManager.getMaxIndexBkEve() + 1);

                if (bkeve == null) {
                    try {
                        serviceManager.enregistrer(a);
                    } catch (Exception ex) {
                        Logger.getLogger(BkEvePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    a.setId(bkeve.getId());
                    try {
                        serviceManager.modifier(a);
                    } catch (Exception ex) {
                        Logger.getLogger(BkEvePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                dispose();
                try {
                    parentPanel.setContent(new BkEvePanel());
                } catch (IOException ex) {
                    Logger.getLogger(BkEvePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            annulerBtn.addActionListener((ActionEvent ae) -> {
                dispose();
                try {
                    parentPanel.setContent(new BkEvePanel());
                } catch (IOException ex) {
                    Logger.getLogger(BkEvePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }   
}
