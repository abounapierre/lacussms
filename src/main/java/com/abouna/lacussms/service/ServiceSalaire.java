package com.abouna.lacussms.service;

import com.abouna.lacussms.dto.BkEtatOpConfigBean;
import com.abouna.lacussms.dto.SendResponseDTO;
import com.abouna.lacussms.entities.*;
import com.abouna.lacussms.sender.context.SenderContext;
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
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.abouna.lacussms.views.tools.ConstantUtils.GET_CONNECTION_NULL_ERROR;
import static com.abouna.lacussms.views.tools.ConstantUtils.SECRET_KEY;

@Component
public class ServiceSalaire {
    private final LacusSmsService serviceManager;
    private final SenderContext senderContext;
    private Connection conn;
    private final List<String> listString;
    private final SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");

    public ServiceSalaire(LacusSmsService serviceManager, SenderContext senderContext, BkEtatOpConfigBean etatOpConfigBean) {
        this.serviceManager = serviceManager;
        this.senderContext = senderContext;
        this.listString = etatOpConfigBean.getListString();
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


    public void serviceSalaire() throws SQLException, ParseException {
        String msg = "Traitement des salaires en cours.... ";
        BottomPanel.settextLabel(msg, java.awt.Color.BLACK);
        String query = "SELECT b.NCP AS NCP1,b.AGE,b.DCO AS DSAI,b.OPE,b.EVE,b.MON FROM BKMPAI b  WHERE b.NCP >= '0' ORDER BY DCO ASC";
        Logger.info(query, ServiceSalaire.class);
        try (PreparedStatement ps = getConn().prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            Logger.info(String.format("nombre de lignes trouvées: %s", ColUtils.getSize(rs)), ServiceSalaire.class);
            while (rs.next()) {
                runServiceSalaire(rs);
            }
        }catch (Throwable e) {
            String errorMessage = "Erreur lors du traitement des salaires";
            Logger.error(String.format("%s: %s", errorMessage, e.getMessage()), e, ServiceSalaire.class);
            BottomPanel.settextLabel(errorMessage, Color.RED);
        } finally {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        }
    }

    private void runServiceSalaire(ResultSet rs) {
        try {
            String numeroCompte = rs.getString(1);
            if (numeroCompte != null) {
                numeroCompte = numeroCompte.trim();
                String msg = "Traitement salaire.... " + numeroCompte;
                Logger.info(msg, ServiceSalaire.class);
                BottomPanel.settextLabel(msg, Color.BLACK);
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
                            Logger.info(msg, ServiceSalaire.class);
                            BottomPanel.settextLabel(msg, Color.BLACK);
                            serviceManager.enregistrer(eve);
                            ServiceUtils.mettreAjourNumero(serviceManager, conn, bkCli, cli);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.error(String.format("%s: %s", "Impossible de traiter ce salaire", e.getMessage()), e, ServiceSalaire.class);
        }
    }

    public void envoieSMSSalaire() {
        try {
            sendSms();
        } catch (Exception e) {
            String errorMessage = "Erreur lors de l'envoi des SMS de salaire";
            Logger.error(String.format("%s: %s", errorMessage, e.getMessage()), e, ServiceSalaire.class);
            BottomPanel.settextLabel(errorMessage, Color.RED);
        }
    }

    private void sendSms() {
        List<BkEve> list = serviceManager.getBkEveBySendParam(false, listString, TypeEvent.salaire);
        Logger.info("Debut envoie de message des salaires....", ServiceSalaire.class);
        list.forEach((eve) -> {
            BkCli bkCli = eve.getCli();
            if (bkCli != null && eve.getOpe() != null && bkCli.isEnabled() && serviceManager.getBkCompCliByCriteria(bkCli, eve.getCompte(), true) != null && bkCli.getPhone() != 0L) {
                MessageFormat mf = serviceManager.getFormatByBkOpe(eve.getOpe(), bkCli.getLangue());
                if (mf != null) {
                    String text = Utils.remplacerVariable(bkCli, eve.getOpe(), eve, mf);
                    String msg = "Envoie du Message à.... " + eve.getCompte();
                    Logger.info(msg, ServiceSalaire.class);
                    BottomPanel.settextLabel(msg, Color.BLACK);
                    SendResponseDTO sendResponseDTO = senderContext.send(String.valueOf(bkCli.getPhone()), text);
                    Message message = new Message();
                    message.setTitle(eve.getOpe().getLib());
                    message.setContent(text);
                    message.setBkEve(eve);
                    message.setSendDate(new Date());
                    message.setNumero(Long.toString(bkCli.getPhone()));
                    message.setSent(sendResponseDTO.isSent());
                    serviceManager.enregistrer(message);
                    eve.setSent(sendResponseDTO.isSent());
                    serviceManager.modifier(eve);
                    msg = sendResponseDTO.getMessage();
                    Logger.info(msg, ServiceSalaire.class);
                    BottomPanel.settextLabel(msg, Color.BLACK);
                }
            }
        });
    }
}
