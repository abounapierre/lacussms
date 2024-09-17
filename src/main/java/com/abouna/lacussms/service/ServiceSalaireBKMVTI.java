package com.abouna.lacussms.service;

import com.abouna.lacussms.entities.*;
import com.abouna.lacussms.views.main.BottomPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        BottomPanel.settextLabel("Traitement des salaires BKMVTI en cours.... ", java.awt.Color.BLACK);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");

        String query = "SELECT b.AGE,b.NCP AS NCP1,b.OPE,b.EVE,b.DCO AS DSAI,b.DVA,b.MON FROM BKMVTI b WHERE b.NCP >= '0' ORDER BY DCO ASC";
        logger.info("query {}", query);
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String msg = "Recherche données salaires BKMVTI.... ";
                BottomPanel.settextLabel(msg, java.awt.Color.BLACK);
                logger.info(msg);
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
                        eve.setEventDate(format2.parse(format2.format(format1.parse(rs.getString(5).trim()))));
                        eve.setSent(false);
                        eve.setNumEve(rs.getString(4).trim());
                        eve.setId(serviceManager.getMaxIndexBkEve() + 1);
                        eve.setType(TypeEvent.salaire);

                        if (bkCli != null) {
                            if (serviceManager.getBkEveByCriteria(eve.getNumEve(), eve.getEventDate(), eve.getCompte()).isEmpty()) {
                                msg = "Chargement données salaires BKMVTI.... " + eve.getCompte();
                                BottomPanel.settextLabel(msg, java.awt.Color.BLACK);
                                logger.info(msg);
                                serviceManager.enregistrer(eve);
                                ServiceUtils.mettreAjourNumero(serviceManager, conn, bkCli, cli);
                            }
                        }
                    }
                }
            }
        }
    }
}
