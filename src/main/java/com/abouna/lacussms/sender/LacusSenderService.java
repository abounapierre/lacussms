package com.abouna.lacussms.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LacusSenderService {
    private static final Logger logger = LoggerFactory.getLogger(LacusSenderService.class);

    /*public static boolean sendSMS(String urlText, String number, String msg) {
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
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }*/
}
