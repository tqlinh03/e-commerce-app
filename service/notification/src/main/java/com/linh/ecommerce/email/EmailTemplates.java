package com.linh.ecommerce.email;

import lombok.Getter;

@Getter
public enum EmailTemplates {

    ACTIVATE_ACCOUNT("activate_account.html", "Account activation"),
    FORGOT_PASSWORD("forgot_password.html", "Forgot Password"),
    PAYMENT_CONFIRMATION("payment-confirmation.html", "Payment successfully processed"),
    ORDER_CONFIRMATION("order-confirmation.html", "Order confirmation")
    ;

    @Getter
    private final String template;
    @Getter
    private final String subject;

    EmailTemplates(String template, String subject) {
        this.template = template;
        this.subject = subject;
    }
}
