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
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

public class OrangeSMSPlateforme {
    private static final Logger logger = LoggerFactory.getLogger(OrangeSMSPlateforme.class);
    private static Properties properties = null;
    private static final JsonMapper jsonMapper = new JsonMapper();
    private static String token = null;

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
            logger.info("Properties initialized: {}", properties);
        } catch (Exception e) {
            logger.error("Error loading SMS properties", e);
        }
    }

    public static boolean send(String number, String msg) {
        try {
            if(Objects.isNull(properties)) {
                init();
            }
            if(isTokenExpired(token)) {
                token = getToken();
            }
            logger.info("requesting with : {}, token: {}", buildEntity(number, msg), token);
            Assert.notNull(token, "Token can not be null");
            HttpPost post = new HttpPost(properties.getProperty("orange.sms.send.url"));
            post.setHeader("Authorization", "Bearer " + token);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Accept", "application/json");
            post.setEntity(buildEntity(number, msg));
            return postRequest(post, Object.class) != null;
        } catch (Exception e) {
            logger.error("Error sending SMS", e);
            return false;
        }
    }

    private static <T> T postRequest(HttpPost post, Class<T> t) {
        try(CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse response = client.execute(post)) {
            int statusCode = response.getStatusLine().getStatusCode();
            logger.info("Response status code: {}", statusCode);
            if (statusCode == 201 || statusCode == 200) {
                logger.info("Request successful: {}", statusCode);
                return jsonMapper.readValue(response.getEntity().getContent(), t);
            }
        } catch (IOException e) {
            logger.error("Error executing HTTP request", e);
        }
        return null;
    }

    private static HttpEntity buildEntity(String number, String msg) {
        BodyRequest bodyRequest = new BodyRequest("SMS Campaign", msg, "CEPISA", number);
        String json = toJson(bodyRequest);
        logger.info("Request body: {}", json);
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
            logger.info("Requesting  with body: {}", toJson(username, password));
            post.setEntity(new StringEntity(toJson(username, password), ContentType.APPLICATION_JSON));
            return Objects.requireNonNull(postRequest(post, TokenResponse.class)).getToken();
        } catch (Exception e) {
            logger.error("Error getting token", e);
        }
        return null;
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
