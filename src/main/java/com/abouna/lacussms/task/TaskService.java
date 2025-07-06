package com.abouna.lacussms.task;

import com.abouna.lacussms.service.PdfReportService;
import com.abouna.lacussms.views.utils.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@Component
public class TaskService {
    private final String fixedrate;
    private static final Map<String, ScheduledFuture<?>> scheduledTasks = new IdentityHashMap<>();

    public TaskService(@Qualifier("external-configs") Properties properties) {
        this.fixedrate = properties.getProperty("application.monthly.report.scheduler.cron");
    }

    public void cancelScheduledTask(String id) {
        ScheduledFuture<?> future = scheduledTasks.get(id);
        if (null != future) {
            future.cancel(true);
        }
    }

    @PostConstruct
    public void runTask() {
        String id = UUID.randomUUID().toString();
        Logger.info("TaskService initialized with id: " + id, getClass());
        CustomTaskScheduler taskScheduler = new CustomTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.initialize();
        taskScheduler.scheduleAtFixedRate(id, this::generateMonthlyReport, fixedrate);
    }

    void generateMonthlyReport() {
        try {
            Logger.info("Building month report", getClass());
            PdfReportService.getInstance().buildPdfReport();
        } catch (Exception e) {
            Logger.error("Error when building month report" + e.getMessage(), e, getClass());
        }
    }

    private static class CustomTaskScheduler extends ThreadPoolTaskScheduler {
        public void scheduleAtFixedRate(String id, Runnable task, String cron) {
            ScheduledFuture<?> future = super.schedule(task, new CronTrigger(cron));
            scheduledTasks.put(id, future);
        }
    }
}
