package com.abouna.lacussms.views.components;

import com.abouna.lacussms.dto.ClientCSV;
import com.abouna.lacussms.dto.ResultImportDataModelDTO;
import com.abouna.lacussms.service.LacusImportDataService;
import com.abouna.lacussms.views.utils.CustomTable;
import com.abouna.lacussms.views.utils.CustomTableCellRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ImportClientPanel extends JPanel {
    private final CustomTableCellRenderer renderer = new CustomTableCellRenderer();

    public ImportClientPanel(JDialog parent, List<ClientCSV> models) {
        setLayout(new BorderLayout());
        setSize(parent.getSize());
        setBorder(BorderFactory.createTitledBorder("Liste des clients qui seront importés en base (les lignes en rouge sont invalides)"));
        JPanel content = new JPanel(new BorderLayout());
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"N° ligne","Code client", "Civilité", "Nom", "Prénom(s)", "Téléphone", "Compte", "Langue"}, 0);
        JTable table = new CustomTable(tableModel, renderer);
        table.setSelectionForeground(Color.BLUE);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
        content.add(BorderLayout.CENTER, scrollPane);
        addDataToTable(tableModel, models);
        JButton importButton = new JButton("Importer les clients");
        importButton.addActionListener(e -> importAction(models));
        JPanel bas = new JPanel(new FlowLayout());
        bas.add(importButton);
        content.add(BorderLayout.AFTER_LAST_LINE, bas);
        add(BorderLayout.CENTER, content);
    }

    private void addDataToTable(DefaultTableModel tableModel, List<ClientCSV> models) {
        renderer.setSelectedRows(invalidedRows(models));
        for (ClientCSV model : models) {
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
    }

    private List<Integer> invalidedRows(List<ClientCSV> models) {
        return models.stream()
                .filter(ClientCSV::isInvalid)
                .map(ClientCSV::getIndex)
                .collect(Collectors.toList());
    }

    private void importAction(List<ClientCSV> models) {
        LacusImportDataService importDataService = LacusImportDataService.getInstance();
        List<ClientCSV> validClients = models.stream()
                .filter(client -> !client.isInvalid())
                .collect(Collectors.toList());
        ResultImportDataModelDTO resultImportDataModelDTO = importDataService.importAccountData(validClients);
        JOptionPane.showMessageDialog(this, "Importation des clients et des comptes" + "\n" +
                "Nombre importés: " + resultImportDataModelDTO.getSuccess().size() + "\n" +
                "Nombre d'erreurs: " + resultImportDataModelDTO.getErrors().size() + "\n" +
                "Nombre existants: " + resultImportDataModelDTO.getTotalExisting() + "\n",
                "Résultat importation", JOptionPane.INFORMATION_MESSAGE);
    }
}
