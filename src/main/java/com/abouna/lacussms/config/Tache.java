/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.config;

import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.main.LogFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

    public Tache() {

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

}
