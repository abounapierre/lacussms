package com.abouna.lacussms.views.main;

import javax.swing.*;
import java.awt.*;

public class LogScreenPanel extends JPanel{
    public static final JTextArea textArea = new JTextArea(25, 60);

    public LogScreenPanel() {
        textArea.setBackground(Color.black);
        textArea.setLineWrap(true);
        Font font = new Font(Font.SERIF, Font.BOLD, 20);
        textArea.setFont(font);
        textArea.setForeground(Color.WHITE);
        textArea.setCaretPosition(textArea.getDocument().getLength());
        setLayout(new BorderLayout(10, 10));
        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    public static void append(String log) {
        textArea.append(log);
    }

    public static void deleteOldText() {
        if(textArea.getText().length() < 1000000) {
            return;
        }
        textArea.replaceRange(null, 0, 200000);
    }
}
