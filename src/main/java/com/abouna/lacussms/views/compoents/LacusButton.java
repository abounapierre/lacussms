package com.abouna.lacussms.views.compoents;

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
    public LacusButton(ImageIcon icon, Consumer<ActionEvent> action) {
        super(icon);
        addActionListener(action::accept);
    }
    public LacusButton(String text, Consumer<ActionEvent> action) {
        super(text);
        addActionListener(action::accept);
    }
}
