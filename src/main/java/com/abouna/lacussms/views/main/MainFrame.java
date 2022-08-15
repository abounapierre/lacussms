package com.abouna.lacussms.views.main;

import com.abouna.lacussms.config.ApplicationConfig;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.core.env.Environment;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vincent Douwe <douwevincent@yahoo.fr>
 */
public class MainFrame extends JFrame {
    private Image img;
    private HeaderMenu menu = new HeaderMenu();
    private final BottomPanel bottomPanel = new BottomPanel();

    public MainFrame() throws IOException, XmlPullParserException {
        MainMenuPanel mainMenuPanel = ApplicationConfig.getApplicationContext().getBean(MainMenuPanel.class);
        mainMenuPanel.setContent(new HomePanel());
        Environment env = ApplicationConfig.getApplicationContext().getBean(Environment.class);
        this.setTitle("SMILE SMS BANKING VERSION " + env.getProperty("application.version"));
        this.setJMenuBar(menu);
        this.remove(menu);
        try {
            img = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/smile2.png")));
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
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

    public HeaderMenu getMenu() {
        return menu;
    }

    public void setMenu(HeaderMenu menu) {
        this.menu = menu;
    }

}
