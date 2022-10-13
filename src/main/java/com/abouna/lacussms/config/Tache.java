/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.config;

import com.abouna.lacussms.entities.Licence;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.main.LogFile;
import com.abouna.lacussms.views.main.MainFrame;
import com.abouna.lacussms.views.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 *
 * @author SATELLITE
 */
@Component
public class Tache {

    private static final Logger logger = LoggerFactory.getLogger(Tache.class);
    @Autowired
    @Qualifier("logPath")
    private String path;
    @Autowired
    private LacusSmsService service;

    @Autowired
    private LogFile logBean;

    @Autowired
    private Environment env;

    public Tache() {
    }

    ////@Scheduled(cron = "*/10 * * * * *")
    @Scheduled(cron = "0 0 1,8,12,17 * * *")
    public void executeTask() {
        Utils.enregistrerMail(service);
    }

    @Scheduled(cron = "0 0 12 * * *")
    public void checkLic() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        List<Licence> licences = service.getLicences();
        if (!licences.isEmpty()) {
            try {
                Licence lic = licences.get(0);
                Date date = Utils.getTimeFromInternet();
                long jour = (date.getTime() - sdf.parse(lic.getStartDate()).getTime()) / (1000 * 60 * 60 * 24);
                lic.setJour(lic.getJour() - jour);
                service.modifier(lic);
            } catch (ParseException ex) {
                java.util.logging.Logger.getLogger(Tache.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Scheduled(cron = "*/1 * * * * *")
    public void logTask() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            //LogFile logBean = ApplicationConfig.getApplicationContext().getBean(LogFile.class);
            logBean.setLog(reader.lines().collect(Collectors.joining("\n")));
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        }
    }

    @Scheduled(cron = "*/1 * * * * *")
    public void controlLicence() {
        String original = env.getProperty("application.validDate");
        Date date = Utils.getDateSimpleFormat("ddMMyyHHmmss", original);
        if(date != null && date.before(Utils.getTimeFromInternet())) {
            String msg = "une erreur est survenue lors de l'éxécution de l'application";
            logger.error(msg);
            JOptionPane.showMessageDialog(MainFrame.getFrames()[0], msg);
            System.exit(0);
        }
    }
}
