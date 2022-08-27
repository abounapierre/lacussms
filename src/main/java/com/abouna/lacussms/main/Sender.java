//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.abouna.lacussms.main;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Sender {
    private static final Logger logger = LoggerFactory.getLogger(Sender.class);
    public Sender() {
    }

    public static boolean send(String urlText, String number, String msg) {
        String link;
        try {
            link = urlText.replace("<num>", URLEncoder.encode(number, "ISO-8859-1")).replace("<msg>", URLEncoder.encode(msg, "ISO-8859-1"));
        } catch (UnsupportedEncodingException var36) {
            return false;
        }

        HttpGet request = new HttpGet(link);
        boolean resp = false;

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            Throwable var7 = null;

            try {
                CloseableHttpResponse response = httpClient.execute(request);
                Throwable var9 = null;

                try {
                    if (response != null && response.getStatusLine() != null) {
                        int code = response.getStatusLine().getStatusCode();
                        if (code == 200) {
                            resp = true;
                        }
                    }
                } catch (Throwable var37) {
                    var9 = var37;
                    throw var37;
                } finally {
                    if (response != null) {
                        if (var9 != null) {
                            try {
                                response.close();
                            } catch (Throwable var35) {
                                var9.addSuppressed(var35);
                            }
                        } else {
                            response.close();
                        }
                    }

                }
            } catch (Throwable var39) {
                var7 = var39;
                throw var39;
            } finally {
                if (httpClient != null) {
                    if (var7 != null) {
                        try {
                            httpClient.close();
                        } catch (Throwable var34) {
                            var7.addSuppressed(var34);
                        }
                    } else {
                        httpClient.close();
                    }
                }

            }

            return resp;
        } catch (IOException var41) {
            logger.error("erreur d'envoie...." + var41.getMessage());
            return false;
        }
    }
}
