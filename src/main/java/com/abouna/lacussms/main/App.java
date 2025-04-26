//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.abouna.lacussms.main;

import com.abouna.lacussms.task.StartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    public static boolean appliRun = false;

    public static void initApp() throws IOException, ClassNotFoundException, SQLException {
        try {
            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
        } catch (IllegalAccessException | UnsupportedLookAndFeelException | InstantiationException e) {
            logger.error("Error: ", e);
        }
        appliRun = true;
        StartService.startLicence();
    }

}
