/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.tools.AES;
import com.abouna.lacussms.views.tools.ConstantUtils;
import com.abouna.lacussms.views.tools.MacAddressUtils;
import com.abouna.lacussms.views.tools.Utils;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 *
 * @author SATELLITE
 */
public class SendAddressPanel extends JDialog{
    private  JButton okBtn, annulerBtn;
    private  JTextField nameText;
    private final LacusSmsService lacusSmsService;
    
    public SendAddressPanel(){
        lacusSmsService = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        setTitle("GESTION DU POSTE CLIENT");
        setModal(true);
        setLayout(new BorderLayout(10, 10));
        JPanel haut = new JPanel();
        JLabel lbl;
        haut.add(lbl = new JLabel("<html><font color = #012345 > GESTION DU POSTE CLIENT </font></html>"));
        lbl.setFont(new Font("Times New Roman", Font.ITALIC, 18));
        add(BorderLayout.BEFORE_FIRST_LINE, haut);
        DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("right:max(40dlu;p), 12dlu, 180dlu:", ""));
        builder.setDefaultDialogBorder();
        builder.append("Code client:", nameText = new JTextField(50));
        nameText.setEnabled(false);
        JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(okBtn = new JButton("Envoyer"), annulerBtn = new JButton("Annuler"));
        builder.append(buttonBar, builder.getColumnCount());
        add(BorderLayout.CENTER, builder.getPanel());
         okBtn.setEnabled(false);
        String mac = MacAddressUtils.getMacAddress();
        if(mac!=null){
            try {   
                nameText.setText(AES.encrypt(mac, ConstantUtils.SECRET_KEY+Utils.getAppVersion()));
                okBtn.setEnabled(true);
            } catch (    IOException | XmlPullParserException ex) {
                Logger.getLogger(SendAddressPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        okBtn.addActionListener((ActionEvent e) -> {
            
        });
    }
}
