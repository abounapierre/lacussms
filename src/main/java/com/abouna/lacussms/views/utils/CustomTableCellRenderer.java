/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.views.utils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 *
 * @author abouns
 */
public class CustomTableCellRenderer extends DefaultTableCellRenderer {
    private List<Integer> selectedRows;

    public CustomTableCellRenderer() {
    }

    public List<Integer> getSelectedRows() {
        return selectedRows;
    }

    public void setSelectedRows(List<Integer> selectedRows) {
        this.selectedRows = selectedRows;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setBackground(selectedRows.contains(row) ? Color.RED : Color.WHITE);
        return c;
    }
}
