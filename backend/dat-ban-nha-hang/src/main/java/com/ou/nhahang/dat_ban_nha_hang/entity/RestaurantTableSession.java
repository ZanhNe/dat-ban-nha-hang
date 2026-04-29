package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurant_table_session")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("TABLE_SESSION")
@PrimaryKeyJoinColumn(name = "table_session_id")
public class RestaurantTableSession extends PaymentSource {

    public enum TableSessionStatus {
        ACTIVE,
        SERVING,
        SERVED,
        PAYING,
        COMPLETED
    }

    @Column(name = "total", nullable = false)
    @Builder.Default
    private Long total = 0L;

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TableSessionStatus status = TableSessionStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "waiter_id", nullable = true)
    private User waiter;

    @OneToOne(mappedBy = "tableSession")
    private Booking booking;

    @OneToMany(mappedBy = "tableSession")
    @Builder.Default
    private List<FoodOrder> foodOrders = new ArrayList<>();

    public Long calculatePrice() {
        Long price = 0L;
        if (this.foodOrders != null) {
            price = this.foodOrders.stream()
                    .mapToLong(FoodOrder::getTotalPrice)
                    .sum();
        }
        this.total = price;
        return this.total;
    }

}
