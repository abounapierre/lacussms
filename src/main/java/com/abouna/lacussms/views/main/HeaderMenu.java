/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.views.main;


import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.BkCompCli;
import com.abouna.lacussms.entities.Config;
import com.abouna.lacussms.main.App;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.tools.Utils;
import com.abouna.lacussms.views.tools.XlsGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrateur
 */
public class HeaderMenu extends JMenuBar {

    private JMenu employe;
    private JMenuItem langue;
    private final LacusSmsService serviceManager;
    private JCheckBoxMenuItem bkmac,bkmad,bkmpai,event;
    private final JFileChooser fc = new JFileChooser();
    private Config config ;
             
    public HeaderMenu(LacusSmsService service) {
       this.serviceManager = service;//ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        initComponent();
    }

    protected final void initComponent() {
        JMenu fichier1 = new JMenu("Fichier");
        JMenu configuration = new JMenu("Configuration");
        JMenu profil = new JMenu("Profil");
        JMenu service = new JMenu("Services");
        JMenu rapports = new JMenu("Rapports");
        JMenu contact = new JMenu("Contacts");
        JMenuItem copier = new JMenuItem("Copier");
        JMenuItem couper = new JMenuItem("Couper");
        JMenuItem coller = new JMenuItem("Coller");
        JMenuItem quitter = new JMenuItem("Quitter");
        JMenuItem restart = new JMenuItem("Redémarrer");
        JMenuItem typeSMS = new JMenuItem("Type SMS");
        JMenuItem formatSMS = new JMenuItem("Format SMS");
        JMenuItem paramBD = new JMenuItem("BD Distante");
        JMenuItem paramTbl = new JMenuItem("Tables Distance");
        employe = new JMenu("Clients");
        JMenuItem licence = new JMenuItem("Licence");
        JMenuItem user = new JMenuItem("utilisateur");
        JMenuItem typeRapport = new JMenuItem("Type de Rapports");
        JMenuItem rapport = new JMenuItem("Fichier de Banque");
        JMenuItem voirContact = new JMenuItem("Afficher");
        JMenuItem importFichierClient = new JMenuItem("Import excel");
        JMenuItem exportFichierClient = new JMenuItem("Export excel");
        event = new JCheckBoxMenuItem("Evenement");
        bkmac = new JCheckBoxMenuItem("Crédit");
        bkmad = new JCheckBoxMenuItem("Mandat");
        bkmpai = new JCheckBoxMenuItem("Salaire");

        JMenuItem initialiser = new JMenuItem("Initialiser les données");
        fichier1.add(copier);
        fichier1.add(couper);
        fichier1.add(coller);
        fichier1.add(quitter);
        fichier1.add(restart);
        configuration.add(initialiser);
        configuration.add(typeSMS);
        configuration.add(formatSMS);
        configuration.add(paramBD);
        configuration.add(paramTbl);
        configuration.add(service);
        config = !serviceManager.getAllConfig().isEmpty() ? serviceManager.getAllConfig().get(0) : null;
        event.setSelected(config != null && config.isEvent());
        bkmac.setSelected(config != null && config.isBkmac());
        bkmad.setSelected(config != null && config.isMandat());
        bkmpai.setSelected(config != null && config.isBkmpai());
        if(config != null) {
            System.out.println(config.toString());
        }
        event.addActionListener((ActionEvent e) -> {
            if(event.isSelected()){
                config.setEvent(true);
                serviceManager.modifierConfig(config);
            }else{
                config.setEvent(false);
                serviceManager.modifierConfig(config);
            }
        });
        bkmac.addActionListener((ActionEvent e) -> {
            if(bkmac.isSelected()){
                config.setBkmac(true);
                serviceManager.modifierConfig(config);
            }else{
                config.setBkmac(false);
                serviceManager.modifierConfig(config);
            }
        });
        bkmad.addActionListener((ActionEvent e) -> {
            if(bkmad.isSelected()){
                config.setMandat(true);
                serviceManager.modifierConfig(config);
            }else{
                config.setMandat(false);
                serviceManager.modifierConfig(config);
            }
        });
        bkmpai.addActionListener((ActionEvent e) -> {
            if(bkmpai.isSelected()){
                config.setBkmpai(true);
                serviceManager.modifierConfig(config);
            }else{
                config.setBkmpai(false);
                serviceManager.modifierConfig(config);
            }
        });
        service.add(event);
        service.add(bkmac);
        service.add(bkmad);
        service.add(bkmpai);
        employe.add(importFichierClient);
        employe.add(exportFichierClient);        
        configuration.add(employe);
        profil.add(licence);
        profil.add(user);
        //rapports.add(typeRapport);
        rapports.add(rapport);
        contact.add(voirContact);
        
        
        
        initialiser.addActionListener((ActionEvent e) -> {
        });
        
        quitter.addActionListener((ActionEvent e) -> {
            int response = JOptionPane.showConfirmDialog(HeaderMenu.this.getParent(), "Voulez-vous vraiment quitter cette application?", "Confirmation",
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                Window window = SwingUtilities.windowForComponent(HeaderMenu.this);
                if (window instanceof JFrame) {
                    JFrame frame = (JFrame) window;
                    frame.setVisible(false);
                    frame.dispose();
                    System.exit(0);
                }
            }
        });
        
         restart.addActionListener((ActionEvent e) -> {
             int response = JOptionPane.showConfirmDialog(HeaderMenu.this.getParent(), "Voulez-vous vraiment redémarrer cette application?", "Confirmation",
                     JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
             if (response == JOptionPane.YES_OPTION) {
                 Window window = SwingUtilities.windowForComponent(HeaderMenu.this);
                 if (window instanceof JFrame) {
                     JFrame frame = (JFrame) window;
                     frame.setVisible(false);
                     frame.dispose();
                     App.appliRun = false;
                 }
                 MainFrame.main(new String[]{});
                 /*try {
                     MainFrame.main(null);
                 } catch (IOException | ClassNotFoundException | SQLException ex) {
                     Logger.getLogger(HeaderMenu.class.getName()).log(Level.SEVERE, null, ex);
                 }*/
             }
        });
        
        voirContact.addActionListener((ActionEvent e) -> {
            String msg = "";/*"ABOUNA PIERRE EMMANUEL Ingénieur de conception en Iformatique\n"
            + " Option Génie Logiciel Tel: 698984176\n" +
            "Email: abouna.emmanuel@yahoo.fr";*/
            JOptionPane.showMessageDialog(HeaderMenu.this.getParent(),msg);
        });
        
        importFichierClient.addActionListener((ActionEvent e) -> {
            try {
                File repertoireCourant = null;
                repertoireCourant = new File(".").getCanonicalFile();
                System.out.println("Répertoire courant : " + repertoireCourant);
                JFileChooser dialogue = new JFileChooser(repertoireCourant);
                // affichage
                dialogue.showOpenDialog(null);
                // récupération du fichier sélectionné
                if (dialogue.getSelectedFile() != null) {
                    System.out.println("Fichier choisi : " + dialogue.getSelectedFile());
                    final String chemein = dialogue.getSelectedFile().getPath();
                    final String extension = chemein.substring(chemein.lastIndexOf("."));
                    System.out.println("L'extension est: " + extension);
                    if (extension.equals(".xls") || extension.equals(".xlsx")) {
                        Thread t = new Thread(() -> {
                            String result = Utils.importExcel(chemein, extension, serviceManager);
                            if (result.contentEquals("OK")) {
                                JOptionPane.showMessageDialog(HeaderMenu.this.getParent(), "Importation réussie avec success!!", "OK", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(HeaderMenu.this.getParent(), "Importation du fichier terminée avec les erreurs!!", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                        t.start();
                        
                    } else {
                        JOptionPane.showMessageDialog(HeaderMenu.this.getParent(), "Fichier non valide");
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(HeaderMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        
        exportFichierClient.addActionListener((ActionEvent e) -> {
            int val_retour = fc.showSaveDialog(employe);
            if (val_retour == JFileChooser.APPROVE_OPTION) {
                File fichier = fc.getSelectedFile();
                final String path = fichier.getAbsolutePath() + ".xls";
                Thread t = new Thread(() -> {
                    List<BkCompCli> bkCompClis = serviceManager.getAllBkCompClis();
                    XlsGenerator xlsGenerator = new XlsGenerator(bkCompClis, path);
                });
                t.start();
                int response = JOptionPane.showConfirmDialog(null, "<html>Rapport généré avec success!!<br>Voulez vous l'ouvrir?", "Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    try {
                        File pdfFile = new File(path);
                        if (pdfFile.exists()) {
                            if (Desktop.isDesktopSupported()) {
                                Desktop.getDesktop().open(pdfFile);
                            } else {
                                JOptionPane.showMessageDialog(null,"Ce type de fichier n'est pas pris en charge");
                                System.out.println("Awt Desktop is not supported!");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Ce fichier n'existe pas");
                            System.out.println("File is not exists!");
                        }
                    } catch (IOException | HeadlessException ex) {
                        JOptionPane.showMessageDialog(HeaderMenu.this.getParent(), "error " + ex.getMessage());
                    }
                }
            }
        });
        
        rapport.addActionListener((ActionEvent e) -> {
            CbsFileDialog nouveau1 = null;
            try {
                nouveau1 = new CbsFileDialog();
                nouveau1.setSize(375, 200);
                nouveau1.setLocationRelativeTo(null);
                nouveau1.setModal(true);
                nouveau1.setResizable(false);
                nouveau1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                nouveau1.setVisible(true);
            } catch (ParseException ex) {
                Logger.getLogger(HeaderMenu.class.getName()).log(Level.SEVERE, null, ex);
            }    
        });
        
        add(fichier1);
        add(configuration);
        add(profil);
        add(rapports);
        add(contact);
    }

}
