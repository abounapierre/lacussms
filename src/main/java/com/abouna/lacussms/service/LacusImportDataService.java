package com.abouna.lacussms.service;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.dto.ClientCSV;
import com.abouna.lacussms.dto.ResultImportDataModelDTO;

import java.util.List;

public interface LacusImportDataService {
    static LacusImportDataService getInstance() {
        return ApplicationConfig.getApplicationContext().getBean(LacusImportDataService.class);
    }
    ResultImportDataModelDTO importAccountData(List<ClientCSV> models);
}
