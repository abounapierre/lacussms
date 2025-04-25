/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.service;

import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.dto.SmsScheduledDto;
import com.abouna.lacussms.entities.*;
import com.abouna.lacussms.views.main.MainFrame;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author SATELLITE
 */
public interface LacusSmsService {

    static LacusSmsService getInstance() {
        return ApplicationConfig.getApplicationContext().getBean(LacusSmsService.class);
    }

    MessageFormat enregistrer(MessageFormat mf);

    List<MessageFormat> getAll();

    MessageFormat modifier(MessageFormat mf);

    void supprimerFormat(Integer id);

    MessageFormat getFormatById(Integer id);

    List<MessageFormat> getFormatByBkOpe(BkOpe bkOpe);

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

    void supprimerMessage(Integer id);

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

    List<BkEve> getBkEveByEtat(String etat, Date date);

    List<BkEve> getBkEveBySendParam(boolean send);

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

    List<BkCompCli> getBkCompCliByCli(BkCli cli, boolean actif);

    List<BkCompCli> getBkCompCliByCriteria(String cli);

    TypeMessage enregistrer(TypeMessage tm);

    List<TypeMessage> getAllTypeMessages();

    TypeMessage modifier(TypeMessage tm);

    void supprimerTypeMessage(Integer id);

    TypeMessage getTypeMessageById(Integer id);

    User enregistrer(User u);

    List<User> getAllUsers();

    User modifier(User u);

    void supprimerUser(String id);

    User getUserById(String id);

    BkAgence enregistrer(BkAgence a);

    List<BkAgence> getAllBkAgences();

    BkAgence modifier(BkAgence a);

    void supprimerBkAgence(String id);

    BkAgence getBkAgenceById(String id);

    List<BkCli> getBkCliByName(String val);

    List<BkOpe> getBkOpeByName(String val);

    List<BkEve> getBkEveByName(String val);

    List<BkEve> getBkEveByDate(Date date);

    List<BkEve> getBkEveMaxDate();

    MessageFormat getFormatByBkOpe(BkOpe ope, String langue);

    List<BkEve> getBkEveByPeriode(Date d1, Date d2);

    List<Message> getMessageFromPeriode(Date d1, Date d2);

    List<Licence> getLicences();

    Licence enregistrer(Licence licence);

    Licence modifier(Licence licence);

    List<BkTelCli> getListBkTelByCli(BkCli b);

    BkTelCli getListBkTelByCliDefault(BkCli b, boolean d);

    BkTelCli enregistrer(BkTelCli bkTelCli);

    List<BkTelCli> getBkTelByClis();

    BkTelCli modifier(BkTelCli b);

    void supprimer(Integer id);

    BkTelCli getBkTelCliById(Integer i);

    Integer count(BkTelCli bkTelCli);

    List<BkEve> getBkEveBySendParam(boolean b, List<String> listString);

    List<BkCli> getBkCliByCriteria(String criteria);

    List<BkEve> getBkEveByCriteria(String code, String date, String compte);

    List<BkEve> getBkEveByCriteria(String code);

    int supprimerParPeriode(String date1, String date2);

    int supprimerParPeriode(Date date1, Date date2);

    List<BkCli> getBkCliLimit(int limit);

    List<BkCompCli> getBkCompCliLimit(int limit);

    Integer getMaxIndexBkEve(TypeEvent type);

    List<BkEve> getBkEveBySendParam(boolean send, List<String> list, TypeEvent type);

    List<BkCompCli> getBkCompCliByCli(BkCli cli, String compte, boolean actif);

    BkCompCli getBkCompCliByCriteria(BkCli cli, String compte, boolean actif);

    List<BkEve> getBkEveByCriteria(String code, Date date, String compte);
    
    List<BkEve> getBkEveByPeriode(String code, String compte, Date date1, Date date2);

    List<BkMad> getBkMadsByPeriode(Date d1, Date d2);

    int supprimerBkMad(Date d1, Date d2);

    List<BkMad> getbkMadsByCriteria(String val);

    BkMad getBkMadByCriteria(String num, String ad1p, Date date);

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

    void supprimerMessageMandat(Integer id);

    MessageMandat getMessageMandatById(Integer id);

    List<MessageMandat> getMessageMandatFromPeriode(Date d1, Date d2);

    int supprimerTout();

    List<BkMad> getBkMadByLimit(int limit);

    List<BkEve> getBkEveByLimit(int limit);

    List<BkEve> getBkEveByCriteria2(String code, Date date, String compte);

    List<BkEve> getBkEveByCriteria(String code, String compte, String heure, String montant);
    
     List<BkEve> getBkEveByCriteriaMontant(String code, String compte, String montant);
    
    List<BkEve> getBkEveByCriteria(String code, String compte);

    List<SentMail> getMailByDate(Date d);

    SentMail saveMail(SentMail mail);

    List<SentMail> getAllEmails();

    Config enregistrerConfig(Config c);

    Config modifierConfig(Config c);

    List<Config> getAllConfig();

    boolean viderLicence();
    
    Holiday saveHoliday(Holiday holiday) throws ExecutionException;
    
    void supprimerHoliday(Integer id);
    
    List<Holiday> getHolidays();
    
    Holiday getHolidayById(Integer id);
    
    List<Holiday> getHolidaysByDate(String date);
    
    //service des operations 
    Command enregistrer(Command cmd);

    List<Command> getAllCommands();
    
     List<Command> getAllCommands(Date date);
     
     List<Command> getCommandByStatus(Status status);

    Command modifier(Command cmd);

    void supprimerCommand(Integer id);

    Command getCommandById(Integer id);
    
    void serviceRequete();
    
    List<ServiceOffert> getServiceOfferts();
    
    ServiceOffert findServiceByCode(String code);
    
    ServiceOffert enregistrerService(ServiceOffert s);
    
    ServiceOffert modifierService(ServiceOffert s);
    
    void supprimerService(Integer id);

    ServiceOffert getServiceById(Integer id);
    
    ServiceOffert getServiceByCode(String id);
    
    List<CutOff> getAllCutOffs();
    
    List<CutOff> getCutOffs();
    
    CutOff getLastCutOff(Date dateDebut);
    
    CutOff getCutOffById(Integer id);
    
    SmsProgramming save(SmsProgramming sms);
    
    SmsProgramming uodate(SmsProgramming sms);
    
    List<SmsProgramming> findAll();
    
    List<SmsProgramming> findByDate(Date date);

    void enregistrerParametreRequete(Map<String, String> values, TypeService typeService);
    
    List<ParametreRequete> getParametreRequetes(TypeService typeService);
    
    Map<String,String> getParametreRequeteValues(TypeService typeService);

    SmsScheduled getSmsScheduledById(Long id);

    List<SmsScheduled> getAllSmsScheduleds();
    
    List<SmsScheduledDto> getAllSmsScheduledDtos();

    SmsScheduledDto getSmsScheduledDtoById(Long id);
    
    Long saveSmsScheduled(SmsScheduledDto smsScheduledDto);
    
    void updateQuartzId(SmsScheduled smsScheduled);

    Parametre enregistrerParametre(Parametre parametre);
}
