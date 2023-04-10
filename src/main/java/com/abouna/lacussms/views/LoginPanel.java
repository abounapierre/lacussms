/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.views;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.main.HeaderMenu;
import com.abouna.lacussms.views.main.MainFrame;
import com.abouna.lacussms.views.main.MainMenuPanel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SATELLITE
 */
public class LoginPanel extends JPanel{

    private final LacusSmsService service;

    public LoginPanel(LacusSmsService service){
        this.service = service;
        setLayout(new BorderLayout(10,10));
        JPanel loginPanel = new JPanel(new FlowLayout());
        //loginPanel.setBorder(new EmptyBorder(180, 350, 50, 300));
        loginPanel.setBackground(new Color(166, 202, 240));
        DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("l:p, 12dlu, 100dlu", ""));
        builder.setBackground(new Color(166, 202, 240));
        JTextField loginText;
        builder.append("Nom d'utilisateur", loginText = new JTextField());
        JPasswordField passwdText;
        builder.append("Mot de passe", passwdText = new JPasswordField());
        JButton btnLogin;
        JPanel buttonBar = ButtonBarFactory.buildOKCancelBar(btnLogin = new JButton("Connexion"),new JButton("Annuler"));
        builder.append(buttonBar,builder.getColumnCount());
        loginPanel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
        loginPanel.add(BorderLayout.CENTER, builder.getPanel());
        add(BorderLayout.CENTER,loginPanel);
        btnLogin.addActionListener(e -> {
            MainFrame frame = ApplicationConfig.getApplicationContext().getBean(MainFrame.class);
            frame.setJMenuBar(ApplicationConfig.getApplicationContext().getBean(HeaderMenu.class));
            frame.setContent(ApplicationConfig.getApplicationContext().getBean(MainMenuPanel.class));
            frame.validate();
        });
    }

    
    /* @Override
    protected void paintComponent(Graphics g) {
        try {
            Image img = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/smile.png")));
            Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                          RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        super.paintComponent(g);
        //g.drawImage(img, 0, 0,getWidth(),getHeight(),null);
        g.setColor(new Color(166, 202, 240));
       //g.drawString("SUIVI DES INDICATEURS",310,80);
        } catch (IOException ex) {
            Logger.getLogger(LoginPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
}
