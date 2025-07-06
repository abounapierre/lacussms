/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.service.impl;

import com.abouna.generic.dao.DataAccessException;
import com.abouna.lacussms.dao.*;
import com.abouna.lacussms.entities.*;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.views.tools.AES;
import com.abouna.lacussms.views.tools.ConstantUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SATELLITE
 */
@Service
@Transactional
public class LacusSmsServiceImpl implements LacusSmsService {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(LacusSmsServiceImpl.class);
    @Autowired
    private IMessageFormatDao messageFormat;
    @Autowired
    private IBkCliDao bkCliDao;
    @Autowired
    private IBkEveDao bkEveDao;
    @Autowired
    private IBkOpeDao bkOpeDao;
    @Autowired
    private ITypeMessageDao typeMessageDao;
    @Autowired
    private IUserDao userDao;
    @Autowired
    private IMessageDao messageDao;
    @Autowired
    private IBkAgenceDao bkAgenceDao;
    @Autowired
    private IRemoteDBDao remoteDBDao;
    @Autowired
    private ILicenceDao licenceDao;
    @Autowired
    private IBkTelCliDao bkTelCliDao;
    @Autowired
    private IUrlMessageDao urlMessageDao;
    @Autowired
    private IBkEtatOpDao bkEtatOpDao;
    @Autowired
    private IBkCompCliDao bkCompCliDao;
    @Autowired
    private IBkMadDao bkMadDao;
    @Autowired
    private IMessageMandatDao messageMandatDao;
    @Autowired
    private ISentMailDao sentMailDao;
    @Autowired
    private IConfigDao configDao;
    @Autowired
    private ParametreRequeteDao parametreRequeteDao;


