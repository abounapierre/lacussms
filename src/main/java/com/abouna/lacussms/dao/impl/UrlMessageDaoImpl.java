/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.dao.impl;

import com.abouna.lacussms.dao.generic.GenericDao;
import com.abouna.lacussms.dao.IUrlMessageDao;
import com.abouna.lacussms.entities.UrlMessage;
import org.springframework.stereotype.Repository;

import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 *
 * @author ABOUNA
 */
@Repository
public class UrlMessageDaoImpl extends GenericDao<UrlMessage, Integer> implements IUrlMessageDao{

    @Override
    public UrlMessage getDefaultUrlMessage() {
        try{
        CriteriaBuilder builder =  getManager().getCriteriaBuilder();
        CriteriaQuery<UrlMessage> cq = builder.createQuery(UrlMessage.class);
        Root<UrlMessage> urlRoot = cq.from(UrlMessage.class);
        cq.where(builder.equal(urlRoot.get("defaultUrl"), true));
        cq.select(urlRoot);
        return getManager().createQuery(cq).getSingleResult();
        }catch(NoResultException ex){
           return null;
        }
        
    }
    
}
