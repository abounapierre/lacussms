package com.abouna.lacussms.views.groupe;

import com.abouna.lacussms.entities.Groupe;
import com.abouna.lacussms.service.GroupeSmsService;
import com.abouna.lacussms.views.tools.TableWithCheckBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddClientToGroupeDialog extends JDialog {
    private static final Logger log = LoggerFactory.getLogger(AddClientToGroupeDialog.class);
    private final Groupe groupe;
    private TableWithCheckBox table;
    private final ContentPanel contentPanel;

    public AddClientToGroupeDialog(JFrame parent, ContentPanel contentPanel, Groupe groupe) {
        super(parent, true);
        this.groupe = groupe;
        this.contentPanel = contentPanel;
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
        panel.setBorder(BorderFactory.createTitledBorder("Ajouter des clients au groupe " + groupe.getLibelle()));
        panel.add(new AddClientToGroupePanel(), BorderLayout.CENTER);
        return panel;
    }

    private class AddClientToGroupePanel extends JPanel {

        public AddClientToGroupePanel() {
            setLayout(new BorderLayout());
            JPanel content = new JPanel(new BorderLayout());
            content.add(new JScrollPane(getTable()), BorderLayout.CENTER);
            content.add(getButtonPanel(), BorderLayout.SOUTH);
            add(BorderLayout.CENTER, content);
        }

        private JPanel getButtonPanel() {
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(getButton());
            return buttonPanel;
        }

        private JButton getButton() {
            JButton button = new JButton("Ajouter les clients sélectionnés au groupe");
            button.addActionListener(this::getSelectedClients);
            return button;
        }

        private void getSelectedClients(ActionEvent e) {
            int response = JOptionPane.showConfirmDialog(this, "Veuillez confirmer svp", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                List<String> selectedClients = Arrays.stream(table.getSelectedRows())
                        .mapToObj(row -> (String) table.getValueAt(row, 0))
                        .collect(Collectors.toList());
                addSelectedClientsToGroupe(selectedClients, groupe);
                JOptionPane.showMessageDialog(this, "Clients ajoutés au groupe avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                contentPanel.updateTable(groupe);
            }
        }

        private void addSelectedClientsToGroupe(List<String> selectedClients, Groupe groupe) {
            try {
                GroupeSmsService.getInstance().addClientsToGroupe(selectedClients, groupe);
            } catch (Exception ex) {
                log.error("Erreur lors de l'ajout des clients au groupe: {}", ex.getMessage());
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout des clients au groupe: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }

        private JTable getTable() {
            DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Code client", "Noms et prénoms", "N° compte", "Action"}, 0);
            GroupeSmsService.getInstance().findClientsNotInGroupe(groupe.getId()).forEach(client -> {
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
    }
}
