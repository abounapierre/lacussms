package com.abouna.lacussms.dao.impl;

import com.abouna.generic.dao.impl.GenericDao;
import com.abouna.lacussms.dao.GroupeClientDao;
import com.abouna.lacussms.entities.GroupeClient;
import com.abouna.lacussms.entities.GroupeClientPK;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class GroupeClientDaoImpl extends GenericDao<GroupeClient, GroupeClientPK> implements GroupeClientDao {

    @Override
    public List<GroupeClient> findByGroupeId(Integer idGroupe) {
        CriteriaBuilder builder =  getManager().getCriteriaBuilder();
        CriteriaQuery<GroupeClient> cq = builder.createQuery(GroupeClient.class);
        Root<GroupeClient> msgRoot = cq.from(GroupeClient.class);
        cq.where(builder.and(builder.equal(msgRoot.get("id").get("idGroupe"), idGroupe)));
        cq.select(msgRoot);
        return getManager().createQuery(cq).getResultList();
    }

    @Override
    public int deleteGroupeClientByGroupeId(Integer id) {
        CriteriaBuilder builder =  getManager().getCriteriaBuilder();
        CriteriaDelete<GroupeClient> cq = builder.createCriteriaDelete(GroupeClient.class);
        Root<GroupeClient> msgRoot = cq.from(GroupeClient.class);
        cq.where(builder.and(builder.equal(msgRoot.get("id").get("idGroupe"), id)));
        return getManager().createQuery(cq).executeUpdate();
    }
}
