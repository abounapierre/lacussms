package com.abouna.lacussms.task;

import com.abouna.lacussms.config.AppRunConfig;
import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.config.LicenceConfig;
import com.abouna.lacussms.views.main.BottomPanel;
import com.abouna.lacussms.views.utils.Logger;

import java.awt.*;
import java.util.concurrent.TimeUnit;

import static com.abouna.lacussms.views.tools.ConstantUtils.DEFAULT_WAIT_NUMBER;

public class StartService {
    public static Thread thread;
    public static boolean running = false;
    private static DataTaskService dataTaskService;
    private static MessageTaskService messageTaskService;
    private static LicenceConfig licenceConfig;

    public static void startData() {
        if(dataTaskService == null) {
            dataTaskService = ApplicationConfig.getApplicationContext().getBean(DataTaskService.class);
        }
        dataTaskService.executeTask();
    }

    public static void startMessage() {
        if(messageTaskService == null) {
            messageTaskService = ApplicationConfig.getApplicationContext().getBean(MessageTaskService.class);
        }
        messageTaskService.executeTask();
    }

    public static void startLicence() {
        if (licenceConfig == null) {
            licenceConfig = ApplicationConfig.getApplicationContext().getBean(LicenceConfig.class);
        }
        licenceConfig.controlLicence();
    }

    public static void waitThread(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        }catch (Exception e){
            Logger.error("Error in licence job", e, StartService.class);
         }
    }

    public static void start() {
        startLicence();
        startData();
        startMessage();
    }

    public static void startSequential() {
        try {
            DataTaskService dataTaskService = ApplicationConfig.getApplicationContext().getBean(DataTaskService.class);
            MessageTaskService messageTaskService = ApplicationConfig.getApplicationContext().getBean(MessageTaskService.class);
            LicenceConfig licenceConfig = ApplicationConfig.getApplicationContext().getBean(LicenceConfig.class);
            AppRunConfig appRunConfig = ApplicationConfig.getApplicationContext().getBean(AppRunConfig.class);
            appRunConfig.setDataServiceEnabled(Boolean.TRUE);
            appRunConfig.setMessageServiceEnabled(Boolean.TRUE);
            thread = new Thread(() -> {
                running = true;
                while (running) {
                    try {
                        Logger.info("begin running service licence task....", StartService.class);
                        licenceConfig.controlLicence();
                        Logger.info("begin running service data task....", StartService.class);
                        dataTaskService.executeTask();
                        Logger.info("begin running service message task....", StartService.class);
                        messageTaskService.executeTask();
                        BottomPanel.settextLabel("");
                        Logger.info("service is running ....", StartService.class);
                        waitThread(DEFAULT_WAIT_NUMBER);
                        Logger.info("end running service task....", StartService.class);
                    } catch (Exception e) {
                        Logger.error("Error in task execution", e, StartService.class);
                    }
                }
                Logger.info("start stopping service task....", StartService.class);
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                    Logger.info("service is already stopped....", StartService.class);
                }
            });
            Logger.info(String.format("start thread....%s", thread.getId()), StartService.class);
            thread.start();
        } catch (Exception e) {
            Logger.error("Error in task execution", e, StartService.class);
        }
    }

    public static void stopper() {
        AppRunConfig appRunConfig = ApplicationConfig.getApplicationContext().getBean(AppRunConfig.class);
        appRunConfig.setDataServiceEnabled(Boolean.FALSE);
        appRunConfig.setMessageServiceEnabled(Boolean.FALSE);
        BottomPanel.settextLabel("");
        Logger.info("le service a été arreté", StartService.class);
        BottomPanel.settextLabel("", Color.BLACK);
    }
}
