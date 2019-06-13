package com.abouna.lacussms.main;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.config.service.QueryService;
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
import com.abouna.lacussms.entities.TypeService;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.LicencePanel;
import com.abouna.lacussms.views.SendAddressPanel;
import com.abouna.lacussms.views.main.BottomPanel;
import com.abouna.lacussms.views.main.MainFrame;
import com.abouna.lacussms.views.main.SplashFrame;
import com.abouna.lacussms.views.tools.AES;
import com.abouna.lacussms.views.tools.ConstantUtils;
import com.abouna.lacussms.views.tools.MethodUtils;
import com.abouna.lacussms.views.tools.Utils;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Hello world!
 *
 */
public class App {

    //mysql-eabouna.alwaysdata.net
    static LacusSmsService serviceManager;
    private static Thread thread;
    private static boolean running, running1, running2, running3;
    private static Connection conn = null;
    private static String lic, urlParam, methode, condition = "";
    private static String urlMessage;
    final static Logger logger = Logger.getLogger(App.class);
    private static List<String> listString = new ArrayList<>();
    static boolean appliRun = false;
    private static SplashFrame splashFrame;
    public static String username = null;
    public static String licenceString = "";
    static boolean vl = false;
    private static RemoteDB remoteDB;

    public static boolean isAppliRun() {
        return appliRun;
    }

