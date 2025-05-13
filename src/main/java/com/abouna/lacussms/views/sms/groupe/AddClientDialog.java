package com.abouna.lacussms.views.sms.groupe;

import com.abouna.lacussms.service.GroupeSmsService;
import com.abouna.lacussms.views.tools.TableWithCheckBox;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddClientDialog extends JDialog {
    private final GroupeSMSPanel panel;
    private JTable table;

    public AddClientDialog(GroupeSMSJDialog parent, GroupeSMSPanel panel) {
        super(parent, true);
        this.panel = panel;
        setTitle("Ajouter un client au groupe");
        setSize(parent.getWidth() - 150, parent.getHeight() - 150);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(addPanel(), BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel addPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JScrollPane(getTable()), BorderLayout.CENTER);
        panel.add(getButtonPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JTable getTable() {
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Code client", "Noms et prénoms", "N° compte", "Action"}, 0);
        GroupeSmsService.getInstance().findClientsCompte().forEach(client -> {
            tableModel.addRow(new Object[]{
                    client.getBkCli().getCode(),
                    client.getBkCli().toString(),
                    client.getCompte(),
                    Boolean.FALSE
            });
        });
        table = new TableWithCheckBox(tableModel, 3);
        return table;
    }

    private JButton getButtonPanel() {
        JButton button = new JButton("Valider");
        button.addActionListener(this::getSelectedClients);
        button.setBackground(new Color(166, 202, 240));
        return button;
    }

    private void getSelectedClients(ActionEvent e) {
        int response = JOptionPane.showConfirmDialog(this, "Veuillez confirmer svp", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            List<String> selectedClients = Arrays.stream(table.getSelectedRows())
                    .mapToObj(row -> (String) table.getValueAt(row, 0))
                    .collect(Collectors.toList());
            panel.getClientTextArea().setText(String.join(",", selectedClients));
            dispose();
        }
    }

    public static void initDialog(GroupeSMSJDialog parent, GroupeSMSPanel groupeSMSPanel) {
        new AddClientDialog(parent, groupeSMSPanel);
    }
}
