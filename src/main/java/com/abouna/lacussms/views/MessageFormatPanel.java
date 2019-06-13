/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.BkOpe;
import com.abouna.lacussms.entities.MessageFormat;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.main.MainMenuPanel;
import com.abouna.lacussms.views.tools.Utils;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXSearchField;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author abouna
 */
public class MessageFormatPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private final JButton nouveau, modifier, supprimer;
    private final JButton filtre;
    @Autowired
    private MainMenuPanel parentPanel;
    @Autowired
    private LacusSmsService serviceManager;

    public MessageFormatPanel() throws IOException {
        serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        parentPanel = ApplicationConfig.getApplicationContext().getBean(MainMenuPanel.class);
        setLayout(new BorderLayout());
        JPanel haut = new JPanel();
        haut.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel lbl;
        lbl = new JLabel("GESTION DES FORMATS DE MESSAGE");
        haut.add(lbl);
        lbl.setFont(new Font("Broadway", Font.BOLD, 30));
        add(BorderLayout.BEFORE_FIRST_LINE, haut);
        JPanel contenu = new JPanel();
        contenu.setLayout(new BorderLayout());
        JPanel bas = new JPanel();
        bas.setLayout(new FlowLayout());
        Image ajouImg = ImageIO.read(getClass().getResource("/images/Ajouter.png"));
        Image supprImg = ImageIO.read(getClass().getResource("/images/Cancel2.png"));
        Image modifImg = ImageIO.read(getClass().getResource("/images/OK.png"));
        nouveau = new JButton(new ImageIcon(ajouImg));
        nouveau.setToolTipText("Ajouter un nouveau format de massage");
        supprimer = new JButton(new ImageIcon(supprImg));
        supprimer.setToolTipText("Suprimer un format de message");
        modifier = new JButton(new ImageIcon(modifImg));
        modifier.setToolTipText("Modifier un format de message");
        filtre = new JButton("Filtrer");
        nouveau.addActionListener((ActionEvent ae) -> {
            Nouveau nouveau1 = new Nouveau(null);
            nouveau1.setSize(550, 350);
            nouveau1.setLocationRelativeTo(null);
            nouveau1.setModal(true);
            nouveau1.setResizable(false);
            nouveau1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            nouveau1.setVisible(true);
        });
        modifier.addActionListener((ActionEvent ae) -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                Integer id = (Integer) tableModel.getValueAt(selected, 0);
                Nouveau nouveau1;
                try {
                    nouveau1 = new Nouveau(serviceManager.getFormatById(id));
                    nouveau1.setSize(550, 350);
                    nouveau1.setLocationRelativeTo(null);
                    nouveau1.setModal(true);
                    nouveau1.setResizable(false);
                    nouveau1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    nouveau1.setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                JOptionPane.showMessageDialog(null, "Aucun élément n'est selectionné");
            }
        });
        supprimer.addActionListener((ActionEvent ae) -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                Integer id = (Integer) tableModel.getValueAt(selected, 0);
                int res = JOptionPane.showConfirmDialog(null, "Etes vous sûr de suppimer le format de message courant?", "Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (res == JOptionPane.YES_OPTION) {
                    try {
                        serviceManager.supprimerFormat(id);
                    } catch (Exception ex) {
                        Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
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
        searchField.addActionListener((ActionEvent e) -> {
            String val = null;
            if (searchField.getText() != null) {
                try {
                    val = searchField.getText().toUpperCase();
                    tableModel.setNumRows(0);
                    List<MessageFormat> messages = serviceManager.getAll();
                    messages.stream().forEach((a) -> {
                        tableModel.addRow(new Object[]{
                            a.getId(),
                            a.getName(),
                            a.getContent(),
                            a.getOpe() != null ? a.getOpe().getLib() : "",
                            a.getLangue()
                        });
                    });
                } catch (Exception ex) {
                    Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        contenu.add(BorderLayout.AFTER_LAST_LINE, bas);
        contenu.add(BorderLayout.BEFORE_FIRST_LINE, filtrePanel);
        tableModel = new DefaultTableModel(new Object[]{"N° Ordre", "Nom", "Contenu", "Opération", "Langue"}, 0);

        table = new JTable(tableModel);
        table.setBackground(Color.WHITE);
        table.getColumnModel().getColumn(2).setPreferredWidth(280);
        //table.removeColumn(table.getColumnModel().getColumn(0));
        contenu.add(BorderLayout.CENTER, new JScrollPane(table));
        add(BorderLayout.CENTER, contenu);
        try {
            serviceManager.getAll().stream().forEach((a) -> {
                tableModel.addRow(new Object[]{
                    a.getId(),
                    a.getName(),
                    a.getContent(),
                    a.getOpe() != null ? a.getOpe().getLib() : "",
                    a.getLangue()
                });
            });
        } catch (Exception ex) {
            Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class Nouveau extends JDialog {

        private final JButton okBtn, annulerBtn;
        private final JTextField nameText;
        private final JTextArea contentText;
        private final JComboBox<BkOpe> bkOpeBox;
        private final JComboBox<String> langueBox;
        private Integer rang = 0, c = 0;

        public Nouveau(final MessageFormat messageFormat) {
            setTitle("NOUVEAU FORMAT DE MESSAGE");
            setModal(true);
            setLayout(new BorderLayout(10, 10));
            JPanel haut = new JPanel();
            JLabel lbl;
            haut.add(lbl = new JLabel("<html><font color = #012345 > AJOUT D'UN NOUVEAU FORMAT DE MESSAGE </font></html>"));
            lbl.setFont(new Font("Times New Roman", Font.ITALIC, 18));
            add(BorderLayout.BEFORE_FIRST_LINE, haut);
            DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 180dlu:", ""));
            builder.setDefaultDialogBorder();
            builder.append("Opération", bkOpeBox = new JComboBox<>());
            builder.append("Nom", nameText = new JTextField(30));
            builder.append("Langue", langueBox = new JComboBox<>());
            builder.append("Contenu de format", contentText = new JTextArea(5, 20));
            contentText.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            contentText.setLineWrap(true);
            contentText.setWrapStyleWord(true);
            langueBox.addItem("Français");
            langueBox.addItem("Anglais");
            bkOpeBox.addItem(null);
            serviceManager.getAllBkOpes().stream().map((bkOpe) -> {
                bkOpeBox.addItem(bkOpe);
                return bkOpe;
            }).map((bkOpe) -> {
                if (messageFormat != null) {
                    BkOpe bkOpe1 = messageFormat.getOpe();
                    if (bkOpe1 != null) {
                        if (bkOpe1.getOpe().equals(bkOpe.getOpe())) {
                            rang = c;
                        }
                    }
                }
                return bkOpe;
            }).forEach((_item) -> {
                c++;
            });
            JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Enrégistrer"), annulerBtn = new JButton("Annuler"));
            builder.append(buttonBar, builder.getColumnCount());
            add(BorderLayout.CENTER, builder.getPanel());

            if (messageFormat != null) {
                nameText.setText(messageFormat.getName());
                contentText.setText(messageFormat.getContent());
                if (messageFormat.getOpe() != null) {
                    bkOpeBox.setSelectedIndex(rang + 1);
                } else {
                    bkOpeBox.setSelectedIndex(0);
                }

                switch (messageFormat.getLangue()) {
                    case "FR":
                        langueBox.setSelectedIndex(0);
                        break;
                    case "EN":
                        langueBox.setSelectedIndex(1);
                        break;
                }
            }

            okBtn.addActionListener((ActionEvent ae) -> {
                MessageFormat a = new MessageFormat();

                if (!nameText.getText().equals("")) {
                    a.setName(nameText.getText());
                } else {
                    JOptionPane.showMessageDialog(null, "Le nom est obligatoire");
                    return;
                }
                if (!contentText.getText().equals("")) {
                    String text = contentText.getText();
                    if (Utils.isCorrect(text) & Utils.iscorrect(Utils.extract(text))) {
                        a.setContent(contentText.getText());
                    } else {
                        JOptionPane.showMessageDialog(null, "Format du contenu est incorrect");
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Le contenu est obligatoire");
                    return;
                }

                if (bkOpeBox.getSelectedIndex() != 0) {
                    a.setOpe((BkOpe) bkOpeBox.getSelectedItem());
                }

                if (langueBox.getSelectedIndex() == 0) {
                    a.setLangue("FR");
                } else if (langueBox.getSelectedIndex() == 1) {
                    a.setLangue("EN");
                }

                if (messageFormat == null) {
                    try {
                        serviceManager.enregistrer(a);
                    } catch (Exception ex) {
                        Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    a.setId(messageFormat.getId());
                    try {
                        serviceManager.modifier(a);
                    } catch (Exception ex) {
                        Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                dispose();
                try {
                    parentPanel.setContenu(new MessageFormatPanel());
                } catch (IOException ex) {
                    Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            annulerBtn.addActionListener((ActionEvent ae) -> {
                dispose();
                try {
                    parentPanel.setContenu(new MessageFormatPanel());
                } catch (IOException ex) {
                    Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }
}
