package com.abouna.lacussms.service;

import com.abouna.lacussms.entities.*;
import com.abouna.lacussms.views.main.BottomPanel;
import com.abouna.lacussms.views.tools.Utils;
import com.abouna.lacussms.views.utils.Logger;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

import static com.abouna.lacussms.views.tools.ConstantUtils.GET_CONNECTION_NULL_ERROR;
import static com.abouna.lacussms.views.tools.ConstantUtils.SECRET_KEY;

@Component
public class ServiceSalaireBKMVTI {
    private final LacusSmsService serviceManager;
    private Connection conn;

    public ServiceSalaireBKMVTI(LacusSmsService serviceManager) {
        this.serviceManager = serviceManager;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public Connection getConn() {
        if (conn == null) {
            conn = Objects.requireNonNull(Utils.testConnexion(serviceManager, SECRET_KEY), GET_CONNECTION_NULL_ERROR);
        }
        return conn;
    }

    public void serviceSalaireBKMVTI() throws SQLException, ParseException {
        BottomPanel.settextLabel("Traitement des salaires BKMVTI en cours.... ", java.awt.Color.BLACK);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");

        String query = "SELECT b.AGE,b.NCP AS NCP1,b.OPE,b.EVE,b.DCO AS DSAI,b.DVA,b.MON FROM BKMVTI b WHERE b.NCP >= '0' ORDER BY DCO ASC";
        Logger.info(query, ServiceSalaireBKMVTI.class);
        try (PreparedStatement ps = getConn().prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            Logger.info(String.format("nombre de lignes trouvées: %s", ColUtils.getSize(rs)), ServiceSalaireBKMVTI.class);
            String msg = "Recherche données salaires BKMVTI.... ";
            BottomPanel.settextLabel(msg, java.awt.Color.BLACK);
            while (rs.next()) {
                runServiceBKMVTI(msg, rs, format2, format1);
            }
        }catch (Throwable e) {
            String errorMessage = "Erreur lors du traitement des salaires BKMVTI";
            Logger.error(String.format("%s: %s", errorMessage, e.getMessage()), e, ServiceSalaireBKMVTI.class);
            BottomPanel.settextLabel(errorMessage, Color.RED);
        } finally {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        }
    }

    private void runServiceBKMVTI(String msg, ResultSet rs, SimpleDateFormat format2, SimpleDateFormat format1) {
        try {
            Logger.info(msg, ServiceSalaireBKMVTI.class);
            String compte = rs.getString(2);
            if (compte != null) {
                if (compte.trim().length() >= 10) {
                    BkEve eve = new BkEve();
                    BkAgence bkAgence = serviceManager.getBkAgenceById(rs.getString(1).trim());
                    eve.setBkAgence(bkAgence);
                    String cli = "";
                    cli = compte.trim().substring(3, 9);
                    BkCli bkCli = serviceManager.getBkCliById(cli);
                    if (bkCli == null) {
                        bkCli = serviceManager.getBkCliByNumCompte(compte.trim());
                    }
                    eve.setCli(bkCli);
                    eve.setCompte(compte.trim());
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
                            BottomPanel.settextLabel(msg, Color.BLACK);
                            Logger.info(msg, ServiceSalaireBKMVTI.class);
                            serviceManager.enregistrer(eve);
                            ServiceUtils.mettreAjourNumero(serviceManager, conn, bkCli, cli);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.error(String.format("%s: %s", "Impossible de traiter ce salaire BKMVTI", e.getMessage()), e, ServiceSalaireBKMVTI.class);
        }
    }
}
