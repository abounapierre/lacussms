package com.abouna.lacussms.service;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.entities.BkCompCli;
import com.abouna.lacussms.entities.BkEve;
import com.abouna.lacussms.entities.Groupe;

import java.util.List;

public interface LacusDataService {
    static LacusDataService getInstance() {
        return ApplicationConfig.getApplicationContext().getBean(LacusDataService.class);
    }
    List<BkCli> getAllClients();
    List<Groupe> getAllClientsByGroupe();
    List<BkEve> getAllBkEves();
    List<BkCompCli> getAllBkCompClis();
}
