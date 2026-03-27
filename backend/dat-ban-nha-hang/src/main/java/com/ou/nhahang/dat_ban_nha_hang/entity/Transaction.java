package com.ou.nhahang.dat_ban_nha_hang.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Transaction extends Base {

    @Column(name = "amount", nullable = false)
    private Long amount;

    public enum TransactionType {
        DEPOSIT,
        FINAL_PAYMENT,
        REFUND
    }

    public enum TransactionStatus {
        PENDING,
        AUTHORIZED,
        CAPTURED,
        FAILED,
        CANCELLED
    }

    @Column(name = "intent_id", nullable = true)
    private String intentId;

    @Column(name = "client_secret", nullable = true)
    private String clientSecret;

    @Column(name = "transaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "transaction_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @ManyToOne
    @JoinColumn(name = "cashier_id", nullable = true)
    private User cashier;

    @ManyToOne
    @JoinColumn(name = "payment_source_id", nullable = false)
    private PaymentSource paymentSource;

}
