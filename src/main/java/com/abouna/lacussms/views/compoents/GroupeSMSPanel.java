package com.abouna.lacussms.views.compoents;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;

public class GroupeSMSPanel extends JPanel {
    private final int  width;
    private final int  height;
    private final int formWidth;

    public GroupeSMSPanel(JDialog parent) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        width = parent.getWidth() - 200;
        height = parent.getHeight() - 200;
        formWidth = width - 500;
        setPreferredSize(new java.awt.Dimension(width, height));
        setBackground(new java.awt.Color(255, 255, 255));
        add(BorderLayout.CENTER, getFrom().getPanel());
    }

    private DefaultFormBuilder getFrom() {
        DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 800dlu:", ""));
        builder.append("Téléphones: ", createTextArea());
        builder.append(getButtonPanel(), builder.getColumnCount());
        return builder;
    }

    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea("689552222, 689552223, 689552224, 689552225, 689552226, 689552227, 689552228, 689552229, 689552220");
        textArea.setEditable(true);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        //textArea.setPreferredSize(new Dimension(width - 50, height - 50));
        textArea.setRows(10);
        textArea.setColumns(100);
        textArea.setBorder(BorderFactory.createEtchedBorder());
        return textArea;
    }

    public static GroupeSMSPanel init(GroupeSMSJDialog groupeSMSJDialog) {
        GroupeSMSPanel groupeSMSPanel = new GroupeSMSPanel(groupeSMSJDialog);
        groupeSMSPanel.setBorder(BorderFactory.createTitledBorder("Groupe SMS"));
        return groupeSMSPanel;
    }

    private JPanel getButtonPanel() {
        /*JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;*/
        JButton annulerBtn;
        JButton okBtn;
        return ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Envoyer"), annulerBtn = new JButton("Annuler"));
    }
}
