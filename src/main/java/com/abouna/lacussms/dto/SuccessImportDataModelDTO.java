package com.abouna.lacussms.dto;

public class SuccessImportDataModelDTO {
    private final ClientCSV clientCSV;

    public SuccessImportDataModelDTO(ClientCSV clientCSV) {
        this.clientCSV = clientCSV;
    }

    public ClientCSV getClientCSV() {
        return clientCSV;
    }
}
