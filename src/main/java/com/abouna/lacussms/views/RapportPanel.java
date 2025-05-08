
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.dto.SendResponseDTO;
import com.abouna.lacussms.entities.*;
import com.abouna.lacussms.sender.context.SenderContext;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.components.ContentMessageDialog;
import com.abouna.lacussms.views.main.MainMenuPanel;
import com.abouna.lacussms.views.tools.PrintReportPDF;
import com.abouna.lacussms.views.utils.CustomTable;
import com.abouna.lacussms.views.utils.CustomTableCellRenderer;
import com.abouna.lacussms.views.utils.DialogUtils;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXSearchField;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.abouna.lacussms.views.tools.ConstantUtils.NO_SELECTED_ITEM;

/**
 *
 * @author SATELLITE
 */
public class RapportPanel extends JPanel {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(RapportPanel.class);
    private DefaultTableModel tableModel;
    private JTable table;
    private final MainMenuPanel parentPanel;
    private final LacusSmsService serviceManager;
    private final JXDatePicker dateDeb, dateFin;
    private final JFileChooser fc = new JFileChooser();
    private final CustomTableCellRenderer renderer = new CustomTableCellRenderer();

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
        Image ajouImg = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/Ajouter.png")));
        Image supprImg = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/Cancel2.png")));
        Image modifImg = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/OK.png")));
        JButton nouveau = new JButton(new ImageIcon(ajouImg));
        nouveau.setToolTipText("Ajouter un nouveau message");
        JButton supprimer = new JButton(new ImageIcon(supprImg));
        supprimer.setToolTipText("Supprimer un message");
        JButton modifier = new JButton(new ImageIcon(modifImg));
        modifier.setToolTipText("Modifier un message");
        JButton filtre = new JButton("Filtrer");
        nouveau.addActionListener((ActionEvent ae) -> {
            DialogUtils.initDialog(new RapportPanel.Nouveau(null), RapportPanel.this.getParent(), 400, 300);
        });
        modifier.addActionListener((ActionEvent ae) -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                Integer id = (Integer) tableModel.getValueAt(selected, 0);
                try {
                    DialogUtils.initDialog(new RapportPanel.Nouveau(serviceManager.getBkEveById(id)), RapportPanel.this.getParent(), 400, 300);
                } catch (Exception ex) {
                    Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                JOptionPane.showMessageDialog(RapportPanel.this.getParent(), "Aucun élément n'est selectionné");
            }
        });
        supprimer.addActionListener((ActionEvent ae) -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                String id = (String) tableModel.getValueAt(selected, 0);
                int res = JOptionPane.showConfirmDialog(RapportPanel.this.getParent(), "Etes vous sûr de suppimer l'évenement courant?", "Confirmation",
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
                JOptionPane.showMessageDialog(RapportPanel.this.getParent(), "Aucun élément selectionné");
            }
        });
        JLabel labelNumber = new JLabel("Nombre de Messages");
        final JTextField numberText = new JTextField(10);
        JButton purgerBtn = getPurgeButton();
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
                    JOptionPane.showMessageDialog(RapportPanel.this.getParent(), "la date de début ou de fin ne doit pas etre vide");
                    return;
                }
                d1 = format.parse(format.format(dateDeb.getDate()));
                d2 = format.parse(format.format(dateFin.getDate()));
                tableModel.setNumRows(0);
                List<Message> messageList = serviceManager.getMessageFromPeriode(d1, d2);
                addData(messageList);

            } catch (HeadlessException | ParseException ex) {
                Logger.getLogger(RapportPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        printBtn.addActionListener((ActionEvent e) -> {
            try {
                Date date1, date2;
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

                if (dateDeb.getDate() == null || dateFin.getDate() == null) {
                    JOptionPane.showMessageDialog(RapportPanel.this.getParent(), "la date de début ou de fin ne doit pas etre vide");
                    return;
                }
                date1 = format.parse(format.format(dateDeb.getDate()));
                date2 = format.parse(format.format(dateFin.getDate()));
                if (date1 != null & date2 != null) {
                    int valRetour = fc.showSaveDialog(RapportPanel.this);
                    if (valRetour == JFileChooser.APPROVE_OPTION) {
                        File fichier = fc.getSelectedFile();
                        String path = fichier.getAbsolutePath() + ".pdf";
                        try {
                            PrintReportPDF report = new PrintReportPDF(path, date1, date2, serviceManager);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(RapportPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        int response = JOptionPane.showConfirmDialog(RapportPanel.this.getParent(), "<html>Rapport généré avec success!!<br>Voulez vous l'ouvrir?", "Confirmation",
                                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        if (response == JOptionPane.YES_OPTION) {
                            try {
                                File pdfFile = new File(path);
                                if (pdfFile.exists()) {
                                    if (Desktop.isDesktopSupported()) {
                                        Desktop.getDesktop().open(pdfFile);
                                    } else {
                                        JOptionPane.showMessageDialog(RapportPanel.this.getParent(), "Ce type de fichier n'est pas pris en charge");
                                        log.info("Awt Desktop is not supported!");
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(RapportPanel.this.getParent(), "Ce fichier n'existe pas");
                                    System.out.println("File is not exists!");
                                }
                            } catch (IOException | HeadlessException ex) {
                                log.error("Erreur d'ouverture du fichier PDF", ex);
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
            if (searchField.getText() != null) {
                try {
                    tableModel.setNumRows(0);
                   addData(serviceManager.getAllMessages());
                } catch (Exception ex) {
                    Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        contenu.add(BorderLayout.AFTER_LAST_LINE, bas);
        contenu.add(BorderLayout.BEFORE_FIRST_LINE, filtrePanel);
        tableModel = new DefaultTableModel(new Object[]{"ID", "Titre", "Contenu", "Date", "Numéro", "Client", "Agence"}, 0);
        table = new CustomTable(tableModel, renderer);
        table.setComponentPopupMenu(getPopupMenu());
        table.getColumnModel().getColumn(0).setPreferredWidth(20);
        table.getColumnModel().getColumn(2).setPreferredWidth(350);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel. SINGLE_SELECTION);
        table.setSelectionForeground(Color.BLUE);
        contenu.add(BorderLayout.CENTER, new JScrollPane(table));
        add(BorderLayout.CENTER, contenu);
        try {
            addData(serviceManager.getAllMessages());
            numberText.setText(Integer.toString(tableModel.getRowCount()));
        } catch (Exception ex) {
            Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addData(List<Message> messageList) {
        renderer.setSelectedRows(new ArrayList<>());
        messageList.forEach(a -> tableModel.addRow(new Object[]{
            a.getId(),
            a.getTitle(),
            a.getContent(),
            a.getSendDate(),
            a.getNumero(),
            a.getBkEve() == null ? "" : a.getBkEve().getCli() == null ? "" : a.getBkEve().getCli().getNom() + " " + a.getBkEve().getCli().getPrenom(),
            a.getBkEve() == null ? "" : a.getBkEve().getBkAgence() == null ? "" : a.getBkEve().getBkAgence().getNoma()
        }));
    }

    private JPopupMenu getPopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem detailsItem = new JMenuItem("Détails");
        detailsItem.addActionListener((ActionEvent e) -> getDetailsEvent());
        JMenuItem resendEventItem = new JMenuItem("Renvoyer SMS");
        resendEventItem.addActionListener((ActionEvent e) -> getResendSmsEvent());
        popup.add(detailsItem);
        popup.add(resendEventItem);
        return popup;
    }

    private void getResendSmsEvent() {
        int selected = table.getSelectedRow();
        if (selected >= 0) {
            Integer id = (Integer) tableModel.getValueAt(selected, 0);
            try {
                resendMessage(serviceManager.getMessageById(id));
            } catch (Exception ex) {
                Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(RapportPanel.this.getParent(), NO_SELECTED_ITEM);
        }
    }

    private void resendMessage(Message message) {
        try {
            SendResponseDTO sendResponseDTO = SenderContext.getInstance().send(message.getNumero(), message.getContent());
            if (sendResponseDTO.isSent()) {
                 updateBkEve(message);
            }
            JOptionPane.showMessageDialog(RapportPanel.this.getParent(), sendResponseDTO.getMessage());
        } catch (Exception e) {
            com.abouna.lacussms.views.utils.Logger.error("Erreur d'envoi du message", e, RapportPanel.class);
        }
    }

    private void updateBkEve(Message message) {
        if(message.getBkEve() == null) {
            return;
        }
        BkEve bkEve = serviceManager.getBkEveById(message.getBkEve().getId());
        if (bkEve == null) {
            return;
        }
        bkEve.setSent(true);
        serviceManager.modifier(bkEve);
        message.setSent(true);
        message.setSendDate(new Date());
        serviceManager.modifier(message);
    }

    private void getDetailsEvent() {
        int selected = table.getSelectedRow();
        if (selected >= 0) {
            Integer id = (Integer) tableModel.getValueAt(selected, 0);
            try {
                ContentMessageDialog.initDialog(serviceManager.getMessageById(id));
            } catch (Exception ex) {
                Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(RapportPanel.this.getParent(), NO_SELECTED_ITEM);
        }
    }

    private JButton getPurgeButton() {
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
        return purgerBtn;
    }

    private class Nouveau extends JDialog {

        private final JTextField compteText;
        private final JComboBox<String> etatBox;
        private final JComboBox<BkOpe> bkOpeBox;
        private final JComboBox<BkCli> bkCliBox;
        private final JComboBox<BkAgence> bkAgenceBox;
        private final JTextField montText;
        private int c = 0;
        private int rang = 0;
        private int c1 = 0;
        private int rang1 = 0;
        private final int c2 = 0;
        private int rang2 = 0;

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
            serviceManager.getAllCli().stream().peek(bkCliBox::addItem).peek((bkCli) -> {
                if (bkeve != null) {
                    if (bkeve.getCli().getCode().equals(bkCli.getCode())) {
                        rang = c;
                    }
                }
            }).forEach((_item) -> {
                c++;
            });
            serviceManager.getAllBkAgences().stream().peek(bkAgenceBox::addItem).peek((bkagence) -> {
                if (bkeve != null) {
                    if (bkeve.getBkAgence().getNuma().equals(bkagence.getNuma())) {
                        rang2 = c2;
                    }
                }
            }).forEach((_item) -> {
                c++;
            });
            serviceManager.getAllBkOpes().stream().peek(bkOpeBox::addItem).peek((bkOpe) -> {
                if (bkeve != null) {
                    if (bkeve.getOpe().getOpe().equals(bkOpe.getOpe())) {
                        rang1 = c1;
                    }
                }
            }).forEach((_item) -> {
                c1++;
            });
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
                switch (bkeve.getEtat()) {
                    case "VA":
                        etatBox.setSelectedIndex(0);
                        break;
                    case "AT":
                        etatBox.setSelectedIndex(1);
                        break;
                    case "FO":
                    case "VF":
                    case "IG":
                    case "IF":
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
                if (!compteText.getText().isEmpty()) {
                    a.setCompte(compteText.getText());
                } else {
                    JOptionPane.showMessageDialog(RapportPanel.this.getParent(), "Le compte est obligatoire");
                    return;
                }
                if (!montText.getText().isEmpty()) {
                    a.setMont(Double.parseDouble(montText.getText()));
                } else {
                    JOptionPane.showMessageDialog(RapportPanel.this.getParent(), "Le compte est obligatoire");
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
