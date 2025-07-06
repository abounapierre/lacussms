package com.abouna.lacussms.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "job_data")
public class JobData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "quartz_id")
    private String quartzId;
    @Lob
    @Column(name = "contacts")
    private String contacts;
    @Column(name = "all_clients")
    private boolean allClients;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuartzId() {
        return quartzId;
    }

    public void setQuartzId(String quartzId) {
        this.quartzId = quartzId;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public boolean isAllClients() {
        return allClients;
    }

    public void setAllClients(boolean allClients) {
        this.allClients = allClients;
    }
}
