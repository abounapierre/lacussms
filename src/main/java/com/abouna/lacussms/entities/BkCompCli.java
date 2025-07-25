/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author SATELLITE
 */
@Entity
@Table(name = "bkcompcli")
public class BkCompCli implements Serializable{
    @Id
    private String numc;
    @ManyToOne
    private BkCli cli;
    private boolean enabled;

    public BkCompCli() {
    }

    public BkCompCli(String numc, BkCli cli, boolean enabled) {
        this.numc = numc;
        this.cli = cli;
        this.enabled = enabled;
    }

    public String getNumc() {
        return numc;
    }

    public void setNumc(String numc) {
        this.numc = numc;
    }

    public BkCli getCli() {
        return cli;
    }

    public void setCli(BkCli cli) {
        this.cli = cli;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return numc ;
    }
    
    
}
