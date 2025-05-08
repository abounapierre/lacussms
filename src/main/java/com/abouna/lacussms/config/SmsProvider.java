package com.abouna.lacussms.config;

public class SmsProvider {
    private String name;

    public SmsProvider(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static SmsProvider getInstance() {
        return ApplicationConfig.getApplicationContext().getBean(SmsProvider.class);
    }
}
