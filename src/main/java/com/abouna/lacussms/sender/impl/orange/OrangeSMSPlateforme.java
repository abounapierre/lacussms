package com.abouna.lacussms.sender.impl.orange;

import com.abouna.lacussms.config.AppRunConfig;
import com.abouna.lacussms.config.ApplicationConfig;
import com.abouna.lacussms.dto.SendResponseDTO;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.isEmpty;

public class OrangeSMSPlateforme {
    private static final Logger logger = LoggerFactory.getLogger(OrangeSMSPlateforme.class);
    private static Properties properties = null;
    private static final JsonMapper jsonMapper = new JsonMapper();
    private static String token = null;
    private static List<String> codes = null;
    private static final String EMPTY = "";
    private static SendResponseDTO sendResponseDTO = null;
    private static final String REQ = "requesting with : {}, token: {}";

    public static boolean isTokenExpired(String token) {
        try {
            if(Objects.isNull(token)) {
                return true;
            }
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getExpiresAt().before(new Date());
        } catch (JWTDecodeException e) {
            return true;
        }
    }

    public static void init() {
        try {
            properties = ApplicationConfig.getApplicationContext().getBean("external-configs", Properties.class);
            logger.info("Properties initialized: {}", properties.getProperty("orange.sms.token.url"));
        } catch (Exception e) {
            logger.error("Error loading SMS properties", e);
        }
    }

    public static SendResponseDTO send(String number, String msg) {
        try {
            sendResponseDTO = new SendResponseDTO();
            AppRunConfig appRunConfig = AppRunConfig.getInstance();
            if(Objects.isNull(properties)) {
                init();
            }
            if(appRunConfig.getTestModeEnabled()) {
                String msgRes = "Test mode is enabled, not sending SMS";
                com.abouna.lacussms.views.utils.Logger.info(msgRes, OrangeSMSPlateforme.class);
                String phone = properties.getProperty("orange.sms.test.number");
                sendResponseDTO.setSent(!isEmpty(phone) && sendTo(phone, msg).isSent());
                sendResponseDTO.setMessage(msgRes);
                return sendResponseDTO;
            }
            return sendTo(number, msg);
        }  catch (Exception e) {
            logger.error("Error sending SMS", e);
            sendResponseDTO.setSent(false);
            sendResponseDTO.setMessage(e.getMessage());
            return sendResponseDTO;
        }
    }

    public static SendResponseDTO send(List<String> numbers, String msg) {
        try {
            sendResponseDTO = new SendResponseDTO();
            AppRunConfig appRunConfig = AppRunConfig.getInstance();
            if(Objects.isNull(properties)) {
                init();
            }
            if(appRunConfig.getTestModeEnabled()) {
                String msgRes = "Test mode is enabled, not sending SMS";
                com.abouna.lacussms.views.utils.Logger.info(msgRes, OrangeSMSPlateforme.class);
                String phone = properties.getProperty("orange.sms.test.number");
                sendResponseDTO.setSent(!isEmpty(phone) && sendTo(phone, msg).isSent());
                sendResponseDTO.setMessage(msgRes);
                return sendResponseDTO;
            }
            return sendToList(numbers, msg);
        }  catch (Exception e) {
            logger.error("Error sending SMS", e);
            sendResponseDTO.setSent(false);
            sendResponseDTO.setMessage(e.getMessage());
            return sendResponseDTO;
        }
    }

    private static SendResponseDTO sendTo(String number, String msg) {
        try {
            if(Objects.isNull(codes)) {
                codes = getCodes();
            }
            if(isTokenExpired(token)) {
                token = getToken();
            }
            logger.info("#### codes : {} ####", codes);
            String phone = formatPhone(number);
            logger.info(REQ, buildEntity(phone, msg), token);
            Assert.notNull(token, "Token can not be null");
            Integer statusCode = postRequest(getHttpPost(properties.getProperty("orange.sms.send.url"), token, Collections.singletonList(number), msg), Integer.class);
            boolean resp = Objects.nonNull(statusCode) && (statusCode == 201 || statusCode == 200);
            String respMsg = String.format("Message %s envoyé au numéro: %s", (resp ? EMPTY : "non"), phone);
            com.abouna.lacussms.views.utils.Logger.info(respMsg, OrangeSMSPlateforme.class);
            sendResponseDTO.setMessage(respMsg);
            sendResponseDTO.setSent(resp);
            return sendResponseDTO;
        } catch (Exception e) {
            logger.error("Error sending SMS", e);
            sendResponseDTO.setSent(false);
            sendResponseDTO.setMessage("Error sending SMS " + e.getMessage());
            return sendResponseDTO;
        }
    }

