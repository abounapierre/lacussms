package com.abouna.lacussms.service;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.Groupe;
import com.abouna.lacussms.entities.GroupeClient;
import com.abouna.lacussms.entities.GroupeClientPK;

import java.util.List;

public interface GroupeSmsService {

    static GroupeSmsService getInstance() {
        return ApplicationConfig.getApplicationContext().getBean(GroupeSmsService.class);
    }

    Groupe saveGroupe(Groupe groupe);
    List<Groupe> saveGroupeList(List<Groupe> groupes);
    Groupe updateGroupe(Groupe groupe);
    Groupe findGroupeById(Integer id);
    void deleteGroupeById(Integer id);
    List<Groupe> findAllGroupes();


    List<GroupeClient> saveList(List<GroupeClient> groupeClients);
    GroupeClient findGroupeClientById(GroupeClientPK id);
    void deleteGroupeClientById(GroupeClientPK id);
    List<GroupeClient> findAllGroupeClients();
}
