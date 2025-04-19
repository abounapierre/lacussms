package com.abouna.lacussms.dao.custom.impl;

import com.abouna.lacussms.entities.BkCli;

import java.util.List;

public interface BkCliDaoCustom {
    List<BkCli> getBkCliByCriteria(String code);
    List<BkCli> getBkCliLimit(int limit);
    List<BkCli> getBkCliByNumCompte(String code);
    BkCli getBkCliByNumero(Long numero);
}
