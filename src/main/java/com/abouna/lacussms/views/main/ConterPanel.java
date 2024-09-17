package com.abouna.lacussms.views.main;

import javax.swing.*;
import java.awt.*;

public class ConterPanel extends JPanel {

    public ConterPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JPanel panel = new JPanel();
        JButton button = new JButton("Button1");
        button.setPreferredSize(new Dimension(100, 50));

        panel.add(button);

        add(panel);
    }

}
