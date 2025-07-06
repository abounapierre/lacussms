package com.abouna.lacussms.views;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.io.IOException;

public class MandatPanel extends JPanel {

    public MandatPanel() throws IOException {
        setLayout(new BorderLayout(10, 10));
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setForeground(Color.BLUE);
        tabbedPane.addChangeListener(this::updateUI);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setBounds(50,50,200,200);
        tabbedPane.add("Evenements", new BkMadPanel());
        tabbedPane.add("Messages", new MessageMandatPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void updateUI(ChangeEvent changeEvent) {
        JTabbedPane tabbedPane = (JTabbedPane) changeEvent.getSource();
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex == 0) {
            // Update UI for Evenements tab
            BkMadPanel bkMadPanel = (BkMadPanel) tabbedPane.getComponentAt(selectedIndex);
            bkMadPanel.updateUI();
        } else if (selectedIndex == 1) {
            // Update UI for Messages tab
            MessageMandatPanel messageMandatPanel = (MessageMandatPanel) tabbedPane.getComponentAt(selectedIndex);
            messageMandatPanel.updateUI();
        }
    }
}
