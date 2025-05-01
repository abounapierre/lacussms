package com.abouna.lacussms.config;

public class AppRunConfig {
    private Boolean dataServiceEnabled;
    private Boolean messageServiceEnabled;
    private Boolean testModeEnabled;

    public AppRunConfig(Boolean dataServiceEnabled, Boolean messageServiceEnabled) {
        this.dataServiceEnabled = dataServiceEnabled;
        this.messageServiceEnabled = messageServiceEnabled;
    }

    public AppRunConfig(Boolean dataServiceEnabled, Boolean messageServiceEnabled, Boolean testModeEnabled) {
        this.dataServiceEnabled = dataServiceEnabled;
        this.messageServiceEnabled = messageServiceEnabled;
        this.testModeEnabled = testModeEnabled;
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
    public Boolean getTestModeEnabled() {
        return testModeEnabled;
    }
    public void setTestModeEnabled(Boolean testModeEnabled) {
        this.testModeEnabled = testModeEnabled;
    }
}
