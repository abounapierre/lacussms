/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.Message;
import com.abouna.lacussms.entities.UrlMessage;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.main.BottomPanel;
import com.abouna.lacussms.views.main.HeaderMenu;
import com.abouna.lacussms.views.tools.Utils;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SATELLITE
 */
public class EnvoieSMSDialog extends JDialog {

    private final JTextField nameText;
    private final JTextArea contentText;
    private final LacusSmsService serviceManager;
    private String titre, contenu;

    public EnvoieSMSDialog() {
        serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        setTitle("ENVOIE DE MESSAGE PUSH");
        setModal(true);
        setLayout(new BorderLayout(10, 10));
        JPanel haut = new JPanel();
        JLabel lbl;
        haut.add(lbl = new JLabel("<html><font color = #012345 > ENVOIE DE MESSAGE </font></html>"));
        lbl.setFont(new Font("Times New Roman", Font.ITALIC, 18));
        add(BorderLayout.BEFORE_FIRST_LINE, haut);
        DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 180dlu:", ""));
        builder.setDefaultDialogBorder();
        builder.append("Titre", nameText = new JTextField(50));
        builder.append("Contenu de message", contentText = new JTextArea(5, 50));
        //builder.append("Contacts", contentText = new JTextArea(5, 20));
        contentText.setLineWrap(true);
        contentText.setWrapStyleWord(true);
        JButton okBtn;
        JButton annulerBtn;
        JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Envoyer"), annulerBtn = new JButton("Annuler"));
        builder.append(buttonBar, builder.getColumnCount());
        add(BorderLayout.CENTER, builder.getPanel());

        okBtn.addActionListener((ActionEvent ae) -> {
            if (!contentText.getText().isEmpty()) {
                contenu = contentText.getText();
            } else {
                JOptionPane.showMessageDialog(EnvoieSMSDialog.this.getParent(), "Le contenu est obligatoire");
                return;
            }
            if (!nameText.getText().isEmpty()) {
                titre = nameText.getText();
            } else {
                JOptionPane.showMessageDialog(EnvoieSMSDialog.this.getParent(), "Le titre est obligatoire");
                return;
            }
            
            dispose();
            
            try {
                File repertoireCourant;
                repertoireCourant = new File(".").getCanonicalFile();
                System.out.println("Répertoire courant : " + repertoireCourant);
                JFileChooser dialogue = new JFileChooser(repertoireCourant);
                // affichage
                dialogue.showOpenDialog(null);
                // récupération du fichier sélectionné
                if (dialogue.getSelectedFile() != null) {
                    System.out.println("Fichier choisi : " + dialogue.getSelectedFile());
                    final String chemein = dialogue.getSelectedFile().getPath();
                    String extension = chemein.substring(chemein.lastIndexOf("."));
                    System.out.println("L'extension est: " + extension);
                    if (extension.equals(".xls") || extension.equals(".xlsx")) {
                        Thread t = new Thread(() -> {
                            UrlMessage urlMessage = serviceManager.getDefaultUrlMessage();
                            String urlText = urlMessage.getUrlValue();
                            String methode = urlMessage.getMethode();
                            List<String> list = new ArrayList<>();//Utils.getNumFromExcel(chemein);
                            assert list != null;
                            System.out.println("Taille: " + list.size());
                            if (Utils.checkLicence()) {
                                for (String numero : list) {
                                    System.out.println("Numéro: " + numero);
                                    String res = Utils.testConnexionInternet();
                                    BottomPanel.settextLabel("Test connexion ...." + res, java.awt.Color.BLACK);
                                    if (res.equals("OK")) {
                                        BottomPanel.settextLabel("Envoie du Message à.... " + numero, java.awt.Color.BLACK);
                                        if (methode.equals("METHO1")) {
                                            Utils.send(urlText, "" + Long.parseLong(numero), contenu);
                                        } else if (methode.equals("METHO2")) {
                                            Utils.send2(urlText, "" + Long.parseLong(numero), contenu);
                                        }
                                    } else {
                                        BottomPanel.settextLabel("Message non envoyé à.... " + numero + " Problème de connexion internet!!", java.awt.Color.RED);
                                    }
                                    Message message = new Message();
                                    message.setTitle(titre);
                                    message.setContent(contenu);
                                    message.setBkEve(null);
                                    message.setSendDate(new Date());
                                    message.setNumero(numero);
                                    if (res.equals("OK")) {
                                        serviceManager.enregistrer(message);
                                    }
                                }
                                BottomPanel.settextLabel("", java.awt.Color.RED);
                            }
                            JOptionPane.showMessageDialog(EnvoieSMSDialog.this.getParent(), "Envoie PUSH terminé avec success!!");
                        });
                        t.start();
                        
                    } else {
                        JOptionPane.showMessageDialog(EnvoieSMSDialog.this.getParent(), "Fichier non valide");
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(HeaderMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        annulerBtn.addActionListener((ActionEvent ae) -> dispose());
    }
}
