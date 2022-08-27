package com.abouna.lacussms.service;

import com.abouna.lacussms.entities.*;
import com.abouna.lacussms.main.Sender;
import com.abouna.lacussms.views.main.BottomPanel;
import com.abouna.lacussms.views.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ServiceEvenement {
    private final LacusSmsService serviceManager;
    private final String methode;

    private final String urlParam;

    private Connection conn;

    private final String condition;

    private final List<String> listString;

    private static final Logger logger = LoggerFactory.getLogger(ServiceEvenement.class);

    public ServiceEvenement(LacusSmsService serviceManager, String methode, String urlParam, String condition, List<String> listString) {
        this.serviceManager = serviceManager;
        this.methode = methode;
        this.urlParam = urlParam;
        this.condition = condition;
        this.listString = listString;
    }

    public ServiceEvenement(LacusSmsService serviceManager, String methode, String urlParam, Connection conn, String condition, List<String> listString) {
        this.serviceManager = serviceManager;
        this.methode = methode;
        this.urlParam = urlParam;
        this.conn = conn;
        this.condition = condition;
        this.listString = listString;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void serviceEvenement() throws SQLException, ParseException {
        String msg = "Début traitement des venements";
        logger.info(msg);
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

    public void envoieSMSEvenement() {
        if (Utils.checkLicence()) {
            List<BkEve> list = serviceManager.getBkEveBySendParam(false, listString, TypeEvent.ordinaire);
            list.forEach((eve) -> {
                BkCli bkCli = eve.getCli();
                if (bkCli != null && eve.getOpe() != null && !"".equals(methode) && bkCli.isEnabled() && serviceManager.getBkCompCliByCriteria(bkCli, eve.getCompte(), true) != null && bkCli.getPhone() != 0L) {
                    MessageFormat mf = serviceManager.getFormatByBkOpe(eve.getOpe(), bkCli.getLangue());
                    if (mf != null) {
                        String text = Utils.remplacerVariable(bkCli, eve.getOpe(), eve, mf);
                        String res = Utils.testConnexionInternet();
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

}
