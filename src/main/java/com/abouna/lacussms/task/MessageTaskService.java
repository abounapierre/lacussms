package com.abouna.lacussms.task;

import com.abouna.lacussms.entities.Config;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.service.ServiceCredit;
import com.abouna.lacussms.service.ServiceEvenement;
import com.abouna.lacussms.service.ServiceMandat;
import com.abouna.lacussms.service.ServiceSalaire;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MessageTaskService {
    private static final Logger logger = LoggerFactory.getLogger(DataTaskService.class);
    private final LacusSmsService lacusSmsService;
    private final ServiceEvenement serviceEvenement;
    private final ServiceSalaire serviceSalaire;
    private final ServiceCredit serviceCredit;
    private final ServiceMandat serviceMandat;

    public MessageTaskService(LacusSmsService lacusSmsService, ServiceEvenement serviceEvenement, ServiceSalaire serviceSalaire, ServiceCredit serviceCredit, ServiceMandat serviceMandat) {
        this.lacusSmsService = lacusSmsService;
        this.serviceEvenement = serviceEvenement;
        this.serviceSalaire = serviceSalaire;
        this.serviceCredit = serviceCredit;
        this.serviceMandat = serviceMandat;
    }


    @Scheduled(cron = "*/1 * * * * *")
    public void executeTask() {
        executeMessageBash();
    }

    private void executeMessageBash() {
        Config config = lacusSmsService.getAllConfig().get(0);
        logger.info("Démarrage du service sms ...");
        if (config.isEvent()) {
            serviceEvenement.envoieSMSEvenement();
        }

        if (config.isBkmpai()) {
            serviceSalaire.envoieSMSSalaire();
        }

        if (config.isBkmac()) {
            serviceCredit.envoieSMSCredit();
        }

        if (config.isMandat()) {
            serviceMandat.envoieSMSMandat();
        }
    }
}
