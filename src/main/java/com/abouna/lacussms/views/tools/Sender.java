//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.abouna.lacussms.views.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class Sender {
    private static final Logger logger = LoggerFactory.getLogger(Sender.class);
    public Sender() {
    }

    public static boolean send(String urlText, String number, String msg) {
        String link;
        try {
            link = urlText.replace("<num>", URLEncoder.encode(number, StandardCharsets.ISO_8859_1)).replace("<msg>", URLEncoder.encode(msg, StandardCharsets.ISO_8859_1));
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(link))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("####### response {} #######", response.body());
            return true;
        } catch (UnsupportedEncodingException var36) {
            return false;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
