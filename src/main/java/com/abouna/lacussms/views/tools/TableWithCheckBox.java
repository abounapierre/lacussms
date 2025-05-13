package com.abouna.lacussms.views.tools;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TableWithCheckBox extends JTable {
    private final int column;

    public TableWithCheckBox(DefaultTableModel tableModel, int column) {
        super(tableModel);
        this.column = column;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        if (this.column == column) {
            return Boolean.class;
        } else {
            return getModel().getColumnClass(convertColumnIndexToModel(column));
        }
    }
}
