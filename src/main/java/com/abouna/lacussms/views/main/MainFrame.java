package com.abouna.lacussms.views.main;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.views.tools.Utils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 *
 * @author Vincent Douwe <douwevincent@yahoo.fr>
 */
public class MainFrame extends JFrame {
    private HeaderPanel headerPanel;
    private final JPanel contentPanel;
    private Image img;
    private HeaderMenu menu = new HeaderMenu();
    private final BottomPanel bottomPanel = new BottomPanel();
    private final MainMenuPanel parent;

    public MainFrame() throws IOException, XmlPullParserException {
        parent = ApplicationConfig.getApplicationContext().getBean(MainMenuPanel.class);
        contentPanel = new JPanel();
        this.setTitle("SMILE SMS BANKING VERSION " + Utils.getAppVersion());
        this.setJMenuBar(menu);
        this.remove(menu);
        try {
            img = ImageIO.read(getClass().getResource("/images/smile2.png"));
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        setIconImage(img);
        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(getMainPanel(contentPanel), BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    public static boolean checkLicence(Date d) {
        boolean val = false;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        String licence = format.format(d);
        String s = "24/11/17";
        int a = licence.compareTo(s);
        if (a == -1) {
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

    public final JPanel getMainPanel(JPanel panel) throws IOException {
        headerPanel = new HeaderPanel();
        headerPanel.setBorder(BorderFactory.createEtchedBorder(Color.lightGray, Color.yellow));
        panel.setLayout(new BorderLayout());
        parent.setContenu(new EmptyPanel());
        panel.add(headerPanel, BorderLayout.BEFORE_FIRST_LINE);
        panel.add(BorderLayout.CENTER, parent);
        panel.add(bottomPanel, BorderLayout.AFTER_LAST_LINE);
        panel.validate();
        return panel;
    }

    public HeaderMenu getMenu() {
        return menu;
    }

    public void setMenu(HeaderMenu menu) {
        this.menu = menu;
    }

}
