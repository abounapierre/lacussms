package com.abouna.lacussms.service;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsJob implements Job {
    @Autowired
    private SmsService smsService;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String id = jobExecutionContext.getJobDetail().getKey().toString();
        smsService.sendSmsJob(id);
    }
}
