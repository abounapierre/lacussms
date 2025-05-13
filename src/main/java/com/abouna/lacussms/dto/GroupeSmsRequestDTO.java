package com.abouna.lacussms.dto;

public class GroupeSmsRequestDTO {
    private String phoneNumbers;
    private String groupes;
    private String clients;
    private String message;
    private boolean personalized;

    public String getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getGroupes() {
        return groupes;
    }

    public void setGroupes(String groupes) {
        this.groupes = groupes;
    }

    public String getClients() {
        return clients;
    }

    public void setClients(String clients) {
        this.clients = clients;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isPersonalized() {
        return personalized;
    }

    public void setPersonalized(boolean personalized) {
        this.personalized = personalized;
    }
}
