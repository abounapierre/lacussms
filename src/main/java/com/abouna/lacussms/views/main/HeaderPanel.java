package com.abouna.lacussms.views.main;


import com.abouna.lacussms.config.AppRunConfig;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.task.StartService;
import com.abouna.lacussms.views.ContactPanel;
import com.abouna.lacussms.views.sms.groupe.GroupeSMSJDialog;
import com.abouna.lacussms.views.components.LacusIcon;
import com.abouna.lacussms.views.tools.GeneratePDF;
import com.abouna.lacussms.views.tools.TestSMSPanel;
import org.jdesktop.swingx.JXButton;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Objects;

import static com.abouna.lacussms.views.tools.ConstantUtils.ICON_PDF_REPORT;
import static com.abouna.lacussms.views.tools.ConstantUtils.ICON_SEND_SMS;

/**
 *
 * @author ebaouna@gmail.com
 */
public  class HeaderPanel extends JPanel{
    private JButton runBtn;
    private JButton stopBtn;
    private JButton runParaBtn;
    private JCheckBox serviceDataCheckBox;
    private JCheckBox serviceSmsCheckBox;
    private JCheckBox testModeCheckBox;

    public HeaderPanel(LacusSmsService service) throws IOException{
        setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
        setBackground(new Color(166, 202, 240));
        setBorder(BorderFactory.createRaisedSoftBevelBorder());
        JPanel pan = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        pan.setBorder(new EmptyBorder(0, 0, 0, 10));
        setPreferredSize(new Dimension(400,65));
        init(service);
    }
    
    protected final void init(LacusSmsService service) throws IOException{
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
        add(runParaBtn);
        add(stopBtn);

        add(getDataCheckBox());
        add(getSmsCheckBox());
        add(getTestCheckBox());
        add(getTestSMSButton());
        add(getGroupeSMSButton());
        add(getPDFButton());

        runBtn.addActionListener((ActionEvent e) -> startAll());
        runParaBtn.addActionListener((ActionEvent e) -> startAll());
        stopBtn.addActionListener((ActionEvent e) -> stopAll());

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

    private JButton getGroupeSMSButton() {
        JButton groupeSMSButton = new JButton(new LacusIcon(ICON_SEND_SMS));
        groupeSMSButton.setPreferredSize(new Dimension(30, 30));
        groupeSMSButton.addActionListener(e -> GroupeSMSJDialog.initDialog());
        return groupeSMSButton;
    }

    private JButton getTestSMSButton() {
        JButton testSMSButton = new JButton("Test SMS");
        testSMSButton.setPreferredSize(new Dimension(100, 30));
        testSMSButton.addActionListener(e -> TestSMSPanel.init());
        return testSMSButton;
    }

    private JCheckBox getTestCheckBox() {
        testModeCheckBox = new JCheckBox("Test Mode");
        testModeCheckBox.setSelected(true);
        testModeCheckBox.setEnabled(true);
        testModeCheckBox.addActionListener((ActionEvent e) -> setTestMode(testModeCheckBox));
        return testModeCheckBox;
    }

    private JCheckBox getSmsCheckBox() {
        serviceSmsCheckBox = new JCheckBox("Service SMS");
        serviceSmsCheckBox.setSelected(true);
        serviceSmsCheckBox.setEnabled(true);
        serviceSmsCheckBox.addActionListener((ActionEvent e) -> getSetSmsServiceEnabled(serviceSmsCheckBox));
        return serviceSmsCheckBox;
    }

    private JCheckBox getDataCheckBox() {
        serviceDataCheckBox = new JCheckBox("Service Data");
        serviceDataCheckBox.setSelected(true);
        serviceDataCheckBox.setEnabled(true);
        serviceDataCheckBox.addActionListener((ActionEvent e) -> getSetDataServiceEnabled(serviceDataCheckBox));
        return serviceDataCheckBox;
    }

    private JButton getPDFButton() {
        JButton pdfButton = new JButton(new LacusIcon(ICON_PDF_REPORT));
        pdfButton.setPreferredSize(new Dimension(30, 30));
        pdfButton.addActionListener(e -> GeneratePDF.init());
        return pdfButton;
    }

    private void getSetDataServiceEnabled(JCheckBox serviceDataCheckBox) {
        AppRunConfig config = AppRunConfig.getInstance();
        config.setDataServiceEnabled(serviceDataCheckBox.isSelected());
    }

    private void getSetSmsServiceEnabled(JCheckBox serviceSMSCheckBox) {
        AppRunConfig config = AppRunConfig.getInstance();
        config.setMessageServiceEnabled(serviceSMSCheckBox.isSelected());
    }

    private void setTestMode(JCheckBox testMode) {
        AppRunConfig config = AppRunConfig.getInstance();
        config.setTestModeEnabled(testMode.isSelected());
    }

    private void startAll() {
        stopBtn.setEnabled(true);
        runBtn.setEnabled(false);
        runParaBtn.setEnabled(false);
        testModeCheckBox.setEnabled(false);
        serviceDataCheckBox.setEnabled(false);
        serviceSmsCheckBox.setEnabled(false);
        JOptionPane.showMessageDialog(HeaderPanel.this.getParent(), "Le service a démarré avec succès");
        StartService.startSequential();
    }

    private void stopAll() {
        stopBtn.setEnabled(false);
        runBtn.setEnabled(true);
        runParaBtn.setEnabled(true);
        testModeCheckBox.setEnabled(true);
        serviceDataCheckBox.setEnabled(true);
        serviceSmsCheckBox.setEnabled(true);
        JOptionPane.showMessageDialog(HeaderPanel.this.getParent(), "Le service a été arrêté");
        StartService.stopper();
    }
}
