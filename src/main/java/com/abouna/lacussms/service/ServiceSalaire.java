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

public class ServiceSalaire {
    private static final Logger logger = LoggerFactory.getLogger(ServiceSalaire.class);

    private final LacusSmsService serviceManager;
    private final String methode;

    private final String urlParam;

    private Connection conn;

    private final List<String> listString;

    public ServiceSalaire(LacusSmsService serviceManager, String methode, String urlParam, List<String> listString) {
        this.serviceManager = serviceManager;
        this.methode = methode;
        this.urlParam = urlParam;
        this.listString = listString;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void serviceSalaire() throws SQLException, ParseException {
        String msg = "Traitement des salaires en cours.... ";
        logger.info(msg);
        BottomPanel.settextLabel(msg, Color.BLACK);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");
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
                    BkCli bkCli;
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


    public void envoieSMSSalaire() {
        if (Utils.checkLicence()) {
            List<BkEve> list = serviceManager.getBkEveBySendParam(false, listString, TypeEvent.salaire);
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
                            BottomPanel.settextLabel("OK Message envoyé ", Color.BLACK);
                        }
                    }
                }

            });
        } else {
            BottomPanel.settextLabel("Message non envoyé Problème de Licence veuillez contacter le fournieur 1.2.5 !!", Color.RED);
        }

    }
}
