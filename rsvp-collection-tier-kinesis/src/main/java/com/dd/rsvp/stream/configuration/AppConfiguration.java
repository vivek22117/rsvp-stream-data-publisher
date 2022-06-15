package com.dd.rsvp.stream.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;


@Configuration
public class AppConfiguration {

    private static AwsCredentialsProvider awsCredentialsProvider;
    private ApplicationContext applicationContext;

    @Value("${isRunningInEC2: No value}")
    private String isRunningInEC2;

    @Value("${isRunningInLocal: No value}")
    private String isRunningInLocal;

    @Value("${aws.region: No value for region}")
    private String awsRegion;

    @Autowired
    public AppConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public Gson createGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    @Bean
    public KinesisClient createPublisherClient() {
        return KinesisClient.builder()
                .credentialsProvider(getAwsCredentials())
                .region(Region.US_EAST_1).build();
    }

    private AwsCredentialsProvider getAwsCredentials() {
        if (awsCredentialsProvider == null) {
            if (Boolean.parseBoolean(isRunningInEC2)) {
                awsCredentialsProvider = InstanceProfileCredentialsProvider.builder().build();
            } else if (Boolean.parseBoolean(isRunningInLocal)) {
                awsCredentialsProvider = ProfileCredentialsProvider.builder().profileName("default").build();
            } else {
                awsCredentialsProvider = DefaultCredentialsProvider.builder().build();
            }
        }
        return awsCredentialsProvider;
    }
}
