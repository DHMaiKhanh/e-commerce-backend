package com.yourdomain.ecommerce.integration.payment;

import java.math.BigDecimal;

public interface PaymentGateway {

    PaymentResult charge(String orderRef, BigDecimal amount, String currency);

    PaymentResult refund(String paymentRef, BigDecimal amount);

    record PaymentResult(boolean success, String providerRef, String message) {
    }
}
