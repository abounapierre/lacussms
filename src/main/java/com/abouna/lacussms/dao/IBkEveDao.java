/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.dao;

import com.abouna.generic.dao.IDao;
import com.abouna.lacussms.entities.BkEve;
import com.abouna.lacussms.entities.TypeEvent;

import java.util.Date;
import java.util.List;

/**
 *
 * @author SATELLITE
 */
public interface IBkEveDao extends IDao<BkEve, Integer>{
    List<BkEve> getBkEvesByEtat(String etat, Date date);
    List<BkEve> getBkEveMaxDate(List<String> states);
    List<BkEve> getBkEveByDate(Date date, List<String> states);
    Integer getMaxIndexBkEve();
    Integer getMaxIndexBkEve(TypeEvent type);
    List<BkEve> getBkEveByPeriode(Date d1, Date d2);
    List<BkEve> getBkEveBySendParam(boolean send, List<String> list);
    List<BkEve> getBkEveBySendParam(boolean send, List<String> list, TypeEvent type);
    List<BkEve> getBkEveByCriteria(String code, String date, String compte);
    List<BkEve> getBkEveByCriteria(String code, Date date, String compte);
    List<BkEve> getBkEveByCriteria2(String code, Date date, String compte);
    List<BkEve> getBkEveByCriteria(String code);
    int supprimerParPeriode(String date1, String date2);
    int supprimerParPeriode(Date date1, Date date2);
    List<BkEve> getBkEveByCriteria(String date1, String date2);
    List<BkEve> getBkEveByLimit(int limit);
    List<BkEve> getBkEveByCriteria(String code, String compte, String heure, String montant);
    List<BkEve> getBkEveByCriteriaMontant(String code, String compte, String montant);
    List<BkEve> getBkEveByPeriode(String code, String compte, Date date1, Date date2);
    Long countEve();
}
