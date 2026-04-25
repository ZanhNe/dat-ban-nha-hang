package com.ou.nhahang.dat_ban_nha_hang.repository;

import java.time.LocalDateTime;
import com.ou.nhahang.dat_ban_nha_hang.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByIntentId(String intentId);
    Optional<Transaction> findByPaymentSource_IdAndTransactionType(Long paymentSourceId, Transaction.TransactionType type);

    /**
     * Doanh thu hoa hồng hệ thống (ước tính): chỉ tính trên các giao dịch CAPTURED.
     * - PERCENTAGE: amount * baseCommissionValue / 100
     * - FIXED: baseCommissionValue
     *
     * Giao dịch hiện tại gắn với PaymentSource; Booking kế thừa PaymentSource nên có thể join theo id.
     */
    @Query("""
            SELECT COALESCE(SUM(
                CASE
                    WHEN r.commissionType = com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant$CommissionType.PERCENTAGE
                        THEN (t.amount * r.baseCommissionValue) / 100
                    ELSE r.baseCommissionValue
                END
            ), 0)
            FROM Transaction t
            JOIN t.paymentSource ps
            JOIN Booking b ON b.id = ps.id
            JOIN b.restaurant r
            WHERE t.transactionStatus = com.ou.nhahang.dat_ban_nha_hang.entity.Transaction$TransactionStatus.CAPTURED
              AND (:from IS NULL OR t.createdAt >= :from)
              AND (:to IS NULL OR t.createdAt < :to)
            """)
    long sumCommissionRevenueCaptured(@Param("from") LocalDateTime from, @Param("to") LocalDateTime toExclusive);
}
