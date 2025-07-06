package com.abouna.lacussms.views.main;

import javax.swing.*;
import java.awt.*;

public class ContainerPanel extends JPanel {
    private JPanel current;

    public void add(JPanel panel) {
        add(BorderLayout.CENTER, panel);
        setCurrent(panel);
    }

    public JPanel getCurrent() {
        return current;
    }

    public void setCurrent(JPanel current) {
        this.current = current;
    }
}
