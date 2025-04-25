//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.abouna.lacussms.views.tools;

import com.abouna.lacussms.sender.OrangeSMSPlateforme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sender {
    private static final Logger logger = LoggerFactory.getLogger(Sender.class);
    public Sender() {
    }

    public static boolean send(String number, String msg) {
        logger.info("Sender.send() called with number: {}, msg: {}", number, msg);
        return OrangeSMSPlateforme.send(number, msg);
    }
}
