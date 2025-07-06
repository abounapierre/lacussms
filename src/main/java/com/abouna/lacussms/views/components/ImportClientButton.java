package com.abouna.lacussms.views.components;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ImportClientButton extends JButton {

    public void setAction(ActionEvent action) {
    }

    public ImportClientButton(ImageIcon icon) {
        super(icon);
        addActionListener(this::setAction);
    }

    public ImportClientButton(String title) {
        super(title);
        addActionListener(this::setAction);
    }
}
