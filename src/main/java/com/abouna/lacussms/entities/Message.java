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
@Table(name = "message")
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String title;
    private String content;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date sendDate;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date sendTime;
    @JoinColumn(name = "bkeve")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private BkEve bkEve;
    private String numero;
    @Column(name = "is_sent")
    private Boolean isSent = false;
    @Column(name = "num_eve")
    private String numEve;

    public Message() {
    }

    public Message(String title, String content, Date sendDate) {
        this.title = title;
        this.content = content;
        this.sendDate = sendDate;
    }

    public Message(Integer id, String title, String content, Date sendDate, String numero) {
        this.title = title;
        this.content = content;
        this.sendDate = sendDate;
        this.id = id;
        this.numero = numero;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public BkEve getBkEve() {
        return bkEve;
    }

    public void setBkEve(BkEve bkEve) {
        this.bkEve = bkEve;
    }

    public Boolean getSent() {
        return isSent;
    }

    public void setSent(Boolean sent) {
        isSent = sent;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getNumEve() {
        return numEve;
    }

    public void setNumEve(String numEve) {
        this.numEve = numEve;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Message)) {
            return false;
        }
        Message other = (Message) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.abouna.sendermanagerpro.entities.Message[ id=" + id + " ]";
    }
    
}
