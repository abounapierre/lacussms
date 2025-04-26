package com.abouna.lacussms.service;

import java.sql.ResultSet;

public class ColUtils {
    public static <T> T getColValue(Object col, Class<T> clazz) {
        if (col == null) {
            throw new IllegalArgumentException("Column value cannot be null");
        }
        return clazz.cast(col);
    }

    public static String getSize(ResultSet rs) {
        try {
            return String.valueOf(rs.getFetchSize());
        } catch (Exception e) {
           return  "Error getting size from ResultSet: " + e.getMessage();
        }
    }
}
