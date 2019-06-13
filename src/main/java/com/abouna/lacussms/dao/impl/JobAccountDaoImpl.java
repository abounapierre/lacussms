/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.dao.impl;

import com.abouna.generic.dao.DataAccessException;
import com.abouna.generic.dao.impl.GenericDao;
import com.abouna.lacussms.dao.IJobAccountDao;
import com.abouna.lacussms.dto.BkCliDto;
import com.abouna.lacussms.entities.JobAccount;
import com.abouna.lacussms.entities.SmsScheduled;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author SATELLITE
 */
@Repository
public class JobAccountDaoImpl extends GenericDao<JobAccount, Long>implements IJobAccountDao{

    @Override
    public List<JobAccount> getClientJobInfos(SmsScheduled smsScheduled) {
        String query = "SELECT j FROM JobAccount j WHERE j.smsScheduled=:smsScheduled";
        return getManager().createQuery(query).setParameter("smsScheduled", smsScheduled).getResultList();
    }
    
}
