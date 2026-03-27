package com.ou.nhahang.dat_ban_nha_hang.integrations;

import com.ou.nhahang.dat_ban_nha_hang.service.port.IStripeGateway;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeGatewayImpl implements IStripeGateway {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public PaymentGatewayIntent createPaymentIntent(Long amount, Long bookingId, String currency) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount)
                    .setCurrency(currency)
                    .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
                    .putMetadata("booking_id", bookingId.toString())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            return new PaymentGatewayIntent(intent.getId(), intent.getClientSecret());
        } catch (StripeException e) {
            throw new BusinessException("Stripe error: " + e.getMessage());
        }
    }

    @Override
    public void capturePayment(String intentId) {
        try {
            PaymentIntent intent = PaymentIntent.retrieve(intentId);
            intent.capture();
        } catch (StripeException e) {
            throw new BusinessException("Stripe error: " + e.getMessage());
        }
    }

    @Override
    public void cancelPayment(String intentId) {
        try {
            PaymentIntent intent = PaymentIntent.retrieve(intentId);
            intent.cancel();
        } catch (StripeException e) {
            throw new BusinessException("Stripe error: " + e.getMessage());
        }
    }

    @Override
    public String extractIntentIdFromWebhook(String payload, String signature) {
        try {
            Event event = Webhook.constructEvent(payload, signature, stripeWebhookSecret);
            if ("payment_intent.amount_capturable_updated".equals(event.getType())) {
                EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
                if (dataObjectDeserializer.getObject().isPresent()) {
                    PaymentIntent intent = (PaymentIntent) dataObjectDeserializer.getObject().get();
                    return intent.getId();
                }
            }
        } catch (SignatureVerificationException e) {
            throw new BusinessException("Invalid signature");
        }
        return null;
    }
}
