/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.UrlMessage;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SATELLITE
 */
public class SmsProgrammingPanel extends JPanel{
    private DefaultTableModel tableModel;
    private JTable table;
    private final MainMenuPanel parentPanel;
    private final LacusSmsService serviceManager;
    
    public SmsProgrammingPanel() throws IOException{
        serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        parentPanel = ApplicationConfig.getApplicationContext().getBean(MainMenuPanel.class);
        setLayout(new BorderLayout());
        JPanel haut = new JPanel();
        haut.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel lbl;
        lbl = new JLabel("PROGRAMMATION DES MESSAGES");
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
        nouveau.setToolTipText("Programmer l'envoie des messages");
        JButton supprimer = new JButton(new ImageIcon(supprImg));
        supprimer.setToolTipText("Suprimer un programme");
        JButton modifier = new JButton(new ImageIcon(modifImg));
        modifier.setToolTipText("Modifier un programme");
        nouveau.addActionListener((ActionEvent ae) -> DialogUtils.initDialog(new Nouveau(null), SmsProgrammingPanel.this.getParent(), 700, 500));
        modifier.addActionListener((ActionEvent ae) -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                Integer id = (Integer) tableModel.getValueAt(selected, 0);
                try {
                    DialogUtils.initDialog(new Nouveau(serviceManager.getUrlMessageById(id)), SmsProgrammingPanel.this.getParent(), 700, 500);
                } catch (Exception ex) {
                    Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(SmsProgrammingPanel.this.getParent(), "Aucun élément n'est selectionné");
            }
        });
        supprimer.addActionListener((ActionEvent ae) -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                Integer id = (Integer) tableModel.getValueAt(selected, 0);
                int res = JOptionPane.showConfirmDialog(SmsProgrammingPanel.this.getParent(), "Etes vous sûr de suppimer ce programme?", "Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (res == JOptionPane.YES_OPTION) {
                    try {
                        serviceManager.supprimerUrlMessage(id);
                    } catch (Exception ex) {
                        Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    tableModel.removeRow(selected);
                }
            } else {
                JOptionPane.showMessageDialog(SmsProgrammingPanel.this.getParent(), "Aucun élément selectionné");
            }
        });
        JButton testBtn = new JButton("Test Connexion");
        testBtn.addActionListener((ActionEvent e) -> {
            if(Utils.testConnexion(serviceManager, ConstantUtils.SECRET_KEY) != null){
                JOptionPane.showMessageDialog(parentPanel, "Connexion réussie");
            }else{
                JOptionPane.showMessageDialog(parentPanel, "Erreur lors de l'établissement de la connexion!");
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
            if (searchField.getText() != null) {
                try {
                    tableModel.setNumRows(0);
                    List<UrlMessage> urlMessageList = serviceManager.getAllUrlMessages();
                    urlMessageList.forEach((a) -> tableModel.addRow(new Object[]{
                        a.getId(),
                        a.getUrlValue(),
                        a.getMethode().equals("METHO1") ? "Méthode 1" : "Méthode 2",
                        a.isDefaultUrl()
                    }));
                } catch (Exception ex) {
                    Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        contenu.add(BorderLayout.AFTER_LAST_LINE, bas);
        contenu.add(BorderLayout.BEFORE_FIRST_LINE, filtrePanel);
        tableModel = new DefaultTableModel(new Object[]{"id", "Url","Methode","Par défaut?"}, 0);

        table = new JTable(tableModel);
        table.setBackground(Color.WHITE);
        table.getColumnModel().getColumn(1).setPreferredWidth(350);
        //table.removeColumn(table.getColumnModel().getColumn(0));
        contenu.add(BorderLayout.CENTER, new JScrollPane(table));
        add(BorderLayout.CENTER, contenu);
        try {
            serviceManager.getAllUrlMessages().forEach((a) -> {
                tableModel.addRow(new Object[]{
                    a.getId(),
                    a.getUrlValue(),
                    a.getMethode().equals("METHO1")?"Méthode 1":"Méthode 2",
                    a.isDefaultUrl()
                });
            });
        } catch (Exception ex) {
            Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class Nouveau extends JDialog {

        private final JTextArea contactsText;
        //private final JCheckBox chkBox;
        //private final JComboBox<String> methodeBox;

        public Nouveau(final UrlMessage url) {
            setTitle("PROG SMS");
            setModal(true);
            setLayout(new BorderLayout(10, 10));
            JPanel haut = new JPanel();
            JLabel lbl;
            haut.add(lbl = new JLabel("<html><font color = #012345 > AJOUT D'UNE NOUVELLE URL POUR MESSAGE </font></html>"));
            lbl.setFont(new Font("Times New Roman", Font.ITALIC, 18));
            add(BorderLayout.BEFORE_FIRST_LINE, haut);
            DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 180dlu:", ""));
            builder.setDefaultDialogBorder();
            contactsText = new JTextArea(20, 80);
            builder.append("Contacts", new JScrollPane(contactsText));
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showOpenDialog(null);
            builder.append("", chooser);
            //builder.append("Methode d'envoie", methodeBox = new JComboBox<>());
            //builder.append("Par défaut?", chkBox = new JCheckBox());
            contactsText.setSize(20, 80);
            contactsText.setLineWrap(true);
            contactsText.setWrapStyleWord(true);
            JButton okBtn;
            JButton annulerBtn;
            JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Enrégistrer"), annulerBtn = new JButton("Annuler"));
            builder.append(buttonBar, builder.getColumnCount());
            add(BorderLayout.CENTER, new JScrollPane(builder.getPanel()));
            //methodeBox.addItem("Méthode 1");
            //methodeBox.addItem("Méthode 2");
            if (url != null) {
               String url1 = url.getUrlValue();
                        String[] tab = url1.split("=");
                        for(int i = 1;i<tab.length;i++){
                            String[] t2 = tab[i].split("&"); //URLEncoder.encode(t2[0], "ISO-8859-1")
                            if(!t2[0].contains("<")){
                                try {
                                    if(t2.length == 2)
                                        url1 = url1.replace(t2[0], URLDecoder.decode(t2[0], "UTF-8")).replace(t2[1], URLDecoder.decode(t2[1], "UTF-8"));
                                    else
                                        url1 = url1.replace(t2[0], URLDecoder.decode(t2[0], "UTF-8"));
                                     } catch (UnsupportedEncodingException ex) {
                                    Logger.getLogger(UrlParamPanel.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
               contactsText.setText(url1);
            }

            okBtn.addActionListener((ActionEvent ae) -> {
                UrlMessage a = new UrlMessage();
                if (!contactsText.getText().equals("")) {
                    String url1 = contactsText.getText();
                    String[] tab = url1.split("=");
                    for (int i = 1; i<tab.length; i++) {
                        String[] t2 = tab[i].split("&");
                        if (!t2[0].contains("<")) {
                            try {
                                if (t2.length == 2) {
                                    url1 = url1.replace(t2[0], URLEncoder.encode(t2[0], "UTF-8")).replace(t2[1], URLEncoder.encode(t2[1], "UTF-8"));
                                } else {
                                    url1 = url1.replace(t2[0], URLEncoder.encode(t2[0], "UTF-8"));
                                }
                            }catch (UnsupportedEncodingException ex) {
                                Logger.getLogger(UrlParamPanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    a.setUrlValue(url1);
                } else {
                    JOptionPane.showMessageDialog(null, "L'url est obligatoire");
                    return;
                }

                if(a.isDefaultUrl()){
                    serviceManager.getAllUrlMessages().stream().peek((r) -> r.setDefaultUrl(false)).forEach(serviceManager::modifier);
                }
                int i = 0;
                StringBuilder r = new StringBuilder();
                String var = contactsText.getText();
                while (contactsText.getText().charAt(i) != '?') {
                    r.append(contactsText.getText().charAt(i));
                    var = var.replace(""+contactsText.getText().charAt(i), "");
                    i++;
                }
                var = var.replace("?", "");
                a.setRoot(r.toString());
                a.setParamString(var);
                if (url == null) {
                    try {
                        serviceManager.enregistrer(a);
                    } catch (Exception ex) {
                        Logger.getLogger(RemoteDBPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    a.setId(url.getId());
                    try {
                        serviceManager.modifier(a);
                    } catch (Exception ex) {
                        Logger.getLogger(UrlParamPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                dispose();
                try {
                    parentPanel.setContent(new UrlParamPanel());
                } catch (IOException ex) {
                    Logger.getLogger(UrlParamPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            annulerBtn.addActionListener((ActionEvent ae) -> {
                dispose();
                try {
                    parentPanel.setContent(new UrlParamPanel());
                } catch (IOException ex) {
                    Logger.getLogger(UrlParamPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }
}
