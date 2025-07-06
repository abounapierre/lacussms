package com.abouna.lacussms.dto;

import java.util.List;

public class ReportPDFAgenceInputDTO {
    private List<RapportPdfModelGroupByAgenceDTO> messages;
    private String entreprise;
    private String dateDebut;
    private String dateFin;
    private boolean mask;

    public List<RapportPdfModelGroupByAgenceDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<RapportPdfModelGroupByAgenceDTO> messages) {
        this.messages = messages;
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

    public String getTitle() {
        return "Rapport des messages Par Agence";
    }
}
