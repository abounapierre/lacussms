/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.dao.impl;

import com.abouna.generic.dao.impl.GenericDao;
import com.abouna.lacussms.dao.IMessageMandatDao;
import com.abouna.lacussms.entities.MessageMandat;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author SATELLITE
 */
@Repository
public class MessageMandatDaoImpl extends GenericDao<MessageMandat, Integer> implements IMessageMandatDao{

    @Override
    public List<MessageMandat> getMessageFromPeriode(Date d1, Date d2) {
        CriteriaBuilder builder =  getManager().getCriteriaBuilder();
        CriteriaQuery<MessageMandat> cq = builder.createQuery(MessageMandat.class);
        Root<MessageMandat> msgRoot = cq.from(MessageMandat.class);
        cq.where(builder.and(builder.greaterThanOrEqualTo(msgRoot.get("sendDate"), d1),
                builder.lessThanOrEqualTo(msgRoot.get("sendDate"), d2)));
        cq.select(msgRoot);
        return getManager().createQuery(cq).getResultList();
    }

    @Override
    public int supprimerTout() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<MessageMandat> getMessageMandatByNumEve(String eve) {
        CriteriaBuilder builder =  getManager().getCriteriaBuilder();
        CriteriaQuery<MessageMandat> cq = builder.createQuery(MessageMandat.class);
        Root<MessageMandat> msgRoot = cq.from(MessageMandat.class);
        cq.where(builder.equal(msgRoot.get("numEve"), eve));
        cq.select(msgRoot);
        return getManager().createQuery(cq).getResultList().stream().findFirst();
    }

}
