package com.linh.ecommerce.kafka;

import com.linh.ecommerce.email.EmailService;
import com.linh.ecommerce.kafka.account.AccountVerification;
import com.linh.ecommerce.kafka.account.ForgotPassword;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationsConsumer {
    private final EmailService emailService;

    @KafkaListener(topics = "account-verification-topic", groupId = "verificationGroup")
    public void customerAccountVerificationEmail(AccountVerification accountVerification) throws MessagingException {
        log.info(format("Consuming the message from account-verification-topic Topic:: %s", accountVerification));
        emailService.sendAccountVerificationEmail(
                accountVerification.to(),
                accountVerification.username(),
                accountVerification.confirmationUrl(),
                accountVerification.activationCode()
        );
    }

    @KafkaListener(topics = "forgot-password-topic", groupId = "forgotPasswordGroup")
    public void forgotPasswordEmail(ForgotPassword forgotPassword) throws MessagingException {
        log.info(format("Consuming the message from forgot-password-topic Topic:: %s", forgotPassword));
        emailService.sendForgotPasswordEmail(
                forgotPassword.to(),
                forgotPassword.username(),
                forgotPassword.activationCode()
        );
    }
}
