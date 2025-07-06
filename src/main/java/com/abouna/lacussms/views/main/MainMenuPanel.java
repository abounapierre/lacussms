package com.abouna.lacussms.views.main;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.views.*;
import com.abouna.lacussms.views.groupe.GroupeClientPanel;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Emmanuel ABOUNA <abouna.emmanuel@yahoo.fr> copy right 2017
 */
@Component
public class MainMenuPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(MainMenuPanel.class);
    private final ContainerPanel container;

    public MainMenuPanel() throws IOException {
        setLayout(new BorderLayout());

        JXHyperlink formatMsgBtn = createLink("Formats SMS", MessageFormatPanel.class);
        JXHyperlink cliBtn = createLink("Clients", BkCliPanel.class);
        JXHyperlink grpCliBtn = createLink("Groupes clients", GroupeClientPanel.class);
        JXHyperlink opeBtn = createLink("Opérations", BkOpePanel.class);
        JXHyperlink eveBtn = createLink("Évènements", BkEvePanel.class);
        JXHyperlink remoteBdBtn = createLink("BD CBS", RemoteDBPanel.class);
        JXHyperlink userBtn = createLink("Utilisateur", MessageFormatPanel.class);
        JXHyperlink rapportBtn = createLink("Messages", RapportPanel.class);
        JXHyperlink agenceBtn = createLink("Agences", BkAgencePanel.class);
        JXHyperlink etatParam = createLink("Etat OP", EtatOPParamPanel.class);
        JXHyperlink gescom = createLink("Comptes clients", BkCompCliPanel.class);
        JXHyperlink accueilBtn = createLink("Accueil", HomePanel.class);
        JXHyperlink licenceBtn = createLink("Licence", null);
        JXHyperlink mandatBtn = createLink("Mandats", MandatPanel.class);


        licenceBtn.addActionListener((ActionEvent ae) -> {
            getLicencePanel();
        });

        JXTaskPane donneesPane = createTaskPane("Configurations");
        donneesPane.add(accueilBtn);
        donneesPane.add(agenceBtn);
        donneesPane.add(cliBtn);
        donneesPane.add(gescom);
        donneesPane.add(grpCliBtn);
        //donneesPane.add(etatParam);
        donneesPane.add(opeBtn);
        donneesPane.add(formatMsgBtn);
        donneesPane.add(remoteBdBtn);
        JXTaskPane comptePane = createTaskPane("Profil");
        comptePane.setTitle("Profil");
        comptePane.add(userBtn);

        JXTaskPane rapportPane = createTaskPane("Gestion");
        rapportPane.add(eveBtn);
        rapportPane.add(rapportBtn);
        rapportPane.add(mandatBtn);

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

        container = new ContainerPanel();
        container.setLayout(new BorderLayout());
        container.setBorder(defautBorder);
        setBackground(Color.red);
        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jScrollPane, container);
        jSplitPane.setResizeWeight(0.2);
        jSplitPane.setDividerLocation(.2);
        add(jSplitPane, BorderLayout.CENTER);
    }

    public static MainMenuPanel getInstance() {
        return ApplicationConfig.getApplicationContext().getBean(MainMenuPanel.class);
    }

    public void setContent(JPanel pan) {
        //stopTimer(pan);
        container.removeAll();
        container.add(pan);
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
                    logger.error("Erreur: ", e);
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
