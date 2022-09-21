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

public class ServiceCredit {
    private static final Logger logger = LoggerFactory.getLogger(ServiceCredit.class);
    private final LacusSmsService serviceManager;
    private final String methode;

    private final String urlParam;

    private Connection conn;

    private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    private final List<String> listString;

    public ServiceCredit(LacusSmsService serviceManager, String methode, String urlParam, List<String> listString) {
        this.serviceManager = serviceManager;
        this.methode = methode;
        this.urlParam = urlParam;
        this.listString = listString;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
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
        try (PreparedStatement ps = conn.prepareStatement(getQuery())) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BottomPanel.settextLabel("Recherche évenements credits.... ", java.awt.Color.BLACK);
                if (rs.getString(1) != null && rs.getString(2) != null) {
                    if (rs.getString(1).trim().length() >= 10) {
                        String age;

                        String queryAgence = "SELECT AGE FROM BKCOM WHERE NCP = '" + rs.getString(1).trim() + "'";

                        try (PreparedStatement psss = conn.prepareStatement(queryAgence)) {
                            ResultSet result = psss.executeQuery();
                            age = null;
                            while (result.next()) {
                                age = result.getString(1).trim();
                            }
                        }
                        BkEve eve = new BkEve();
                        BkAgence bkAgence;
                        if (age != null) {
                            bkAgence = serviceManager.getBkAgenceById(age);
                        } else {
                            bkAgence = serviceManager.getBkAgenceById("00200");
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
                        BkOpe bkOpe = serviceManager.getBkOpeById(rs.getString(3).trim());
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
                            BottomPanel.settextLabel("Chargement données credits.... " + eve.getCompte(), java.awt.Color.BLACK);
                            serviceManager.enregistrer(eve);
                            ServiceUtils.mettreAjourNumero(serviceManager, conn, bkCli, cli);
                        }
                    }
                }
            }
        }
    }

    public void envoieSMSCredit() {
        if (Utils.checkLicence()) {
            List<BkEve> list = serviceManager.getBkEveBySendParam(false, listString, TypeEvent.credit);
            list.forEach((eve) -> {
                BkCli bkCli = eve.getCli();
                if (bkCli != null && eve.getOpe() != null && !"".equals(methode) && bkCli.isEnabled() && serviceManager.getBkCompCliByCriteria(bkCli, eve.getCompte(), true) != null && bkCli.getPhone() != 0L) {
                    MessageFormat mf = serviceManager.getFormatByBkOpe(eve.getOpe(), bkCli.getLangue());
                    if (mf != null) {
                        String text = Utils.remplacerVariable(bkCli, eve.getOpe(), eve, mf);
                        String res = Utils.testConnexionInternet();
                        BottomPanel.settextLabel("Test connexion ...." + res, Color.BLACK);
                        if (res.equals("OK")) {
                            BottomPanel.settextLabel("Envoie du Message à.... " + eve.getCompte(), Color.BLACK);
                            switch (methode) {
                                case "METHO1":
                                case "METHO2":
                                    Sender.send(urlParam, "" + bkCli.getPhone(), text);
                                    break;
                            }
                        } else {
                            String msg1 = "Message non envoyé à.... " + eve.getCompte() + " Problème de connexion internet!!";
                            logger.info(msg1);
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
                            logger.info(msg);
                            BottomPanel.settextLabel(msg, Color.BLACK);
                        }
                    }
                }

            });
        } else {
            String msg = "Message non envoyé Problème de Licence veuillez contacter le fournieur 2.0 !!";
            logger.info(msg);
            BottomPanel.settextLabel(msg, Color.RED);
        }

    }
}
