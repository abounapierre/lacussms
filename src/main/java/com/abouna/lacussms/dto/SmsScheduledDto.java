/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.dto;

import com.abouna.lacussms.entities.MessageFormat;
import com.abouna.lacussms.entities.SmsScheduled;
import java.util.List;

/**
 *
 * @author SATELLITE
 */
public class SmsScheduledDto {
    private SmsScheduled smsScheduled;
    private List<MessageFormat> messageFormats;
    private List<String> comptes;
    private String action;
    
    public SmsScheduledDto(){
    }

    public SmsScheduled getSmsScheduled() {
        return smsScheduled;
    }

    public void setSmsScheduled(SmsScheduled smsScheduled) {
        this.smsScheduled = smsScheduled;
    }

    public List<MessageFormat> getMessageFormats() {
        return messageFormats;
    }

    public void setMessageFormats(List<MessageFormat> messageFormats) {
        this.messageFormats = messageFormats;
    }

    public List<String> getComptes() {
        return comptes;
    }

    public void setComptes(List<String> comptes) {
        this.comptes = comptes;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
    
    
}
