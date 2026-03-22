package com.ou.nhahang.dat_ban_nha_hang.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_item")
@Getter
@Setter

public class FoodItem extends Base {

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    public enum FoodItemStatus {
        NOT_COOKED,
        COOKED,
        CANCELLED
    }

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private FoodItemStatus status;

    @ManyToOne
    @JoinColumn(name = "chef_id", nullable = true)
    private User chef;

    @ManyToOne
    @JoinColumn(name = "food_order_id", nullable = false)
    private FoodOrder foodOrder;

    @ManyToOne
    @JoinColumn(name = "food_description_id", nullable = false)
    private FoodDescription foodDescription;

    public FoodItem() {
    }

}
