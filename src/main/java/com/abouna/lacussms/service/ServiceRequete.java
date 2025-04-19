package com.abouna.lacussms.service;

import com.abouna.lacussms.entities.*;
import com.abouna.lacussms.views.tools.ConstantUtils;
import com.abouna.lacussms.views.tools.Sender;
import com.abouna.lacussms.views.main.BottomPanel;
import com.abouna.lacussms.views.tools.AES;
import com.abouna.lacussms.views.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ServiceRequete {
private final LacusSmsService serviceManager;
private final String methode;

private final String urlParam;

private static final Logger logger = LoggerFactory.getLogger(ServiceRequete.class);

    public ServiceRequete(LacusSmsService serviceManager, String methode, String urlParam) {
        this.serviceManager = serviceManager;
        this.methode = methode;
        this.urlParam = urlParam;
    }

    public void serviceRequete() throws SQLException, ParseException {
        BottomPanel.settextLabel("Traitement des requetes en cours.... ", Color.BLACK);
        new SimpleDateFormat("yyyy-MM-dd");
        new SimpleDateFormat("dd/MM/yyyy");
        RemoteDB remoteDB = serviceManager.getDefaultRemoteDB(true);
        String decryptedString = AES.decrypt(remoteDB.getPassword(), ConstantUtils.SECRET_KEY);
        Connection conn = DriverManager.getConnection(remoteDB.getUrl(), remoteDB.getName(), decryptedString);
        List<Command> commands = serviceManager.getCommandByStatus(Status.PENDING);
        String query;
        Iterator<Command> var7 = commands.iterator();
        boolean running = true;
        while(running) {
            while(var7.hasNext()) {
                Command command = var7.next();
                ServiceOffert ser;
                switch (command.getOpe()) {
                    case "SOLDE":
                        ser = serviceManager.findServiceByCode("SOLDE");
                        query = "SELECT b.SIN FROM BKCOM b WHERE b.NCP='" + command.getCompte() + "'";
                        System.err.println("Resquete " + query);
                        PreparedStatement ps = conn.prepareStatement(query);
                        Throwable throwable = null;

                        String solde;
                        try {
                            ResultSet rs = ps.executeQuery();
                            for(solde = ""; rs.next(); solde = rs.getBigDecimal("SIN").toPlainString()) {
                            }
                        } catch (Throwable throwable1) {
                            throwable = throwable1;
                            throw throwable1;
                        } finally {
                            if (ps != null) {
                                if (throwable != null) {
                                    try {
                                        ps.close();
                                    } catch (Throwable var22) {
                                        throwable.addSuppressed(var22);
                                    }
                                } else {
                                    ps.close();
                                }
                            }
                        }

                        String msg = "Cher client au compte " + command.getCompte() + " votre solde est de " + solde + ".";
                        command.setMessage(msg);
                        command.setMontant(ser.getMontant());
                        envoieSmsRequete(command);
                        break;
                    case "HIST":
                        ser = serviceManager.findServiceByCode("HIST");
                        query = "SELECT b.MCTV, b.EVE, b.AGE, b.DVA, b.OPE FROM BKHIS b WHERE b.NCP='" + command.getCompte() + "' ORDER BY b.DVA ASC";
                        PreparedStatement psmt = conn.prepareStatement(query);
                        ResultSet rs = psmt.executeQuery();
                        int i = 0;
                        StringBuilder builder = new StringBuilder();
                        builder.append("Cher client au compte ");
                        builder.append(command.getCompte());
                        builder.append(" ");
                        builder.append("voici les 5 derières operations ");

                        while(rs.next()) {
                            BkAgence bkAgence = serviceManager.getBkAgenceById(rs.getString("AGE").trim());
                            BkOpe bkOpe = serviceManager.getBkOpeById(rs.getString("OPE").trim());
                            builder.append(bkOpe.getLib());
                            builder.append(" ");
                            builder.append(rs.getString("MCTV").trim());
                            builder.append(" ");
                            builder.append(rs.getString("DVA").trim());
                            builder.append(" ");
                            builder.append(bkAgence.getNoma());
                            builder.append("\n");
                            ++i;
                            if (i == 5) {
                                break;
                            }
                        }

                        command.setMontant(ser.getMontant());
                        command.setMessage(builder.toString());
                        envoieSmsRequete(command);
                }
            }

            conn.close();
            return;
        }
    }

    public void envoieSmsRequete(Command command) {
        String msg;
        if (Utils.checkLicence()) {
            String res = Utils.testConnexionInternet();
            BottomPanel.settextLabel("Test connexion ...." + res, Color.BLACK);
            if (res.equals("OK")) {
                BottomPanel.settextLabel("Envoie du Message à.... " + command.getCompte(), Color.BLACK);
                switch (methode) {
                    case "METHO1":
                        Sender.send(urlParam, command.getPhone(), command.getMessage());
                        break;
                    case "METHO2":
                        Sender.send(urlParam, command.getPhone(), command.getMessage());
                }
            } else {
                msg = "Message non envoyé à.... " + command.getCompte() + " Problème de connexion internet!!";
                logger.info(msg);
                BottomPanel.settextLabel(msg, Color.RED);
                command.setErrorDescription("problème de connexion internet");
                serviceManager.modifier(command);
            }

            if (res.equals("OK")) {
                command.setStatus(Status.PROCESSED);
                command.setProcessedDate(new Date());
                serviceManager.modifier(command);
                BottomPanel.settextLabel("OK Message envoyé ", Color.BLACK);
            }
        } else {
            msg = "Message non envoyé Problème de Licence veuillez contacter le fournieur 1.2.5 !!";
            logger.info(msg);
            BottomPanel.settextLabel(msg, Color.RED);
            command.setErrorDescription("problème de licence");
            serviceManager.modifier(command);
        }

    }

    public void setConn(Connection connexion) {
    }
}
