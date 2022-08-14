
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.BkAgence;
import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.entities.BkEve;
import com.abouna.lacussms.entities.BkOpe;
import com.abouna.lacussms.entities.Message;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.main.MainMenuPanel;
import com.abouna.lacussms.views.tools.PrintReportPDF;
import com.abouna.lacussms.views.utils.CustomTableCellRenderer;
import com.abouna.lacussms.views.utils.CustomTableModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXSearchField;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author SATELLITE
 */
public class RapportPanel extends JPanel {

    private CustomTableModel tableModel;
    private JTable table;
    private final JButton nouveau, modifier, supprimer;
    private final JButton filtre;
    @Autowired
    private MainMenuPanel parentPanel;
    @Autowired
    private LacusSmsService serviceManager;
    private final JXDatePicker dateDeb, dateFin;
    private final JFileChooser fc = new JFileChooser();

    public RapportPanel() throws IOException {
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
        Image ajouImg = ImageIO.read(getClass().getResource("/images/Ajouter.png"));
        Image supprImg = ImageIO.read(getClass().getResource("/images/Cancel2.png"));
        Image modifImg = ImageIO.read(getClass().getResource("/images/OK.png"));
        nouveau = new JButton(new ImageIcon(ajouImg));
        nouveau.setToolTipText("Ajouter un nouveau message");
        supprimer = new JButton(new ImageIcon(supprImg));
        supprimer.setToolTipText("Suprimer un message");
        modifier = new JButton(new ImageIcon(modifImg));
        modifier.setToolTipText("Modifier un message");
        filtre = new JButton("Filtrer");
        nouveau.addActionListener((ActionEvent ae) -> {
            Nouveau nouveau1 = new Nouveau(null);
            nouveau1.setSize(400, 300);
            nouveau1.setLocationRelativeTo(null);
            nouveau1.setModal(true);
            nouveau1.setResizable(false);
            nouveau1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            nouveau1.setVisible(true);
        });
        modifier.addActionListener((ActionEvent ae) -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                Integer id = (Integer) tableModel.getValueAt(selected, 0);
                Nouveau nouveau1;
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
        });
        supprimer.addActionListener((ActionEvent ae) -> {
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
        });
        JLabel labelNumber = new JLabel("Nombre de Messages");
        final JTextField numberText = new JTextField(10);
        JButton purgerBtn = new JButton("Purger");
        purgerBtn.addActionListener((ActionEvent e) -> {
            int res = JOptionPane.showConfirmDialog(null, "Etes vous sûr de vouloir vider cette table?", "Confirmation",
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (res == JOptionPane.YES_OPTION) {
                try {
                    int result = serviceManager.supprimerToutMessage();
                    if (result != -1) {
                        tableModel.setNumRows(0);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
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
        filterBtn.addActionListener((ActionEvent e) -> {
            try {
                Date d1, d2;
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

                if (dateDeb.getDate() == null || dateFin.getDate() == null) {
                    JOptionPane.showMessageDialog(null, "la date de début ou de fin ne doit pas etre vide");
                    return;
                }
                d1 = format.parse(format.format(dateDeb.getDate()));
                d2 = format.parse(format.format(dateFin.getDate()));
                tableModel.setNumRows(0);
                List<Message> messageList = serviceManager.getMessageFromPeriode(d1, d2);
                messageList.stream().forEach((a) -> {
                    tableModel.addRow(new Object[]{
                        a.getId(),
                        a.getTitle(),
                        a.getContent(),
                        a.getSendDate(),
                        a.getNumero(),
                        a.getBkEve() == null ? "" : a.getBkEve().getCli() == null ? "" : a.getBkEve().getCli().getNom() + " " + a.getBkEve().getCli().getPrenom(),
                        a.getBkEve() == null ? "" : a.getBkEve().getBkAgence() == null ? "" : a.getBkEve().getBkAgence().getNoma()
                    });
                });

            } catch (HeadlessException | ParseException ex) {
                Logger.getLogger(RapportPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        printBtn.addActionListener((ActionEvent e) -> {
            try {
                Date d1, d2;
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

                if (dateDeb.getDate() == null || dateFin.getDate() == null) {
                    JOptionPane.showMessageDialog(null, "la date de début ou de fin ne doit pas etre vide");
                    return;
                }
                d1 = format.parse(format.format(dateDeb.getDate()));
                d2 = format.parse(format.format(dateFin.getDate()));
                if (d1 != null & d2 != null) {
                    //d1 = format.parse(format.format(dateDeb.getDate()));
                    //d2 = format.parse(format.format(dateFin.getDate()));
                    int val_retour = fc.showSaveDialog(RapportPanel.this);
                    if (val_retour == JFileChooser.APPROVE_OPTION) {
                        File fichier = fc.getSelectedFile();
                        String path = fichier.getAbsolutePath() + ".pdf";
                        try {
                            PrintReportPDF report = new PrintReportPDF(path, d1, d2, serviceManager);
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
                                        JOptionPane.showMessageDialog(RapportPanel.this, "Ce type de fichier n'est pas pris en charge");
                                        System.out.println("Awt Desktop is not supported!");
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(RapportPanel.this, "Ce fichier n'existe pas");
                                    System.out.println("File is not exists!");
                                }
                            } catch (IOException | HeadlessException ex) {
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(parentPanel, "les dates de début et de fin sont obligatoires");
                }
            } catch (ParseException ex) {
                Logger.getLogger(RapportPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        filtrePanel.add(labelDate1);
        filtrePanel.add(dateDeb);
        filtrePanel.add(labelDate2);
        filtrePanel.add(dateFin);
        filtrePanel.add(filterBtn);
        filtrePanel.add(printBtn);
        filtrePanel.setBackground(new Color(166, 202, 240));
        searchField.addActionListener((ActionEvent e) -> {
            String val = null;
            if (searchField.getText() != null) {
                try {
                    val = searchField.getText().toUpperCase();
                    tableModel.setNumRows(0);
                    List<Message> messageList = serviceManager.getAllMessages();
                    messageList.stream().forEach((a) -> {
                        tableModel.addRow(new Object[]{
                            a.getId(),
                            a.getTitle(),
                            a.getContent(),
                            a.getSendDate(),
                            a.getNumero(),
                            a.getBkEve() == null ? "" : a.getBkEve().getCli() == null ? "" : a.getBkEve().getCli().getNom() + " " + a.getBkEve().getCli().getPrenom(),
                            a.getBkEve() == null ? "" : a.getBkEve().getBkAgence() == null ? "" : a.getBkEve().getBkAgence().getNoma()
                        });
                    });
                } catch (Exception ex) {
                    Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        contenu.add(BorderLayout.AFTER_LAST_LINE, bas);
        contenu.add(BorderLayout.BEFORE_FIRST_LINE, filtrePanel);
        tableModel = new CustomTableModel(new Object[]{"Code 1", "Titre", "Contenu", "Date", "Numéro", "Client", "Agence"}, 0);
        //tableModel.setRowColour(1, Color.red);
        table = new JTable(tableModel);
        table.setBackground(Color.WHITE);
        //table.getColumnModel().getColumn(2).setPreferredWidth(280);
        //table.removeColumn(table.getColumnModel().getColumn(0));
        CustomTableCellRenderer renderer = new CustomTableCellRenderer();
        table.getColumnModel().getColumn(0).setCellRenderer(renderer);
        table.getColumnModel().getColumn(1).setCellRenderer(renderer);
        table.getColumnModel().getColumn(2).setCellRenderer(renderer);
        contenu.add(BorderLayout.CENTER, new JScrollPane(table));
        add(BorderLayout.CENTER, contenu);
        try {
            serviceManager.getAllMessages().stream().forEach((a) -> {
                tableModel.addRow(new Object[]{
                    a.getId(),
                    a.getTitle(),
                    a.getContent(),
                    a.getSendDate(),
                    a.getNumero(),
                    a.getBkEve() == null ? "" : a.getBkEve().getCli() == null ? "" : a.getBkEve().getCli().getNom() + " " + a.getBkEve().getCli().getPrenom(),
                    a.getBkEve() == null ? "" : a.getBkEve().getBkAgence() == null ? "" : a.getBkEve().getBkAgence().getNoma()
                });
            });
            numberText.setText(Integer.toString(tableModel.getRowCount()));
        } catch (Exception ex) {
            Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class Nouveau extends JDialog {

        private final JButton okBtn, annulerBtn;
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
            builder.append("Agence", bkAgenceBox = new JComboBox<>());
            builder.append("Client", bkCliBox = new JComboBox<>());
            builder.append("Opération", bkOpeBox = new JComboBox<>());
            builder.append("Numéro compte", compteText = new JTextField(50));
            builder.append("Montant", montText = new JTextField(50));
            builder.append("Etat", etatBox = new JComboBox<>());
            etatBox.addItem("VA");
            etatBox.addItem("AT");
            etatBox.addItem("FO");
            etatBox.addItem("VF");
            etatBox.addItem("IG");
            etatBox.addItem("IF");
            etatBox.addItem("AB");
            etatBox.addItem("AN");
            etatBox.addItem("TR");
            serviceManager.getAllCli().stream().map((bkCli) -> {
                bkCliBox.addItem(bkCli);
                return bkCli;
            }).map((bkCli) -> {
                if (bkeve != null) {
                    if (bkeve.getCli().getCode().equals(bkCli.getCode())) {
                        rang = c;
                    }
                }
                return bkCli;
            }).forEach((_item) -> {
                c++;
            });
            serviceManager.getAllBkAgences().stream().map((bkagence) -> {
                bkAgenceBox.addItem(bkagence);
                return bkagence;
            }).map((bkagence) -> {
                if (bkeve != null) {
                    if (bkeve.getBkAgence().getNuma().equals(bkagence.getNuma())) {
                        rang2 = c2;
                    }
                }
                return bkagence;
            }).forEach((_item) -> {
                c++;
            });
            serviceManager.getAllBkOpes().stream().map((bkOpe) -> {
                bkOpeBox.addItem(bkOpe);
                return bkOpe;
            }).map((bkOpe) -> {
                if (bkeve != null) {
                    if (bkeve.getOpe().getOpe().equals(bkOpe.getOpe())) {
                        rang1 = c1;
                    }
                }
                return bkOpe;
            }).forEach((_item) -> {
                c1++;
            });
            JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Enrégistrer"), annulerBtn = new JButton("Annuler"));
            builder.append(buttonBar, builder.getColumnCount());
            add(BorderLayout.CENTER, builder.getPanel());

            if (bkeve != null) {
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
                        etatBox.setSelectedIndex(2);
                        break;
                    case "VF":
                        etatBox.setSelectedIndex(2);
                        break;
                    case "IG":
                        etatBox.setSelectedIndex(2);
                        break;
                    case "IF":
                        etatBox.setSelectedIndex(2);
                        break;
                    case "AB":
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

            okBtn.addActionListener((ActionEvent ae) -> {
                BkEve a = new BkEve();
                if (!compteText.getText().equals("")) {
                    a.setCompte(compteText.getText());
                } else {
                    JOptionPane.showMessageDialog(null, "Le compte est obligatoire");
                    return;
                }
                if (!montText.getText().equals("")) {
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