    private static SendResponseDTO sendToList(List<String> numbers, String msg) {
        try {
            if(Objects.isNull(codes)) {
                codes = getCodes();
            }
            if(isTokenExpired(token)) {
                token = getToken();
            }
            List<String> phones = numbers.stream().map(OrangeSMSPlateforme::formatPhone).collect(Collectors.toList());
            Assert.notNull(token, "Token can not be null");
            Integer statusCode = postRequest(getHttpPost(properties.getProperty("orange.sms.send.url"), token, phones, msg), Integer.class);
            boolean resp = Objects.nonNull(statusCode) && (statusCode == 201 || statusCode == 200);
            String respMsg = String.format("Message %s envoyé au numéro: %s", (resp ? EMPTY : "non"), phones);
            com.abouna.lacussms.views.utils.Logger.info(respMsg, OrangeSMSPlateforme.class);
            sendResponseDTO.setMessage(respMsg);
            sendResponseDTO.setSent(resp);
            return sendResponseDTO;
        } catch (Exception e) {
            logger.error("Error sending SMS", e);
            sendResponseDTO.setSent(false);
            sendResponseDTO.setMessage("Error sending SMS " + e.getMessage());
            return sendResponseDTO;
        }
    }

    private static HttpPost getHttpPost(String url, String token, List<String> phones, String msg) {
        HttpPost post = new HttpPost(url);
        post.setHeader("Authorization", "Bearer " + token);
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Accept", "application/json");
        post.setEntity(buildEntity(phones, msg));
        return post;
    }

    private static String formatPhone(String number) {
        String phone = number;
        for (String code : codes) {
            if (number.startsWith(code)) {
                phone = number.replaceFirst(code, EMPTY);
                break;
            } else if (number.startsWith("+" + code)) {
                phone = number.replaceFirst("+" + code, EMPTY);
                break;
            }
            else if (number.startsWith("00" + code)) {
                phone = number.replaceFirst("00" + code, EMPTY);
                break;
            }
        }
        return phone;
    }

    private static <T> T postRequest(HttpPost post, Class<T> t) {
        try(CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse response = client.execute(post)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String respMsg = "Response status code";
            com.abouna.lacussms.views.utils.Logger.info(String.format("%s: %s", respMsg, statusCode), OrangeSMSPlateforme.class);
            logger.info("{}: {}", respMsg, statusCode);
            if (statusCode == 201 || statusCode == 200) {
                logger.info("Request successful: {}", statusCode);
                return t.equals(Integer.class) ? getValue(statusCode, t) : jsonMapper.readValue(response.getEntity().getContent(), t);
            }
            return getValue(statusCode, t);
        } catch (IOException e) {
            throw new RuntimeException("Error when posting request: " + e.getMessage(), e);
        }
    }

    private static <T> T getValue(Object o, Class<T> t) {
        return t.cast(o);
    }

    private static HttpEntity buildEntity(String number, String msg) {
        BodyRequest bodyRequest = new BodyRequest(getCampaign(), msg, getProjectName(), number);
        String json = toJson(bodyRequest);
        return new StringEntity(json, ContentType.APPLICATION_JSON);
    }

    private static HttpEntity buildEntity(List<String> numbers, String msg) {
        BodyRequest bodyRequest = new BodyRequest(getCampaign(), msg, getProjectName(), numbers);
        String json = toJsonWithNumberList(bodyRequest);
        return new StringEntity(json, ContentType.APPLICATION_JSON);
    }

