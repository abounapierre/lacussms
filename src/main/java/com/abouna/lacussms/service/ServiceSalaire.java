package com.abouna.lacussms.service;

import com.abouna.lacussms.dto.BkEtatOpConfigBean;
import com.abouna.lacussms.entities.*;
import com.abouna.lacussms.views.main.BottomPanel;
import com.abouna.lacussms.views.tools.Sender;
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
public class ServiceSalaire {
    private static final Logger logger = LoggerFactory.getLogger(ServiceSalaire.class);
    private final LacusSmsService serviceManager;
    private Connection conn;
    private final List<String> listString;
    private final SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");

    public ServiceSalaire(LacusSmsService serviceManager, BkEtatOpConfigBean etatOpConfigBean) {
        this.serviceManager = serviceManager;
        this.listString = etatOpConfigBean.getListString();
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }


    public void serviceSalaire() throws SQLException, ParseException {
        BottomPanel.settextLabel("Traitement des salaires en cours.... ", java.awt.Color.BLACK);

        String query = "SELECT b.NCP AS NCP1,b.AGE,b.DCO AS DSAI,b.OPE,b.EVE,b.MON FROM BKMPAI b  WHERE b.NCP >= '0' ORDER BY DCO ASC";

        String msg = "Recherche données salaires.... ";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                logger.info(msg);
                BottomPanel.settextLabel(msg, java.awt.Color.BLACK);
                String numeroCompte = rs.getString(1);
                if (numeroCompte != null) {
                    numeroCompte = numeroCompte.trim();
                    if (numeroCompte.length() >= 10) {
                        BkEve eve = new BkEve();
                        BkAgence bkAgence = serviceManager.getBkAgenceById(rs.getString(2).trim());
                        eve.setBkAgence(bkAgence);
                        BkCli bkCli;
                        String cli;
                        cli = numeroCompte.substring(3, 9);
                        bkCli = serviceManager.getBkCliById(cli);
                        if (bkCli == null) {
                            bkCli = serviceManager.getBkCliByNumCompte(numeroCompte);
                        }
                        eve.setCli(bkCli);
                        eve.setCompte(numeroCompte);
                        eve.setEtat("VA");
                        eve.setHsai("00:00:00.000");
                        eve.setMont(Double.parseDouble(rs.getString(6).trim()));
                        eve.setMontant(rs.getString(6).trim());
                        BkOpe bkOpe = serviceManager.getBkOpeById(rs.getString(4).trim());
                        eve.setOpe(bkOpe);
                        eve.setDVAB(rs.getString(3).trim());
                        eve.setEventDate(format2.parse(format2.format(format1.parse(rs.getString(3).trim()))));
                        eve.setSent(false);
                        eve.setNumEve(rs.getString(5).trim());
                        eve.setId(serviceManager.getMaxIndexBkEve() + 1);
                        eve.setType(TypeEvent.salaire);

                        if (bkCli != null) {
                            if (serviceManager.getBkEveByCriteria(eve.getNumEve(), eve.getEventDate(), eve.getCompte()).isEmpty()) {
                                msg = "Chargement données salaires.... " + eve.getCompte();
                                logger.info(msg);
                                BottomPanel.settextLabel(msg, java.awt.Color.BLACK);
                                serviceManager.enregistrer(eve);
                                ServiceUtils.mettreAjourNumero(serviceManager, conn, bkCli, cli);
                            }
                        }
                    }
                }
            }
        }
    }

    public void envoieSMSSalaire() {
        if (Utils.checkLicence()) {
            List<BkEve> list = serviceManager.getBkEveBySendParam(false, listString, TypeEvent.salaire);
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
                            BottomPanel.settextLabel("OK Message envoyé ", Color.BLACK);
                        }
                    }
                }

            });
        } else {
            BottomPanel.settextLabel("Message non envoyé Problème de Licence veuillez contacter le fournieur !!", Color.RED);
        }
    }
}
