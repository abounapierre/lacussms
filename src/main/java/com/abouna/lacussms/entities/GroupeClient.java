package com.abouna.lacussms.entities;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "groupe_client")
public class GroupeClient implements Serializable {
    @EmbeddedId
    private GroupeClientPK id;

    public GroupeClient() {
        super();
    }

    public GroupeClient(GroupeClientPK id) {
        super();
        this.id = id;
    }

    public GroupeClientPK getId() {
        return id;
    }

    public void setId(GroupeClientPK id) {
        this.id = id;
    }
}
