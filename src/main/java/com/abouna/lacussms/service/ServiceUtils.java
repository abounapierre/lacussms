package com.abouna.lacussms.service;

import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.views.main.BottomPanel;
import com.abouna.lacussms.views.tools.Utils;
import com.abouna.lacussms.views.utils.Logger;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ServiceUtils {
    public static void mettreAjourNumero(LacusSmsService serviceManager, Connection conn, BkCli bkCli, String cli) {
        String q = "SELECT b.NUM, b.CLI, b.TYP FROM BKTELCLI b WHERE b.CLI='" + cli + "'";
        String msg = "Récupération de la liste des numéros disponibles .........";
        Logger.info(msg, ServiceUtils.class);
        try (PreparedStatement pss = conn.prepareStatement(q)) {
            ResultSet resultat = pss.executeQuery();
            Logger.info(String.format("nombre de lignes trouvées: %s", resultat.getFetchSize()), ServiceUtils.class);
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
                            Logger.info(msg, ServiceUtils.class);
                            BottomPanel.settextLabel(msg, java.awt.Color.BLACK);
                            serviceManager.modifier(bkCli);
                            n++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            String errorMessage = "Erreur lors de la mise à jour du numero client";
            Logger.error(String.format("%s: %s", errorMessage, e.getMessage()), e, ServiceUtils.class);
            BottomPanel.settextLabel(errorMessage, Color.RED);
        }
    }
}
