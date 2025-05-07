package com.abouna.lacussms.views.components;

import com.abouna.lacussms.entities.BkEve;
import com.abouna.lacussms.main.MainFrame;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;

public class ContentEveDialog extends JDialog {

    public ContentEveDialog(JFrame parent, BkEve bkEve) {
        super(parent, true);
        setTitle("Content Eve");
        setSize(700, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 300dlu:", ""));
        builder.append("Code:", createTextField(String.valueOf(bkEve.getId())));
        builder.append("NumEve:", createTextField(bkEve.getNumEve()));
        builder.append("Client:", createTextField((bkEve.getCli() == null ? "" : bkEve.getCli().getNom() + " " + bkEve.getCli().getPrenom())));
        builder.append("Operation:", createTextField((bkEve.getOpe() == null ? "" : bkEve.getOpe().getLib())));
        builder.append("Compte:", createTextField(bkEve.getCompte()));
        builder.append("Montant:", createTextField(bkEve.getMontant()));
        builder.append("Etat:", createTextField(bkEve.getEtat()));
        builder.append("Agence:", createTextField((bkEve.getBkAgence() == null ? "" : bkEve.getBkAgence().getNoma())));
        builder.append("Date:", createTextField((bkEve.getDVAB() == null ? "" : bkEve.getDVAB())));
        builder.append("Heure:", createTextField(bkEve.getHsai()));
        builder.append("Traité?:", createTextField(bkEve.isSent() ? "Oui" : "Non"));
        add(BorderLayout.CENTER, builder.getPanel());
    }

    public static void initDialog(BkEve bkEveById) {
        MainFrame mainFrame = MainFrame.getInstance();
        ContentEveDialog dialog = new ContentEveDialog(mainFrame, bkEveById);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    private JTextField createTextField(String text) {
        JTextField textField = new JTextField(text);
        textField.setEditable(false);
        return textField;
    }
}
