/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.dao;

import com.abouna.generic.dao.IDao;
import com.abouna.lacussms.entities.BkMad;

import java.util.Date;
import java.util.List;

/**
 *
 * @author SATELLITE
 */
public interface IBkMadDao extends IDao<BkMad, Integer>{
    List<BkMad> getBkMadsByPeriode(Date d1, Date d2);
    int supprimerBkMad(Date d1, Date d2);
    List<BkMad> getbkMadsByCriteria(String val);
    BkMad getBkMadByCriteria(String num, String ad1p, Date date);
    Integer getMaxBkMad();
    void supprimerAll();
    BkMad getBkMadByClesec(String num);
    List<BkMad> getBkMadByTraite(int traite);
    List<BkMad> getBkMadByTraite();
    List<BkMad> getBkMadByLimit(int limit);
}
