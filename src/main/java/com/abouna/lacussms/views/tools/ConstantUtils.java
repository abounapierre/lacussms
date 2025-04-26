/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.views.tools;

import java.nio.file.Paths;

/**
 *
 * @author SATELLITE
 */
public class ConstantUtils {
    private ConstantUtils() {
    }

    public static final String SECRET_KEY = "!LACUS@2017!";
    public static final String DATE_SOLDE = "DATE_ENVOIE_SOLDE";
    public static String LOGO_GENU = "/images/genu-logo.png";
    public static String LOGO = "/images/logo-app.PNG";
    public static String LOGO_1 = "/images/LACUS.PNG";
    public static final String ORACLE_DRIVER_CLASS = "oracle.jdbc.OracleDriver";
    public static final String POSTGRESQL_DRIVER_CLASS = "org.postgresql.Driver";
    public static final String H2_DRIVER_CLASS = "org.h2.Driver";
    public static final String MYSQL_DRIVER_CLASS = "com.mysql.jdbc.Driver";
    public static final int ACCOUNT_NUMBER_LENGTH = 9;
    public static final String ICON_EXCEL = "/images/excel.PNG";
    public static final String ICON_ADD = "/images/Ajouter.png";
    public static final String ICON_UPDATE = "/images/OK.png";
    public static final String ICON_DELETE = "/images/Cancel2.png";
    public static final String [] HEADER = {"code","civilite", "nom", "prenom", "telephone","compte", "langue"};
    public static final String ROOT_LACUS_PATH = Paths.get(".").toAbsolutePath().getParent().getParent().toString();
    public static final String GET_CONNECTION_NULL_ERROR = "Connexion cannot be null";
    public static final String DEFAULT_AGENCE_CODE = "00200";
    public static final int DEFAULT_ACCOUNT_LENGTH = 10;
}
