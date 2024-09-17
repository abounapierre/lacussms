/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.dao;

import com.abouna.generic.dao.IDao;
import com.abouna.lacussms.entities.ParametreRequete;
import com.abouna.lacussms.entities.TypeService;

import java.util.List;

/**
 *
 * @author SATELLITE
 */
public interface ParametreRequeteDao extends IDao<ParametreRequete, Long>{
    public List<ParametreRequete> getParametersByService(TypeService typeService);
    public List<ParametreRequete> getParametersByService(String code,TypeService typeService);
}
