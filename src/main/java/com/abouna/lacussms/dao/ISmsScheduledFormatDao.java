/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.dao;

import com.abouna.lacussms.dao.generic.IDao;
import com.abouna.lacussms.entities.SmsScheduled;
import com.abouna.lacussms.entities.SmsScheduledFormat;

import java.util.List;

/**
 *
 * @author SATELLITE
 */
public interface ISmsScheduledFormatDao extends IDao<SmsScheduledFormat, Long>{
    public List<SmsScheduledFormat> getScheduledFormats(SmsScheduled smsScheduled);
    public Integer deleteScheduledFormats(SmsScheduled smsScheduled);
}
