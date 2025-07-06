package com.abouna.lacussms.sender.impl.keudal;

import com.abouna.lacussms.config.AppRunConfig;
import com.abouna.lacussms.dto.SendResponseDTO;
import com.abouna.lacussms.sender.LacusSenderService;
import com.abouna.lacussms.sender.context.SenderContext;
import com.abouna.lacussms.views.utils.Logger;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Properties;

@Service
public class KeudalSmsSenderImpl implements LacusSenderService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(KeudalSmsSenderImpl.class);
    private final String username;
    private final String pass;
    private final String sid;
    private final String url;
    private final String testNumber;

    public KeudalSmsSenderImpl(@Qualifier("external-configs") Properties properties) {
        this.username = properties.getProperty("keudal.sms.account.username");
        this.pass = properties.getProperty("keudal.sms.account.password");
        this.sid = properties.getProperty("keudal.sms.account.sender");
        this.url = properties.getProperty("keudal.sms.send.url");
        this.testNumber = properties.getProperty("keudal.sms.test.number");
    }

    @Override
    public SendResponseDTO send(String number, String msg) {
        AppRunConfig config = AppRunConfig.getInstance();
        return config.getTestModeEnabled() ? sentTo(testNumber, msg) : sentTo(number, msg);
    }

    @Override
    public SendResponseDTO send(List<String> numbers, String msg) {
        AppRunConfig config = AppRunConfig.getInstance();
        return config.getTestModeEnabled() ? sentTo(testNumber, msg) : sentTo(numbers, msg);
    }

    @PostConstruct
    void register() {
        SenderContext.senders.put("keudal", this);
    }

    private SendResponseDTO sentTo(String number, String msg) {
        try {
            Logger.info("KEUDAL: sending message to " + number + ": " + msg, this.getClass());
            String postBody = "user=" + URLEncoder.encode(username, "ISO-8859-1") + "&password=" + URLEncoder.encode(pass, "ISO-8859-1") + "&receive=" + URLEncoder.encode(number, "ISO-8859-1") + "&sms=" + URLEncoder.encode(msg, "ISO-8859-1") + "&sender=" + URLEncoder.encode(sid, "ISO-8859-1");
            String link = url + "?" + postBody;
            return postRequest(link);
        } catch (Exception e) {
            return new SendResponseDTO(false, "Failed to send message: " + e.getMessage());
        }
    }

    private SendResponseDTO sentTo(List<String> numbers, String msg) {
        try {
            Logger.info("KEUDAL: Sending message to " + numbers + ": " + msg, this.getClass());
            String numbersStr = String.join(",", numbers);
            String postBody = "user=" + URLEncoder.encode(username, "ISO-8859-1") + "&password=" + URLEncoder.encode(pass, "ISO-8859-1") + "&receive=" + URLEncoder.encode(numbersStr, "ISO-8859-1") + "&sms=" + URLEncoder.encode(msg, "ISO-8859-1") + "&sender=" + URLEncoder.encode(sid, "ISO-8859-1");
            String link = url + "?" + postBody;
            return postRequest(link);
        } catch (Exception e) {
            return new SendResponseDTO(false, "KEUDAL: Failed to send message: " + e.getMessage());
        }
    }

    private SendResponseDTO postRequest(String link) {
        try {
            HttpGet httpGet = new HttpGet(link);
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Accept", "application/json");
            try(CloseableHttpClient client = HttpClients.createDefault();
                CloseableHttpResponse response = client.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 201 || statusCode == 200) {
                    String respEntity = EntityUtils.toString(response.getEntity());
                    Logger.info(String.format("KEUDAL: Request status: %s, response: %s", statusCode, respEntity), this.getClass());
                    return new SendResponseDTO(true, String.format("Message envoyé avec succès: %s", respEntity));
                }
                return new SendResponseDTO(false, "Failed to send message: " + statusCode);
            } catch (IOException e) {
                return new SendResponseDTO(false, "Error when posting request: " + e.getMessage());
            }
        } catch (Exception e) {
            return new SendResponseDTO(false, "Error when posting request: " + e.getMessage());
        }
    }
}
