package com.abouna.lacussms.service.impl;

import com.abouna.lacussms.dao.IBkCliDao;
import com.abouna.lacussms.dao.IBkCompCliDao;
import com.abouna.lacussms.dto.ClientCSV;
import com.abouna.lacussms.dto.FailImportDataModelDTO;
import com.abouna.lacussms.dto.ResultImportDataModelDTO;
import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.entities.BkCompCli;
import com.abouna.lacussms.service.LacusImportDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LacusImportDataServiceImpl implements LacusImportDataService {
    private final IBkCliDao bkCliDao;
    private final IBkCompCliDao bkCompCliDao;

    @Autowired
    public LacusImportDataServiceImpl(IBkCliDao bkCliDao, IBkCompCliDao bkCompCliDao) {
        this.bkCliDao = bkCliDao;
        this.bkCompCliDao = bkCompCliDao;
    }


    @Override
    public ResultImportDataModelDTO importAccountData(List<ClientCSV> models) {
        ResultImportDataModelDTO resultImportDataModelDTO = new ResultImportDataModelDTO();
        int line = 0;
        try {
            for(ClientCSV model : models) {
                BkCli bkCli = new BkCli();
                BkCompCli bkCompCli = new BkCompCli();
                bkCli.setCode(model.getCode());
                bkCli.setNom(model.getNom());
                bkCli.setPrenom(model.getPrenom().toUpperCase());
                String compte = model.getCompte().replace(" ", "").replace(".", "").replace(";", "").replace(",", "");
                String numStr = model.getTelephone();
                bkCli.setPhone(Long.parseLong(numStr.replace("+", "").replace(" ", "").replace(".", "").replace(";", "").replace(",", "")));
                bkCli.setEmail("");
                bkCli.setLangue(model.getLangue());
                bkCli.setEnabled(true);
                bkCli.setLibelle(model.getCivilite());
                if (bkCliDao.findById(bkCli.getCode()) == null) {
                    bkCliDao.create(bkCli);
                }
                bkCompCli.setCli(bkCli);
                bkCompCli.setNumc(compte.toUpperCase());
                bkCompCli.setEnabled(true);
                if (bkCompCliDao.findById(compte) == null) {
                    bkCompCliDao.create(bkCompCli);
                }
                ++line;
            }
        } catch (Exception e) {
            resultImportDataModelDTO.getErrors().add(new FailImportDataModelDTO(e.getMessage(), line));
        }
        return resultImportDataModelDTO;
    }
}
