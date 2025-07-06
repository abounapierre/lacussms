package com.abouna.lacussms.dto;

import com.abouna.lacussms.entities.BkCli;

public class BkCliCompte {
    private BkCli bkCli;
    private String compte;

    public BkCliCompte() {
    }

    public BkCliCompte(BkCli bkCli, String compte) {
        this.bkCli = bkCli;
        this.compte = compte;
    }

    public BkCli getBkCli() {
        return bkCli;
    }

    public void setBkCli(BkCli bkCli) {
        this.bkCli = bkCli;
    }

    public String getCompte() {
        return compte;
    }

    public void setCompte(String compte) {
        this.compte = compte;
    }

}
