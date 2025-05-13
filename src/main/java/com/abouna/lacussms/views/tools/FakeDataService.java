package com.abouna.lacussms.views.tools;

import com.abouna.lacussms.entities.BkAgence;
import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.entities.BkCompCli;
import com.abouna.lacussms.entities.BkEve;
import com.abouna.lacussms.entities.BkOpe;
import com.abouna.lacussms.entities.Groupe;
import com.abouna.lacussms.entities.Message;

import java.util.ArrayList;
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

    public static BkEve getFakeEve(int i) {
        BkEve bkEve = getFakeEve();
        bkEve.setId(i);
        return bkEve;
    }

    public static List<BkCompCli> getAllBkCompClis() {
        BkCli bkCli = new BkCli();
        bkCli.setCode("00012457884");
        bkCli.setLangue("fr");
        bkCli.setEnabled(true);
        bkCli.setNom("Abouna");
        bkCli.setPrenom("Abdou");
        bkCli.setPhone(237694567894L);
        BkCompCli bkCompCli1 = new BkCompCli();
        bkCompCli1.setCli(bkCli);
        bkCompCli1.setNumc("00012457884");
        bkCompCli1.setEnabled(true);

        BkCli bkCli2 = new BkCli();
        bkCli2.setCode("00012457885");
        bkCli2.setLangue("fr");
        bkCli2.setEnabled(true);
        bkCli2.setNom("Abouna2");
        bkCli2.setPrenom("Abdou2");
        bkCli2.setPhone(237694567895L);
        BkCompCli bkCompCli2 = new BkCompCli();
        bkCompCli2.setNumc("00012457885");
        bkCompCli2.setCli(bkCli2);
        bkCompCli2.setEnabled(true);

        return Arrays.asList(bkCompCli1, bkCompCli2);
    }

    public static List<BkCli> getAllBkClis() {
        List<BkCli> bkClis = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            BkCli bkCli = new BkCli();
            bkCli.setCode("0001245788" + i);
            bkCli.setLangue("fr");
            bkCli.setEnabled(true);
            bkCli.setNom("Abouna" + i);
            bkCli.setPrenom("Abdou" + i);
            bkCli.setPhone(237694567894L + i);
            bkClis.add(bkCli);
        }
        return bkClis;
    }

    public static List<Groupe> getAllGroupes() {
        List<Groupe> groupes = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            Groupe groupe = new Groupe();
            groupe.setId(i);
            groupe.setLibelle("Groupe " + i);
            groupes.add(groupe);
        }
        return groupes;
    }

    public static List<BkEve> getAllBkEves() {
        return Arrays.asList(getFakeEve(1), getFakeEve(2), getFakeEve(3), getFakeEve(4), getFakeEve(5),
                getFakeEve(6), getFakeEve(7), getFakeEve(8), getFakeEve(9), getFakeEve(10));
    }
}
