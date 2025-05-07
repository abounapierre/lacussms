package com.abouna.lacussms.service.impl;

import com.abouna.generic.dao.DataAccessException;
import com.abouna.lacussms.dao.GroupeClientDao;
import com.abouna.lacussms.dao.GroupeDao;
import com.abouna.lacussms.entities.Groupe;
import com.abouna.lacussms.entities.GroupeClient;
import com.abouna.lacussms.entities.GroupeClientPK;
import com.abouna.lacussms.service.GroupeSmsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroupeSmsServiceImpl implements GroupeSmsService {
    private final GroupeClientDao groupeClientDao;
    private final GroupeDao groupeDao;

    public GroupeSmsServiceImpl(GroupeClientDao groupeClientDao, GroupeDao groupeDao) {
        this.groupeClientDao = groupeClientDao;
        this.groupeDao = groupeDao;
    }


    @Override
    public Groupe saveGroupe(Groupe groupe) {
        try {
            return groupeDao.create(groupe);
        } catch (DataAccessException | RuntimeException e) {
            throw new RuntimeException("Error while saving group", e);
        }
    }

    @Override
    public List<Groupe> saveGroupeList(List<Groupe> groupes) {
        try {
            return groupes.stream().peek(
                    groupe -> {
                        try {
                            groupeDao.create(groupe);
                        } catch (DataAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
            ).collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw new RuntimeException("Error while saving group list", e);
        }
    }

    @Override
    public Groupe updateGroupe(Groupe groupe) {
        try {
            return groupeDao.update(groupe);
        } catch (DataAccessException | RuntimeException e) {
            throw new RuntimeException("Error while saving group", e);
        }
    }

    @Override
    public Groupe findGroupeById(Integer id) {
        try {
            return groupeDao.findById(id);
        } catch (DataAccessException | RuntimeException e) {
            throw new RuntimeException("Error while saving group", e);
        }
    }

    @Override
    public void deleteGroupeById(Integer id) {
        try {
             groupeDao.delete(id);
        } catch (DataAccessException | RuntimeException e) {
            throw new RuntimeException("Error while saving group", e);
        }
    }

    @Override
    public List<Groupe> findAllGroupes() {
        try {
            return groupeDao.findAll();
        } catch (DataAccessException | RuntimeException e) {
            throw new RuntimeException("Error while saving group", e);
        }
    }

    @Override
    public List<GroupeClient> saveList(List<GroupeClient> groupeClients) {
        try {
            return groupeClients.stream().peek(
                    groupeClient -> {
                        try {
                            groupeClientDao.create(groupeClient);
                        } catch (DataAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
            ).collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw new RuntimeException("Error while saving group list", e);
        }
    }

    @Override
    public GroupeClient findGroupeClientById(GroupeClientPK id) {
        try {
            return groupeClientDao.findById(id);
        } catch (DataAccessException | RuntimeException e) {
            throw new RuntimeException("Error while saving group", e);
        }
    }

    @Override
    public void deleteGroupeClientById(GroupeClientPK id) {
        try {
            groupeClientDao.delete(id);
        } catch (DataAccessException | RuntimeException e) {
            throw new RuntimeException("Error while deleteGroupeClientById", e);
        }
    }

    @Override
    public List<GroupeClient> findAllGroupeClients() {
        try {
            return groupeClientDao.findAll();
        } catch (DataAccessException | RuntimeException e) {
            throw new RuntimeException("Error while saving group", e);
        }
    }
}
