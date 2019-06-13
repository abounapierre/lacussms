/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.service.impl;

import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.tools.Utils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author SATELLITE
 */
@Component
public class EmailJob implements Job{
    @Autowired
    private LacusSmsService service;

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        JobDataMap jobDataMap = jec.getMergedJobDataMap();
        String subject = jobDataMap.getString("subject");
        String body = jobDataMap.getString("content");
        String recipientEmail = jobDataMap.getString("email");
        Utils.enregistrerMail(service,body,recipientEmail,subject);
    }
    
}
