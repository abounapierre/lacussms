package com.abouna.lacussms.service;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SmsJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String id = jobExecutionContext.getJobDetail().getKey().toString();
        System.out.println("job id = " + id);
        //jobExecutionContext.getScheduler().shutdown();
    }
}
