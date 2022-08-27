package com.abouna.lacussms.views.main;

import javax.swing.*;
import java.awt.*;

//@Component
public class HomePanel extends JPanel {
    private final LoggingPanel loggingPanel;

    public HomePanel() {
        setLayout(new BorderLayout(10, 10));
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setBounds(50,50,200,200);
        tabbedPane.add("Accueil",new EmptyPanel());
        loggingPanel = new LoggingPanel(); //ApplicationConfig.getApplicationContext().getBean(LoggingPanel.class);
        tabbedPane.add("Logs",loggingPanel);
        tabbedPane.addChangeListener(e -> {
            if(tabbedPane.getSelectedIndex() == 1) {
                    loggingPanel.printLogging();
            }
        });
        add(tabbedPane, BorderLayout.CENTER);
    }

    public LoggingPanel getLoggingPanel() {
        return loggingPanel;
    }
}
