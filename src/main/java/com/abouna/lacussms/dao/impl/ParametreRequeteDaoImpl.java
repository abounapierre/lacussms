/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.dao.impl;

import com.abouna.lacussms.dao.generic.GenericDao;
import com.abouna.lacussms.dao.ParametreRequeteDao;
import com.abouna.lacussms.entities.ParametreRequete;
import com.abouna.lacussms.entities.TypeService;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author SATELLITE
 */
@Repository
public class ParametreRequeteDaoImpl extends GenericDao<ParametreRequete, Long> implements ParametreRequeteDao{

    @Override
    public List<ParametreRequete> getParametersByService(TypeService typeService) {
        String query = "SELECT p FROM ParametreRequete p WHERE p.service=:service";
        return getManager().createQuery(query).setParameter("service", typeService).getResultList();
    }

    @Override
    public List<ParametreRequete> getParametersByService(String code, TypeService typeService) {
        String query = "SELECT p FROM ParametreRequete p WHERE p.service=:service AND p.code=:code";
        return getManager().createQuery(query).setParameter("service", typeService)
                .setParameter("code", code).getResultList();
    }
    
}
