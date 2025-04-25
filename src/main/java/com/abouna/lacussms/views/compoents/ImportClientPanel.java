package com.abouna.lacussms.views.compoents;

import com.abouna.lacussms.dto.ClientCSV;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ImportClientPanel extends JPanel {

    public ImportClientPanel(JDialog parent, List<ClientCSV> models) {
        setLayout(new BorderLayout());
        setSize(parent.getSize());
        setBorder(BorderFactory.createTitledBorder("Liste des clients qui seront importés en base"));
        JPanel content = new JPanel(new BorderLayout());
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"N° ligne","Code client", "Civilité", "Nom", "Prénom(s)", "Téléphone", "Compte", "Langue"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
        content.add(BorderLayout.CENTER, scrollPane);
        for (ClientCSV model : models) {
            addDataToTable(tableModel, model);
        }
        JButton importButton = new JButton("Importer les clients");
        importButton.addActionListener(e -> importAction(models));
        JPanel bas = new JPanel(new FlowLayout());
        bas.add(importButton);
        content.add(BorderLayout.AFTER_LAST_LINE, bas);
        add(BorderLayout.CENTER, content);
    }

    private void addDataToTable(DefaultTableModel tableModel, ClientCSV model) {
        tableModel.addRow(new Object[]{
                model.getLigne(),
                model.getCode(),
                model.getCivilite(),
                model.getNom(),
                model.getPrenom(),
                model.getTelephone(),
                model.getCompte(),
                model.getLangue()
        });
    }

    private void importAction(List<ClientCSV> models) {
        // Handle import logic here
        JOptionPane.showMessageDialog(this, "Clients imported successfully!");
    }
}
