package com.abouna.lacussms.dao;

import com.abouna.generic.dao.IDao;
import com.abouna.lacussms.entities.GroupeClient;
import com.abouna.lacussms.entities.GroupeClientPK;

import java.util.List;

public interface GroupeClientDao extends IDao<GroupeClient, GroupeClientPK> {
    List<GroupeClient> findByGroupeId(Integer id);
    int deleteGroupeClientByGroupeId(Integer id);
}
