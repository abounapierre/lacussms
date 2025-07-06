/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.dao;

import com.abouna.generic.dao.IDao;
import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.entities.BkTelCli;

import java.util.List;

/**
 *
 * @author ABOUNA
 */
public interface IBkTelCliDao extends IDao<BkTelCli,Integer>{
    List<BkTelCli> getListBkTelByCli(BkCli b);
    BkTelCli getBkTelCliDefault(BkCli b, boolean d);

    List<BkTelCli> getListBkTelByCli();

}
