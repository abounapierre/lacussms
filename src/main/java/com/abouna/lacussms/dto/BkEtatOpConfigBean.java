package com.abouna.lacussms.dto;

import java.util.List;

public class BkEtatOpConfigBean {
    private List<String> listString;
    private String condition;

    public BkEtatOpConfigBean(List<String> listString, String condition) {
        this.listString = listString;
        this.condition = condition;
    }

    public List<String> getListString() {
        return listString;
    }

    public void setListString(List<String> listString) {
        this.listString = listString;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
