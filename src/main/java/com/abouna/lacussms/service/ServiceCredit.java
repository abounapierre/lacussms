package com.abouna.lacussms.service;

import com.abouna.lacussms.dto.BkEtatOpConfigBean;
import com.abouna.lacussms.entities.*;
import com.abouna.lacussms.views.tools.Sender;
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

import static com.abouna.lacussms.service.ColUtils.getColValue;
import static com.abouna.lacussms.views.tools.ConstantUtils.DEFAULT_ACCOUNT_LENGTH;
import static com.abouna.lacussms.views.tools.ConstantUtils.DEFAULT_AGENCE_CODE;
import static com.abouna.lacussms.views.tools.ConstantUtils.GET_CONNECTION_NULL_ERROR;
import static com.abouna.lacussms.views.tools.ConstantUtils.SECRET_KEY;

@Component
public class ServiceCredit {
    private final LacusSmsService serviceManager;

    private Connection conn;

    private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    private final List<String> listString;

    public ServiceCredit(LacusSmsService serviceManager, BkEtatOpConfigBean etatOpConfigBean) {
        this.serviceManager = serviceManager;
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

    private String getQuery() {
        Date current = new Date();
        String currentString = format.format(current);

        int compteur = 0;
        if (serviceManager.getMaxIndexBkEve(TypeEvent.credit) != null) {
            compteur = serviceManager.getMaxIndexBkEve(TypeEvent.credit);
        }

        String query, hsai;
        if (compteur == 0) {
            query = "SELECT b.NCP AS NCP1,b.CAI,b.OPE,b.EVE,b.MNT,b.HEU,b.ETA FROM BKMAC b WHERE b.NCP >= '0' AND b.CAI >= '0' ORDER BY HEU ASC";
        } else {
            BkEve bkEve = serviceManager.getBkEveById(compteur);
            hsai = bkEve.getHsai();
            Date date = bkEve.getEventDate();
            if (!currentString.equals(format.format(date))) {
                query = "SELECT b.NCP AS NCP1,b.CAI,b.OPE,b.EVE,b.MNT,b.HEU,b.ETA FROM BKMAC b WHERE b.NCP >= '0' AND b.CAI >= '0' ORDER BY HEU ASC";
            } else {
                if (Integer.parseInt(hsai) > 1) {
                    hsai = Integer.parseInt(hsai) - 100 + "";
                    if (hsai.length() == 3) {
                        hsai = "0" + hsai;
                    }
                }
                query = "SELECT b.NCP ,b.CAI ,b.OPE ,b.EVE,b.MNT,b.HEU,b.ETA FROM BKMAC b WHERE b.NCP >= '0' AND b.CAI >= '0' AND HEU > '" + hsai + "'  ORDER BY HEU ASC";
            }
        }
        return query;
    }

    public void serviceCredit() throws SQLException, ParseException {
        BottomPanel.settextLabel("Traitement des credits en cours.... ", java.awt.Color.BLACK);
        String query = getQuery();
        Logger.info(query, ServiceCredit.class);
        try (PreparedStatement ps = getConn().prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            Logger.info(String.format("nombre de lignes trouvées: %s", ColUtils.getSize(rs)), ServiceCredit.class);
            while (rs.next()) {
                runServiceCredit(rs);
            }
        }catch (Throwable e) {
            String errorMessage = "Erreur lors du traitement des crédit";
            Logger.error(String.format("%s: %s", errorMessage, e.getMessage()), e, ServiceCredit.class);
            BottomPanel.settextLabel(errorMessage, Color.RED);
        } finally {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        }
    }

    private void runServiceCredit(ResultSet rs) {
        try {
            String numeroCompte = getColValue(rs.getString(1), "NCP", String.class);
            String ope = getColValue(rs.getString(3), "OPE", String.class);
            BottomPanel.settextLabel(String.format("Salaire Récupération client: %s", numeroCompte), Color.BLACK);
            if (numeroCompte.trim().length() >= DEFAULT_ACCOUNT_LENGTH) {
                String age;

                String queryAgence = "SELECT AGE FROM BKCOM WHERE NCP = '" + numeroCompte.trim() + "'";

                try (PreparedStatement agencePreparedStatement = getConn().prepareStatement(queryAgence)) {
                    ResultSet result = agencePreparedStatement.executeQuery();
                    age = null;
                    while (result.next()) {
                        age = result.getString(1).trim();
                        Logger.info("Recuperation de l'agence: " + age, ServiceCredit.class);
                    }
                }
                BkEve eve = new BkEve();
                BkAgence bkAgence;
                if (age != null) {
                    bkAgence = serviceManager.getBkAgenceById(age);
                } else {
                    bkAgence = serviceManager.getBkAgenceById(DEFAULT_AGENCE_CODE);
                }
                eve.setBkAgence(bkAgence);
                String cli = rs.getString(1).trim();
                if (cli.length() >= 9) {
                    cli = cli.substring(3, 9);
                }
                BkCli bkCli = serviceManager.getBkCliById(cli);
                if (bkCli == null) {
                    bkCli = serviceManager.getBkCliByNumCompte(cli);
                }
                eve.setCli(bkCli);
                eve.setCompte(rs.getString(1).trim());
                eve.setEtat(rs.getString(7).trim());
                eve.setHsai(rs.getString(6).trim());
                eve.setMont(Double.parseDouble(rs.getString(5).trim().replace(".", "")));
                eve.setMontant(rs.getString(5).trim().replace(".", "").replace(" ", ""));
                BkOpe bkOpe = serviceManager.getBkOpeById(ope.trim());
                eve.setOpe(bkOpe);
                eve.setDVAB(format.format(new Date()));
                eve.setEventDate(format.parse(format.format(new Date())));
                eve.setSent(false);
                eve.setNumEve(rs.getString(4).trim());
                eve.setId(serviceManager.getMaxIndexBkEve() + 1);
                eve.setType(TypeEvent.credit);

                boolean traitement = bkCli != null;

                if (!serviceManager.getBkEveByCriteria(eve.getNumEve(), eve.getEventDate(), eve.getCompte()).isEmpty()) {
                    traitement = false;
                }

                if (!serviceManager.getBkEveByCriteriaMontant(eve.getNumEve(), eve.getCompte(), eve.getNumEve()).isEmpty()) {
                    traitement = false;
                }

                if (!serviceManager.getBkEveByPeriode(eve.getNumEve(), eve.getCompte(), Utils.add(eve.getEventDate(), -3), eve.getEventDate()).isEmpty()) {
                    traitement = false;
                }

                if (traitement) {
                    BottomPanel.settextLabel("Chargement données credits.... " + eve.getCompte(), Color.BLACK);
                    serviceManager.enregistrer(eve);
                    ServiceUtils.mettreAjourNumero(serviceManager, conn, bkCli, cli);
                }
            }
        }catch (Exception e) {
            String errorMessage = "Erreur lors du traitement des crédits";
            Logger.error(String.format("%s: %s", errorMessage, e.getMessage()), e, ServiceCredit.class);
        }
    }

    public void envoieSMSCredit() {
        Logger.info("Debut envoie de message des crédits....", ServiceCredit.class);
        List<BkEve> list = serviceManager.getBkEveBySendParam(false, listString, TypeEvent.credit);
        list.forEach((eve) -> {
            BkCli bkCli = eve.getCli();
            if (bkCli != null && eve.getOpe() != null && bkCli.isEnabled() && serviceManager.getBkCompCliByCriteria(bkCli, eve.getCompte(), true) != null && bkCli.getPhone() != 0L) {
                MessageFormat mf = serviceManager.getFormatByBkOpe(eve.getOpe(), bkCli.getLangue());
                if (mf != null) {
                    String text = Utils.remplacerVariable(bkCli, eve.getOpe(), eve, mf);
                    String res = Utils.testConnexionInternet();
                    BottomPanel.settextLabel("Test connexion ...." + res, Color.BLACK);
                    if (res.equals("OK")) {
                        BottomPanel.settextLabel("Envoie du Message à.... " + eve.getCompte(), Color.BLACK);
                        Sender.send(String.valueOf(bkCli.getPhone()) , text);
                    } else {
                        String msg1 = "Message non envoyé à.... " + eve.getCompte() + " Problème de connexion internet!!";
                        Logger.info(msg1, ServiceCredit.class);
                        BottomPanel.settextLabel(msg1, Color.RED);
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
                        String msg = "OK Message envoyé ";
                        Logger.info(msg, ServiceCredit.class);
                        BottomPanel.settextLabel(msg, Color.BLACK);
                    }
                }
            }
        });
    }
}
