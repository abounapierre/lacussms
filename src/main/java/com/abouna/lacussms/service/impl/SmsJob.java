/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.service.impl;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 *
 * @author SATELLITE
 */
@Component
public class SmsJob extends QuartzJobBean{

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {
       JobDataMap jobDataMap = jec.getMergedJobDataMap();
        System.out.println("Execution du JOb test " + jobDataMap.getLong("idJob"));
    }
    
}
