/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author SATELLITE
 */
@Entity
@Table(name = "bkcli")
public class BkCli implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String code;
    private String nom;
    private String prenom;
    private String libelle;
    @Column(name = "phone")
    private long phone = 0;
    private String email;
    private boolean enabled;
    private String langue;

    private String ddn;

    public BkCli() {
    }

    public BkCli(String code, String nom, String prenom, String libelle) {
        this.code = code;
        this.nom = nom;
        this.prenom = prenom;
        this.libelle = libelle;
    }

    public BkCli(String code, String nom, String prenom, String libelle, long phone, String email, boolean enabled, String langue, String ddn) {
        this.code = code;
        this.nom = nom;
        this.prenom = prenom;
        this.libelle = libelle;
        this.phone = phone;
        this.email = email;
        this.enabled = enabled;
        this.langue = langue;
        this.ddn = ddn;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        this.langue = langue;
    }

    public String getDdn() {
        return ddn;
    }

    public void setDdn(String ddn) {
        this.ddn = ddn;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.code != null ? this.code.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BkCli other = (BkCli) obj;
        return Objects.equals(this.code, other.code);
    }

    @Override
    public String toString() {
        return  nom + " " + prenom ;
    }

   
}
