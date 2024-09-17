package com.abouna.lacussms.views.main;

import com.abouna.lacussms.views.tools.ConstantUtils;

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
        tabbedPane.add("Accueil",new EmptyPanel(ConstantUtils.LOGO));
        loggingPanel = new LoggingPanel(); //ApplicationConfig.getApplicationContext().getBean(LoggingPanel.class);
        tabbedPane.add("Logs",loggingPanel);
        tabbedPane.addChangeListener(e -> {
            if(tabbedPane.getSelectedIndex() == 1) {
                    loggingPanel.printLogging();
            } else {
                loggingPanel.getTimer().stop();
            }
        });
        add(tabbedPane, BorderLayout.CENTER);
    }

    public LoggingPanel getLoggingPanel() {
        return loggingPanel;
    }
}
