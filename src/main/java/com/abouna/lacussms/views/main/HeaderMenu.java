/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.views.main;


import com.abouna.lacussms.entities.Config;
import com.abouna.lacussms.main.MainFrame;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.DateSoldeDialog;
import com.abouna.lacussms.views.tools.CSVFileImport;
import com.abouna.lacussms.views.utils.DialogUtils;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 *
 * @author Administrateur
 */
public class HeaderMenu extends JMenuBar {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HeaderMenu.class);
    private JMenu employee;
    private JMenuItem langue;
    private final LacusSmsService serviceManager;
    private JCheckBoxMenuItem bkmac;
    private JCheckBoxMenuItem bkmad;
    private JCheckBoxMenuItem bkmpai;
    private JCheckBoxMenuItem event;
    private JCheckBoxMenuItem hbd;
    private JCheckBoxMenuItem solde;
    private final JFileChooser fileChooser = new JFileChooser();
    private Config config ;
    private JMenu smsProviderMenu;
             
    public HeaderMenu(LacusSmsService service) {
       this.serviceManager = service;
        initComponent();
    }

    protected final void initComponent() {
        JMenu fichier1 = new JMenu("Fichier");
        JMenu configuration = new JMenu("Configuration");
        JMenu profil = new JMenu("Profil");
        JMenu service = new JMenu("Services");
        JMenu rapports = new JMenu("Rapports");
        JMenu contact = new JMenu("Contacts");
        smsProviderMenu = new JMenu("Fournisseur SMS");
        JMenuItem copier = new JMenuItem("Copier");
        JMenuItem couper = new JMenuItem("Couper");
        JMenuItem coller = new JMenuItem("Coller");
        JMenuItem quitter = new JMenuItem("Quitter");
        JMenuItem restart = new JMenuItem("Redémarrer");
        JMenuItem typeSMS = new JMenuItem("Type SMS");
        JMenuItem formatSMS = new JMenuItem("Format SMS");
        JMenuItem paramBD = new JMenuItem("BD Distante");
        JMenuItem paramTbl = new JMenuItem("Tables Distance");
        JMenuItem dateSolde = new JMenuItem("Date d'envoie de solde");
        employee = new JMenu("Clients");
        JMenuItem licence = new JMenuItem("Licence");
        JMenuItem user = new JMenuItem("utilisateur");
        JMenuItem typeRapport = new JMenuItem("Type de Rapports");
        JMenuItem rapport = new JMenuItem("Fichier de Banque");
        JMenuItem voirContact = new JMenuItem("Afficher");
        JMenuItem importFichierClient = new JMenuItem("Import csv");
        JMenuItem exportFichierClient = new JMenuItem("Export csv");
        event = new JCheckBoxMenuItem("Evenement");
        bkmac = new JCheckBoxMenuItem("Crédit");
        bkmad = new JCheckBoxMenuItem("Mandat");
        bkmpai = new JCheckBoxMenuItem("Salaire");
        hbd = new JCheckBoxMenuItem("Anniversaire");
        solde = new JCheckBoxMenuItem("Solde");

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
        hbd.setSelected(config!=null && config.getHbd().equals("true"));
        solde.setSelected(config != null && config.getSolde().equals("true"));

        if(config != null) {
            logger.info("{}" , config);
        }

        dateSolde.addActionListener((ActionEvent e) ->{
            DialogUtils.initDialog(new DateSoldeDialog(), HeaderMenu.this.getParent(), 500, 150);
        });

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
        hbd.addActionListener((ActionEvent e) -> {
            if(hbd.isSelected()){
                config.setHbd("true");
                serviceManager.modifierConfig(config);
            }else{
                config.setHbd("false");
                serviceManager.modifierConfig(config);
            }
        });
        solde.addActionListener((ActionEvent e) -> {
            if(solde.isSelected()){
                config.setSolde("true");
                serviceManager.modifierConfig(config);
            }else{
                config.setSolde("false");
                serviceManager.modifierConfig(config);
            }
        });
        service.add(event);
        service.add(bkmac);
        service.add(bkmad);
        service.add(bkmpai);
        service.add(hbd);
        service.add(solde);

        employee.add(importFichierClient);
        employee.add(exportFichierClient);
        configuration.add(employee);
        configuration.add(dateSolde);
        profil.add(licence);
        profil.add(user);
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
                     MainFrame.appliRun = false;
                 }
                 MainFrame.main(new String[]{});
             }
        });
        
        voirContact.addActionListener((ActionEvent e) -> {
            String msg = "";
            JOptionPane.showMessageDialog(HeaderMenu.this.getParent(),msg);
        });
        
        importFichierClient.addActionListener((ActionEvent e) -> {
            try {
                File repertoireCourant;
                repertoireCourant = new File(".").getCanonicalFile();
                logger.info("Répertoire courant : {}",repertoireCourant);
                JFileChooser dialogue = new JFileChooser(repertoireCourant);
                // affichage
                dialogue.showOpenDialog(null);
                // récupération du fichier sélectionné
                if (dialogue.getSelectedFile() != null) {
                    logger.info("Fichier choisi : {}", dialogue.getSelectedFile());
                    final String chemein = dialogue.getSelectedFile().getPath();
                    final String extension = chemein.substring(chemein.lastIndexOf("."));
                    logger.info("L'extension est: {}", extension);
                    if (extension.equals(".csv")) {
                        Thread t = new Thread(() -> {
                            CSVFileImport.parseFile(chemein);//Utils.importExcel(chemein, extension, serviceManager);
                            JOptionPane.showMessageDialog(HeaderMenu.this.getParent(), "Importation réussie avec success!!", "OK", JOptionPane.INFORMATION_MESSAGE);
                        });
                        t.start();
                    } else {
                        JOptionPane.showMessageDialog(HeaderMenu.this.getParent(), "Fichier non valide");
                    }
                }
            } catch (IOException ex) {
                logger.error("Erreur de chargement du fichier", ex);
                JOptionPane.showMessageDialog(HeaderMenu.this.getParent(), "Importation du fichier terminée avec les erreurs!!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        
        exportFichierClient.addActionListener((ActionEvent e) -> {
            int val_retour = fileChooser.showSaveDialog(employee);
            if (val_retour == JFileChooser.APPROVE_OPTION) {
                File fichier = fileChooser.getSelectedFile();
                final String path = fichier.getAbsolutePath() + ".csv";
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
                                logger.info("Awt Desktop is not supported!");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Ce fichier n'existe pas");
                            logger.info("File is not exists!");
                        }
                    } catch (IOException | HeadlessException ex) {
                        JOptionPane.showMessageDialog(HeaderMenu.this.getParent(), "error " + ex.getMessage());
                    }
                }
            }
        });
        
        rapport.addActionListener((ActionEvent e) -> {
            CbsFileDialog nouveau1;
            try {
                nouveau1 = new CbsFileDialog();
                nouveau1.setSize(375, 200);
                nouveau1.setLocationRelativeTo(null);
                nouveau1.setModal(true);
                nouveau1.setResizable(false);
                nouveau1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                nouveau1.setVisible(true);
            } catch (ParseException ex) {
                logger.error("Erreur de parsing", ex);
            }    
        });
        
        add(fichier1);
        add(configuration);
        add(profil);
        add(rapports);
        add(contact);
        add(smsProviderMenu);
    }

    public JMenu getSmsProviderMenu() {
        return smsProviderMenu;
    }
}
