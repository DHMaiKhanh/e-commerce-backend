package com.yourdomain.ecommerce.integration.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@Profile({"dev", "test"})
public class StubPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult charge(String orderRef, BigDecimal amount, String currency) {
        String ref = "STUB-" + UUID.randomUUID();
        log.info("[StubPayment] charge order={} amount={} {} -> {}", orderRef, amount, currency, ref);
        return new PaymentResult(true, ref, "ok");
    }

    @Override
    public PaymentResult refund(String paymentRef, BigDecimal amount) {
        log.info("[StubPayment] refund payment={} amount={}", paymentRef, amount);
        return new PaymentResult(true, paymentRef + "-R", "refunded");
    }
}
