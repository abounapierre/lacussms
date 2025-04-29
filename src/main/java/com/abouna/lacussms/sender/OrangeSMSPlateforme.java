package com.abouna.lacussms.sender;

import com.abouna.lacussms.config.MessageProperties;
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
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class OrangeSMSPlateforme {
    private static final Logger logger = LoggerFactory.getLogger(OrangeSMSPlateforme.class);
    private static Properties properties = null;
    private static final JsonMapper jsonMapper = new JsonMapper();
    private static String token = null;
    private static List<String> codes = null;
    private static final String EMPTY = "";

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
            properties = MessageProperties.getProperties();
            logger.info("Properties initialized: {}", Objects.nonNull(properties));
        } catch (Exception e) {
            logger.error("Error loading SMS properties", e);
        }
    }

    public static boolean send(String number, String msg) {
        try {
            if(Objects.isNull(properties)) {
                init();
            }
            if(Objects.isNull(codes)) {
                codes = getCodes();
            }
            if(isTokenExpired(token)) {
                token = getToken();
            }
            logger.info("#### codes : {} ####", codes);
            String phone = formatPhone(number);
            logger.info("requesting with : {}, token: {}", buildEntity(phone, msg), token);
            Assert.notNull(token, "Token can not be null");
            HttpPost post = new HttpPost(properties.getProperty("orange.sms.send.url"));
            post.setHeader("Authorization", "Bearer " + token);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Accept", "application/json");
            post.setEntity(buildEntity(phone, msg));
            Integer statusCode = postRequest(post, Integer.class);
            boolean resp = Objects.nonNull(statusCode) && (statusCode == 201 || statusCode == 200);
            com.abouna.lacussms.views.utils.Logger.info(String.format("Message %s envoyé au numéro: %s", (resp ? EMPTY : "non"), phone), OrangeSMSPlateforme.class);
            return resp;
        } catch (Exception e) {
            logger.error("Error sending SMS", e);
            return false;
        }
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
            logger.error("Error executing HTTP request", e);
            return null;
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

    private static String toJson(BodyRequest bodyRequest) {
        return "{" +
                "\"campaignTitle\":" + "\"" + bodyRequest.getCampaignTitle() + "\"" + "," +
                "\"messageContent\":" + "\"" + bodyRequest.getMessageContent() + "\"" + "," +
                "\"projectName\":" + "\"" + bodyRequest.getProjectName() + "\"" + "," +
                "\"recipients\":" + "[\"" + bodyRequest.getRecipients() + "\"]" +
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
            logger.error("Error getting token", e);
        }
        return null;
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

    private static class BodyRequest {
        private String campaignTitle;
        private String messageContent;
        private String projectName;
        private String recipients;

        public BodyRequest(String campaignTitle, String messageContent, String projectName, String recipients) {
            this.campaignTitle = campaignTitle;
            this.messageContent = messageContent;
            this.projectName = projectName;
            this.recipients = recipients;
        }

        public String getCampaignTitle() {
            return campaignTitle;
        }

        public void setCampaignTitle(String campaignTitle) {
            this.campaignTitle = campaignTitle;
        }

        public String getMessageContent() {
            return messageContent;
        }

        public void setMessageContent(String messageContent) {
            this.messageContent = messageContent;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public String getRecipients() {
            return recipients;
        }

        public void setRecipients(String recipients) {
            this.recipients = recipients;
        }
    }

    private static class TokenResponse {
        private String token;
        private String RefreshToken;

        public TokenResponse() {
        }

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
