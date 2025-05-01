package com.abouna.lacussms.views.tools;

import com.abouna.lacussms.entities.BkAgence;
import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.entities.BkEve;
import com.abouna.lacussms.entities.BkOpe;
import com.abouna.lacussms.entities.Message;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FakeDataService {

    public static List<Message> getFakeMessages() {
        return Arrays.asList(new Message(1,"Titre 1", "Message 1", new Date(), "237694567894"),
                new Message(2,"Titre 2", "Message 1", new Date(), "237694567894"),
                new Message(3,"Titre 3", "Message 1", new Date(), "237694567894"),
                new Message(4,"Titre ", "Message 1", new Date(), "237694567894"),
                new Message(5,"Titre 1", "Message 1", new Date(), "237694567894"),
                new Message(6,"Titre 1", "Message 1", new Date(), "237694567894"));
    }

    public static BkEve getFakeEve() {
        BkEve bkEve = new BkEve();
        bkEve.setId(1);
        bkEve.setSent(true);
        BkCli bkCli = new BkCli();
        bkCli.setCode("00012457884");
        bkCli.setLangue("fr");
        bkCli.setEnabled(true);
        bkCli.setNom("Abouna");
        bkCli.setPrenom("Abdou");
        bkCli.setPhone(237694567894L);
        bkEve.setCli(bkCli);
        bkEve.setCompte("00012457884");
        bkEve.setHsai("00:00:00.000");
        bkEve.setMont(1000.0);
        bkEve.setNumEve("4124574");
        bkEve.setEventDate(new Date());
        bkEve.setEtat("VA");
        BkOpe bkOpe = new BkOpe();
        bkOpe.setOpe("VA");
        bkOpe.setLib("Retrait");
        bkEve.setOpe(bkOpe);
        BkAgence bkAgence = new BkAgence();
        bkAgence.setNuma("0012");
        bkAgence.setNoma("Yaoundé");
        bkEve.setBkAgence(bkAgence);
        return bkEve;
    }
}
