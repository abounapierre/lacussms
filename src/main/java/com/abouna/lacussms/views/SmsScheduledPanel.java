/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.dto.SmsScheduledDto;
import com.abouna.lacussms.entities.MessageFormat;
import com.abouna.lacussms.entities.SmsScheduled;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.service.impl.SchedulerSmsService;
import com.abouna.lacussms.views.main.MainMenuPanel;
import com.abouna.lacussms.views.utils.CheckListItem;
import com.abouna.lacussms.views.utils.CheckListRenderer;
import com.abouna.lacussms.views.utils.WordWrapCellRenderer;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXSearchField;
import org.quartz.SchedulerException;

/**
 *
 * @author SATELLITE
 */
public class SmsScheduledPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JButton nouveau, modifier, supprimer, ajoutCompte;
    private MainMenuPanel parentPanel;
    private LacusSmsService serviceManager;
    private SchedulerSmsService schedulerSmsService;

    public SmsScheduledPanel() {
        try {
            serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
            parentPanel = ApplicationConfig.getApplicationContext().getBean(MainMenuPanel.class);
            schedulerSmsService = ApplicationConfig.getApplicationContext().getBean(SchedulerSmsService.class);
            setLayout(new BorderLayout());
            JPanel haut = new JPanel();
            haut.setLayout(new FlowLayout(FlowLayout.CENTER));
            JLabel lbl;
            lbl = new JLabel("PROGRAMMATION DES SMS");
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
            nouveau.setToolTipText("Programmer l'envoie des messages");
            supprimer = new JButton(new ImageIcon(supprImg));
            supprimer.setToolTipText("Suprimer un programme");
            modifier = new JButton(new ImageIcon(modifImg));
            modifier.setToolTipText("Modifier un programme");
            ajoutCompte = new JButton("Associer Compte");
            ajoutCompte.setToolTipText("Associer les numéros de comptes");
            nouveau.addActionListener((ActionEvent ae) -> {
                Nouveau nouveau1 = new Nouveau(null);
                nouveau1.setSize(600, 350);
                nouveau1.setLocationRelativeTo(null);
                nouveau1.setModal(true);
                nouveau1.setResizable(false);
                nouveau1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                nouveau1.setVisible(true);
            });
            modifier.addActionListener((ActionEvent ae) -> {
                int selected = table.getSelectedRow();
                if (selected >= 0) {
                    Long id = (Long) tableModel.getValueAt(selected, 0);
                    Nouveau nouveau1;
                    try {
                        nouveau1 = new Nouveau(serviceManager.getSmsScheduledDtoById(id));
                        nouveau1.setSize(600, 350);
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
            supprimer.addActionListener((ActionEvent ae) -> {
                int selected = table.getSelectedRow();
                if (selected >= 0) {
                    Integer id = (Integer) tableModel.getValueAt(selected, 0);
                    int res = JOptionPane.showConfirmDialog(null, "Etes vous sûr de suppimer ce programme?", "Confirmation",
                            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    if (res == JOptionPane.YES_OPTION) {
                        try {
                            serviceManager.supprimerUrlMessage(id);
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
            bas.add(ajoutCompte);
            JPanel filtrePanel = new JPanel();
            filtrePanel.setLayout(new FlowLayout());
            final JXSearchField searchField = new JXSearchField("Rechercher");
            searchField.setPreferredSize(new Dimension(500, 50));
            filtrePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), "Zone de recherche"));
            filtrePanel.add(searchField);
            filtrePanel.setBackground(new Color(166, 202, 240));
            searchField.addActionListener((ActionEvent e) -> {
                String val = searchField.getText();
                if (val != null) {

                }
            });
            ajoutCompte.addActionListener((ActionEvent ae) -> {
                int selected = table.getSelectedRow();
                if (selected >= 0) {

                } else {
                    JOptionPane.showMessageDialog(null, "Aucun élément selectionné");
                }
            });
            contenu.add(BorderLayout.AFTER_LAST_LINE, bas);
            contenu.add(BorderLayout.BEFORE_FIRST_LINE, filtrePanel);
            tableModel = new DefaultTableModel(new Object[]{"id", "Date", "Jour", "Description", "Heure", "Type"}, 0);
            table = new JTable(tableModel);
            table.setBackground(Color.WHITE);
            table.getColumnModel().getColumn(3).setPreferredWidth(350);
            //table.getColumnModel().getColumn(3).setCellRenderer(new WordWrapCellRenderer());
            //table.removeColumn(table.getColumnModel().getColumn(0));
            contenu.add(BorderLayout.CENTER, new JScrollPane(table));
            add(BorderLayout.CENTER, contenu);
            try {
                List<SmsScheduledDto> list = serviceManager.getAllSmsScheduledDtos();
                list.stream().forEach((dto) -> {
                    tableModel.addRow(new Object[]{
                        dto.getSmsScheduled().getId(),
                        dto.getSmsScheduled().getDayOfMonth(),
                        dto.getSmsScheduled().getDayOfWeek(),
                        dto.getSmsScheduled().getDescription(),
                        dto.getSmsScheduled().getHeure(),
                        dto.getSmsScheduled().getType()
                    });
                });
            } catch (Exception ex) {
                Logger.getLogger(MessageFormatPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(SmsScheduledPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class Nouveau extends JDialog {

        private final JSpinner spinner;
        private final JComboBox<Integer> dayOfMonthBox;
        private final JComboBox<Map.Entry<Integer, String>> dayOfWeekBox;
        private final JComboBox<String> typeBox;
        private final JButton okBtn;
        private final JButton annulerBtn;
        private final JList liste;
        private final Map<Integer, MessageFormat> formatMap = new HashMap<>();
        private final JTextField descriptiontext;

        public Nouveau(SmsScheduledDto smsScheduled) {
            setTitle("PROGRAMMATION DES SMS");
            setModal(true);
            setLayout(new BorderLayout(10, 10));
            JPanel haut = new JPanel();
            JLabel lbl;
            haut.add(lbl = new JLabel("<html><font color = #012345 > PROGRAMMER UN ENVOI SMS </font></html>"));
            lbl.setFont(new Font("Times New Roman", Font.ITALIC, 18));
            add(BorderLayout.BEFORE_FIRST_LINE, haut);
            DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 180dlu:", ""));
            builder.setDefaultDialogBorder();
            SpinnerDateModel model = new SpinnerDateModel();
            DefaultListModel dlm = new DefaultListModel();
            liste = new JList(dlm);
            model.setCalendarField(Calendar.MINUTE);
            builder.append("Heure:", spinner = new JSpinner());
            builder.append("Type:", typeBox = new JComboBox<>());
            builder.append("Jour du mois:", dayOfMonthBox = new JComboBox<>());
            builder.append("Jour de la semaine:", dayOfWeekBox = new JComboBox<>());
            builder.append("Description:", descriptiontext = new JTextField());
            builder.append("Format SMS:", new JScrollPane(liste));
            liste.setCellRenderer(new CheckListRenderer());
            liste.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            liste.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    JList list = (JList) event.getSource();
                    int index = list.locationToIndex(event.getPoint());// Get index of item
                    // clicked
                    CheckListItem item = (CheckListItem) list.getModel()
                            .getElementAt(index);
                    item.setSelected(!item.isSelected()); // Toggle selected state
                    list.repaint(list.getCellBounds(index, index));// Repaint cell
                }
            });
            spinner.setModel(model);
            JSpinner.DateEditor de = new JSpinner.DateEditor(spinner, "HH:mm");
            spinner.setEditor(de);
            if (smsScheduled != null) {
                List<MessageFormat> messageFormats = smsScheduled.getMessageFormats();
                messageFormats.stream().forEach((msgFormat) -> {
                    formatMap.put(msgFormat.getId(), msgFormat);
                });
            }
            for (int i = 1; i <= 31; i++) {
                dayOfMonthBox.addItem(i);
            }
            getMapOfDay().entrySet().stream().forEach((entry) -> {
                dayOfWeekBox.addItem(entry);
            });
            serviceManager.getAll().stream().forEach((format) -> {
                CheckListItem item = new CheckListItem(format.getName(),format.getId()); 
                item.setSelected(formatMap.get(format.getId())!=null);
                dlm.addElement(item);
                
            });
            getTypes().stream().forEach((type) -> {
                typeBox.addItem(type);
            });
            
            JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Enrégistrer"), annulerBtn = new JButton("Annuler"));
            builder.append(buttonBar, builder.getColumnCount());
            add(BorderLayout.CENTER, new JScrollPane(builder.getPanel()));
            okBtn.addActionListener((ActionEvent e) -> {
                try {
                    SmsScheduled smsScheduled1 = new SmsScheduled();
                    Date date = new Date();
                    LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
                    smsScheduled1.setDateTime(ldt);
                    smsScheduled1.setTimeZone(ZoneId.systemDefault());
                    System.out.println(TimeZone.getDefault());
                    SmsScheduledDto smsScheduledDto = new SmsScheduledDto();
                    smsScheduledDto.setMessageFormats(new ArrayList<>());
                    Date val = (Date) spinner.getValue();
                    if (val != null) {
                        smsScheduled1.setHeure(de.getFormat().format(val));
                    }
                    smsScheduled1.setDayOfMonth((Integer) dayOfMonthBox.getSelectedItem());
                    Map.Entry<Integer, String> entry = (Map.Entry<Integer, String>) dayOfWeekBox.getSelectedItem();
                    smsScheduled1.setDayOfWeek(entry.getKey());
                    smsScheduled1.setType((String) typeBox.getSelectedItem());
                    if(!descriptiontext.getText().isEmpty()){
                        smsScheduled1.setDescription(descriptiontext.getText());
                    }
                    smsScheduledDto.setSmsScheduled(smsScheduled1);
                   
                    for (int i = 0; i < liste.getModel().getSize(); i++) {
                        CheckListItem item = (CheckListItem) liste.getModel().getElementAt(i);
                        if(item.isSelected()){
                          smsScheduledDto.getMessageFormats().add(serviceManager.getFormatById(item.getId()));  
                        }
                    }
                    
                    if(smsScheduledDto.getSmsScheduled().getId()!=null){
                        smsScheduledDto.setAction("create");
                    }else{
                        smsScheduledDto.setAction("update");
                    }
                    Map<String, String> result;
                    Long id = serviceManager.saveSmsScheduled(smsScheduledDto);
                    if (id != null) {
                        smsScheduledDto.getSmsScheduled().setId(id);
                        result = schedulerSmsService.scheduledSms(smsScheduledDto);
                        dispose();
                        parentPanel.setContenu(new SmsScheduledPanel());
                        String msg;
                        if (result != null) {
                            String key = result.get("jobId");
                            String name = result.get("jobName");
                            if (key != null && name != null) {
                                smsScheduledDto.getSmsScheduled().setQuartzId(key);
                                smsScheduledDto.setAction("update");
                                serviceManager.updateQuartzId(smsScheduledDto.getSmsScheduled());
                                msg = "Clé=" + key + " JobName=" + name + " SMS Programmé avec Success!";
                                JOptionPane.showMessageDialog(parentPanel, msg);
                            }

                        }
                    }
                } catch (SchedulerException ex) {
                    Logger.getLogger(SmsScheduledPanel.class.getName()).log(Level.SEVERE, null, ex);
                }

            });
            annulerBtn.addActionListener((ActionEvent e) -> {
                dispose();
                parentPanel.setContenu(SmsScheduledPanel.this);
            });

        }

        public final Integer getPosition(MessageFormat messageFormat) {
            int i = 0;
            Integer result = null;
            for (MessageFormat msg : serviceManager.getAll()) {
                if (messageFormat.getId().equals(msg.getId())) {
                    result = i;
                }
                i++;
            }
            return result;
        }
    }

    public List<String> getTypes() {
        List<String> types = new ArrayList<>();
        types.add("daily");
        types.add("monthly");
        types.add("weekly");
        return types;
    }

    public static Map<Integer, String> getMapOfDay() {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "LUNDI");
        map.put(2, "MARDI");
        map.put(3, "MERCREDI");
        map.put(4, "JEUDI");
        map.put(5, "VENDREDI");
        map.put(6, "SAMEDI");
        map.put(7, "DIMANCHE");
        return map;
    }
}
