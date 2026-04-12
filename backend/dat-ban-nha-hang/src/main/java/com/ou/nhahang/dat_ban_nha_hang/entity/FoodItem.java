package com.ou.nhahang.dat_ban_nha_hang.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_item")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodItem extends Base {

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    public enum FoodItemStatus {
        PENDING,
        SERVED,
        CANCELLED
    }

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private FoodItemStatus status;

    @ManyToOne
    @JoinColumn(name = "food_order_id", nullable = false)
    private FoodOrder foodOrder;

    @ManyToOne
    @JoinColumn(name = "food_description_id", nullable = false)
    private FoodDescription foodDescription;

    @ManyToOne
    @JoinColumn(name = "waiter_id", nullable = true)
    private User waiter;

}
