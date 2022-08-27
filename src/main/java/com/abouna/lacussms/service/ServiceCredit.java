package com.abouna.lacussms.service;

import com.abouna.lacussms.entities.*;
import com.abouna.lacussms.main.Sender;
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

    public void serviceCredit() throws SQLException, ParseException {
        String msg = "Traitement des credits en cours.... ";
        logger.info(msg);
        BottomPanel.settextLabel(msg, Color.BLACK);
        SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");
        Date current = new Date();
        String currentString = f2.format(current);
        int compteur = 0;
        if (serviceManager.getMaxIndexBkEve(TypeEvent.credit) != null) {
            compteur = serviceManager.getMaxIndexBkEve(TypeEvent.credit);
        }

        String query;
        if (compteur == 0) {
            query = "SELECT b.NCP AS NCP1,b.CAI,b.OPE,b.EVE,b.MNT,b.HEU,b.ETA FROM BKMAC b WHERE b.NCP >= '0' AND b.CAI >= '0' ORDER BY HEU ASC";
        } else {
            BkEve bkEve = serviceManager.getBkEveById(compteur);
            String hsai = bkEve.getHsai();
            Date date = bkEve.getEventDate();
            if (!currentString.equals(f2.format(date))) {
                query = "SELECT b.NCP AS NCP1,b.CAI,b.OPE,b.EVE,b.MNT,b.HEU,b.ETA FROM BKMAC b WHERE b.NCP >= '0' AND b.CAI >= '0' ORDER BY HEU ASC";
            } else {
                if (Integer.parseInt(hsai) > 1) {
                    hsai = Integer.parseInt(hsai) - 100 + "";
                    if (hsai.length() == 3) {
                        hsai = "0" + hsai;
                    }
                }

                query = "SELECT b.NCP AS NCP1,b.CAI,b.OPE,b.EVE,b.MNT,b.HEU,b.ETA FROM BKMAC b WHERE b.NCP >= '0' AND b.CAI >= '0' AND HEU > '" + hsai + "'  ORDER BY HEU ASC";
            }
        }

        PreparedStatement ps = conn.prepareStatement(query);
        Throwable var73 = null;

        try {
            ResultSet rs = ps.executeQuery();

            label766:
            while(true) {
                BkEve eve;
                BkCli bkCli;
                boolean traitement;
                do {
                    do {
                        do {
                            do {
                                if (!rs.next()) {
                                    break label766;
                                }

                                msg = "Recherche évenements credits.... ";
                                logger.info(msg);
                                BottomPanel.settextLabel(msg, Color.BLACK);
                            } while(rs.getString("NCP1") == null);
                        } while(rs.getString("CAI") == null);
                    } while(rs.getString("NCP1").trim().length() < 10);

                    PreparedStatement psss = conn.prepareStatement("SELECT AGE FROM BKCOM WHERE NCP = '" + rs.getString("NCP1").trim() + "'");
                    Throwable var14 = null;

                    String age;
                    try {
                        ResultSet result = psss.executeQuery();

                        for(age = null; result.next(); age = result.getString("AGE").trim()) {
                        }
                    } catch (Throwable var66) {
                        var14 = var66;
                        throw var66;
                    } finally {
                        if (psss != null) {
                            if (var14 != null) {
                                try {
                                    psss.close();
                                } catch (Throwable var65) {
                                    var14.addSuppressed(var65);
                                }
                            } else {
                                psss.close();
                            }
                        }

                    }

                    eve = new BkEve();
                    BkAgence bkAgence;
                    if (age != null) {
                        bkAgence = serviceManager.getBkAgenceById(age);
                    } else {
                        bkAgence = serviceManager.getBkAgenceById("00200");
                    }

                    eve.setBkAgence(bkAgence);
                    String cli = "";
                    if (rs.getString("NCP1").trim().length() >= 9) {
                        cli = rs.getString("NCP1").trim().substring(3, 9);
                    }

                    bkCli = serviceManager.getBkCliById(cli);
                    if (bkCli == null) {
                        bkCli = serviceManager.getBkCliByNumCompte(rs.getString("NCP1").trim());
                    }

                    eve.setCli(bkCli);
                    eve.setCompte(rs.getString("NCP1").trim());
                    eve.setEtat(rs.getString("ETA").trim());
                    eve.setHsai(rs.getString("HEU").trim());
                    eve.setMont(Double.parseDouble(rs.getString("MNT").trim().replace(".", "")));
                    eve.setMontant(rs.getString("MNT").trim().replace(".", "").replace(" ", ""));
                    BkOpe bkOpe = serviceManager.getBkOpeById(rs.getString("OPE").trim());
                    eve.setOpe(bkOpe);
                    eve.setDVAB(f2.format(new Date()));
                    eve.setEventDate(f2.parse(f2.format(new Date())));
                    eve.setSent(false);
                    eve.setNumEve(rs.getString("EVE").trim());
                    eve.setId(serviceManager.getMaxIndexBkEve() + 1);
                    eve.setType(TypeEvent.credit);
                    traitement = bkCli != null;

                    if (!serviceManager.getBkEveByCriteria(eve.getNumEve(), eve.getEventDate(), eve.getCompte()).isEmpty()) {
                        traitement = false;
                    }

                    if (!serviceManager.getBkEveByCriteriaMontant(eve.getNumEve(), eve.getCompte(), eve.getNumEve()).isEmpty()) {
                        traitement = false;
                    }

                    if (!serviceManager.getBkEveByPeriode(eve.getNumEve(), eve.getCompte(), Utils.add(eve.getEventDate(), -3L), eve.getEventDate()).isEmpty()) {
                        traitement = false;
                    }
                } while(!traitement);

                msg = "Chargement données credits.... " + eve.getCompte();
                logger.info(msg);
                BottomPanel.settextLabel(msg, Color.BLACK);
                serviceManager.enregistrer(eve);
                String q = "SELECT b.NUM, b.CLI, b.TYP FROM bktelcli b WHERE b.CLI='" + rs.getString("NCP1").trim().substring(3, 9) + "'";
                PreparedStatement pss = conn.prepareStatement(q);
                Throwable var21 = null;

                try {
                    ResultSet resultat = pss.executeQuery();
                    int n = 0;

                    while(true) {
                        while(true) {
                            do {
                                if (!resultat.next()) {
                                    continue label766;
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
                } catch (Throwable var68) {
                    var21 = var68;
                    throw var68;
                } finally {
                    if (pss != null) {
                        if (var21 != null) {
                            try {
                                pss.close();
                            } catch (Throwable var64) {
                                var21.addSuppressed(var64);
                            }
                        } else {
                            pss.close();
                        }
                    }

                }
            }
        } catch (Throwable var70) {
            var73 = var70;
            throw var70;
        } finally {
            if (ps != null) {
                if (var73 != null) {
                    try {
                        ps.close();
                    } catch (Throwable var63) {
                        var73.addSuppressed(var63);
                    }
                } else {
                    ps.close();
                }
            }

        }

        conn.close();
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
