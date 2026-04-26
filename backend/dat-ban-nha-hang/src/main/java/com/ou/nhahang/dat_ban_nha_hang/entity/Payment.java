package com.ou.nhahang.dat_ban_nha_hang.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @Builder.Default
    private PaymentType paymentType = PaymentType.PAYMENT;

    @Column(name = "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.SUCCESS;

    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

}
