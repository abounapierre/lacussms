package com.abouna.lacussms.views.main;

import ch.qos.logback.classic.Level;
import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.main.App;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.LoginPanel;
import com.abouna.lacussms.views.tools.ConstantUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

/**
 *
 * @author Vincent Douwe <douwevincent@yahoo.fr>
 */
@SpringBootApplication
@ComponentScan({ "com.abouna.lacussms"})
public class MainFrame extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);
    private final BottomPanel bottomPanel = new BottomPanel();

    public static Thread thread;

    public MainFrame(MainMenuPanel mainMenuPanel, HeaderMenu menu, LacusSmsService service, Environment env) throws IOException {
        mainMenuPanel.setContent(new HomePanel());
        this.setTitle(env.getProperty("application.title"));
        this.setJMenuBar(menu);
        try {
            Image img = ImageIO.read(Objects.requireNonNull(getClass().getResource(ConstantUtils.LOGO_GENU)));
            setIconImage(img);
        } catch (IOException ex) {
            logger.error("Erreur de chargement de l'image");
        }

        setContent(getMainPanel(mainMenuPanel, service));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    public void setContent(JPanel panel) {
        Container pane = this.getContentPane();
        pane.removeAll();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
        //pane.setLayout(new BorderLayout(10, 10));
        pane.add(panel);
        pane.validate();
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
        logger.info("##### main ####");
        thread = new Thread(SplashScreen::new);
        thread.start();
        logger.info("##### configuration de l'application  ####");
        try {
            ApplicationConfig.setApplicationContext(new SpringApplicationBuilder(MainFrame.class).headless(false).run(args));
            if(args.length > 0) {
                logger.info("###### loglevel {} ######", args[0]);
                setLogLevel(args[0]);
            }
            App.initApp();
        } catch (IOException e) {
            logger.error("Main: Erreur I/O: ",e);
            JOptionPane.showMessageDialog(null, "Erreur de démarrage de l'application, l'application va s'arrêter");
            System.exit(0);
        } catch (ClassNotFoundException e) {
            logger.error("main: Erreur Classe non trouvée: ",e);
            JOptionPane.showMessageDialog(null, "Erreur de démarrage de l'application fichier introuvable, l'application va s'arrêter");
            System.exit(0);
        } catch (SQLException e) {
            logger.error("Man: Erreur problème SQL: ",e);
            JOptionPane.showMessageDialog(null, "Erreur de démarrage de l'application problème de connexion à la base de données, l'application va s'arrêter");
            System.exit(0);
        } catch (BeanInstantiationException e) {
            logger.error("Man: Erreur problème SQL: ",e);
            JOptionPane.showMessageDialog(null, "Erreur de démarrage de l'application");
            System.exit(0);
        }
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(MainFrame.this, message);
    }

    public static void setLogLevel(String logLevel) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        if(logLevel.equalsIgnoreCase("info")) {
            root.setLevel(Level.INFO);
        } else if(logLevel.equalsIgnoreCase("debug")) {
            root.setLevel(Level.DEBUG);
        } else if(logLevel.equalsIgnoreCase("trace")) {
            root.setLevel(Level.TRACE);
        } else if(logLevel.equalsIgnoreCase("error")) {
            root.setLevel(Level.ERROR);
        }
    }

}
