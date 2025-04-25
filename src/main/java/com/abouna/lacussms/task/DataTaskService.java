package com.abouna.lacussms.task;

import com.abouna.lacussms.config.AppRunConfig;
import com.abouna.lacussms.entities.Config;
import com.abouna.lacussms.entities.RemoteDB;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.service.ServiceCredit;
import com.abouna.lacussms.service.ServiceEvenement;
import com.abouna.lacussms.service.ServiceMandat;
import com.abouna.lacussms.service.ServiceSalaire;
import com.abouna.lacussms.service.ServiceSalaireBKMVTI;
import com.abouna.lacussms.views.main.BottomPanel;
import com.abouna.lacussms.views.tools.AES;
import com.abouna.lacussms.views.tools.ConstantUtils;
import com.abouna.lacussms.views.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
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



    @Scheduled(cron = "*/60 * * * * *")
    public void executeTask() {
        logger.info("Scheduled task executed service data: {}", appRunConfig.getDataServiceEnabled());
        if(appRunConfig.getDataServiceEnabled()) {
            executeDataBash();
        }
    }

    private void executeDataBash() {
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
    }

    public void setConnexion(Connection connexion) {
        serviceSalaire.setConn(connexion);
        serviceMandat.setConn(connexion);
        serviceCredit.setConn(connexion);
        serviceSalaireBKMVTI.setConn(connexion);
        serviceEvenement.setConn(connexion);
    }

    public static Connection initConnection(LacusSmsService service, String secret) {
        try {
            Utils.initDriver();
            RemoteDB remoteDB = service.getDefaultRemoteDB(true);
            if(remoteDB == null) {
                return null;
            }
            logger.info("remote{}", remoteDB);
            String decryptedString = AES.decrypt(remoteDB.getPassword(), secret);
            return DriverManager.getConnection(remoteDB.getUrl(), remoteDB.getName(), decryptedString);
        } catch (Exception e) {
            logger.error("Erreur de connexion à la base de données: {}", e.getMessage());
            return null;
        }
    }

    @PostConstruct
    void initConnexion() {
        final  Connection connexion = initConnection(lacusSmsService, ConstantUtils.SECRET_KEY);
        if(connexion != null) {
            setConnexion(connexion);
        } else {
            BottomPanel.settextLabel("Attention la base données du CBS n'est pas connectée veuillez la paramétrer avant de commencer !!!", Color.RED);
        }
    }
}
