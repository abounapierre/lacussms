//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.abouna.lacussms.main;

import com.abouna.lacussms.config.AppRunConfig;
import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.views.main.BottomPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    public static boolean appliRun = false;
    static boolean vl = false;

    public static void initApp() throws IOException, ClassNotFoundException, SQLException {
        vl = true;
        try {
            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
        } catch (IllegalAccessException | UnsupportedLookAndFeelException | InstantiationException e) {
            logger.error("Error: ", e);
        }
        appliRun = true;
    }

    public static void stopper() {
        AppRunConfig appRunConfig = ApplicationConfig.getApplicationContext().getBean(AppRunConfig.class);
        appRunConfig.setDataServiceEnabled(Boolean.FALSE);
        appRunConfig.setMessageServiceEnabled(Boolean.FALSE);
        BottomPanel.settextLabel("");
        logger.info("le service a été arreté");
    }

    public static void start() {
        AppRunConfig appRunConfig = ApplicationConfig.getApplicationContext().getBean(AppRunConfig.class);
        appRunConfig.setDataServiceEnabled(Boolean.TRUE);
        appRunConfig.setMessageServiceEnabled(Boolean.TRUE);
    }

}
