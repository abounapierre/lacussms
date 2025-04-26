package com.abouna.lacussms.service;

import java.sql.ResultSet;
import java.util.Objects;

public class ColUtils {
    public static <T> T getColValue(Object col, String colName, Class<T> clazz) {
        return Objects.requireNonNull(clazz.cast(col), String.format("%s ne peut pas être récupéré", colName));
    }

    public static String getSize(ResultSet rs) {
        try {
            return String.valueOf(rs.getFetchSize());
        } catch (Exception e) {
           return  "Error getting size from ResultSet: " + e.getMessage();
        }
    }
}
