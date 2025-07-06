package com.abouna.lacussms.views.utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;

public class LogParam {
    private BufferedReader reader;
    private JTextArea textArea;

    private String log;

    private ActionEvent event;

    public BufferedReader getReader() {
        return reader;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }

    public ActionEvent getEvent() {
        return event;
    }

    public void setEvent(ActionEvent event) {
        this.event = event;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
