package com.abouna.lacussms.views.utils;

import org.springframework.stereotype.Component;

@Component
public class LogBean {
    private String logs;

    public String getLogs() {
        return logs;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }
}
