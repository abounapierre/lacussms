package com.abouna.lacussms.views.groupe;

import com.abouna.lacussms.dto.BkCliCompte;
import com.abouna.lacussms.entities.Groupe;
import com.abouna.lacussms.service.GroupeSmsService;
import com.abouna.lacussms.views.tools.TableWithCheckBox;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;

public class ContentPanel extends JPanel {
    public static final String GROUPE_VALIDE = "Veuillez sélectionner un groupe valide.";
    private final Object[] columnNames;
    private DefaultTableModel tableModel;
    private TableWithCheckBox table;
    private JComboBox<Groupe> comboBox;

    public ContentPanel(Object[] columnNames) {
        this.columnNames = columnNames;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        add(BorderLayout.AFTER_LAST_LINE, new BottomPanel(this));
        add(BorderLayout.BEFORE_FIRST_LINE, middlePanel());
        add(BorderLayout.CENTER, getPane());
    }

    private JPanel middlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBackground(new Color(166, 202, 240));
        panel.setBorder(BorderFactory.createTitledBorder("Liste des groupes"));
        panel.add(new JLabel("Sélectionner le groupe :"));
        panel.add(getComBox());
        panel.add(getAddButton());
        panel.add(getUpdateButton());
        panel.add(getDeleteButton());
        return panel;
    }

    private JButton getDeleteButton() {
        JButton deleteButton = new JButton("Supprimer");
        deleteButton.addActionListener(e -> deleteGroupe());
        return deleteButton;
    }

    private JButton getUpdateButton() {
        JButton updateButton = new JButton("Modifier");
        updateButton.addActionListener(e -> updateGroupe());
        return updateButton;
    }

    private JButton getAddButton() {
        JButton addButton = new JButton("Ajouter");
        addButton.addActionListener(e -> addGroupe());
        return addButton;
    }

    private JComboBox<Groupe> getComBox() {
        comboBox = new JComboBox<>();
        addGroupeToComboBox();
        comboBox.setPreferredSize(new Dimension(200, 30));
        comboBox.addItemListener(this::getClients);
        return comboBox;
    }

    private void getClients(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            Groupe groupe = (Groupe) comboBox.getSelectedItem();
            if (groupe != null && groupe.getId() != null) {
                updateTable(groupe);
            } else {
                tableModel.setRowCount(0);
            }
        }
    }

    public void updateTable(Groupe groupe) {
        List<BkCliCompte> groupes = GroupeSmsService.getInstance().findClientsByGroupeId(groupe.getId());
        tableModel.setNumRows(0);
        groupes.forEach(g -> tableModel.addRow(new Object[]{g.getBkCli().getCode(), g.getBkCli().toString(), g.getCompte(), Boolean.FALSE}));
    }

    public void addGroupeToComboBox() {
        comboBox.addItem(new Groupe("Sélectionner un groupe"));
        GroupeSmsService.getInstance().findAllGroupes().forEach(comboBox::addItem);
    }

    private JScrollPane getPane() {
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new TableWithCheckBox(tableModel, 3);
        return new JScrollPane(table);
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public JComboBox<Groupe> getComboBox() {
        return comboBox;
    }

    public void deleteGroupe() {
        Groupe groupe = (Groupe) comboBox.getSelectedItem();
        if (groupe == null || groupe.getId() == null) {
            JOptionPane.showMessageDialog(this, GROUPE_VALIDE, "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int response = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer ce groupe ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            GroupeSmsService.getInstance().deleteGroupeById(groupe.getId());
            JOptionPane.showMessageDialog(this, "Groupe supprimé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            comboBox.removeItem(groupe);
        }
    }

    public void updateGroupe() {
        Groupe groupe = (Groupe) comboBox.getSelectedItem();
        if (groupe == null || groupe.getId() == null) {
            JOptionPane.showMessageDialog(this, GROUPE_VALIDE, "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        AddGroupeDialog.initDialog(this, groupe);
    }

    public void addGroupe() {
        AddGroupeDialog.initDialog(this, new Groupe());
    }

    public TableWithCheckBox getTable() {
        return table;
    }
}
