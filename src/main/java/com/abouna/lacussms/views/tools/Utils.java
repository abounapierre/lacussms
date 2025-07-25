package com.abouna.lacussms.views.tools;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.*;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.main.BottomPanel;
import com.abouna.lacussms.views.main.LogFile;
import com.abouna.lacussms.views.utils.LogParam;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.Timer;
import java.awt.Color;
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.*;
import java.util.function.Function;

public class Utils {
    static final Logger logger = LoggerFactory.getLogger(Utils.class);

    static LacusSmsService serviceManager;
    static List<String> listString;

    public Utils() {
    }

    public static Date getTimeFromInternet(String timeServer) {
        try {
            Date date;
            NTPUDPClient timeClient = new NTPUDPClient();
            timeClient.setDefaultTimeout(3000);
            InetAddress inetAddress = InetAddress.getByName(timeServer);
            TimeInfo timeInfo = timeClient.getTime(inetAddress);
            long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
            date = new Date(returnTime);
            return date;
        } catch (Throwable t) {
            com.abouna.lacussms.views.utils.Logger.error(String.format("problème de récuperation de la date de puis %s", timeServer), t, Utils.class);
            return new Date();
        }
    }

    public static Date getTimeFromInternet() {
       return getTimeFromInternet("time-a.nist.gov");
    }


    public static String moveZero(Double d) {
        StringBuilder res = new StringBuilder();

        for(int i = 0; i != Double.toString(d).length() && Double.toString(d).charAt(i) != '.'; ++i) {
            res.append(Double.toString(d).charAt(i));
        }

        return res.toString();
    }

    public static boolean isCorrect(String text) {
        int a = 0;
        int b = 0;

        for(int i = 0; i < text.length(); ++i) {
            if (text.charAt(i) == '<') {
                ++a;
            } else if (text.charAt(i) == '>') {
                ++b;
            }
        }
        return a == b;
    }

    public static boolean iscorrect(List<String> variables) {
        int a = 0;
        int b = variables.size();

        for (String var : variables) {

            for (Variable s : Variable.values()) {
                if (s.toString().equals(var)) {
                    ++a;
                }
            }
        }

        return a == b;
    }

    public static List<String> extract(String text) {
        List<String> list = new ArrayList<>();

        for(int i = 0; i < text.length(); ++i) {
            StringBuilder result = new StringBuilder();
            if (text.charAt(i) == '<') {
                for(int j = i + 1; text.charAt(j) != '>'; ++j) {
                    result.append(text.charAt(j));
                }

                list.add(result.toString());
            }
        }
        return list;
    }

    public static String remplacerVariable(BkCli bkCli, BkOpe bkOpe, BkEve bkEve, MessageFormat mf) {
        List<String> list = extract(mf.getContent());
        String text = mf.getContent();

        for (String s : list) {
            switch (s) {
                case "nom":
                    text = text.replace("<" + s + ">", bkCli.getNom());
                    break;
                case "pre":
                    text = text.replace("<" + s + ">", bkCli.getPrenom());
                    break;
                case "numc":
                    text = text.replace("<" + s + ">", bkEve.getCompte());
                    break;
                case "lib":
                    text = text.replace("<" + s + ">", bkCli.getLibelle());
                    break;
                case "numt":
                    text = text.replace("<" + s + ">", bkCli.getPhone() + "");
                    break;
                case "date":
                    text = text.replace("<" + s + ">", bkEve.getDVAB().length() > 10 ? bkEve.getDVAB().substring(0, 10) : bkEve.getDVAB());
                    break;
                case "mont":
                    text = text.replace("<" + s + ">", bkEve.getMontant() == null ? moveZero(bkEve.getMont()) : bkEve.getMontant());
                    break;
                case "agence":
                    text = text.replace("<" + s + ">", bkEve.getBkAgence() == null ? "" : bkEve.getBkAgence().getNoma());
                    break;
                case "heure":
                    text = text.replace("<" + s + ">", getHSAI(bkEve.getHsai()));
                    break;
            }
        }

        return text;
    }

