/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.views.utils;

/**
 *
 * @author SATELLITE
 */
public class CheckListItem {

    private final String label;
    private boolean isSelected = false;
    private final Integer id;

    public CheckListItem(String label, Integer id) {
        this.label = label;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public String toString() {
        return label;
    }
}
