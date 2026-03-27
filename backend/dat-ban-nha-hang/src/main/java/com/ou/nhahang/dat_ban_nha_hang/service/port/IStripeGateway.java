package com.ou.nhahang.dat_ban_nha_hang.service.port;

public interface IStripeGateway {
    record PaymentGatewayIntent(String intentId, String clientSecret) {}
    
    PaymentGatewayIntent createPaymentIntent(Long amount, Long bookingId, String currency);
    void capturePayment(String intentId);
    void cancelPayment(String intentId);
    String extractIntentIdFromWebhook(String payload, String signature);
}
