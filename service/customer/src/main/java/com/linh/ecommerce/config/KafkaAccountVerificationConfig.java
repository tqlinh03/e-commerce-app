package com.linh.ecommerce.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaAccountVerificationConfig {
    @Bean
    public NewTopic accountVerificationTopic() {
        return TopicBuilder
                .name("account-verification-topic")
                .build();
    }

    @Bean
    public NewTopic forgetPasswordTopic() {
        return TopicBuilder
                .name("forgot-password-topic")
                .build();
    }
}
