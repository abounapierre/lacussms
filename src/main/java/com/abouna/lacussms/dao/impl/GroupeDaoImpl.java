package com.abouna.lacussms.dao.impl;

import com.abouna.generic.dao.impl.GenericDao;
import com.abouna.lacussms.dao.GroupeDao;
import com.abouna.lacussms.entities.Groupe;
import org.springframework.stereotype.Repository;

@Repository
public class GroupeDaoImpl extends GenericDao<Groupe, Integer> implements GroupeDao {
}
