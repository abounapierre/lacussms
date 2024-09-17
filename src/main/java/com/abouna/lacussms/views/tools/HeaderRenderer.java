/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.views.tools;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 *
 * @author Administrateur
 */
public class HeaderRenderer extends JLabel implements TableCellRenderer{

    public HeaderRenderer() {
        setFont(new Font("Consolas", Font.BOLD, 14));
        setOpaque(true);
        setForeground(Color.BLUE);
        setBackground(Color.PINK);
        setBorder(BorderFactory.createEtchedBorder());
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText(value.toString());
        return this;
    }
    
}
