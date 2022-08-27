package com.abouna.lacussms.service;

import com.abouna.lacussms.entities.*;
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

public class ServiceSalaireBKMVTI {
    private static final Logger logger = LoggerFactory.getLogger(ServiceSalaireBKMVTI.class);
    private final LacusSmsService serviceManager;
    private Connection conn;

    public ServiceSalaireBKMVTI(LacusSmsService serviceManager) {
        this.serviceManager = serviceManager;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void serviceSalaireBKMVTI() throws SQLException, ParseException {
        String msg = "Traitement des salaires BKMVTI en cours.... ";
        logger.info(msg);
        BottomPanel.settextLabel(msg, Color.BLACK);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");
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
}
