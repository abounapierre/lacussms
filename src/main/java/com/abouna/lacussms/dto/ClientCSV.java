package com.abouna.lacussms.dto;

import static org.codehaus.plexus.util.StringUtils.isNumeric;

public class ClientCSV {
    private final Integer ligne;
    private final String code;
    private final String civilite;
    private final String nom;
    private final String prenom;
    private final String telephone;
    private final String compte;
    private final String langue;

    public ClientCSV(Integer ligne, String code, String civilite, String nom, String prenom, String telephone, String compte, String langue) {
        this.code = code;
        this.civilite = civilite;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.compte = compte;
        this.langue = langue;
        this.ligne = ligne;
    }

    public String getCode() {
        return code;
    }

    public String getCivilite() {
        return civilite;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getCompte() {
        return compte;
    }

    public String getLangue() {
        return langue;
    }

    public Integer getLigne() {
        return ligne;
    }

    public boolean isInvalid() {
        return code == null || code.isEmpty() ||
                civilite == null || civilite.isEmpty() ||
                ((nom == null || nom.isEmpty()) &&
                (prenom == null || prenom.isEmpty())) ||
                telephone == null || telephone.isEmpty() || !isNumeric(telephone) ||
                compte == null || compte.isEmpty() ||
                langue == null || langue.isEmpty();
    }

    public Integer getIndex() {
        return ligne - 1;
    }
}
