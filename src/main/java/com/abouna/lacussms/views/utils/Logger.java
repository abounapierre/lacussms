package com.abouna.lacussms.views.utils;

import com.abouna.lacussms.views.main.LogScreenPanel;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

public class Logger {
    private static final String RETURN_LINE = "\n";
    public static void info(String message, Class<?> clazz) {
        LoggerFactory.getLogger(clazz).info(message);
        LogScreenPanel.append(getCurrentTime() + message + RETURN_LINE);
    }

    public static void error(String message, Exception e, Class<?> clazz) {
        LoggerFactory.getLogger(clazz).error(message, e);
        LogScreenPanel.append(getCurrentTime() + message + RETURN_LINE);
    }

    public static void warning(String message, Class<?> clazz) {
        LoggerFactory.getLogger(clazz).warn(message);
        LogScreenPanel.append(getCurrentTime() + message + RETURN_LINE);
    }

    public static void debug(String message, Class<?> clazz) {
        LoggerFactory.getLogger(clazz).debug(message);
        LogScreenPanel.append(getCurrentTime() + message + RETURN_LINE);
    }

    private static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return "[" + dateFormat.format(System.currentTimeMillis()) + "] ";
    }
}
