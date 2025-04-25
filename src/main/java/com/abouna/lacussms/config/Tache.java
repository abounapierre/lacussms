/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.config;

import com.abouna.lacussms.entities.Licence;
import com.abouna.lacussms.main.App;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.main.LogFile;
import com.abouna.lacussms.views.tools.Utils;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.abouna.lacussms.views.tools.ConstantUtils.ROOT_LACUS_PATH;

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

    @Autowired
    @Qualifier("jasyptStringEncryptor")
    private StringEncryptor encryptor;

    public Tache() {
    }

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
            String file = ROOT_LACUS_PATH + File.separator + path;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            logBean.setLog(reader.lines().collect(Collectors.joining("\n")));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Scheduled(cron = "*/1 * * * * *")
    public void controlLicence() {
        String message = "La date de validité de la licence est dépassée ou licence non valide. \n" +
                "Veuillez contacter le support technique pour renouveler votre licence.";
        try {
            String original = encryptor.decrypt(getKey());//env.getProperty("application.validDate");
            Date date = Utils.getDateSimpleFormat("ddMMyyHHmmss", original);
            if(date != null && date.before(Utils.getTimeFromInternet())) {
                close(message, null);
            }
        } catch (Exception e) {
            close(message, e);
        }
    }

    private static void close(String message, Exception e) {
        App.stopper();
        logger.error(message, e);
        JOptionPane.showMessageDialog(null, message);
        System.exit(0);
    }

    private String getKey() {
        String keyPath = env.getProperty("application.key.path");
        try {
            String filePath = ROOT_LACUS_PATH + File.separator + keyPath;
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String key = reader.lines().collect(Collectors.joining("\n"));
            String temp = new String(Base64.getDecoder().decode(key));
            temp = temp.replace(temp.substring(0, 1000), "");
            int length = temp.length();
            temp = temp.replace(temp.substring(length - 1000, length), "");
            return temp;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
