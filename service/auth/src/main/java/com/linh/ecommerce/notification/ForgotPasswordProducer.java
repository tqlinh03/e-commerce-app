package com.linh.ecommerce.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordProducer {
    private final KafkaTemplate<String, ForgotPassword> kafkaTemplate;

    public void sendNotification(ForgotPassword forgotPassword) {
        log.info("Sending notification with body = < {} >", forgotPassword);
        Message<ForgotPassword> message = MessageBuilder
                .withPayload(forgotPassword)
                .setHeader(TOPIC, "forgot-password-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
