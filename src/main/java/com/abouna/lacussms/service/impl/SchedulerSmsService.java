/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.service.impl;

import com.abouna.lacussms.dto.SmsScheduledDto;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author SATELLITE
 */
@Service
public class SchedulerSmsService {

    @Autowired
    private Scheduler scheduler;
    private Map<String, Object> configMap = new HashMap<>();

    /**
     *
     * @param smsScheduled
     * @return
     * @throws SchedulerException
     */
    public Map<String, String> scheduledSms(SmsScheduledDto smsScheduled) throws SchedulerException {
        Map<String, String> result = new HashMap<>(2);
        configMap.put("date", smsScheduled.getSmsScheduled().getDayOfMonth());
        configMap.put("jour", smsScheduled.getSmsScheduled().getDayOfWeek());
        configMap.put("type", smsScheduled.getSmsScheduled().getType());
        configMap.put("heure", smsScheduled.getSmsScheduled().getHeure());
        JobDetail jobDetail = buildJobDetail(smsScheduled);
        Trigger trigger = buildJobTrigger(jobDetail);
        scheduler.scheduleJob(jobDetail, trigger);
        result.put("jobName", jobDetail.getKey().getGroup());
        result.put("jobId", jobDetail.getKey().getName());
        return result;
    }

    /**
     *
     * @param sentMail
     * @return
     */
    private JobDetail buildJobDetail(SmsScheduledDto smsScheduled) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("idJob", smsScheduled.getSmsScheduled().getId());
        jobDataMap.put("description", smsScheduled.getSmsScheduled().getDescription());

        return JobBuilder.newJob(SmsJob.class)
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
    private Trigger buildJobTrigger(JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "sms-triggers")
                .withDescription("Send SMS Trigger")
                .withSchedule(cronSchedule(getCronExp()/*"0 0/2 8-22 * * ?"*/))
                .build();
    }

    public String getCronExp() {

        String exp = "";

        final String type = (String) configMap.get("type");
        final String time = (String) configMap.get("heure");
        final String[] split = time.split(":");
        String hour = split[0];
        String min = split[1];

        if ("00".equalsIgnoreCase(min)) {
            min = "0";
        }
        if ("00".equalsIgnoreCase(hour)) {
            hour = "0";
        }
        if ("daily".equalsIgnoreCase(type)) {

            exp = "0" + " " + min + " " + hour + " " + "*" + " " + "*"
                    + " " + "?";

        } else if ("monthly".equalsIgnoreCase(type)) {
            final String date = (String) configMap.get("date");
            exp = "00" + " " + min + " " + hour + " " + date + " " + "*" + " "
                    + "?";

        } else if ("weekly".equalsIgnoreCase(type)) {

            final String dayOfWeek = (String) configMap.get("jour");

            exp = "00" + " " + min + " " + hour + " " + "?" + " " + "*" + " "
                    + dayOfWeek;
        }
        System.out.println("Cron exp="+exp);
        return exp;
    }

    public Map<String, Object> getConfigMap() {
        return configMap;
    }

    public void setConfigMap(Map<String, Object> configMap) {
        this.configMap = configMap;
    }

}
