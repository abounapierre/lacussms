package com.abouna.lacussms.views.main;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.main.App;
import com.abouna.lacussms.views.tools.ConstantUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SplashScreen extends JWindow {
    private static final JProgressBar progressBar = new JProgressBar();
    public static SplashScreen execute;
    private static int count;
    private static Timer timer;

    public SplashScreen() {

        Container container = getContentPane();
        container.setLayout(null);

        EmptyPanel panel = new EmptyPanel(ConstantUtils.LOGO_1);
        panel.setBorder(new javax.swing.border.EtchedBorder());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBounds(10, 10, 348, 150);
        panel.setLayout(null);
        container.add(panel);

        progressBar.setMaximum(200);
        progressBar.setBounds(55, 180, 250, 15);
        container.add(progressBar);
        loadProgressBar();
        setSize(370, 215);
        setLocationRelativeTo(null);
        setVisible(true);
        execute = this;
    }

    private void loadProgressBar() {
        ActionListener al = evt -> {
            count++;
            progressBar.setValue(count);
            if (App.appliRun) {
                EventQueue.invokeLater(() -> {
                    MainFrame frame = ApplicationConfig.getApplicationContext().getBean(MainFrame.class);
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    frame.setSize((int) screenSize.getWidth() - 100, (int) screenSize.getHeight() - 100);
                    frame.setLocationRelativeTo(null);
                    frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                    frame.setVisible(true);
                });
                execute.setVisible(false);
                timer.stop();
                execute.dispose();
                MainFrame.thread.interrupt();
            }
        };
        timer = new Timer(50, al);
        timer.start();
    }
}
