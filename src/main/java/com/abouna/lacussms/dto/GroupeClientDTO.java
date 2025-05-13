package com.abouna.lacussms.dto;

import com.abouna.lacussms.entities.BkCli;

import java.util.List;

public class GroupeClientDTO {
    private Integer groupeId;
    private List<BkCli> clients;

    public GroupeClientDTO(Integer groupeId, List<BkCli> clients) {
        this.groupeId = groupeId;
        this.clients = clients;
    }

    public Integer getGroupeId() {
        return groupeId;
    }

    public void setGroupeId(Integer groupeId) {
        this.groupeId = groupeId;
    }

    public List<BkCli> getClients() {
        return clients;
    }

    public void setClients(List<BkCli> clients) {
        this.clients = clients;
    }
}
