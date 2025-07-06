/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.dao;

import com.abouna.generic.dao.DataAccessException;
import com.abouna.generic.dao.IDao;
import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.entities.BkCompCli;

import java.util.List;

/**
 *
 * @author SATELLITE
 */
public interface IBkCompCliDao extends IDao<BkCompCli, String>{
    List<BkCompCli> getBkCompCliByCli(BkCli cli) throws DataAccessException;
    List<BkCompCli> getBkCompCliByCli(BkCli cli, boolean actif) throws DataAccessException;
    List<BkCompCli> getBkCompCliByCli(BkCli cli, String compte, boolean actif) throws DataAccessException;
    BkCompCli getBkCompCliByCriteria(BkCli cli, String compte, boolean actif);
    List<BkCompCli> getBkCompCliByCli(String compte) throws DataAccessException;
    List<BkCompCli> getBkCompCliLimit(int limit) throws DataAccessException;
    BkCli getBkCli(String numc) throws DataAccessException;
}
