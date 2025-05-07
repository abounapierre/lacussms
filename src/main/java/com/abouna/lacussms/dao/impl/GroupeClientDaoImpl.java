package com.abouna.lacussms.dao.impl;

import com.abouna.generic.dao.impl.GenericDao;
import com.abouna.lacussms.dao.GroupeClientDao;
import com.abouna.lacussms.entities.GroupeClient;
import com.abouna.lacussms.entities.GroupeClientPK;
import org.springframework.stereotype.Repository;

@Repository
public class GroupeClientDaoImpl extends GenericDao<GroupeClient, GroupeClientPK> implements GroupeClientDao {
}
