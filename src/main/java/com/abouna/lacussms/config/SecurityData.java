package com.abouna.lacussms.config;

public class SecurityData {
    private final String validityDate;
    private final String macAddress;

    public SecurityData(String validityDate, String macAddress) {
        this.validityDate = validityDate;
        this.macAddress = macAddress;
    }

    public String getValidityDate() {
        return validityDate;
    }

    public String getMacAddress() {
        return macAddress;
    }
}
