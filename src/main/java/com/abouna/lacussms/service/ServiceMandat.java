package com.abouna.lacussms.service;

import com.abouna.lacussms.entities.*;
import com.abouna.lacussms.views.tools.Sender;
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
    private Connection conn;
    private final SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");


    public ServiceMandat(LacusSmsService serviceManager) {
        this.serviceManager = serviceManager;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void serviceMandat() throws SQLException, ParseException {
        String msg = "Traitement des mandats en cours.... ";
        BottomPanel.settextLabel(msg, java.awt.Color.BLACK);
        logger.info(msg);

        try (PreparedStatement ps = conn.prepareStatement(getQuery())) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                msg = "Recherche données de manadats.... ";
                logger.info(msg);
                BottomPanel.settextLabel(msg, java.awt.Color.BLACK);
                BkMad bkMad = serviceManager.getBkMadByClesec(rs.getString(1).trim());
                String result = rs.getString(2).trim();
                if (bkMad == null) {
                    BkMad eve = new BkMad();
                    BkAgence bkAgence = serviceManager.getBkAgenceById(rs.getString(3).trim());
                    eve.setAge(bkAgence);
                    eve.setMnt(rs.getString(10).trim());
                    BkOpe bkOpe = serviceManager.getBkOpeById(rs.getString(5).trim());
                    eve.setOpe(bkOpe);
                    eve.setDco(rs.getString(4).trim());
                    eve.setDateEnvoie(format2.parse(format2.format(format1.parse(rs.getString(4).trim()))));
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
                    switch (result) {
                        case "9":
                            eve.setTraite(1);
                            break;
                        case "0":
                            eve.setTraite(0);
                            break;
                    }
                    eve.setCtr(result);
                    msg = "Chargement données salaires.... " + eve.getAd1p();
                    logger.info(msg);
                    BottomPanel.settextLabel(msg, java.awt.Color.BLACK);
                    serviceManager.enregistrer(eve);
                } else if (!bkMad.getCtr().equals("9")) {
                    if (result.equals("9")) {
                        String date = rs.getString(7).trim();
                        bkMad.setDbd(date);
                        bkMad.setDateRetrait(format2.parse(format2.format(format1.parse(date))));
                        bkMad.setCtr("9");
                        bkMad.setTraite(1);
                        serviceManager.modifier(bkMad);
                    }
                }
            }
        }
    }

    private String getQuery() {
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
        return query;
    }

    public void envoieSMSMandat() {
        boolean bon = false;
        String msg;
        msg = "Debut envoie de message";
        logger.info(msg);
        BottomPanel.settextLabel(msg, Color.BLACK);
        List<BkMad> list = serviceManager.getBkMadByTraite();

        for (BkMad eve : list) {
            if (Utils.testPhone(eve.getAd1p()) != null && Utils.testPhone(eve.getAd2p()) != null && eve.getOpe() != null) {
                MessageFormat mf = serviceManager.getFormatByBkOpe(eve.getOpe(), "FR");
                if (mf != null) {
                    String text = Utils.remplacerVariable(eve, mf);
                    String res = Utils.testConnexionInternet();
                    BottomPanel.settextLabel("Test connexion ...." + res, Color.BLACK);
                    if (res.equals("OK")) {
                        if (eve.getTraite() == 0) {
                            BottomPanel.settextLabel("Envoie du Message à.... " + eve.getAd2p(), Color.BLACK);
                            Sender.send(eve.getAd2p(), text);
                        } else if (eve.getTraite() == 1) {
                            mf = serviceManager.getFormatByBkOpe(serviceManager.getBkOpeById("100"), "FR");
                            if (mf != null) {
                                text = Utils.remplacerVariable(eve, mf);
                                bon = true;
                                BottomPanel.settextLabel("Envoie du Message à.... " + eve.getAd1p(), Color.BLACK);
                                Sender.send(eve.getAd1p(), text);
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
    }
}