    private static String toJsonWithNumberList(BodyRequest bodyRequest) {
        return "{" +
                "\"campaignTitle\":" + "\"" + bodyRequest.getCampaignTitle() + "\"" + "," +
                "\"messageContent\":" + "\"" + bodyRequest.getMessageContent() + "\"" + "," +
                "\"projectName\":" + "\"" + bodyRequest.getProjectName() + "\"" + "," +
                "\"recipients\":" + "[" + getStringWithDoubleQuotes(bodyRequest.getRecipients()) + "]" +
                "}";
    }

    private static String toJson(BodyRequest bodyRequest) {
        return "{" +
                "\"campaignTitle\":" + "\"" + bodyRequest.getCampaignTitle() + "\"" + "," +
                "\"messageContent\":" + "\"" + bodyRequest.getMessageContent() + "\"" + "," +
                "\"projectName\":" + "\"" + bodyRequest.getProjectName() + "\"" + "," +
                "\"recipients\":" + "[\"" + bodyRequest.getRecipient() + "\"]" +
                "}";
    }

    private static String toJson(String username, String password) {
        return "{" +
                "\"username\":" + "\"" + username + "\"" + "," +
                "\"password\":" + "\"" + password + "\"" +
                "}";
    }

    private static String getToken() {
        try {
            HttpPost post = new HttpPost(properties.getProperty("orange.sms.token.url"));
            String username = properties.getProperty("orange.sms.account.username");
            String password = properties.getProperty("orange.sms.account.password");
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Accept", "application/json");
            post.setEntity(new StringEntity(toJson(username, password), ContentType.APPLICATION_JSON));
            return Objects.requireNonNull(postRequest(post, TokenResponse.class)).getToken();
        } catch (Exception e) {
            throw new RuntimeException("Error getting token: " + e.getMessage(), e);
        }
    }

    private static List<String> getCodes() {
        try {
            String codes = properties.getProperty("orange.sms.country.codes");
            return Objects.isNull(codes) ? new ArrayList<>() : Arrays.asList(codes.split(","));
        } catch (Exception e) {
            logger.error("Error getting codes", e);
        }
        return new ArrayList<>();
    }

    private static String getProjectName() {
        String defaultName = "CEPISA";
        try {
            String projectName = properties.getProperty("orange.sms.account.project");
            return Objects.isNull(projectName) ? defaultName : projectName;
        } catch (Exception e) {
            logger.error("Error getting project name", e);
        }
        return defaultName;
    }

    private static String getCampaign() {
        String defaultName = "Notification";
        try {
            String campaign = properties.getProperty("orange.sms.account.campaign");
            return Objects.isNull(campaign) ? defaultName : campaign;
        } catch (Exception e) {
            logger.error("Error getting campaign name", e);
        }
        return defaultName;
    }

    private static String getStringWithDoubleQuotes(List<String> recipients) {
        StringBuilder sb = new StringBuilder();
        for (String recipient : recipients) {
            sb.append("\"").append(recipient).append("\"").append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1); // Remove the last comma
        }
        return sb.toString();
    }

    private static class BodyRequest {
        private final String campaignTitle;
        private final String messageContent;
        private final String projectName;
        private String recipient;
        private List<String> recipients;

        public BodyRequest(String campaignTitle, String messageContent, String projectName, String recipient) {
            this.campaignTitle = campaignTitle;
            this.messageContent = messageContent;
            this.projectName = projectName;
            this.recipient = recipient;
        }

        public BodyRequest(String campaignTitle, String messageContent, String projectName, List<String> recipients) {
            this.campaignTitle = campaignTitle;
            this.messageContent = messageContent;
            this.projectName = projectName;
            this.recipients = recipients;
        }

        public String getCampaignTitle() {
            return campaignTitle;
        }

        public String getMessageContent() {
            return messageContent;
        }

        public String getProjectName() {
            return projectName;
        }

        public String getRecipient() {
            return recipient;
        }

        public List<String> getRecipients() {
            return recipients;
        }
    }

    private static class TokenResponse {
        private String token;
        private String RefreshToken;

        public TokenResponse(String token, String refreshToken) {
            this.token = token;
            RefreshToken = refreshToken;
        }

        public String getToken() {
            return token;
        }
        public void setToken(String token) {
            this.token = token;
        }
        public String getRefreshToken() {
            return RefreshToken;
        }
        public void setRefreshToken(String refreshToken) {
            RefreshToken = refreshToken;
        }
    }
}
