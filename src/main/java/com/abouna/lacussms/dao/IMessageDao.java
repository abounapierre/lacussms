/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.dao;

import com.abouna.generic.dao.IDao;
import com.abouna.lacussms.entities.Message;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author SATELLITE
 */
public interface IMessageDao extends IDao<Message, Integer>{
    List<Message> getMessageFromPeriode(Date d1, Date d2);
    int supprimerTout();
    Optional<Message> getMessageByEveId(Integer eveId);
}
