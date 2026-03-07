package com.ou.nhahang.dat_ban_nha_hang.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_order")
@Data
@DiscriminatorValue("FOOD_ORDER")
@PrimaryKeyJoinColumn(name = "food_order_id")
@EqualsAndHashCode(callSuper = true)
public class FoodOrder extends PaymentSource {

    public enum FoodOrderStatus {
        TAKING_ORDER,
        CONFIRMED,
        CANCELLED,
        COMPLETED,
        CLOSED // Khi thanh toán xong thì nó sẽ đóng
    }

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private FoodOrderStatus status;

    @ManyToOne
    @JoinColumn(name = "table_session_id", nullable = false)
    private RestaurantTableSession tableSession;

    public FoodOrder() {
    }

}
