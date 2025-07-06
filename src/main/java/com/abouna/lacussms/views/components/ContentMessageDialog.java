package com.abouna.lacussms.views.components;

import com.abouna.lacussms.entities.Message;
import com.abouna.lacussms.main.MainFrame;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;

public class ContentMessageDialog extends JDialog {

    public ContentMessageDialog(JFrame parent, Message message) {
        super(parent, true);
        setTitle("Message");
        setSize(780, 350);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 340dlu:", ""));
        builder.append("Code: ", createTextField(String.valueOf(message.getId())));
        builder.append("Titre:", createTextField(message.getTitle()));
        builder.append("Message:",createTextArea(message.getContent()));
        builder.append("Date envoie:", createTextField(String.valueOf(message.getSendDate())));
        builder.append("Téléphone:", createTextField(message.getNumero()));
        builder.append("Client:", createTextField((message.getBkEve() == null ? "" : message.getBkEve().getCli() == null ? ""
                : message.getBkEve().getCli().getNom() + " " + message.getBkEve().getCli().getPrenom())));
        builder.append("Agence:", createTextField((message.getBkEve() == null ? "" : message.getBkEve().getBkAgence() == null
                ? "" : message.getBkEve().getBkAgence().getNoma())));
        builder.append("Statut:", createCheckBox(message.getSent()));
        add(BorderLayout.CENTER, builder.getPanel());
    }

    public static void initDialog(Message message) {
        MainFrame mainFrame = MainFrame.getInstance();
        ContentMessageDialog dialog = new ContentMessageDialog(mainFrame, message);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    private JTextField createTextField(String text) {
        JTextField textField = new JTextField(text);
        textField.setEditable(false);
        return textField;
    }

    private JTextArea createTextArea(String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setPreferredSize(new Dimension(400, 100));
        textArea.setBorder(BorderFactory.createEtchedBorder());
        return textArea;
    }

    private JCheckBox createCheckBox(Boolean sent) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(sent);
        checkBox.setEnabled(false);
        return checkBox;
    }
}
