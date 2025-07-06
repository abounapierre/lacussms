package com.abouna.lacussms.task;

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
import com.abouna.lacussms.views.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

@Component
public class DataTaskService {
    private final LacusSmsService lacusSmsService;
    private final ServiceEvenement serviceEvenement;
    private final ServiceSalaire serviceSalaire;
    private final ServiceSalaireBKMVTI serviceSalaireBKMVTI;
    private final ServiceCredit serviceCredit;
    private final ServiceMandat serviceMandat;

    @Autowired
    public DataTaskService(LacusSmsService lacusSmsService, ServiceEvenement serviceEvenement, ServiceSalaire serviceSalaire, ServiceSalaireBKMVTI serviceSalaireBKMVTI, ServiceCredit serviceCredit, ServiceMandat serviceMandat) {
        this.lacusSmsService = lacusSmsService;
        this.serviceEvenement = serviceEvenement;
        this.serviceSalaire = serviceSalaire;
        this.serviceSalaireBKMVTI = serviceSalaireBKMVTI;
        this.serviceCredit = serviceCredit;
        this.serviceMandat = serviceMandat;
    }



    public void executeTask() {
        Logger.info("begin service data", DataTaskService.class);
        executeDataBash();
    }

    private void executeDataBash() {
        Config config = new Config(true, true, true, true);
        final String errorDbMessage = "Echec de connexion à la base de données";
        final String errorDateMessage = "Problème de formatage de la date";

        if (config.isEvent()) {
            try {
                serviceEvenement.serviceEvenement();
            } catch (SQLException e) {
                error(errorDbMessage, e);
            } catch (ParseException e) {
                error( errorDateMessage, e);
            } catch (Exception e) {
                error(String.format("Une erreur inattendue s'est produite lors du traitement des événements: %s", e.getMessage()), e);
            }
        }

        if (config.isBkmpai()) {
            try {
                serviceSalaire.serviceSalaire();
                serviceSalaireBKMVTI.serviceSalaireBKMVTI();
            } catch (SQLException e) {
                error( errorDbMessage, e);
            } catch (ParseException e) {
                error( errorDateMessage, e);
            } catch (Exception e) {
                error(String.format("Une erreur inattendue s'est produite lors du traitement des salaires: %s", e.getMessage()), e);
            }
        }

        if (config.isBkmac()) {
            try {
                serviceCredit.serviceCredit();
            } catch (SQLException e) {
                error( errorDbMessage, e);
            } catch (ParseException e) {
                error( errorDateMessage, e);
            } catch (Exception e) {
                error(String.format("Une erreur inattendue s'est produite lors du traitement des crédits: %s", e.getMessage()), e);
            }
        }

        if (config.isMandat()) {
            try {
                serviceMandat.serviceMandat();
            } catch (SQLException e) {
                error( errorDbMessage, e);
            } catch (ParseException e) {
                error( errorDateMessage, e);
            } catch (Exception e) {
                error(String.format("Une erreur inattendue s'est produite lors du traitement des mandats: %s", e.getMessage()), e);
            }
        }
    }

    private static void error(String msgException, Exception e) {
        Logger.error(msgException, e, DataTaskService.class);
        BottomPanel.settextLabel(msgException, Color.RED);
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
