//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.abouna.lacussms.main;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.BkEtatOp;
import com.abouna.lacussms.entities.Config;
import com.abouna.lacussms.service.*;
import com.abouna.lacussms.views.main.BottomPanel;
import com.abouna.lacussms.views.main.MainFrame;
import com.abouna.lacussms.views.tools.ConstantUtils;
import com.abouna.lacussms.views.tools.Sender;
import com.abouna.lacussms.views.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static ScheduledExecutorService firstExecutor;
    public static boolean appliRun = false;
    static boolean vl = false;
    private static ServiceRequete serviceRequete;
    private static ServiceEvenement serviceEvenement;
    private static ServiceSalaireBKMVTI serviceSalaireBKMVTI;
    private static ServiceSalaire serviceSalaire;
    private static ServiceCredit serviceCredit;
    private static ServiceMandat serviceMandat;
    private static LacusSmsService serviceManager;

    public static void initApp() throws IOException, ClassNotFoundException, SQLException {
        serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        firstExecutor = Executors.newSingleThreadScheduledExecutor();

        if (serviceManager.getAllConfig().isEmpty()) {
            serviceManager.enregistrerConfig(new Config(true, true, true, true));
        }

        String urlParam = serviceManager.getDefaultUrlMessage() == null ? "" : serviceManager.getDefaultUrlMessage().getUrlValue();
        String methode = serviceManager.getDefaultUrlMessage() == null ? "" : serviceManager.getDefaultUrlMessage().getMethode();
        String urlMessage = serviceManager.getDefaultUrlMessage() == null ? "" : serviceManager.getDefaultUrlMessage().getRoot();
        logger.debug("Root: " + urlMessage + "URL: " + urlParam + " Methode : " + methode);
        List<BkEtatOp> list = serviceManager.getListBkEtatOp(true);
        int taille = list.size();
        int i = 0;
        StringBuilder condition = new StringBuilder();
        List<String> listString = new ArrayList<>();

        for(Iterator<BkEtatOp> opIterator = list.iterator(); opIterator.hasNext(); ++i) {
            BkEtatOp nextOp = opIterator.next();
            listString.add(nextOp.getValeur());
            if (i != taille - 1) {
                condition.append("b.ETA='").append(nextOp.getValeur()).append("' OR ");
            } else {
                condition.append("b.ETA='").append(nextOp.getValeur()).append("'");
            }
        }

        logger.debug("MAC address " + Utils.getMacAddress());
        boolean bb = Sender.send("http://keudal.com/assmsserver/assmsserver?user=AS-6853&password=J5676FTJ&sms=<msg>&receive=<num>&sender=CEPI+SA", "237698984176", Utils.hacher("MD5", Objects.requireNonNull(Utils.getMacAddress())));

        logger.info(" envoie " + bb);

        vl = true;
        try {
            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
        } catch (IllegalAccessException | UnsupportedLookAndFeelException | InstantiationException e) {
            logger.error("Error: ", e);
        }

        logger.info("##### initialisation des services ########");
        /* initialisation des differents services */
        serviceRequete = new ServiceRequete(serviceManager, methode, urlParam);
        serviceEvenement = new ServiceEvenement(serviceManager, methode, urlParam, condition.toString(), listString);
        serviceCredit = new ServiceCredit(serviceManager, methode, urlParam, listString);
        serviceMandat = new ServiceMandat(serviceManager, methode, urlParam);
        serviceSalaireBKMVTI = new ServiceSalaireBKMVTI(serviceManager);
        serviceSalaire = new ServiceSalaire(serviceManager, methode, urlParam, listString);

        logger.info("##### initialisation de la connexion de la base de données du CBS ########");
        /* initialisation de la connexion de la base de données */
        Connection conn = Utils.initConnection(serviceManager, ConstantUtils.SECRET_KEY);
        if(conn != null) {
            setConnexion(conn);
        } else {
            BottomPanel.settextLabel("Attention la base données du CBS n'est pas connectée veuillez la paramétrer avant de commencer !!!", Color.RED);
        }
        appliRun = true;
    }

    public static void runApp() {
        EventQueue.invokeLater(() -> {
            try {
                logger.info("#### lancement de l'application #####");
                MainFrame frame = ApplicationConfig.getApplicationContext().getBean(MainFrame.class);
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                frame.setSize((int) screenSize.getWidth() - 100, (int) screenSize.getHeight() - 100);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                String original = ApplicationConfig.getApplicationContext().getBean("licence", String.class);
                Date date = Utils.getDateSimpleFormat("ddMMyyHHmmss", original);
                boolean expression = date == null || date.before(Utils.getTimeFromInternet());
                if(!expression) {
                    frame.setVisible(true);
                } else {
                    logger.error(ConstantUtils.ERROR_MESSAGE);
                    frame.showError(ConstantUtils.ERROR_MESSAGE);
                    System.exit(0);
                }
                frame.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        if (JOptionPane.showConfirmDialog(frame,
                                "Êtes vous sur de vouloir fermer cette application?", "Fermer l'application?",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                            System.exit(0);
                        }
                    }
                });
            } catch (Exception e) {
                logger.error("Erreur de démerrage ", e);
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        });
    }


    public static void demarrerServiceData() {
        try {
            Utils.initDriver();

            Config config = serviceManager.getAllConfig().get(0);
            AtomicReference<String> msg = new AtomicReference<>();
            firstExecutor.scheduleAtFixedRate(() -> {
                logger.info("Démarrage du service de données .....");
                    if (config.isEvent()) {
                        try {
                            serviceEvenement.serviceEvenement();
                        } catch (SQLException var2) {
                            msg.set("ServiceData: Echec de connexion à la base de données");
                            logger.error(msg.get());
                            BottomPanel.settextLabel(msg.get(), Color.RED);
                        } catch (ParseException e) {
                            msg.set("ServiceData: Problème de formatage de la date");
                            logger.error(msg.get());
                            BottomPanel.settextLabel(msg.get(), Color.RED);
                        }
                    }

                    if (config.isBkmpai()) {
                        try {
                            serviceSalaire.serviceSalaire();
                            serviceSalaireBKMVTI.serviceSalaireBKMVTI();
                        } catch (SQLException e) {
                            msg.set("ServiceData: Echec de connexion à la base de données");
                            logger.error(msg.get());
                            BottomPanel.settextLabel(msg.get(), Color.RED);
                        } catch (ParseException e) {
                            msg.set("ServiceData: Problème de formatage de la date");
                            logger.error(msg.get());
                            BottomPanel.settextLabel(msg.get(), Color.RED);
                        }
                    }

                    if (config.isBkmac()) {
                        try {
                            serviceCredit.serviceCredit();
                        } catch (SQLException e) {
                            msg.set("ServiceData: Echec de connexion à la base de données");
                            logger.error(msg.get());
                            BottomPanel.settextLabel(msg.get(), Color.RED);
                        } catch (ParseException e) {
                            msg.set("ServiceData: Problème de formatage de la date");
                            logger.error(msg.get());
                            BottomPanel.settextLabel(msg.get(), Color.RED);
                        }
                    }

                    if (config.isMandat()) {
                        try {
                            serviceMandat.serviceMandat();
                        } catch (SQLException e) {
                            msg.set("ServiceData: Echec de connexion à la base de données");
                            logger.error(msg.get());
                            BottomPanel.settextLabel(msg.get(), Color.RED);
                        } catch (ParseException e) {
                            msg.set("ServiceData: Problème de formatage de la date");
                            logger.error(msg.get());
                            BottomPanel.settextLabel(msg.get(), Color.RED);
                        }
                   }
            }, 0, 1000, TimeUnit.MILLISECONDS);
        } catch (ClassNotFoundException e) {
            String msg = "ServiceData: Problème de chargement du pilote";
            logger.error(msg);
            BottomPanel.settextLabel(msg, Color.red);
        }

    }

    public static void demarrerServiceSms() {
        Config config = serviceManager.getAllConfig().get(0);
        firstExecutor.scheduleAtFixedRate(() -> {
            logger.info("Démarrage du service sms ...");
            logger.info("demarrage du service 111111...");
                if (config.isEvent()) {
                serviceEvenement.envoieSMSEvenement();
                }

                if (config.isBkmpai()) {
                    serviceSalaire.envoieSMSSalaire();
                }

                if (config.isBkmac()) {
                    serviceCredit.envoieSMSCredit();
                }

                if (config.isMandat()) {
                    serviceMandat.envoieSMSMandat();
                }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public static void demarrerServiceRequete() {
        firstExecutor.scheduleAtFixedRate(() -> {
            logger.info("Démarrage du service...");

            try {
                serviceRequete.serviceRequete();
            } catch (ParseException | SQLException var1) {
                String msg = "erreur lors de l'execution du service ...";
                logger.error(msg);
                BottomPanel.settextLabel(msg, Color.RED);
            }

        }, 0, 1000, TimeUnit.MILLISECONDS);
    }


    public static void demarrerServiceSequenciel() {
        try {
            Utils.initDriver();
            firstExecutor = Executors.newSingleThreadScheduledExecutor();
            Config config = serviceManager.getAllConfig().get(0);
            final String[] msg = {"demarrerServiceSequenciel: Démarrage du service..."};
            logger.info(msg[0]);
            BottomPanel.settextLabel(msg[0], Color.BLACK);
            firstExecutor.scheduleAtFixedRate(() -> {
                logger.info(msg[0]);
                    if (config.isEvent()) {
                        try {
                            serviceEvenement.serviceEvenement();
                        } catch (SQLException e) {
                            msg[0] = "demarrerServiceSequenciel: Echec de connexion à la base de données";
                            logger.info(msg[0]);
                            BottomPanel.settextLabel(msg[0], Color.RED);
                        } catch (ParseException e) {
                            msg[0] = "demarrerServiceSequenciel: Problème de formatage de la date";
                            logger.info(msg[0]);
                            BottomPanel.settextLabel(msg[0], Color.RED);
                        }
                        serviceEvenement.envoieSMSEvenement();
                    }

                    if (config.isBkmpai()) {
                        try {
                            serviceSalaire.serviceSalaire();
                            serviceSalaireBKMVTI.serviceSalaireBKMVTI();
                        } catch (SQLException e) {
                            msg[0] = "demarrerServiceSequenciel: Problème de connexion sur la base de données";
                            logger.error(msg[0]);
                            BottomPanel.settextLabel(msg[0], Color.RED);
                        } catch (ParseException e) {
                            msg[0] = "demarrerServiceSequenciel: Problème de formatage de la date";
                            logger.error(msg[0]);
                            BottomPanel.settextLabel(msg[0], Color.RED);
                        }

                        serviceSalaire.envoieSMSSalaire();
                    }

                    if (config.isBkmac()) {
                        try {
                            serviceCredit.serviceCredit();
                        } catch (SQLException e) {
                            msg[0] = "demarrerServiceSequenciel: Problème de connexion sur la base de données";
                            logger.error(msg[0]);
                            BottomPanel.settextLabel(msg[0], Color.RED);
                        } catch (ParseException e) {
                            msg[0] = "demarrerServiceSequenciel: Problème de formatage de la date";
                            logger.error(msg[0]);
                            BottomPanel.settextLabel(msg[0], Color.RED);
                        }

                        serviceCredit.envoieSMSCredit();
                    }

                    if (config.isMandat()) {
                        try {
                            serviceMandat.serviceMandat();
                        } catch (SQLException e) {
                            msg[0] = "demarrerServiceSequenciel: Problème de connexion sur la base de données";
                            logger.error(msg[0]);
                            BottomPanel.settextLabel(msg[0], Color.RED);
                        } catch (ParseException e) {
                            msg[0] = "demarrerServiceSequenciel: Problème de formatage de la date";
                            logger.error(msg[0]);
                            BottomPanel.settextLabel(msg[0], Color.RED);
                        }

                        serviceMandat.envoieSMSMandat();
                    }

            }, 0, 1000, TimeUnit.MILLISECONDS);
        } catch (ClassNotFoundException e) {
            String msg = "demarrerServiceSequenciel: Problème de chargement du pilote";
            logger.error(msg);
            BottomPanel.settextLabel(msg, Color.red);
        }

    }

    public static void stopper() {
        if (firstExecutor != null) {
            firstExecutor.shutdownNow();
        }
        BottomPanel.settextLabel("");
        logger.info("le service a été arreté");
    }

    public static void setConnexion(Connection connexion) {
        serviceSalaire.setConn(connexion);
        serviceMandat.setConn(connexion);
        serviceCredit.setConn(connexion);
        serviceSalaireBKMVTI.setConn(connexion);
        serviceRequete.setConn(connexion);
        serviceEvenement.setConn(connexion);
    }

}
