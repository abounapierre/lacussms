/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.main;

import com.abouna.lacussms.dao.IRemoteDBDao;
import com.abouna.lacussms.dto.BkEtatOpConfigBean;
import com.abouna.lacussms.entities.*;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.service.ServiceEvenement;
import com.abouna.lacussms.views.tools.ConstantUtils;
import com.abouna.lacussms.views.tools.Utils;
import org.easymock.EasyMock;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

/**
 *
 * @author SATELLITE
 */
public class ServiceEvenementTest {
    
    private  LacusSmsService serviceManager;

    private ServiceEvenement serviceEvenement;
    
    public ServiceEvenementTest(){
        
    }
    
    @Before
    public void setup() throws SQLException, IOException {
        DBUtils.initDatabase();
        EasyMock.createMock(IRemoteDBDao.class);
        serviceManager = EasyMock.createMock(LacusSmsService.class);
        //remoteDB = EasyMock.createMock(RemoteDB.class);
        Connection conn = DBUtils.getConnection();//EasyMock.createMock(Connection.class);
        //app = EasyMock.createMock(App.class);
        RemoteDB remoteDB = new RemoteDB();
        remoteDB.setDriverClassName("org.h2.Driver");
        remoteDB.setHostName("192.168.0.2");
        remoteDB.setId(1);
        remoteDB.setName("sa");
        remoteDB.setParDefault(true);
        remoteDB.setUrl("jdbc:h2:mem:testDb;DB_CLOSE_DELAY=-1");
        remoteDB.setPassword("");
        serviceEvenement = new ServiceEvenement(serviceManager, new BkEtatOpConfigBean(Arrays.asList("OA", "OP"), ""));
        serviceEvenement.setConn(conn);
    }

    @Test
    public void testServiceEvenementCompteurNonNUll() throws SQLException, ParseException{
        BkEve eve = new BkEve();
        eve.setCompte("14554524545545");
        eve.setDCO(new Date());
        eve.setHsai("02:54:20.000");
        eve.setDVAB("2022-08-16 00:00:00");
        Integer compteur = 894;
        BkCli bkCli = new BkCli("0608922", "AB", "PI", "MR");
        bkCli.setPhone(0);
        BkAgence bkAgence = new BkAgence("020", "AG TEST","TEST ADR");
        BkOpe bkOpe = new BkOpe("001", "SOLDE");
        
        Map<String,String> map = new HashMap<>();
        map.put(RequeteEvenement.CODE_AGENCE.name(), "AGE");
        map.put(RequeteEvenement.CODE_CLIENT.name(), "CLI1");
        map.put(RequeteEvenement.CODE_ETAT.name(), "ETA");
        map.put(RequeteEvenement.CODE_EVENEMENT.name(), "EVE");
        map.put(RequeteEvenement.CODE_OPERATION.name(), "OPE");
        map.put(RequeteEvenement.DATE_EVENEMENT.name(), "DSAI");
        map.put(RequeteEvenement.DATE_VALEUR.name(), "DVAB");
        map.put(RequeteEvenement.HEURE_EVENEMENT.name(), "HSAI");
        map.put(RequeteEvenement.MONTANT_OPERATION.name(), "MON1");
        map.put(RequeteEvenement.NOM_TABLE.name(), "BKEVE");
        map.put(RequeteEvenement.NUMERO_COMPTE.name(), "NCP");
        Map<String,String> mapClient = new HashMap<>();
        mapClient.put(RequeteClient.CODE_CLIENT.name(), "CLI");
        mapClient.put(RequeteClient.NOM_TABLE.name(), "BKTELCLI");
        mapClient.put(RequeteClient.NUMERO_TELEPHONE.name(), "NUM");
        mapClient.put(RequeteClient.TYPE_CLIENT.name(), "TYP");
        
        EasyMock.expect(serviceManager.getMaxIndexBkEve(TypeEvent.ordinaire)).andReturn(compteur);
        EasyMock.expect(serviceManager.getBkEveById(compteur)).andReturn(eve);
        EasyMock.expect(serviceManager.getParametreRequeteValues(TypeService.EVENEMENT)).andReturn(map);
        EasyMock.expect(serviceManager.getBkAgenceById(EasyMock.isA(String.class))).andReturn(bkAgence);
        EasyMock.expect(serviceManager.getBkCliById(EasyMock.isA(String.class))).andReturn(bkCli);
        EasyMock.expect(serviceManager.getBkOpeById(EasyMock.isA(String.class))).andReturn(bkOpe);
        EasyMock.expect(serviceManager.getMaxIndexBkEve()).andReturn(compteur+1);
        EasyMock.expect(serviceManager.getBkCliById(EasyMock.isA(String.class))).andReturn(bkCli);
        EasyMock.expect(serviceManager.getBkEveByCriteria(EasyMock.isA(String.class), EasyMock.isA(Date.class), EasyMock.isA(String.class))).andReturn(new ArrayList<>());
        EasyMock.expect(serviceManager.getBkEveByPeriode(EasyMock.isA(String.class), EasyMock.isA(String.class), EasyMock.isA(Date.class),EasyMock.isA(Date.class))).andReturn(new ArrayList<>());
        EasyMock.expect(serviceManager.getBkEveByCriteria(EasyMock.isA(String.class), EasyMock.isA(String.class), EasyMock.isA(String.class),EasyMock.isA(String.class))).andReturn(new ArrayList<>());
        EasyMock.expect(serviceManager.getBkEveByCriteriaMontant(EasyMock.isA(String.class), EasyMock.isA(String.class), EasyMock.isA(String.class))).andReturn(new ArrayList<>());
        EasyMock.expect(serviceManager.enregistrer(EasyMock.isA(BkEve.class))).andReturn(eve);
        EasyMock.expect(serviceManager.getParametreRequeteValues(TypeService.TELEPHONE_CLIENT)).andReturn(mapClient);
        EasyMock.expect(serviceManager.modifier(EasyMock.isA(BkCli.class))).andReturn(bkCli);
        EasyMock.replay(serviceManager);
        serviceEvenement.serviceEvenement();
    }

