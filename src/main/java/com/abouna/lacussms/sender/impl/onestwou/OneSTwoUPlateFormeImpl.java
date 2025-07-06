package com.abouna.lacussms.sender.impl.onestwou;

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
public class OneSTwoUPlateFormeImpl implements LacusSenderService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(OneSTwoUPlateFormeImpl.class);
    private final String username;
    private final String pass;
    private final String sid;
    private final String mt;
    private final String url;
    private final String testNumber;

    public OneSTwoUPlateFormeImpl(@Qualifier("external-configs") Properties properties) {
        this.username = properties.getProperty("1s2u.sms.account.username");
        this.pass = properties.getProperty("1s2u.sms.account.password");
        this.sid = properties.getProperty("1s2u.sms.account.sid");
        this.mt = properties.getProperty("1s2u.sms.account.mt");
        this.url = properties.getProperty("1s2u.sms.send.url");
        this.testNumber = properties.getProperty("1s2u.sms.test.number");
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
        SenderContext.senders.put("1s2u", this);
    }

    private SendResponseDTO sentTo(String number, String msg) {
        try {
            Logger.info("Sending message to " + number + ": " + msg, this.getClass());
            String postBody = "username=" + URLEncoder.encode(username, "ISO-8859-1") + "&password=" + URLEncoder.encode(pass, "ISO-8859-1") + "&mno=" + URLEncoder.encode(number, "ISO-8859-1") + "&msg=" + URLEncoder.encode(msg, "ISO-8859-1") + "&Sid=" + URLEncoder.encode(sid, "ISO-8859-1") + "&mt=" + URLEncoder.encode(mt, "ISO-8859-1");
            String link = url + "?" + postBody;
            return postRequest(link);
        } catch (Exception e) {
            return new SendResponseDTO(false, "Failed to send message: " + e.getMessage());
        }
    }

    private SendResponseDTO sentTo(List<String> numbers, String msg) {
        try {
            Logger.info("Sending message to " + numbers + ": " + msg, this.getClass());
            String numbersStr = String.join(",", numbers);
            String postBody = "username=" + URLEncoder.encode(username, "ISO-8859-1") + "&password=" + URLEncoder.encode(pass, "ISO-8859-1") + "&mno=" + URLEncoder.encode(numbersStr, "ISO-8859-1") + "&msg=" + URLEncoder.encode(msg, "ISO-8859-1") + "&Sid=" + URLEncoder.encode(sid, "ISO-8859-1") + "&mt=" + URLEncoder.encode(mt, "ISO-8859-1");
            String link = url + "?" + postBody;
            return postRequest(link);
        } catch (Exception e) {
            return new SendResponseDTO(false, "Failed to send message: " + e.getMessage());
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
                    Logger.info(String.format("Request status: %s, response: %s", statusCode, respEntity), this.getClass());
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
