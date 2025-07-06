/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.main.MainMenuPanel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SATELLITE
 */
public class DeleteBkEveDialog extends JDialog{
    private final JXDatePicker date1;
    private final JXDatePicker date2;
    private final LacusSmsService serviceManager;
    private final MainMenuPanel parentPanel;
    
    public DeleteBkEveDialog(){
        serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        parentPanel = ApplicationConfig.getApplicationContext().getBean(MainMenuPanel.class);
        setTitle("SUPPRESSION EVENEMENT");
            setModal(true);
            setLayout(new BorderLayout(10, 10));
            JPanel haut = new JPanel();
            JLabel lbl;
            haut.add(lbl = new JLabel("<html><font color = #012345 > SUPPRESSION MULTIPLE DES EVENEMENTS </font></html>"));
            lbl.setFont(new Font("Times New Roman", Font.ITALIC, 18));
            add(BorderLayout.BEFORE_FIRST_LINE, haut);
            DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 180dlu:", ""));
            builder.setDefaultDialogBorder();
            builder.append("Date début", date1 = new JXDatePicker());
            builder.append("Date début", date2 = new JXDatePicker());
        JButton okBtn;
        JButton annulerBtn;
        JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Valider"), annulerBtn = new JButton("Annuler"));
            builder.append(buttonBar, builder.getColumnCount());
            add(BorderLayout.CENTER, builder.getPanel());
            
            okBtn.addActionListener((ActionEvent ae) -> {
                try {
                    Date d1 = null, d2 = null;
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    if(date1.getDate()!=null && date2.getDate()!=null){
                        d1 = format.parse(format.format(date1.getDate()));
                        d2 = format.parse(format.format(date2.getDate()));
                    }
                    int res = JOptionPane.showConfirmDialog(null, "Etes vous sûr de vouloir supprimer ces évènements?", "Confirmation",
                            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    if (res == JOptionPane.YES_OPTION) {
                        try {
                            int nbre;
                            if (d1 != null && d2 != null) {
                                nbre = serviceManager.supprimerParPeriode(d1, d2);
                            }else{
                                nbre = serviceManager.supprimerToutBkEve();
                            }
                            if (nbre > 0) {
                                JOptionPane.showMessageDialog(null, "Suppression de " + nbre + " évènement(s) effectuée avec succès.", "Information", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "Aucun évènement à supprimer.", "Information", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    dispose();
                    parentPanel.setContent(new BkEvePanel());
                } catch (IOException | ParseException ex) {
                    Logger.getLogger(DeleteBkEveDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            annulerBtn.addActionListener((ActionEvent ae) -> {
                dispose();
            });
    }
}
