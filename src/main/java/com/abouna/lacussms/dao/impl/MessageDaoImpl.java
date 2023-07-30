/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.dao.impl;

import com.abouna.lacussms.dao.generic.GenericDao;
import com.abouna.lacussms.dao.IMessageDao;
import com.abouna.lacussms.entities.Message;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

/**
 *
 * @author SATELLITE
 */
@Repository
public class MessageDaoImpl extends GenericDao<Message, Integer> implements IMessageDao{

    @Override
    public List<Message> getMessageFromPeriode(Date d1, Date d2) {
         CriteriaBuilder builder =  getManager().getCriteriaBuilder();
        CriteriaQuery<Message> cq = builder.createQuery(Message.class);
        Root<Message> msgRoot = cq.from(Message.class);
        cq.where(builder.and(builder.greaterThanOrEqualTo(msgRoot.get("sendDate"), d1),
                builder.lessThanOrEqualTo(msgRoot.get("sendDate"), d2)));
        cq.select(msgRoot);
        return getManager().createQuery(cq).getResultList();
    }

    @Override
    public int supprimerTout() {
        CriteriaBuilder builder = getManager().getCriteriaBuilder();
        CriteriaDelete<Message> cq = builder.createCriteriaDelete(Message.class);
        Root<Message> root = cq.from(Message.class);
        int result = em.createQuery(cq).executeUpdate();
        return result;
    }
    
}