    public static String remplacerVariable(BkMad eve, MessageFormat mf) {
        List<String> list = extract(mf.getContent());
        String text = mf.getContent();

        for (String s : list) {
            switch (s) {
                case "numt1":
                    text = text.replace("<" + s + ">", eve.getAd1p());
                    break;
                case "numt2":
                    text = text.replace("<" + s + ">", eve.getAd2p());
                    break;
                case "code":
                    text = text.replace("<" + s + ">", eve.getClesec());
                    break;
                case "date1":
                    text = text.replace("<" + s + ">", eve.getDco().length() > 10 ? eve.getDco().substring(0, 10) : eve.getDco());
                    break;
                case "date2":
                    text = text.replace("<" + s + ">", eve.getDbd().length() > 10 ? eve.getDbd().substring(0, 10) : eve.getDbd());
                    break;
                case "mont":
                    text = text.replace("<" + s + ">", eve.getMnt());
                    break;
            }
        }

        return text;
    }

    public static String getHSAI(String heure) {
        String result = null;
        if (heure.length() > 8) {
            result = heure.substring(0, 5);
        } else if (heure.length() == 4) {
            result = heure.substring(0, 2) + ":" + heure.substring(2, 4);
        }

        return result;
    }

    public static boolean estUnEntier(String chaine) {
        try {
            Integer.parseInt(chaine);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    public static void sendMail(String msg) {
        String username = "lacussms.app@gmail.com";
        String password = "@dmin123!";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("lacussms.app@gmail.com", "@dmin123!");
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("lacussms.appil@gmail.com"));
            message.setRecipients(RecipientType.TO, InternetAddress.parse("eabouna@gmail.com"));
            message.setSubject("Application en cours d'exécution");
            new Date();
            message.setText(msg);
            Transport.send(message);
        } catch (MessagingException var7) {
            logger.error(var7.getMessage());
        }
    }

    public static String testConnexionInternet() {
        String code = "KO";
        try {
            URL url = new URL("http://www.google.com");
            HttpURLConnection conn1 = (HttpURLConnection)url.openConnection();
            if (conn1.getResponseCode() == 200) {
                code = "OK";
            }
            return code;
        } catch (IOException var3) {
            return "KO";
        }
    }

    public static void enregistrerMail(LacusSmsService service) {
        Date date = new Date();

        String message;
        try {
            message = "Application tourne correctement,\n\n Lacus SMS, please CopyRight 2018 SunSoftware Fundation!\n\n en date du " + date + " depuis " + InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException var5) {
            message = "Application tourne correctement,\n\n Lacus SMS, please CopyRight 2018 SunSoftware Fundation!\n\n en date du " + date + " depuis  unknow host";
        }

        SentMail sentMail = new SentMail();
        sentMail.setContent(message);
        sentMail.setHeure(date);
        sentMail.setSentDate(date);
        String res = testConnexionInternet();
        if (res.equals("OK")) {
            sendMail(message);
            service.saveMail(sentMail);
        }

    }

    public static Date add(Date d, long a) {
        return new Date(d.getTime() + a * 86400000L);
    }

    public static String day(int i) {
        if (i == 2) {
            return "Lundi";
        } else if (i == 3) {
            return "Mardi";
        } else if (i == 4) {
            return "Mercredi";
        } else if (i == 5) {
            return "Jeudi";
        } else if (i == 6) {
            return "Vendredi";
        } else if (i == 7) {
            return "Samedi";
        } else {
            return i == 1 ? "Dimanche" : "";
        }
    }

