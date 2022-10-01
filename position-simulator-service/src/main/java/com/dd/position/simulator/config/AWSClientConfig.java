package com.dd.position.simulator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;

@Configuration
public class AWSClientConfig {

    private static AwsCredentialsProvider awsCredentialsProvider;

    @Value("${isRunningInEC2: No value}")
    private boolean isRunningInEC2;

    @Value("${isRunningInLocal: No value}")
    private boolean isRunningInLocal;

    @Bean
    public KinesisClient createPublisherClient() {
        return KinesisClient.builder()
                .credentialsProvider(getAwsCredentials())
                .region(Region.US_EAST_1).build();
    }

    private AwsCredentialsProvider getAwsCredentials() {
        if (awsCredentialsProvider == null) {
            if (isRunningInEC2) {
                awsCredentialsProvider = InstanceProfileCredentialsProvider.builder().build();
            } else if (isRunningInLocal) {
                awsCredentialsProvider = ProfileCredentialsProvider.builder().profileName("admin").build();
            } else {
                awsCredentialsProvider = DefaultCredentialsProvider.builder().build();
            }
        }
        return awsCredentialsProvider;
    }
}
