package com.abouna.lacussms.views.main;

import com.abouna.lacussms.views.tools.ConstantUtils;

import javax.swing.*;
import java.awt.*;

//@Component
public class HomePanel extends JPanel {

    public HomePanel() {
        setLayout(new BorderLayout(10, 10));
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setBounds(50,50,200,200);
        tabbedPane.add("Accueil",new EmptyPanel(ConstantUtils.LOGO));
        LogScreenPanel loggingPanel = new LogScreenPanel(); //ApplicationConfig.getApplicationContext().getBean(LoggingPanel.class);
        tabbedPane.add("Logs", loggingPanel);
        add(tabbedPane, BorderLayout.CENTER);
    }
}
