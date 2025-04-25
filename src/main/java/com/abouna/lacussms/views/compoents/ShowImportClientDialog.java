package com.abouna.lacussms.views.compoents;

import com.abouna.lacussms.dto.ClientCSV;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ShowImportClientDialog extends JDialog {
    public ShowImportClientDialog(JFrame parent, List<ClientCSV> clients) {
        super(parent, true);
        JPanel panel = new ImportClientPanel(this, clients);
        setTitle("Import Client");
        setSize(parent.getWidth() - 200, parent.getHeight() - 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        add(BorderLayout.CENTER, panel);
        setVisible(true);
    }
}
