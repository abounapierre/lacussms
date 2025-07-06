package com.abouna.lacussms.config;

import com.abouna.lacussms.entities.BkAgence;
import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.entities.BkCompCli;
import com.abouna.lacussms.entities.BkEve;
import com.abouna.lacussms.entities.BkMad;
import com.abouna.lacussms.entities.BkOpe;
import com.abouna.lacussms.entities.MessageFormat;
import com.abouna.lacussms.entities.TypeEvent;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.utils.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DataServiceConfig implements ApplicationListener<ApplicationReadyEvent> {
    private final LacusSmsService lacusSmsService;
    private List<BkAgence> agences;
    private List<BkCli> clients;
    private List<BkCompCli> comptesClients;
    private List<BkEve> evenements;
    private List<BkMad> mandats;
    private List<BkOpe> operations;
    private final Set<String> codes = new HashSet<>();
    private final Set<String> numEves = new HashSet<>();
    private final Set<String> mandatNumEves = new HashSet<>();
    private final Set<String> comptes = new HashSet<>();
    private List<MessageFormat> messageFormats;
    private final Set<String> clientCodes = new HashSet<>();
    private final Environment env;

    public DataServiceConfig(LacusSmsService lacusSmsService, Environment env) {
        this.lacusSmsService = lacusSmsService;
        this.env = env;
    }

    public List<BkAgence> getAgences() {
        return agences;
    }

    public void setAgences(List<BkAgence> agences) {
        this.agences = agences;
    }

    public List<BkCli> getClients() {
        return clients;
    }

    public void setClients(List<BkCli> clients) {
        this.clients = clients;
    }

    public List<BkCompCli> getComptesClients() {
        return comptesClients;
    }

    public void setComptesClients(List<BkCompCli> comptesClients) {
        this.comptesClients = comptesClients;
    }

    public List<BkEve> getEvenements() {
        return evenements;
    }

    public void setEvenements(List<BkEve> evenements) {
        this.evenements = evenements;
    }

    public List<BkMad> getMandats() {
        return mandats;
    }

    public void setMandats(List<BkMad> mandats) {
        this.mandats = mandats;
    }

    public List<BkOpe> getOperations() {
        return operations;
    }

    public void setOperations(List<BkOpe> operations) {
        this.operations = operations;
    }

    public Set<String> getCodes() {
        return codes;
    }

    public Set<String> getComptes() {
        return comptes;
    }

    public Set<String> getNumEves() {
        return numEves;
    }

    public List<MessageFormat> getMessageFormats() {
        return messageFormats;
    }

    public void setMessageFormats(List<MessageFormat> messageFormats) {
        this.messageFormats = messageFormats;
    }

    private void generateData() {
        agences  = saveAndGetAgences();
        clients = saveAndGetClients();
        operations = saveAndOperations();
        //comptesClients = saveAndComptesClients();
        evenements = saveAndEvenements();
        messageFormats = saveAndMessageFormats();
        mandats = saveAndMandats();
    }

    private List<BkMad> saveAndMandats() {
        List<BkMad> madList = lacusSmsService.getBkMadByDate(new Date());
        System.out.println("Madats found: " + madList.size());
        if (madList.isEmpty()) {
            madList = lacusSmsService.getAllBkMads();
            System.out.println("Madats found in all: " + madList.size());
            int i = 0;
            for (BkMad bkMad : madList) {
                System.out.println("Madat: " + bkMad.getId() + ", Sent: " + bkMad.isSent() + ", Date creation: " + bkMad.getCreationDate());
                i++;
                if(i == 100) {
                    break; // Limit output to 100 records for performance
                }
            }
        } /*else {
            return madList;
        }
        if (madList.isEmpty()) {
            madList = generateMandats();
            madList.forEach(lacusSmsService::enregistrer);
        } else {
            madList.forEach(mad -> {
                mad.setSent(false);
                mad.setDateEnvoie(new Date());
                mad.setCreationDate(new Date());
                mad.setDco(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                mad.setEve(getRandomMandatNumEve());
                mad.setId(mad.getId() == null ? 0 : mad.getId());
                lacusSmsService.modifier(mad);
            });
        }*/
        return madList;
    }

    private BkOpe getRandomOperation(String number) {
        return operations.stream().filter(op -> op.getOpe().equals(number))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Operation with number " + number + " not found"));
    }

    private List<BkCompCli> saveAndComptesClients() {
        List<BkCompCli> compList = lacusSmsService.getAllBkCompClis();
        if (compList.isEmpty()) {
            compList = generateComptesClients();
            compList.forEach(lacusSmsService::enregistrer);
        }
        return compList;
    }

    private List<BkEve> saveAndEvenements() {
        List<BkEve> eveList = lacusSmsService.getBkEveByDate(new Date(), Collections.emptyList());
        if (eveList.isEmpty()) {
            eveList = lacusSmsService.getAllBkEves();
        } else {
            return eveList;
        }
        if (eveList.isEmpty()) {
            eveList = generateEvenements();
            eveList.forEach(lacusSmsService::enregistrer);
        } else {
            eveList.forEach(eve -> {
                eve.setSent(false);
                eve.setEventDate(new Date());
                eve.setDVAB(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                lacusSmsService.modifier(eve);
            });
        }
        return eveList;
    }

    private List<BkOpe> saveAndOperations() {
        List<BkOpe> opeList = lacusSmsService.getAllBkOpes();
        if (opeList.isEmpty()) {
            opeList = generateOperations();
            opeList.forEach(lacusSmsService::enregistrer);
        }
        return opeList;
    }

    private List<BkAgence> saveAndGetAgences() {
        List<BkAgence> agences = lacusSmsService.getAllBkAgences();
        if (agences.isEmpty()) {
            agences = generateAgences();
            agences.forEach(lacusSmsService::enregistrer);
        }
        return agences;
    }

    private List<BkCli> saveAndGetClients() {
        List<BkCli> clis = lacusSmsService.getAllCli();
        comptesClients = lacusSmsService.getAllBkCompClis();
        if (clis.isEmpty()) {
            clis = generateClients();
            clis.forEach(bkCli -> {
                if (bkCli.getCode() == null || bkCli.getCode().isEmpty()) {
                    bkCli.setCode(getRandomCode());
                }
                if (bkCli.getLibelle() == null || bkCli.getLibelle().isEmpty()) {
                    bkCli.setLibelle(getRandomLibelle());
                }
                if (bkCli.getPrenom() == null || bkCli.getPrenom().isEmpty()) {
                    bkCli.setPrenom(getRandomPrenom());
                }
                if (bkCli.getLangue() == null || bkCli.getLangue().isEmpty()) {
                    bkCli.setLangue(getRandomLangue());
                }
                String compte = getRandomCompteCode();
                bkCli.setCode(compte.substring(3, 9));
                BkCli bkCli1 = lacusSmsService.enregistrer(bkCli);
                clientCodes.add(compte.substring(3, 9));
                BkCompCli bkCompCli = new BkCompCli(compte, bkCli1, true);
                comptesClients.add(lacusSmsService.enregistrer(bkCompCli));
            });
        }
        return clis;
    }


    private List<BkEve> generateEvenements() {
        List<BkEve> bkEves = new ArrayList<>();
        List<BkOpe> opeList = operations.stream().filter(ope -> !"10003".equals(ope.getOpe())).collect(Collectors.toList());
        for (int i = 1; i <= 2045; i++) {
            BkEve bkEve = new BkEve();
            bkEve.setId(i);
            bkEve.setSent(false);
            BkCli bkCli = getRandomClient();
            bkEve.setCli(bkCli);
            bkEve.setCompte(getCompteClient(bkCli.getCode()));
            bkEve.setHsai(getRandomHours());
            bkEve.setMont(getRandomAmount());
            bkEve.setNumEve(getRandomNumEve());
            bkEve.setDVAB(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            bkEve.setEventDate(new Date());
            bkEve.setEtat(Arrays.asList("VA","AT","FO","VF","IG","IF","AB","AN","TR").get((int) (Math.random() * 9)));
            BkOpe bkOpe = opeList.get((int) (Math.random() * opeList.size()));
            bkEve.setOpe(bkOpe);
            BkAgence bkAgence = agences.get((int) (Math.random() * operations.size()));
            bkEve.setBkAgence(bkAgence);
            bkEve.setType(getRandomTypeEvent());
            bkEve.setCreationDate(new Date());
            bkEves.add(bkEve);
        }
        return bkEves;
    }

    private TypeEvent getRandomTypeEvent() {
        TypeEvent[] types = TypeEvent.values();
        return types[(int) (Math.random() * types.length)];
    }

    private List<BkMad> generateMandats() {
        List<BkMad> bkEves = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            BkMad bkMad = new BkMad();
            bkMad.setId(i);
            bkMad.setSent(false);
            BkAgence bkAgence = agences.get((int) (Math.random() * operations.size()));
            bkMad.setAge(bkAgence);
            bkMad.setMnt(String.valueOf(getRandomAmount()));
            BkOpe bkOpe = operations.stream().filter(ope -> "10003".equals(ope.getOpe())).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No operation found for mandat"));
            bkMad.setOpe(bkOpe);
            bkMad.setDco(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            bkMad.setDateEnvoie(new Date());
            bkMad.setCreationDate(new Date());
            bkMad.setAd1p(i% 15 == 0 ? "Rue de la République" : null);
            bkMad.setAd2p(i% 15 == 0 ? "Rue de la République" : null);
            bkMad.setSent(false);
            bkMad.setEve(getRandomNumEve());
            bkMad.setClesec(getRandomCode());
            bkMad.setId(i);
            bkMad.setTraite(Arrays.asList(1, 0).get((int) (Math.random() * 2)));
            bkMad.setCtr(Arrays.asList("0", "9").get((int) (Math.random() * 2)));
            bkEves.add(bkMad);
        }
        return bkEves;
    }

    private String getCompteClient(String code) {
        return comptesClients.stream()
                .filter(compte -> code.equals(compte.getCli().getCode()))
                .findFirst()
                .map(BkCompCli::getNumc)
                .orElseThrow(() -> new IllegalArgumentException("Compte client with code " + code + " not found"));
    }

    private String getRandomNumEve() {
        long min = 100L;
        long max = 9999999999L;
        String code = String.valueOf(min + (long) (Math.random() * (max - min + 1)));
        while (numEves.contains(code)) {
            code = String.valueOf(min + (long) (Math.random() * (max - min + 1)));
        }
        numEves.add(code);
        return code;
    }

    private String getRandomMandatNumEve() {
        long min = 100L;
        long max = 9999999999L;
        String code = String.valueOf(min + (long) (Math.random() * (max - min + 1)));
        while (numEves.contains(code)) {
            code = String.valueOf(min + (long) (Math.random() * (max - min + 1)));
        }
        numEves.add(code);
        return code;
    }

    private double getRandomAmount() {
        double min = 1000.0;
        double max = 100000.0;
        return min + (Math.random() * (max - min));
    }

    private BkCli getRandomClient() {
        int randomIndex = (int) (Math.random() * clients.size());
        return clients.get(randomIndex);
    }

    private BkCompCli getRandomCompteClient() {
        int randomIndex = (int) (Math.random() * comptesClients.size());
        return comptesClients.get(randomIndex);
    }

    private List<BkCompCli> generateComptesClients() {
        List<BkCompCli> bkCompClis = new ArrayList<>();
        for (int i = 1; i <= 1201; i++) {
            String code = getRandomCompteCode();
            BkCli client = getRandomClient();
            BkCompCli compteClient = new BkCompCli(code, client, true);
            bkCompClis.add(compteClient);
        }
        return bkCompClis;
    }

    private String getRandomHours() {
        int hours = (int) (Math.random() * 24);
        int minutes = (int) (Math.random() * 60);
        int seconds = (int) (Math.random() * 60);
        return String.format("%02d:%02d:%02d.000", hours, minutes, seconds);
    }

    private String getRandomCompteCode() {
        long min = 1000000000L;
        long max = 9999999999L;
        String code = String.valueOf(min + (long) (Math.random() * (max - min + 1)));
        while (comptes.contains(code) || clientCodes.contains(code.substring(3, 9))) {
            code = String.valueOf(min + (long) (Math.random() * (max - min + 1)));
        }
        return code;
    }

    private List<BkCli> generateClients() {
        List<BkCli> bkClis = new ArrayList<>();
        for (int i = 1; i <= 733; i++) {
            String code = getRandomCode();
            String nom = getRandomNom();
            String libelle = getRandomLibelle();
            String prenom = "Mr".equals(libelle) ? getRandomPrenom() : getRandomFemininPrenom();
            long phone = getRandomPhone();
            String email = getRandomEmail();
            String langue = getRandomLangue();
            String ddn = getRandomDdn();
            BkCli client = new BkCli(code, nom, prenom, libelle, phone, email, true, langue, ddn);
            bkClis.add(client);
        }
        return bkClis;
    }

    private List<BkOpe> generateOperations() {
        return Arrays.asList(
                new BkOpe("10001", "CREDIT"),
                new BkOpe("10002", "DEBIT"),
                new BkOpe("10003", "MANDAT"),
                new BkOpe("10004", "VIREMENT"),
                new BkOpe("10005", "RETRAIT"));
    }

    private List<MessageFormat> saveAndMessageFormats() {
        List<MessageFormat> messageFormats = lacusSmsService.getAll();
        if (messageFormats.isEmpty()) {
            messageFormats = Arrays.asList(
                    new MessageFormat("CREDIT", "<lib> <nom> <pre>, <agence> vous informe que votre compte <numc> a été crédité de <mont> FCFA le <date> à <heure>.", getRandomOperation("10001"), "fr"),
                    new MessageFormat("DEBIT", "<lib> <nom> <pre>, <agence> vous informe que votre compte <numc> a été débité de <mont> FCFA le <date> à <heure>.", getRandomOperation("10002"), "fr"),
                    new MessageFormat("MANDAT", "<lib> <nom> <pre>, <agence> vous informe qu'un mandat de <mont> FCFA a été créé pour votre compte <numc> le <date> à <heure>.", getRandomOperation("10003"), "fr"),
                    new MessageFormat("VIREMENT", "<lib> <nom> <pre>, <agence> vous informe qu'un virement de <mont> FCFA a été effectué depuis votre compte <numc> le <date> à <heure>.", getRandomOperation("10004"), "fr"),
                    new MessageFormat("RETRAIT", "<lib> <nom> <pre>, <agence> vous informe qu'un retrait de <mont> FCFA a été effectué sur votre compte <numc> le <date> à <heure>.", getRandomOperation("10005"), "fr"),
                    new MessageFormat("CREDIT", "<lib> <nom> <pre>, <agence> vous informe que votre compte <numc> a été crédité de <mont> FCFA le <date> à <heure>.", getRandomOperation("10001"), "en"),
                    new MessageFormat("DEBIT", "<lib> <nom> <pre>, <agence> vous informe que votre compte <numc> a été débité de <mont> FCFA le <date> à <heure>.", getRandomOperation("10002"), "en"),
                    new MessageFormat("MANDAT", "<lib> <nom> <pre>, <agence> vous informe qu'un mandat de <mont> FCFA a été créé pour votre compte <numc> le <date1> à <heure>.", getRandomOperation("10003"), "en"),
                    new MessageFormat("VIREMENT", "<lib> <nom> <pre>, <agence> vous informe qu'un virement de <mont> FCFA a été effectué depuis votre compte <numc> le <date> à <heure>.", getRandomOperation("10004"), "en"),
                    new MessageFormat("RETRAIT", "<lib> <nom> <pre>, <agence> vous informe qu'un retrait de <mont> FCFA a été effectué sur votre compte <numc> le <date> à <heure>.", getRandomOperation("10005"), "en")
            );
            messageFormats.forEach(lacusSmsService::enregistrer);
        }
        return messageFormats;
    }

    private String getRandomDdn() {
        int year = 1970 + (int) (Math.random() * 30); // Random year between 1970 and 2000
        int month = 1 + (int) (Math.random() * 12); // Random month between 1 and 12
        int day = 1 + (int) (Math.random() * 28); // Random day between 1 and 28 to avoid month-end issues
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    private String getRandomLangue() {
        String[] langues = {"fr", "en"};
        return langues[(int) (Math.random() * langues.length)];
    }

    private String getRandomEmail() {
        String[] domains = {"gmail.com", "yahoo.com", "demo.com", "sample.com"};
        String[] prefixes = {"user", "client", "contact", "info", "support"};
        String prefix = prefixes[(int) (Math.random() * prefixes.length)];
        String domain = domains[(int) (Math.random() * domains.length)];
        return prefix + Math.random() + "@" + domain;
    }

    private long getRandomPhone() {
        long min = 237650000000L;
        long max = 237699999999L;
        return min + (long) (Math.random() * (max - min + 1));
    }

    private String getRandomLibelle() {
        String[] libelles = {"Mr", "Mme", "Mlle"};
        return libelles[(int) (Math.random() * libelles.length)];
    }

    private String getRandomPrenom() {
        String[] prenoms = {"Jean", "Marie", "Pierre", "Paul", "Jacques", "Luc", "Michel", "Claude", "Henri", "François", "André", "Bernard", "Robert", "Daniel", "Philippe", "Alain", "Dominique", "Nicolas", "Antoine", "Julien",
        "Sébastien", "Vincent", "Mathieu", "Laurent", "Christophe", "David", "Thomas", "Olivier", "Eric", "Patrick", "Gilles", "Yves", "Georges", "Jacques", "Bernard", "Maurice", "René", "Roger", "Alfred",
        "Gaston", "Henri", "Marcel", "Fernand", "Louis", "Charles", "Edmond", "Émile", "Armand", "Albert", "Léon", "Lucien", "Édouard", "Jules", "Victor", "Aimé", "Célestin", "Gustave", "Benoît"};
        return prenoms[(int) (Math.random() * prenoms.length)];
    }

    private String getRandomFemininPrenom() {
        String[] prenoms = {"Marie", "Sophie", "Julie", "Claire", "Céline", "Isabelle", "Nathalie", "Sandrine", "Caroline", "Valérie", "Emilie", "Aline", "Christine", "Monique", "Martine", "Hélène", "Sylvie",
        "Patricia", "Dominique", "Nicole", "Brigitte", "Anne", "Catherine", "Françoise", "Geneviève", "Jacqueline", "Michèle", "Chantal", "Colette", "Josiane", "Yvette", "Liliane", "Danielle", "Martine", "Béatrice", "Catherine", "Nadine", "Véronique", "Muriel", "Sylviane", "Monique",
        "Catherine", "Nathalie", "Christine", "Isabelle", "Sophie", "Julie", "Claire", "Emilie", "Aline", "Caroline", "Valérie", "Sandrine", "Hélène", "Patricia", "Dominique", "Nicole"};
        return prenoms[(int) (Math.random() * prenoms.length)];
    }

    private String getRandomNom() {
        String[] noms = {"ABOUNA", "ASSAM", "NDONG", "PASSANG", "NGAM", "MENDOUA", "SIMEN", "TSOBENG", "EWANDJE", "Leroy", "Moreau", "Simon", "Laurent", "Michel", "Garcia", "David", "Bertrand", "Roux", "Vincent", "Fournier", "Girard", "Andre", "Lefebvre", "Mercier", "Dupuis", "Blanc", "Garnier", "Faure", "Chevalier", "Martinez", "Legrand", "Gauthier", "Rousseau", "Nicolas", "Perrin", "Mathieu", "Clement", "Lemoine", "Barbier",
        "Benoit", "Leclerc", "Garnier", "Lemoine", "Renaud", "Boucher", "Colin", "Leclercq", "Poirier", "Garnier", "Leroy", "Morel", "Simon", "Laurent", "Michel", "Garcia", "David", "Bertrand", "Roux",
        "Vincent", "Fournier", "Girard", "Andre", "Lefebvre", "Mercier", "Dupuis", "Blanc", "Garnier", "Faure", "Chevalier", "Martinez", "Legrand", "Gauthier", "Rousseau", "Nicolas", "Perrin", "Mathieu", "Clement",
        "MBAZOA", "MVONDO AWONO", "AWONO", "NGAH", "DASSI", "ELE", "ABBE", "BILOA", "LEBOMO", "MENYE", "TCHASSI", "NTCHAM", "MBOU"};
        return noms[(int) (Math.random() * noms.length)];
    }

    private String getRandomCode() {
        String code = "C" + String.format("%05d", (int) (Math.random() * 100000));
        while (codes.contains(code)) {
            code = "C" + String.format("%05d", (int) (Math.random() * 100000));
        }
        codes.add(code);
        return code;
    }

    private List<BkAgence> generateAgences() {
        return Arrays.asList(
            new BkAgence("10001", "Agence de Yaoundé 1", "Yaoundé Etoudi"),
            new BkAgence("10002", "Agence de Douala", "Douala Bonanjo"),
            new BkAgence("10003", "Agence de Yaoundé 2", "Yaoundé Bastos"),
            new BkAgence("10004", "Agence de Maroua", "Maroua pont vert"),
            new BkAgence("10005", "Agence de Ngaoundere 1", "Ngaoundere université"),
            new BkAgence("10006", "Agence d'Obala", "Obala Nkolbikok"),
            new BkAgence("10007", "Agence de Sa'a", "Sa'a ville"),
            new BkAgence("10008", "Agence de Bertoua", "Bertoua Yademe"),
            new BkAgence("10009", "Agence de Monatele", "Monatele centre"),
            new BkAgence("10010", "Agence de Soa", "Soa université"),
            new BkAgence("10011", "Agence de Garoua", "Garoua runde adja"),
            new BkAgence("10012", "Agence de Maroua", "Maroua Domayo"),
            new BkAgence("10013", "Agence de Ngaoundere 2", "Ngaoundere ville"),
            new BkAgence("10014", "Agence de Foumban", "Foumban ville"),
            new BkAgence("10015", "Agence de Buea", "Buea centre"),
            new BkAgence("10016", "Agence de Yaoundé 3", "Yaoundé Nkolbisson"),
            new BkAgence("10017", "Agence de Douala", "Douala Bonaberi"),
            new BkAgence("10018", "Agence de Bamenda", "Bamenda centre"),
            new BkAgence("10019", "Agence de Bafoussam", "Bafoussam centre"),
            new BkAgence("10020", "Agence de Bertoua", "Bertoua Nkolbikon")
        );
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String[] profiles = env.getActiveProfiles();
        boolean verify = Stream.of(profiles).anyMatch(prof -> prof.equals("dev") || prof.equals("test") || prof.equals("preprod"));
        if (verify) {
            Logger.info("Generating data for dev/test/preprod profile...", DataServiceConfig.class);
            generateData();
        }
    }
}
