/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.BkAgence;
import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.entities.BkEve;
import com.abouna.lacussms.entities.BkOpe;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.components.ContentEveDialog;
import com.abouna.lacussms.views.main.MainMenuPanel;
import com.abouna.lacussms.views.utils.CustomTable;
import com.abouna.lacussms.views.utils.CustomTableCellRenderer;
import com.abouna.lacussms.views.utils.DialogUtils;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXSearchField;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.abouna.lacussms.views.tools.ConstantUtils.NO_SELECTED_ITEM;

/**
 *
 * @author SATELLITE
 */
public class BkEvePanel extends JPanel{
    private DefaultTableModel tableModel;
    private JTable table;
    private final MainMenuPanel parentPanel;
    private final LacusSmsService serviceManager;
    private final CustomTableCellRenderer renderer = new CustomTableCellRenderer();

    public BkEvePanel() throws IOException{
        serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        parentPanel = ApplicationConfig.getApplicationContext().getBean(MainMenuPanel.class);
        setLayout(new BorderLayout());
        JPanel haut = new JPanel();
        haut.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel lbl;
        lbl = new JLabel("GESTION DES EVENEMENTS");
        haut.add(lbl);
        lbl.setFont(new Font("Broadway", Font.BOLD, 30));
        add(BorderLayout.BEFORE_FIRST_LINE, haut);
        JPanel contenu = new JPanel();
        contenu.setLayout(new BorderLayout());
        JPanel bas = new JPanel();
        bas.setLayout(new FlowLayout());
        Image addImg = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/Ajouter.png")));
        Image deleteImg = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/Cancel2.png")));
        Image updateImg = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/OK.png")));
        JButton nouveau = new JButton(new ImageIcon(addImg));
        nouveau.setToolTipText("Ajouter un nouvel évenement");
        JButton supprimer = new JButton(new ImageIcon(deleteImg));
        supprimer.setToolTipText("Supprimer un evenement");
        JButton modifier = new JButton(new ImageIcon(updateImg));
        modifier.setToolTipText("Modifier un evenement");
        JButton filtre = new JButton("Filtrer");
        nouveau.addActionListener((ActionEvent ae) -> {
            DialogUtils.initDialog(new Nouveau(null), BkEvePanel.this.getParent(), 500, 400);
        });
        modifier.addActionListener((ActionEvent ae) -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                Integer id = (Integer) tableModel.getValueAt(selected, 0);
                try {
                    DialogUtils.initDialog(new Nouveau(serviceManager.getBkEveById(id)), BkEvePanel.this.getParent(), 500, 400);
                } catch (Exception ex) {
                    Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(BkEvePanel.this.getParent(), "Aucun élément n'est selectionné");
            }
        });
        supprimer.addActionListener((ActionEvent ae) -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                Integer id = (Integer) tableModel.getValueAt(selected, 0);
                int res = JOptionPane.showConfirmDialog(BkEvePanel.this.getParent(), "Etes vous sûr de suppimer l'évenement courant?", "Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (res == JOptionPane.YES_OPTION) {
                    try {
                        serviceManager.supprimerBkEve(id);
                    } catch (Exception ex) {
                        Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    tableModel.removeRow(selected);
                }
            } else {
                JOptionPane.showMessageDialog(BkEvePanel.this.getParent(), NO_SELECTED_ITEM);
            }
        });
        JButton purgerBtn = new JButton("Purger");
        purgerBtn.addActionListener((ActionEvent e) -> {
            DialogUtils.initDialog(new DeleteBkEveDialog(), BkEvePanel.this.getParent(), 450, 200);
        });
        bas.add(nouveau);
        bas.add(modifier);
        bas.add(supprimer);
        bas.add(purgerBtn);
        JPanel filtrePanel = new JPanel();
        JPanel searchPanel = new JPanel(new FlowLayout());
        filtrePanel.setLayout(new FlowLayout());
        final JXSearchField searchField = new JXSearchField("Rechercher");
        searchField.setPreferredSize(new Dimension(500, 25));
        JLabel labelDate1 = new JLabel("DAte de début");
        JXDatePicker dateDeb = new JXDatePicker();
        JLabel labelDate2 = new JLabel("DAte de fin");
        JXDatePicker dateFin = new JXDatePicker();
        JButton filterBtn = new JButton("Filtrer");
        JButton printBtn = new JButton("Imprimer");
        searchPanel.add(labelDate1);
        searchPanel.add(dateDeb);
        searchPanel.add(labelDate2);
        searchPanel.add(dateFin);
        searchPanel.add(filterBtn);
        searchPanel.add(printBtn);
        filtrePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), "Zone de recherche"));
        filtrePanel.add(searchField);
        filtrePanel.setBackground(new Color(166, 202, 240));
        searchField.addActionListener((ActionEvent e) -> {
            String val;
            if (searchField.getText() != null) {
                try {
                    val = searchField.getText().toUpperCase();
                    tableModel.setNumRows(0);
                    addData(serviceManager.getBkEveByCriteria(val));
                } catch (Exception ex) {
                    Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        contenu.add(BorderLayout.AFTER_LAST_LINE, bas);
        contenu.add(BorderLayout.BEFORE_FIRST_LINE, filtrePanel);
        tableModel = new DefaultTableModel(new Object[]{"Id","Code","Client","Compte","Date","Heure","Traité?"}, 0);
        table = new CustomTable(tableModel, renderer);
        table.setBackground(Color.WHITE);
        table.setComponentPopupMenu(getPopupMenu());
        table.setSelectionForeground(Color.BLUE);
        contenu.add(BorderLayout.CENTER, new JScrollPane(table));
        add(BorderLayout.CENTER, contenu);
        try {
            addData(serviceManager.getBkEveByLimit(100));
        } catch (Exception ex) {
            Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private JPopupMenu getPopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Détails");
        menuItem.addActionListener((ActionEvent e) -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                Integer id = (Integer) tableModel.getValueAt(selected, 0);
                try {
                    ContentEveDialog.initDialog(serviceManager.getBkEveById(id));
                } catch (Exception ex) {
                    Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(BkEvePanel.this.getParent(), "Aucun élément n'est selectionné");
            }
        });
        popup.add(menuItem);
        return popup;
    }

    private void addData(List<BkEve> bkeveList) {
        renderer.setSelectedRows(IntStream.range(0, bkeveList.size()).filter(i -> !bkeveList.get(i).isSent()).boxed().collect(Collectors.toList()));
        bkeveList.forEach((a) -> tableModel.addRow(new Object[]{
            a.getId(),
            a.getNumEve(),
            a.getCli() == null ? "" : a.getCli().getNom() + " " + a.getCli().getPrenom(),
            a.getCompte(),
            a.getDVAB() == null ? "" : a.getDVAB(),
            a.getHsai(),
            a.isSent() ? "Oui" : "Non"
        }));
    }

    private class Nouveau extends JDialog {

        private final JTextField compteText;
        private final JComboBox<String> etatBox;
        private final JComboBox<BkOpe> bkOpeBox;
        private final JComboBox<BkCli> bkCliBox;
        private final JComboBox<BkAgence> bkAgenceBox;
        private final JTextField montText,codeText;
        private int c = 0;
        private int rang =0;
        private int c1 = 0;
        private int rang1 =0;
        private final int c2=0;
        private int rang2=0;

        public Nouveau(final BkEve bkeve) {
            setTitle("NOUVEL EVENEMENT");
            setModal(true);
            setLayout(new BorderLayout(10, 10));
            JPanel haut = new JPanel();
            JLabel lbl;
            haut.add(lbl = new JLabel("<html><font color = #012345 > AJOUT D'UN NOUVEL EVENEMENT </font></html>"));
            lbl.setFont(new Font("Times New Roman", Font.ITALIC, 18));
            add(BorderLayout.BEFORE_FIRST_LINE, haut);
            DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 110dlu:", ""));
            builder.setDefaultDialogBorder();
            builder.append("Code ", codeText = new JTextField(50));
            builder.append("Agence", bkAgenceBox = new JComboBox<>());
            builder.append("Client", bkCliBox = new JComboBox<>());
            builder.append("Opération", bkOpeBox = new JComboBox<>());
            builder.append("Numéro compte", compteText = new JTextField(50));
            builder.append("Montant", montText = new JTextField(50));
            builder.append("Etat", etatBox = new JComboBox<>());
            etatBox.addItem("VA");
            etatBox.addItem("AT");
            etatBox.addItem("FO");
            etatBox.addItem("VF");
            etatBox.addItem("IG");
            etatBox.addItem("IF");
            etatBox.addItem("AB");
            etatBox.addItem("AN");
            etatBox.addItem("TR");
            serviceManager.getAllCli().stream().peek(bkCliBox::addItem).peek((bkCli) -> {
                if(bkeve != null){
                    if(bkeve.getCli().getCode().equals(bkCli.getCode())){
                        rang = c;
                    } 
                }
            }).forEach((_item) -> c++);
            serviceManager.getAllBkAgences().stream().peek(bkAgenceBox::addItem).peek((bkagence) -> {
                if(bkeve != null){
                    if(bkeve.getBkAgence().getNuma().equals(bkagence.getNuma())){
                        rang2 = c2;
                    } 
                }
            }).forEach((_item) -> c++);
            serviceManager.getAllBkOpes().stream().peek(bkOpeBox::addItem).peek((bkOpe) -> {
                if(bkeve != null){
                    if(bkeve.getOpe().getOpe().equals(bkOpe.getOpe())){
                        rang1 = c1;
                    }  
                }
            }).forEach((_item) -> {
                c1++;
            });
            JButton okBtn;
            JButton annulerBtn;
            JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Enrégistrer"), annulerBtn = new JButton("Annuler"));
            builder.append(buttonBar, builder.getColumnCount());
            add(BorderLayout.CENTER, builder.getPanel());
            
            if (bkeve != null) {
               codeText.setText(Integer.toString(bkeve.getId()));
               codeText.setEditable(false);
               compteText.setText(bkeve.getCompte());
               bkCliBox.setSelectedIndex(rang);
               bkOpeBox.setSelectedIndex(rang1);
               bkAgenceBox.setSelectedIndex(rang2);
               montText.setText(Double.toString(bkeve.getMont()));
                switch (bkeve.getEtat()) {
                    case "VA":
                        etatBox.setSelectedIndex(0);
                        break;
                    case "AT":
                        etatBox.setSelectedIndex(1);
                        break;
                    case "FO":
                    case "IG":
                    case "IF":
                    case "AB":
                    case "VF":
                        etatBox.setSelectedIndex(2);
                        break;
                    case "AN":
                        etatBox.setSelectedIndex(7);
                        break;
                    case "TR":
                        etatBox.setSelectedIndex(8);
                        break;
                }
            }

            okBtn.addActionListener(ae -> {
                BkEve a = new BkEve();
                 if (!compteText.getText().isEmpty()) {
                    a.setCompte(compteText.getText());
                } else {
                    JOptionPane.showMessageDialog(BkEvePanel.this.getParent(), "Le compte est obligatoire");
                    return;
                }
                 if (!montText.getText().isEmpty()) {
                    a.setMont(Double.parseDouble(montText.getText()));
                } else {
                    JOptionPane.showMessageDialog(BkEvePanel.this.getParent(), "Le compte est obligatoire");
                    return;
                }

                if (!codeText.getText().isEmpty()) {
                    a.setNumEve(codeText.getText());
                } else {
                    JOptionPane.showMessageDialog(BkEvePanel.this.getParent(), "Le code est obligatoire");
                    return;
                }

                a.setOpe((BkOpe) bkOpeBox.getSelectedItem());
                a.setEtat((String) etatBox.getSelectedItem());
                a.setCli((BkCli) bkCliBox.getSelectedItem());
                a.setBkAgence((BkAgence) bkAgenceBox.getSelectedItem());
                Date d = new Date();
                long l = d.getTime();
                a.setHsai(Long.toString(l));
                a.setSent(false);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
                a.setEventDate(d);
                a.setDVAB(format.format(d));
                a.setId(serviceManager.getMaxIndexBkEve() + 1);

                if (bkeve == null) {
                    try {
                        serviceManager.enregistrer(a);
                    } catch (Exception ex) {
                        Logger.getLogger(BkEvePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    a.setId(bkeve.getId());
                    try {
                        serviceManager.modifier(a);
                    } catch (Exception ex) {
                        Logger.getLogger(BkEvePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                dispose();
                try {
                    parentPanel.setContent(new BkEvePanel());
                } catch (IOException ex) {
                    Logger.getLogger(BkEvePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            annulerBtn.addActionListener((ActionEvent ae) -> {
                dispose();
                try {
                    parentPanel.setContent(new BkEvePanel());
                } catch (IOException ex) {
                    Logger.getLogger(BkEvePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }  
}
