/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.views.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 *
 * @author SATELLITE
 */
public class SplashFrame {
    final java.awt.SplashScreen splash = java.awt.SplashScreen.getSplashScreen();
    private final Logger logger = LoggerFactory.getLogger(SplashFrame.class);
    
    public SplashFrame(){
        if (splash == null) {
            logger.info("SplashScreen.getSplashScreen() returned null");
            return;
        }
        Graphics2D g = splash.createGraphics();
        if (g == null) {
            logger.info("g is null");
            return;
        }
        for(int i=0; i<100; i++) {
            renderSplashFrame(g, i);
            splash.update();
            try {
                Thread.sleep(90);
            }
            catch(InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
        
    }
    
    public void close(){
        splash.close();
    }
    
    static void renderSplashFrame(Graphics2D g, int frame) {
        final String[] comps = {"services", "parametres", "licence","données","initialisation"};
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(88,89,146,92);
        g.setPaintMode();
        g.setColor(Color.BLACK);
        g.setFont(new Font("TimesRoman", Font.BOLD, 14));
        //g.drawString("Loading "+comps[(frame/5)%5]+"...", 75, 100);
        g.drawString("démrrage en cours ...", 50, 100);
    }
}
