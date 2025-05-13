package com.abouna.lacussms.views.groupe;

import com.abouna.lacussms.entities.Groupe;
import com.abouna.lacussms.main.MainFrame;
import com.abouna.lacussms.service.GroupeSmsService;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class AddGroupeDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(AddGroupeDialog.class);
    private final ContentPanel contentPanel;

    public AddGroupeDialog(JFrame parent, ContentPanel content, Groupe groupe) {
        super(parent, true);
        this.contentPanel = content;
        setTitle("Ajouter un groupe");
        setSize(410, 150);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(AddGroupePanel.init(this, groupe), BorderLayout.CENTER);
    }

    public static void initDialog(ContentPanel content, Groupe groupe) {
        JFrame parent = MainFrame.getInstance();
        AddGroupeDialog dialog = new AddGroupeDialog(parent, content, groupe);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private void updateComboBox() {
        log.info("Updating combo box with new group data {}", contentPanel==null ? "null" : "not null");
        if (contentPanel != null) {
            contentPanel.getComboBox().removeAllItems();
            contentPanel.addGroupeToComboBox();
        }
    }

    private static class AddGroupePanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private JTextField codeText;
        private final AddGroupeDialog dialog;
        private final Groupe groupe;

        public static JPanel init(AddGroupeDialog dialog, Groupe groupe) {
            return new AddGroupePanel(dialog, groupe);
        }

        public AddGroupePanel(AddGroupeDialog parent, Groupe groupe) {
            this.dialog = parent;
            this.groupe = groupe;
            setLayout(new BorderLayout());
            add(BorderLayout.CENTER, getFormPanel());
            setFormData();
        }

        private void setFormData() {
            if(groupe != null && groupe.getLibelle() != null) {
                codeText.setText(groupe.getLibelle());
            }
        }

        private Component getFormPanel() {
            DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 140dlu:", ""));
            builder.setDefaultDialogBorder();
            builder.append("Code ", codeText = new JTextField(50));
            JButton okBtn;
            JButton annulerBtn;
            JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Enrégistrer"), annulerBtn = new JButton("Annuler"));
            builder.append(buttonBar, builder.getColumnCount());
            okBtn.addActionListener(e -> saveGroupe());
            annulerBtn.addActionListener(e -> dialog.dispose());
            return builder.getPanel();
        }

        private void saveGroupe() {
            try {
                groupe.setLibelle(codeText.getText());
                GroupeSmsService service = GroupeSmsService.getInstance();
                if(groupe.getId() != null) {
                    service.updateGroupe(groupe);
                } else {
                    service.saveGroupe(groupe);
                }
                JOptionPane.showMessageDialog(this, "Groupe enregistré avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                dialog.updateComboBox();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            } finally {
                dialog.dispose();
            }
        }
    }
}
