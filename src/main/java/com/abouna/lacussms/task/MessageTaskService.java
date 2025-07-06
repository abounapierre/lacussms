package com.abouna.lacussms.task;

import com.abouna.lacussms.entities.Config;
import com.abouna.lacussms.service.ServiceCredit;
import com.abouna.lacussms.service.ServiceEvenement;
import com.abouna.lacussms.service.ServiceMandat;
import com.abouna.lacussms.service.ServiceSalaire;
import org.springframework.stereotype.Component;

@Component
public class MessageTaskService {
    private final ServiceEvenement serviceEvenement;
    private final ServiceSalaire serviceSalaire;
    private final ServiceCredit serviceCredit;
    private final ServiceMandat serviceMandat;

    public MessageTaskService(ServiceEvenement serviceEvenement, ServiceSalaire serviceSalaire, ServiceCredit serviceCredit, ServiceMandat serviceMandat) {
        this.serviceEvenement = serviceEvenement;
        this.serviceSalaire = serviceSalaire;
        this.serviceCredit = serviceCredit;
        this.serviceMandat = serviceMandat;
    }


    public void executeTask() {
        com.abouna.lacussms.views.utils.Logger.info("begin service message", MessageTaskService.class);
        executeMessageBash();
    }

    private void executeMessageBash() {
        Config config = new Config(true, true, true, true);
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
