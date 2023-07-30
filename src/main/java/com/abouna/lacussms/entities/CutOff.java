/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author SATELLITE
 */
@Entity
@Table(name = "CUT_OFF")
public class CutOff implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "CODE_DATE")
    private String codeDate;
    @Column(name = "CUT_DATE")
    @Temporal(jakarta.persistence.TemporalType.TIMESTAMP)
    private Date cutDate;

    public CutOff() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodeDate() {
        return codeDate;
    }

    public void setCodeDate(String codeDate) {
        this.codeDate = codeDate;
    }

    public Date getCutDate() {
        return cutDate;
    }

    public void setCutDate(Date cutDate) {
        this.cutDate = cutDate;
    }

    @Override
    public String toString() {
        return cutDate.toString() ;
    }
    
    
}
