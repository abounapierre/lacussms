package com.abouna.lacussms.dao.generic;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class GenericDao<T extends Serializable, id> implements IDao<T, id> {
    @PersistenceContext
    protected EntityManager em;
    private Class<T> type;

    public GenericDao() {
        Type t = this.getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType)t;
        this.type = (Class)pt.getActualTypeArguments()[0];
    }

    public T create(T t) throws DataAccessException {
        this.em.persist(t);
        return t;
    }

    public void delete(Object id) throws DataAccessException {
        this.em.remove(id);
    }

    public T findById(Object id) {
        return (T) this.em.find(this.type, id);
    }

    public T update(T t) throws DataAccessException {
        return (T) this.em.merge(t);
    }

    public List<T> findAll() {
        return this.em.createQuery("from " + this.type.getName()).getResultList();
    }

    public EntityManager getManager() {
        return this.em;
    }

    public id maxId(T t) {
        return (id) this.em.createQuery("select max(id) from " + this.type.getName()).getSingleResult();
    }

    public Integer count(T t) throws DataAccessException {
        return (Integer)this.em.createQuery("select count(*) from " + this.type.getName()).getSingleResult();
    }

    public void purge() {
        this.em.createQuery("delete from " + this.type.getName()).executeUpdate();
    }

    public int deleteAll() {
        CriteriaBuilder builder = this.em.getCriteriaBuilder();
        CriteriaDelete<T> cq = builder.createCriteriaDelete(this.type);
        cq.from(this.type);
        return this.em.createQuery(cq).executeUpdate();
    }
}
