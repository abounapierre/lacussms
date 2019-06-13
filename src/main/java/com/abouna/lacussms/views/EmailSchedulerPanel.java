/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.OffsetBase;
import com.abouna.lacussms.entities.SentMail;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.service.impl.SchedulerService;
import com.abouna.lacussms.views.main.MainMenuPanel;
import com.abouna.lacussms.views.tools.TimeZoneUtils;
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
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.PlainDocument;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXSearchField;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author SATELLITE
 */
public class EmailSchedulerPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(EmailSchedulerPanel.class);
    @Autowired
    private LacusSmsService serviceManager;
    private DefaultTableModel tableModel;
    private JTable table;
    private JButton nouveau, modifier, supprimer;
    @Autowired
    private MainMenuPanel parentPanel;
    @Autowired
    private SchedulerService schedulerService;

    public EmailSchedulerPanel() {
        try {
            serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
            parentPanel = ApplicationConfig.getApplicationContext().getBean(MainMenuPanel.class);
            schedulerService = ApplicationConfig.getApplicationContext().getBean(SchedulerService.class);
            setLayout(new BorderLayout());
            JPanel haut = new JPanel();
            haut.setLayout(new FlowLayout(FlowLayout.CENTER));
            JLabel lbl;
            lbl = new JLabel("PROGRAMMER UN ENVOIE DE MAIL");
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
            nouveau.setToolTipText("Programmer un envoie de mail");
            supprimer = new JButton(new ImageIcon(supprImg));
            supprimer.setToolTipText("Suprimer un programme de mail");
            modifier = new JButton(new ImageIcon(modifImg));
            modifier.setToolTipText("Modifier une programmation de mail");
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
                        nouveau1 = new Nouveau(null);
                        nouveau1.setSize(400, 300);
                        nouveau1.setLocationRelativeTo(null);
                        nouveau1.setModal(true);
                        nouveau1.setResizable(false);
                        nouveau1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        nouveau1.setVisible(true);
                    } catch (Exception ex) {
                        java.util.logging.Logger.getLogger(EmailSchedulerPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Aucun élément n'est selectionné");
                }
            });
            supprimer.addActionListener((ActionEvent ae) -> {
                int selected = table.getSelectedRow();
                if (selected >= 0) {
                    Integer id = (Integer) tableModel.getValueAt(selected, 0);
                    int res = JOptionPane.showConfirmDialog(null, "Etes vous sûr de suppimer le service courant?", "Confirmation",
                            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    if (res == JOptionPane.YES_OPTION) {
                        try {
                            serviceManager.supprimerService(id);
                        } catch (Exception ex) {
                            java.util.logging.Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        tableModel.removeRow(selected);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Aucun élément selectionné");
                }
            });

            bas.add(nouveau);
            bas.add(modifier);
            bas.add(supprimer);
            //bas.add(exportBtn);
            JPanel filtrePanel = new JPanel();
            filtrePanel.setLayout(new FlowLayout());
            final JXSearchField searchField = new JXSearchField("Rechercher");
            searchField.setPreferredSize(new Dimension(500, 50));
            filtrePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), "Zone de recherche"));
            filtrePanel.add(searchField);
            filtrePanel.setBackground(new Color(166, 202, 240));
            searchField.addActionListener((ActionEvent e) -> {
            });
            contenu.add(BorderLayout.AFTER_LAST_LINE, bas);
            contenu.add(BorderLayout.BEFORE_FIRST_LINE, filtrePanel);
            tableModel = new DefaultTableModel(new Object[]{"ID", "Code", "Libellé", "Montant", "Actif?"}, 0);

            table = new JTable(tableModel);
            table.setBackground(Color.WHITE);
            //table.getColumnModel().getColumn(2).setPreferredWidth(280);
            table.removeColumn(table.getColumnModel().getColumn(0));
            contenu.add(BorderLayout.CENTER, new JScrollPane(table));
            add(BorderLayout.CENTER, contenu);
            serviceManager.getServiceOfferts().stream().forEach((a) -> {
                tableModel.addRow(new Object[]{
                    a.getId(),
                    a.getCode(),
                    a.getLibelle(),
                    a.getMontant(),
                    a.isActif()
                });
            });
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(EmailSchedulerPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class Nouveau extends JDialog {

        private final JButton okBtn, annulerBtn;
        private final JTextField emailTxt;
        private final JTextField subjectTxt;
        private final JTextArea contentTxt;
        private final JXDatePicker dateTxt;
        private final JComboBox<String> timeZoneBox;

        public Nouveau(final SentMail sentMail) {
            setTitle("NOUVEAU PROGRAMME DE MAIL");
            setModal(true);
            setLayout(new BorderLayout(10, 10));
            JPanel haut = new JPanel();
            JLabel lbl;
            haut.add(lbl = new JLabel("<html><font color = #012345 > NOUVEAU PROGRAMME DE MAIL </font></html>"));
            lbl.setFont(new Font("Times New Roman", Font.ITALIC, 18));
            add(BorderLayout.BEFORE_FIRST_LINE, haut);
            DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 110dlu:", ""));
            builder.setDefaultDialogBorder();
            builder.append("Date", dateTxt = new JXDatePicker());
            builder.append("Email", emailTxt = new JTextField(50));
            builder.append("Entête", subjectTxt = new JTextField(50));
            builder.append("Contenu", contentTxt = new JTextArea(new PlainDocument()));
            builder.append("Time Zone", timeZoneBox = new JComboBox<>());
            TimeZoneUtils timeZoneUtils = new TimeZoneUtils();
            timeZoneUtils.getTimeZoneList(OffsetBase.GMT).stream().forEach((timeZone) -> {
                timeZoneBox.addItem(timeZone);
            });
            JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Enrégistrer"), annulerBtn = new JButton("Annuler"));
            builder.append(buttonBar, builder.getColumnCount());
            add(BorderLayout.CENTER, builder.getPanel());

            if (sentMail != null) {
                emailTxt.setText(sentMail.getEmail());
                subjectTxt.setText(sentMail.getSubject());
                contentTxt.setText(sentMail.getContent());
                Date out = Date.from(sentMail.getDateTime().atZone(ZoneId.systemDefault()).toInstant());
                dateTxt.setDate(out);
            }

            okBtn.addActionListener((ActionEvent ae) -> {
                SentMail a = new SentMail();
                if (!contentTxt.getText().equals("")) {
                    a.setContent(contentTxt.getText());
                } else {
                    JOptionPane.showMessageDialog(null, "Le contenu est obligatoire");
                }

                if (!subjectTxt.getText().equals("")) {
                    a.setSubject(subjectTxt.getText());
                } else {
                    JOptionPane.showMessageDialog(null, "L'entête du mail est obligatoire");
                }

                if (!emailTxt.getText().equals("")) {
                    a.setEmail(emailTxt.getText());
                } else {
                    JOptionPane.showMessageDialog(null, "vous devez renseigner au moins une adresse mail");
                }

                if (dateTxt.getDate() != null) {
                    LocalDateTime ldt = LocalDateTime.ofInstant(dateTxt.getDate().toInstant(), ZoneId.systemDefault());
                    a.setDateTime(ldt);
                } else {
                    JOptionPane.showMessageDialog(null, "La date du mail est obligatoire");
                }
                String zone = (String) timeZoneBox.getSelectedItem();
                String[] ss = zone.split(" ");
                a.setTimeZone(ZoneId.of(ss[1]));

                if (sentMail != null) {
                    a.setId(sentMail.getId());
                }
                try {
                    String s = schedulerService.scheduledEmail(a);
                    JOptionPane.showMessageDialog(null, s);
                } catch (SchedulerException ex) {
                    java.util.logging.Logger.getLogger(EmailSchedulerPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                /*if (a.getId() != null) {
                    serviceManager.saveMail(a);
                } else {
                    serviceManager.saveMail(a);
                }*/
                dispose();
                parentPanel.setContenu(new EmailSchedulerPanel());
            });

            annulerBtn.addActionListener((ActionEvent ae) -> {
                dispose();
                parentPanel.setContenu(new EmailSchedulerPanel());
            });
        }
    }
}
