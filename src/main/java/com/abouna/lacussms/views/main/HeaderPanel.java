package com.abouna.lacussms.views.main;


import com.abouna.lacussms.main.App;
import com.abouna.lacussms.views.ContactPanel;
import com.abouna.lacussms.views.tools.Utils;
import org.jdesktop.swingx.JXButton;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.util.Objects;

/**
 *
 * @author Vincent Douwe <douwevincent@yahoo.fr>
 */
public  class HeaderPanel extends JPanel{
    private JButton runBtn;
    private JButton stopBtn;
    private JButton runParaBtn;
    
    public HeaderPanel() throws IOException{
        setLayout(new FlowLayout(FlowLayout.LEFT,10,10));        
        setBackground(new Color(166, 202, 240));
        setBorder(BorderFactory.createRaisedSoftBevelBorder());
        JPanel pan = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        pan.setBorder(new EmptyBorder(0, 0, 0, 10));
        setPreferredSize(new Dimension(400,65));
        init();
    }
    
    protected final void init() throws IOException{
       ImageIcon img1 = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/adherent.jpg"))));
       ImageIcon img2 = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/icona.jpg"))));
       ImageIcon img3 = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/run.JPG"))));
       ImageIcon img4 = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/stop.PNG"))));
       ImageIcon img5 = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/play.JPG"))));
        JButton adBtn = new JXButton(img1);
        adBtn.setPreferredSize(new Dimension(40, 40));
        JButton cotBtn = new JXButton(img2);
        cotBtn.setPreferredSize(new Dimension(40, 40));
        runBtn = new JXButton(img3);
        runBtn.setPreferredSize(new Dimension(40, 40));
        stopBtn = new JXButton(img4);
        stopBtn.setPreferredSize(new Dimension(40, 40));
        stopBtn.setEnabled(false);
        runBtn.setToolTipText("Démarrer le service SMS Séquentiel");
        stopBtn.setToolTipText("Arrêter le service SMS");
        runParaBtn = new JXButton(img5);
        runParaBtn.setPreferredSize(new Dimension(40, 40));
        runParaBtn.setToolTipText("Démarrer le service SMS Parallèle");
        add(adBtn);
        add(cotBtn);
        add(runBtn);
        add(runParaBtn);
        add(stopBtn);
        //runParaBtn.setEnabled(false);
        
        runBtn.addActionListener((ActionEvent e) -> {
            Connection connection = Utils.testConnexion(App.SECRET);
            if (connection != null) {
                App.setConnexion(connection);
                App.demarrerServiceSequenciel();
                //App.demarrerServiceRequete();
                stopBtn.setEnabled(true);
                runBtn.setEnabled(false);
                runParaBtn.setEnabled(false);
                JOptionPane.showMessageDialog(HeaderPanel.this.getParent(), "Le service a démarré avec succès");
            } else {
                JOptionPane.showMessageDialog(HeaderPanel.this.getParent(), "Erreur lors du démarrage du service, la connexion à la base de donnees n'est pas prête!!");
            }
       });       
         runParaBtn.addActionListener((ActionEvent e) -> {
             Connection connection = Utils.testConnexion(App.SECRET);
             if(connection != null){
                 App.setConnexion(connection);
                 App.demarrerServiceData();
                 App.demarrerServiceSms();
                 //App.demarrerServiceRequete();
                 stopBtn.setEnabled(true);
                 runBtn.setEnabled(false);
                 runParaBtn.setEnabled(false);
                 JOptionPane.showMessageDialog(HeaderPanel.this.getParent(), "Le service a démarré avec succès");
             }else{
                 JOptionPane.showMessageDialog(HeaderPanel.this.getParent(), "Erreur lors du démarrage du service, la connexion à la base de donnees n'est pas prête!!");
             }
       });
       
        stopBtn.addActionListener((ActionEvent e) -> {
            App.stopper();
            stopBtn.setEnabled(false);
            runBtn.setEnabled(true);
            runParaBtn.setEnabled(true);
            JOptionPane.showMessageDialog(HeaderPanel.this.getParent(), "Le service a été arrêté");
       });      
        cotBtn.addActionListener((ActionEvent e) -> {
            JDialog contact = new ContactPanel();
            contact.setSize(400, 200);
            contact.setLocationRelativeTo(null);
            contact.setModal(true);
            contact.setResizable(false);
            contact.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            contact.setVisible(true);
       });
    }
}
