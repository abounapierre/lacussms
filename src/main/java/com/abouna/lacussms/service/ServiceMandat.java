package com.abouna.lacussms.service;

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
import java.util.Optional;

import static com.abouna.lacussms.service.ServiceUtils.isStopped;
import static com.abouna.lacussms.views.tools.ConstantUtils.GET_CONNECTION_NULL_ERROR;
import static com.abouna.lacussms.views.tools.ConstantUtils.SECRET_KEY;

@Component
public class ServiceMandat {
    private final LacusSmsService serviceManager;
    private final SenderContext senderContext;
    private final SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
    private Connection conn;


    public ServiceMandat(LacusSmsService serviceManager, SenderContext senderContext) {
        this.serviceManager = serviceManager;
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

    public void serviceMandat() throws SQLException, ParseException {
        String msg = "Traitement des mandats en cours.... ";
        BottomPanel.settextLabel(msg, Color.BLACK);
        Logger.info(msg, ServiceMandat.class);
        String query = getQuery();
        Logger.info(query, ServiceMandat.class);
        try (PreparedStatement ps = getConn().prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            Logger.info(String.format("Le nombre de mandats trouvés est de: %s", ColUtils.getSize(rs)), ServiceMandat.class);
            while (rs.next()) {
                if (isStopped()) return;
                runServiceMandat(rs);
            }
        }catch (Throwable e) {
            String errorMessage = "Erreur lors du traitement des mandats";
            Logger.error(String.format("%s: %s", errorMessage, e.getMessage()), e, ServiceMandat.class);
            BottomPanel.settextLabel(errorMessage, Color.RED);
        }finally {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        }
    }

    private void runServiceMandat(ResultSet rs) {
        String msg;
        try {
            String num = rs.getString(1).trim();
            msg = String.format("Récuperation du mandats.... %s", num);
            Logger.info(msg, ServiceMandat.class);
            BottomPanel.settextLabel(msg, Color.BLACK);
            BkMad bkMad = serviceManager.getBkMadByClesec(num);
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
                eve.setClesec(num);
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
                eve.setCreationDate(new Date());
                msg = "Chargement données salaires.... " + eve.getAd1p();
                Logger.info(msg, ServiceMandat.class);
                BottomPanel.settextLabel(msg, Color.BLACK);
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
        } catch (Exception e) {
            Logger.error(String.format("%s: %s", "Impossible de traiter ce mandat", e.getMessage()), e, ServiceMandat.class);
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
        try {
            runSms();
        } catch (Exception e) {
            String msg = "Erreur lors de l'envoie de message: %s";
            Logger.error(String.format(msg, e.getMessage()), e, ServiceMandat.class);
        }
    }

    private void runSms() {
        boolean bon = false;
        String msg;
        msg = "Debut envoie de message mandats....";
        Logger.info(msg, ServiceMandat.class);
        BottomPanel.settextLabel(msg, Color.BLACK);
        List<BkMad> list = serviceManager.getBkMadByTraite();

        for (BkMad eve : list) {
            if (isStopped()) return;
            if (serviceManager.getMessageMandatByNumEve(eve.getEve()).isPresent()){
                msg = "Un message existe déjà pour le mandat: " + eve.getEve();
                Logger.info(msg, false, ServiceEvenement.class);
                BottomPanel.settextLabel(msg, Color.BLACK);
                continue;
            }
            BottomPanel.settextLabel(String.format("Vérification téléphone: getAd1p %s, getAd2p %s", eve.getAd1p(), eve.getAd2p()), Color.BLACK);
            if (Utils.testPhone(eve.getAd1p()) != null && Utils.testPhone(eve.getAd2p()) != null && eve.getOpe() != null) {
                MessageFormat mf = serviceManager.getFormatByBkOpe(eve.getOpe(), "FR");
                if (mf == null) {
                    String errorMsg = "Format de message non trouvé pour l'opération: " + eve.getOpe().getLib();
                    Logger.info(errorMsg, ServiceMandat.class);
                    continue;
                }
                SendResponseDTO sendResponseDTO = null;
                String text = Utils.remplacerVariable(eve, mf);
                if (eve.getTraite() == 0) {
                    BottomPanel.settextLabel("Envoie du Message à.... " + eve.getAd2p(), Color.BLACK);
                    sendResponseDTO = senderContext.send(eve.getAd2p(), text);
                } else if (eve.getTraite() == 1) {
                    BkOpe bkOpe = Optional.ofNullable(eve.getOpe()).orElseThrow(() -> new RuntimeException("Opération de mandat est obligatoire"));
                    mf = serviceManager.getFormatByBkOpe(bkOpe, "FR");
                    text = Utils.remplacerVariable(eve, mf);
                    bon = true;
                    BottomPanel.settextLabel("Envoie du Message à.... " + eve.getAd1p(), Color.BLACK);
                    sendResponseDTO = senderContext.send(eve.getAd1p(), text);
                }
                if(sendResponseDTO == null) {
                    Logger.info("Erreur d'envoie de message", ServiceMandat.class);
                    continue;
                }
                MessageMandat message = new MessageMandat();
                message.setTitle(eve.getOpe().getLib());
                message.setContent(text);
                message.setBkMad(eve.getId());
                message.setBkMadEve(eve.getEve());
                message.setSendDate(new Date());
                message.setNumEve(eve.getEve());
                message.setSent(sendResponseDTO.isSent());
                if (eve.getTraite() == 0) {
                    eve.setTraite(1);
                    eve.setSent(sendResponseDTO.isSent());
                    message.setNumero(eve.getAd2p());
                    message.setSendTime(new Date());
                    serviceManager.enregistrer(message);
                } else if (eve.getTraite() == 1 && bon) {
                    eve.setTraite(2);
                    eve.setSent(sendResponseDTO.isSent());
                    message.setNumero(eve.getAd1p());
                    serviceManager.enregistrer(message);
                }
                serviceManager.modifier(eve);
                msg = sendResponseDTO.getMessage();
                Logger.info(msg, ServiceMandat.class);
                BottomPanel.settextLabel(msg, Color.BLACK);
            }
        }
    }
}
