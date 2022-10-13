package com.abouna.lacussms.views.utils;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.views.main.MainFrame;
import org.jdesktop.swingx.util.WindowUtils;

import javax.swing.*;
import java.awt.*;

public class DialogUtils {

    public static void initDialog(JDialog dialog, Component parent, int width, int height) {
        MainFrame frame = ApplicationConfig.getApplicationContext().getBean(MainFrame.class);
        dialog.setSize(width, height);
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = frame.getLocationOnScreen().x  + (frame.getWidth() - dialog.getWidth()) / 2;
        final int y = frame.getLocationOnScreen().y + (frame.getHeight() - dialog.getHeight()) / 2;
        System.out.println(" x= " + x + " y = " + y);
        Point p = WindowUtils.getPointForCentering(dialog);
        dialog.setLocation(p);
        System.out.println("Principale " + frame.getLocation());
        //System.out.println("type parent" + parent.getClass().getTypeName());
        //System.out.println("Parent x = " + parent.getWidth() + " y = " + parent.getHeight());
        //System.out.println("parent  LocationOnScreen = " + parent.getLocationOnScreen() + " Location = " + parent.getLocation());

        dialog.setLocationRelativeTo(frame);
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        System.out.println("dialog x=" + dialog.getLocation().x + " y=" + dialog.getLocation().y);
        System.out.println("Dialog " + dialog.getLocation());
    }

    public static void initDialog(JDialog dialog, int width, int height) {
        MainFrame frame = ApplicationConfig.getApplicationContext().getBean(MainFrame.class);
        dialog.setSize(width, height);
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (frame.getLocationOnScreen().x + dialog.getWidth()) / 2;
        final int y = (frame.getLocationOnScreen().y + dialog.getHeight()) / 2;
        System.out.println(" x= " + x + " y = " + y);
        dialog.setLocation(x, y);
        System.out.println("Principale " + frame.getLocation());
        System.out.println("dialog x=" + dialog.getLocation().x + " y=" + dialog.getLocation().y);
        System.out.println("Dialog " + dialog.getLocation());
        //dialog.setLocationRelativeTo(frame);
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
}
