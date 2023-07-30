package com.abouna.lacussms.dao.generic;

import java.io.Serializable;
import java.util.List;

public interface IDao<T extends Serializable, id> {
    T create(T var1) throws DataAccessException;

    void delete(Object var1) throws DataAccessException;

    T findById(Object var1) throws DataAccessException;

    T update(T var1) throws DataAccessException;

    List<T> findAll() throws DataAccessException;

    id maxId(T var1) throws DataAccessException;

    Integer count(T var1) throws DataAccessException;

    int deleteAll() throws DataAccessException;

    void purge() throws DataAccessException;
}
