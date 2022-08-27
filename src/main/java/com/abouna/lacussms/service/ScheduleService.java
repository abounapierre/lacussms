package com.abouna.lacussms.service;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Component
public class ScheduleService {

    @Autowired
    private Scheduler scheduler;

    @PostConstruct
    public void init() throws SchedulerException {
        //String dd = scheduleJob("ENVOIE_MAIL_MARDI", "envoie des mails du mardi", "*/1 * * * * ?");
        //String de = scheduleJob("ENVOIE_MAIL_LUNDI", "envoie des mails du lundi", "*/2 * * * * ?");
        //System.out.println("retour création job " + dd);
    }

    public String scheduleJob(String detail, String desc, String cron) throws SchedulerException {
        JobDetail job = newJob(detail, desc);
        return scheduler.scheduleJob(job, trigger(job, cron)).toString();
    }

    private JobDetail newJob(String identity, String description) {
        return JobBuilder.newJob().ofType(SmsJob.class).storeDurably()
                .withIdentity(JobKey.jobKey(identity))
                .withDescription(description)
                .build();
    }

    private Trigger trigger(JobDetail jobDetail, String cron) {
        return TriggerBuilder.newTrigger().forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), jobDetail.getKey().getGroup())
                .withSchedule(cronSchedule(cron))
                .build();
    }
}
