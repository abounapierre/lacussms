/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.main;

import com.abouna.lacussms.dao.IRemoteDBDao;
import com.abouna.lacussms.entities.BkAgence;
import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.entities.BkEve;
import com.abouna.lacussms.entities.BkOpe;
import com.abouna.lacussms.entities.RemoteDB;
import com.abouna.lacussms.entities.RequeteClient;
import com.abouna.lacussms.entities.RequeteEvenement;
import com.abouna.lacussms.entities.TypeEvent;
import com.abouna.lacussms.entities.TypeService;
import com.abouna.lacussms.service.LacusSmsService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 *
 * @author SATELLITE
 */
public class AppTest {
    
    private  LacusSmsService serviceManager;
    private static RemoteDB remoteDB;
    private static Connection conn;
    
    public AppTest(){
        
    }
    
    @Before
    public void setup(){
        EasyMock.createMock(IRemoteDBDao.class);
        serviceManager = EasyMock.createMock(LacusSmsService.class);
        //remoteDB = EasyMock.createMock(RemoteDB.class);
        conn = EasyMock.createMock(Connection.class);
        //app = EasyMock.createMock(App.class);
        remoteDB = new RemoteDB();
        remoteDB.setDriverClassName("org.h2.Driver");
        remoteDB.setHostName("192.168.0.2");
        remoteDB.setId(1);
        remoteDB.setName("sa");
        remoteDB.setParDefault(true);
        remoteDB.setUrl("jdbc:h2:~/.smile/db-test;AUTO_SERVER=true");
        remoteDB.setPassword("");
        
        ReflectionTestUtils.setField(App.class, "serviceManager", serviceManager);
        ReflectionTestUtils.setField(App.class, "remoteDB", remoteDB);
        ReflectionTestUtils.setField(App.class, "conn", conn);
    }
    
    @Test
    public void testServiceEvenement() throws SQLException, ParseException{
        new App();
        BkEve eve = new BkEve();
        eve.setCompte("14554524545545");
        eve.setDCO(new Date());
        eve.setHsai("02:54:20.000");
        eve.setDVAB("2019-03-01 00:00:00");
        Integer compteur = 894;
        BkCli bkCli = new BkCli("0608922", "AB", "PI", "MR");
        bkCli.setPhone(0);
        BkAgence bkAgence = new BkAgence("020", "AG TEST","TEST ADR");
        BkOpe bkOpe = new BkOpe("001", "SOLDE");
        
        Map<String,String> map = new HashMap<>();
        map.put(RequeteEvenement.CODE_AGENCE.name(), "AGE_1");
        map.put(RequeteEvenement.CODE_CLIENT.name(), "CLI1");
        map.put(RequeteEvenement.CODE_ETAT.name(), "ETA");
        map.put(RequeteEvenement.CODE_EVENEMENT.name(), "EVE");
        map.put(RequeteEvenement.CODE_OPERATION.name(), "OPE");
        map.put(RequeteEvenement.DATE_EVENEMENT.name(), "DSAI");
        map.put(RequeteEvenement.DATE_VALEUR.name(), "DVAB");
        map.put(RequeteEvenement.HEURE_EVENEMENT.name(), "HSAI");
        map.put(RequeteEvenement.MONTANT_OPERATION.name(), "MON1");
        map.put(RequeteEvenement.NOM_TABLE.name(), "BKEVE1");
        map.put(RequeteEvenement.NUMERO_COMPTE.name(), "NCP");
        Map<String,String> mapClient = new HashMap<>();
        mapClient.put(RequeteClient.CODE_CLIENT.name(), "CLI");
        mapClient.put(RequeteClient.NOM_TABLE.name(), "BKTELCLI_NEW");
        mapClient.put(RequeteClient.NUMERO_TELEPHONE.name(), "NUM");
        mapClient.put(RequeteClient.TYPE_CLIENT.name(), "TYP");
        
        PreparedStatement prepStmt = EasyMock.createMock(PreparedStatement.class);
        ResultSet rs = EasyMock.createMock(ResultSet.class);
         
        EasyMock.expect(conn.prepareStatement(EasyMock.isA(String.class))).andReturn(prepStmt);
        EasyMock.expect(prepStmt.executeQuery()).andReturn(rs);
        EasyMock.expect(rs.next()).andReturn(true);
        
        
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
        App.serviceEvenement();
    }
}
