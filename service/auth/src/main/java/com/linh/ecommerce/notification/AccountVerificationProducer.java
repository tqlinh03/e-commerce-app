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
public class AccountVerificationProducer {
    private final KafkaTemplate<String, AccountVerification> kafkaTemplate;

    public void sendNotification(AccountVerification accountVerification) {
        log.info("Sending notification with body = < {} >", accountVerification);
        Message<AccountVerification> message = MessageBuilder
                .withPayload(accountVerification)
                .setHeader(TOPIC, "account-verification-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
