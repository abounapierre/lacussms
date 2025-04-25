package com.abouna.lacussms.task;

import com.abouna.lacussms.entities.Config;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.service.ServiceCredit;
import com.abouna.lacussms.service.ServiceEvenement;
import com.abouna.lacussms.service.ServiceMandat;
import com.abouna.lacussms.service.ServiceSalaire;
import com.abouna.lacussms.service.ServiceSalaireBKMVTI;
import com.abouna.lacussms.views.main.BottomPanel;
import com.abouna.lacussms.views.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class DataTaskService {
    private static final Logger logger = LoggerFactory.getLogger(DataTaskService.class);
    private final LacusSmsService lacusSmsService;
    private final ServiceEvenement serviceEvenement;
    private final ServiceSalaire serviceSalaire;
    private final ServiceSalaireBKMVTI serviceSalaireBKMVTI;
    private final ServiceCredit serviceCredit;
    private final ServiceMandat serviceMandat;

    public DataTaskService(LacusSmsService lacusSmsService, ServiceEvenement serviceEvenement, ServiceSalaire serviceSalaire, ServiceSalaireBKMVTI serviceSalaireBKMVTI, ServiceCredit serviceCredit, ServiceMandat serviceMandat) {
        this.lacusSmsService = lacusSmsService;
        this.serviceEvenement = serviceEvenement;
        this.serviceSalaire = serviceSalaire;
        this.serviceSalaireBKMVTI = serviceSalaireBKMVTI;
        this.serviceCredit = serviceCredit;
        this.serviceMandat = serviceMandat;
    }

    @Scheduled(cron = "*/1 * * * * *")
    public void executeTask() {
        executeDataBash();
    }

    private void executeDataBash() {
        try {
            Utils.initDriver();
            Config config = lacusSmsService.getAllConfig().get(0);
            AtomicReference<String> msg = new AtomicReference<>();
            logger.info("Démarrage du service de données .....");
            if (config.isEvent()) {
                try {
                    serviceEvenement.serviceEvenement();
                } catch (SQLException e) {
                    msg.set("Echec de connexion à la base de données");
                    logger.error(msg.get());
                    BottomPanel.settextLabel(msg.get(), Color.RED);
                } catch (ParseException e) {
                    msg.set("Problème de formatage de la date");
                    logger.error(msg.get());
                    BottomPanel.settextLabel(msg.get(), Color.RED);
                }
            }

            if (config.isBkmpai()) {
                try {
                    serviceSalaire.serviceSalaire();
                    serviceSalaireBKMVTI.serviceSalaireBKMVTI();
                } catch (SQLException e) {
                    msg.set("Echec de connexion à la base de données");
                    logger.error(msg.get());
                    BottomPanel.settextLabel(msg.get(), Color.RED);
                } catch (ParseException e) {
                    msg.set("Problème de formatage de la date");
                    logger.error(msg.get());
                    BottomPanel.settextLabel(msg.get(), Color.RED);
                }
            }

            if (config.isBkmac()) {
                try {
                    serviceCredit.serviceCredit();
                } catch (SQLException e) {
                    msg.set("Echec de connexion à la base de données");
                    logger.error(msg.get());
                    BottomPanel.settextLabel(msg.get(), Color.RED);
                } catch (ParseException e) {
                    msg.set("Problème de formatage de la date");
                    logger.error(msg.get());
                    BottomPanel.settextLabel(msg.get(), Color.RED);
                }
            }

            if (config.isMandat()) {
                try {
                    serviceMandat.serviceMandat();
                } catch (SQLException e) {
                    msg.set("Echec de connexion à la base de données");
                    logger.error(msg.get());
                    BottomPanel.settextLabel(msg.get(), Color.RED);
                } catch (ParseException e) {
                    msg.set("Problème de formatage de la date");
                    logger.error(msg.get());
                    BottomPanel.settextLabel(msg.get(), Color.RED);
                }
            }
        } catch (ClassNotFoundException e) {
            String msg = "Problème de chargement du pilote";
            logger.error(msg);
            BottomPanel.settextLabel(msg, Color.red);
        }
    }
}
