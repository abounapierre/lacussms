/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.BkOpe;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.main.MainMenuPanel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import org.jdesktop.swingx.JXSearchField;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SATELLITE
 */
public class BkOpePanel extends JPanel{
 private DefaultTableModel tableModel;
    private JTable table;
    private final MainMenuPanel parentPanel;
    private final LacusSmsService serviceManager;
    
    public BkOpePanel() throws IOException{
        serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        parentPanel = ApplicationConfig.getApplicationContext().getBean(MainMenuPanel.class);
        setLayout(new BorderLayout());
        JPanel haut = new JPanel();
        haut.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel lbl;
        lbl = new JLabel("GESTION DES OPERATIONS");
        haut.add(lbl);
        lbl.setFont(new Font("Broadway", Font.BOLD, 30));
        add(BorderLayout.BEFORE_FIRST_LINE, haut);
        JPanel contenu = new JPanel();
        contenu.setLayout(new BorderLayout());
        JPanel bas = new JPanel();
        bas.setLayout(new FlowLayout());
        Image ajouImg = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/Ajouter.png")));
        Image supprImg = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/Cancel2.png")));
        Image modifImg = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/OK.png")));
        JButton nouveau = new JButton(new ImageIcon(ajouImg));
        nouveau.setToolTipText("Ajouter une nouvelle opératation");
        JButton supprimer = new JButton(new ImageIcon(supprImg));
        supprimer.setToolTipText("Suprimer une opération");
        JButton modifier = new JButton(new ImageIcon(modifImg));
        modifier.setToolTipText("Modifier une opération");
        nouveau.addActionListener(ae -> {
            Nouveau nouveau1 = new Nouveau(null);
            nouveau1.setSize(400, 200);
            nouveau1.setLocationRelativeTo(null);
            nouveau1.setModal(true);
            nouveau1.setResizable(false);
            nouveau1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            nouveau1.setVisible(true);
        });
        modifier.addActionListener(ae -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                String id = (String) tableModel.getValueAt(selected, 0);
                Nouveau nouveau1;
                try {
                    nouveau1 = new Nouveau(serviceManager.getBkOpeById(id));
                    nouveau1.setSize(400, 200);
                    nouveau1.setLocationRelativeTo(null);
                    nouveau1.setModal(true);
                    nouveau1.setResizable(false);
                    nouveau1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    nouveau1.setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Aucun élément n'est selectionné");
            }
        });
        supprimer.addActionListener(ae -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                String id = (String) tableModel.getValueAt(selected, 0);
                int res = JOptionPane.showConfirmDialog(null, "Etes vous sûr de suppimer l'opération courante?", "Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (res == JOptionPane.YES_OPTION) {
                    try {
                        serviceManager.supprimerBkOpe(id);
                    } catch (Exception ex) {
                        Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    tableModel.removeRow(selected);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Aucun élément selectionné");
            }
        });
        bas.add(nouveau);
        bas.add(modifier);
        bas.add(supprimer);
        JPanel filtrePanel = new JPanel();
        filtrePanel.setLayout(new FlowLayout());
        final JXSearchField searchField = new JXSearchField("Rechercher");
        searchField.setPreferredSize(new Dimension(500, 50));
        filtrePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), "Zone de recherche"));
        filtrePanel.add(searchField);
         filtrePanel.setBackground(new Color(166, 202, 240));
        searchField.addActionListener(e -> {
            String val;
            if (searchField.getText() != null) {
                try {
                    val = searchField.getText().toUpperCase();
                    tableModel.setNumRows(0);
                    List<BkOpe> bkopeList = serviceManager.getBkOpeByName(val);
                    for (BkOpe a : bkopeList) {
                        tableModel.addRow(new Object[]{
                            a.getOpe(),
                            a.getLib()
                        });
                    }
                } catch (Exception ex) {
                    Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        contenu.add(BorderLayout.AFTER_LAST_LINE, bas);
        contenu.add(BorderLayout.BEFORE_FIRST_LINE, filtrePanel);
        tableModel = new DefaultTableModel(new Object[]{"Code","Libellé"}, 0);

        table = new JTable(tableModel);
        table.setBackground(Color.WHITE);
        //table.getColumnModel().getColumn(2).setPreferredWidth(280);
        //table.removeColumn(table.getColumnModel().getColumn(0));
        contenu.add(BorderLayout.CENTER, new JScrollPane(table));
        add(BorderLayout.CENTER, contenu);
        try {
             for (BkOpe a : serviceManager.getAllBkOpes()) {
                            tableModel.addRow(new Object[]{
                                a.getOpe(),
                                a.getLib()
                            });
            }
        } catch (Exception ex) {
            Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class Nouveau extends JDialog {

        private final JTextField codeText;
        private final JTextField nameText;
        

        public Nouveau(final BkOpe bkope) {
            setTitle("NOUVELLE OPERATION");
            setModal(true);
            setLayout(new BorderLayout(10, 10));
            JPanel haut = new JPanel();
            JLabel lbl;
            haut.add(lbl = new JLabel("<html><font color = #012345 > AJOUT D'UNE NOUVELLE OPERATION </font></html>"));
            lbl.setFont(new Font("Times New Roman", Font.ITALIC, 18));
            add(BorderLayout.BEFORE_FIRST_LINE, haut);
            DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 110dlu:", ""));
            builder.setDefaultDialogBorder();
            builder.append("Code", codeText = new JTextField(50));
            builder.append("Libellé", nameText = new JTextField(50));

            JButton annulerBtn;
            JButton okBtn;
            JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Enrégistrer"), annulerBtn = new JButton("Annuler"));
            builder.append(buttonBar, builder.getColumnCount());
            add(BorderLayout.CENTER, builder.getPanel());
            
            if (bkope != null) {
               codeText.setText(bkope.getOpe());
               nameText.setText(bkope.getLib());
               codeText.setEditable(false);
            }

            okBtn.addActionListener(ae -> {
                BkOpe a = new BkOpe();
                 if (!codeText.getText().isEmpty()) {
                    a.setOpe(codeText.getText());
                } else {
                    JOptionPane.showMessageDialog(null, "Le nom est obligatoire");
                }
                if (!nameText.getText().isEmpty()) {
                    a.setLib(nameText.getText());
                } else {
                    JOptionPane.showMessageDialog(null, "Le libellé est obligatoire");
                }

                if (bkope == null) {
                    try {
                        serviceManager.enregistrer(a);
                    } catch (Exception ex) {
                        Logger.getLogger(BkCliPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    a.setOpe(bkope.getOpe());
                    try {
                        serviceManager.modifier(a);
                    } catch (Exception ex) {
                        Logger.getLogger(BkOpePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                dispose();
                try {
                    parentPanel.setContent(new BkOpePanel());
                } catch (IOException ex) {
                    Logger.getLogger(BkOpePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            annulerBtn.addActionListener(ae -> {
                dispose();
                try {
                    parentPanel.setContent(new BkOpePanel());
                } catch (IOException ex) {
                    Logger.getLogger(BkOpePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }   
}
