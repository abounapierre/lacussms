package com.abouna.lacussms.dto;

import java.util.List;

public class ReportPDFPeriodeInputDTO {
    private List<RapportPdfModelDTO> dataModels;
    private String entreprise;
    private String dateDebut;
    private String dateFin;
    private boolean mask;

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

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public boolean isMask() {
        return mask;
    }

    public void setMask(boolean mask) {
        this.mask = mask;
    }
}
