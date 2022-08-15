package com.abouna.lacussms.views.main;

import com.abouna.lacussms.config.ApplicationConfig;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {

    public HomePanel() {
        setLayout(new BorderLayout(10, 10));
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setBounds(50,50,200,200);
        tabbedPane.add("Accueil",new EmptyPanel());
        LoggingPanel loggingPanel = ApplicationConfig.getApplicationContext().getBean(LoggingPanel.class);
        tabbedPane.add("Logs",loggingPanel);
        tabbedPane.addChangeListener(e -> {
            if(tabbedPane.getSelectedIndex() == 1) {
                    loggingPanel.printLogging();
            }
        });
        add(tabbedPane, BorderLayout.CENTER);
    }
}
