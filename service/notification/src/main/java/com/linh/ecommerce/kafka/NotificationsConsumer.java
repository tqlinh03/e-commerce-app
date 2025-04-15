package com.linh.ecommerce.kafka;

import com.linh.ecommerce.email.EmailService;
import com.linh.ecommerce.kafka.account.AccountVerification;
import com.linh.ecommerce.kafka.account.ForgotPassword;
import com.linh.ecommerce.kafka.order.OrderConfirmation;
import com.linh.ecommerce.notification.Notification;
import com.linh.ecommerce.notification.NotificationRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.linh.ecommerce.notification.NotificationType.ORDER_CONFIRMATION;
import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationsConsumer {
    private final EmailService emailService;
    private final NotificationRepository repository;

    @KafkaListener(topics = "account-verification-topic", groupId = "verificationGroup")
    public void customerAccountVerificationEmail(AccountVerification accountVerification) throws MessagingException {
        log.info(format("Consuming the message from account-verification-topic Topic:: %s", accountVerification));
        emailService.sendAccountVerificationEmail(
                accountVerification.to(),
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

    @KafkaListener(topics = "order-topic", groupId = "orderGroup")
    public void consumeOrderConfirmationNotifications(OrderConfirmation orderConfirmation) throws MessagingException {
        log.info(format("Consuming the message from order-topic Topic:: %s", orderConfirmation));
        repository.save(
                Notification.builder()
                        .type(ORDER_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .orderConfirmation(orderConfirmation)
                        .build()
        );
        var customerName = orderConfirmation.customer().fullName();
        emailService.sendOrderConfirmationEmail(
                orderConfirmation.customer().email(),
                customerName,
                orderConfirmation.totalAmount(),
                orderConfirmation.orderReference(),
                orderConfirmation.products()
        );
    }
}
