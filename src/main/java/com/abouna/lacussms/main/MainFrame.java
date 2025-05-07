package com.abouna.lacussms.main;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.task.StartService;
import com.abouna.lacussms.views.main.BottomPanel;
import com.abouna.lacussms.views.main.HeaderMenu;
import com.abouna.lacussms.views.main.HeaderPanel;
import com.abouna.lacussms.views.main.HomePanel;
import com.abouna.lacussms.views.main.MainMenuPanel;
import com.abouna.lacussms.views.main.SplashScreen;
import com.abouna.lacussms.views.tools.ConstantUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author abouna
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan({ "com.abouna.lacussms"})
public class MainFrame extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);
    private final BottomPanel bottomPanel;
    public static boolean appliRun = false;
    public static Thread thread;

    public MainFrame(MainMenuPanel mainMenuPanel, LacusSmsService service, Environment env) throws IOException {
        this.bottomPanel = new BottomPanel(env.getProperty("application.signature.text"));
        mainMenuPanel.setContent(new HomePanel());
        this.setTitle("LACUS SMS " + env.getProperty("application.version"));
        HeaderMenu menu = new HeaderMenu(service);
        this.setJMenuBar(menu);
        this.remove(menu);
        Image logo = ImageIO.read(Objects.requireNonNull(getClass().getResource(ConstantUtils.LOGO_GENU)));
        setIconImage(logo);
        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(getMainPanel(mainMenuPanel, service), BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    public static MainFrame getInstance() {
        return ApplicationConfig.getApplicationContext().getBean(MainFrame.class);
    }

    public void setContent(JPanel panel) {
        this.removeAll();
        this.setLayout(new BorderLayout());
        this.getContentPane().setLayout(new BorderLayout(10, 10));
        this.getContentPane().add(panel, BorderLayout.CENTER);
    }

    public final JPanel getMainPanel(MainMenuPanel mainMenuPanel, LacusSmsService service) throws IOException {
        JPanel mainPanel = new JPanel();
        HeaderPanel headerPanel = new HeaderPanel(service);
        headerPanel.setBorder(BorderFactory.createEtchedBorder(Color.lightGray, Color.yellow));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(headerPanel, BorderLayout.BEFORE_FIRST_LINE);
        mainPanel.add(BorderLayout.CENTER, mainMenuPanel);
        mainPanel.add(bottomPanel, BorderLayout.AFTER_LAST_LINE);
        mainPanel.validate();
        return mainPanel;
    }


    public static void main(String[] args) {
        try {
            logger.info("##### input args: {} ######", Arrays.toString(args));
            thread = new Thread(SplashScreen::new);
            thread.start();
            ApplicationConfig.setApplicationContext(new SpringApplicationBuilder(MainFrame.class).headless(false).run(args));
            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
            appliRun = true;
            StartService.startLicence();
        } catch (Exception e) {
            logError(e);
        }
    }

    private static void logError(Exception e) {
        String message = "Erreur de démarrage de l'application, plus de details dans le fichier de log";
        logger.error(message, e);
        JOptionPane.showMessageDialog(null, message);
        thread.interrupt();
        SplashScreen.execute.dispose();
        System.exit(0);
    }

}
