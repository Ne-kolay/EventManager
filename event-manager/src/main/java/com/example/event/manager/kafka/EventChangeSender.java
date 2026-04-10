package com.example.event.manager.kafka;

import com.example.eventcommon.kafka.EventChangeKafkaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventChangeSender {

    private static final Logger log = LoggerFactory.getLogger(EventChangeSender.class);
    private static final String TOPIC = "event-changes";

    private final KafkaTemplate<String, EventChangeKafkaMessage> kafkaTemplate;

    public EventChangeSender(KafkaTemplate<String, EventChangeKafkaMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(EventChangeKafkaMessage message) {
        log.info("Sending event change: eventId={}, type={}",
                message.eventId(), message.eventType());

        kafkaTemplate.send(TOPIC, String.valueOf(message.eventId()), message)
                .thenAccept(result -> log.info("Send successful"));
    }
}
