package com.abouna.lacussms.config;

public class AppRunConfig {
    private Boolean dataServiceEnabled;
    private Boolean messageServiceEnabled;

    public AppRunConfig(Boolean dataServiceEnabled, Boolean messageServiceEnabled) {
        this.dataServiceEnabled = dataServiceEnabled;
        this.messageServiceEnabled = messageServiceEnabled;
    }

    public static AppRunConfig getInstance() {
        return ApplicationConfig.getApplicationContext().getBean(AppRunConfig.class);
    }

    public Boolean getDataServiceEnabled() {
        return dataServiceEnabled;
    }

    public void setDataServiceEnabled(Boolean dataServiceEnabled) {
        this.dataServiceEnabled = dataServiceEnabled;
    }

    public Boolean getMessageServiceEnabled() {
        return messageServiceEnabled;
    }

    public void setMessageServiceEnabled(Boolean messageServiceEnabled) {
        this.messageServiceEnabled = messageServiceEnabled;
    }
}
