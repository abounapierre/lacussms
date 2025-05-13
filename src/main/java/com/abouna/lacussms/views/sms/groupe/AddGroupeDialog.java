package com.abouna.lacussms.views.sms.groupe;

import com.abouna.lacussms.service.GroupeSmsService;
import com.abouna.lacussms.views.tools.TableWithCheckBox;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AddGroupeDialog extends JDialog {
    private JTable table;
    private final GroupeSMSPanel groupeSMSPanel;

    public AddGroupeDialog(GroupeSMSJDialog parent, GroupeSMSPanel groupeSMSPanel) {
        super(parent, true);
        this.groupeSMSPanel = groupeSMSPanel;
        setTitle("Sélectionner des groupes");
        setSize(450,  250);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(addPanel(), BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel addPanel() {
        JPanel content = new JPanel(new BorderLayout());
        content.add(new JScrollPane(getTable()), BorderLayout.CENTER);
        content.add(getButtonPanel(), BorderLayout.SOUTH);
        add(BorderLayout.CENTER, content);
        return content;
    }

    private JButton getButtonPanel() {
        JButton button = new JButton("Ajouter");
        button.addActionListener(this::getSelectedGroupes);
        button.setBackground(new Color(166, 202, 240));
        return button;
    }

    private void getSelectedGroupes(ActionEvent actionEvent) {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner au moins un groupe.", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        groupeSMSPanel.getGroupeTextArea().setText(Arrays.stream(selectedRows)
                .mapToObj( row -> table.getValueAt(row, 0).toString()).collect(Collectors.joining(",")));
        dispose();
    }

    private JTable getTable() {
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Id groupe", "Libellé", "Action"}, 0);
        table = new TableWithCheckBox(tableModel, 2);
        GroupeSmsService.getInstance().findAllGroupes().forEach(groupe -> {
            tableModel.addRow(new Object[]{
                    groupe.getId(),
                    groupe.getLibelle(),
                    Boolean.FALSE
            });
        });
        return table;
    }

    public static void initDialog(GroupeSMSJDialog parent, GroupeSMSPanel groupeSMSPanel) {
        new AddGroupeDialog(parent, groupeSMSPanel);
    }
}
