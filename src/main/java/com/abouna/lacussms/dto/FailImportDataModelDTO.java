package com.abouna.lacussms.dto;

public class FailImportDataModelDTO {
    private String id;
    private String errorMessage;
    private int line;

    public FailImportDataModelDTO(String errorMessage, int line) {
        this.errorMessage = errorMessage;
        this.line = line;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
