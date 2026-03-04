package com.ou.nhahang.dat_ban_nha_hang.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_item")
@Data
@EqualsAndHashCode(callSuper = true)
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

    public FoodItem() {
    }

}
