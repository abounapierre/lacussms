package com.abouna.lacussms.views.groupe;

import javax.swing.*;
import java.awt.*;

public class TitlePanel extends JPanel {
    public TitlePanel(String title) {
        setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel lbl;
        lbl = new JLabel(title);
        add(lbl);
        lbl.setFont(new Font("Broadway", Font.BOLD, 30));
    }
}
