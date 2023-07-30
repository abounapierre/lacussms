package com.abouna.lacussms.service;

import jakarta.annotation.PostConstruct;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



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

    public String scheduleJob(String id, String group, String desc, String cron) throws SchedulerException {
        JobDetail job = newJob(id, desc, group);
        return scheduler.scheduleJob(job, trigger(job, cron)).toString();
    }

    public boolean deleteJob(String id) throws SchedulerException {
        return scheduler.deleteJob(JobKey.jobKey(id));
    }

    public boolean stopJob(String id) throws SchedulerException {
        return scheduler.interrupt(JobKey.jobKey(id));
    }

    private JobDetail newJob(String identity, String description, String group) {
        if(group.equals("EMAIL")) {
            return JobBuilder.newJob().ofType(EmailJob.class).storeDurably()
                    .withIdentity(identity, group)
                    .withDescription(description)
                    .build();
        } else {
            return JobBuilder.newJob().ofType(SmsJob.class).storeDurably()
                    .withIdentity(identity, group)
                    .withDescription(description)
                    .build();
        }
    }

    private Trigger trigger(JobDetail jobDetail, String cron) {
        return TriggerBuilder.newTrigger().forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), jobDetail.getKey().getGroup())
                .withSchedule(cronSchedule(cron))
                .build();
    }
}
