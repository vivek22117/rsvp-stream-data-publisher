package com.dd.rsvp.stream;

import com.dd.rsvp.stream.handler.RSVPWebSocketHandler;
import com.dd.rsvp.stream.handler.TweetsStreamHandler;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import twitter4j.*;
import twitter4j.auth.Authorization;

@SpringBootApplication
public class RSVPCollectionAppKinesis {

    private static final String MEETUP_RSVP_ENDPOINT = "ws://stream.meetup.com/2/rsvps";

    public static void main(String[] args) {
        SpringApplication.run(RSVPCollectionAppKinesis.class, args);
    }

    @Bean
    public ApplicationRunner init(TweetsStreamHandler handler) {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                handler.tweetsHandler();
            }
        };

    }
}
