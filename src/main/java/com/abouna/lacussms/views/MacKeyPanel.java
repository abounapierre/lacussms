package com.abouna.lacussms.views;

import com.abouna.lacussms.views.utils.DialogUtils;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;

public class MacKeyPanel extends JDialog {
    private final JTextField keyText;
    public MacKeyPanel(String mac) {
        setTitle("Clé d'installation");
        setModal(true);
        setLayout(new BorderLayout(10, 10));
        JPanel haut = new JPanel();
        JLabel lbl;
        haut.add(lbl = new JLabel("<html><font color = #012345 > Veuillez enregistrer la clé d'installation et l'envoyer à votre support technique. </font></html>"));
        lbl.setFont(new Font("Times New Roman", Font.ITALIC, 18));
        add(BorderLayout.BEFORE_FIRST_LINE, haut);
        DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 300dlu:", ""));
        builder.setDefaultDialogBorder();
        builder.append("Clé:", keyText = new JTextField(50));
         keyText.setText(mac);
         keyText.setEnabled(false);
        JButton annulerBtn;
        JButton okBtn;
        JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Enregistrer"), annulerBtn = new JButton("Fermer"));
        builder.append(buttonBar, builder.getColumnCount());
        add(BorderLayout.CENTER, builder.getPanel());

        okBtn.addActionListener(ae -> saveKey());
        annulerBtn.addActionListener(e -> {
            setVisible(false);
            dispose();
        });
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static void init(String mac) {;
        DialogUtils.initDialog(new MacKeyPanel(mac), null, 700, 200);
    }

    private void saveKey() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setDialogTitle("Sélectionner le dossier");
        fileChooser.setApproveButtonText("Choisir");
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                String filename = fileChooser.getSelectedFile() + File.separator + "key.txt";
                FileWriter myWriter = new FileWriter(filename);
                myWriter.write(keyText.getText());
                myWriter.close();
                String msg = "La clé d'installation a été enregistrée avec succès dans le dossier : " + filename;
                JOptionPane.showMessageDialog(this, msg, "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'importation : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
