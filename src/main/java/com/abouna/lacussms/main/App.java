//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.abouna.lacussms.main;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.BkAgence;
import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.entities.BkEtatOp;
import com.abouna.lacussms.entities.BkEve;
import com.abouna.lacussms.entities.BkMad;
import com.abouna.lacussms.entities.BkOpe;
import com.abouna.lacussms.entities.Command;
import com.abouna.lacussms.entities.Config;
import com.abouna.lacussms.entities.Licence;
import com.abouna.lacussms.entities.Message;
import com.abouna.lacussms.entities.MessageFormat;
import com.abouna.lacussms.entities.MessageMandat;
import com.abouna.lacussms.entities.RemoteDB;
import com.abouna.lacussms.entities.ServiceOffert;
import com.abouna.lacussms.entities.Status;
import com.abouna.lacussms.entities.TypeEvent;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.service.ServiceRequete;
import com.abouna.lacussms.views.LicencePanel;
import com.abouna.lacussms.views.main.BottomPanel;
import com.abouna.lacussms.views.main.MainFrame;
import com.abouna.lacussms.views.main.SplashFrame;
import com.abouna.lacussms.views.tools.AES;
import com.abouna.lacussms.views.tools.Utils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    static LacusSmsService serviceManager;
    private static Thread thread;
    private static boolean running;
    private static boolean running1;
    private static boolean running2;
    private static boolean running3;
    private static Connection conn = null;
    private static String lic;
    private static String urlParam;
    private static String methode;
    private static String condition = "";
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static List<String> listString = new ArrayList<>();
    static boolean appliRun = false;
    public static String username = null;
    public static String licenceString = "";
    public static String fixedDate = "30082022";
    static boolean vl = false;

    private static MainFrame frame;


    public static boolean isAppliRun() {
        return appliRun;
    }


    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
         new SplashFrame();
        initApp();
    }

    public static void initApp() throws IOException, ClassNotFoundException {
        serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        if (serviceManager.getAllConfig().isEmpty()) {
            serviceManager.enregistrerConfig(new Config(true, true, true, true));
        }

        urlParam = serviceManager.getDefaultUrlMessage() == null ? "" : serviceManager.getDefaultUrlMessage().getUrlValue();
        methode = serviceManager.getDefaultUrlMessage() == null ? "" : serviceManager.getDefaultUrlMessage().getMethode();
        String urlMessage = serviceManager.getDefaultUrlMessage() == null ? "" : serviceManager.getDefaultUrlMessage().getRoot();
        logger.info("Root: " + urlMessage + "URL: " + urlParam + " Methode : " + methode);
        List<BkEtatOp> list = serviceManager.getListBkEtatOp(true);
        int taille = list.size();
        int i = 0;
        condition = "";
        listString = new ArrayList<>();

        for(Iterator<BkEtatOp> var3 = list.iterator(); var3.hasNext(); ++i) {
            BkEtatOp op = (BkEtatOp)var3.next();
            listString.add(op.getValeur());
            if (i != taille - 1) {
                condition = condition + "b.ETA='" + op.getValeur() + "' OR ";
            } else {
                condition = condition + "b.ETA='" + op.getValeur() + "'";
            }
        }

        logger.info("MAC address " + Utils.getMacAddress());
        boolean bb = Sender.send("http://keudal.com/assmsserver/assmsserver?user=AS-6853&password=J5676FTJ&sms=<msg>&receive=<num>&sender=CEPI+SA", "237698984176", Utils.hacher("MD5", Objects.requireNonNull(Utils.getMacAddress())));
        logger.info(" envoie " + bb);
        boolean b = true; //verifierLicence();
        logger.info(" testtt     .....2");

        try {
            vl = true;
            logger.info(" testtt     .....3");
        } catch (Exception var7) {
            JOptionPane.showMessageDialog(frame.getParent(), "Problème de connexion assurez vous que votre poste\r\n est connecté à internet ou verifiez votre parfeu\r\n s'il ne bloque pas le connexion vers le serveur (alwaysdata.com)!");
            logger.error("Problème de connexion assurez vous que votre poste" +
                    " est connecté à internet ou verifiez votre parfeu" +
                    " s'il ne bloque pas le connexion vers le serveur (alwaysdata.com)!");
        }

        try {
            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
        } catch (IllegalAccessException | UnsupportedLookAndFeelException | InstantiationException e) {
            logger.error("Error: ", e);
        }

        SwingUtilities.invokeLater(() -> {
            try {
                if (!vl) {
                    JOptionPane.showMessageDialog(frame.getParent(), "Votre licence n'est plus valide\r\n merci de cliquer sur ok pour enregistrer une nouvelle\r\n ou contactez votre fournisseur!");
                }

                if (vl) {
                    appliRun = true;
                    frame = new MainFrame();
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    frame.setSize((int) screenSize.getWidth() - 100, (int) screenSize.getHeight() -100);
                    frame.setLocationRelativeTo(null);
                    frame.setDefaultCloseOperation(0);
                    frame.setVisible(true);
                } else {
                    LicencePanel nouveau1 = new LicencePanel(null);
                    nouveau1.setSize(450, 150);
                    nouveau1.setLocationRelativeTo(null);
                    nouveau1.setModal(true);
                    nouveau1.setResizable(false);
                    nouveau1.setDefaultCloseOperation(0);
                    nouveau1.setVisible(true);
                    nouveau1.setUndecorated(true);
                    nouveau1.getRootPane().setWindowDecorationStyle(0);
                }
            } catch (IOException | XmlPullParserException e) {
                logger.error("Error " , e);
            }

        });
    }


    public static void demarrerServiceData() {
        try {
            Utils.initDriver();
            Thread t = new Thread(() -> {
                running1 = true;
                String msg;
                while(running1) {
                    Config config = (Config)serviceManager.getAllConfig().get(0);
                    if (config.isEvent()) {
                        try {
                            serviceEvenement();
                        } catch (SQLException var2) {
                            msg = "Echec de connexion à la base de données";
                            logger.error(msg);
                            BottomPanel.settextLabel(msg, Color.RED);
                        } catch (ParseException var3) {
                            msg = "Problème de formatage de la date";
                            logger.error(msg);
                            BottomPanel.settextLabel(msg, Color.RED);
                        }
                    }

                    if (config.isBkmpai()) {
                        try {
                            ServiceSalaire();
                            ServiceSalaireBKMVTI();
                        } catch (SQLException var4) {
                            msg = "Echec de connexion à la base de données";
                            logger.error(msg);
                            BottomPanel.settextLabel(msg, Color.RED);
                        } catch (ParseException var5) {
                            msg = "Problème de formatage de la date";
                            logger.error(msg);
                            BottomPanel.settextLabel(msg, Color.RED);
                        }
                    }

                    if (config.isBkmac()) {
                        try {
                            ServiceCredit();
                        } catch (SQLException var6) {
                            msg = "Echec de connexion à la base de données";
                            logger.error(msg);
                            BottomPanel.settextLabel(msg, Color.RED);
                        } catch (ParseException var7) {
                            msg = "Problème de formatage de la date";
                            logger.error(msg);
                            BottomPanel.settextLabel(msg, Color.RED);
                        }
                    }

                    if (config.isMandat()) {
                        try {
                            ServiceMandat();
                        } catch (SQLException var8) {
                            msg = "Echec de connexion à la base de données";
                            logger.error(msg);
                            BottomPanel.settextLabel(msg, Color.RED);
                        } catch (ParseException var9) {
                            msg = "Problème de formatage de la date";
                            logger.error(msg);
                            BottomPanel.settextLabel(msg, Color.RED);
                        }
                    }
                }

            });
            t.start();
        } catch (ClassNotFoundException var2) {
            String msg = "Problème de chargement du pilote";
            logger.error(msg);
            BottomPanel.settextLabel(msg, Color.red);
        }

    }

    public static void demarrerServiceSms() {
        thread = new Thread(() -> {
            logger.info("Démarrage du service...");
            running = true;

            while(running) {
                Config config = serviceManager.getAllConfig().get(0);
                if (config.isEvent()) {
                    envoieSMSEvenement();
                }

                if (config.isBkmpai()) {
                    envoieSMSSalaire();
                }

                if (config.isBkmac()) {
                    envoieSMSCredit();
                }

                if (config.isMandat()) {
                    envoieSMSMandat();
                }
            }

        });
        thread.start();
    }

    public static void demarrerServiceRequete() {
        thread = new Thread(() -> {
            logger.info("Démarrage du service...");
            running3 = true;

            while(running3) {
                try {
                    ServiceRequete.ServiceRequete();
                } catch (ParseException | SQLException var1) {
                    String msg = "erreur lors de l'execution du service ...";
                    logger.error(msg);
                    BottomPanel.settextLabel(msg, Color.RED);
                }
            }

        });
        thread.start();
    }


    public static void demarrerServiceSequenciel() {
        try {
            Utils.initDriver();
            Thread threads = new Thread(() -> {
                String msg = "Démarrage du service...";
                logger.info(msg);
                BottomPanel.settextLabel(msg, Color.BLACK);
                running2 = true;

                while(running2) {
                    Config config = (Config)serviceManager.getAllConfig().get(0);
                    if (config.isEvent()) {
                        try {
                            serviceEvenement();
                        } catch (SQLException var8) {
                            msg = "Echec de connexion à la base de données";
                            logger.info(msg);
                            BottomPanel.settextLabel(msg, Color.RED);
                        } catch (ParseException var9) {
                            msg = "Problème de formatage de la date";
                            logger.info(msg);
                            BottomPanel.settextLabel(msg, Color.RED);
                        }

                        envoieSMSEvenement();
                    }

                    if (config.isBkmpai()) {
                        try {
                            ServiceSalaire();
                            ServiceSalaireBKMVTI();
                        } catch (SQLException var6) {
                            msg = "Problème de connexion sur la base de données";
                            logger.error(msg);
                            BottomPanel.settextLabel(msg, Color.RED);
                        } catch (ParseException var7) {
                            msg = "Problème de formatage de la date";
                            logger.error(msg);
                            BottomPanel.settextLabel(msg, Color.RED);
                        }

                        envoieSMSSalaire();
                    }

                    if (config.isBkmac()) {
                        try {
                            ServiceCredit();
                        } catch (SQLException var4) {
                            msg = "Problème de connexion sur la base de données";
                            logger.error(msg);
                            BottomPanel.settextLabel(msg, Color.RED);
                        } catch (ParseException var5) {
                            msg = "Problème de formatage de la date";
                            logger.error(msg);
                            BottomPanel.settextLabel(msg, Color.RED);
                        }

                        envoieSMSCredit();
                    }

                    if (config.isMandat()) {
                        try {
                            ServiceMandat();
                        } catch (SQLException var2) {
                            msg = "Problème de connexion sur la base de données";
                            logger.error(msg);
                            BottomPanel.settextLabel(msg, Color.RED);
                        } catch (ParseException var3) {
                            msg = "Problème de formatage de la date";
                            logger.error(msg);
                            BottomPanel.settextLabel(msg, Color.RED);
                        }

                        envoieSMSMandat();
                    }

                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException var10) {
                        break;
                    }
                }

            });
            threads.start();
        } catch (ClassNotFoundException var2) {
            String msg = "Problème de chargement du pilote";
            logger.error(msg);
            BottomPanel.settextLabel(msg, Color.red);
        }

    }

    public static void serviceEvenement() throws SQLException, ParseException {
        String msg = "Début traitement des venements";
        logger.info(msg);
        RemoteDB remoteDB = serviceManager.getDefaultRemoteDB(true);
        String decryptedString = AES.decrypt(remoteDB.getPassword(), "LACUS2017");
        conn = DriverManager.getConnection(remoteDB.getUrl(), remoteDB.getName(), decryptedString);
        int compteur = serviceManager.getMaxIndexBkEve(TypeEvent.ordinaire);
        String currentString = "";
        Date current = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");
        String query = "";
        String heureInit = "00:00:00.000";
        String heure;
        String date;
        String finalquery;
        if (compteur == 0) {
            heure = "00:00:00.000";
            date = format.format(current);
            if (!condition.equals("")) {
                finalquery = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM bkeve b WHERE b.NCP1 >= '0' AND b.DSAI = '" + date + "' AND b.HSAI > '" + heure + "' AND (" + condition + ")  ORDER BY b.DSAI,b.HSAI ASC";
            } else {
                finalquery = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM bkeve b WHERE b.NCP1 >= '0' AND b.DSAI = '" + date + "' AND b.HSAI > '" + heure + "'  ORDER BY b.DSAI,b.HSAI ASC";
            }
        } else {
            BkEve bkEve = serviceManager.getBkEveById(compteur);
            heure = bkEve.getHsai();
            int a = 0;
            if (!heure.equals("00:00:00.000")) {
                a = Integer.parseInt(heure.split(":")[0]) - 1;
            }

            if (a < 10) {
                heure = heure.replace(heure.split(":")[0], "0" + a);
            } else {
                heure = heure.replace(heure.split(":")[0], "" + a);
            }

            if (bkEve.getDVAB().length() > 10) {
                date = bkEve.getDVAB().substring(0, 10);
            } else {
                date = bkEve.getDVAB();
            }

            String[] tab = date.split("-");
            date = tab[2] + "-" + tab[1] + "-" + tab[0];
            currentString = format.format(current);
            String suffix = " ORDER BY DSAI,HSAI ASC";
            if (!currentString.equals(date)) {
                String query1;
                if (!condition.equals("")) {
                    query = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM bkeve b WHERE b.NCP1 >= '0' AND b.DSAI = '" + date + "' AND b.HSAI > '" + heure + "' AND (" + condition + ")";
                    query1 = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM bkeve b WHERE b.NCP1 >= '0' AND b.DSAI = '" + currentString + "' AND b.HSAI > '" + heureInit + "' AND (" + condition + ")";
                    finalquery = "(" + query + ") UNION (" + query1 + ")" + suffix;
                } else {
                    query = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM bkeve b WHERE b.NCP1 >= '0' AND b.DSAI = '" + date + "' AND b.HSAI > '" + heure + "'";
                    query1 = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM bkeve b WHERE b.NCP1 >= '0' AND b.DSAI = '" + currentString + "' AND b.HSAI > '" + heureInit + "'";
                    finalquery = "(" + query + ") UNION (" + query1 + ")" + suffix;
                }
            } else if (!condition.equals("")) {
                query = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM bkeve b WHERE b.NCP1 >= '0' AND b.DSAI = '" + date + "' AND b.HSAI > '" + heure + "' AND (" + condition + ")";
                finalquery = query + suffix;
            } else {
                query = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM bkeve b WHERE b.NCP1 >= '0' AND b.DSAI = '" + date + "' AND b.HSAI > '" + heure + "'";
                finalquery = query + suffix;
            }
        }

        PreparedStatement ps = conn.prepareStatement(finalquery);
        Throwable var57 = null;

        try {
            ResultSet rs = ps.executeQuery();

            label513:
            while(true) {
                BkCli bkCli;
                String cli;
                boolean traitement;
                BkEve eve;
                do {
                    do {
                        do {
                            if (!rs.next()) {
                                break label513;
                            }

                            BottomPanel.settextLabel("Recherche des évenements en cours.... ", Color.BLACK);
                        } while(rs.getString("NCP1") == null);
                    } while(rs.getString("NCP1").trim().length() < 10);

                    eve = new BkEve();
                    BkAgence bkAgence = serviceManager.getBkAgenceById(rs.getString("AGE").trim());
                    eve.setBkAgence(bkAgence);
                    bkCli = null;
                    cli = "";
                    if (rs.getString("NCP1").trim().length() >= 9) {
                        cli = rs.getString("NCP1").trim().substring(3, 9);
                    }

                    bkCli = serviceManager.getBkCliById(cli);
                    if (bkCli == null) {
                        bkCli = serviceManager.getBkCliByNumCompte(rs.getString("NCP1").trim());
                    }

                    eve.setCli(bkCli);
                    eve.setCompte(rs.getString("NCP1").trim());
                    eve.setEtat(rs.getString("ETA").trim());
                    eve.setHsai(rs.getString("HSAI").trim());
                    eve.setMont(Double.parseDouble(rs.getString("MON1").trim()));
                    eve.setMontant(rs.getString("MON1").trim());
                    BkOpe bkOpe = serviceManager.getBkOpeById(rs.getString("OPE").trim());
                    eve.setOpe(bkOpe);
                    eve.setDVAB(rs.getString("DSAI").trim());
                    eve.setEventDate(f2.parse(f2.format(format1.parse(rs.getString("DSAI").trim()))));
                    eve.setSent(false);
                    eve.setNumEve(rs.getString("EVE").trim());
                    eve.setId(serviceManager.getMaxIndexBkEve() + 1);
                    eve.setType(TypeEvent.ordinaire);
                    traitement = bkCli != null;

                    if (!serviceManager.getBkEveByCriteria(eve.getNumEve(), eve.getEventDate(), eve.getCompte()).isEmpty()) {
                        traitement = false;
                    }

                    if (!serviceManager.getBkEveByPeriode(eve.getNumEve(), eve.getCompte(), Utils.add(eve.getEventDate(), -2L), eve.getEventDate()).isEmpty()) {
                        traitement = false;
                    }

                    if (!serviceManager.getBkEveByCriteria(eve.getNumEve(), eve.getCompte(), eve.getHsai(), eve.getMontant()).isEmpty()) {
                        traitement = false;
                    }

                    if (!serviceManager.getBkEveByCriteriaMontant(eve.getNumEve(), eve.getCompte(), eve.getMontant()).isEmpty()) {
                        traitement = false;
                    }
                } while(!traitement);

                msg = "Chargement données évenement.... " + eve.getCompte();
                logger.info(msg);
                BottomPanel.settextLabel(msg, Color.BLACK);
                serviceManager.enregistrer(eve);
                String q = "SELECT b.NUM, b.CLI, b.TYP FROM bktelcli b WHERE b.CLI='" + cli + "'";
                PreparedStatement pss = conn.prepareStatement(q);
                Throwable var26 = null;

                try {
                    ResultSet resultat = pss.executeQuery();
                    int n = 0;

                    while(true) {
                        while(true) {
                            do {
                                if (!resultat.next()) {
                                    continue label513;
                                }
                            } while(bkCli.getPhone() != 0L);

                            String code = "";
                            String numero = null;
                            if (resultat.getString("NUM").replace(" ", "").replace("/", "").trim().length() == 9 && Utils.estUnEntier(resultat.getString("NUM").trim())) {
                                code = "237";
                                numero = code + resultat.getString("NUM").trim();
                                if (bkCli.getPhone() != Long.parseLong(numero)) {
                                    bkCli.setPhone(Long.parseLong(numero));
                                    if (n == 0) {
                                        msg = "Mise à jour numero client.... " + bkCli.getCode();
                                        logger.info(msg);
                                        BottomPanel.settextLabel(msg, Color.BLACK);
                                        serviceManager.modifier(bkCli);
                                        ++n;
                                    }
                                }
                            } else if (resultat.getString("NUM").replace(" ", "").replace("/", "").trim().length() == 8 && Utils.estUnEntier(resultat.getString("NUM").trim())) {
                                code = "241";
                                numero = code + resultat.getString("NUM").trim();
                                if (bkCli.getPhone() != Long.parseLong(numero)) {
                                    bkCli.setPhone(Long.parseLong(numero));
                                    if (n == 0) {
                                        msg = "Mise à jour numero client.... " + bkCli.getCode();
                                        logger.info(msg);
                                        BottomPanel.settextLabel(msg, Color.BLACK);
                                        serviceManager.modifier(bkCli);
                                        ++n;
                                    }
                                }
                            }
                        }
                    }
                } catch (Throwable var52) {
                    var26 = var52;
                    throw var52;
                } finally {
                    if (pss != null) {
                        if (var26 != null) {
                            try {
                                pss.close();
                            } catch (Throwable var51) {
                                var26.addSuppressed(var51);
                            }
                        } else {
                            pss.close();
                        }
                    }

                }
            }
        } catch (Throwable var54) {
            var57 = var54;
            throw var54;
        } finally {
            if (ps != null) {
                if (var57 != null) {
                    try {
                        ps.close();
                    } catch (Throwable var50) {
                        var57.addSuppressed(var50);
                    }
                } else {
                    ps.close();
                }
            }

        }

        conn.close();
    }

    public static void envoieSMSEvenement() {
        if (Utils.checkLicence()) {
            List<BkEve> list = serviceManager.getBkEveBySendParam(false, listString, TypeEvent.ordinaire);
            list.forEach((eve) -> {
                BkCli bkCli = eve.getCli();
                if (bkCli != null && eve.getOpe() != null && !"".equals(methode) && bkCli.isEnabled() && serviceManager.getBkCompCliByCriteria(bkCli, eve.getCompte(), true) != null && bkCli.getPhone() != 0L) {
                    MessageFormat mf = serviceManager.getFormatByBkOpe(eve.getOpe(), bkCli.getLangue());
                    if (mf != null) {
                        String text = Utils.remplacerVariable(bkCli, eve.getOpe(), eve, mf);
                        String res = testConnexionInternet();
                        String msg = "Test connexion ...." + res;
                        logger.info(msg);
                        BottomPanel.settextLabel(msg, Color.BLACK);
                        if (res.equals("OK")) {
                            msg = "Envoie du Message à.... " + eve.getCompte();
                            logger.info(msg);
                            BottomPanel.settextLabel(msg, Color.BLACK);
                            switch (methode) {
                                case "METHO1":
                                case "METHO2":
                                    Sender.send(urlParam, "" + bkCli.getPhone(), text);
                                    break;
                            }
                        } else {
                            msg = "Message non envoyé à.... " + eve.getCompte() + " Problème de connexion internet!!";
                            logger.info(msg);
                            BottomPanel.settextLabel(msg, Color.RED);
                        }

                        Message message = new Message();
                        message.setTitle(eve.getOpe().getLib());
                        message.setContent(text);
                        message.setBkEve(eve);
                        message.setSendDate(new Date());
                        message.setNumero(Long.toString(bkCli.getPhone()));
                        if (res.equals("OK")) {
                            serviceManager.enregistrer(message);
                            eve.setSent(true);
                            serviceManager.modifier(eve);
                            msg = "OK Message envoyé ";
                            logger.info(msg);
                            BottomPanel.settextLabel(msg, Color.BLACK);
                        }
                    }
                }

            });
        } else {
            String msg = "Message non envoyé Problème de Licence veuillez contacter le fournieur!!";
            logger.info(msg);
            BottomPanel.settextLabel(msg, Color.RED);
        }

    }

    public static void stopper() {
        running = false;
        running1 = false;
        running2 = false;
        running3 = false;
        BottomPanel.settextLabel("");
        logger.info("le service a été arreté");
    }


    public static String testConnexionInternet() {
        String code = "KO";

        try {
            URL url = new URL("https://www.google.com");
            HttpURLConnection conn1 = (HttpURLConnection)url.openConnection();
            if (conn1.getResponseCode() == 200) {
                code = "OK";
            }

            return code;
        } catch (IOException var3) {
            logger.error("problème de connexion internet " + var3.getMessage());
            return "KO";
        }
    }


    public static void ServiceSalaire() throws SQLException, ParseException {
        String msg = "Traitement des salaires en cours.... ";
        logger.info(msg);
        BottomPanel.settextLabel(msg, Color.BLACK);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");
        RemoteDB remoteDB = serviceManager.getDefaultRemoteDB(true);
        String decryptedString = AES.decrypt(remoteDB.getPassword(), "LACUS2017");
        conn = DriverManager.getConnection(remoteDB.getUrl(), remoteDB.getName(), decryptedString);
        PreparedStatement ps = conn.prepareStatement("SELECT b.NCP AS NCP1,b.AGE,b.DCO AS DSAI,b.OPE,b.EVE,b.MON FROM BKMPAI b  WHERE b.NCP >= '0' ORDER BY DCO ASC");
        Throwable var6 = null;

        try {
            ResultSet rs = ps.executeQuery();

            label357:
            while(rs.next()) {
                msg = "Recherche données salaires.... ";
                logger.info(msg);
                BottomPanel.settextLabel(msg, Color.BLACK);
                if (rs.getString("NCP1") != null && rs.getString("NCP1").trim().length() >= 10) {
                    BkEve eve = new BkEve();
                    BkAgence bkAgence = serviceManager.getBkAgenceById(rs.getString("AGE").trim());
                    eve.setBkAgence(bkAgence);
                    BkCli bkCli = null;
                    String cli = "";
                    if (rs.getString("NCP1").trim().length() >= 9) {
                        cli = rs.getString("NCP1").trim().substring(3, 9);
                    }

                    bkCli = serviceManager.getBkCliById(cli);
                    if (bkCli == null) {
                        bkCli = serviceManager.getBkCliByNumCompte(rs.getString("NCP1").trim());
                    }

                    eve.setCli(bkCli);
                    eve.setCompte(rs.getString("NCP1").trim());
                    eve.setEtat("VA");
                    eve.setHsai("00:00:00.000");
                    eve.setMont(Double.parseDouble(rs.getString("MON").trim()));
                    eve.setMontant(rs.getString("MON").trim());
                    BkOpe bkOpe = serviceManager.getBkOpeById(rs.getString("OPE").trim());
                    eve.setOpe(bkOpe);
                    eve.setDVAB(rs.getString("DSAI").trim());
                    eve.setEventDate(f2.parse(f2.format(format1.parse(rs.getString("DSAI").trim()))));
                    eve.setSent(false);
                    eve.setNumEve(rs.getString("EVE").trim());
                    eve.setId(serviceManager.getMaxIndexBkEve() + 1);
                    eve.setType(TypeEvent.salaire);
                    if (bkCli != null && serviceManager.getBkEveByCriteria(eve.getNumEve(), eve.getEventDate(), eve.getCompte()).isEmpty()) {
                        msg = "Chargement données salaires.... " + eve.getCompte();
                        logger.info(msg);
                        BottomPanel.settextLabel(msg, Color.BLACK);
                        serviceManager.enregistrer(eve);
                        String q = "SELECT b.NUM, b.CLI, b.TYP FROM bktelcli b WHERE b.CLI='" + rs.getString("NCP1").trim().substring(3, 9) + "'";
                        PreparedStatement pss = conn.prepareStatement(q);
                        Throwable var15 = null;

                        try {
                            ResultSet resultat = pss.executeQuery();
                            int n = 0;

                            while(true) {
                                while(true) {
                                    do {
                                        if (!resultat.next()) {
                                            continue label357;
                                        }
                                    } while(bkCli.getPhone() != 0L);

                                    String code = "";
                                    String numero = null;
                                    if (resultat.getString("NUM").replace(" ", "").replace("/", "").trim().length() == 9 && Utils.estUnEntier(resultat.getString("NUM").trim())) {
                                        code = "237";
                                        numero = code + resultat.getString("NUM").trim();
                                        if (bkCli.getPhone() != Long.parseLong(numero)) {
                                            bkCli.setPhone(Long.parseLong(numero));
                                            if (n == 0) {
                                                msg = "Mise à jour numero client.... " + bkCli.getCode();
                                                logger.info(msg);
                                                BottomPanel.settextLabel(msg, Color.BLACK);
                                                serviceManager.modifier(bkCli);
                                                ++n;
                                            }
                                        }
                                    } else if (resultat.getString("NUM").replace(" ", "").replace("/", "").trim().length() == 8 && Utils.estUnEntier(resultat.getString("NUM").trim())) {
                                        code = "241";
                                        numero = code + resultat.getString("NUM").trim();
                                        if (bkCli.getPhone() != Long.parseLong(numero)) {
                                            bkCli.setPhone(Long.parseLong(numero));
                                            if (n == 0) {
                                                msg = "Mise à jour numero client.... " + bkCli.getCode();
                                                logger.info(msg);
                                                BottomPanel.settextLabel(msg, Color.BLACK);
                                                serviceManager.modifier(bkCli);
                                                ++n;
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Throwable var41) {
                            var15 = var41;
                            throw var41;
                        } finally {
                            if (pss != null) {
                                if (var15 != null) {
                                    try {
                                        pss.close();
                                    } catch (Throwable var40) {
                                        var15.addSuppressed(var40);
                                    }
                                } else {
                                    pss.close();
                                }
                            }

                        }
                    }
                }
            }
        } catch (Throwable var43) {
            var6 = var43;
            throw var43;
        } finally {
            if (ps != null) {
                if (var6 != null) {
                    try {
                        ps.close();
                    } catch (Throwable var39) {
                        var6.addSuppressed(var39);
                    }
                } else {
                    ps.close();
                }
            }

        }

        conn.close();
    }

    public static void ServiceSalaireBKMVTI() throws SQLException, ParseException {
        String msg = "Traitement des salaires BKMVTI en cours.... ";
        logger.info(msg);
        BottomPanel.settextLabel(msg, Color.BLACK);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");
        RemoteDB remoteDB = serviceManager.getDefaultRemoteDB(true);
        String decryptedString = AES.decrypt(remoteDB.getPassword(), "LACUS2017");
        conn = DriverManager.getConnection(remoteDB.getUrl(), remoteDB.getName(), decryptedString);
        String query = "SELECT b.AGE,b.NCP AS NCP1,b.OPE,b.EVE,b.DCO AS DSAI,b.DVA,b.MON FROM BKMVTI b WHERE b.NCP >= '0' ORDER BY DCO ASC";
        PreparedStatement ps = conn.prepareStatement(query);
        Throwable var7 = null;

        try {
            ResultSet rs = ps.executeQuery();

            label357:
            while(rs.next()) {
                msg = "Recherche données salaires BKMVTI.... ";
                logger.info(msg);
                BottomPanel.settextLabel(msg, Color.BLACK);
                if (rs.getString("NCP1") != null && rs.getString("NCP1").trim().length() >= 10) {
                    BkEve eve = new BkEve();
                    BkAgence bkAgence = serviceManager.getBkAgenceById(rs.getString("AGE").trim());
                    eve.setBkAgence(bkAgence);
                    String cli = "";
                    if (rs.getString("NCP1").trim().length() >= 9) {
                        cli = rs.getString("NCP1").trim().substring(3, 9);
                    }

                    BkCli bkCli = serviceManager.getBkCliById(cli);
                    if (bkCli == null) {
                        bkCli = serviceManager.getBkCliByNumCompte(rs.getString("NCP1").trim());
                    }

                    eve.setCli(bkCli);
                    eve.setCompte(rs.getString("NCP1").trim());
                    eve.setEtat("VA");
                    eve.setHsai("00:00:00.000");
                    eve.setMont(Double.parseDouble(rs.getString("MON").trim()));
                    eve.setMontant(rs.getString("MON").trim());
                    BkOpe bkOpe = serviceManager.getBkOpeById(rs.getString("OPE").trim());
                    eve.setOpe(bkOpe);
                    eve.setDVAB(rs.getString("DSAI").trim());
                    eve.setEventDate(f2.parse(f2.format(format1.parse(rs.getString("DSAI").trim()))));
                    eve.setSent(false);
                    eve.setNumEve(rs.getString("EVE").trim());
                    eve.setId(serviceManager.getMaxIndexBkEve() + 1);
                    eve.setType(TypeEvent.salaire);
                    if (bkCli != null && serviceManager.getBkEveByCriteria(eve.getNumEve(), eve.getEventDate(), eve.getCompte()).isEmpty()) {
                        msg = "Chargement données salaires BKMVTI.... " + eve.getCompte();
                        logger.info(msg);
                        BottomPanel.settextLabel(msg, Color.BLACK);
                        serviceManager.enregistrer(eve);
                        String q = "SELECT b.NUM, b.CLI, b.TYP FROM bktelcli b WHERE b.CLI='" + rs.getString("NCP1").trim().substring(3, 9) + "'";
                        PreparedStatement pss = conn.prepareStatement(q);
                        Throwable var16 = null;

                        try {
                            ResultSet resultat = pss.executeQuery();
                            int n = 0;

                            while(true) {
                                while(true) {
                                    do {
                                        if (!resultat.next()) {
                                            continue label357;
                                        }
                                    } while(bkCli.getPhone() != 0L);

                                    String code;
                                    String numero;
                                    if (resultat.getString("NUM").replace(" ", "").replace("/", "").trim().length() == 9 && Utils.estUnEntier(resultat.getString("NUM").trim())) {
                                        code = "237";
                                        numero = code + resultat.getString("NUM").trim();
                                        if (bkCli.getPhone() != Long.parseLong(numero)) {
                                            bkCli.setPhone(Long.parseLong(numero));
                                            if (n == 0) {
                                                msg = "Mise à jour numero client.... " + bkCli.getCode();
                                                logger.info(msg);
                                                BottomPanel.settextLabel(msg, Color.BLACK);
                                                serviceManager.modifier(bkCli);
                                                ++n;
                                            }
                                        }
                                    } else if (resultat.getString("NUM").replace(" ", "").replace("/", "").trim().length() == 8 && Utils.estUnEntier(resultat.getString("NUM").trim())) {
                                        code = "241";
                                        numero = code + resultat.getString("NUM").trim();
                                        if (bkCli.getPhone() != Long.parseLong(numero)) {
                                            bkCli.setPhone(Long.parseLong(numero));
                                            if (n == 0) {
                                                msg = "Mise à jour numero client.... " + bkCli.getCode();
                                                logger.info(msg);
                                                BottomPanel.settextLabel(msg, Color.BLACK);
                                                serviceManager.modifier(bkCli);
                                                ++n;
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Throwable var42) {
                            var16 = var42;
                            throw var42;
                        } finally {
                            if (pss != null) {
                                if (var16 != null) {
                                    try {
                                        pss.close();
                                    } catch (Throwable var41) {
                                        var16.addSuppressed(var41);
                                    }
                                } else {
                                    pss.close();
                                }
                            }

                        }
                    }
                }
            }
        } catch (Throwable var44) {
            var7 = var44;
            throw var44;
        } finally {
            if (ps != null) {
                if (var7 != null) {
                    try {
                        ps.close();
                    } catch (Throwable var40) {
                        var7.addSuppressed(var40);
                    }
                } else {
                    ps.close();
                }
            }

        }

        conn.close();
    }

    public static void envoieSMSSalaire() {
        if (Utils.checkLicence()) {
            List<BkEve> list = serviceManager.getBkEveBySendParam(false, listString, TypeEvent.salaire);
            list.forEach((eve) -> {
                BkCli bkCli = eve.getCli();
                if (bkCli != null && eve.getOpe() != null && !"".equals(methode) && bkCli.isEnabled() && serviceManager.getBkCompCliByCriteria(bkCli, eve.getCompte(), true) != null && bkCli.getPhone() != 0L) {
                    MessageFormat mf = serviceManager.getFormatByBkOpe(eve.getOpe(), bkCli.getLangue());
                    if (mf != null) {
                        String text = Utils.remplacerVariable(bkCli, eve.getOpe(), eve, mf);
                        String res = testConnexionInternet();
                        String msg = "Test connexion ...." + res;
                        logger.info(msg);
                        BottomPanel.settextLabel(msg, Color.BLACK);
                        if (res.equals("OK")) {
                            msg = "Envoie du Message à.... " + eve.getCompte();
                            logger.info(msg);
                            BottomPanel.settextLabel(msg, Color.BLACK);
                            switch (methode) {
                                case "METHO1":
                                case "METHO2":
                                    Sender.send(urlParam, "" + bkCli.getPhone(), text);
                                    break;
                            }
                        } else {
                            msg = "Message non envoyé à.... " + eve.getCompte() + " Problème de connexion internet!!";
                            logger.info(msg);
                            BottomPanel.settextLabel(msg, Color.RED);
                        }

                        Message message = new Message();
                        message.setTitle(eve.getOpe().getLib());
                        message.setContent(text);
                        message.setBkEve(eve);
                        message.setSendDate(new Date());
                        message.setNumero(Long.toString(bkCli.getPhone()));
                        if (res.equals("OK")) {
                            serviceManager.enregistrer(message);
                            eve.setSent(true);
                            serviceManager.modifier(eve);
                            msg = "OK Message envoyé ";
                            logger.info(msg);
                            BottomPanel.settextLabel("OK Message envoyé ", Color.BLACK);
                        }
                    }
                }

            });
        } else {
            BottomPanel.settextLabel("Message non envoyé Problème de Licence veuillez contacter le fournieur 1.2.5 !!", Color.RED);
        }

    }

    public static void ServiceCredit() throws SQLException, ParseException {
        String msg = "Traitement des credits en cours.... ";
        logger.info(msg);
        BottomPanel.settextLabel(msg, Color.BLACK);
        SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");
        RemoteDB remoteDB = serviceManager.getDefaultRemoteDB(true);
        String decryptedString = AES.decrypt(remoteDB.getPassword(), "LACUS2017");
        conn = DriverManager.getConnection(remoteDB.getUrl(), remoteDB.getName(), decryptedString);
        Date current = new Date();
        String currentString = f2.format(current);
        int compteur = 0;
        if (serviceManager.getMaxIndexBkEve(TypeEvent.credit) != null) {
            compteur = serviceManager.getMaxIndexBkEve(TypeEvent.credit);
        }

        String query;
        if (compteur == 0) {
            query = "SELECT b.NCP AS NCP1,b.CAI,b.OPE,b.EVE,b.MNT,b.HEU,b.ETA FROM BKMAC b WHERE b.NCP >= '0' AND b.CAI >= '0' ORDER BY HEU ASC";
        } else {
            BkEve bkEve = serviceManager.getBkEveById(compteur);
            String hsai = bkEve.getHsai();
            Date date = bkEve.getEventDate();
            if (!currentString.equals(f2.format(date))) {
                query = "SELECT b.NCP AS NCP1,b.CAI,b.OPE,b.EVE,b.MNT,b.HEU,b.ETA FROM BKMAC b WHERE b.NCP >= '0' AND b.CAI >= '0' ORDER BY HEU ASC";
            } else {
                if (Integer.parseInt(hsai) > 1) {
                    hsai = Integer.parseInt(hsai) - 100 + "";
                    if (hsai.length() == 3) {
                        hsai = "0" + hsai;
                    }
                }

                query = "SELECT b.NCP AS NCP1,b.CAI,b.OPE,b.EVE,b.MNT,b.HEU,b.ETA FROM BKMAC b WHERE b.NCP >= '0' AND b.CAI >= '0' AND HEU > '" + hsai + "'  ORDER BY HEU ASC";
            }
        }

        PreparedStatement ps = conn.prepareStatement(query);
        Throwable var73 = null;

        try {
            ResultSet rs = ps.executeQuery();

            label766:
            while(true) {
                BkEve eve;
                BkCli bkCli;
                boolean traitement;
                do {
                    do {
                        do {
                            do {
                                if (!rs.next()) {
                                    break label766;
                                }

                                msg = "Recherche évenements credits.... ";
                                logger.info(msg);
                                BottomPanel.settextLabel(msg, Color.BLACK);
                            } while(rs.getString("NCP1") == null);
                        } while(rs.getString("CAI") == null);
                    } while(rs.getString("NCP1").trim().length() < 10);

                    PreparedStatement psss = conn.prepareStatement("SELECT AGE FROM BKCOM WHERE NCP = '" + rs.getString("NCP1").trim() + "'");
                    Throwable var14 = null;

                    String age;
                    try {
                        ResultSet result = psss.executeQuery();

                        for(age = null; result.next(); age = result.getString("AGE").trim()) {
                        }
                    } catch (Throwable var66) {
                        var14 = var66;
                        throw var66;
                    } finally {
                        if (psss != null) {
                            if (var14 != null) {
                                try {
                                    psss.close();
                                } catch (Throwable var65) {
                                    var14.addSuppressed(var65);
                                }
                            } else {
                                psss.close();
                            }
                        }

                    }

                    eve = new BkEve();
                    BkAgence bkAgence;
                    if (age != null) {
                        bkAgence = serviceManager.getBkAgenceById(age);
                    } else {
                        bkAgence = serviceManager.getBkAgenceById("00200");
                    }

                    eve.setBkAgence(bkAgence);
                    String cli = "";
                    if (rs.getString("NCP1").trim().length() >= 9) {
                        cli = rs.getString("NCP1").trim().substring(3, 9);
                    }

                    bkCli = serviceManager.getBkCliById(cli);
                    if (bkCli == null) {
                        bkCli = serviceManager.getBkCliByNumCompte(rs.getString("NCP1").trim());
                    }

                    eve.setCli(bkCli);
                    eve.setCompte(rs.getString("NCP1").trim());
                    eve.setEtat(rs.getString("ETA").trim());
                    eve.setHsai(rs.getString("HEU").trim());
                    eve.setMont(Double.parseDouble(rs.getString("MNT").trim().replace(".", "")));
                    eve.setMontant(rs.getString("MNT").trim().replace(".", "").replace(" ", ""));
                    BkOpe bkOpe = serviceManager.getBkOpeById(rs.getString("OPE").trim());
                    eve.setOpe(bkOpe);
                    eve.setDVAB(f2.format(new Date()));
                    eve.setEventDate(f2.parse(f2.format(new Date())));
                    eve.setSent(false);
                    eve.setNumEve(rs.getString("EVE").trim());
                    eve.setId(serviceManager.getMaxIndexBkEve() + 1);
                    eve.setType(TypeEvent.credit);
                    traitement = bkCli != null;

                    if (!serviceManager.getBkEveByCriteria(eve.getNumEve(), eve.getEventDate(), eve.getCompte()).isEmpty()) {
                        traitement = false;
                    }

                    if (!serviceManager.getBkEveByCriteriaMontant(eve.getNumEve(), eve.getCompte(), eve.getNumEve()).isEmpty()) {
                        traitement = false;
                    }

                    if (!serviceManager.getBkEveByPeriode(eve.getNumEve(), eve.getCompte(), Utils.add(eve.getEventDate(), -3L), eve.getEventDate()).isEmpty()) {
                        traitement = false;
                    }
                } while(!traitement);

                msg = "Chargement données credits.... " + eve.getCompte();
                logger.info(msg);
                BottomPanel.settextLabel(msg, Color.BLACK);
                serviceManager.enregistrer(eve);
                String q = "SELECT b.NUM, b.CLI, b.TYP FROM bktelcli b WHERE b.CLI='" + rs.getString("NCP1").trim().substring(3, 9) + "'";
                PreparedStatement pss = conn.prepareStatement(q);
                Throwable var21 = null;

                try {
                    ResultSet resultat = pss.executeQuery();
                    int n = 0;

                    while(true) {
                        while(true) {
                            do {
                                if (!resultat.next()) {
                                    continue label766;
                                }
                            } while(bkCli.getPhone() != 0L);

                            String code;
                            String numero;
                            if (resultat.getString("NUM").replace(" ", "").replace("/", "").trim().length() == 9 && Utils.estUnEntier(resultat.getString("NUM").trim())) {
                                code = "237";
                                numero = code + resultat.getString("NUM").trim();
                                if (bkCli.getPhone() != Long.parseLong(numero)) {
                                    bkCli.setPhone(Long.parseLong(numero));
                                    if (n == 0) {
                                        msg = "Mise à jour numero client.... " + bkCli.getCode();
                                        logger.info(msg);
                                        BottomPanel.settextLabel(msg, Color.BLACK);
                                        serviceManager.modifier(bkCli);
                                        ++n;
                                    }
                                }
                            } else if (resultat.getString("NUM").replace(" ", "").replace("/", "").trim().length() == 8 && Utils.estUnEntier(resultat.getString("NUM").trim())) {
                                code = "241";
                                numero = code + resultat.getString("NUM").trim();
                                if (bkCli.getPhone() != Long.parseLong(numero)) {
                                    bkCli.setPhone(Long.parseLong(numero));
                                    if (n == 0) {
                                        msg = "Mise à jour numero client.... " + bkCli.getCode();
                                        logger.info(msg);
                                        BottomPanel.settextLabel(msg, Color.BLACK);
                                        serviceManager.modifier(bkCli);
                                        ++n;
                                    }
                                }
                            }
                        }
                    }
                } catch (Throwable var68) {
                    var21 = var68;
                    throw var68;
                } finally {
                    if (pss != null) {
                        if (var21 != null) {
                            try {
                                pss.close();
                            } catch (Throwable var64) {
                                var21.addSuppressed(var64);
                            }
                        } else {
                            pss.close();
                        }
                    }

                }
            }
        } catch (Throwable var70) {
            var73 = var70;
            throw var70;
        } finally {
            if (ps != null) {
                if (var73 != null) {
                    try {
                        ps.close();
                    } catch (Throwable var63) {
                        var73.addSuppressed(var63);
                    }
                } else {
                    ps.close();
                }
            }

        }

        conn.close();
    }

    public static void envoieSMSCredit() {
        if (Utils.checkLicence()) {
            List<BkEve> list = serviceManager.getBkEveBySendParam(false, listString, TypeEvent.credit);
            list.forEach((eve) -> {
                BkCli bkCli = eve.getCli();
                if (bkCli != null && eve.getOpe() != null && !"".equals(methode) && bkCli.isEnabled() && serviceManager.getBkCompCliByCriteria(bkCli, eve.getCompte(), true) != null && bkCli.getPhone() != 0L) {
                    MessageFormat mf = serviceManager.getFormatByBkOpe(eve.getOpe(), bkCli.getLangue());
                    if (mf != null) {
                        String text = Utils.remplacerVariable(bkCli, eve.getOpe(), eve, mf);
                        String res = testConnexionInternet();
                        BottomPanel.settextLabel("Test connexion ...." + res, Color.BLACK);
                        if (res.equals("OK")) {
                            BottomPanel.settextLabel("Envoie du Message à.... " + eve.getCompte(), Color.BLACK);
                            switch (methode) {
                                case "METHO1":
                                case "METHO2":
                                    Sender.send(urlParam, "" + bkCli.getPhone(), text);
                                    break;
                            }
                        } else {
                            String msg1 = "Message non envoyé à.... " + eve.getCompte() + " Problème de connexion internet!!";
                            logger.info(msg1);
                            BottomPanel.settextLabel(msg1, Color.RED);
                        }

                        Message message = new Message();
                        message.setTitle(eve.getOpe().getLib());
                        message.setContent(text);
                        message.setBkEve(eve);
                        message.setSendDate(new Date());
                        message.setNumero(Long.toString(bkCli.getPhone()));
                        if (res.equals("OK")) {
                            serviceManager.enregistrer(message);
                            eve.setSent(true);
                            serviceManager.modifier(eve);
                            String msg = "OK Message envoyé ";
                            logger.info(msg);
                            BottomPanel.settextLabel(msg, Color.BLACK);
                        }
                    }
                }

            });
        } else {
            String msg = "Message non envoyé Problème de Licence veuillez contacter le fournieur 2.0 !!";
            logger.info(msg);
            BottomPanel.settextLabel(msg, Color.RED);
        }

    }

    public static void ServiceMandat() throws SQLException, ParseException {
        String msg = "Traitement des mandats en cours.... ";
        logger.info(msg);
        BottomPanel.settextLabel(msg, Color.BLACK);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");
        String secretKey = "LACUS2017";
        RemoteDB remoteDB = serviceManager.getDefaultRemoteDB(true);
        String decryptedString = AES.decrypt(remoteDB.getPassword(), "LACUS2017");
        conn = DriverManager.getConnection(remoteDB.getUrl(), remoteDB.getName(), decryptedString);
        new SimpleDateFormat("dd-MM-yyyy");
        int compteur = serviceManager.getMaxBkMad();
        String query = "";
        if (compteur == 0) {
            query = "SELECT b.CLESEC,b.CTR,b.AGE,b.DCO AS DSAI,b.OPE,b.EVE,b.DBD,b.AD1P,b.AD2P,b.MNT FROM BKMAD b WHERE MNT > '0' ORDER BY DCO ASC";
        } else {
            BkMad bkMad = serviceManager.getBkMadById(compteur);
            String[] date = bkMad.getDco().substring(0, 10).split("-");
            String s = date[2] + "-" + date[1] + "-" + date[0];
            query = "SELECT b.CLESEC,b.CTR,b.AGE,b.DCO AS DSAI,b.OPE,b.EVE,b.DBD,b.AD1P,b.AD2P,b.MNT FROM BKMAD b WHERE MNT > '0' AND DCO >= '" + s + "' ORDER BY DCO ASC";
        }

        System.err.println("Resquete " + query);
        PreparedStatement ps = conn.prepareStatement(query);
        Throwable var27 = null;

        try {
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                msg = "Recherche données de manadats.... ";
                logger.info(msg);
                BottomPanel.settextLabel(msg, Color.BLACK);
                BkMad bkMad = serviceManager.getBkMadByClesec(rs.getString("CLESEC").trim());
                if (bkMad == null) {
                    BkMad eve = new BkMad();
                    BkAgence bkAgence = serviceManager.getBkAgenceById(rs.getString("AGE").trim());
                    eve.setAge(bkAgence);
                    eve.setMnt(rs.getString("MNT").trim());
                    BkOpe bkOpe = serviceManager.getBkOpeById(rs.getString("OPE").trim());
                    eve.setOpe(bkOpe);
                    eve.setDco(rs.getString("DSAI").trim());
                    eve.setDateEnvoie(f2.parse(f2.format(format1.parse(rs.getString("DSAI").trim()))));
                    if (rs.getString("AD1P") != null) {
                        eve.setAd1p(rs.getString("AD1P").trim());
                    }

                    if (rs.getString("AD2P") != null) {
                        eve.setAd2p(rs.getString("AD2P").trim());
                    }

                    eve.setSent(false);
                    eve.setEve(rs.getString("EVE").trim());
                    eve.setClesec(rs.getString("CLESEC").trim());
                    eve.setId(serviceManager.getMaxBkMad() + 1);
                    switch (rs.getString("CTR").trim()) {
                        case "9":
                            eve.setTraite(1);
                            break;
                        case "0":
                            eve.setTraite(0);
                    }

                    eve.setCtr(rs.getString("CTR").trim());
                    msg = "Chargement données salaires.... " + eve.getAd1p();
                    logger.info(msg);
                    BottomPanel.settextLabel(msg, Color.BLACK);
                    serviceManager.enregistrer(eve);
                } else if (!bkMad.getCtr().equals("9") && rs.getString("CTR").trim().equals("9")) {
                    bkMad.setDbd(rs.getString("DBD").trim());
                    bkMad.setDateRetrait(f2.parse(f2.format(format1.parse(rs.getString("DBD").trim()))));
                    bkMad.setCtr("9");
                    bkMad.setTraite(1);
                    serviceManager.modifier(bkMad);
                }
            }
        } catch (Throwable var24) {
            var27 = var24;
            throw var24;
        } finally {
            if (ps != null) {
                if (var27 != null) {
                    try {
                        ps.close();
                    } catch (Throwable var23) {
                        var27.addSuppressed(var23);
                    }
                } else {
                    ps.close();
                }
            }

        }

        conn.close();
    }

    public static void envoieSMSMandat() {
        boolean bon = false;
        String msg;
        msg = "Debut envoie de message";
        logger.info(msg);
        BottomPanel.settextLabel(msg, Color.BLACK);
        if (Utils.checkLicence()) {
            List<BkMad> list = serviceManager.getBkMadByTraite();

            for (BkMad eve : list) {
                if (testPhone(eve.getAd1p()) != null && testPhone(eve.getAd2p()) != null && eve.getOpe() != null && !"".equals(methode)) {
                    MessageFormat mf = serviceManager.getFormatByBkOpe(eve.getOpe(), "FR");
                    if (mf != null) {
                        String text = Utils.remplacerVariable(eve, mf);
                        String res = testConnexionInternet();
                        BottomPanel.settextLabel("Test connexion ...." + res, Color.BLACK);
                        if (res.equals("OK")) {
                            if (eve.getTraite() == 0) {
                                BottomPanel.settextLabel("Envoie du Message à.... " + eve.getAd2p(), Color.BLACK);
                                switch (methode) {
                                    case "METHO1":
                                        Sender.send(urlParam, "" + eve.getAd2p(), text);
                                        break;
                                    case "METHO2":
                                        Sender.send(urlParam, "" + eve.getAd2p(), text);
                                }
                            } else if (eve.getTraite() == 1) {
                                mf = serviceManager.getFormatByBkOpe(serviceManager.getBkOpeById("100"), "FR");
                                if (mf != null) {
                                    text = Utils.remplacerVariable(eve, mf);
                                    bon = true;
                                    BottomPanel.settextLabel("Envoie du Message à.... " + eve.getAd1p(), Color.BLACK);
                                    switch (methode) {
                                        case "METHO1":
                                            Sender.send(urlParam, "" + eve.getAd1p(), text);
                                            break;
                                        case "METHO2":
                                            Sender.send(urlParam, "" + eve.getAd1p(), text);
                                    }
                                }
                            }
                        } else {
                            msg = "Message non envoyé problème de connexion internet!!";
                            logger.info(msg);
                            BottomPanel.settextLabel(msg, Color.RED);
                        }

                        MessageMandat message = new MessageMandat();
                        message.setTitle(eve.getOpe().getLib());
                        message.setContent(text);
                        message.setBkMad(eve);
                        message.setSendDate(new Date());
                        if (res.equals("OK")) {
                            if (eve.getTraite() == 0) {
                                eve.setTraite(1);
                                message.setNumero(eve.getAd2p());
                                serviceManager.enregistrer(message);
                            } else if (eve.getTraite() == 1 && bon) {
                                eve.setTraite(2);
                                eve.setSent(true);
                                message.setNumero(eve.getAd1p());
                                serviceManager.enregistrer(message);
                            }

                            serviceManager.modifier(eve);
                            msg = "OK Message envoyé ";
                            logger.info(msg);
                            BottomPanel.settextLabel(msg, Color.BLACK);
                        }
                    }
                }
            }
        } else {
            msg = "Message non envoyé Problème de Licence veuillez contacter le fournieur 2.0 !!";
            BottomPanel.settextLabel(msg, Color.RED);
        }

    }

    public static String testPhone(String num) {
        String res = null;
        if (Utils.estUnEntier(num)) {
            if (num.length() == 8) {
                res = "241" + num;
            } else if (num.length() == 9) {
                res = "237" + num;
            }
        }

        return res;
    }

}
