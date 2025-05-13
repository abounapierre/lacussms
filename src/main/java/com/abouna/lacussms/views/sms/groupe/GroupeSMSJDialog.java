package com.abouna.lacussms.views.sms.groupe;

import com.abouna.lacussms.main.MainFrame;

import javax.swing.*;
import java.awt.*;

public class GroupeSMSJDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    public GroupeSMSJDialog(JFrame parent) {
        super(parent, true);
        setLayout(new BorderLayout(10, 10));
        setTitle("Envoyer des SMS groupés");
        setSize(new Dimension(parent.getWidth() - 450, parent.getHeight() - 350));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(GroupeSMSPanel.init(this), BorderLayout.CENTER);
    }

    public static void initDialog() {
        JFrame parent = MainFrame.getInstance();
        GroupeSMSJDialog dialog = new GroupeSMSJDialog(parent);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
