package com.abouna.lacussms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static com.abouna.lacussms.views.tools.ConstantUtils.ROOT_LACUS_PATH;

public class MessageProperties {
    private static final Logger logger = LoggerFactory.getLogger(MessageProperties.class);
    public static Properties getProperties() {
        String fileName = ApplicationConfig.getApplicationContext().getBean("messageConfigPath", String.class);
        String filePath = ROOT_LACUS_PATH + File.separator + fileName;
        try(FileInputStream fis = new FileInputStream(filePath)) {
            Properties properties = new Properties();
            properties.load(fis);
            return properties;
        } catch (IOException e) {
            logger.error("Error loading properties file: {}", filePath, e);
            return null;
        }
    }
}
