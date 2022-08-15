package com.abouna.lacussms.service.impl;

import javax.swing.*;
import java.util.List;

public class LogWorker extends SwingWorker<Void, String> {
    private JTextArea textArea;
    private String lastLog;

    public LogWorker(JTextArea textArea, String lastLog) {
        this.textArea = textArea;
        this.lastLog = lastLog;
    }

    @Override
    protected Void doInBackground() throws Exception {

        while (!isCancelled()) {
            lastLog = "";
            publish(lastLog);
            Thread.sleep(1000);
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        /*for (String c : chunks) {
            route.add(c);
        }
        map.repaint();*/
    }
}
