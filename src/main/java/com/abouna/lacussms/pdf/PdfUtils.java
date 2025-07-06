package com.abouna.lacussms.pdf;

import java.time.LocalDate;

public class PdfUtils {
    private static final String EMPTY_STRING = "";

    public static int getPreviousMonth(LocalDate currentDate) {
        int month = currentDate.getMonthValue();
        if (month == 1) {
            return 12; // January becomes December
        } else {
            return month - 1; // Other months just decrement by 1
        }
    }

    public static int getPreviousYear(LocalDate currentDate) {
        int month = currentDate.getMonthValue();
        if (month == 1) {
            return currentDate.getYear() - 1; // January becomes December of the previous year
        } else {
            return currentDate.getYear(); // Other months remain the same year
        }
    }

    public static String getContentClient(String clientName, boolean mask) {
        if (clientName == null || clientName.isEmpty()) {
            return EMPTY_STRING;
        }
        if (mask) {
            return clientName.replace(clientName.substring(2), "**********");
        } else {
            return clientName;
        }
    }

    public static String getClientAccount(String accountNumber, boolean mask) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            return EMPTY_STRING;
        }
        if (mask) {
            return accountNumber.replace(accountNumber.substring(0, 6), "**********");
        } else {
            return accountNumber;
        }
    }

    public static String getContentMessage(String messageContent, String clientName, boolean mask) {
        if (messageContent == null || messageContent.isEmpty()) {
            return EMPTY_STRING;
        }
        if (mask) {
            if (clientName == null || clientName.isEmpty()) {
                return messageContent.replaceAll("[0-9]", "*");
            }
            String[] words = clientName.trim().split(" ");
            for (String word : words) {
                if(word.isEmpty()) {
                    continue; // Skip empty words
                }
                messageContent = messageContent.replace(word, "**********");
            }
        }
        return messageContent;
    }

}
