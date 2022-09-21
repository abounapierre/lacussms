package com.abouna.lacussms.views.main;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.main.App;
import com.abouna.lacussms.service.LacusSmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Vincent Douwe <douwevincent@yahoo.fr>
 */
@SpringBootApplication
@ComponentScan({ "com.abouna.lacussms"})
public class MainFrame extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);
    private Image img;
    private final BottomPanel bottomPanel = new BottomPanel();

    public static Thread thread;

    public MainFrame(MainMenuPanel mainMenuPanel, LacusSmsService service, Environment env) throws IOException {
        mainMenuPanel.setContent(new HomePanel());
        this.setTitle("SMILE SMS BANKING VERSION " + env.getProperty("application.version"));
        HeaderMenu menu = new HeaderMenu(service);
        this.setJMenuBar(menu);
        this.remove(menu);
        try {
            img = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/smile2.png")));
        } catch (IOException ex) {
            logger.error("Erreur de chargement de l'image");
        }
        setIconImage(img);
        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(getMainPanel(mainMenuPanel), BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    public static boolean checkLicence(Date d) {
        boolean val = false;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        String licence = format.format(d);
        String s = "24/11/17";
        int a = licence.compareTo(s);
        if (a < 0) {
            val = true;
        }
        return val;
    }

    public void setContent(JPanel panel) {
        this.removeAll();
        this.setLayout(new BorderLayout());
        this.getContentPane().setLayout(new BorderLayout(10, 10));
        this.getContentPane().add(panel, BorderLayout.CENTER);
    }

    public final JPanel getMainPanel(MainMenuPanel mainMenuPanel) throws IOException {
        JPanel mainPanel = new JPanel();
        HeaderPanel headerPanel = new HeaderPanel();
        headerPanel.setBorder(BorderFactory.createEtchedBorder(Color.lightGray, Color.yellow));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(headerPanel, BorderLayout.BEFORE_FIRST_LINE);
        mainPanel.add(BorderLayout.CENTER, mainMenuPanel);
        mainPanel.add(bottomPanel, BorderLayout.AFTER_LAST_LINE);
        mainPanel.validate();
        return mainPanel;
    }


    public static void main(String[] args) {
        thread = new Thread(SplashScreen::new);
        thread.start();
        ApplicationConfig.setApplicationContext(new SpringApplicationBuilder(MainFrame.class).headless(false).run(args));
        try {
            App.initApp();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erreur de démarrage de l'application ");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Erreur de démarrage de l'application fichier introuvable");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erreur de démarrage de l'application problème de connexion à la base de données");
        }
    }

}
