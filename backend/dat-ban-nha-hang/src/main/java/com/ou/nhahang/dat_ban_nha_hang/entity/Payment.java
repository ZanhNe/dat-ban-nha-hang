package com.ou.nhahang.dat_ban_nha_hang.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment")
@Getter
@Setter

public class Payment extends Base {
    @Column(name = "price", nullable = false)
    private Long price;

    public enum PaymentType {
        PAYMENT,
        REFUND
    }

    public enum PaymentStatus {
        PENDING,
        SUCCESS,
        FAILED
    }

    public enum PaymentMethod {
        CASH,
        CREDIT_CARD
    }

    @Column(name = "payment_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(name = "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "gateway_ref", columnDefinition = "TEXT", nullable = true)
    private String gatewayRef;

    @Column(name = "log", columnDefinition = "TEXT", nullable = true)
    private String log;

    public Payment() {
    }
}
