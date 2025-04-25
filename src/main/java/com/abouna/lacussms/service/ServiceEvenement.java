package com.abouna.lacussms.service;

import com.abouna.lacussms.dto.BkEtatOpConfigBean;
import com.abouna.lacussms.entities.*;
import com.abouna.lacussms.views.tools.Sender;
import com.abouna.lacussms.views.main.BottomPanel;
import com.abouna.lacussms.views.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class ServiceEvenement {
    private final LacusSmsService serviceManager;
    private Connection conn;
    private final String condition;
    private final List<String> listString;

    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");

    private static final Logger logger = LoggerFactory.getLogger(ServiceEvenement.class);

    public ServiceEvenement(LacusSmsService serviceManager, BkEtatOpConfigBean bkEtatOpConfigBean) {
        this.serviceManager = serviceManager;
        this.condition = bkEtatOpConfigBean.getCondition();
        this.listString = bkEtatOpConfigBean.getListString();
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void envoieSMSEvenement() {
        List<BkEve> list = serviceManager.getBkEveBySendParam(false, listString, TypeEvent.ordinaire);
        list.forEach((eve) -> {
            BkCli bkCli = eve.getCli();
            if (bkCli != null && eve.getOpe() != null && bkCli.isEnabled() && serviceManager.getBkCompCliByCriteria(bkCli, eve.getCompte(), true) != null && bkCli.getPhone() != 0L) {
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
                        Sender.send(String.valueOf(bkCli.getPhone()), text);
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
    }

    public void serviceEvenement() throws SQLException, ParseException {
        try (PreparedStatement ps = conn.prepareStatement(getQuery())) {
            ResultSet rs = ps.executeQuery();
            String msg = "Recherche des évenements en cours....";
            while (rs.next()) {
                logger.info(msg);
                BottomPanel.settextLabel(msg, java.awt.Color.BLACK);
                String numeroCompte = rs.getString("NCP1");
                if (numeroCompte != null && numeroCompte.trim().length() >= 10) {
                    numeroCompte = numeroCompte.trim();
                    BkEve eve = new BkEve();
                    BkAgence bkAgence = serviceManager.getBkAgenceById(rs.getString("AGE").trim());
                    eve.setBkAgence(bkAgence);
                    BkCli bkCli;
                    String cli = "";
                    if (numeroCompte.length() >= 9) {
                        cli = numeroCompte.substring(3, 9);
                    }
                    bkCli = serviceManager.getBkCliById(cli);
                    if (bkCli == null) {
                        bkCli = serviceManager.getBkCliByNumCompte(numeroCompte);
                    }
                    eve.setCli(bkCli);
                    eve.setCompte(numeroCompte);
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


                    boolean traitement = bkCli != null;

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
                        msg = "Chargement données évenement.... " + eve.getCompte();
                        logger.info(msg);
                        BottomPanel.settextLabel(msg, java.awt.Color.BLACK);
                        serviceManager.enregistrer(eve);
                        ServiceUtils.mettreAjourNumero(serviceManager, conn, bkCli, cli);
                    }
                }
            }
        }
    }

    public String getQuery() {
        int compteur = serviceManager.getMaxIndexBkEve(TypeEvent.ordinaire);
        String heure, date, currentString;
        Date current = new Date();
        String query, query1, finalquery, heureInit = "00:00:00.000";
        if (compteur == 0) {
            heure = "00:00:00.000";
            date = format.format(current);
            if (!condition.isEmpty()) {
                finalquery = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM bkeve b WHERE b.NCP1 >= '0' AND b.DSAI = '" + date + "' AND b.HSAI > '" + heure + "'" + " AND (" + condition + ")" + "  ORDER BY b.DSAI,b.HSAI ASC";
            } else {
                finalquery = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM bkeve b WHERE b.NCP1 >= '0' AND b.DSAI = '" + date + "' AND b.HSAI > '" + heure + "'" + "  ORDER BY b.DSAI,b.HSAI ASC";
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
                if (!condition.isEmpty()) {
                    query = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM bkeve b WHERE b.NCP1 >= '0' AND b.DSAI = '" + date + "' AND b.HSAI > '" + heure + "'" + " AND (" + condition + ")";
                    query1 = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM bkeve b WHERE b.NCP1 >= '0' AND b.DSAI = '" + currentString + "' AND b.HSAI > '" + heureInit + "'" + " AND (" + condition + ")";
                } else {
                    query = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM bkeve b WHERE b.NCP1 >= '0' AND b.DSAI = '" + date + "' AND b.HSAI > '" + heure + "'";
                    query1 = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM bkeve b WHERE b.NCP1 >= '0' AND b.DSAI = '" + currentString + "' AND b.HSAI > '" + heureInit + "'";
                }
                finalquery = "(" + query + ") UNION (" + query1 + ")" + suffix;
            } else {
                if (!condition.isEmpty()) {
                    query = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM bkeve b WHERE b.NCP1 >= '0' AND b.DSAI = '" + date + "' AND b.HSAI > '" + heure + "'" + " AND (" + condition + ")";
                } else {
                    query = "SELECT b.NCP1,b.EVE,b.CLI1,b.ETA,b.DSAI,b.HSAI,b.DVAB,b.OPE,b.MON1,b.AGE FROM bkeve b WHERE b.NCP1 >= '0' AND b.DSAI = '" + date + "' AND b.HSAI > '" + heure + "'";
                }
                finalquery = query + suffix;
            }
        }
        return finalquery;
    }
}
