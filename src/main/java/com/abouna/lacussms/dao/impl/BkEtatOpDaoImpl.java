/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.dao.impl;

import com.abouna.lacussms.dao.generic.GenericDao;
import com.abouna.lacussms.dao.IBkEtatOpDao;
import com.abouna.lacussms.entities.BkEtatOp;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

/**
 *
 * @author ABOUNA
 */
@Repository
public class BkEtatOpDaoImpl extends GenericDao<BkEtatOp, Integer> implements IBkEtatOpDao{

    @Override
    public List<BkEtatOp> getListBkEtatOp(boolean actif) {
        CriteriaBuilder builder =  getManager().getCriteriaBuilder();
        CriteriaQuery<BkEtatOp> cq = builder.createQuery(BkEtatOp.class);
        Root<BkEtatOp> etatRoot = cq.from(BkEtatOp.class);
        cq.where(builder.equal(etatRoot.get("actif"), actif));
        cq.select(etatRoot);
        return getManager().createQuery(cq).getResultList();
    }
    
}
