/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.dao.impl;

import com.abouna.generic.dao.DataAccessException;
import com.abouna.generic.dao.impl.GenericDao;
import com.abouna.lacussms.dao.IBkMadDao;
import com.abouna.lacussms.entities.BkMad;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SATELLITE
 */
@Repository
public class BkMadDaoImpl extends GenericDao<BkMad, Integer> implements IBkMadDao{

    @Override
    public List<BkMad> getBkMadsByPeriode(Date d1, Date d2) {
        CriteriaBuilder builder =  getManager().getCriteriaBuilder();
        CriteriaQuery<BkMad> cq = builder.createQuery(BkMad.class);
        Root<BkMad> bkEveRoot = cq.from(BkMad.class);
        cq.where(builder.and(builder.greaterThanOrEqualTo(bkEveRoot.get("dateEnvoie"), d1),
                builder.lessThanOrEqualTo(bkEveRoot.get("dateEnvoie"), d2)));
        cq.select(bkEveRoot);
        return getManager().createQuery(cq).getResultList();
    }

    @Override
    public int supprimerBkMad(Date d1, Date d2) {
         int i =  0;
        for(BkMad eve : getBkMadsByPeriode(d1, d2)){
            try {
                delete(findById(eve.getId()));
                i++;
            } catch (DataAccessException ex) {
                Logger.getLogger(BkMadDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return i;
    }

    @Override
    public List<BkMad> getbkMadsByCriteria(String val) {
        CriteriaBuilder cb = getManager().getCriteriaBuilder();
        CriteriaQuery<BkMad> cq = cb.createQuery(BkMad.class);
        Root<BkMad> bkEveRoot = cq.from(BkMad.class);
        cq.where(cb.or(cb.like(bkEveRoot.get("ad1p"),"%"+val+"%"),
                cb.like(bkEveRoot.get("ad2p"),"%"+val+"%"),
                cb.like(bkEveRoot.get("age").get("noma"),"%"+val+"%"),
                cb.like(bkEveRoot.get("eve"),"%"+val+"%"),
                cb.like(bkEveRoot.get("dco"),"%"+val+"%"),
                cb.like(bkEveRoot.get("dbd"),"%"+val+"%"),
                cb.like(bkEveRoot.get("ope").get("ope"),"%"+val+"%"),
                cb.like(bkEveRoot.get("ope").get("lib"),"%"+val+"%")));
        return getManager().createQuery(cq).getResultList();
    }

    @Override
    public BkMad getBkMadByCriteria(String num, String ad1p, Date date) {
        try {
            CriteriaBuilder builder =  getManager().getCriteriaBuilder();
        CriteriaQuery<BkMad> cq = builder.createQuery(BkMad.class);
        Root<BkMad> bkEveRoot = cq.from(BkMad.class);
        cq.where(builder.and(builder.equal(bkEveRoot.get("eve"), num),
                builder.equal(bkEveRoot.get("dateEnvoie"), date),
                builder.equal(bkEveRoot.get("ad1p"), ad1p)));
        cq.select(bkEveRoot);
        return getManager().createQuery(cq).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Integer getMaxBkMad() {
        CriteriaBuilder builder =  getManager().getCriteriaBuilder();
        CriteriaQuery<Number> cq = builder.createQuery(Number.class);
        Root<BkMad> bkEveRoot = cq.from(BkMad.class);
        cq.select(builder.max(bkEveRoot.get("id")));
        return (Integer) getManager().createQuery(cq).getSingleResult();
    }

    @Override
    public void supprimerAll() {
        
    }

    @Override
    public BkMad getBkMadByClesec(String num) {
       try {
        CriteriaBuilder builder =  getManager().getCriteriaBuilder();
        CriteriaQuery<BkMad> cq = builder.createQuery(BkMad.class);
        Root<BkMad> bkEveRoot = cq.from(BkMad.class);
        cq.where(builder.equal(bkEveRoot.get("clesec"), num));
        cq.select(bkEveRoot);
        return getManager().createQuery(cq).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<BkMad> getBkMadByTraite(int traite) {
        CriteriaBuilder builder =  getManager().getCriteriaBuilder();
        CriteriaQuery<BkMad> cq = builder.createQuery(BkMad.class);
        Root<BkMad> bkEveRoot = cq.from(BkMad.class);
        cq.where(builder.equal(bkEveRoot.get("traite"), traite));
        cq.select(bkEveRoot);
        return getManager().createQuery(cq).getResultList();
    }

    @Override
    public List<BkMad> getBkMadByTraite() {
        CriteriaBuilder builder =  getManager().getCriteriaBuilder();
        CriteriaQuery<BkMad> cq = builder.createQuery(BkMad.class);
        Root<BkMad> bkEveRoot = cq.from(BkMad.class);
        cq.where(builder.or(builder.equal(bkEveRoot.get("traite"), 0)
                ,builder.equal(bkEveRoot.get("traite"), 1)));
        cq.select(bkEveRoot);
        return getManager().createQuery(cq).getResultList();
    }

    @Override
    public List<BkMad> getBkMadByLimit(int limit) {
       CriteriaBuilder builder =  getManager().getCriteriaBuilder();
        CriteriaQuery<BkMad> cq = builder.createQuery(BkMad.class);
        Root<BkMad> bkMadRoot = cq.from(BkMad.class);
        cq.select(bkMadRoot);
        cq.orderBy(builder.desc(bkMadRoot.get("id")));
        return getManager().createQuery(cq).setMaxResults(limit).getResultList();
    }

    @Override
    public List<BkMad> getBkMadByDate(Date date) {
        CriteriaBuilder builder =  getManager().getCriteriaBuilder();
        CriteriaQuery<BkMad> cq = builder.createQuery(BkMad.class);
        Root<BkMad> bkMadRoot = cq.from(BkMad.class);
        cq.where(builder.equal(bkMadRoot.get("creationDate"), date));
        cq.select(bkMadRoot);
        cq.orderBy(builder.desc(bkMadRoot.get("id")));
        return getManager().createQuery(cq).getResultList();
    }

}
