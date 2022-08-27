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

public class ServiceMandat {
    private static final Logger logger = LoggerFactory.getLogger(ServiceMandat.class);
    private final LacusSmsService serviceManager;
    private final String methode;

    private final String urlParam;

    private Connection conn;

    public ServiceMandat(LacusSmsService serviceManager, String methode, String urlParam) {
        this.serviceManager = serviceManager;
        this.methode = methode;
        this.urlParam = urlParam;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void serviceMandat() throws SQLException, ParseException {
        String msg = "Traitement des mandats en cours.... ";
        logger.info(msg);
        BottomPanel.settextLabel(msg, Color.BLACK);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");
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

    public void envoieSMSMandat() {
        boolean bon = false;
        String msg;
        msg = "Debut envoie de message";
        logger.info(msg);
        BottomPanel.settextLabel(msg, Color.BLACK);
        if (Utils.checkLicence()) {
            List<BkMad> list = serviceManager.getBkMadByTraite();

            for (BkMad eve : list) {
                if (Utils.testPhone(eve.getAd1p()) != null && Utils.testPhone(eve.getAd2p()) != null && eve.getOpe() != null && !"".equals(methode)) {
                    MessageFormat mf = serviceManager.getFormatByBkOpe(eve.getOpe(), "FR");
                    if (mf != null) {
                        String text = Utils.remplacerVariable(eve, mf);
                        String res = Utils.testConnexionInternet();
                        BottomPanel.settextLabel("Test connexion ...." + res, Color.BLACK);
                        if (res.equals("OK")) {
                            if (eve.getTraite() == 0) {
                                BottomPanel.settextLabel("Envoie du Message à.... " + eve.getAd2p(), Color.BLACK);
                                switch (methode) {
                                    case "METHO1":
                                    case "METHO2":
                                        Sender.send(urlParam, "" + eve.getAd2p(), text);
                                        break;
                                }
                            } else if (eve.getTraite() == 1) {
                                mf = serviceManager.getFormatByBkOpe(serviceManager.getBkOpeById("100"), "FR");
                                if (mf != null) {
                                    text = Utils.remplacerVariable(eve, mf);
                                    bon = true;
                                    BottomPanel.settextLabel("Envoie du Message à.... " + eve.getAd1p(), Color.BLACK);
                                    switch (methode) {
                                        case "METHO1":
                                        case "METHO2":
                                            Sender.send(urlParam, "" + eve.getAd1p(), text);
                                            break;
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
}
