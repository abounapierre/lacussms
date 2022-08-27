/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author SATELLITE
 */
@Entity
@Table(name = "executed_job")
public class ExecutedJob implements Serializable{
    @Id
    private Long id;
    @ManyToOne
    private SmsProgramming smsProgramming;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date executionDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SmsProgramming getSmsProgramming() {
        return smsProgramming;
    }

    public void setSmsProgramming(SmsProgramming smsProgramming) {
        this.smsProgramming = smsProgramming;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }
    
    
    
}
