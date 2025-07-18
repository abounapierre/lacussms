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
import com.abouna.lacussms.views.model.MessageMandatUI;
import com.abouna.lacussms.views.tools.PrintReportPDF;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
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
public class MessageMandatPanel extends JPanel{
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final MainMenuPanel parentPanel;
    private final LacusSmsService serviceManager;
    private final JXDatePicker dateDeb, dateFin;
    private final JFileChooser fc = new JFileChooser();

    public MessageMandatPanel() throws IOException {
        serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        parentPanel = ApplicationConfig.getApplicationContext().getBean(MainMenuPanel.class);
        setLayout(new BorderLayout());
        JPanel haut = new JPanel();
        haut.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel lbl;
        lbl = new JLabel("GESTION DES MESSAGES");
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
        nouveau.setToolTipText("Ajouter un nouveau message");
        JButton supprimer = new JButton(new ImageIcon(supprImg));
        supprimer.setToolTipText("Suprimer un message");
        JButton modifier = new JButton(new ImageIcon(modifImg));
        modifier.setToolTipText("Modifier un message");
        nouveau.addActionListener(ae -> {
            Nouveau nouveau1 = new Nouveau(null);
            nouveau1.setSize(400, 300);
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
                        nouveau1 = new Nouveau(serviceManager.getBkEveById(id));
                        nouveau1.setSize(400, 300);
                        nouveau1.setLocationRelativeTo(null);
                        nouveau1.setModal(true);
                        nouveau1.setResizable(false);
                        nouveau1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        nouveau1.setVisible(true);
                    } catch (Exception ex) {
                        Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
                    String id = (String) tableModel.getValueAt(selected, 0);
                    int res = JOptionPane.showConfirmDialog(null, "Etes vous sûr de suppimer l'évenement courant?", "Confirmation",
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
                    JOptionPane.showMessageDialog(null, "Aucun élément selectionné");
                }
            }
        });
        JLabel labelNumber = new JLabel("Nombre de Messages");
        final JTextField numberText = new JTextField(10);
        JButton purgerBtn = new JButton("Purger");
        purgerBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int res = JOptionPane.showConfirmDialog(null, "Etes vous sûr de vouloir vider cette table?", "Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (res == JOptionPane.YES_OPTION) {
                    try {
                        int result = serviceManager.supprimerTout();
                        if(result != -1)
                            tableModel.setNumRows(0);
                    } catch (Exception ex) {
                        Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        bas.add(labelNumber);
        bas.add(numberText);
        bas.add(purgerBtn);
        JPanel filtrePanel = new JPanel();
        filtrePanel.setLayout(new FlowLayout());
        final JXSearchField searchField = new JXSearchField("Rechercher");
        searchField.setPreferredSize(new Dimension(500, 50));
        filtrePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), "Zone de recherche"));
        //filtrePanel.add(searchField);
        JLabel labelDate1 = new JLabel("DAte de début");
        dateDeb = new JXDatePicker();
        JLabel labelDate2 = new JLabel("DAte de fin");
        dateFin = new JXDatePicker();
        JButton filterBtn = new JButton("Filtrer");
        JButton printBtn = new JButton("Imprimer");
        filterBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Date d1 = null, d2 = null;
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    
                    d1 = format.parse(format.format(dateDeb.getDate()));
                    d2 = format.parse(format.format(dateFin.getDate()));
                    tableModel.setNumRows(0);
                    List<MessageMandat> messageList = serviceManager.getMessageMandatFromPeriode(d1, d2);
                    for (MessageMandat mandat : messageList) {
                        addDataToTable(toUi(mandat));
                    }

                } catch (Exception ex) {
                    Logger.getLogger(RapportPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        printBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Date d1 = null, d2 = null;
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        d1 = format.parse(format.format(dateDeb.getDate()));
                        d2 = format.parse(format.format(dateFin.getDate()));
                    if (d1 != null & d2 != null) {
                        //d1 = format.parse(format.format(dateDeb.getDate()));
                        //d2 = format.parse(format.format(dateFin.getDate()));
                        int val_retour = fc.showSaveDialog(MessageMandatPanel.this);
                        if (val_retour == JFileChooser.APPROVE_OPTION) {
                            File fichier = fc.getSelectedFile();
                            String path = fichier.getAbsolutePath() + ".pdf";
                            try {
                                PrintReportPDF report = new PrintReportPDF(path,d1,d2,serviceManager);
                            } catch (FileNotFoundException ex) {
                                Logger.getLogger(RapportPanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            int response = JOptionPane.showConfirmDialog(null, "<html>Rapport généré avec success!!<br>Voulez vous l'ouvrir?", "Confirmation",
                                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                            if (response == JOptionPane.YES_OPTION) {
                                try {
                                    File pdfFile = new File(path);
                                    if (pdfFile.exists()) {
                                        if (Desktop.isDesktopSupported()) {
                                            Desktop.getDesktop().open(pdfFile);
                                        } else {
                                            JOptionPane.showMessageDialog(MessageMandatPanel.this, "Ce type de fichier n'est pas pris en charge");
                                            System.out.println("Awt Desktop is not supported!");
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(MessageMandatPanel.this, "Ce fichier n'existe pas");
                                        System.out.println("File is not exists!");
                                    }
                                } catch (IOException | HeadlessException ex) {
                                    JOptionPane.showMessageDialog(MessageMandatPanel.this, "Erreur lors de l'ouverture du fichier: " + ex.getMessage());
                                    Logger.getLogger(RapportPanel.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(parentPanel, "les dates de début et de fin sont obligatoires");
                    }
                } catch (ParseException ex) {
                    Logger.getLogger(RapportPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        filtrePanel.add(labelDate1);
        filtrePanel.add(dateDeb);
        filtrePanel.add(labelDate2);
        filtrePanel.add(dateFin);
        filtrePanel.add(filterBtn);
        filtrePanel.add(printBtn);
        filtrePanel.setBackground(new Color(166, 202, 240));
        searchField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String val = null;
                if (searchField.getText() != null) {
                    try {
                        val = searchField.getText().toUpperCase();
                        tableModel.setNumRows(0);
                        List<MessageMandat> messageList = serviceManager.getAllMessageMandats();
                        for (MessageMandat a : messageList) {
                            addDataToTable(toUi(a));
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        contenu.add(BorderLayout.AFTER_LAST_LINE, bas);
        contenu.add(BorderLayout.BEFORE_FIRST_LINE, filtrePanel);
        tableModel = new DefaultTableModel(new Object[]{"Code", "Titre", "Contenu", "Date","Numéro", "Expediteur","Destinataire","Numero", "Agence"}, 0);

        table = new JTable(tableModel);
        table.setBackground(Color.WHITE);
        contenu.add(BorderLayout.CENTER, new JScrollPane(table));
        add(BorderLayout.CENTER, contenu);
        try {
            for (MessageMandat mandat : serviceManager.getAllMessageMandats()) {
                addDataToTable(toUi(mandat));
            }
            numberText.setText(Integer.toString(tableModel.getRowCount()));
        } catch (Exception ex) {
            Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private MessageMandatUI toUi(MessageMandat a) {
        MessageMandatUI ui = new MessageMandatUI();
        ui.setId(a.getId());
        ui.setTitle(a.getTitle());
        ui.setContent(a.getContent());
        ui.setSendDate(a.getSendDate());
        ui.setNumero(a.getNumero());
        ui.setAd1p("");
        ui.setAd2p("");
        ui.setClesec("");
        ui.setAge("");
        return ui;
    }

    private void addDataToTable(MessageMandatUI a) {
        tableModel.addRow(new Object[] {
                a.getId(),
                a.getTitle(),
                a.getContent(),
                a.getSendDate(),
                a.getNumero(),
                a.getAd1p(),
                a.getAd2p(),
                a.getClesec(),
                a.getAge()
        });
    }

    private class Nouveau extends JDialog {

        private final JTextField compteText;
        private final JComboBox<String> etatBox;
        private final JComboBox<BkOpe> bkOpeBox;
        private final JComboBox<BkCli> bkCliBox;
        private final JComboBox<BkAgence> bkAgenceBox;
        private final JTextField montText;
        private int c = 0, rang = 0, c1 = 0, rang1 = 0, c2 = 0, rang2 = 0;

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
            for (BkCli bkCli : serviceManager.getAllCli()) {
                bkCliBox.addItem(bkCli);
                if (bkeve != null) {
                    if (bkeve.getCli().getCode().equals(bkCli.getCode())) {
                        rang = c;
                    }
                }
                c++;
            }
            for (BkAgence bkagence : serviceManager.getAllBkAgences()) {
                bkAgenceBox.addItem(bkagence);
                if (bkeve != null) {
                    if (bkeve.getBkAgence().getNuma().equals(bkagence.getNuma())) {
                        rang2 = c2;
                    }
                }
                c++;
            }
            for (BkOpe bkOpe : serviceManager.getAllBkOpes()) {
                bkOpeBox.addItem(bkOpe);
                if (bkeve != null) {
                    if (bkeve.getOpe().getOpe().equals(bkOpe.getOpe())) {
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
                compteText.setText(bkeve.getCompte());
                bkCliBox.setSelectedIndex(rang);
                bkOpeBox.setSelectedIndex(rang1);
                bkAgenceBox.setSelectedIndex(rang2);
                montText.setText(Double.toString(bkeve.getMont()));
                if (bkeve.getEtat().equals("VA")) {
                    etatBox.setSelectedIndex(0);
                } else if (bkeve.getEtat().equals("AT")) {
                    etatBox.setSelectedIndex(1);
                } else if (bkeve.getEtat().equals("FO")) {
                    etatBox.setSelectedIndex(2);
                } else if (bkeve.getEtat().equals("VF")) {
                    etatBox.setSelectedIndex(2);
                } else if (bkeve.getEtat().equals("IG")) {
                    etatBox.setSelectedIndex(2);
                } else if (bkeve.getEtat().equals("IF")) {
                    etatBox.setSelectedIndex(2);
                } else if (bkeve.getEtat().equals("AB")) {
                    etatBox.setSelectedIndex(2);
                } else if (bkeve.getEtat().equals("AN")) {
                    etatBox.setSelectedIndex(7);
                } else if (bkeve.getEtat().equals("TR")) {
                    etatBox.setSelectedIndex(8);
                }
            }

            okBtn.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    BkEve a = new BkEve();
                    if (!compteText.getText().isEmpty()) {
                        a.setCompte(compteText.getText());
                    } else {
                        JOptionPane.showMessageDialog(null, "Le compte est obligatoire");
                        return;
                    }
                    if (!montText.getText().isEmpty()) {
                        a.setMont(Double.parseDouble(montText.getText()));
                    } else {
                        JOptionPane.showMessageDialog(null, "Le compte est obligatoire");
                        return;
                    }

                    a.setOpe((BkOpe) bkOpeBox.getSelectedItem());
                    a.setEtat((String) etatBox.getSelectedItem());
                    a.setCli((BkCli) bkCliBox.getSelectedItem());
                    a.setBkAgence((BkAgence) bkAgenceBox.getSelectedItem());
                    Date d = new Date();
                    //a.setHsai(d);
                    a.setSent(false);
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        a.setEventDate(format.parse(format.format(d)));
                    } catch (ParseException ex) {
                        Logger.getLogger(BkEvePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }

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
