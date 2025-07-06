package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.Parametre;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.tools.ConstantUtils;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;

public class DateSoldeDialog extends JDialog {

    public DateSoldeDialog() {
        LacusSmsService serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        JButton okBtn = new JButton("Enregistrer");
        JButton annulerBtn = new JButton("Annuler");
        JXDatePicker dateTxt;
        DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 180dlu:", ""));
        builder.setDefaultDialogBorder();
        builder.append("Date", dateTxt = new JXDatePicker());
        JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn , annulerBtn);
        builder.append(buttonBar, builder.getColumnCount());
        add(BorderLayout.CENTER, builder.getPanel());
        okBtn.addActionListener((ActionEvent e1) ->{
            Parametre parametre = new Parametre();
            parametre.setNom(ConstantUtils.DATE_SOLDE);
            String valeur = "";
            parametre.setValeur(valeur);
            serviceManager.enregistrerParametre(parametre);
        });
        annulerBtn.addActionListener((ActionEvent e1) -> {
            //System.out.println("#####Annluer#####");
            dispose();
        });
        dateTxt.setDate(new Date());
    }
}
