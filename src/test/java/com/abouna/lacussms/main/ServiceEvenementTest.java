/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.main;

import com.abouna.lacussms.dao.IRemoteDBDao;
import com.abouna.lacussms.entities.*;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.service.ServiceEvenement;
import com.abouna.lacussms.views.tools.FingerPrint;
import com.abouna.lacussms.views.tools.Utils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;

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

        serviceEvenement = new ServiceEvenement(serviceManager, "METHODE1", remoteDB.getUrl(), conn, "", Arrays.asList("OA", "OP"));
        
        //ReflectionTestUtils.setField(ServiceEvenement.class, "serviceManager", serviceManager);
        //ReflectionTestUtils.setField(App.class, "remoteDB", remoteDB);
        //ReflectionTestUtils.setField(ServiceEvenement.class, "conn", conn);

    }

    @Test
    public void testServiceEvenementCompteurNonNUll() throws SQLException, ParseException{
        //new App();
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
        
        //PreparedStatement prepStmt = EasyMock.createMock(PreparedStatement.class);
        //ResultSet rs = EasyMock.createMock(ResultSet.class);

        //EasyMock.expect(conn.prepareStatement(EasyMock.isA(String.class))).andReturn(prepStmt);
        //EasyMock.expect(prepStmt.executeQuery()).andReturn(rs);
        //EasyMock.expect(rs.next()).andReturn(true);
        //EasyMock.replay(conn);
        
        
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

        //PreparedStatement prepStmt = EasyMock.createMock(PreparedStatement.class);
        //ResultSet rs = EasyMock.createMock(ResultSet.class);

        //EasyMock.expect(conn.prepareStatement(EasyMock.isA(String.class))).andReturn(prepStmt);
        //EasyMock.expect(prepStmt.executeQuery()).andReturn(rs);
        //EasyMock.expect(rs.next()).andReturn(true);
        //EasyMock.replay(conn);


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
        String plainText = "310822210000";
        String encryptedPassword = Utils.getStringEncryptor().encrypt(plainText);
        System.out.println("encryptedPassword : " + encryptedPassword);
    }

    @Test
    public void testDate() {
        Date date = Utils.getDateSimpleFormat("ddMMyyHHmmss", "280822141000");
        System.out.println(" Date : " + date);
        assertEquals(date, Utils.convertToDate(LocalDateTime.of(2022, 8, 28, 14, 10, 00)));
    }

    @Test
    public void testDateError() {
        Date date = Utils.getDateSimpleFormat("ddMMyyHHmmss", "1310221423");
        System.out.println(" Date : " + date);
        assertNull(date);
    }

    @Test
    public void testDateError1() {
        Date date = Utils.getDateSimpleFormat("ddMMyyHHmmss", "28822141000");
        System.out.println(" Date : " + date);
        assertNull(date);
    }

    @Test
    public void testDateError2() {
        Date date = Utils.getDateSimpleFormat("ddMMyyHHmmss", "281522141000");
        System.out.println(" Date : " + date);
        assertNotNull(date);
    }

   /* public void testConfig() {
        String driver = env.getProperty("jdbc.driverClassName");
        String url = env.getProperty("jdbc.url");
        String user = env.getProperty("jdbc.user");
        String pass = env.getProperty("jdbc.pass");
        logger.debug("### driver {} url {} user {} pass {} ###", driver, url, user, pass);
    }*/

    @Test
    public void testHashMacAddress() throws IOException {
        System.out.println(" FINGERPRINT : " + FingerPrint.getEncodedMacAddress());
        FingerPrint.writeByte(FingerPrint.getEncodedMacAddress());
        FingerPrint.writeBinary(FingerPrint.getEncodedMacAddress());
        String original = FingerPrint.readBinaryFile("C:\\Users\\eabou\\.lacuss\\output.bin");
        System.out.println("Original: " + original);
        System.out.println("path home: " + System.getProperty("user.home"));
    }
}
