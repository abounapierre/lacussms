/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author SATELLITE
 */
@Entity
@Table(name = "SMS_SCHEDULED_FORMAT")
public class SmsScheduledFormat implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "SMS_SCHEDULED_ID")
    private SmsScheduled smsScheduled;
    @Column(name = "quartz_id")
    private String quartzId;
    @ManyToOne
    @JoinColumn(name = "SMS_FORMAT_ID")
    private MessageFormat messageFormat;

    public SmsScheduledFormat() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SmsScheduled getSmsScheduled() {
        return smsScheduled;
    }

    public void setSmsScheduled(SmsScheduled smsScheduled) {
        this.smsScheduled = smsScheduled;
    }

    public MessageFormat getMessageFormat() {
        return messageFormat;
    }

    public void setMessageFormat(MessageFormat messageFormat) {
        this.messageFormat = messageFormat;
    }

    public String getQuartzId() {
        return quartzId;
    }

    public void setQuartzId(String quartzId) {
        this.quartzId = quartzId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.smsScheduled);
        hash = 29 * hash + Objects.hashCode(this.messageFormat);
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
        final SmsScheduledFormat other = (SmsScheduledFormat) obj;
        if (!Objects.equals(this.smsScheduled, other.smsScheduled)) {
            return false;
        }
        return Objects.equals(this.messageFormat, other.messageFormat);
    }
    
}
