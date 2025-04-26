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
        setLayout(new BorderLayout(10, 10));
        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    public static void append(String log) {
        textArea.append(log);
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    /*
    if (log.contains("Exception") || log.contains("error") || log.contains("Error")
                || log.contains("failed") || log.contains("Failed") || log.contains("Erreur") || log.contains("erreur")) {
            textArea.setForeground(Color.RED);
        } else {
            textArea.setForeground(Color.WHITE);
        }
     */
}
