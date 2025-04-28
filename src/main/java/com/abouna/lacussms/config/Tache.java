/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.config;

import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.task.StartService;
import com.abouna.lacussms.views.main.LogFile;
import com.abouna.lacussms.views.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author SATELLITE
 */
@Component
public class Tache {

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

}
