/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.dao.impl;

import com.abouna.lacussms.dao.generic.GenericDao;
import com.abouna.lacussms.dao.ISmsScheduledFormatDao;
import com.abouna.lacussms.entities.SmsScheduled;
import com.abouna.lacussms.entities.SmsScheduledFormat;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author SATELLITE
 */
@Repository
public class SmsScheduledFormatDaoImpl extends GenericDao<SmsScheduledFormat, Long> implements ISmsScheduledFormatDao{

    @Override
    public List<SmsScheduledFormat> getScheduledFormats(SmsScheduled smsScheduled) {
        return getManager().createQuery("SELECT s FROM SmsScheduledFormat s WHERE s.smsScheduled=:smsScheduled")
                .setParameter("smsScheduled", smsScheduled).getResultList();
    }

    @Override
    public Integer deleteScheduledFormats(SmsScheduled smsScheduled) {
        return getManager().createQuery("DELETE FROM SmsScheduledFormat s WHERE s.smsScheduled=:smsScheduled")
                .setParameter("smsScheduled", smsScheduled).executeUpdate();
    }
    
}
