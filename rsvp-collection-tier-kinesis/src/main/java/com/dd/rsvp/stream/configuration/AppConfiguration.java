package com.dd.rsvp.stream.configuration;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TwitterApi;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.NotFoundException;

import static org.slf4j.LoggerFactory.getLogger;


@Configuration
public class AppConfiguration {

    private static final Logger logger = getLogger(AppConfiguration.class);

    private static AWSCredentialsProvider awsCredentialsProvider;
    private final ApplicationContext applicationContext;

    @Value("${isRunningInEC2: No value}")
    private boolean isRunningInEC2;

    @Value("${isRunningInLocal: No value}")
    private boolean isRunningInLocal;

    @Autowired
    public AppConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public AWSSecretsManager createSecretsManager() {
        try {
            awsCredentialsProvider = getAWSCredentialProvider();
            return AWSSecretsManagerClientBuilder.standard().
                    withCredentials(awsCredentialsProvider)
                    .withRegion(Regions.US_EAST_1)
                    .build();

        } catch (Exception ex) {
            logger.error("Exception Occurred while creating aws secrets-manager client" + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Bean
    public Gson createGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    @Bean
    public TwitterApi createTwitterApi(AWSSecretsManager awsSecretsManager) {
        String authorizationToken = getSecret(awsSecretsManager);
        return new TwitterApi(new TwitterCredentialsBearer(authorizationToken));
    }

    @Bean
    public HttpClient connectStreamClient() {
        return HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
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
            logger.error("Specified " + secretName + "secret not found!");
            throw new NotFoundException("The specified aws secret not found!");
        }
        if (getSecretValueResult.getSecretString() != null) {
            secret = getSecretValueResult.getSecretString();
            return secret;
        }

        return null;
    }


    private AWSCredentialsProvider getAWSCredentialProvider() {
        if (awsCredentialsProvider == null) {
            if (isRunningInEC2) {
                awsCredentialsProvider = new InstanceProfileCredentialsProvider(false);
            } else if (isRunningInLocal) {
                awsCredentialsProvider = new ProfileCredentialsProvider("admin");
            } else {
                awsCredentialsProvider = new DefaultAWSCredentialsProviderChain();
            }
        }
        return awsCredentialsProvider;
    }
}
