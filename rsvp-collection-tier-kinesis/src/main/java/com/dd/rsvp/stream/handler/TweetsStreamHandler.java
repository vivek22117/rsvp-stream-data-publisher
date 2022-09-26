package com.dd.rsvp.stream.handler;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.api.TweetsApi;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.*;

@Component
public class TweetsStreamHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TweetsStreamHandler.class);

    private final HttpClient httpClient;
    private final TwitterApi apiInstance;
    private final AWSSecretsManager awsSecretsManager;
    private final  String bearerToken;

    public TweetsStreamHandler(HttpClient httpClient, TwitterApi apiInstance, AWSSecretsManager awsSecretsManager) {
        this.httpClient = httpClient;
        this.apiInstance = apiInstance;
        this.awsSecretsManager = awsSecretsManager;
        this.bearerToken = getSecret(awsSecretsManager);
    }

    public void tweetsHandler() throws IOException, URISyntaxException {
        Set<String> tweetFields = new HashSet<>();
        tweetFields.add("author_id");
        tweetFields.add("id");
        tweetFields.add("created_at");

        setupStreamRules();

        try {
            InputStream streamResult = apiInstance.tweets().searchStream()
                    .backfillMinutes(0)
                    .tweetFields(tweetFields)
                    .execute();
            // sampleStream with TweetsStreamListenersExecutor
            Responder responder = new Responder();
            TweetsStreamListenersExecutor tsle = new TweetsStreamListenersExecutor(streamResult);
            tsle.addListener(responder);
            tsle.executeListeners();
        } catch (ApiException e) {
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }

    private void setupStreamRules() throws IOException, URISyntaxException {
        List<String> existingRules = getRules();

        if (existingRules.size() > 0) {
            deleteRulesUsingHttpClient(existingRules);
        }

        Map<String, String> rules = new HashMap<>();
        rules.put("AWS has:media", "AWS");
        rules.put("bitcoin has:media", "bitcoin");
        rules.put("cloud has:media", "cloud");
        rules.put("iphone has:media", "iphone");

        createRules(rules);
    }

    /*
     * Helper method to create rules for filtering
     * */
    private void createRules(Map<String, String> rules) throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("https://api.twitter.com/2/tweets/search/stream/rules");

        HttpPost httpPost = new HttpPost(uriBuilder.build());
        httpPost.setHeader("Authorization", String.format("Bearer %s", bearerToken));
        httpPost.setHeader("content-type", "application/json");
        StringEntity body = new StringEntity(getFormattedString("{\"add\": [%s]}", rules));
        httpPost.setEntity(body);
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        if (null != entity) {
            System.out.println(EntityUtils.toString(entity, "UTF-8"));
        }
    }

    /*
     * Helper method to get existing rules
     * */
    private List<String> getRules() throws URISyntaxException, IOException {
        List<String> rules = new ArrayList<>();

        URIBuilder uriBuilder = new URIBuilder("https://api.twitter.com/2/tweets/search/stream/rules");

        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.setHeader("Authorization", String.format("Bearer %s", bearerToken));
        httpGet.setHeader("content-type", "application/json");
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if (null != entity) {
            JSONObject json = new JSONObject(EntityUtils.toString(entity, "UTF-8"));
            if (json.length() > 1) {
                JSONArray array = (JSONArray) json.get("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = (JSONObject) array.get(i);
                    rules.add(jsonObject.getString("id"));
                }
            }
        }
        return rules;
    }

    /*
     * Helper method to delete rules
     * */
    private void deleteRules(List<String> existingRules) {
        AddOrDeleteRulesRequest addOrDeleteRulesRequest = new AddOrDeleteRulesRequest();
        DeleteRulesRequest deleteRulesRequest = new DeleteRulesRequest();
        DeleteRulesRequestDelete deleteRules = new DeleteRulesRequestDelete();

        deleteRules.ids(existingRules);
        deleteRulesRequest.delete(deleteRules);

        addOrDeleteRulesRequest.setActualInstance(deleteRulesRequest);

        apiInstance.tweets().addOrDeleteRules(addOrDeleteRulesRequest).dryRun(true);
    }

    /*
     * HTTP Helper method to delete rules
     * */
    private void deleteRulesUsingHttpClient(List<String> existingRules) throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder("https://api.twitter.com/2/tweets/search/stream/rules");

        HttpPost httpPost = new HttpPost(uriBuilder.build());
        httpPost.setHeader("Authorization", String.format("Bearer %s", bearerToken));
        httpPost.setHeader("content-type", "application/json");
        StringEntity body = new StringEntity(getFormattedString("{ \"delete\": { \"ids\": [%s]}}", existingRules));
        httpPost.setEntity(body);
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        if (null != entity) {
            System.out.println(EntityUtils.toString(entity, "UTF-8"));
        }
    }

    private static String getFormattedString(String string, List<String> ids) {
        StringBuilder sb = new StringBuilder();
        if (ids.size() == 1) {
            return String.format(string, "\"" + ids.get(0) + "\"");
        } else {
            for (String id : ids) {
                sb.append("\"" + id + "\"" + ",");
            }
            String result = sb.toString();
            return String.format(string, result.substring(0, result.length() - 1));
        }
    }

    private static String getFormattedString(String string, Map<String, String> rules) {
        StringBuilder sb = new StringBuilder();
        if (rules.size() == 1) {
            String key = rules.keySet().iterator().next();
            return String.format(string, "{\"value\": \"" + key + "\", \"tag\": \"" + rules.get(key) + "\"}");
        } else {
            for (Map.Entry<String, String> entry : rules.entrySet()) {
                String value = entry.getKey();
                String tag = entry.getValue();
                sb.append("{\"value\": \"" + value + "\", \"tag\": \"" + tag + "\"}" + ",");
            }
            String result = sb.toString();
            return String.format(string, result.substring(0, result.length() - 1));
        }
    }

    private String getSecret(AWSSecretsManager client) {

        String secretName = "twitter-stream/token/value";

        String secret;
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName);
        GetSecretValueResult getSecretValueResult = null;

        try {
            getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        } catch (Exception ex) {
            LOGGER.error("Specified " + secretName + "secret not found!");
            throw new NotFoundException("The specified aws secret not found!");
        }
        if (getSecretValueResult.getSecretString() != null) {
            secret = getSecretValueResult.getSecretString();
            return secret;
        }

        return null;
    }
}
