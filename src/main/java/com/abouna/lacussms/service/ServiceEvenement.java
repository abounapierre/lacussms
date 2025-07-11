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

import static com.abouna.lacussms.service.ServiceUtils.isStopped;
import static com.abouna.lacussms.views.tools.ConstantUtils.GET_CONNECTION_NULL_ERROR;
import static com.abouna.lacussms.views.tools.ConstantUtils.SECRET_KEY;

@Component
public class ServiceEvenement {
    private final LacusSmsService serviceManager;
    private Connection conn;
    private final String condition;
    private final List<String> listString;
    private final SenderContext senderContext;

    SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat format3 = new SimpleDateFormat("dd/MM/yyyy");

    public ServiceEvenement(LacusSmsService serviceManager, BkEtatOpConfigBean bkEtatOpConfigBean, SenderContext senderContext) {
        this.serviceManager = serviceManager;
        this.condition = bkEtatOpConfigBean.getCondition();
        this.listString = bkEtatOpConfigBean.getListString();
        this.senderContext = senderContext;
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

    public void envoieSMSEvenement() {
        try {
            runSms();
        } catch (Exception e) {
            String errorMessage = "Erreur lors de l'envoie de message des évènements";
            Logger.error(String.format("%s: %s", errorMessage, e.getMessage()), e, ServiceEvenement.class);
        }
    }

    private void runSms() {
        Logger.info(String.format("Début envoie de message des évènements....%s", listString), ServiceEvenement.class);
        List<BkEve> list = serviceManager.getBkEveBySendParam(false, listString, TypeEvent.ordinaire);
        Logger.info(String.format("Nombre d'évènement à traiter %s", list.size()), ServiceEvenement.class);
        for (BkEve eve : list) {
            if (isStopped()) return;
            if (serviceManager.getMessageByEveId(eve.getId()).isPresent()) {
                String msg = "Un message existe déjà pour l'évènement: " + eve.getNumEve();
                Logger.info(msg, false, ServiceEvenement.class);
                BottomPanel.settextLabel(msg, Color.BLACK);
                continue;
            }
            BkCli bkCli = eve.getCli();
            boolean verify = bkCli != null && eve.getOpe() != null && bkCli.isEnabled() && serviceManager.getBkCompCliByCriteria(bkCli, eve.getCompte(), true) != null && bkCli.getPhone() != 0L;
            logVerification(eve, verify);
            if (verify) {
                MessageFormat mf = serviceManager.getFormatByBkOpe(eve.getOpe(), bkCli.getLangue());
                if (mf != null) {
                    String text = Utils.remplacerVariable(bkCli, eve.getOpe(), eve, mf);
                    String msg = "Envoie du Message à.... " + eve.getCli().getPhone();
                    Logger.info(msg, ServiceEvenement.class);
                    BottomPanel.settextLabel(msg, Color.BLACK);
                    SendResponseDTO sendResponseDTO = senderContext.send(String.valueOf(bkCli.getPhone()), text);
                    Message message = new Message();
                    message.setTitle(eve.getOpe().getLib());
                    message.setContent(text);
                    message.setBkEve(eve);
                    message.setSendDate(new Date());
                    message.setNumero(Long.toString(bkCli.getPhone()));
                    message.setSent(sendResponseDTO.isSent());
                    message.setSendTime(new Date());
                    message.setNumEve(eve.getNumEve());
                    serviceManager.enregistrer(message);
                    eve.setSent(sendResponseDTO.isSent());
                    serviceManager.modifier(eve);
                    msg = sendResponseDTO.getMessage();
                    Logger.info(msg, ServiceEvenement.class);
                    BottomPanel.settextLabel(msg, Color.BLACK);
                }
            }
        }
    }

    private static void logVerification(BkEve eve, boolean verify) {
        Logger.info(String.format("Sms évènement: vérification des conditions: eve %s vérifié: %s", eve.getNumEve(), verify), false, ServiceEvenement.class);
        BottomPanel.settextLabel(String.format("Sms évènement: vérification des conditions: eve %s vérifié: %s", eve.getNumEve(), verify), Color.BLACK);
    }

    public void serviceEvenement() throws SQLException, ParseException {
        String query = getQuery();
        BottomPanel.settextLabel("Recherche évènements....", java.awt.Color.BLACK);
        Logger.info(query, ServiceEvenement.class);
        try (PreparedStatement ps = getConn().prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            Logger.info(String.format("nombre de lignes trouvées: %s", ColUtils.getSize(rs)), ServiceEvenement.class);
            String msg = "Recherche des évènements en cours...." + ps.getFetchSize();
            Logger.info(String.format("##### %s ######", msg), ServiceEvenement.class);
            while (rs.next()) {
                if (isStopped()) return;
                runServiceEvt(rs);
            }
        }catch (Throwable e) {
            String errorMessage = "Erreur lors du traitement des évènements";
            Logger.error(String.format("%s: %s", errorMessage, e.getMessage()), e, ServiceEvenement.class);
            BottomPanel.settextLabel(errorMessage, Color.RED);
        } finally {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        }
    }

    private void runServiceEvt(ResultSet rs) {
        String msg;
        try {
            String numeroCompte = rs.getString("NCP1");
            String numEve = ColUtils.getColValue(rs.getString("EVE"), "EVE", String.class);
            if (numeroCompte != null && numeroCompte.trim().length() >= 10) {
                String print = String.format("Service évenement récupération eve: %s, compte: %s", numEve, numeroCompte);
                Logger.info(print, ServiceEvenement.class);
                BottomPanel.settextLabel(print, Color.BLACK);
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
                eve.setEventDate(format3.parse(format3.format(format2.parse(rs.getString("DSAI").trim()))));
                eve.setSent(false);
                eve.setNumEve(rs.getString("EVE").trim());
                eve.setId(serviceManager.getMaxIndexBkEve() + 1);
                eve.setType(TypeEvent.ordinaire);
                eve.setCreationDate(new Date());


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
                    Logger.info(msg, ServiceEvenement.class);
                    BottomPanel.settextLabel(msg, Color.BLACK);
                    serviceManager.enregistrer(eve);
                    ServiceUtils.mettreAjourNumero(serviceManager, conn, bkCli, cli);
                }else {
                    msg = "Evènement déjà traité.... " + eve.getNumEve();
                    Logger.info(msg, ServiceEvenement.class);
                    BottomPanel.settextLabel(msg, Color.RED);
                }
            }
        } catch (Exception e) {
            Logger.error(String.format("Impossible de traiter cet évènement: %s", e.getMessage()), e, ServiceEvenement.class);
        }
    }

    public String getQuery() {
        int compteur = serviceManager.getMaxIndexBkEve(TypeEvent.ordinaire);
        String heure, date, currentString;
        Date current = new Date();
        String query, query1, finalquery, heureInit = "00:00:00.000";
        if (compteur == 0) {
            heure = "00:00:00.000";
            date = format1.format(current);
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
            currentString = format1.format(current);
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
