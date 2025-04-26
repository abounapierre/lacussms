package com.abouna.lacussms.task;

import com.abouna.lacussms.config.AppRunConfig;
import com.abouna.lacussms.entities.Config;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.service.ServiceCredit;
import com.abouna.lacussms.service.ServiceEvenement;
import com.abouna.lacussms.service.ServiceMandat;
import com.abouna.lacussms.service.ServiceSalaire;
import com.abouna.lacussms.service.ServiceSalaireBKMVTI;
import com.abouna.lacussms.views.main.BottomPanel;
import com.abouna.lacussms.views.tools.ConstantUtils;
import com.abouna.lacussms.views.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.sql.Connection;
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
    private final AppRunConfig appRunConfig;

    public DataTaskService(LacusSmsService lacusSmsService, ServiceEvenement serviceEvenement, ServiceSalaire serviceSalaire, ServiceSalaireBKMVTI serviceSalaireBKMVTI, ServiceCredit serviceCredit, ServiceMandat serviceMandat, AppRunConfig appRunConfig) {
        this.lacusSmsService = lacusSmsService;
        this.serviceEvenement = serviceEvenement;
        this.serviceSalaire = serviceSalaire;
        this.serviceSalaireBKMVTI = serviceSalaireBKMVTI;
        this.serviceCredit = serviceCredit;
        this.serviceMandat = serviceMandat;
        this.appRunConfig = appRunConfig;
    }



    //@Scheduled(fixedDelay = 60000)
    public void executeTask() {
        //logger.info("Démarrage du service de données .....");
        if(appRunConfig.getDataServiceEnabled()) {
            executeDataBash();
        }
    }

    private void executeDataBash() {
        Config config = new Config(true, true, true, true);//lacusSmsService.getAllConfig().get(0);
        AtomicReference<String> msg = new AtomicReference<>();
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
    }

    public void setConnexion(Connection connexion) {
        serviceSalaire.setConn(connexion);
        serviceMandat.setConn(connexion);
        serviceCredit.setConn(connexion);
        serviceSalaireBKMVTI.setConn(connexion);
        serviceEvenement.setConn(connexion);
    }


    @PostConstruct
    void initConnexion() {
        if (Utils.testConnexion(lacusSmsService, ConstantUtils.SECRET_KEY) == null) {
            BottomPanel.settextLabel("Attention la base données du CBS n'est pas connectée veuillez la paramétrer avant de commencer !!!", Color.RED);
        }
    }
}
