package com.abouna.lacussms.dao.impl;

import com.abouna.generic.dao.impl.GenericDao;
import com.abouna.lacussms.dao.IBkCliDao;
import com.abouna.lacussms.entities.BkCli;
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
public class BkCliDaoImpl extends GenericDao<BkCli, String> implements IBkCliDao {

    @Override
    public List<BkCli> getBkCliByCriteria(String code) {
        CriteriaBuilder cb = getManager().getCriteriaBuilder();
        CriteriaQuery<BkCli> cq = cb.createQuery(BkCli.class);
        Root<BkCli> bkCliRoot = cq.from(BkCli.class);
        cq.where(cb.or(cb.like(bkCliRoot.get("code"), "%" + code + "%"),
                cb.like(bkCliRoot.get("nom"), "%" + code + "%"),
                cb.like(bkCliRoot.get("prenom"), "%" + code + "%")));
        return getManager().createQuery(cq).getResultList();
    }

    @Override
    public List<BkCli> getBkCliLimit(int limit) {
        CriteriaBuilder cb = getManager().getCriteriaBuilder();
        CriteriaQuery<BkCli> cq = cb.createQuery(BkCli.class);
        Root<BkCli> bkCliRoot = cq.from(BkCli.class);
        cq.select(bkCliRoot);
        return getManager().createQuery(cq).setMaxResults(limit).getResultList();
    }

    @Override
    public List<BkCli> getBkCliByNumCompte(String code) {
        CriteriaBuilder cb = getManager().getCriteriaBuilder();
        CriteriaQuery<BkCli> cq = cb.createQuery(BkCli.class);
        Root<BkCli> bkCliRoot = cq.from(BkCli.class);
        cq.where(cb.or(cb.like(bkCliRoot.get("code"),"%" + code + "%"),
        cb.like(bkCliRoot.get("nom"),"%" + code + "%"),
        cb.like(bkCliRoot.get("prenom"),"%" + code + "%")));
        return getManager().createQuery(cq).getResultList();
    }

    @Override
    public BkCli getBkCliByNumero(Long numero) {
        try {
            CriteriaBuilder cb = getManager().getCriteriaBuilder();
            CriteriaQuery<BkCli> cq = cb.createQuery(BkCli.class);
            Root<BkCli> bkCliRoot = cq.from(BkCli.class);
            cq.where(cb.or(cb.equal(bkCliRoot.get("phone"), numero)));
            cq.select(bkCliRoot);
            return getManager().createQuery(cq).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
