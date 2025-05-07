package com.abouna.lacussms.views.components;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.undo.CannotUndoException;
import java.awt.*;

public class GroupeSMSPanel extends JPanel {
    private JTextArea phoneTextArea;
    private JTextArea groupeTextArea;
    private JTextArea clientTextArea;
    private JTextArea messageTextArea;
    private final int width;
    private final int height;
    private int formWidth;
    private final JDialog parent;

    public GroupeSMSPanel(JDialog parent) {
        this.parent = parent;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        width = parent.getWidth();
        height = parent.getHeight();
        setPreferredSize(new java.awt.Dimension(width, height));
        setBackground(new java.awt.Color(255, 255, 255));
        add(BorderLayout.CENTER, getMainPanel());
    }

    private JPanel getMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setSize(width, height);
        JPanel formPanel = getFrom().getPanel();
        formWidth = width - 150;
        formPanel.setBounds(0, 0, formWidth, height - 80);
        mainPanel.add(formPanel);
        mainPanel.add(importButtonPanel());
        mainPanel.add(addGroupeButtonPanel());
        mainPanel.add(addClientButtonPanel());
        return mainPanel;
    }

    private JButton importButtonPanel() {
        JButton importButton = new JButton("Importer");
        importButton.setToolTipText("Importer les numéros de téléphone");
        importButton.setBounds(formWidth , 70, 100, 30);
        return importButton;
    }

    private JButton addGroupeButtonPanel() {
        JButton importButton = new JButton("Ajouter groupes");
        importButton.setToolTipText("Ajouter des groupes");
        importButton.setBounds(formWidth, 200, 100, 30);
        return importButton;
    }

    private JButton addClientButtonPanel() {
        JButton importButton = new JButton("Ajouter clients");
        importButton.setToolTipText("Ajouter des clients");
        importButton.setBounds(formWidth, 330, 100, 30);
        return importButton;
    }

    private DefaultFormBuilder getFrom() {
        DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 700dlu:", ""));
        builder.append("Téléphones: ", new JScrollPane(createPhoneTextArea()));
        builder.append("Groupes: ", new JScrollPane(createGroupeTextArea()));
        builder.append("Clients: ", new JScrollPane(createClientTextArea()));
        builder.append("Message: ", new JScrollPane(createMessageTextArea()));
        builder.append(getButtonPanel(), builder.getColumnCount());
        return builder;
    }

    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea(/*"689552222, 689552223, 689552224, 689552225, 689552226, 689552227, 689552228, 689552229, 689552220"*/);
        textArea.setEditable(true);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setRows(8);
        textArea.setColumns(50);
        textArea.setBorder(BorderFactory.createEtchedBorder());
        return textArea;
    }

    public static GroupeSMSPanel init(GroupeSMSJDialog groupeSMSJDialog) {
        GroupeSMSPanel groupeSMSPanel = new GroupeSMSPanel(groupeSMSJDialog);
        groupeSMSPanel.setBorder(BorderFactory.createTitledBorder("Groupe SMS"));
        return groupeSMSPanel;
    }

    private JPanel getButtonPanel() {
        JButton okButton = new JButton("Envoyer");
        okButton.addActionListener(e -> envoyerSMS());
        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> annuler());
        return ButtonBarFactory.buildOKCancelBar(okButton, cancelButton);
    }

    private void annuler() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }

    private void envoyerSMS() {
    }

    private JTextArea createPhoneTextArea() {
        phoneTextArea = createTextArea();
        phoneTextArea.setToolTipText("Entrez les numéros de téléphone séparés par des virgules");
        return phoneTextArea;
    }

    private JTextArea createGroupeTextArea() {
        groupeTextArea = createTextArea();
        groupeTextArea.setToolTipText("Entrez les identifiants des groupes séparés par des virgules");
        return groupeTextArea;
    }

    private JTextArea createClientTextArea() {
        clientTextArea = createTextArea();
        clientTextArea.setToolTipText("Entrez les codes clients séparés par des virgules");
        return clientTextArea;
    }

    private JTextArea createMessageTextArea() {
        messageTextArea = createTextArea();
        return messageTextArea;
    }
}
