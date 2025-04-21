package com.abouna.lacussms.service.impl;

import com.abouna.lacussms.dao.IBkCliDao;
import com.abouna.lacussms.dao.IBkCompCliDao;
import com.abouna.lacussms.dto.AccountCSVModel;
import com.abouna.lacussms.dto.FailImportDataModelDTO;
import com.abouna.lacussms.dto.ResultImportDataModelDTO;
import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.entities.BkCompCli;
import com.abouna.lacussms.service.LacusImportDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.abouna.lacussms.views.tools.ConstantUtils.ACCOUNT_NUMBER_LENGTH;

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
    public ResultImportDataModelDTO importAccountData(List<AccountCSVModel> models) {
        ResultImportDataModelDTO resultImportDataModelDTO = new ResultImportDataModelDTO();
        int line = 0;
        try {
            for(AccountCSVModel model : models) {
                if (model.getCol1() == null || model.getCol2() == null || model.getCol3() == null || model.getCol4() == null || model.getCol5() == null || model.getCol6() == null) {
                    resultImportDataModelDTO.getErrors().add(new FailImportDataModelDTO("Invalid data at line " + line, line));
                    continue;
                }
                BkCli bkCli = new BkCli();
                BkCompCli bkCompCli = new BkCompCli();
                bkCli.setNom(model.getCol1());
                bkCli.setPrenom(model.getCol1().toUpperCase());
                String id;
                String compte = model.getCol2().replace(" ", "").replace(".", "").replace(";", "").replace(",", "");
                id = compte.length() >= ACCOUNT_NUMBER_LENGTH ? compte.substring(3, ACCOUNT_NUMBER_LENGTH) : compte;
                bkCli.setCode(id);
                long num = 0L;
                String numStr = model.getCol3();
                if (numStr.length() == ACCOUNT_NUMBER_LENGTH) {
                    num = Long.parseLong("237" + numStr);
                } else if (model.getCol3().length() == 8) {
                    num = Long.parseLong("241" + numStr);
                }
                bkCli.setEmail(model.getCol4());
                bkCli.setPhone(num);
                bkCli.setLangue(model.getCol5());
                bkCli.setEnabled(true);
                bkCli.setLibelle(model.getCol6());
                if (bkCliDao.findById(id) == null) {
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
