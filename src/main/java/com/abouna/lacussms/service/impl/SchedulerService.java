/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.service.impl;

import com.abouna.lacussms.entities.SentMail;
import com.abouna.lacussms.entities.SmsProgramming;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author SATELLITE
 */
@Service
public class SchedulerService {

    @Autowired
    private Scheduler scheduler;

    /**
     * default constructor.
     */
    public SchedulerService() {
        super();
    }

    /**
     *
     * @param sentMail
     * @return
     * @throws SchedulerException
     */
    public String scheduledEmail(SentMail sentMail) throws SchedulerException {
        String result;
        System.out.println("datetime="+sentMail.getDateTime() + " timezone"+sentMail.getTimeZone());
        ZonedDateTime dateTime = ZonedDateTime.of(sentMail.getDateTime(), sentMail.getTimeZone());
        if (dateTime.isBefore(ZonedDateTime.now())) {
            result = "Fail: dateTime must be after current time";
        } else {
            JobDetail jobDetail = buildJobDetail(sentMail);
            Trigger trigger = buildJobTrigger(jobDetail, dateTime);
            scheduler.scheduleJob(jobDetail, trigger);
            result = "JobName=" + jobDetail.getKey().getName() + " key=" + jobDetail.getKey().getGroup() + " Email Scheduled Successfully!";
        }
        return result;
    }
    
    /**
     *
     * @param sentMail
     * @return
     * @throws SchedulerException
     */
    public String scheduledSms(SmsProgramming sentMail) throws SchedulerException {
        return null; 
    }

    /**
     *
     * @param sentMail
     * @return
     */
    private JobDetail buildJobDetail(SentMail sentMail) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("email", sentMail.getEmail());
        jobDataMap.put("subject", sentMail.getSubject());
        jobDataMap.put("body", sentMail.getContent());

        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), "sms-jobs")
                .withDescription("Send sms Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    /**
     *
     * @param jobDetail
     * @param dateTime
     * @return
     */
    private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime dateTime) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "email-triggers")
                .withDescription("Send Email Trigger")
                .startAt(Date.from(dateTime.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
    
    /*public String  getCronExp(final Map<String, Object> configMap) {

    LOGGER.debug(">>  getCronExp");

    String exp = "";

    final String type = (String) configMap.get(SCHEDULER_TYPE);
    final String time = (String) configMap.get(TIME);
    final String[] split = time.split(this.COLON);
    String hour = split[0];
    String min = split[1];

    if ("00".equalsIgnoreCase(min)) {
        min = ZERO;
    }
    if ("00".equalsIgnoreCase(hour)) {
        hour = "0";
    }
    if ("daily".equalsIgnoreCase(type)) {

        exp = this.ZERO + this.WHITE_SPACE + min + this.WHITE_SPACE + hour + this.WHITE_SPACE + this.ASTERISK + this.WHITE_SPACE + this.ASTERISK
                + this.WHITE_SPACE + "?";

    } else if ("monthly".equalsIgnoreCase(type)) {
        final String date = (String) configMap.get(START_DATE);
        exp = this.ZERO + this.WHITE_SPACE + min + this.WHITE_SPACE + hour + this.WHITE_SPACE + date + this.WHITE_SPACE + this.ASTERISK + this.WHITE_SPACE
                + "?";

    } else if ("weekly".equalsIgnoreCase(type)) {

        final String dayOfWeek = (String) configMap.get(DAY_OF_WEEK);

        exp = this.ZERO + this.WHITE_SPACE + min + this.WHITE_SPACE + hour + this.WHITE_SPACE + "?" + this.WHITE_SPACE + this.ASTERISK + this.WHITE_SPACE
                + dayOfWeek;
    }

    LOGGER.info("Latest cron  expression scheduler " + exp);
    LOGGER.debug("<<  getCronExp");
    return exp;
}*/

}
