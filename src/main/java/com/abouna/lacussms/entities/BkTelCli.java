package com.abouna.lacussms.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 * @author ABOUNA
 */
@Entity
public class BkTelCli implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "cli")
    private BkCli bkCli;
    private long numTel;
    private String typeNum;
    private boolean pardefault = false;

    public boolean isPardefault() {
        return pardefault;
    }

    public void setPardefault(boolean pardefault) {
        this.pardefault = pardefault;
    }

    public BkTelCli() {
    }

    public BkTelCli(BkCli bkCli, long numTel, String typeNum) {
        this.bkCli = bkCli;
        this.numTel = numTel;
        this.typeNum = typeNum;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BkCli getBkCli() {
        return bkCli;
    }

    public void setBkCli(BkCli bkCli) {
        this.bkCli = bkCli;
    }

    public long getNumTel() {
        return numTel;
    }

    public void setNumTel(long numTel) {
        this.numTel = numTel;
    }

    public String getTypeNum() {
        return typeNum;
    }

    public void setTypeNum(String typeNum) {
        this.typeNum = typeNum;
    }
    
    
}
