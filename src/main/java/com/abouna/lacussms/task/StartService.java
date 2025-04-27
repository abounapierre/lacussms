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
    public static Thread dataThread;
    public static Thread messageThread;
    public static boolean running = false;

    public static void startData() {
        DataTaskService dataTaskService = ApplicationConfig.getApplicationContext().getBean(DataTaskService.class);
        dataThread = new Thread(() -> {
            while (true) {
                dataTaskService.executeTask();
                waitThread(5000);
            }
        });
        dataThread.start();
    }

    public static void startMessage() {
        MessageTaskService messageTaskService = ApplicationConfig.getApplicationContext().getBean(MessageTaskService.class);
        messageThread = new Thread(() -> {
            while (true) {
                messageTaskService.executeTask();
                waitThread(5000);
            }
        });
        messageThread.start();
    }

    public static void startLicence() {
        LicenceConfig licenceConfig = ApplicationConfig.getApplicationContext().getBean(LicenceConfig.class);
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
        startData();
        startMessage();
        startLicence();
    }

    public static void startSequential() {
        DataTaskService dataTaskService = ApplicationConfig.getApplicationContext().getBean(DataTaskService.class);
        MessageTaskService messageTaskService = ApplicationConfig.getApplicationContext().getBean(MessageTaskService.class);
        LicenceConfig licenceConfig = ApplicationConfig.getApplicationContext().getBean(LicenceConfig.class);
        AppRunConfig appRunConfig = ApplicationConfig.getApplicationContext().getBean(AppRunConfig.class);
        appRunConfig.setDataServiceEnabled(Boolean.TRUE);
        appRunConfig.setMessageServiceEnabled(Boolean.TRUE);
        running = true;
        dataThread = new Thread(() -> {
            while (running) {
                try {
                    Logger.info("running service task....", StartService.class);
                    licenceConfig.controlLicence();
                    dataTaskService.executeTask();
                    messageTaskService.executeTask();
                    BottomPanel.settextLabel("");
                    waitThread(DEFAULT_WAIT_NUMBER);
                } catch (Exception e) {
                    Logger.error("Error in task execution", e, StartService.class);
                }
            }
            Logger.info("stopping service task....", StartService.class);
            dataThread.interrupt();
        });
        dataThread.start();
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
