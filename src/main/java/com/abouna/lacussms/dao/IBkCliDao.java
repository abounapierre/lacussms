/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.dao;

import com.abouna.generic.dao.IDao;
import com.abouna.lacussms.entities.BkCli;

import java.util.List;

/**
 *
 * @author SATELLITE
 */
public interface IBkCliDao extends IDao<BkCli, String>{
    List<BkCli> getBkCliByCriteria(String code);
    List<BkCli> getBkCliLimit(int limit);
    List<BkCli> getBkCliByNumCompte(String code);
    BkCli getBkCliByNumero(Long numero);
}
