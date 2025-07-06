/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.views.utils;

import javax.swing.*;
import javax.swing.table.TableModel;

/**
 *
 * @author abouns
 */
public class CustomTable extends JTable {
    public CustomTable(TableModel tableModel, CustomTableCellRenderer renderer) {
        super(tableModel);
        setRenderer(renderer);
    }

    public void setRenderer(CustomTableCellRenderer renderer) {
        for (int i = 0; i < getColumnCount(); i++) {
            this.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }
}
