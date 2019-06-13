/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author SATELLITE
 */
@Entity
@Table(name = "job_account")
public class JobAccount implements Serializable{
    @Id
    private Long id;
    @ManyToOne
    @JoinColumn(name = "account_number_id")
    private BkCompCli bkCompCli;
    @ManyToOne
    @JoinColumn(name = "sms_job_id")
    private SmsScheduled smsScheduled;

    public JobAccount() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BkCompCli getBkCompCli() {
        return bkCompCli;
    }

    public void setBkCompCli(BkCompCli bkCompCli) {
        this.bkCompCli = bkCompCli;
    }

    public SmsScheduled getSmsScheduled() {
        return smsScheduled;
    }

    public void setSmsScheduled(SmsScheduled smsScheduled) {
        this.smsScheduled = smsScheduled;
    }
    
    
}