    public static boolean compareDate(String d1, String d2) {
        boolean r;
        try {
            int a = Integer.parseInt(d1.substring(0, 2));
            int a1 = Integer.parseInt(d2.substring(0, 2));
            int b = Integer.parseInt(d1.substring(2, 4));
            int b1 = Integer.parseInt(d2.substring(2, 4));
            int c = Integer.parseInt(d1.substring(4, 6));
            int c1 = Integer.parseInt(d2.substring(4, 6));
            if (c < c1) {
                r = true;
            } else if (c == c1) {
                if (b < b1) {
                    r = true;
                } else if (b == b1) {
                    r = a <= a1;
                } else {
                    r = false;
                }
            } else {
                r = false;
            }
            return r;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static String decript(String s) {
        String result = "";
        for (int i = 2; i < s.length(); i += 3) {
            char c = s.charAt(i);
            result += "" + c;
            if (result.length() == 6) {
                break;
            }
        }
        return result;
    }

    public static void verifyComputer() throws IOException {
        Utils.createAppDirectory();
        if (!Utils.verify()) {
            serviceManager.viderLicence();
        }
    }

    public static boolean checkLicence() {
        try {
            boolean val = false;
            String secretKey = ConstantUtils.SECRET_KEY;
            SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
            Date date = new Date();//Utils.getTimeFromInternet();
            String dest = format.format(date);
            List<Licence> licences = serviceManager.getLicences();
            String s = "a";
            long jour = 0;
            if (licences.isEmpty()) {
                val = false;
            } else {
                for (Licence li : licences) {
                    s = AES.decrypt(li.getValeur(), secretKey) == null ? "" : AES.decrypt(li.getValeur(), secretKey);
                    jour = li.getJour();
                    licenceString = li.getValeur();
                }
                if (s.length() == 12) {
                    val = App.compareDate(dest, s.substring(3, 9)) || jour > 0;
                }
            }
            return val;
        } catch (Exception ex) {
            serviceManager.viderLicence();
            return false;
        }
    }

    public static String bytesToHex(byte[] b) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder buffer = new StringBuilder();
        for (int j = 0; j < b.length; j++) {
            buffer.append(hexDigits[(b[j] >> 4) & 0x0f]);
            buffer.append(hexDigits[b[j] & 0x0f]);
        }
        return buffer.toString();
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        splashFrame = new SplashFrame();
        initApp();
    }

    public static void initApp() throws IOException, ClassNotFoundException {
        //logger.info("app startup");
        serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        if (serviceManager.getAllConfig().isEmpty()) {
            serviceManager.enregistrerConfig(new Config(true, true, true, true));
        }
        urlParam = serviceManager.getDefaultUrlMessage() == null ? "" : serviceManager.getDefaultUrlMessage().getUrlValue();
        methode = serviceManager.getDefaultUrlMessage() == null ? "" : serviceManager.getDefaultUrlMessage().getMethode();
        urlMessage = serviceManager.getDefaultUrlMessage() == null ? "" : serviceManager.getDefaultUrlMessage().getRoot();
        remoteDB = serviceManager.getDefaultRemoteDB(true);
        System.out.println("Root: " + urlMessage + "URL: " + urlParam + " Methode : " + methode);
        List<BkEtatOp> list = serviceManager.getListBkEtatOp(true);
        int taille = list.size();
        int i = 0;
        condition = "";
        listString = new ArrayList<>();
        for (BkEtatOp op : list) {
            listString.add(op.getValeur());
            if (i != taille - 1) {
                condition += "b.ETA='" + op.getValeur() + "' OR ";
            } else {
                condition += "b.ETA='" + op.getValeur() + "'";
            }
            i++;
        }
        verifyComputer();
        final boolean b = true;//App.checkLicence();
        try {
            if (b) {
                vl = true;//Utils.existAndUsed(licenceString, Utils.getConnection());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Problème de connexion assurez vous que votre poste\r\n "
                    + "est connecté à internet ou verifiez votre parfeu\r\n "
                    + "s'il ne bloque pas le connexion vers le serveur (alwaysdata.com)!");
        }
        try {
            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
        } catch (InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }

        SwingUtilities.invokeLater(() -> {
            try {
                /*if (vl == false) {
                    JOptionPane.showMessageDialog(null, "Votre licence n'est plus valide\r\n "
                            + "merci de cliquer sur ok pour enregistrer une nouvelle\r\n "
                            + "ou contactez votre fournisseur!");
                }*/
                if (b == false || vl == false) {
                    SendAddressPanel nouveau1 = new SendAddressPanel();
                    nouveau1.setSize(450, 150);
                    nouveau1.setLocationRelativeTo(null);
                    nouveau1.setModal(true);
                    nouveau1.setResizable(false);
                    nouveau1.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    nouveau1.setVisible(true);
                    nouveau1.setUndecorated(true);
                    nouveau1.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
                } else {
                    appliRun = true;
                    System.out.println("Demarrage.....");
                    MainFrame frame = new MainFrame();
                    frame.setSize(1000, 600);
                    frame.setLocationRelativeTo(null);
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    //splashFrame.close();
                    frame.setVisible(true);
                }

            } catch (IOException ex) {

            } catch (XmlPullParserException ex) {
                java.util.logging.Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public static boolean testLicence() {
        final boolean b = App.checkLicence();
        if (b == false) {
            LicencePanel nouveau1 = new LicencePanel(null);
            nouveau1.setSize(450, 150);
            nouveau1.setLocationRelativeTo(null);
            nouveau1.setModal(true);
            nouveau1.setResizable(false);
            nouveau1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            nouveau1.setVisible(true);
            nouveau1.setUndecorated(true);
            nouveau1.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        }
        return b;
    }

    public static void test() {
        List<BkEve> list = serviceManager.getBkEveBySendParam(true, listString);
        NumberFormat formatnum = NumberFormat.getCurrencyInstance();
        formatnum.setMinimumFractionDigits(0);
        list.stream().map((eve) -> {
            System.out.println(eve.toString());
            return eve;
        }).forEach((eve) -> {
            System.out.println(" Montant : " + Double.toString(eve.getMont()) + " " + Utils.moveZero(eve.getMont()));
        });
    }

    public static boolean testConnexion() {
        logger.info("### Test de connexion de la BD ###");
        try {
            TimeZone timeZone = TimeZone.getTimeZone("GMT");
            TimeZone.setDefault(timeZone);
            if (remoteDB != null) {
                Class.forName(remoteDB.getDriverClassName());
                System.out.println("URL: " + remoteDB.getUrl());
                System.out.println("Username: " + remoteDB.getName());
                System.out.println("Password: " + remoteDB.getPassword());
                setConnection();
                if (conn != null) {
                    return true;
                }
            }
        } catch (NullPointerException | ClassNotFoundException | SQLException ex) {
            System.out.println("problème de connexion bd " + ex.getMessage());
            return false;
        }
        return false;
    }

    //la fonction 
    public static void demarrerServiceData() {
        try {
            TimeZone timeZone = TimeZone.getTimeZone("GMT");
            TimeZone.setDefault(timeZone);
            Class.forName(remoteDB.getDriverClassName());
            Thread t = new Thread(() -> {
                running1 = true;
                while (running1) {
                    Config config = serviceManager.getAllConfig().get(0);
                    if (config.isEvent()) {
                        try {
                            serviceEvenement();
                        } catch (SQLException ex) {
                            BottomPanel.settextLabel("Echec de connexion à la base de données", Color.RED);
                        } catch (ParseException ex) {
                            BottomPanel.settextLabel("Problème de formatage de la date", Color.RED);
                        }
                    }
                    if (config.isBkmpai()) {
                        try {
                            ServiceSalaire();
                            ServiceSalaireBKMVTI();
                        } catch (SQLException ex) {
                            BottomPanel.settextLabel("Echec de connexion à la base de données", Color.RED);
                        } catch (ParseException ex) {
                            BottomPanel.settextLabel("Problème de formatage de la date", Color.RED);
                        }
                    }
                    if (config.isBkmac()) {
                        try {
                            ServiceCredit();
                        } catch (SQLException ex) {
                            BottomPanel.settextLabel("Echec de connexion à la base de données", Color.RED);
                        } catch (ParseException ex) {
                            BottomPanel.settextLabel("Problème de formatage de la date", Color.RED);
                        }
                    }
                    if (config.isMandat()) {
                        try {
                            ServiceMandat();
                        } catch (SQLException ex) {
                            BottomPanel.settextLabel("Echec de connexion à la base de données", Color.RED);
                        } catch (ParseException ex) {
                            BottomPanel.settextLabel("Problème de formatage de la date", Color.RED);
                        }
                    }
                }
            });
            t.start();
        } catch (ClassNotFoundException ex) {
            BottomPanel.settextLabel("Problème de chargement du pilote", java.awt.Color.red);
        }
    }

    //fonction permettant de démarrer le service d'envoie des SMS
    public static void demarrerServiceSms() {
        thread = new Thread(() -> {
            System.out.println("Démarrage du service...");
            running = true;
            while (running) {
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

    //fonction permettant de démarrer le service d'envoie des SMS
    public static void demarrerServiceRequete() {
        thread = new Thread(() -> {
            System.out.println("Démarrage du service...");
            running3 = true;
            while (running3) {
                try {
                    ServiceRequete();
                } catch (SQLException | ParseException ex) {
                     BottomPanel.settextLabel("erreur lors de l'execution du service ...", java.awt.Color.RED);
                }
            }
        });
        thread.start();
    }

    public static void testAffichage() {
        Thread t;
        t = new Thread(() -> {
            int i = 1;
            System.out.println("Démarrage....");
            running = true;
            while (running) {
                try {

                    BottomPanel.settextLabel("Traitement.... " + i, java.awt.Color.BLACK);
                    i++;
                    if (i % 10 == 0) {
                        BottomPanel.settextLabel("Chargement des données.... " + i, java.awt.Color.RED);
                    }
                    Thread.sleep(500);

                } catch (InterruptedException ex) {
                }
            }
        });
        t.start();
    }

    //fonction permettant de démarrer le service d'envoie des SMS
    public static void demarrerServiceSequenciel() {
        try {
            TimeZone timeZone = TimeZone.getTimeZone("GMT");
            TimeZone.setDefault(timeZone);
            Class.forName(remoteDB.getDriverClassName());
            Thread threads;
            threads = new Thread(() -> {
                BottomPanel.settextLabel("Démarrage du service...", java.awt.Color.BLACK);
                running2 = true;
                while (running2) {
                    Config config = serviceManager.getAllConfig().get(0);
                    if (config.isEvent()) {
                        try {
                            serviceEvenement();
                        } catch (SQLException ex) {
                            BottomPanel.settextLabel("Echec de connexion à la base de données", java.awt.Color.RED);
                        } catch (ParseException ex) {
                            BottomPanel.settextLabel("Problème de formatage de la date", java.awt.Color.RED);
                        }
                        envoieSMSEvenement();
                    }
                    if (config.isBkmpai()) {
                        try {
                            ServiceSalaire();
                            ServiceSalaireBKMVTI();
                        } catch (SQLException ex) {
                            BottomPanel.settextLabel("Problème de connexion sur la base de données", java.awt.Color.RED);
                        } catch (ParseException ex) {
                            BottomPanel.settextLabel("Problème de formatage de la date", java.awt.Color.RED);
                        }
                        envoieSMSSalaire();
                    }
                    if (config.isBkmac()) {
                        try {
                            ServiceCredit();
                        } catch (SQLException ex) {
                            BottomPanel.settextLabel("Problème de connexion sur la base de données", java.awt.Color.RED);
                        } catch (ParseException ex) {
                            BottomPanel.settextLabel("Problème de formatage de la date", java.awt.Color.RED);
                        }
                        envoieSMSCredit();
                    }
                    if (config.isMandat()) {
                        try {
                            ServiceMandat();
                        } catch (SQLException ex) {
                            BottomPanel.settextLabel("Problème de connexion sur la base de données", java.awt.Color.RED);
                        } catch (ParseException ex) {
                            BottomPanel.settextLabel("Problème de formatage de la date", java.awt.Color.RED);
                        }
                        envoieSMSMandat();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        break;
                    }
                }
            });
            threads.start();
        } catch (ClassNotFoundException ex) {
            BottomPanel.settextLabel("Problème de chargement du pilote", java.awt.Color.red);
        }
    }

    public static void serviceEvenement() throws SQLException, ParseException {
        setConnection();
        int compteur = serviceManager.getMaxIndexBkEve(TypeEvent.ordinaire);
        String heure, date, currentString;
        Date current = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");
        String query, query1, finalquery, heureInit = "00:00:00.000";
        if (compteur == 0) {
            heure = "00:00:00.000";
            date = format.format(current);
            if (!condition.equals("")) {
                finalquery = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM BKEVE b WHERE b.NCP1 >= '0' AND b.DSAI = '" + date + "' AND b.HSAI > '" + heure + "'" + " AND (" + condition + ")" + "  ORDER BY b.DSAI,b.HSAI ASC";
            } else {
                finalquery = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM BKEVE b WHERE b.NCP1 >= '0' AND b.DSAI = '" + date + "' AND b.HSAI > '" + heure + "'" + "  ORDER BY b.DSAI,b.HSAI ASC";
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
                if (!condition.equals("")) {
                    query = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM BKEVE b WHERE b.NCP1 >= '0' AND b.DSAI = '" + date + "' AND b.HSAI > '" + heure + "'" + " AND (" + condition + ")";
                    query1 = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM BKEVE b WHERE b.NCP1 >= '0' AND b.DSAI = '" + currentString + "' AND b.HSAI > '" + heureInit + "'" + " AND (" + condition + ")";
                    finalquery = "(" + query + ") UNION (" + query1 + ")" + suffix;
                } else {
                    query = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM BKEVE b WHERE b.NCP1 >= '0' AND b.DSAI = '" + date + "' AND b.HSAI > '" + heure + "'";
                    query1 = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM BKEVE b WHERE b.NCP1 >= '0' AND b.DSAI = '" + currentString + "' AND b.HSAI > '" + heureInit + "'";
                    finalquery = "(" + query + ") UNION (" + query1 + ")" + suffix;
                }
            } else {
                if (!condition.equals("")) {
                    query = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM BKEVE b WHERE b.NCP1 >= '0' AND b.DSAI = '" + date + "' AND b.HSAI > '" + heure + "'" + " AND (" + condition + ")";
                    finalquery = query + suffix;
                } else {
                    query = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM BKEVE b WHERE b.NCP1 >= '0' AND b.DSAI = '" + date + "' AND b.HSAI > '" + heure + "'";
                    finalquery = query + suffix;
                }
            }
        }
        
        finalquery = QueryService.buildQuery(finalquery, serviceManager.getParametreRequeteValues(TypeService.EVENEMENT), TypeService.EVENEMENT);
        
        if(finalquery == null){
            return;
        }
        System.out.println(finalquery);
        
        try (PreparedStatement ps = conn.prepareStatement(finalquery)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BottomPanel.settextLabel("Recherche des évenements en cours.... ", java.awt.Color.BLACK);
                if (rs.getString(1) != null) {
                    if (rs.getString(1).trim().length() >= 10) {
                        BkEve eve = new BkEve();
                        BkAgence bkAgence = serviceManager.getBkAgenceById(rs.getString(10).trim());
                        eve.setBkAgence(bkAgence);
                        BkCli bkCli;
                        String cli = "";
                        if (rs.getString(1).trim().length() >= 9) {
                            cli = rs.getString(1).trim().substring(3, 9);
                        }
                        bkCli = serviceManager.getBkCliById(cli);
                        if (bkCli == null) {
                            bkCli = serviceManager.getBkCliByNumCompte(rs.getString(1).trim());
                        }
                        eve.setCli(bkCli);
                        eve.setCompte(rs.getString(1).trim());
                        eve.setEtat(rs.getString(4).trim());
                        eve.setHsai(rs.getString(6).trim());
                        eve.setMont(Double.parseDouble(rs.getString(9).trim()));
                        eve.setMontant(rs.getString(9).trim());
                        BkOpe bkOpe = serviceManager.getBkOpeById(rs.getString(8).trim());
                        eve.setOpe(bkOpe);
                        eve.setDVAB(rs.getString(5).trim());
                        eve.setEventDate(f2.parse(f2.format(format1.parse(rs.getString(5).trim()))));
                        eve.setSent(false);
                        eve.setNumEve(rs.getString(2).trim());
                        eve.setId(serviceManager.getMaxIndexBkEve() + 1);
                        eve.setType(TypeEvent.ordinaire);
                        
                        boolean traitement = true;
                        
                        if (bkCli == null) {
                            traitement = false;
                        }
                        
                        if (!serviceManager.getBkEveByCriteria(eve.getNumEve(), eve.getEventDate(), eve.getCompte()).isEmpty()) {
                            traitement = false;
                        }
                        
                        if (!serviceManager.getBkEveByPeriode(eve.getNumEve(), eve.getCompte(), Utils.add(eve.getEventDate(), -2), eve.getEventDate()).isEmpty()) {
                            traitement = false;
                        }
                        
                        if (!serviceManager.getBkEveByCriteria(eve.getNumEve(), eve.getCompte(), eve.getHsai(), eve.getMontant()).isEmpty()) {
                            traitement = false;
                        }
                        
                        if (!serviceManager.getBkEveByCriteriaMontant(eve.getNumEve(), eve.getCompte(), eve.getMontant()).isEmpty()) {
                            traitement = false;
                        }
                        
                        if (traitement) {
                            BottomPanel.settextLabel("Chargement données évenement.... " + eve.getCompte(), java.awt.Color.BLACK);
                            serviceManager.enregistrer(eve);
                            
                            String q = "SELECT b.NUM, b.CLI, b.TYP FROM BKTELCLI b WHERE b.CLI='" + cli + "'";
                            q = QueryService.buildQuery(q, serviceManager.getParametreRequeteValues(TypeService.TELEPHONE_CLIENT), TypeService.TELEPHONE_CLIENT);
                                if(q == null){
                                    return;
                                }
                            
                            try (PreparedStatement pss = conn.prepareStatement(q)) {
                                ResultSet resultat = pss.executeQuery();
                                
                                int n = 0;
                                
                                while (resultat.next()) {
                                    if (bkCli.getPhone() == 0) {
                                        String code, numero;
                                        if (resultat.getString(1).replace(" ", "").replace("/", "").trim().length() == 9 && Utils.estUnEntier(resultat.getString(1).trim())) {
                                            code = "237";
                                            numero = code + resultat.getString(1).trim();
                                            if (bkCli.getPhone() != Long.parseLong(numero)) {
                                                bkCli.setPhone(Long.parseLong(numero));
                                                if (n == 0) {
                                                    BottomPanel.settextLabel("Mise à jour numero client.... " + bkCli.getCode(), java.awt.Color.BLACK);
                                                    serviceManager.modifier(bkCli);
                                                    n++;
                                                }
                                            }
                                        } else if (resultat.getString(1).replace(" ", "").replace("/", "").trim().length() == 8 && Utils.estUnEntier(resultat.getString(1).trim())) {
                                            code = "241";
                                            numero = code + resultat.getString(1).trim();
                                            if (bkCli.getPhone() != Long.parseLong(numero)) {
                                                bkCli.setPhone(Long.parseLong(numero));
                                                if (n == 0) {
                                                    BottomPanel.settextLabel("Mise à jour numero client.... " + bkCli.getCode(), java.awt.Color.BLACK);
                                                    serviceManager.modifier(bkCli);
                                                    n++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
            }
        }
        conn.close();
    }

    public static void envoieSMSEvenement() {
        if (checkLicence()) {
            List<BkEve> list = serviceManager.getBkEveBySendParam(false, listString, TypeEvent.ordinaire);
            list.stream().forEach((BkEve eve) -> {
                BkCli bkCli = eve.getCli();
                if (bkCli != null && eve.getOpe() != null && !"".equals(methode)) {
                    if (bkCli.isEnabled() && serviceManager.getBkCompCliByCriteria(bkCli, eve.getCompte(), true) != null && bkCli.getPhone() != 0) {
                        MessageFormat mf = serviceManager.getFormatByBkOpe(eve.getOpe(), bkCli.getLangue());
                        if (mf != null) {
                            String text = Utils.remplacerVariable(bkCli, eve.getOpe(), eve, mf);
                            String res = testConnexionInternet();
                            BottomPanel.settextLabel("Test connexion ...." + res, java.awt.Color.BLACK);
                            if (res.equals("OK")) {
                                BottomPanel.settextLabel("Envoie du Message à.... " + eve.getCompte(), java.awt.Color.BLACK);
                                switch (methode) {
                                    case "METHO1":
                                        App.send(urlParam, "" + bkCli.getPhone(), text);
                                        break;
                                    case "METHO2":
                                        App.send2(urlParam, "" + bkCli.getPhone(), text);
                                        break;
                                }
                            } else {
                                BottomPanel.settextLabel("Message non envoyé à.... " + eve.getCompte() + " Problème de connexion internet!!", java.awt.Color.RED);
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
                                BottomPanel.settextLabel("OK Message envoyé ", java.awt.Color.BLACK);
                            }
                        }
                    }
                }
            });
        } else {
            BottomPanel.settextLabel("Message non envoyé Problème de Licence veuillez contacter le fournieur!!", java.awt.Color.RED);
        }
    }

    public static void stopper() {
        running = false;
        running1 = false;
        running2 = false;
        running3 = false;
        BottomPanel.settextLabel("");
        System.out.println("le service a été arreté");
    }

    public static void send(String username, String pass, String number, String msg, String sid, int fl, int mt, String ipcl) {
        try {
            String postBody = "username=" + URLEncoder.encode(username, "ISO-8859-1") + "&"
                    + "password=" + URLEncoder.encode(pass, "ISO-8859-1") + "&"
                    + "mno=" + URLEncoder.encode(number, "ISO-8859-1") + "&"
                    + "msg=" + URLEncoder.encode(msg, "ISO-8859-1") + "&"
                    + "Sid=" + URLEncoder.encode(sid, "ISO-8859-1") + "&"
                    + "fl=" + URLEncoder.encode("" + fl, "ISO-8859-1") + "&"
                    + "mt=" + URLEncoder.encode("" + mt, "ISO-8859-1") + "&"
                    + "ipcl=" + URLEncoder.encode(ipcl, "ISO-8859-1");
            String link = "https://1s2u.com/sms/sendsms/sendsms.asp?" + postBody;
            URL url = new URL(link);
            HttpURLConnection conn1 = (HttpURLConnection) url.openConnection();
            System.out.println(postBody);
            conn1.setRequestMethod("POST");
            conn1.setDoOutput(true);
            try (OutputStreamWriter wr = new OutputStreamWriter(conn1.getOutputStream())) {
                wr.write(postBody);
                wr.flush();
            }
            conn1.connect();
            StringBuilder results;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn1.getInputStream()))) {
                results = new StringBuilder();
                String oneline;
                while ((oneline = br.readLine()) != null) {
                    results.append(oneline);
                }
            }
            System.out.println(URLDecoder.decode(results.toString(), "ISO-8859-1"));
        } catch (IOException exception) {
            System.out.println(exception.getMessage() + exception.getCause());
        }
    }

    public static String testConnexionInternet() {
        String code = "KO";
        try {
            URL url = new URL("http://www.google.com");
            HttpURLConnection conn1 = (HttpURLConnection) url.openConnection();
            if (conn1.getResponseCode() == HttpURLConnection.HTTP_OK) {
                code = "OK";
            }
        } catch (UnknownHostException ex) {
            System.out.println("problème de connexion internet " + ex.getMessage());
            return "KO";
        } catch (MalformedURLException ex) {
            System.out.println("problème de connexion internet " + ex.getMessage());
            return "KO";
        } catch (IOException ex) {
            System.out.println("problème de connexion internet " + ex.getMessage());
            return "KO";
        }
        return code;
    }

    public static String send(String urlText, String number, String msg) {
        String res = "", rCode = "KO";
        try {
            String link = urlText.replace("<num>", URLEncoder.encode(number, "UTF-8")).replace("<msg>", URLEncoder.encode(msg, "UTF-8"));
            int i = 0;
            String r = "", r1 = link;
            while (link.charAt(i) != '?') {
                r += link.charAt(i);
                i++;
            }
            r1 = r1.replace(r + "?", "");
            URL url = new URL(r + "?");
            HttpURLConnection conn1 = (HttpURLConnection) url.openConnection();
            System.out.println(r + "?" + r1);
            conn1.setRequestMethod("POST");
            conn1.setReadTimeout(5000);
            conn1.setDoOutput(true);
            try (OutputStreamWriter wr = new OutputStreamWriter(conn1.getOutputStream())) {
                wr.write(r1);
                wr.flush();
            }
            conn1.connect();
            StringBuilder results;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn1.getInputStream()))) {
                results = new StringBuilder();
                String oneline;
                BottomPanel.settextLabel("Récupération du résultat...", Color.black);
                while ((oneline = br.readLine()) != null) {
                    results.append(oneline);
                }
            }
            System.out.println(URLDecoder.decode(results.toString(), "UTF-8"));
            res = URLDecoder.decode(results.toString(), "UTF-8");
            rCode = "OK";
        } catch (IOException exception) {
            System.out.println(exception.getMessage() + exception.getCause());
            return null;
        }
        return rCode;
    }

    public static String send2(String urlText, String number, String msg) {
        String res = "", rCode = "KO";
        try {
            String link = urlText.replace("<num>", URLEncoder.encode(number, "UTF-8")).replace("<msg>", URLEncoder.encode(msg, "UTF-8"));
            URL url = new URL(link);
            HttpURLConnection conn1 = (HttpURLConnection) url.openConnection();
            System.out.println(link);
            conn1.setRequestMethod("POST");
            conn1.setDoOutput(true);
            try (OutputStreamWriter wr = new OutputStreamWriter(conn1.getOutputStream())) {
                wr.write(urlText);
                wr.flush();
            }
            conn1.connect();
            StringBuilder results;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn1.getInputStream()))) {
                results = new StringBuilder();
                String oneline;
                BottomPanel.settextLabel("Récupération du résultat...", Color.black);
                while ((oneline = br.readLine()) != null) {
                    results.append(oneline);
                }
            }
            System.out.println(URLDecoder.decode(results.toString(), "UTF-8"));
            res = URLDecoder.decode(results.toString(), "UTF-8");
            rCode = "OK";
        } catch (IOException exception) {
            //System.out.println("erreur lors de l'envoie");
            System.out.println(exception.getMessage() + exception.getCause());
            return null;
        }
        return rCode;
    }

    public static void ServiceSalaire() throws SQLException, ParseException {
        BottomPanel.settextLabel("Traitement des salaires en cours.... ", java.awt.Color.BLACK);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");

        setConnection();
        
        String query = "SELECT b.NCP AS NCP1,b.AGE,b.DCO AS DSAI,b.OPE,b.EVE,b.MON FROM BKMPAI b  WHERE b.NCP >= '0' ORDER BY DCO ASC";
        
        query = QueryService.buildQuery(query, serviceManager.getParametreRequeteValues(TypeService.SALAIRE1), TypeService.SALAIRE1);
        if(query == null){
            return;
        }
        
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                BottomPanel.settextLabel("Recherche données salaires.... ", java.awt.Color.BLACK);
                if (rs.getString(1) != null) {
                    if (rs.getString(1).trim().length() >= 10) {
                        BkEve eve = new BkEve();
                        BkAgence bkAgence = serviceManager.getBkAgenceById(rs.getString(2).trim());
                        eve.setBkAgence(bkAgence);
                        BkCli bkCli;
                        String cli = "";
                        if (rs.getString(1).trim().length() >= 9) {
                            cli = rs.getString(1).trim().substring(3, 9);
                        }
                        bkCli = serviceManager.getBkCliById(cli);
                        if (bkCli == null) {
                            bkCli = serviceManager.getBkCliByNumCompte(rs.getString(1).trim());
                        }
                        eve.setCli(bkCli);
                        eve.setCompte(rs.getString(1).trim());
                        eve.setEtat("VA");
                        eve.setHsai("00:00:00.000");
                        eve.setMont(Double.parseDouble(rs.getString(6).trim()));
                        eve.setMontant(rs.getString(6).trim());
                        BkOpe bkOpe = serviceManager.getBkOpeById(rs.getString(4).trim());
                        eve.setOpe(bkOpe);
                        eve.setDVAB(rs.getString(3).trim());
                        eve.setEventDate(f2.parse(f2.format(format1.parse(rs.getString(3).trim()))));
                        eve.setSent(false);
                        eve.setNumEve(rs.getString(5).trim());
                        eve.setId(serviceManager.getMaxIndexBkEve() + 1);
                        eve.setType(TypeEvent.salaire);
                        
                        if (bkCli != null) {
                            if (serviceManager.getBkEveByCriteria(eve.getNumEve(), eve.getEventDate(), eve.getCompte()).isEmpty()) {
                                BottomPanel.settextLabel("Chargement données salaires.... " + eve.getCompte(), java.awt.Color.BLACK);
                                serviceManager.enregistrer(eve);
                                
                                String q = "SELECT b.NUM, b.CLI, b.TYP FROM BKTELCLI b WHERE b.CLI='" + rs.getString(2).trim().substring(3, 9) + "'";
                                q = QueryService.buildQuery(q, serviceManager.getParametreRequeteValues(TypeService.TELEPHONE_CLIENT), TypeService.TELEPHONE_CLIENT);
                                if(q == null){
                                    return;
                                }
                                try (PreparedStatement pss = conn.prepareStatement(q)) {
                                    ResultSet resultat = pss.executeQuery();
                                    
                                    int n = 0;
                                    
                                    while (resultat.next()) {
                                        if (bkCli.getPhone() == 0) {
                                            String code, numero;
                                            if (resultat.getString(1).replace(" ", "").replace("/", "").trim().length() == 9 && Utils.estUnEntier(resultat.getString(1).trim())) {
                                                code = "237";
                                                numero = code + resultat.getString(1).trim();
                                                if (bkCli.getPhone() != Long.parseLong(numero)) {
                                                    bkCli.setPhone(Long.parseLong(numero));
                                                    if (n == 0) {
                                                        BottomPanel.settextLabel("Mise à jour numero client.... " + bkCli.getCode(), java.awt.Color.BLACK);
                                                        serviceManager.modifier(bkCli);
                                                        n++;
                                                    }
                                                }
                                            } else if (resultat.getString(1).replace(" ", "").replace("/", "").trim().length() == 8 && Utils.estUnEntier(resultat.getString(1).trim())) {
                                                code = "241";
                                                numero = code + resultat.getString(1).trim();
                                                if (bkCli.getPhone() != Long.parseLong(numero)) {
                                                    bkCli.setPhone(Long.parseLong(numero));
                                                    if (n == 0) {
                                                        BottomPanel.settextLabel("Mise à jour numero client.... " + bkCli.getCode(), java.awt.Color.BLACK);
                                                        serviceManager.modifier(bkCli);
                                                        n++;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        conn.close();
    }

    public static void ServiceSalaireBKMVTI() throws SQLException, ParseException {
        BottomPanel.settextLabel("Traitement des salaires BKMVTI en cours.... ", java.awt.Color.BLACK);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");

        setConnection();
        
        String query = "SELECT b.AGE,b.NCP AS NCP1,b.OPE,b.EVE,b.DCO AS DSAI,b.DVA,b.MON FROM BKMVTI b WHERE b.NCP >= '0' ORDER BY DCO ASC";
        query = QueryService.buildQuery(query, serviceManager.getParametreRequeteValues(TypeService.SALAIRE2), TypeService.SALAIRE2);
        if(query == null){
            return;
        }
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BottomPanel.settextLabel("Recherche données salaires BKMVTI.... ", java.awt.Color.BLACK);
                if (rs.getString(2) != null) {
                    if (rs.getString(2).trim().length() >= 10) {
                        BkEve eve = new BkEve();
                        BkAgence bkAgence = serviceManager.getBkAgenceById(rs.getString(1).trim());
                        eve.setBkAgence(bkAgence);
                        String cli = "";
                        if (rs.getString(2).trim().length() >= 9) {
                            cli = rs.getString(2).trim().substring(3, 9);
                        }
                        BkCli bkCli = serviceManager.getBkCliById(cli);
                        if (bkCli == null) {
                            bkCli = serviceManager.getBkCliByNumCompte(rs.getString(2).trim());
                        }
                        eve.setCli(bkCli);
                        eve.setCompte(rs.getString(2).trim());
                        eve.setEtat("VA");
                        eve.setHsai("00:00:00.000");
                        eve.setMont(Double.parseDouble(rs.getString(7).trim()));
                        eve.setMontant(rs.getString(7).trim());
                        BkOpe bkOpe = serviceManager.getBkOpeById(rs.getString(3).trim());
                        eve.setOpe(bkOpe);
                        eve.setDVAB(rs.getString(5).trim());
                        eve.setEventDate(f2.parse(f2.format(format1.parse(rs.getString(5).trim()))));
                        eve.setSent(false);
                        eve.setNumEve(rs.getString(4).trim());
                        eve.setId(serviceManager.getMaxIndexBkEve() + 1);
                        eve.setType(TypeEvent.salaire);

                        if (bkCli != null) {
                            if (serviceManager.getBkEveByCriteria(eve.getNumEve(), eve.getEventDate(), eve.getCompte()).isEmpty()) {
                                BottomPanel.settextLabel("Chargement données salaires BKMVTI.... " + eve.getCompte(), java.awt.Color.BLACK);
                                serviceManager.enregistrer(eve);

                                String q = "SELECT b.NUM, b.CLI, b.TYP FROM BKTELCLI b WHERE b.CLI='" + rs.getString(2).trim().substring(3, 9) + "'";
                                q = QueryService.buildQuery(q, serviceManager.getParametreRequeteValues(TypeService.TELEPHONE_CLIENT), TypeService.TELEPHONE_CLIENT);
                                if(q == null){
                                    return;
                                }
                                try (PreparedStatement pss = conn.prepareStatement(q)) {
                                    ResultSet resultat = pss.executeQuery();
                                    
                                    int n = 0;
                                    
                                    while (resultat.next()) {
                                        if (bkCli.getPhone() == 0) {
                                            String code , numero;
                                            if (resultat.getString(1).replace(" ", "").replace("/", "").trim().length() == 9 && Utils.estUnEntier(resultat.getString(1).trim())) {
                                                code = "237";
                                                numero = code + resultat.getString(1).trim();
                                                if (bkCli.getPhone() != Long.parseLong(numero)) {
                                                    bkCli.setPhone(Long.parseLong(numero));
                                                    if (n == 0) {
                                                        BottomPanel.settextLabel("Mise à jour numero client.... " + bkCli.getCode(), java.awt.Color.BLACK);
                                                        serviceManager.modifier(bkCli);
                                                        n++;
                                                    }
                                                }
                                            } else if (resultat.getString(1).replace(" ", "").replace("/", "").trim().length() == 8 && Utils.estUnEntier(resultat.getString(1).trim())) {
                                                code = "241";
                                                numero = code + resultat.getString(1).trim();
                                                if (bkCli.getPhone() != Long.parseLong(numero)) {
                                                    bkCli.setPhone(Long.parseLong(numero));
                                                    if (n == 0) {
                                                        BottomPanel.settextLabel("Mise à jour numero client.... " + bkCli.getCode(), java.awt.Color.BLACK);
                                                        serviceManager.modifier(bkCli);
                                                        n++;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        conn.close();
    }

    public static void envoieSMSSalaire() {
        if (checkLicence()) {
            List<BkEve> list = serviceManager.getBkEveBySendParam(false, listString, TypeEvent.salaire);
            list.stream().forEach((BkEve eve) -> {
                BkCli bkCli = eve.getCli();
                if (bkCli != null && eve.getOpe() != null && !"".equals(methode)) {
                    if (bkCli.isEnabled() && serviceManager.getBkCompCliByCriteria(bkCli, eve.getCompte(), true) != null && bkCli.getPhone() != 0) {
                        MessageFormat mf = serviceManager.getFormatByBkOpe(eve.getOpe(), bkCli.getLangue());
                        if (mf != null) {
                            String text = Utils.remplacerVariable(bkCli, eve.getOpe(), eve, mf);
                            String res = testConnexionInternet();
                            BottomPanel.settextLabel("Test connexion ...." + res, java.awt.Color.BLACK);
                            if (res.equals("OK")) {
                                BottomPanel.settextLabel("Envoie du Message à.... " + eve.getCompte(), java.awt.Color.BLACK);
                                switch (methode) {
                                    case "METHO1":
                                        App.send(urlParam, "" + bkCli.getPhone(), text);
                                        break;
                                    case "METHO2":
                                        App.send2(urlParam, "" + bkCli.getPhone(), text);
                                        break;
                                }
                            } else {
                                BottomPanel.settextLabel("Message non envoyé à.... " + eve.getCompte() + " Problème de connexion internet!!", java.awt.Color.RED);
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
                                BottomPanel.settextLabel("OK Message envoyé ", java.awt.Color.BLACK);
                            }
                        }
                    }
                }
            });
        } else {
            BottomPanel.settextLabel("Message non envoyé Problème de Licence veuillez contacter le fournieur!!", java.awt.Color.RED);
        }
    }

    public static void ServiceCredit() throws SQLException, ParseException {
        BottomPanel.settextLabel("Traitement des credits en cours.... ", java.awt.Color.BLACK);
        SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");
        Map<String,String> map = serviceManager.getParametreRequeteValues(TypeService.CREDIT);
        
        setConnection();

        Date current = new Date();
        String currentString = f2.format(current);

        int compteur = 0;
        if (serviceManager.getMaxIndexBkEve(TypeEvent.credit) != null) {
            compteur = serviceManager.getMaxIndexBkEve(TypeEvent.credit);
        }

        String query, hsai;
        if (compteur == 0) {
            query = "SELECT b.NCP AS NCP1,b.CAI,b.OPE,b.EVE,b.MNT,b.HEU,b.ETA FROM BKMAC b WHERE b.NCP >= '0' AND b.CAI >= '0' ORDER BY HEU ASC";
        } else {
            BkEve bkEve = serviceManager.getBkEveById(compteur);
            hsai = bkEve.getHsai();
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
                query = "SELECT b.NCP ,b.CAI ,b.OPE ,b.EVE,b.MNT,b.HEU,b.ETA FROM BKMAC b WHERE b.NCP >= '0' AND b.CAI >= '0' AND HEU > '" + hsai + "'  ORDER BY HEU ASC";
            }
        }
        query = QueryService.buildQuery(query, map, TypeService.CREDIT);
        if(query==null){
            return;
        }
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BottomPanel.settextLabel("Recherche évenements credits.... ", java.awt.Color.BLACK);
                if (rs.getString(1) != null && rs.getString(2) != null) {
                    if (rs.getString(1).trim().length() >= 10) {
                        String age;
                        
                        String queryAgence = "SELECT AGE FROM BKCOM WHERE NCP = '" + rs.getString(1).trim() + "'";
                        
                        queryAgence = QueryService.buildQuery(queryAgence, serviceManager.getParametreRequeteValues(TypeService.AGENCE), TypeService.CREDIT);
                        if(queryAgence==null){
                            return;
                        }
                        try (PreparedStatement psss = conn.prepareStatement(queryAgence)) {
                            ResultSet result = psss.executeQuery();
                            age = null;
                            while (result.next()) {
                                age = result.getString(1).trim();
                            }
                        }
                        BkEve eve = new BkEve();
                        BkAgence bkAgence;
                        if (age != null) {
                            bkAgence = serviceManager.getBkAgenceById(age);
                        } else {
                            bkAgence = serviceManager.getBkAgenceById("00200");
                        }
                        eve.setBkAgence(bkAgence);
                        String cli = "";
                        if (rs.getString(1).trim().length() >= 9) {
                            cli = rs.getString(1).trim().substring(3, 9);
                        }
                        BkCli bkCli = serviceManager.getBkCliById(cli);
                        if (bkCli == null) {
                            bkCli = serviceManager.getBkCliByNumCompte(rs.getString(1).trim());
                        }
                        eve.setCli(bkCli);
                        eve.setCompte(rs.getString(1).trim());
                        eve.setEtat(rs.getString(7).trim());
                        eve.setHsai(rs.getString(6).trim());
                        eve.setMont(Double.parseDouble(rs.getString(5).trim().replace(".", "")));
                        eve.setMontant(rs.getString(5).trim().replace(".", "").replace(" ", ""));
                        BkOpe bkOpe = serviceManager.getBkOpeById(rs.getString(3).trim());
                        eve.setOpe(bkOpe);
                        eve.setDVAB(f2.format(new Date()));
                        eve.setEventDate(f2.parse(f2.format(new Date())));
                        eve.setSent(false);
                        eve.setNumEve(rs.getString(4).trim());
                        eve.setId(serviceManager.getMaxIndexBkEve() + 1);
                        eve.setType(TypeEvent.credit);

                        boolean traitement = true;

                        if (bkCli == null) {
                            traitement = false;
                        }

                        if (!serviceManager.getBkEveByCriteria(eve.getNumEve(), eve.getEventDate(), eve.getCompte()).isEmpty()) {
                            traitement = false;
                        }

                        if (!serviceManager.getBkEveByCriteriaMontant(eve.getNumEve(), eve.getCompte(), eve.getNumEve()).isEmpty()) {
                            traitement = false;
                        }

                        if (!serviceManager.getBkEveByPeriode(eve.getNumEve(), eve.getCompte(), Utils.add(eve.getEventDate(), -3), eve.getEventDate()).isEmpty()) {
                            traitement = false;
                        }

                        if (traitement) {
                            BottomPanel.settextLabel("Chargement données credits.... " + eve.getCompte(), java.awt.Color.BLACK);
                            serviceManager.enregistrer(eve);

                            String q = "SELECT b.NUM, b.CLI, b.TYP FROM BKTELCLI b WHERE b.CLI='" + rs.getString(1).trim().substring(3, 9) + "'";
                            q = QueryService.buildQuery(q, serviceManager.getParametreRequeteValues(TypeService.TELEPHONE_CLIENT), TypeService.TELEPHONE_CLIENT);
                            try (PreparedStatement pss = conn.prepareStatement(q)) {
                                ResultSet resultat = pss.executeQuery();
                                
                                int n = 0;
                                
                                while (resultat.next()) {
                                    Long number = bkCli.getPhone();
                                    if (number == 0) {
                                        String code, numero;
                                        if (resultat.getString(1).replace(" ", "").replace("/", "").trim().length() == 9 
                                                && Utils.estUnEntier(resultat.getString(1).trim())) {
                                            code = "237";
                                            numero = code + resultat.getString(1).trim();
                                            if (bkCli.getPhone() != Long.parseLong(numero)) {
                                                bkCli.setPhone(Long.parseLong(numero));
                                                if (n == 0) {
                                                    BottomPanel.settextLabel("Mise à jour numero client.... " + bkCli.getCode(), java.awt.Color.BLACK);
                                                    serviceManager.modifier(bkCli);
                                                    n++;
                                                }
                                            }
                                        } else if (resultat.getString(1).replace(" ", "").replace("/", "").trim().length() == 8 
                                                && Utils.estUnEntier(resultat.getString(1).trim())) {
                                            code = "241";
                                            numero = code + resultat.getString(1).trim();
                                            if (bkCli.getPhone() != Long.parseLong(numero)) {
                                                bkCli.setPhone(Long.parseLong(numero));
                                                if (n == 0) {
                                                    BottomPanel.settextLabel("Mise à jour numero client.... " + bkCli.getCode(), java.awt.Color.BLACK);
                                                    serviceManager.modifier(bkCli);
                                                    n++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        conn.close();
    }

    public static void envoieSMSCredit() {
        if (checkLicence()) {
            List<BkEve> list = serviceManager.getBkEveBySendParam(false, listString, TypeEvent.credit);
            list.stream().forEach((eve) -> {
                BkCli bkCli = eve.getCli();
                if (bkCli != null && eve.getOpe() != null && !"".equals(methode)) {
                    if (bkCli.isEnabled() && serviceManager.getBkCompCliByCriteria(bkCli, eve.getCompte(), true) != null && bkCli.getPhone() != 0) {
                        MessageFormat mf = serviceManager.getFormatByBkOpe(eve.getOpe(), bkCli.getLangue());
                        if (mf != null) {
                            String text = Utils.remplacerVariable(bkCli, eve.getOpe(), eve, mf);
                            String res = testConnexionInternet();
                            BottomPanel.settextLabel("Test connexion ...." + res, java.awt.Color.BLACK);
                            if (res.equals("OK")) {
                                BottomPanel.settextLabel("Envoie du Message à.... " + eve.getCompte(), java.awt.Color.BLACK);
                                switch (methode) {
                                    case "METHO1":
                                        App.send(urlParam, "" + bkCli.getPhone(), text);
                                        break;
                                    case "METHO2":
                                        App.send2(urlParam, "" + bkCli.getPhone(), text);
                                        break;
                                }
                            } else {
                                BottomPanel.settextLabel("Message non envoyé à.... " + eve.getCompte() + " Problème de connexion internet!!", java.awt.Color.RED);
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
                                BottomPanel.settextLabel("OK Message envoyé ", java.awt.Color.BLACK);
                            }
                        }
                    }
                }
            });
        } else {
            BottomPanel.settextLabel("Message non envoyé Problème de Licence veuillez contacter le fournieur!!", java.awt.Color.RED);
        }
    }

    public static void ServiceMandat() throws SQLException, ParseException {
        BottomPanel.settextLabel("Traitement des mandats en cours.... ", java.awt.Color.BLACK);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");
        
        setConnection();

        int compteur = serviceManager.getMaxBkMad();
        String query;
        if (compteur == 0) {
            query = "SELECT b.CLESEC,b.CTR,b.AGE,b.DCO AS DSAI,b.OPE,b.EVE,b.DBD,b.AD1P,b.AD2P,b.MNT FROM BKMAD b WHERE MNT > '0' ORDER BY DCO ASC";
        } else {
            BkMad bkMad = serviceManager.getBkMadById(compteur);
            String[] date = bkMad.getDco().substring(0, 10).split("-");
            String s = date[2] + "-" + date[1] + "-" + date[0];
            query = "SELECT b.CLESEC,b.CTR,b.AGE,b.DCO AS DSAI,b.OPE,b.EVE,b.DBD,b.AD1P,b.AD2P,b.MNT FROM BKMAD b WHERE MNT > '0' AND DCO >= '" + s + "' ORDER BY DCO ASC";
        }
        query = QueryService.buildQuery(query, serviceManager.getParametreRequeteValues(TypeService.MANDAT), TypeService.MANDAT);
        if(query==null){
            return;
        }
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BottomPanel.settextLabel("Recherche données de manadats.... ", java.awt.Color.BLACK);
                BkMad bkMad = serviceManager.getBkMadByClesec(rs.getString(1).trim());
                if (bkMad == null) {
                    BkMad eve = new BkMad();
                    BkAgence bkAgence = serviceManager.getBkAgenceById(rs.getString(3).trim());
                    eve.setAge(bkAgence);
                    eve.setMnt(rs.getString(10).trim());
                    BkOpe bkOpe = serviceManager.getBkOpeById(rs.getString(5).trim());
                    eve.setOpe(bkOpe);
                    eve.setDco(rs.getString(4).trim());
                    eve.setDateEnvoie(f2.parse(f2.format(format1.parse(rs.getString(4).trim()))));
                    if (rs.getString(8) != null) {
                        eve.setAd1p(rs.getString(8).trim());
                    }
                    if (rs.getString(9) != null) {
                        eve.setAd2p(rs.getString(9).trim());
                    }
                    eve.setSent(false);
                    eve.setEve(rs.getString(6).trim());
                    eve.setClesec(rs.getString(1).trim());
                    eve.setId(serviceManager.getMaxBkMad() + 1);
                    switch (rs.getString(2).trim()) {
                        case "9":
                            eve.setTraite(1);
                            break;
                        case "0":
                            eve.setTraite(0);
                            break;
                    }
                    eve.setCtr(rs.getString(2).trim());
                    BottomPanel.settextLabel("Chargement données salaires.... " + eve.getAd1p(), java.awt.Color.BLACK);
                    serviceManager.enregistrer(eve);
                } else if (!bkMad.getCtr().equals("9")) {
                    if (rs.getString(2).trim().equals("9")) {
                        bkMad.setDbd(rs.getString(7).trim());
                        bkMad.setDateRetrait(f2.parse(f2.format(format1.parse(rs.getString(7).trim()))));
                        bkMad.setCtr("9");
                        bkMad.setTraite(1);
                        serviceManager.modifier(bkMad);
                    }
                }
            }
        }
        conn.close();
    }

    public static void envoieSMSMandat() {
        boolean bon = false;
        BottomPanel.settextLabel("Debut envoie de message", java.awt.Color.BLACK);
        if (checkLicence()) {
            List<BkMad> list = serviceManager.getBkMadByTraite();
            for (BkMad eve : list) {
                if (testPhone(eve.getAd1p()) != null && testPhone(eve.getAd2p()) != null) {
                    if (eve.getOpe() != null && !"".equals(methode)) {
                        MessageFormat mf = serviceManager.getFormatByBkOpe(eve.getOpe(), "FR");
                        if (mf != null) {
                            String text = Utils.remplacerVariable(eve, mf);
                            String res = testConnexionInternet();
                            BottomPanel.settextLabel("Test connexion ...." + res, java.awt.Color.BLACK);
                            if (res.equals("OK")) {
                                if (eve.getTraite() == 0) {
                                    BottomPanel.settextLabel("Envoie du Message à.... " + eve.getAd2p(), java.awt.Color.BLACK);
                                    switch (methode) {
                                        case "METHO1":
                                            App.send(urlParam, "" + eve.getAd2p(), text);
                                            break;
                                        case "METHO2":
                                            App.send2(urlParam, "" + eve.getAd2p(), text);
                                            break;
                                    }
                                } else if (eve.getTraite() == 1) {
                                    mf = serviceManager.getFormatByBkOpe(serviceManager.getBkOpeById("100"), "FR");
                                    if (mf != null) {
                                        text = Utils.remplacerVariable(eve, mf);
                                        bon = true;
                                        BottomPanel.settextLabel("Envoie du Message à.... " + eve.getAd1p(), java.awt.Color.BLACK);
                                        switch (methode) {
                                            case "METHO1":
                                                App.send(urlParam, "" + eve.getAd1p(), text);
                                                break;
                                            case "METHO2":
                                                App.send2(urlParam, "" + eve.getAd1p(), text);
                                                break;
                                        }
                                    }
                                }
                            } else {
                                BottomPanel.settextLabel("Message non envoyé problème de connexion internet!!", java.awt.Color.RED);
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
                                BottomPanel.settextLabel("OK Message envoyé ", java.awt.Color.BLACK);
                            }
                        }
                    }
                }
            }
        } else {
            BottomPanel.settextLabel("Message non envoyé Problème de Licence veuillez contacter le fournieur!!", java.awt.Color.RED);
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

    /**
     * 
     * @throws SQLException
     * @throws ParseException 
     */
    public static void ServiceRequete() throws SQLException, ParseException {
        BottomPanel.settextLabel("Traitement des requetes en cours.... ", java.awt.Color.BLACK);

        setConnection();

        List<Command> commands = serviceManager.getCommandByStatus(Status.PENDING);
        String query;
        for (Command command : commands) {
            switch (command.getOpe()) {
                case MethodUtils.SOLDE: {
                    final ServiceOffert ser = serviceManager.findServiceByCode(MethodUtils.SOLDE);
                    query = "SELECT b.SIN FROM BKCOM b WHERE b.NCP='" + command.getCompte() + "'";
                    query = QueryService.buildQuery(query, serviceManager.getParametreRequeteValues(TypeService.SOLDE), TypeService.SOLDE);
                    if(query == null){
                        return;
                    }
                    System.out.println("Resquete " + query);
                    String solde;
                    try (PreparedStatement ps = conn.prepareStatement(query)) {
                        ResultSet rs = ps.executeQuery();
                        solde = "";
                        while (rs.next()) {
                            solde = rs.getBigDecimal(1).toPlainString();
                        }
                    }
                    String msg = "Cher client au compte " + command.getCompte() + " votre solde est de " + solde + ".";
                    command.setMessage(msg);
                    command.setMontant(ser.getMontant());
                    envoieSmsRequete(command);
                    break;
                }
                case MethodUtils.HIST: {
                    final ServiceOffert ser = serviceManager.findServiceByCode(MethodUtils.HIST);
                    query = "SELECT b.MCTV, b.EVE, b.AGE, b.DVA, b.OPE FROM BKHIS b WHERE b.NCP='" + command.getCompte() + "' ORDER BY b.DVA ASC";
                    query = QueryService.buildQuery(query, serviceManager.getParametreRequeteValues(TypeService.HISTORIQUE), TypeService.HISTORIQUE);
                    if(query == null){
                        return;
                    }
                    PreparedStatement ps = conn.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    int i = 0;
                    StringBuilder msg = new StringBuilder();
                    msg.append("Cher client au compte ");
                    msg.append(command.getCompte());
                    msg.append(" ");
                    msg.append("voici les 5 derières operations ");
                    while (rs.next()) {
                        BkAgence bkAgence = serviceManager.getBkAgenceById(rs.getString(3).trim());
                        BkOpe bkOpe = serviceManager.getBkOpeById(rs.getString(5).trim());
                        msg.append(bkOpe.getLib());
                        msg.append(" ");
                        msg.append(rs.getString(1).trim());
                        msg.append(" ");
                        msg.append(rs.getString(4).trim());
                        msg.append(" ");
                        msg.append(bkAgence.getNoma());
                        msg.append("\n");
                        i++;
                        if (i == 5) {
                            break;
                        }
                    }
                    command.setMontant(ser.getMontant());
                    command.setMessage(msg.toString());
                    envoieSmsRequete(command);
                    break;
                }
            }
        }
        conn.close();
    }

    public static void envoieSmsRequete(Command command) {
        if (checkLicence()) {
            String res = testConnexionInternet();
            BottomPanel.settextLabel("Test connexion ...." + res, java.awt.Color.BLACK);
            if (res.equals("OK")) {
                BottomPanel.settextLabel("Envoie du Message à.... " + command.getCompte(), java.awt.Color.BLACK);
                //if(command.getCompte())
                switch (methode) {
                    case "METHO1":
                        App.send(urlParam, command.getPhone(), command.getMessage());
                        break;
                    case "METHO2":
                        App.send2(urlParam, "" + command.getPhone(), command.getMessage());
                        break;
                }
            } else {
                BottomPanel.settextLabel("Message non envoyé à.... " + command.getCompte() + " Problème de connexion internet!!", java.awt.Color.RED);
                command.setErrorDescription("problème de connexion internet");
                serviceManager.modifier(command);
            }

            if (res.equals("OK")) {
                command.setStatus(Status.PROCESSED);
                command.setProcessedDate(new Date());
                serviceManager.modifier(command);
                BottomPanel.settextLabel("OK Message envoyé ", java.awt.Color.BLACK);
            }
        } else {
            BottomPanel.settextLabel("Message non envoyé Problème de Licence veuillez contacter le fournieur!!", java.awt.Color.RED);
            command.setErrorDescription("problème de licence");
            serviceManager.modifier(command);
        }
    }
    
    public static void setConnection() throws SQLException{
        String pwd = remoteDB.getPassword();
        if(pwd != null && !pwd.isEmpty()){
           String decryptedString = AES.decrypt(pwd, ConstantUtils.SECRET_KEY);
            conn = DriverManager.
                getConnection(remoteDB.getUrl(), remoteDB.getName(), decryptedString);
        }else{
            conn = DriverManager.
                getConnection(remoteDB.getUrl(), remoteDB.getName(), "");
        }
    }
}
