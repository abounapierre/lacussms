package com.abouna.lacussms.pdf;

import java.time.LocalDate;

public class DateUtils {

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

}