    @Override
    public MessageFormat enregistrer(MessageFormat mf) {
        try {
            return messageFormat.create(mf); //To change body of generated methods, choose Tools | Templates.
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<MessageFormat> getAll() {
        try {
            return messageFormat.findAll(); //To change body of generated methods, choose Tools | Templates.
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public MessageFormat modifier(MessageFormat mf) {
        try {
            return messageFormat.update(mf);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void supprimerFormat(Integer id) {
        try {
            messageFormat.delete(messageFormat.findById(id));
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public MessageFormat getFormatById(Integer id) {
        try {
            return messageFormat.findById(id);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<MessageFormat> getFormatByName(String val) {
        return null;
    }

    @Override
    public BkCli enregistrer(BkCli cli) {
        try {
            return bkCliDao.create(cli);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<BkCli> getAllCli() {
        try {
            return bkCliDao.findAll();
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public BkCli modifier(BkCli cli) {
        try {
            return bkCliDao.update(cli);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void supprimerBkCli(String id) {
        try {
            bkCliDao.delete(bkCliDao.findById(id));
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public BkCli getBkCliById(String id) {
        try {
            return bkCliDao.findById(id);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public BkEve enregistrer(BkEve eve) {
        try {
            return bkEveDao.create(eve);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<BkEve> getAllBkEves() {
        try {
            return bkEveDao.findAll();
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public BkEve modifier(BkEve eve) {
        try {
            return bkEveDao.update(eve);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void supprimerBkEve(Integer id) {
        try {
            bkEveDao.delete(bkEveDao.findById(id));
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public BkEve getBkEveById(Integer id) {
        try {
            return bkEveDao.findById(id);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public BkOpe enregistrer(BkOpe ope) {
        try {
            return bkOpeDao.create(ope);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<BkOpe> getAllBkOpes() {
        try {
            return bkOpeDao.findAll();
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public BkOpe modifier(BkOpe ope) {
        try {
            return bkOpeDao.update(ope);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void supprimerBkOpe(String id) {
        try {
            bkOpeDao.delete(bkOpeDao.findById(id));
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public BkOpe getBkOpeById(String id) {
        try {
            return bkOpeDao.findById(id);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public TypeMessage enregistrer(TypeMessage tm) {
        try {
            return typeMessageDao.create(tm);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public TypeMessage modifier(TypeMessage tm) {
        try {
            return typeMessageDao.update(tm);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public User enregistrer(User u) {
        try {
            return userDao.create(u);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public User modifier(User u) {
        try {
            return userDao.update(u);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<BkOpe> getBkOpeByName(String val) {
        return null;
    }

    @Override
    public List<BkEve> getBkEveByName(String val) {
        return null;
    }

    @Override
    public Message enregistrer(Message msg) {
        try {
            return messageDao.create(msg);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Message> getAllMessages() {
        try {
            return messageDao.findAll();
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Message modifier(Message msg) {
        try {
            return messageDao.update(msg);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Message getMessageById(Integer id) {
        try {
            return messageDao.findById(id);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<BkEve> getBkEveByDate(Date date, List<String> states) {
        return bkEveDao.getBkEveByDate(date, states);
    }


    @Override
    public BkAgence enregistrer(BkAgence a) {
        try {
            return bkAgenceDao.create(a);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<BkAgence> getAllBkAgences() {
        try {
            return bkAgenceDao.findAll();
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public BkAgence modifier(BkAgence a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void supprimerBkAgence(String id) {
        try {
            bkAgenceDao.delete(bkAgenceDao.findById(id));
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public BkAgence getBkAgenceById(String id) {
        try {
            return bkAgenceDao.findById(id);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public MessageFormat getFormatByBkOpe(BkOpe ope, String langue) {
        return messageFormat.getMessageFormatByOpe(ope, langue);
    }

    @Override
    public RemoteDB enregistrer(RemoteDB mf) {
        try {
            return remoteDBDao.create(mf);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<RemoteDB> getAllRemoteDB() {
        try {
            return remoteDBDao.findAll();
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public RemoteDB modifier(RemoteDB mf) {
        try {
            return remoteDBDao.update(mf);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void supprimerRemoteDB(Integer id) {
        try {
            remoteDBDao.delete(remoteDBDao.findById(id));
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public RemoteDB getRemoteDBById(Integer id) {
        try {
            return remoteDBDao.findById(id);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public RemoteDB getDefaultRemoteDB(boolean b) {
        return remoteDBDao.getDefaultRemoteDB(b);
    }

    @Override
    public Integer getMaxIndexBkEve() {
        Integer a = bkEveDao.getMaxIndexBkEve();
        return a == null ? 0 : a;
    }

    @Override
    public List<Message> getMessageFromPeriode(Date d1, Date d2) {
        return messageDao.getMessageFromPeriode(d1, d2);
    }

    @Override
    public List<Licence> getLicences() {
        try {
            return licenceDao.findAll();
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Licence enregistrer(Licence licence) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
            Calendar cal = Calendar.getInstance();
            licence.setStartDate(sdf.format(cal.getTime()));
            String secretKey = ConstantUtils.SECRET_KEY;
            String value = AES.decrypt(licence.getValeur(), secretKey);
            assert value != null;
            long jour = (sdf.parse(value.substring(0, 6)).getTime() - cal.getTime().getTime()) / (1000 * 60 * 60 * 24);
            licence.setJour(jour);
            return licenceDao.create(licence);
        } catch (DataAccessException | ParseException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Licence modifier(Licence licence) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
            Calendar cal = Calendar.getInstance();
            licence.setStartDate(sdf.format(cal.getTime()));
            String secretKey = ConstantUtils.SECRET_KEY;
            String value = AES.decrypt(licence.getValeur(), secretKey);
            assert value != null;
            long jour = (sdf.parse(value.substring(0, 6)).getTime() - cal.getTime().getTime()) / (1000 * 60 * 60 * 24);
            logger.info("jour=" + jour);
            licence.setJour(jour);
            return licenceDao.update(licence);
        } catch (DataAccessException | ParseException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public BkTelCli enregistrer(BkTelCli bkTelCli) {
        try {
            return bkTelCliDao.create(bkTelCli);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public BkTelCli modifier(BkTelCli b) {
        try {
            return bkTelCliDao.update(b);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void supprimer(Integer id) {
        try {
            bkTelCliDao.delete(bkTelCliDao.findById(id));
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public UrlMessage enregistrer(UrlMessage url) {
        try {
            return urlMessageDao.create(url);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<UrlMessage> getAllUrlMessages() {
        try {
            return urlMessageDao.findAll();
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public UrlMessage modifier(UrlMessage url) {
        try {
            return urlMessageDao.update(url);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void supprimerUrlMessage(Integer id) {
        try {
            urlMessageDao.delete(urlMessageDao.findById(id));
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public UrlMessage getUrlMessageById(Integer id) {
        try {
            return urlMessageDao.findById(id);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public UrlMessage getDefaultUrlMessage() {
        return urlMessageDao.getDefaultUrlMessage();
    }

    @Override
    public List<BkEtatOp> getListBkEtatOp(boolean actif) {
        return bkEtatOpDao.getListBkEtatOp(actif);
    }

    @Override
    public BkEtatOp enregistrer(BkEtatOp etat) {
        try {
            return bkEtatOpDao.create(etat);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<BkEtatOp> getAllBkEtatOps() {
        try {
            return bkEtatOpDao.findAll();
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public BkEtatOp modifier(BkEtatOp etat) {
        try {
            return bkEtatOpDao.update(etat);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void supprimerBkEtatOp(Integer id) {
        try {
            bkEtatOpDao.delete(bkEtatOpDao.findById(id));
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public BkEtatOp getBkEtatOpById(Integer id) {
        try {
            return bkEtatOpDao.findById(id);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<BkEve> getBkEveBySendParam(boolean b, List<String> listString) {
        return bkEveDao.getBkEveBySendParam(b, listString);
    }

    @Override
    public int supprimerToutMessage() {
        try {
            messageDao.purge();
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @Override
    public int supprimerToutBkEve() {
        try {
            bkEveDao.purge();
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @Override
    public BkCompCli enregistrer(BkCompCli com) {
        try {
            return bkCompCliDao.create(com);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<BkCompCli> getAllBkCompClis() {
        try {
            return bkCompCliDao.findAll();
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public BkCompCli modifier(BkCompCli com) {
        try {
            return bkCompCliDao.update(com);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void supprimerBkCompCli(String id) {
        try {
            bkCompCliDao.delete(bkCompCliDao.findById(id));
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public BkCompCli getBkCompCliById(String id) {
        try {
            return bkCompCliDao.findById(id);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<BkCompCli> getBkCompCliByCli(BkCli cli) {
        try {
            return bkCompCliDao.getBkCompCliByCli(cli);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<BkCli> getBkCliByCriteria(String criteria) {
        return bkCliDao.getBkCliByCriteria(criteria);
    }

    @Override
    public List<BkCompCli> getBkCompCliByCriteria(String cli) {
        try {
            return bkCompCliDao.getBkCompCliByCli(cli);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<BkEve> getBkEveByCriteria(String code) {
        return bkEveDao.getBkEveByCriteria(code);
    }

    @Override
    public int supprimerParPeriode(Date date1, Date date2) {
        return bkEveDao.supprimerParPeriode(date1, date2);
    }

    @Override
    public List<BkCli> getBkCliLimit(int limit) {
        return bkCliDao.getBkCliLimit(limit);
    }

    @Override
    public List<BkCompCli> getBkCompCliLimit(int limit) {
        try {
            return bkCompCliDao.getBkCompCliLimit(limit);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Integer getMaxIndexBkEve(TypeEvent type) {
        Integer a = bkEveDao.getMaxIndexBkEve(type);
        return a == null ? 0 : a;
    }

    @Override
    public List<BkEve> getBkEveBySendParam(boolean send, List<String> list, TypeEvent type) {
        return bkEveDao.getBkEveBySendParam(send, list, type);
    }

    @Override
    public BkCompCli getBkCompCliByCriteria(BkCli cli, String compte, boolean actif) {
        return bkCompCliDao.getBkCompCliByCriteria(cli, compte, actif);
    }

    @Override
    public List<BkEve> getBkEveByCriteria(String code, Date date, String compte) {
        return bkEveDao.getBkEveByCriteria(code, date, compte);
    }

    @Override
    public int supprimerBkMad(Date d1, Date d2) {
        return bkMadDao.supprimerBkMad(d1, d2);
    }

    @Override
    public List<BkMad> getbkMadsByCriteria(String val) {
        return bkMadDao.getbkMadsByCriteria(val);
    }

    @Override
    public Integer getMaxBkMad() {
        Integer a = bkMadDao.getMaxBkMad();
        return a == null ? 0 : a;
    }

    @Override
    public BkMad enregistrer(BkMad bkMad) {
        try {
            return bkMadDao.create(bkMad);
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<BkMad> getAllBkMads() {
        try {
            return bkMadDao.findAll();
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public BkMad modifier(BkMad bkMad) {
        try {
            return bkMadDao.update(bkMad);
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public void supprimerBkMad(Integer id) {
        try {
            bkMadDao.delete(bkMadDao.findById(id));
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error deleting BkMad with id: " + id, ex);
        }
    }

    @Override
    public BkMad getBkMadById(Integer id) {
        try {
            return bkMadDao.findById(id);
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public void supprimerToutBkMad() {
        try {
            bkMadDao.purge();
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public BkMad getBkMadByClesec(String num) {
        return bkMadDao.getBkMadByClesec(num);
    }

    @Override
    public List<BkMad> getBkMadByTraite() {
        return bkMadDao.getBkMadByTraite();
    }

    @Override
    public MessageMandat enregistrer(MessageMandat mes) {
        try {
            return messageMandatDao.create(mes);
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<MessageMandat> getAllMessageMandats() {
        try {
            return messageMandatDao.findAll();
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public MessageMandat modifier(MessageMandat mes) {
        try {
            return messageMandatDao.update(mes);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }


    @Override
    public List<MessageMandat> getMessageMandatFromPeriode(Date d1, Date d2) {
        return messageMandatDao.getMessageFromPeriode(d1, d2);
    }

    @Override
    public int supprimerTout() {
        try {
            messageMandatDao.purge();
            return 0;
        } catch (DataAccessException ex) {
            return 0;
        }
    }

    @Override
    public List<BkMad> getBkMadByLimit(int limit) {
        return bkMadDao.getBkMadByLimit(limit);
    }

    @Override
    public List<BkEve> getBkEveByLimit(int limit) {
        return bkEveDao.getBkEveByLimit(limit);
    }


    @Override
    public List<BkEve> getBkEveByCriteria(String code, String compte, String heure, String montant) {
        return bkEveDao.getBkEveByCriteria(code, compte, heure, montant);
    }

    @Override
    public BkCli getBkCliByNumCompte(String compte) {
        try {
            return bkCompCliDao.getBkCli(compte);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public SentMail saveMail(SentMail mail) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            mail.setDate2(format.format(mail.getSentDate()));
            return sentMailDao.create(mail);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Config enregistrerConfig(Config c) {
        try {
            return configDao.create(c);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Config modifierConfig(Config c) {
        try {
            return configDao.update(c);
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public List<Config> getAllConfig() {
        try {
            return configDao.findAll();
        } catch (DataAccessException ex) {
            Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return Collections.emptyList();
        }
    }

    @Override
    public List<BkEve> getBkEveByCriteriaMontant(String code, String compte, String montant) {
        return bkEveDao.getBkEveByCriteriaMontant(code, compte, montant);
    }

    @Override
    public List<BkEve> getBkEveByPeriode(String code, String compte, Date date1, Date date2) {
        return bkEveDao.getBkEveByPeriode(code, compte, date1, date2);
    }

    @Override
    public void enregistrerParametreRequete(Map<String, String> values, TypeService typeService) {
        values.forEach((k, v) -> {
            ParametreRequete p = new ParametreRequete();
            p.setCode((String) k);
            p.setValeur((String) v);
            p.setService(typeService);
            try {
                List<ParametreRequete> list = parametreRequeteDao.getParametersByService(p.getCode(), typeService);
                if (list.isEmpty()) {
                    parametreRequeteDao.create(p);
                } else {
                    p.setId(list.get(0).getId());
                    parametreRequeteDao.update(p);
                }
            } catch (DataAccessException ex) {
                Logger.getLogger(LacusSmsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    @Override
    public Map<String, String> getParametreRequeteValues(TypeService typeService) {
        Map<String, String> map = new HashMap<>();
        parametreRequeteDao.getParametersByService(typeService).forEach(p -> map.put(p.getCode(), p.getValeur()));
        return map;
    }

    @Override
    public Parametre enregistrerParametre(Parametre parametre) {
        return null;
    }

    @Override
    public Optional<Message> getMessageByEveId(Integer eveId) {
        return messageDao.getMessageByEveId(eveId);
    }

    @Override
    public Optional<MessageMandat> getMessageMandatByNumEve(String eve) {
        return messageMandatDao.getMessageMandatByNumEve(eve);
    }

    @Override
    public Long countBkEve() {
        try {
            return bkEveDao.countEve();
        } catch (Exception e) {
            logger.error("Error counting BkEve records", e);
            return 0L;
        }
    }

    @Override
    public List<BkMad> getBkMadByDate(Date date) {
        return bkMadDao.getBkMadByDate(date);
    }
}
