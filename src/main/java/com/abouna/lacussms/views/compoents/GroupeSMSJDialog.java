package com.abouna.lacussms.views.compoents;

import com.abouna.lacussms.main.MainFrame;

import javax.swing.*;
import java.awt.*;

public class GroupeSMSJDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    public GroupeSMSJDialog(JFrame parent) {
        super(parent, true);
        setTitle("Groupe SMS");
        setSize(700, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    public static void initDialog() {
        JFrame parent = MainFrame.getInstance();
        GroupeSMSJDialog dialog = new GroupeSMSJDialog(parent);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
