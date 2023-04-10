package com.abouna.lacussms.views.main;

import com.abouna.lacussms.views.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Emmanuel ABOUNA <abouna.emmanuel@yahoo.fr> copy right 2017
 * @author Vincent Douwe <douwevincent@yahoo.fr>
 */
@Component
public class MainMenuPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(MainMenuPanel.class);
    private final ContainerPanel container;

    private final Map<JXTaskPane, List<JXHyperlink>> componentMap;

    public MainMenuPanel() throws IOException {
        setLayout(new BorderLayout());
        componentMap = new HashMap<>();

        JXTaskPane donneesPane = createTaskPane("Configurations");
        JXTaskPane comptePane = createTaskPane("Profil");
        JXTaskPane rapportPane = createTaskPane("Rapports");

        JXHyperlink formatMsgBtn = createLink("Format SMS", MessageFormatPanel.class, donneesPane);
        JXHyperlink cliBtn = createLink("Client", BkCliPanel.class, donneesPane);
        JXHyperlink opeBtn = createLink("Opération", BkOpePanel.class, donneesPane);
        JXHyperlink eveBtn = createLink("Evenement", BkEvePanel.class, donneesPane);
        JXHyperlink remoteBdBtn = createLink("BD CBS", RemoteDBPanel.class, donneesPane);
        JXHyperlink agenceBtn = createLink("Agence", BkAgencePanel.class, donneesPane);
        JXHyperlink urlParam = createLink("Param Url", UrlParamPanel.class, donneesPane);
        JXHyperlink etatParam = createLink("Etat OP", EtatOPParamPanel.class, donneesPane);
        JXHyperlink gescom = createLink("Gest. Compte", BkCompCliPanel.class, donneesPane);

        JXHyperlink serviceBtn = createLink("Services", ServiceOffertPanel.class, donneesPane);
        JXHyperlink programSmsBtn = createLink("Prog. SMS", SmsProgrammingPanel.class, donneesPane);
        JXHyperlink configReqBtn = createLink("Conf. req.", ParametreRequetePanel.class, donneesPane);
        JXHyperlink accueilBtn = createLink("Accueil", HomePanel.class, donneesPane);

        JXHyperlink commandBtn = createLink("Requêtes", CommandPanel.class, rapportPane);
        JXHyperlink rapportBtn = createLink("Rapports", RapportPanel.class, rapportPane);

        JXHyperlink userBtn = createLink("Utilisateur", MessageFormatPanel.class, comptePane);
        JXHyperlink licenceBtn = createLink("Licence", null, comptePane);

        licenceBtn.addActionListener((ActionEvent ae) -> getLicencePanel());

        JXHyperlink envoieMsgBtn = createLink("SMS PUSH", null, donneesPane);
        envoieMsgBtn.addActionListener((ActionEvent ae) -> {
            EnvoieSMSDialog nouveau1 = new EnvoieSMSDialog();
            nouveau1.setSize(500, 300);
            nouveau1.setLocationRelativeTo(null);
            nouveau1.setModal(true);
            nouveau1.setResizable(false);
            nouveau1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            nouveau1.setVisible(true);
        });

        /*JXHyperlink holidayBtn = createLink("Jour ferié", null);
        holidayBtn.addActionListener((ActionEvent ae) -> {
            HolidayDialog nouveau1 = new HolidayDialog();
            nouveau1.setSize(600, 400);
            nouveau1.setLocationRelativeTo(null);
            nouveau1.setModal(true);
            nouveau1.setResizable(false);
            nouveau1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            nouveau1.setVisible(true);
        });*/


        add(donneesPane, accueilBtn, componentMap);
        add(donneesPane, agenceBtn, componentMap);
        add(donneesPane, cliBtn, componentMap);
        add(donneesPane, gescom, componentMap);
        add(donneesPane, etatParam, componentMap);
        add(donneesPane, opeBtn, componentMap);
        add(donneesPane, serviceBtn, componentMap);
        add(donneesPane, formatMsgBtn, componentMap);
        add(donneesPane, eveBtn, componentMap);
        add(donneesPane, programSmsBtn, componentMap);
        add(donneesPane, envoieMsgBtn, componentMap);
        add(donneesPane, remoteBdBtn, componentMap);
        add(donneesPane, configReqBtn, componentMap);
        add(donneesPane, urlParam, componentMap);


        add(comptePane, licenceBtn, componentMap);
        add(comptePane, userBtn, componentMap);

        /** rapport pane **/
        add(rapportPane, rapportBtn, componentMap);
        add(rapportPane, commandBtn, componentMap);

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

    public void setContent(JPanel pan) {
        stopTimer(pan);
        container.removeAll();
        container.add(pan);
        container.validate();
    }

    private void stopTimer(JPanel pan) {
        if(container.getCurrent() != null && !(pan instanceof HomePanel) && container.getCurrent() instanceof HomePanel) {
            HomePanel current = (HomePanel) container.getCurrent();
            if(current.getLoggingPanel().getTimer().isRunning()) {
                current.getLoggingPanel().getTimer().stop();
            }
        }
    }

    private <T  extends JPanel> JXHyperlink createLink(String text, Class<T>  panelClass, JXTaskPane pane) {
        JXHyperlink b = new JXHyperlink();
        Color unclickedColortextColor = new Color(16, 66, 104);
        Color clickedColortextColor = new Color(104, 16, 69);
        b.setUnclickedColor(unclickedColortextColor);
        b.setClickedColor(clickedColortextColor);
        b.setFocusable(false);
        b.setText(text);
        b.setBackground(new Color(166, 202, 240));
        if(panelClass != null) {
            b.addActionListener((ActionEvent ae) -> {
                try {
                    setContent(panelClass.getDeclaredConstructor().newInstance());
                    boolean ol = b.getParent() instanceof JXTaskPane;
                    logger.info("valid {} {}",ol, b.getParent().getClass().getName());
                    b.setBackground(clickedColortextColor);
                    componentMap.get(pane).forEach(c -> {
                        if(!c.equals(b)) {
                            c.setUnclickedColor(unclickedColortextColor);
                            pane.validate();
                            logger.info("##testtest###");
                        }
                    });
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    logger.error("Erreur: ", e);
                    JOptionPane.showMessageDialog(MainMenuPanel.this.getParent(), "Erreur lors de la création du panneau");
                }
                ((JXHyperlink)ae.getSource()).setBackground(Color.YELLOW);
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

    private void add(JXTaskPane pane, JXHyperlink link, Map<JXTaskPane, List<JXHyperlink>> map) {
        pane.add(link);
        if(map.containsKey(pane)) {
            List<JXHyperlink> list = map.get(pane);
            list.add(link);
            map.put(pane,list);
        }else {
            map.put(pane, new ArrayList<>());
        }
    }

}
