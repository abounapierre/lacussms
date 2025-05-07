package com.abouna.lacussms.entities;

import com.google.common.base.Objects;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class GroupeClientPK implements Serializable {
    private Integer idGroupe;
    private String idClient;

    public GroupeClientPK() {
    }

    public GroupeClientPK(Integer idGroupe, String idClient) {
        this.idGroupe = idGroupe;
        this.idClient = idClient;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public Integer getIdGroupe() {
        return idGroupe;
    }

    public void setIdGroupe(Integer idGroupe) {
        this.idGroupe = idGroupe;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GroupeClientPK that = (GroupeClientPK) o;
        return Objects.equal(idGroupe, that.idGroupe) && Objects.equal(idClient, that.idClient);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idGroupe, idClient);
    }
}