    public static boolean exist(String lic, Connection conn) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("select * from licence where code=? and used=?");
        pstmt.setString(1, lic);
        pstmt.setBoolean(2, false);
        ResultSet rs = pstmt.executeQuery();
        return rs.first();
    }

    public static Connection getConnection() throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://mysql-eabouna.alwaysdata.net:3306/eabouna_lacus", "eabouna", "Lebomo@1989!");
            return conn;
        } catch (SQLException var2) {
            return null;
        }
    }


    public static void ecrire(String ch) throws IOException {
        String fileName = System.getenv("APPDATA") + File.separator + "lacus" + File.separator + "app.txt";
        FileWriter fileWriter = new FileWriter(fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print(ch);
        printWriter.close();
        fileWriter.close();
    }



    public static String generateDate() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        String formatDate = sdf.format(date);
        formatDate = "AB-" + formatDate + "-PI";
        return AES.encrypt(formatDate, ConstantUtils.SECRET_KEY);
    }

    public static Date getDateSimpleFormat(String format, String value) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String hacher(String algorithme, String monMessage) {
        byte[] digest = null;

        try {
            MessageDigest sha = MessageDigest.getInstance(algorithme);
            digest = sha.digest(monMessage.getBytes());
        } catch (NoSuchAlgorithmException ignored) {
            return null;
        }

        assert digest != null;
        return bytesToHex(digest);
    }

    public static String bytesToHex(byte[] b) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder buffer = new StringBuilder();

        for (byte value : b) {
            buffer.append(hexDigits[value >> 4 & 15]);
            buffer.append(hexDigits[value & 15]);
        }

        return buffer.toString();
    }

    public static String getAppVersion() throws IOException, XmlPullParserException {
        throw new XmlPullParserException("");
    }

    public static Function<LogParam, Void> printLog = logParam -> {
        logParam.getTextArea().append(logParam.getLog());
        return null;
    };

    Function<LogParam, Void> print = logParam -> {
        String line;
        try {
            if ((line = logParam.getReader().readLine()) != null) {
                logParam.getTextArea().append(line + "\n");
            } else {
                ((Timer) logParam.getEvent().getSource()).stop();
            }
        } catch (IOException ex) {
            //Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    };

    public static String getLog() {
        LogFile logBean = ApplicationConfig.getApplicationContext().getBean(LogFile.class);
        return logBean.getLog();
    }

    public static void initDriver() throws ClassNotFoundException {
        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        TimeZone.setDefault(timeZone);
        Class.forName("oracle.jdbc.driver.OracleDriver");
    }

    public static boolean checkLicence() {
        return true;
    }


    public static void test() {
        List<BkEve> list = serviceManager.getBkEveBySendParam(true, listString);
        NumberFormat formatnum = NumberFormat.getCurrencyInstance();
        formatnum.setMinimumFractionDigits(0);
        list.stream().peek((eve) -> logger.info(eve.toString())).forEach((eve) -> {
            logger.info(" Montant : " + eve.getMont() + " " + Utils.moveZero(eve.getMont()));
        });
    }

    public static Connection testConnexion(LacusSmsService serviceManager, String secret) {
        try {
            String decryptedString;
            Utils.initDriver();
            RemoteDB remoteDB = serviceManager.getDefaultRemoteDB(true);
            if (remoteDB != null) {
                logger.debug("URL: {}", remoteDB.getUrl());
                logger.debug("Username: {}", remoteDB.getName());
                logger.debug("Password: {}", remoteDB.getPassword());
                decryptedString = AES.decrypt(remoteDB.getPassword(), secret);
                return DriverManager.getConnection(remoteDB.getUrl(), remoteDB.getName(), decryptedString);
            }
            return null;
        } catch (ClassNotFoundException | SQLException | NullPointerException ex) {
            logger.error("problème de connexion bd {}", ex.getMessage());
            return null;
        }
    }


    public static void send(String username, String pass, String number, String msg, String sid, int fl, int mt, String ipcl) {
        try {
            String postBody = "username=" + URLEncoder.encode(username, "ISO-8859-1") + "&password=" + URLEncoder.encode(pass, "ISO-8859-1") + "&mno=" + URLEncoder.encode(number, "ISO-8859-1") + "&msg=" + URLEncoder.encode(msg, "ISO-8859-1") + "&Sid=" + URLEncoder.encode(sid, "ISO-8859-1") + "&fl=" + URLEncoder.encode("" + fl, "ISO-8859-1") + "&mt=" + URLEncoder.encode("" + mt, "ISO-8859-1") + "&ipcl=" + URLEncoder.encode(ipcl, "ISO-8859-1");
            String link = "https://1s2u.com/sms/sendsms/sendsms.asp?" + postBody;
            URL url = new URL(link);
            HttpURLConnection conn1 = (HttpURLConnection)url.openConnection();
            logger.info(postBody);
            conn1.setRequestMethod("POST");
            conn1.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn1.getOutputStream());
            wr.write(postBody);
            wr.flush();
            wr.close();
            conn1.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
            StringBuilder results = new StringBuilder();

            String oneline;
            while((oneline = br.readLine()) != null) {
                results.append(oneline);
            }

            br.close();
            logger.info(URLDecoder.decode(results.toString(), "ISO-8859-1"));
        } catch (IOException var16) {
            logger.error(var16.getMessage() + var16.getCause());
        }

    }


    public static String send(String urlText, String number, String msg) {
        String res = "";
        String rCode;

        try {
            String link = urlText.replace("<num>", URLEncoder.encode(number, "UTF-8")).replace("<msg>", URLEncoder.encode(msg, "UTF-8"));
            int i = 0;

            String r;
            for(r = ""; link.charAt(i) != '?'; ++i) {
                r = r + link.charAt(i);
            }

            String r1 = link.replace(r + "?", "");
            URL url = new URL(r + "?");
            HttpURLConnection conn1 = (HttpURLConnection)url.openConnection();
            logger.info(r + "?" + r1);
            conn1.setRequestMethod("POST");
            conn1.setReadTimeout(5000);
            conn1.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn1.getOutputStream());
            wr.write(r1);
            wr.flush();
            wr.close();
            conn1.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
            StringBuilder results = new StringBuilder();
            BottomPanel.settextLabel("Récupération du résultat...", Color.black);

            String oneline;
            while((oneline = br.readLine()) != null) {
                results.append(oneline);
            }

            br.close();
            logger.info(URLDecoder.decode(results.toString(), "UTF-8"));
            res = URLDecoder.decode(results.toString(), "UTF-8");
            rCode = "OK";
            return rCode;
        } catch (IOException var15) {
            logger.error(var15.getMessage() + var15.getCause());
            return null;
        }
    }

    public static void send2(String urlText, String number, String msg) {
        String res = "";
        String rCode = "KO";

        try {
            String link = urlText.replace("<num>", URLEncoder.encode(number, "UTF-8")).replace("<msg>", URLEncoder.encode(msg, "UTF-8"));
            URL url = new URL(link);
            HttpURLConnection conn1 = (HttpURLConnection)url.openConnection();
            logger.info(link);
            conn1.setRequestMethod("POST");
            conn1.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn1.getOutputStream());
            Throwable var9 = null;

            try {
                wr.write(urlText);
                wr.flush();
            } catch (Throwable var19) {
                var9 = var19;
                throw var19;
            } finally {
                if (var9 != null) {
                    try {
                        wr.close();
                    } catch (Throwable var18) {
                        var9.addSuppressed(var18);
                    }
                } else {
                    wr.close();
                }

            }

            conn1.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
            StringBuilder results = new StringBuilder();
            msg = "Récupération du résultat...";
            logger.info(msg);
            BottomPanel.settextLabel(msg, Color.black);

            String oneline;
            while((oneline = br.readLine()) != null) {
                results.append(oneline);
            }

            br.close();
            logger.info(URLDecoder.decode(results.toString(), "UTF-8"));
            res = URLDecoder.decode(results.toString(), "UTF-8");
            rCode = "OK";
        } catch (IOException var21) {
            logger.error("{} {}", var21.getMessage(), var21.getCause());
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
