package com.abouna.lacussms.dto;

import java.util.List;

public class ReportPDFInputDTO {
    List<RapportPdfModelDTO> dataModels;
    private String entreprise;
    private String month;
    private String year;

    public List<RapportPdfModelDTO> getDataModels() {
        return dataModels;
    }

    public void setDataModels(List<RapportPdfModelDTO> dataModels) {
        this.dataModels = dataModels;
    }

    public String getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(String entreprise) {
        this.entreprise = entreprise;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
