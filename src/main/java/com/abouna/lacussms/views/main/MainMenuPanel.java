package com.abouna.lacussms.views.main;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.views.*;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Emmanuel ABOUNA <abouna.emmanuel@yahoo.fr> copy right 2017
 * @author Vincent Douwe <douwevincent@yahoo.fr>
 */
@Component
public class MainMenuPanel extends JPanel {



    private final JPanel container;

    public MainMenuPanel() throws IOException {
        setLayout(new BorderLayout());

        JXHyperlink  typeMsgBtn = createLink("Types SMS",MessageFormatPanel.class);
        JXHyperlink formatMsgBtn = createLink("Format SMS", MessageFormatPanel.class);
        JXHyperlink cliBtn = createLink("Client", BkCliPanel.class);
        JXHyperlink opeBtn = createLink("Opération", BkOpePanel.class);
        JXHyperlink eveBtn = createLink("Evenement", BkEvePanel.class);
        JXHyperlink remoteBdBtn = createLink("BD CBS", RemoteDBPanel.class);
        JXHyperlink employeeBtn = createLink("Employés", MessageFormatPanel.class);
        JXHyperlink userBtn = createLink("Utilisateur", MessageFormatPanel.class);
        JXHyperlink rapportTypeBtn = createLink("Types Rapports", MessageFormatPanel.class);
        JXHyperlink rapportBtn = createLink("Rapports", RapportPanel.class);
        JXHyperlink agenceBtn = createLink("Agence", BkAgencePanel.class);
        JXHyperlink urlParam = createLink("Param Url", UrlParamPanel.class);
        JXHyperlink etatParam = createLink("Etat OP", EtatOPParamPanel.class);
        JXHyperlink gescom = createLink("Gest. Compte", BkCompCliPanel.class);
        JXHyperlink mandat = createLink("Mandats", BkMadPanel.class);
        JXHyperlink rapportMandatBtn = createLink("Rapports Mandats", MessageMandatPanel.class);
        JXHyperlink commandBtn = createLink("Requêtes", CommandPanel.class);
        JXHyperlink serviceBtn = createLink("Services", ServiceOffertPanel.class);
        //JXHyperlink programBtn = createLink("Prog. Mail");
        //JXHyperlink programSmsBtn = createLink("Prog. SMS");
        JXHyperlink configReqBtn = createLink("Conf. req.", ParametreRequetePanel.class);
        JXHyperlink accueilBtn = createLink("Accueil", HomePanel.class);
        JXHyperlink paramTblBtn = createLink("Param Table", MessageFormatPanel.class);
        JXHyperlink licenceBtn = createLink("Licence", null);

        licenceBtn.addActionListener((ActionEvent ae) -> {
            getLicencePanel();
        });

        JXHyperlink envoieMsgBtn = createLink("SMS PUSH", null);
        envoieMsgBtn.addActionListener((ActionEvent ae) -> {
            EnvoieSMSDialog nouveau1 = new EnvoieSMSDialog();
            nouveau1.setSize(500, 300);
            nouveau1.setLocationRelativeTo(null);
            nouveau1.setModal(true);
            nouveau1.setResizable(false);
            nouveau1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            nouveau1.setVisible(true);
        });

        JXHyperlink holidayBtn = createLink("Jour ferié", null);
        holidayBtn.addActionListener((ActionEvent ae) -> {
            HolidayDialog nouveau1 = new HolidayDialog();
            nouveau1.setSize(600, 400);
            nouveau1.setLocationRelativeTo(null);
            nouveau1.setModal(true);
            nouveau1.setResizable(false);
            nouveau1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            nouveau1.setVisible(true);
        });

        JXTaskPane donneesPane = createTaskPane("Configurations");
        donneesPane.add(accueilBtn);
        donneesPane.add(agenceBtn);
        donneesPane.add(cliBtn);
        donneesPane.add(gescom);
        donneesPane.add(etatParam);
        donneesPane.add(opeBtn);
        donneesPane.add(serviceBtn);
        donneesPane.add(formatMsgBtn);
        donneesPane.add(eveBtn);
        //donneesPane.add(holidayBtn);
        //donneesPane.add(programBtn);
        //donneesPane.add(programSmsBtn);
        donneesPane.add(envoieMsgBtn);
        donneesPane.add(remoteBdBtn);
        donneesPane.add(configReqBtn);
        donneesPane.add(urlParam);

        JXTaskPane comptePane = createTaskPane("Profil");
        //donneesPane.add(typeMsgBtn);
        comptePane.setTitle("Profil");
        comptePane.add(licenceBtn);
        comptePane.add(userBtn);

        JXTaskPane rapportPane = createTaskPane("Rapports");
        //rapportPane.add(rapportTypeBtn);
        rapportPane.add(rapportBtn);
        rapportPane.add(commandBtn);
        //rapportPane.add(rapportMandatBtn);

        /* creation du menu */
        JXTaskPaneContainer menu = new JXTaskPaneContainer();
        menu.setEnabled(false);
        menu.add(donneesPane);
        menu.add(comptePane);
        menu.add(rapportPane);

        menu.setBackground(new Color(166, 202, 240));
        Border raisedBevelBorder = BorderFactory.createRaisedSoftBevelBorder();
        Border defautBorder = BorderFactory.createCompoundBorder(raisedBevelBorder, raisedBevelBorder);
        menu.setBorder(defautBorder);
        JScrollPane jScrollPane = new JScrollPane(menu);

        container = new JPanel();
        container.setLayout(new BorderLayout());
        container.setBorder(defautBorder);
        setBackground(Color.red);
        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jScrollPane, container);
        jSplitPane.setResizeWeight(0.2);
        jSplitPane.setDividerLocation(.2);
        add(jSplitPane, BorderLayout.CENTER);
    }

    public void setContent(JPanel pan) {
        container.removeAll();
        container.add(BorderLayout.CENTER, pan);
        container.validate();
    }

    private <T  extends JPanel> JXHyperlink createLink(String text, Class<T>  panelClass) {
        JXHyperlink b = new JXHyperlink();
        Color textColor = new Color(16, 66, 104);
        b.setUnclickedColor(textColor);
        b.setClickedColor(textColor);
        b.setFocusable(false);
        b.setText(text);
        b.setBackground(new Color(166, 202, 240));
        if(panelClass != null) {
            b.addActionListener((ActionEvent ae) -> {
                try {
                    setContent(panelClass.getDeclaredConstructor().newInstance());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    JOptionPane.showMessageDialog(MainMenuPanel.this.getParent(), "Erreur lors de la création du panneau");
                }
            });
        }
        return b;
    }

    private void getLicencePanel() {
        LicencePanel nouveau1 = new LicencePanel(null);
        nouveau1.setSize(450, 150);
        nouveau1.setLocationRelativeTo(null);
        nouveau1.setModal(true);
        nouveau1.setResizable(false);
        nouveau1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        nouveau1.setVisible(true);
    }

    private JXTaskPane createTaskPane(String title) {
        JXTaskPane task = new JXTaskPane() {
            @Override
            public boolean isVisible() {
                return true;
            }
        };
        task.setCollapsed(true);
        task.setTitle(title);
        return task;
    }

}