    @Test
    public void testServiceEvenementCompteurZero() throws SQLException, ParseException{
        //new App();
        BkEve eve = new BkEve();
        eve.setCompte("14554524545545");
        eve.setDCO(new Date());
        eve.setHsai("02:54:20.000");
        eve.setDVAB("2022-08-16 00:00:00");
        Integer compteur = 0;
        BkCli bkCli = new BkCli("0608922", "AB", "PI", "MR");
        bkCli.setPhone(0);
        BkAgence bkAgence = new BkAgence("020", "AG TEST","TEST ADR");
        BkOpe bkOpe = new BkOpe("001", "SOLDE");

        Map<String,String> map = new HashMap<>();
        map.put(RequeteEvenement.CODE_AGENCE.name(), "AGE");
        map.put(RequeteEvenement.CODE_CLIENT.name(), "CLI1");
        map.put(RequeteEvenement.CODE_ETAT.name(), "ETA");
        map.put(RequeteEvenement.CODE_EVENEMENT.name(), "EVE");
        map.put(RequeteEvenement.CODE_OPERATION.name(), "OPE");
        map.put(RequeteEvenement.DATE_EVENEMENT.name(), "DSAI");
        map.put(RequeteEvenement.DATE_VALEUR.name(), "DVAB");
        map.put(RequeteEvenement.HEURE_EVENEMENT.name(), "HSAI");
        map.put(RequeteEvenement.MONTANT_OPERATION.name(), "MON1");
        map.put(RequeteEvenement.NOM_TABLE.name(), "BKEVE");
        map.put(RequeteEvenement.NUMERO_COMPTE.name(), "NCP");
        Map<String,String> mapClient = new HashMap<>();
        mapClient.put(RequeteClient.CODE_CLIENT.name(), "CLI");
        mapClient.put(RequeteClient.NOM_TABLE.name(), "BKTELCLI");
        mapClient.put(RequeteClient.NUMERO_TELEPHONE.name(), "NUM");
        mapClient.put(RequeteClient.TYPE_CLIENT.name(), "TYP");

        EasyMock.expect(serviceManager.getMaxIndexBkEve(TypeEvent.ordinaire)).andReturn(compteur);
        EasyMock.expect(serviceManager.getBkEveById(compteur)).andReturn(eve);
        EasyMock.expect(serviceManager.getParametreRequeteValues(TypeService.EVENEMENT)).andReturn(map);
        EasyMock.expect(serviceManager.getBkAgenceById(EasyMock.isA(String.class))).andReturn(bkAgence);
        EasyMock.expect(serviceManager.getBkCliById(EasyMock.isA(String.class))).andReturn(bkCli);
        EasyMock.expect(serviceManager.getBkOpeById(EasyMock.isA(String.class))).andReturn(bkOpe);
        EasyMock.expect(serviceManager.getMaxIndexBkEve()).andReturn(compteur+1);
        EasyMock.expect(serviceManager.getBkCliById(EasyMock.isA(String.class))).andReturn(bkCli);
        EasyMock.expect(serviceManager.getBkEveByCriteria(EasyMock.isA(String.class), EasyMock.isA(Date.class), EasyMock.isA(String.class))).andReturn(new ArrayList<>());
        EasyMock.expect(serviceManager.getBkEveByPeriode(EasyMock.isA(String.class), EasyMock.isA(String.class), EasyMock.isA(Date.class),EasyMock.isA(Date.class))).andReturn(new ArrayList<>());
        EasyMock.expect(serviceManager.getBkEveByCriteria(EasyMock.isA(String.class), EasyMock.isA(String.class), EasyMock.isA(String.class),EasyMock.isA(String.class))).andReturn(new ArrayList<>());
        EasyMock.expect(serviceManager.getBkEveByCriteriaMontant(EasyMock.isA(String.class), EasyMock.isA(String.class), EasyMock.isA(String.class))).andReturn(new ArrayList<>());
        EasyMock.expect(serviceManager.enregistrer(EasyMock.isA(BkEve.class))).andReturn(eve);
        EasyMock.expect(serviceManager.getParametreRequeteValues(TypeService.TELEPHONE_CLIENT)).andReturn(mapClient);
        EasyMock.expect(serviceManager.modifier(EasyMock.isA(BkCli.class))).andReturn(bkCli);
        EasyMock.replay(serviceManager);
        serviceEvenement.serviceEvenement();
    }

    @Test
    public void testPasswordEncryption() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(ConstantUtils.SECRET_KEY); // encryptor's private key
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        String plainText = "310822210000";
        String encryptedPassword = encryptor.encrypt(plainText);
        System.out.println("encryptedPassword : " + encryptedPassword);
    }

    @Test
    public void testDate() {
        Date date = Utils.getDateSimpleFormat("ddMMyyHHmmss", "280822141000");
        System.out.println(" Date : " + date);
    }
}
