package com.abouna.lacussms.views.components;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class LacusButton extends JButton{
    public LacusButton(ImageIcon icon) {
        super(icon);
    }
    public LacusButton(String text) {
        super(text);
    }
    public LacusButton(ImageIcon icon, String desc, Consumer<ActionEvent> action) {
        super(icon);
        addActionListener(action::accept);
        setToolTipText(desc);
    }
    public LacusButton(String text, Consumer<ActionEvent> action) {
        super(text);
        addActionListener(action::accept);
        setToolTipText(text);
    }
}
