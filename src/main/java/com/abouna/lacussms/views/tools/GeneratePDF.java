package com.abouna.lacussms.views.tools;

import com.abouna.lacussms.service.PdfReportService;
import com.abouna.lacussms.views.utils.DialogUtils;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class GeneratePDF extends JDialog {
    private final JXDatePicker dateDebut;
    private final JXDatePicker dateFin;
    private final JCheckBox regrouperParAgence;
    private final JCheckBox masquerClients;

    public GeneratePDF() {
        setTitle("RAPPORT PDF");
        setModal(true);
        setLayout(new BorderLayout(10, 10));
        JPanel haut = new JPanel();
        JLabel lbl;
        haut.add(lbl = new JLabel("<html><font color = #012345 > GENERER LE RAPPORT </font></html>"));
        lbl.setFont(new Font("Times New Roman", Font.ITALIC, 18));
        add(BorderLayout.BEFORE_FIRST_LINE, haut);
        DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 110dlu:", ""));
        builder.setDefaultDialogBorder();
        builder.append("Date de début", dateDebut = new JXDatePicker());
        builder.append("Date de fin", dateFin = new JXDatePicker());
        builder.append("Regrouper par agence ?", regrouperParAgence = new JCheckBox());
        builder.append("Masquer les informations du clients ?", masquerClients = new JCheckBox());

        JButton annulerBtn;
        JButton okBtn;
        JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Générer PDF"), annulerBtn = new JButton("Fermer"));
        builder.append(buttonBar, builder.getColumnCount());
        add(BorderLayout.CENTER, builder.getPanel());

        okBtn.addActionListener(ae -> generateDPF());

        annulerBtn.addActionListener(ae -> {
            GeneratePDF.this.dispose();
        });
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void generateDPF() {
        if (dateDebut.getDate() == null) {
            JOptionPane.showMessageDialog(this.getParent(), "La date de début est obligatoire");
            return;
        }
        if (dateFin.getDate() == null) {
            JOptionPane.showMessageDialog(this.getParent(), "La date de fin est obligatoire");
            return;
        }
        if (dateDebut.getDate().after(dateFin.getDate())) {
            JOptionPane.showMessageDialog(this.getParent(), "La date de début doit être inférieure à la date de fin");
            return;
        }
        try {
            String path;
            if (regrouperParAgence.isSelected()) {
                path = PdfReportService.getInstance().buildPdfReportByAgency(dateDebut.getDate(), dateFin.getDate(), masquerClients.isSelected());
            } else {
                path = PdfReportService.getInstance().buildPdfReport(dateDebut.getDate(), dateFin.getDate(), masquerClients.isSelected());
            }
            int response = JOptionPane.showConfirmDialog(this, "Rapport généré avec succès cliquez sur ok pour ouvrir", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                Desktop.getDesktop().open(new File(path));
            }
            this.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la génération du rapport : " + e.getMessage());
            this.dispose();
        }
    }

    public static void init() {;
        DialogUtils.initDialog(new GeneratePDF(), null, 470, 240);
    }
}
