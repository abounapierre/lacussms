package com.abouna.lacussms.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author SATELLITE
 */
@Entity
public class MessageMandat implements Serializable{
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
    private Integer bkMad;
    @Column(name = "bk_mad_eve")
    private String bkMadEve;
    private String numero;
    @Column(name = "is_sent")
    private Boolean isSent = false;
    @Column(name = "num_eve")
    private String numEve;

    public MessageMandat() {
    }

    public MessageMandat(String title, String content, Date sendDate) {
        this.title = title;
        this.content = content;
        this.sendDate = sendDate;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Integer getBkMad() {
        return bkMad;
    }

    public void setBkMad(Integer bkMad) {
        this.bkMad = bkMad;
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

    public String getBkMadEve() {
        return bkMadEve;
    }

    public void setBkMadEve(String bkMadEve) {
        this.bkMadEve = bkMadEve;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
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
        final MessageMandat other = (MessageMandat) obj;
        return Objects.equals(this.id, other.id);
    }

}
