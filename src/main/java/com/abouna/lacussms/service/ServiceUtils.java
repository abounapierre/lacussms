package com.abouna.lacussms.service;

import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.views.main.BottomPanel;
import com.abouna.lacussms.views.tools.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceUtils {
    private static final Logger logger = LoggerFactory.getLogger(ServiceUtils.class);
    public static void mettreAjourNumero(LacusSmsService serviceManager, Connection conn, BkCli bkCli, String cli) throws SQLException {
        String q = "SELECT b.NUM, b.CLI, b.TYP FROM BKTELCLI b WHERE b.CLI='" + cli + "'";
        String msg = "Récupération de la liste des numero disponibles .........";
        logger.info(msg);
        try (PreparedStatement pss = conn.prepareStatement(q)) {
            ResultSet resultat = pss.executeQuery();
            int n = 0;
            while (resultat.next()) {
                if (bkCli.getPhone() == 0) {
                    String code = "";
                    String numero = resultat.getString(1).replace(" ", "").replace("/", "").trim();
                    if (numero.length() == 9 && Utils.estUnEntier(numero.trim())) {
                        code = "237";
                    } else if (numero.length() == 8 && Utils.estUnEntier(numero.trim())) {
                        code = "241";
                    }
                    numero = code + numero;
                    if (bkCli.getPhone() != Long.parseLong(numero)) {
                        bkCli.setPhone(Long.parseLong(numero));
                        if (n == 0) {
                            msg = "Mise à jour numero client.... " + bkCli.getCode();
                            logger.info(msg);
                            BottomPanel.settextLabel(msg, java.awt.Color.BLACK);
                            serviceManager.modifier(bkCli);
                            n++;
                        }
                    }
                }
            }
        }
    }
}
