package com.example.event.manager.kafka;

import com.example.eventcommon.kafka.EventChangeKafkaMessage;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaConfig {

    @Bean
    public KafkaTemplate<String, EventChangeKafkaMessage> kafkaTemplate(KafkaProperties kafkaProperties) {

        var properties = kafkaProperties.buildProducerProperties(new DefaultSslBundleRegistry());
        var producerFactory = new DefaultKafkaProducerFactory<String, EventChangeKafkaMessage>(properties);

        return new KafkaTemplate<>(producerFactory);
    }
}
