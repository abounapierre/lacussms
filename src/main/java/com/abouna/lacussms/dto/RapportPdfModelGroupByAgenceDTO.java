package com.abouna.lacussms.dto;

import java.util.List;

public class RapportPdfModelGroupByAgenceDTO {
    private String codeAgence;
    private String nomAgence;
    private List<RapportPdfModelDTO> dataModels;

    public String getCodeAgence() {
        return codeAgence;
    }

    public void setCodeAgence(String codeAgence) {
        this.codeAgence = codeAgence;
    }

    public String getNomAgence() {
        return nomAgence;
    }

    public void setNomAgence(String nomAgence) {
        this.nomAgence = nomAgence;
    }

    public List<RapportPdfModelDTO> getDataModels() {
        return dataModels;
    }

    public void setDataModels(List<RapportPdfModelDTO> dataModels) {
        this.dataModels = dataModels;
    }
}
