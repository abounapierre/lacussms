package com.abouna.lacussms.dto;

public class SendResponseDTO {
    private boolean isSent;
    private String message;

    public SendResponseDTO() {
    }
    public SendResponseDTO(boolean isSent, String message) {
        this.isSent = isSent;
        this.message = message;
    }
    public boolean isSent() {
        return isSent;
    }
    public void setSent(boolean isSent) {
        this.isSent = isSent;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SendResponseDTO{" +
                "isSent=" + isSent +
                ", message='" + message + '\'' +
                '}';
    }
}
