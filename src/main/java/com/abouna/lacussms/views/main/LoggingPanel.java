package com.abouna.lacussms.views.main;

import com.abouna.lacussms.views.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class LoggingPanel extends JPanel {
    private final Timer timer;
    private final JTextArea textArea;

    private final Logger logger = LoggerFactory.getLogger(LoggingPanel.class);
    public LoggingPanel() {
        textArea = new JTextArea(25, 60);
        textArea.setBackground(Color.black);
        textArea.setLineWrap(true);
        Font font = new Font(Font.SERIF, Font.BOLD, 20);
        textArea.setFont(font);
        textArea.setForeground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        timer = new Timer(100, e -> textArea.setText(Utils.getLog()));
    }

    public void printLogging() {
        logger.info("AFFICHE LES LOGS");
        timer.start();
    }

}
