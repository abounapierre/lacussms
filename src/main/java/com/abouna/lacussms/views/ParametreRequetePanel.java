/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.RequeteAgence;
import com.abouna.lacussms.entities.RequeteClient;
import com.abouna.lacussms.entities.RequeteCredit;
import com.abouna.lacussms.entities.RequeteEvenement;
import com.abouna.lacussms.entities.RequeteHistorique;
import com.abouna.lacussms.entities.RequeteMandat;
import com.abouna.lacussms.entities.RequeteSalaire1;
import com.abouna.lacussms.entities.RequeteSalaire2;
import com.abouna.lacussms.entities.RequeteSolde;
import com.abouna.lacussms.entities.TypeService;
import com.abouna.lacussms.main.App;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.main.MainMenuPanel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author SATELLITE
 */
public class ParametreRequetePanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private final JButton modifier;
    @Autowired
    private MainMenuPanel parentPanel;
    @Autowired
    private LacusSmsService serviceManager;

    public ParametreRequetePanel() throws IOException {
        serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        parentPanel = ApplicationConfig.getApplicationContext().getBean(MainMenuPanel.class);
        setLayout(new BorderLayout());
        JPanel haut = new JPanel();
        haut.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel lbl;
        lbl = new JLabel("CONFIGURATION DES PARAMETRES DES REQUETES DES SERVICES");
        haut.add(lbl);
        lbl.setFont(new Font("Broadway", Font.BOLD, 30));
        add(BorderLayout.BEFORE_FIRST_LINE, haut);
        JPanel contenu = new JPanel();
        contenu.setLayout(new BorderLayout());
        JPanel bas = new JPanel();
        bas.setLayout(new FlowLayout());
        Image modifImg = ImageIO.read(getClass().getResource("/images/OK.png"));
        modifier = new JButton(new ImageIcon(modifImg));
        modifier.setToolTipText("Configurer les services");
        modifier.addActionListener((ActionEvent ae) -> {
            int selected = table.getSelectedRow();
            if (selected >= 0) {
                String code = (String) tableModel.getValueAt(selected, 1);
                Nouveau nouveau1;
                try {
                    nouveau1 = new Nouveau(code);
                    nouveau1.setSize(500, 450);
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

        bas.add(modifier);

        contenu.add(BorderLayout.AFTER_LAST_LINE, bas);

        tableModel = new DefaultTableModel(new Object[]{"id", "Code", "Label"}, 0);

        table = new JTable(tableModel);
        table.setBackground(Color.WHITE);
        table.getColumnModel().getColumn(1).setPreferredWidth(350);
        contenu.add(BorderLayout.CENTER, new JScrollPane(table));
        add(BorderLayout.CENTER, contenu);
        int i = 1;
        List<Enum> list = Arrays.asList(TypeService.values());
        for (Enum e : list) {
            tableModel.addRow(new Object[]{
                i,
                e.name(),
                "TABLE ".concat(e.name().replace('_', ' ')),});
            i++;
        }
    }

    private class Nouveau extends JDialog {

        private final JButton okBtn, annulerBtn;
        private Map<String, JTextField> champs;
        private final Map<String, String> values = new HashMap<>();
        private Boolean ok = true;

        public Nouveau(String service) {
            Map<String, String> values1 = serviceManager.getParametreRequeteValues(getService(service));
            getMap(service);
            setTitle("CONFIGURATIONS " + service);
            setModal(true);
            setLayout(new BorderLayout(10, 10));
            JPanel haut = new JPanel();
            JLabel lbl;
            haut.add(lbl = new JLabel("<html><font color = #012345 > CONFIGURATION DE LA TABLE ET DES CHAMPS </font></html>"));
            lbl.setFont(new Font("Times New Roman", Font.ITALIC, 18));
            add(BorderLayout.BEFORE_FIRST_LINE, haut);
            DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 110dlu:", ""));
            builder.setDefaultDialogBorder();
            champs.forEach((k, v) -> {
                String val = values1.get(k);
                if (val != null) {
                    v.setText(val);
                }
                builder.append(k.replace('_', ' '), v);
            });

            JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Enregistrer"), annulerBtn = new JButton("Annuler"));
            builder.append(buttonBar, builder.getColumnCount());
            add(BorderLayout.CENTER, builder.getPanel());

            okBtn.addActionListener((ActionEvent ae) -> {
                champs.forEach((k, v) -> {
                    values.put(k, v.getText());
                    if ("".equals(v.getText())) {
                        ok = false;
                    }
                });
                if (!ok) {
                    JOptionPane.showMessageDialog(null, "Tous les champs sont obligatoires");
                    return;
                }
                serviceManager.enregistrerParametreRequete(values, getService(service));
                dispose();
                JOptionPane.showMessageDialog(null, "Enregister avec success");
            });

            annulerBtn.addActionListener((ActionEvent ae) -> {
                dispose();
            });
        }

        public final Map<String, JTextField> getMap(String service) {
            champs = new HashMap<>();
            getCodeAttribut(service).stream().forEach((ser) -> {
                champs.put(ser.name(), new JTextField(10));
            });
            return champs;
        }
    }

    /**
     *
     * @param service
     * @return
     */
    public List<Enum> getCodeAttribut(String service) {
        if (service.equals(TypeService.EVENEMENT.name())) {
            return Arrays.asList(RequeteEvenement.values());
        } else if (service.equals(TypeService.CREDIT.name())) {
            return Arrays.asList(RequeteCredit.values());
        } else if (service.equals(TypeService.MANDAT.name())) {
            return Arrays.asList(RequeteMandat.values());
        } else if (service.equals(TypeService.SOLDE.name())) {
            return Arrays.asList(RequeteSolde.values());
        } else if (service.equals(TypeService.SALAIRE1.name())) {
            return Arrays.asList(RequeteSalaire1.values());
        } else if (service.equals(TypeService.SALAIRE2.name())) {
            return Arrays.asList(RequeteSalaire2.values());
        } else if (service.equals(TypeService.AGENCE.name())) {
            return Arrays.asList(RequeteAgence.values());
        } else if (service.equals(TypeService.TELEPHONE_CLIENT.name())) {
            return Arrays.asList(RequeteClient.values());
        } else if (service.equals(TypeService.HISTORIQUE.name())) {
            return Arrays.asList(RequeteHistorique.values());
        }
        return null;
    }

    /**
     *
     * @param service
     * @return
     */
    public TypeService getService(String service) {
        if (TypeService.CREDIT.name().equals(service)) {
            return TypeService.CREDIT;
        } else if (TypeService.EVENEMENT.name().equals(service)) {
            return TypeService.EVENEMENT;
        } else if (TypeService.HISTORIQUE.name().equals(service)) {
            return TypeService.HISTORIQUE;
        } else if (TypeService.MANDAT.name().equals(service)) {
            return TypeService.MANDAT;
        } else if (TypeService.SALAIRE1.name().equals(service)) {
            return TypeService.SALAIRE1;
        } else if (TypeService.SOLDE.name().equals(service)) {
            return TypeService.SOLDE;
        } else if (TypeService.SALAIRE2.name().equals(service)) {
            return TypeService.SALAIRE2;
        } else if (TypeService.AGENCE.name().equals(service)) {
            return TypeService.AGENCE;
        } else if (TypeService.TELEPHONE_CLIENT.name().equals(service)) {
           return TypeService.TELEPHONE_CLIENT;
        }
        return null;
    }
}
