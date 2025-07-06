package com.abouna.lacussms.views.sms.groupe;

import com.abouna.lacussms.dto.GroupeSmsRequestDTO;
import com.abouna.lacussms.dto.GroupeSmsResponseDTO;
import com.abouna.lacussms.service.GroupeSmsService;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class GroupeSMSPanel extends JPanel {
    private static final Logger log = LoggerFactory.getLogger(GroupeSMSPanel.class);
    private JTextArea phoneTextArea;
    private JTextArea groupeTextArea;
    private JTextArea clientTextArea;
    private JTextArea messageTextArea;
    private JCheckBox personalizeCheckBox;
    private final int width;
    private final int height;
    private int formWidth;
    private final GroupeSMSJDialog parent;

    public GroupeSMSPanel(GroupeSMSJDialog parent) {
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
        mainPanel.add(importButton());
        mainPanel.add(addGroupeButton());
        mainPanel.add(addClientButton());
        mainPanel.add(addPersonalizeCheckBox());
        return mainPanel;
    }

    private JButton importButton() {
        JButton importButton = new JButton("Importer");
        importButton.setToolTipText("Importer les numéros de téléphone");
        importButton.setBounds(formWidth , 70, 100, 30);
        return importButton;
    }

    private JButton addGroupeButton() {
        JButton importButton = new JButton("Ajouter groupes");
        importButton.setToolTipText("Ajouter des groupes");
        importButton.setBounds(formWidth, 200, 100, 30);
        importButton.addActionListener(e -> AddGroupeDialog.initDialog(parent, this));
        return importButton;
    }

    private JButton addClientButton() {
        JButton importButton = new JButton("Ajouter clients");
        importButton.setToolTipText("Ajouter des clients");
        importButton.setBounds(formWidth, 330, 100, 30);
        importButton.addActionListener(e -> AddClientDialog.initDialog(parent, this));
        return importButton;
    }

    private JCheckBox addPersonalizeCheckBox() {
        personalizeCheckBox = new JCheckBox("Personnalisable");
        personalizeCheckBox.setToolTipText("Envoyer un message personnalisé");
        personalizeCheckBox.setBounds(formWidth, 480, 150, 30);
        return personalizeCheckBox;
    }

    private DefaultFormBuilder getFrom() {
        DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 400dlu:", ""));
        builder.append("Téléphones: ", new JScrollPane(createPhoneTextArea()));
        builder.append("Groupes: ", new JScrollPane(createGroupeTextArea()));
        builder.append("Clients: ", new JScrollPane(createClientTextArea()));
        builder.append("Message: ", new JScrollPane(createMessageTextArea()));
        builder.append(getButtonPanel(), builder.getColumnCount());
        return builder;
    }

    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea();
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
        try {
            GroupeSmsRequestDTO requestDTO = new GroupeSmsRequestDTO();
            String phoneNumbers = phoneTextArea.getText();
            String groupes = groupeTextArea.getText();
            String clients = clientTextArea.getText();
            String message = messageTextArea.getText();
            boolean empty = true;
            if(!phoneNumbers.isEmpty()) {
                requestDTO.setPhoneNumbers(phoneNumbers);
                empty = false;
            }
            if(!groupes.isEmpty()) {
                requestDTO.setGroupes(groupes);
                empty = false;
            }
            if(!clients.isEmpty()) {
                requestDTO.setClients(clients);
                empty = false;
            }
            requestDTO.setPersonalized(personalizeCheckBox.isSelected());
            if (empty) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer au moins un numéro de téléphone, un groupe ou un client", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(message.isEmpty()) {
                JOptionPane.showMessageDialog(this, "le message ne doit pas être vide", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            requestDTO.setMessage(message);
            GroupeSmsResponseDTO smsResponseDTO = GroupeSmsService.getInstance().sendSmsGroupe(requestDTO);
            log.info("SMS envoyés avec succès: {}", smsResponseDTO);
            JOptionPane.showMessageDialog(this, "SMS envoyé avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'envoi du SMS: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } finally {
            parent.dispose();
        }
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

    public JTextArea getPhoneTextArea() {
        return phoneTextArea;
    }

    public void setPhoneTextArea(JTextArea phoneTextArea) {
        this.phoneTextArea = phoneTextArea;
    }

    public JTextArea getGroupeTextArea() {
        return groupeTextArea;
    }

    public JTextArea getClientTextArea() {
        return clientTextArea;
    }

}
