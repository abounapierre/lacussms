/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.dao.impl;

import com.abouna.generic.dao.DataAccessException;
import com.abouna.generic.dao.impl.GenericDao;
import com.abouna.lacussms.dao.IBkCompCliDao;
import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.entities.BkCompCli;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 *
 * @author SATELLITE
 */
@Repository
public class BkCompCliDaoImpl extends GenericDao<BkCompCli, String> implements IBkCompCliDao{

    @Override
    public List<BkCompCli> getBkCompCliByCli(BkCli cli) throws DataAccessException {
        try{
        CriteriaBuilder builder =  getManager().getCriteriaBuilder();
        CriteriaQuery<BkCompCli> cq = builder.createQuery(BkCompCli.class);
        Root<BkCompCli> bkComCliRoot = cq.from(BkCompCli.class);
        cq.where(builder.equal(bkComCliRoot.get("cli"), cli));
        cq.select(bkComCliRoot);
        return getManager().createQuery(cq).getResultList();
        }catch(NoResultException ex){
           return null;
        }
    }

    @Override
    public List<BkCompCli> getBkCompCliByCli(BkCli cli, boolean actif) throws DataAccessException {
        try{
        CriteriaBuilder builder =  getManager().getCriteriaBuilder();
        CriteriaQuery<BkCompCli> cq = builder.createQuery(BkCompCli.class);
        Root<BkCompCli> bkComCliRoot = cq.from(BkCompCli.class);
        cq.where(builder.and(builder.equal(bkComCliRoot.get("cli"), cli),
                builder.equal(bkComCliRoot.get("enabled"), actif)));
        cq.select(bkComCliRoot);
        return getManager().createQuery(cq).getResultList();
        }catch(NoResultException ex){
           return null;
        }
    }

    @Override
    public List<BkCompCli> getBkCompCliByCli(String compte) throws DataAccessException {
       CriteriaBuilder cb = getManager().getCriteriaBuilder();
        CriteriaQuery<BkCompCli> cq = cb.createQuery(BkCompCli.class);
        Root<BkCompCli> bkCompCliRoot = cq.from(BkCompCli.class);
        cq.where(cb.or(cb.like(bkCompCliRoot.get("numc"),"%"+compte+"%"),
                cb.like(bkCompCliRoot.get("cli").get("nom"),"%"+compte+"%"),
                cb.like(bkCompCliRoot.get("cli").get("prenom"),"%"+compte+"%")));
        return getManager().createQuery(cq).getResultList();
    }

    @Override
    public List<BkCompCli> getBkCompCliLimit(int limit) throws DataAccessException {
       CriteriaBuilder cb = getManager().getCriteriaBuilder();
        CriteriaQuery<BkCompCli> cq = cb.createQuery(BkCompCli.class);
        Root<BkCompCli> bkCompCliRoot = cq.from(BkCompCli.class);
        cq.select(bkCompCliRoot);
        return getManager().createQuery(cq).setMaxResults(limit).getResultList();
    }

    @Override
    public List<BkCompCli> getBkCompCliByCli(BkCli cli, String compte, boolean actif) throws DataAccessException {
        CriteriaBuilder builder =  getManager().getCriteriaBuilder();
        CriteriaQuery<BkCompCli> cq = builder.createQuery(BkCompCli.class);
        Root<BkCompCli> bkComCliRoot = cq.from(BkCompCli.class);
        cq.where(builder.and(builder.equal(bkComCliRoot.get("cli"), cli),
                builder.equal(bkComCliRoot.get("numc"), compte),
                builder.equal(bkComCliRoot.get("enabled"), actif)));
        cq.select(bkComCliRoot);
        return getManager().createQuery(cq).getResultList();
    }

    @Override
    public BkCompCli getBkCompCliByCriteria(BkCli cli, String compte, boolean actif) {
        try {
            CriteriaBuilder builder =  getManager().getCriteriaBuilder();
        CriteriaQuery<BkCompCli> cq = builder.createQuery(BkCompCli.class);
        Root<BkCompCli> bkComCliRoot = cq.from(BkCompCli.class);
        cq.where(builder.and(builder.equal(bkComCliRoot.get("cli"), cli),
                builder.equal(bkComCliRoot.get("numc"), compte),
                builder.equal(bkComCliRoot.get("enabled"), actif)));
        cq.select(bkComCliRoot);
        return getManager().createQuery(cq).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public BkCli getBkCli(String numc) throws DataAccessException {
        try {
            CriteriaBuilder builder = getManager().getCriteriaBuilder();
            CriteriaQuery<BkCompCli> cq = builder.createQuery(BkCompCli.class);
            Root<BkCompCli> bkComCliRoot = cq.from(BkCompCli.class);
            cq.where(builder.equal(bkComCliRoot.get("numc"), numc));
            cq.select(bkComCliRoot);
            BkCompCli compte = getManager().createQuery(cq).getSingleResult();
            return compte.getCli();
        } catch (NoResultException e) {
            return null;
        }
    }
    
}
