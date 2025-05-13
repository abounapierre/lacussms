package com.abouna.lacussms.views.groupe;

import com.abouna.lacussms.entities.Groupe;
import com.abouna.lacussms.main.MainFrame;
import com.abouna.lacussms.service.GroupeSmsService;
import com.abouna.lacussms.views.components.LacusIcon;
import com.abouna.lacussms.views.tools.TableWithCheckBox;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.abouna.lacussms.views.tools.ConstantUtils.ICON_ADD;
import static com.abouna.lacussms.views.tools.ConstantUtils.ICON_DELETE;

public class BottomPanel extends JPanel {
    protected JButton supprimer;
    protected JButton nouveau;
    private final ContentPanel contentPanel;

    public BottomPanel(ContentPanel contentPanel){
        this.contentPanel = contentPanel;
        setLayout(new FlowLayout());
        nouveau = new JButton(new LacusIcon(ICON_ADD));
        nouveau.setToolTipText("Ajouter des clients dans le groupe");
        supprimer = new JButton(new LacusIcon(ICON_DELETE));
        supprimer.setToolTipText("Supprimer des clients du groupe");
        nouveau.addActionListener(e -> showDialog());
        supprimer.addActionListener(e -> showDeleteDialog());
        add(nouveau);
        add(supprimer);
    }

    private void showDeleteDialog() {
        Groupe groupe = (Groupe) contentPanel.getComboBox().getSelectedItem();
        if (groupe == null || groupe.getId() == null) {
            JOptionPane.showMessageDialog(contentPanel, "Veuillez sélectionner un groupe avant de supprimer des clients.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int response = JOptionPane.showConfirmDialog(contentPanel, "Êtes-vous sûr de vouloir supprimer ces clients du groupe " + groupe.getLibelle() + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);

        if(response == JOptionPane.YES_OPTION) {
            TableWithCheckBox table = contentPanel.getTable();
            List<String> selectedClients = Arrays.stream(table.getSelectedRows())
                    .mapToObj(row -> (String) table.getValueAt(row, 0))
                    .collect(Collectors.toList());
            if (selectedClients.isEmpty()) {
                JOptionPane.showMessageDialog(contentPanel, "Veuillez sélectionner au moins un client à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
            } else {
                int result = removeClientsFromGroupe(groupe, selectedClients);
                if (result > 0) {
                    contentPanel.getTableModel().setNumRows(0);
                    contentPanel.updateTable(groupe);
                    JOptionPane.showMessageDialog(contentPanel, "Clients supprimés avec succès du groupe " + groupe.getLibelle(), "Succès", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(contentPanel, "Erreur lors de la suppression des clients du groupe.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private int removeClientsFromGroupe(Groupe groupe, List<String> selectedClients) {
        try {
            return GroupeSmsService.getInstance().removeClientsFromGroupe(groupe, selectedClients);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(contentPanel, "Erreur lors de la suppression des clients du groupe : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            return 0;
        }
    }


    private void showDialog() {
        Groupe groupe = (Groupe) contentPanel.getComboBox().getSelectedItem();
        if (groupe == null || groupe.getId() == null) {
            JOptionPane.showMessageDialog(contentPanel, "Veuillez sélectionner un groupe avant d'ajouter des clients.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new AddClientToGroupeDialog(MainFrame.getInstance(), contentPanel, groupe);
    }
}
