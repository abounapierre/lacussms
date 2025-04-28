package com.abouna.lacussms.config;

import com.abouna.lacussms.task.StartService;
import com.abouna.lacussms.views.MacKeyPanel;
import com.abouna.lacussms.views.tools.Utils;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import static com.abouna.lacussms.views.tools.ConstantUtils.ROOT_LACUS_PATH;

@Component
public class LicenceConfig {
    private static final Logger logger = LoggerFactory.getLogger(LicenceConfig.class);
    private final Environment env;
    private final StringEncryptor encryptor;
    private static final String EMPTY = "";


    public LicenceConfig(@Qualifier("jasyptStringEncryptor") StringEncryptor encryptor, Environment env) {
        this.encryptor = encryptor;
        this.env = env;
    }


    public void controlLicence() {
        String message = "La date de validité de la licence est dépassée ou licence non valide. \n" +
                "Veuillez contacter le support technique pour renouveler votre licence.";
        try {
            logger.info("service licence is running");
            String original = encryptor.decrypt(getKey());
            String[] parts = original.split(":");
            if(parts.length != 2) {
                showKeyPanel();
            }
            if(!verifyMacAddress(parts[1])) {
                message = "L'adresse MAC de la machine ne correspond pas à celle de la licence.\n" +
                        "Veuillez contacter le support technique pour renouveler votre licence.";
                showKeyPanel();
            }
            Date date = Utils.getDateSimpleFormat("ddMMyyHHmmss", parts[0]);
            if(date != null && date.before(Utils.getTimeFromInternet())) {
                close(message, null);
            }
        } catch (Exception e) {
            close(message, e);
        }
    }

    private void showKeyPanel() {
        MacKeyPanel.init(getMacAddress());
        System.exit(0);
    }

    private static void close(String message, Exception e) {
        StartService.running = false;
        logger.error(message, e);
        JOptionPane.showMessageDialog(null, message);
        System.exit(0);
    }

    private String getKey() {
        String keyPath = env.getProperty("application.key.path");
        try {
            String filePath = ROOT_LACUS_PATH + File.separator + keyPath;
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String key = reader.lines().collect(Collectors.joining("\n"));
            String temp = new String(Base64.getDecoder().decode(key));
            temp = temp.replace(temp.substring(0, 1000), EMPTY);
            int length = temp.length();
            temp = temp.replace(temp.substring(length - 1000, length), EMPTY);
            return temp;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String getMacAddress() {
        try {
            List<String> macs = new ArrayList<>();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while(true) {
                byte[] hardwareAddress;
                do {
                    if (!networkInterfaces.hasMoreElements()) {
                        String[] myArray = new String[macs.size()];
                        return String.join(EMPTY, macs.toArray(myArray));
                    }
                    NetworkInterface ni = networkInterfaces.nextElement();
                    hardwareAddress = ni.getHardwareAddress();
                } while(hardwareAddress == null);

                String[] hexadecimalFormat = new String[hardwareAddress.length];

                for(int i = 0; i < hardwareAddress.length; ++i) {
                    hexadecimalFormat[i] = String.format("%02X", hardwareAddress[i]);
                }
                String mac = String.join(EMPTY, hexadecimalFormat);
                macs.add(mac);
            }
        } catch (SocketException exception) {
            logger.error("Erreur lors de la récupération de l'adresse MAC: {}", exception.getMessage());
            return null;
        }
    }

    private boolean verifyMacAddress(String mac) {
        String macAddress = getMacAddress();
        return !StringUtils.isEmpty(macAddress) && macAddress.equals(mac);
    }
}
