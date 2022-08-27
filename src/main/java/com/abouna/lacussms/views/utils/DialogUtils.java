package com.abouna.lacussms.views.utils;

import javax.swing.*;
import java.awt.*;

public class DialogUtils {

    public static void initDialog(JDialog dialog, Component parent, int width, int height) {
        dialog.setSize(width, height);
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - dialog.getWidth()) / 2;
        final int y = (screenSize.height - dialog.getHeight()) / 2;
        //dialog.setLocation(x, y);
        dialog.setLocationRelativeTo(parent);
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
}
