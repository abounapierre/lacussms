/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author SATELLITE
 */
public class ApplicationConfig {
   private static ApplicationContext applicationContext;// = buildApplicationContextFactory();

    private static ApplicationContext buildApplicationContextFactory() {
        try {
            return new AnnotationConfigApplicationContext(SpringMainConfig.class);
        }
        catch (Throwable ex) {
            System.err.println("Initial Application Context creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationConfig.applicationContext = applicationContext;
    }
}
