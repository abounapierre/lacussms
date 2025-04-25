//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.abouna.lacussms.main;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.Config;
import com.abouna.lacussms.service.*;
import com.abouna.lacussms.views.main.BottomPanel;
import com.abouna.lacussms.views.tools.ConstantUtils;
import com.abouna.lacussms.views.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static ScheduledExecutorService firstExecutor;
    public static boolean appliRun = false;
    static boolean vl = false;
    private static LacusSmsService serviceManager;

    public static void initApp() throws IOException, ClassNotFoundException, SQLException {
        serviceManager = ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
        firstExecutor = Executors.newSingleThreadScheduledExecutor();

        vl = true;
        try {
            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
        } catch (IllegalAccessException | UnsupportedLookAndFeelException | InstantiationException e) {
            logger.error("Error: ", e);
        }

        /* initialisation de la connexion de la base de données */
        Connection conn = Utils.initConnection(serviceManager, ConstantUtils.SECRET_KEY);
        if(conn != null) {
            setConnexion(conn);
        } else {
            BottomPanel.settextLabel("Attention la base données du CBS n'est pas connectée veuillez la paramétrer avant de commencer !!!", Color.RED);
        }
        appliRun = true;
    }

    public static void stopper() {
        BottomPanel.settextLabel("");
        logger.info("le service a été arreté");
    }

    public static void setConnexion(Connection connexion) {
        serviceSalaire.setConn(connexion);
        serviceMandat.setConn(connexion);
        serviceCredit.setConn(connexion);
        serviceSalaireBKMVTI.setConn(connexion);
        serviceRequete.setConn(connexion);
        serviceEvenement.setConn(connexion);
    }

}
