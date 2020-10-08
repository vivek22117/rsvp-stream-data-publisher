package com.ddsolutions.kinesis.handler;

import com.ddsolutions.kinesis.publisher.RSVPKinesisPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Component
public class RSVPWebSocketHandler extends AbstractWebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RSVPWebSocketHandler.class);

    private RSVPKinesisPublisher kinesisPublisher;

    @Autowired
    public RSVPWebSocketHandler(RSVPKinesisPublisher kinesisPublisher) {
        this.kinesisPublisher = kinesisPublisher;
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        try {
            kinesisPublisher.publish(message);
        } catch (Exception ex) {
            LOGGER.error("Processing failed while publishing message, {} to Kafka or Kinesis", message.getPayload(), ex);
        }
    }
}
