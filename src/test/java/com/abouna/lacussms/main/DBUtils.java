package com.abouna.lacussms.main;

import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.stream.Collectors;

public class DBUtils {
    public static void initDatabase() throws SQLException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(DBUtils.class.getResource("/database.sql")).openStream()));
        DriverManager.registerDriver(new org.h2.Driver());
        Connection con = getConnection();
        Statement stmt = con.createStatement();
        stmt.executeUpdate(reader.lines().collect(Collectors.joining("\n")));
        System.out.println("Created table in given database...");

        // STEP 4: Clean-up environment
        stmt.close();
        con.close();
        /*ScriptRunner sr = new ScriptRunner(con);

        sr.runScript(reader);*/
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:mem:testDb;DB_CLOSE_DELAY=-1", "sa", "");
    }
}
