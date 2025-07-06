/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.service;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.entities.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author SATELLITE
 */
public interface LacusSmsService {

    static LacusSmsService getInstance() {
        return ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
    }

    List<BkEve> getBkEveByDate(Date date, List<String> states);

    MessageFormat enregistrer(MessageFormat mf);

    List<MessageFormat> getAll();

    MessageFormat modifier(MessageFormat mf);

    void supprimerFormat(Integer id);

    MessageFormat getFormatById(Integer id);

    RemoteDB getDefaultRemoteDB(boolean b);

    Integer getMaxIndexBkEve();

    RemoteDB enregistrer(RemoteDB mf);

    List<RemoteDB> getAllRemoteDB();

    RemoteDB modifier(RemoteDB mf);

    void supprimerRemoteDB(Integer id);

    RemoteDB getRemoteDBById(Integer id);

    Message enregistrer(Message msg);

    List<Message> getAllMessages();

    Message modifier(Message msg);

    Message getMessageById(Integer id);

    int supprimerToutMessage();

    UrlMessage enregistrer(UrlMessage url);

    List<UrlMessage> getAllUrlMessages();

    UrlMessage modifier(UrlMessage url);

    void supprimerUrlMessage(Integer id);

    UrlMessage getUrlMessageById(Integer id);

    UrlMessage getDefaultUrlMessage();

    List<BkEtatOp> getListBkEtatOp(boolean actif);

    BkEtatOp enregistrer(BkEtatOp etat);

    List<BkEtatOp> getAllBkEtatOps();

    BkEtatOp modifier(BkEtatOp etat);

    void supprimerBkEtatOp(Integer id);

    BkEtatOp getBkEtatOpById(Integer id);

    List<MessageFormat> getFormatByName(String val);

    BkCli enregistrer(BkCli cli);

    List<BkCli> getAllCli();

    BkCli modifier(BkCli cli);

    void supprimerBkCli(String id);

    BkCli getBkCliById(String id);

    BkCli getBkCliByNumCompte(String compte);

    BkEve enregistrer(BkEve eve);

    List<BkEve> getAllBkEves();

    BkEve modifier(BkEve eve);

    void supprimerBkEve(Integer id);

    BkEve getBkEveById(Integer id);

    List<BkEve> getBkEveBySendParam(boolean send, List<String> states);

    int supprimerToutBkEve();

    BkOpe enregistrer(BkOpe ope);

    List<BkOpe> getAllBkOpes();

    BkOpe modifier(BkOpe ope);

    void supprimerBkOpe(String id);

    BkOpe getBkOpeById(String id);

    BkCompCli enregistrer(BkCompCli com);

    List<BkCompCli> getAllBkCompClis();

    BkCompCli modifier(BkCompCli com);

    void supprimerBkCompCli(String id);

    BkCompCli getBkCompCliById(String id);

    List<BkCompCli> getBkCompCliByCli(BkCli cli);

    List<BkCompCli> getBkCompCliByCriteria(String cli);

    TypeMessage enregistrer(TypeMessage tm);

    TypeMessage modifier(TypeMessage tm);


    User enregistrer(User u);

    User modifier(User u);

    BkAgence enregistrer(BkAgence a);

    List<BkAgence> getAllBkAgences();

    BkAgence modifier(BkAgence a);

    void supprimerBkAgence(String id);

    BkAgence getBkAgenceById(String id);

    List<BkOpe> getBkOpeByName(String val);

    List<BkEve> getBkEveByName(String val);

    MessageFormat getFormatByBkOpe(BkOpe ope, String langue);

    List<Message> getMessageFromPeriode(Date d1, Date d2);

    List<Licence> getLicences();

    Licence enregistrer(Licence licence);

    Licence modifier(Licence licence);

    BkTelCli enregistrer(BkTelCli bkTelCli);

    BkTelCli modifier(BkTelCli b);

    void supprimer(Integer id);

    List<BkCli> getBkCliByCriteria(String criteria);

    List<BkEve> getBkEveByCriteria(String code);

    int supprimerParPeriode(Date date1, Date date2);

    List<BkCli> getBkCliLimit(int limit);

    List<BkCompCli> getBkCompCliLimit(int limit);

    Integer getMaxIndexBkEve(TypeEvent type);

    List<BkEve> getBkEveBySendParam(boolean send, List<String> list, TypeEvent type);

    BkCompCli getBkCompCliByCriteria(BkCli cli, String compte, boolean actif);

    List<BkEve> getBkEveByCriteria(String code, Date date, String compte);
    
    List<BkEve> getBkEveByPeriode(String code, String compte, Date date1, Date date2);

    int supprimerBkMad(Date d1, Date d2);

    List<BkMad> getbkMadsByCriteria(String val);

    Integer getMaxBkMad();

    BkMad enregistrer(BkMad bkMad);

    List<BkMad> getAllBkMads();

    BkMad modifier(BkMad bkMad);

    void supprimerBkMad(Integer id);

    BkMad getBkMadById(Integer id);

    void supprimerToutBkMad();

    BkMad getBkMadByClesec(String num);

    List<BkMad> getBkMadByTraite();

    MessageMandat enregistrer(MessageMandat mes);

    List<MessageMandat> getAllMessageMandats();

    MessageMandat modifier(MessageMandat mes);

    List<MessageMandat> getMessageMandatFromPeriode(Date d1, Date d2);

    int supprimerTout();

    List<BkMad> getBkMadByLimit(int limit);

    List<BkEve> getBkEveByLimit(int limit);

    List<BkEve> getBkEveByCriteria(String code, String compte, String heure, String montant);
    
     List<BkEve> getBkEveByCriteriaMontant(String code, String compte, String montant);

    SentMail saveMail(SentMail mail);

    Config enregistrerConfig(Config c);

    Config modifierConfig(Config c);

    List<Config> getAllConfig();

    void enregistrerParametreRequete(Map<String, String> values, TypeService typeService);

    Map<String,String> getParametreRequeteValues(TypeService typeService);

    Parametre enregistrerParametre(Parametre parametre);

    Optional<Message> getMessageByEveId(Integer eveId);

    Optional<MessageMandat> getMessageMandatByNumEve(String eve);

    Long countBkEve();

    List<BkMad> getBkMadByDate(Date date);
}
