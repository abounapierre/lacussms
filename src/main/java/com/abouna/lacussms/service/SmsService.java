/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author SATELLITE
 */
@Component
public class SmsService {
    @Autowired
    private LacusSmsService service;
    
    public void sendSmsJob(String jobId){
        //service.getBkCliByCriteria()
    }
    
    //public 
    
}
